package com.han.common.oss.core;

import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.Constants;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.file.FileUtils;
import com.han.common.oss.constant.OssConstant;
import com.han.common.oss.entity.UploadResult;
import com.han.common.oss.enums.AccessPolicyType;
import com.han.common.oss.exception.OssException;
import com.han.common.oss.properties.OssProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.*;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: S3 存储协议 所有兼容S3协议的云厂商均支持
 */
@Slf4j
public class S3OssClient extends AbstractOssClient {

    private final S3AsyncClient client;
    private final S3TransferManager transferManager;
    private final S3Presigner presigner;

    public S3OssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
        try {
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey()));

            boolean isStyle = !StringUtils.containsAny(properties.getEndpoint(), OssConstant.CLOUD_SERVICE);

            this.client = S3AsyncClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(getEndpoint()))
                .region(of())
                .forcePathStyle(isStyle)
                .httpClient(NettyNioAsyncHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(60)).build())
                .build();

            this.transferManager = S3TransferManager.builder().s3Client(this.client).build();

            S3Configuration config = S3Configuration.builder().chunkedEncodingEnabled(false)
                .pathStyleAccessEnabled(isStyle).build();

            this.presigner = S3Presigner.builder()
                .region(of())
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(getDomain()))
                .serviceConfiguration(config)
                .build();

        } catch (Exception e) {
            if (e instanceof OssException) {
                throw e;
            }
            throw new OssException("配置错误! 请检查系统配置:[" + e.getMessage() + "]");
        }
    }

    @Override
    public UploadResult uploadSuffix(byte[] data, String suffix, String contentType) {
        return upload(new ByteArrayInputStream(data), getPath(properties.getPrefix(), suffix), contentType);
    }

    @Override
    public UploadResult uploadSuffix(InputStream inputStream, String suffix, Long length, String contentType) {
        return upload(inputStream, getPath(properties.getPrefix(), suffix), contentType);
    }

    @Override
    public UploadResult uploadSuffix(File file, String suffix) {
        return upload(file.toPath(), getPath(properties.getPrefix(), suffix), null, FileUtils.getMimeType(suffix));
    }

    public UploadResult upload(Path filePath, String key, String md5Digest, String contentType) {
        try {
            FileUpload fileUpload = transferManager.uploadFile(
                x -> x.putObjectRequest(
                        y -> y.bucket(properties.getBucketName())
                            .key(key)
                            .contentMD5(StringUtils.isNotEmpty(md5Digest) ? md5Digest : null)
                            .contentType(contentType)
                            .build())
                    .addTransferListener(LoggingTransferListener.create())
                    .source(filePath).build());

            CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
            String eTag = uploadResult.response().eTag();

            return UploadResult.builder().url(getUrl() + StringUtils.SLASH + key).filename(key).eTag(eTag).build();
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        } finally {
            FileUtils.del(filePath);
        }
    }

    public UploadResult upload(InputStream inputStream, String key, String contentType) {
        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile("oss-upload-", ".tmp");
            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            return upload(tempFilePath, key, null, contentType);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        } finally {
            if (tempFilePath != null) {
                FileUtils.del(tempFilePath);
            }
        }
    }

    @Override
    public void download(String key, OutputStream out, Consumer<Long> consumer) {
        try {
            DownloadRequest<ResponsePublisher<GetObjectResponse>> publisherDownloadRequest = DownloadRequest.builder()
                .getObjectRequest(y -> y.bucket(properties.getBucketName())
                    .key(key)
                    .build())
                .addTransferListener(LoggingTransferListener.create())
                .responseTransformer(AsyncResponseTransformer.toPublisher())
                .build();

            Download<ResponsePublisher<GetObjectResponse>> publisherDownload = transferManager.download(publisherDownloadRequest);
            ResponsePublisher<GetObjectResponse> publisher = publisherDownload.completionFuture().join().result();
            Optional.ofNullable(consumer)
                .ifPresent(lengthConsumer -> lengthConsumer.accept(publisher.response().contentLength()));

            try(WritableByteChannel channel = Channels.newChannel(out)){
                publisher.subscribe(byteBuffer -> {
                    while (byteBuffer.hasRemaining()) {
                        try {
                            channel.write(byteBuffer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).join();
            }
        } catch (Exception e) {
            throw new OssException("文件下载失败，错误信息:[" + e.getMessage() + "]");
        }
    }

    @Override
    public void delete(String path) {
        try {
            client.deleteObject(
                x -> x.bucket(properties.getBucketName())
                    .key(removeBaseUrl(path))
                    .build());
        } catch (Exception e) {
            throw new OssException("删除文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }

    @Override
    public String getPrivateUrl(String objectKey, Duration expiredTime) {
        URL url = presigner.presignGetObject(
                x -> x.signatureDuration(expiredTime)
                    .getObjectRequest(
                        y -> y.bucket(properties.getBucketName())
                            .key(objectKey)
                            .build())
                    .build())
            .url();
        return url.toString();
    }

    @Override
    public AccessPolicyType getAccessPolicy() {
        return AccessPolicyType.getByType(properties.getAccessPolicy());
    }

    @Override
    public void close() {
        if (transferManager != null) {
            try {
                transferManager.close();
            } catch (Exception e) {
                log.error("S3TransferManager close error", e);
            }
        }
        if (presigner != null) {
            try {
                presigner.close();
            } catch (Exception e) {
                log.error("S3Presigner close error", e);
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                log.error("S3AsyncClient close error", e);
            }
        }
    }

    public String getEndpoint() {
        String header = getIsHttps();
        return header + properties.getEndpoint();
    }

    public String getDomain() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getIsHttps();

        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            return StringUtils.isNotEmpty(domain) ? header + domain : header + endpoint;
        }

        if (StringUtils.isNotEmpty(domain)) {
            return domain.startsWith(Constants.HTTPS) || domain.startsWith(Constants.HTTP) ? domain : header + domain;
        }

        return header + endpoint;
    }

    public Region of() {
        String region = properties.getRegion();
        return StringUtils.isNotEmpty(region) ? Region.of(region) : Region.US_EAST_1;
    }

    public String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getIsHttps();
        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            return header + (StringUtils.isNotEmpty(domain) ? domain : properties.getBucketName() + "." + endpoint);
        }
        if (StringUtils.isNotEmpty(domain)) {
            return (domain.startsWith(Constants.HTTPS) || domain.startsWith(Constants.HTTP)) ?
                domain + StringUtils.SLASH + properties.getBucketName() : header + domain + StringUtils.SLASH + properties.getBucketName();
        }
        return header + endpoint + StringUtils.SLASH + properties.getBucketName();
    }

    public String removeBaseUrl(String path) {
        return path.replace(getUrl() + StringUtils.SLASH, "");
    }

    public String getIsHttps() {
        return OssConstant.IS_HTTPS.equals(properties.getIsHttps()) ? Constants.HTTPS : Constants.HTTP;
    }
}
