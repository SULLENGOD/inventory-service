package com.inventory.inventory.application.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.inventory.domain.model.Item;

@Service
public class XlsxExportService {

    @Autowired
    private InventoryService inventoryService;

    public byte[] exportFile() {
        List<Item> items = inventoryService.findAll();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventory");
            createHeader(sheet);

            int rowNum = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowNum++);
                fillRow(row, item);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error creating Excel file", e);
        }
    }

    private void createHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Code",
                "Name",
                "Price",
                "Last Price",
                "Existence"
        };

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

    }

    private void fillRow(Row row, Item item) {
        Object[] values = {
                item.getCode(),
                item.getName(),
                item.getPrice(),
                item.getLastPrice(),
                item.getExistence()
        };

        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i] != null ? values[i].toString() : "");
        }
    }
}
