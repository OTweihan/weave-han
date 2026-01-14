/*
 Navicat Premium Dump SQL

 Source Server         : WeaveHan
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30-cynos)
 Source Host           : sh-cynosdbmysql-grp-p9edryt2.sql.tencentcdb.com:28079
 Source Schema         : weave-han

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30-cynos)
 File Encoding         : 65001

 Date: 14/01/2026 10:38:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blog_category
-- ----------------------------
DROP TABLE IF EXISTS `blog_category`;
CREATE TABLE `blog_category`  (
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `slug` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类别名',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `ancestors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '祖先路径（如：0,1,2）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `cover_image` bigint NULL DEFAULT NULL COMMENT '分类封面图片ID',
  `post_count` int NULL DEFAULT 0 COMMENT '文章数量',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `uk_slug`(`slug` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_ancestors`(`ancestors` ASC) USING BTREE,
  INDEX `fk_category_cover_image`(`cover_image` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_category
-- ----------------------------

-- ----------------------------
-- Table structure for blog_comment
-- ----------------------------
DROP TABLE IF EXISTS `blog_comment`;
CREATE TABLE `blog_comment`  (
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID（登录用户）',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名（游客）',
  `user_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱（游客）',
  `user_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址（支持IPv6）',
  `user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0待审核 1通过 2拒绝）',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_create_time`(`user_id` ASC, `create_time` DESC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_post_status_time`(`post_id` ASC, `status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_comment
-- ----------------------------

-- ----------------------------
-- Table structure for blog_draft
-- ----------------------------
DROP TABLE IF EXISTS `blog_draft`;
CREATE TABLE `blog_draft`  (
  `draft_id` bigint NOT NULL COMMENT '草稿ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`draft_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '草稿箱表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_draft
-- ----------------------------

-- ----------------------------
-- Table structure for blog_draft_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_draft_tag`;
CREATE TABLE `blog_draft_tag`  (
  `draft_id` bigint NOT NULL COMMENT '草稿ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`draft_id`, `tag_id`) USING BTREE,
  INDEX `fk_draft_tag_tag`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '草稿标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_draft_tag
-- ----------------------------

-- ----------------------------
-- Table structure for blog_image
-- ----------------------------
DROP TABLE IF EXISTS `blog_image`;
CREATE TABLE `blog_image`  (
  `image_id` bigint NOT NULL COMMENT '图片ID',
  `oss_id` bigint NOT NULL COMMENT 'OSS文件ID',
  `image_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片名称',
  `image_type` tinyint NOT NULL DEFAULT 0 COMMENT '图片类型（关联字典表blog_image_type）',
  `post_id` bigint NULL DEFAULT NULL COMMENT '关联文章ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '关联分类ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始文件名',
  `alt` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片描述（alt属性）',
  `caption` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片标题/说明',
  `width` int NULL DEFAULT NULL COMMENT '图片宽度',
  `height` int NULL DEFAULT NULL COMMENT '图片高度',
  `file_size` int NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `mime_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `is_public` tinyint NULL DEFAULT 1 COMMENT '是否公开（0私有 1公开）',
  `visit_count` int NULL DEFAULT 0 COMMENT '访问次数',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`image_id`) USING BTREE,
  UNIQUE INDEX `uk_oss_id`(`oss_id` ASC) USING BTREE,
  INDEX `idx_image_type`(`image_type` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_create_by`(`create_by` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` DESC) USING BTREE,
  INDEX `idx_visit_count`(`visit_count` DESC) USING BTREE,
  INDEX `idx_del_flag`(`del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客图片库表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_image
-- ----------------------------

-- ----------------------------
-- Table structure for blog_like
-- ----------------------------
DROP TABLE IF EXISTS `blog_like`;
CREATE TABLE `blog_like` (
    `like_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `post_id` BIGINT NOT NULL COMMENT '文章ID',
    `user_id` BIGINT NULL DEFAULT NULL COMMENT '用户ID（登录用户）',
    `ip_address` VARCHAR(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址（游客，支持IPv6）',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`like_id`) USING BTREE,
    UNIQUE INDEX `uk_post_user_ip` (
        `post_id` ASC,
        ((COALESCE(`user_id`, 0))) ASC,
        ((COALESCE(`ip_address`, ''))) ASC
    ) USING BTREE,
    INDEX `idx_post_id` (`post_id` ASC) USING BTREE,
    INDEX `idx_user_id` (`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_like
-- ----------------------------

-- ----------------------------
-- Table structure for blog_link
-- ----------------------------
DROP TABLE IF EXISTS `blog_link`;
CREATE TABLE `blog_link`  (
  `link_id` bigint NOT NULL COMMENT '链接ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '链接名称',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '链接地址',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '链接描述',
  `logo` bigint NULL DEFAULT NULL COMMENT '链接Logo图片ID',
  `target` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '_blank' COMMENT '打开方式',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`link_id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `fk_link_logo`(`logo` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '友情链接表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_link
-- ----------------------------

-- ----------------------------
-- Table structure for blog_page
-- ----------------------------
DROP TABLE IF EXISTS `blog_page`;
CREATE TABLE `blog_page`  (
  `page_id` bigint NOT NULL COMMENT '页面ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面标题',
  `slug` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面别名',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面内容',
  `content_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '渲染后的HTML内容',
  `template` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'default' COMMENT '页面模板',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0草稿 1发布 2下架）',
  `comment_enabled` tinyint NULL DEFAULT 0 COMMENT '允许评论（0否 1是）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`page_id`) USING BTREE,
  UNIQUE INDEX `uk_slug`(`slug` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '页面管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_page
-- ----------------------------

-- ----------------------------
-- Table structure for blog_post
-- ----------------------------
DROP TABLE IF EXISTS `blog_post`;
CREATE TABLE `blog_post`  (
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `slug` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章别名（URL友好）',
  `summary` varchar(800) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `cover_image` bigint NULL DEFAULT NULL COMMENT '封面图片ID',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0草稿 1发布 2下架 3回收站）',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶（0否 1是）',
  `is_featured` tinyint NULL DEFAULT 0 COMMENT '是否推荐（0否 1是）',
  `allow_comment` tinyint NULL DEFAULT 1 COMMENT '允许评论（0否 1是）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章访问密码',
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ORIGINAL' COMMENT '来源类型（ORIGINAL原创 REPRINT转载 TRANSLATION翻译）',
  `source_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原文链接',
  `seo_keywords` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO描述',
  `published_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `last_comment_time` datetime NULL DEFAULT NULL COMMENT '最后评论时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
  PRIMARY KEY (`post_id`) USING BTREE,
  UNIQUE INDEX `uk_slug`(`slug` ASC) USING BTREE,
  INDEX `idx_author_id`(`author_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_published_time`(`published_time` ASC) USING BTREE,
  INDEX `idx_author_time`(`author_id` ASC, `published_time` DESC) USING BTREE COMMENT '作者文章列表',
  INDEX `idx_category_time`(`category_id` ASC, `published_time` DESC) USING BTREE COMMENT '分类文章列表',
  INDEX `idx_status_top_featured_time`(`status` ASC, `is_top` DESC, `is_featured` DESC, `published_time` DESC) USING BTREE COMMENT '首页文章列表',
  INDEX `idx_author_status`(`author_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_category_status`(`category_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_featured_status`(`is_featured` ASC, `status` ASC) USING BTREE,
  INDEX `fk_post_cover_image`(`cover_image` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_post
-- ----------------------------

-- ----------------------------
-- Table structure for blog_post_content
-- ----------------------------
DROP TABLE IF EXISTS `blog_post_content`;
CREATE TABLE `blog_post_content`  (
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
  `content_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '渲染后的HTML内容',
  `word_count` int NULL DEFAULT 0 COMMENT '字数统计',
  `reading_time` int NULL DEFAULT NULL COMMENT '预计阅读时间（分钟）',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客文章内容表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_post_content
-- ----------------------------

-- ----------------------------
-- Table structure for blog_post_stats
-- ----------------------------
DROP TABLE IF EXISTS `blog_post_stats`;
CREATE TABLE `blog_post_stats`  (
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `idx_view_count`(`view_count` DESC) USING BTREE,
  INDEX `idx_like_count`(`like_count` DESC) USING BTREE,
  INDEX `idx_comment_count`(`comment_count` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客文章统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_post_stats
-- ----------------------------

-- ----------------------------
-- Table structure for blog_post_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_post_tag`;
CREATE TABLE `blog_post_tag`  (
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`post_id`, `tag_id`) USING BTREE,
  INDEX `fk_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_post_tag
-- ----------------------------

-- ----------------------------
-- Table structure for blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag`  (
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `slug` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签别名',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签描述',
  `color` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#1890ff' COMMENT '标签颜色',
  `post_count` int NULL DEFAULT 0 COMMENT '文章数量',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  UNIQUE INDEX `uk_slug`(`slug` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_tag
-- ----------------------------

-- ----------------------------
-- Table structure for blog_visit
-- ----------------------------
DROP TABLE IF EXISTS `blog_visit`;
CREATE TABLE `blog_visit`  (
  `visit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问记录ID',
  `post_id` bigint NULL DEFAULT NULL COMMENT '文章ID',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP地址（支持IPv6）',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `referer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源页（适当放宽长度）',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`visit_id`) USING BTREE,
  INDEX `idx_post_time`(`post_id` ASC, `visit_time` DESC) USING BTREE,
  INDEX `idx_time`(`visit_time` DESC) USING BTREE,
  INDEX `idx_ip_post_time`(`ip_address` ASC, `post_id` ASC, `visit_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '博客访问记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_visit
-- ----------------------------

-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client`  (
  `id` bigint NOT NULL COMMENT 'id',
  `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端id',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端key',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端秘钥',
  `grant_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授权类型',
  `device_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型',
  `active_timeout` int NULL DEFAULT 1800 COMMENT 'token活跃超时时间',
  `timeout` int NULL DEFAULT 604800 COMMENT 'token固定超时',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统授权表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_client
-- ----------------------------
INSERT INTO `sys_client` VALUES (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, 0, 0, 1, '2025-12-20 10:29:24', 1, '2025-12-20 10:29:24');
INSERT INTO `sys_client` VALUES (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms,social', 'android', 1800, 604800, 0, 0, 1, '2025-12-20 10:29:24', 1, '2025-12-20 10:29:24');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` bigint NOT NULL COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 1, '2025-12-20 10:28:34', NULL, NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 1, '2025-12-20 10:28:34', NULL, NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 1, '2025-12-20 10:28:34', NULL, NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 1, '2025-12-20 10:28:34', NULL, NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (11, 'OSS预览列表资源开关', 'sys.oss.previewListResource', 'true', 'Y', 1, '2025-12-20 10:28:34', NULL, NULL, 'true:开启, false:关闭');
INSERT INTO `sys_config` VALUES (12, '网站名称', 'blog.site_name', '我的博客', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客网站名称');
INSERT INTO `sys_config` VALUES (13, '网站描述', 'blog.site_description', '基于若依开发的个人博客系统', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客网站描述');
INSERT INTO `sys_config` VALUES (14, '网站关键词', 'blog.site_keywords', '博客,技术,分享', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客网站关键词');
INSERT INTO `sys_config` VALUES (15, '网站作者', 'blog.site_author', '管理员', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客网站作者');
INSERT INTO `sys_config` VALUES (16, '每页文章数量', 'blog.posts_per_page', '10', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客每页显示的文章数量');
INSERT INTO `sys_config` VALUES (17, '是否开启评论', 'blog.comment_enabled', 'true', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客是否允许评论');
INSERT INTO `sys_config` VALUES (18, '评论是否需要审核', 'blog.comment_audit', 'false', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客评论是否需要审核');
INSERT INTO `sys_config` VALUES (19, '是否开启访问统计', 'blog.visit_statistics', 'true', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '博客是否开启访问统计');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', 1, '2025-12-20 10:28:02', NULL, NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', 1, '2025-12-20 10:28:02', NULL, NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', 1, '2025-12-20 10:28:02', NULL, NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', 1, '2025-12-20 10:28:02', NULL, NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', 1, '2025-12-20 10:28:02', NULL, NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', 1, '2025-12-20 10:28:02', NULL, NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (29, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (30, 0, '密码认证', 'password', 'sys_grant_type', 'el-check-tag', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '密码认证');
INSERT INTO `sys_dict_data` VALUES (31, 0, '短信认证', 'sms', 'sys_grant_type', 'el-check-tag', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '短信认证');
INSERT INTO `sys_dict_data` VALUES (32, 0, '邮件认证', 'email', 'sys_grant_type', 'el-check-tag', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '邮件认证');
INSERT INTO `sys_dict_data` VALUES (33, 0, '小程序认证', 'xcx', 'sys_grant_type', 'el-check-tag', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '小程序认证');
INSERT INTO `sys_dict_data` VALUES (34, 0, '三方登录认证', 'social', 'sys_grant_type', 'el-check-tag', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '三方登录认证');
INSERT INTO `sys_dict_data` VALUES (35, 0, 'PC', 'pc', 'sys_device_type', '', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, 'PC');
INSERT INTO `sys_dict_data` VALUES (36, 0, '安卓', 'android', 'sys_device_type', '', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '安卓');
INSERT INTO `sys_dict_data` VALUES (37, 0, 'iOS', 'ios', 'sys_device_type', '', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, 'iOS');
INSERT INTO `sys_dict_data` VALUES (38, 0, '小程序', 'xcx', 'sys_device_type', '', 'default', 'N', 1, '2025-12-20 10:28:02', NULL, NULL, '小程序');
INSERT INTO `sys_dict_data` VALUES (1001, 1, '文章封面', 'POST_COVER', 'blog_image_type', 'primary', 'primary', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '文章封面图片');
INSERT INTO `sys_dict_data` VALUES (1002, 2, '文章内容', 'POST_CONTENT', 'blog_image_type', 'success', 'success', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '文章正文中的图片');
INSERT INTO `sys_dict_data` VALUES (1003, 3, '文章图集', 'POST_GALLERY', 'blog_image_type', 'info', 'info', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '文章图集中的图片');
INSERT INTO `sys_dict_data` VALUES (1004, 4, '用户头像', 'AVATAR', 'blog_image_type', 'warning', 'warning', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '用户头像图片');
INSERT INTO `sys_dict_data` VALUES (1005, 5, '分类封面', 'CATEGORY_COVER', 'blog_image_type', 'danger', 'danger', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '分类封面图片');
INSERT INTO `sys_dict_data` VALUES (1006, 6, '链接Logo', 'LINK_LOGO', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '友情链接Logo');
INSERT INTO `sys_dict_data` VALUES (1007, 7, '网站Logo', 'SITE_LOGO', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '网站Logo图片');
INSERT INTO `sys_dict_data` VALUES (1008, 8, '网站图标', 'FAVICON', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '网站Favicon图标');
INSERT INTO `sys_dict_data` VALUES (1009, 9, '轮播图', 'BANNER', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '首页轮播图');
INSERT INTO `sys_dict_data` VALUES (1010, 10, '广告图片', 'ADVERTISEMENT', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '广告位图片');
INSERT INTO `sys_dict_data` VALUES (1011, 11, '水印图片', 'WATERMARK', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '水印图片');
INSERT INTO `sys_dict_data` VALUES (1012, 12, '二维码', 'QR_CODE', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '二维码图片');
INSERT INTO `sys_dict_data` VALUES (1013, 99, '其他', 'OTHER', 'blog_image_type', '', '', 'N', 1, '2025-12-20 10:14:19', NULL, NULL, '其他类型图片');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', 1, '2025-12-20 10:27:29', NULL, NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', 1, '2025-12-20 10:27:29', NULL, NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', 1, '2025-12-20 10:27:29', NULL, NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', 1, '2025-12-20 10:27:29', NULL, NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', 1, '2025-12-20 10:27:29', NULL, NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', 1, '2025-12-20 10:27:29', NULL, NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', 1, '2025-12-20 10:27:29', NULL, NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', 1, '2025-12-20 10:27:29', NULL, NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (11, '授权类型', 'sys_grant_type', 1, '2025-12-20 10:27:29', NULL, NULL, '认证授权类型');
INSERT INTO `sys_dict_type` VALUES (12, '设备类型', 'sys_device_type', 1, '2025-12-20 10:27:29', NULL, NULL, '客户端设备类型');
INSERT INTO `sys_dict_type` VALUES (100, '图片类型', 'blog_image_type', 1, '2025-12-20 10:14:19', 1, '2025-12-20 10:14:19', '博客图片类型字典');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户账号',
  `client_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '客户端',
  `device_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '设备类型',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` tinyint NULL DEFAULT 0 COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (2002206946838745089, 'admin', 'pc', 'pc', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome', 'Windows 10 or Windows Server 2016', 0, '登录成功', '2025-12-20 10:38:30');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` tinyint NULL DEFAULT 0 COMMENT '显示状态（0显示 1隐藏）',
  `status` tinyint NULL DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_menu_type`(`menu_type` ASC) USING BTREE,
  INDEX `idx_menu_status`(`status` ASC, `visible` ASC) USING BTREE,
  INDEX `idx_order_num`(`order_num` ASC) USING BTREE,
  INDEX `idx_path`(`path` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', 0, 0, '', 'system', 1, '2025-12-20 10:26:42', NULL, NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 3, 'monitor', NULL, '', 1, 0, 'M', 0, 0, '', 'monitor', 1, '2025-12-20 10:26:42', NULL, NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', 0, 0, 'system:user:list', 'user', 1, '2025-12-20 10:26:42', NULL, NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', 0, 0, 'system:role:list', 'peoples', 1, '2025-12-20 10:26:42', NULL, NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', 0, 0, 'system:menu:list', 'tree-table', 1, '2025-12-20 10:26:42', NULL, NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', 0, 0, 'system:dict:list', 'dict', 1, '2025-12-20 10:26:42', NULL, NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', 0, 0, 'system:config:list', 'edit', 1, '2025-12-20 10:26:42', NULL, NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', 0, 0, 'system:notice:list', 'message', 1, '2025-12-20 10:26:42', NULL, NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', 0, 0, '', 'log', 1, '2025-12-20 10:26:42', NULL, NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', 0, 0, 'monitor:online:list', 'online', 1, '2025-12-20 10:26:42', NULL, NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', 0, 0, 'monitor:cache:list', 'redis', 1, '2025-12-20 10:26:42', NULL, NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (117, 'Admin监控', 2, 5, 'Admin', 'monitor/admin/index', '', 1, 0, 'C', 0, 0, 'monitor:admin:list', 'dashboard', 1, '2025-12-20 10:26:42', NULL, NULL, 'Admin监控菜单');
INSERT INTO `sys_menu` VALUES (118, '文件管理', 1, 10, 'oss', 'system/oss/index', '', 1, 0, 'C', 0, 0, 'system:oss:list', 'upload', 1, '2025-12-20 10:26:42', NULL, NULL, '文件管理菜单');
INSERT INTO `sys_menu` VALUES (120, '任务调度中心', 2, 6, 'snailjob', 'monitor/snailjob/index', '', 1, 0, 'C', 0, 0, 'monitor:snailjob:list', 'job', 1, '2025-12-20 10:26:42', NULL, NULL, 'SnailJob控制台菜单');
INSERT INTO `sys_menu` VALUES (123, '客户端管理', 1, 11, 'client', 'system/client/index', '', 1, 0, 'C', 0, 0, 'system:client:list', 'international', 1, '2025-12-20 10:26:42', NULL, NULL, '客户端管理菜单');
INSERT INTO `sys_menu` VALUES (130, '分配用户', 1, 2, 'role-auth/user/:roleId', 'system/role/authUser', '', 1, 1, 'C', 1, 0, 'system:role:edit', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '/system/role');
INSERT INTO `sys_menu` VALUES (131, '分配角色', 1, 1, 'user-auth/role/:userId', 'system/user/authRole', '', 1, 1, 'C', 1, 0, 'system:user:edit', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '/system/user');
INSERT INTO `sys_menu` VALUES (132, '字典数据', 1, 6, 'dict-data/index/:dictId', 'system/dict/data', '', 1, 1, 'C', 1, 0, 'system:dict:list', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '/system/dict');
INSERT INTO `sys_menu` VALUES (133, '文件配置管理', 1, 10, 'oss-config/index', 'system/oss/config', '', 1, 1, 'C', 1, 0, 'system:ossConfig:list', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '/system/oss');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', 0, 0, 'monitor:operlog:list', 'form', 1, '2025-12-20 10:26:42', NULL, NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', 0, 0, 'monitor:logininfor:list', 'logininfor', 1, '2025-12-20 10:26:42', NULL, NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', 0, 0, 'system:user:query', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', '', 1, 0, 'F', 0, 0, 'system:user:add', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', '', 1, 0, 'F', 0, 0, 'system:user:edit', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', '', 1, 0, 'F', 0, 0, 'system:user:remove', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', '', 1, 0, 'F', 0, 0, 'system:user:export', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', '', 1, 0, 'F', 0, 0, 'system:user:import', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', '', 1, 0, 'F', 0, 0, 'system:user:resetPwd', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', '', 1, 0, 'F', 0, 0, 'system:role:query', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', '', 1, 0, 'F', 0, 0, 'system:role:add', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', '', 1, 0, 'F', 0, 0, 'system:role:edit', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', '', 1, 0, 'F', 0, 0, 'system:role:remove', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', '', 1, 0, 'F', 0, 0, 'system:role:export', '#', 1, '2025-12-20 10:26:42', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', 0, 0, 'system:menu:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', 0, 0, 'system:menu:add', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', 0, 0, 'system:menu:edit', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', 0, 0, 'system:menu:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', 0, 0, 'system:dict:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', 0, 0, 'system:dict:add', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', 0, 0, 'system:dict:edit', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', 0, 0, 'system:dict:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', 0, 0, 'system:dict:export', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', 0, 0, 'system:config:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', 0, 0, 'system:config:add', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', 0, 0, 'system:config:edit', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', 0, 0, 'system:config:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', 0, 0, 'system:config:export', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', 0, 0, 'system:notice:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', 0, 0, 'system:notice:add', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', 0, 0, 'system:notice:edit', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', 0, 0, 'system:notice:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:operlog:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:operlog:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:operlog:export', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:logininfor:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:logininfor:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:logininfor:export', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:online:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:online:batchLogout', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:online:forceLogout', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', 0, 0, 'monitor:logininfor:unlock', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1061, '客户端管理查询', 123, 1, '#', '', '', 1, 0, 'F', 0, 0, 'system:client:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1062, '客户端管理新增', 123, 2, '#', '', '', 1, 0, 'F', 0, 0, 'system:client:add', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1063, '客户端管理修改', 123, 3, '#', '', '', 1, 0, 'F', 0, 0, 'system:client:edit', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1064, '客户端管理删除', 123, 4, '#', '', '', 1, 0, 'F', 0, 0, 'system:client:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1065, '客户端管理导出', 123, 5, '#', '', '', 1, 0, 'F', 0, 0, 'system:client:export', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1600, '文件查询', 118, 1, '#', '', '', 1, 0, 'F', 0, 0, 'system:oss:query', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1601, '文件上传', 118, 2, '#', '', '', 1, 0, 'F', 0, 0, 'system:oss:upload', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1602, '文件下载', 118, 3, '#', '', '', 1, 0, 'F', 0, 0, 'system:oss:download', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1603, '文件删除', 118, 4, '#', '', '', 1, 0, 'F', 0, 0, 'system:oss:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1620, '配置列表', 118, 5, '#', '', '', 1, 0, 'F', 0, 0, 'system:ossConfig:list', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1621, '配置添加', 118, 6, '#', '', '', 1, 0, 'F', 0, 0, 'system:ossConfig:add', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1622, '配置编辑', 118, 6, '#', '', '', 1, 0, 'F', 0, 0, 'system:ossConfig:edit', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');
INSERT INTO `sys_menu` VALUES (1623, '配置删除', 118, 6, '#', '', '', 1, 0, 'F', 0, 0, 'system:ossConfig:remove', '#', 1, '2025-12-20 10:26:43', NULL, NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` bigint NOT NULL COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_type` tinyint NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告内容',
  `status` tinyint NULL DEFAULT 0 COMMENT '公告状态（0正常 1关闭）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `idx_notice_type`(`notice_type` ASC) USING BTREE,
  INDEX `idx_notice_status`(`status` ASC) USING BTREE,
  INDEX `idx_notice_create_time`(`create_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 新版本发布啦', 2, '新版本内容', 0, 1, '2025-12-20 10:28:48', NULL, NULL, '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 系统凌晨维护', 1, '维护内容', 0, 1, '2025-12-20 10:28:48', NULL, NULL, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作人员',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE,
  INDEX `idx_oper_name_time`(`oper_name` ASC, `oper_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (2002207011301003265, '菜单管理', 3, 'com.han.system.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '/system/menu/cascade/4', '0:0:0:0:0:0:0:1', '内网IP', '[4]', '{\"code\":200,\"msg\":\"操作成功\",\"data\":null}', 0, '', '2025-12-20 10:38:45', 179);
INSERT INTO `sys_oper_log` VALUES (2002207023233798145, '菜单管理', 3, 'com.han.system.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '/system/menu/cascade/5,1500,1501,1502,1503,1504,1505,1506,1507,1508,1509,1510,1511', '0:0:0:0:0:0:0:1', '内网IP', '[5,1500,1501,1502,1503,1504,1505,1506,1507,1508,1509,1510,1511]', '{\"code\":200,\"msg\":\"操作成功\",\"data\":null}', 0, '', '2025-12-20 10:38:48', 154);
INSERT INTO `sys_oper_log` VALUES (2002207039914545154, '菜单管理', 3, 'com.han.system.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '/system/menu/cascade/3,116,115,1055,1056,1058,1057,1059,1060', '0:0:0:0:0:0:0:1', '内网IP', '[3,116,115,1055,1056,1058,1057,1059,1060]', '{\"code\":200,\"msg\":\"操作成功\",\"data\":null}', 0, '', '2025-12-20 10:38:52', 149);
INSERT INTO `sys_oper_log` VALUES (2002207115923722242, '菜单管理', 3, 'com.han.system.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '/system/menu/cascade/103,1017,1018,1019,1020,104,1021,1022,1023,1024,1025', '0:0:0:0:0:0:0:1', '内网IP', '[103,1017,1018,1019,1020,104,1021,1022,1023,1024,1025]', '{\"code\":200,\"msg\":\"操作成功\",\"data\":null}', 0, '', '2025-12-20 10:39:10', 138);

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`  (
  `oss_id` bigint NOT NULL COMMENT '对象存储主键',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原名',
  `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件后缀名',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'URL地址',
  `ext1` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展字段',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '上传人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `service` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'minio' COMMENT '服务商',
  PRIMARY KEY (`oss_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'OSS对象存储表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oss
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oss_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config`  (
  `oss_config_id` bigint NOT NULL COMMENT '主键',
  `config_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '配置key',
  `access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'accessKey',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '秘钥',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '桶名称',
  `prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '前缀',
  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '访问站点',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '自定义域名',
  `is_https` tinyint NULL DEFAULT 0 COMMENT '是否https（0=否,1=是）',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '域',
  `access_policy` tinyint NOT NULL DEFAULT 1 COMMENT '桶权限类型(0=private 1=public 2=custom)',
  `status` tinyint NULL DEFAULT 1 COMMENT '是否默认（0=是,1=否）',
  `ext1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '扩展字段',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`oss_config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '对象存储配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oss_config
-- ----------------------------
INSERT INTO `sys_oss_config` VALUES (1, 'minio', 'ruoyi', 'ruoyi123', 'ruoyi', '', '127.0.0.1:9000', '', 0, '', 1, 0, '', 1, '2025-12-20 10:29:11', 1, '2025-12-20 10:29:11', NULL);
INSERT INTO `sys_oss_config` VALUES (2, 'qiniu', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi', '', 's3-cn-north-1.qiniucs.com', '', 0, '', 1, 1, '', 1, '2025-12-20 10:29:11', 1, '2025-12-20 10:29:11', NULL);
INSERT INTO `sys_oss_config` VALUES (3, 'aliyun', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi', '', 'oss-cn-beijing.aliyuncs.com', '', 0, '', 1, 1, '', 1, '2025-12-20 10:29:11', 1, '2025-12-20 10:29:11', NULL);
INSERT INTO `sys_oss_config` VALUES (4, 'qcloud', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi-1240000000', '', 'cos.ap-beijing.myqcloud.com', '', 0, 'ap-beijing', 1, 1, '', 1, '2025-12-20 10:29:11', 1, '2025-12-20 10:29:11', NULL);
INSERT INTO `sys_oss_config` VALUES (5, 'image', 'ruoyi', 'ruoyi123', 'ruoyi', 'image', '127.0.0.1:9000', '', 0, '', 1, 1, '', 1, '2025-12-20 10:29:12', 1, '2025-12-20 10:29:12', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` tinyint NULL DEFAULT 1 COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `status` tinyint NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_key`(`role_key` ASC) USING BTREE,
  INDEX `idx_role_name`(`role_name` ASC) USING BTREE,
  INDEX `idx_role_status`(`status` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_role_sort`(`role_sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'superadmin', 1, 1, 1, 0, 0, 1, '2025-12-20 10:22:38', NULL, NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (3, '本部门及以下', 'test1', 3, 4, 1, 0, 0, 1, '2025-12-20 10:22:38', NULL, NULL, '');
INSERT INTO `sys_role` VALUES (4, '仅本人', 'test2', 4, 5, 1, 0, 0, 1, '2025-12-20 10:22:38', NULL, NULL, '');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (3, 1);
INSERT INTO `sys_role_menu` VALUES (3, 100);
INSERT INTO `sys_role_menu` VALUES (3, 101);
INSERT INTO `sys_role_menu` VALUES (3, 102);
INSERT INTO `sys_role_menu` VALUES (3, 105);
INSERT INTO `sys_role_menu` VALUES (3, 106);
INSERT INTO `sys_role_menu` VALUES (3, 107);
INSERT INTO `sys_role_menu` VALUES (3, 108);
INSERT INTO `sys_role_menu` VALUES (3, 118);
INSERT INTO `sys_role_menu` VALUES (3, 123);
INSERT INTO `sys_role_menu` VALUES (3, 500);
INSERT INTO `sys_role_menu` VALUES (3, 501);
INSERT INTO `sys_role_menu` VALUES (3, 1001);
INSERT INTO `sys_role_menu` VALUES (3, 1002);
INSERT INTO `sys_role_menu` VALUES (3, 1003);
INSERT INTO `sys_role_menu` VALUES (3, 1004);
INSERT INTO `sys_role_menu` VALUES (3, 1005);
INSERT INTO `sys_role_menu` VALUES (3, 1006);
INSERT INTO `sys_role_menu` VALUES (3, 1007);
INSERT INTO `sys_role_menu` VALUES (3, 1008);
INSERT INTO `sys_role_menu` VALUES (3, 1009);
INSERT INTO `sys_role_menu` VALUES (3, 1010);
INSERT INTO `sys_role_menu` VALUES (3, 1011);
INSERT INTO `sys_role_menu` VALUES (3, 1012);
INSERT INTO `sys_role_menu` VALUES (3, 1013);
INSERT INTO `sys_role_menu` VALUES (3, 1014);
INSERT INTO `sys_role_menu` VALUES (3, 1015);
INSERT INTO `sys_role_menu` VALUES (3, 1016);
INSERT INTO `sys_role_menu` VALUES (3, 1026);
INSERT INTO `sys_role_menu` VALUES (3, 1027);
INSERT INTO `sys_role_menu` VALUES (3, 1028);
INSERT INTO `sys_role_menu` VALUES (3, 1029);
INSERT INTO `sys_role_menu` VALUES (3, 1030);
INSERT INTO `sys_role_menu` VALUES (3, 1031);
INSERT INTO `sys_role_menu` VALUES (3, 1032);
INSERT INTO `sys_role_menu` VALUES (3, 1033);
INSERT INTO `sys_role_menu` VALUES (3, 1034);
INSERT INTO `sys_role_menu` VALUES (3, 1035);
INSERT INTO `sys_role_menu` VALUES (3, 1036);
INSERT INTO `sys_role_menu` VALUES (3, 1037);
INSERT INTO `sys_role_menu` VALUES (3, 1038);
INSERT INTO `sys_role_menu` VALUES (3, 1039);
INSERT INTO `sys_role_menu` VALUES (3, 1040);
INSERT INTO `sys_role_menu` VALUES (3, 1041);
INSERT INTO `sys_role_menu` VALUES (3, 1042);
INSERT INTO `sys_role_menu` VALUES (3, 1043);
INSERT INTO `sys_role_menu` VALUES (3, 1044);
INSERT INTO `sys_role_menu` VALUES (3, 1045);
INSERT INTO `sys_role_menu` VALUES (3, 1050);
INSERT INTO `sys_role_menu` VALUES (3, 1061);
INSERT INTO `sys_role_menu` VALUES (3, 1062);
INSERT INTO `sys_role_menu` VALUES (3, 1063);
INSERT INTO `sys_role_menu` VALUES (3, 1064);
INSERT INTO `sys_role_menu` VALUES (3, 1065);
INSERT INTO `sys_role_menu` VALUES (3, 1600);
INSERT INTO `sys_role_menu` VALUES (3, 1601);
INSERT INTO `sys_role_menu` VALUES (3, 1602);
INSERT INTO `sys_role_menu` VALUES (3, 1603);
INSERT INTO `sys_role_menu` VALUES (3, 1620);
INSERT INTO `sys_role_menu` VALUES (3, 1621);
INSERT INTO `sys_role_menu` VALUES (3, 1622);
INSERT INTO `sys_role_menu` VALUES (3, 1623);
INSERT INTO `sys_role_menu` VALUES (3, 11616);
INSERT INTO `sys_role_menu` VALUES (3, 11618);
INSERT INTO `sys_role_menu` VALUES (3, 11619);
INSERT INTO `sys_role_menu` VALUES (3, 11622);
INSERT INTO `sys_role_menu` VALUES (3, 11623);
INSERT INTO `sys_role_menu` VALUES (3, 11629);
INSERT INTO `sys_role_menu` VALUES (3, 11632);
INSERT INTO `sys_role_menu` VALUES (3, 11633);
INSERT INTO `sys_role_menu` VALUES (3, 11638);
INSERT INTO `sys_role_menu` VALUES (3, 11639);
INSERT INTO `sys_role_menu` VALUES (3, 11640);
INSERT INTO `sys_role_menu` VALUES (3, 11641);
INSERT INTO `sys_role_menu` VALUES (3, 11642);
INSERT INTO `sys_role_menu` VALUES (3, 11643);

-- ----------------------------
-- Table structure for sys_social
-- ----------------------------
DROP TABLE IF EXISTS `sys_social`;
CREATE TABLE `sys_social`  (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `auth_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '平台+平台唯一id',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户来源',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '平台编号唯一id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户昵称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `access_token` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户的授权令牌',
  `expire_in` int NULL DEFAULT NULL COMMENT '用户的授权令牌的有效期，部分平台可能没有',
  `refresh_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '刷新令牌，部分平台可能没有',
  `access_code` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '平台的授权信息，部分平台可能没有',
  `union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的 unionid',
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授予的权限，部分平台可能没有',
  `token_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个别平台的授权信息，部分平台可能没有',
  `id_token` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'id token，部分平台可能没有',
  `mac_algorithm` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小米平台用户的附带属性，部分平台可能没有',
  `mac_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小米平台用户的附带属性，部分平台可能没有',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户的授权code，部分平台可能没有',
  `oauth_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Twitter平台用户的附带属性，部分平台可能没有',
  `oauth_token_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Twitter平台用户的附带属性，部分平台可能没有',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_auth_id`(`auth_id` ASC) USING BTREE,
  INDEX `idx_source`(`source` ASC) USING BTREE,
  INDEX `idx_open_id`(`open_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社会化关系表（已移除外键约束，提升性能）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_social
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'sys_user' COMMENT '用户类型（sys_user系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` tinyint NULL DEFAULT 0 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` bigint NULL DEFAULT NULL COMMENT '头像地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `del_flag` tinyint NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name` ASC) USING BTREE,
  INDEX `idx_nick_name`(`nick_name` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_phonenumber`(`phonenumber` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` DESC) USING BTREE,
  INDEX `idx_login_date`(`login_date` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '疯狂的狮子Li', 'sys_user', 'crazyLionLi@163.com', '15888888888', 1, NULL, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 0, 0, '0:0:0:0:0:0:0:1', '2025-12-20 10:38:30', 1, '2025-12-20 10:21:11', -1, '2025-12-20 10:38:30', '管理员');
INSERT INTO `sys_user` VALUES (3, 'test', '本部门及以下 密码666666', 'sys_user', '', '', 0, NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', 0, 0, '127.0.0.1', '2025-12-20 10:21:11', 1, '2025-12-20 10:21:11', 3, '2025-12-20 10:21:11', NULL);
INSERT INTO `sys_user` VALUES (4, 'test1', '仅本人 密码666666', 'sys_user', '', '', 0, NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', 0, 0, '127.0.0.1', '2025-12-20 10:21:11', 1, '2025-12-20 10:21:11', 4, '2025-12-20 10:21:11', NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (3, 3);
INSERT INTO `sys_user_role` VALUES (4, 4);

SET FOREIGN_KEY_CHECKS = 1;
