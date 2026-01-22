package com.han.common.excel.core;

import cn.hutool.core.collection.CollUtil;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.handler.WorkbookWriteHandler;
import cn.idev.excel.write.handler.context.WorkbookWriteHandlerContext;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 列值重复合并策略
 */
@Slf4j
public class CellMergeStrategy extends AbstractMergeStrategy implements WorkbookWriteHandler {

    private final List<CellRangeAddress> cellList;
    private final Map<Integer, List<CellRangeAddress>> rowIndexMap = new HashMap<>();

    public CellMergeStrategy(List<CellRangeAddress> cellList) {
        this.cellList = cellList;
        if (CollUtil.isNotEmpty(cellList)) {
            cellList.forEach(this::addRowIndex);
        }
    }

    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.cellList = CellMergeHandler.of(hasTitle).handle(list);
        if (CollUtil.isNotEmpty(cellList)) {
            cellList.forEach(this::addRowIndex);
        }
    }

    private void addRowIndex(CellRangeAddress cellRangeAddress) {
        for (int i = cellRangeAddress.getFirstRow(); i <= cellRangeAddress.getLastRow(); i++) {
            rowIndexMap.computeIfAbsent(i, k -> new ArrayList<>()).add(cellRangeAddress);
        }
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (CollUtil.isEmpty(cellList)){
            return;
        }
        final int rowIndex = cell.getRowIndex();
        List<CellRangeAddress> ranges = rowIndexMap.get(rowIndex);
        if (CollUtil.isEmpty(ranges)) {
            return;
        }
        for (CellRangeAddress cellAddresses : ranges) {
            if (cellAddresses.isInRange(cell) && rowIndex != cellAddresses.getFirstRow()){
                cell.setBlank();
            }
        }
    }

    @Override
    public void afterWorkbookDispose(final WorkbookWriteHandlerContext context) {
        if (CollUtil.isEmpty(cellList)){
            return;
        }
        //当前表格写完后，统一写入
        for (CellRangeAddress item : cellList) {
            context.getWriteContext().writeSheetHolder().getSheet().addMergedRegion(item);
        }
    }
}
