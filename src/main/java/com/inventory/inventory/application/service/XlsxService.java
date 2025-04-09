package com.inventory.inventory.application.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.inventory.inventory.domain.model.Item;
import com.inventory.inventory.infrastructure.adapter.out.ItemsRepository;

@Service
public class XlsxService {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<Item> processFile(MultipartFile file) throws IOException {
        validateName(file.getOriginalFilename());

        try (Workbook workbook = getWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            List<Item> items = parseSheet(sheet);
            itemsRepository.saveAll(items);

            messagingTemplate
                    .convertAndSend("/topic/inventoryUpdate", "The inventory was updated!");

            return items;
        }
    }

    private void validateName(String fileName) {
        if (fileName == null
                || !(fileName.endsWith(".xlsx")
                        || fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("Archive type not valid....");
        }
    }

    private Workbook getWorkbook(MultipartFile file) throws IOException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());

        return fileName.endsWith(".xlsx")
                ? new XSSFWorkbook(file.getInputStream())
                : new HSSFWorkbook(file.getInputStream());
    }

    private List<Item> parseSheet(Sheet sheet) {
        List<Item> items = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();

        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();

            try {
                items.add(parseRow(currentRow));
            } catch (Exception e) {
                throw new IllegalArgumentException("Error" + e.getMessage(), e);
            }
        }

        return items;
    }

    private Item parseRow(Row row) {
        String code = getStringValue(row, 0);
        String name = getStringValue(row, 1);
        Double price = getNumericValue(row, 2);
        Double existence = getNumericValue(row, 3);

        validateFields(code, name, price, existence, row);
        Item targetItem = itemsRepository.findByCode(code);

        Item item = new Item();
        item.setCode(code);
        item.setName(name);
        item.setPrice(price);
        item.setExistence(existence);

        if (targetItem != null) {
            if (targetItem.getPrice() != price) {
                item.setLastPrice(targetItem.getPrice());
            } else {
                item.setLastPrice(targetItem.getLastPrice());
            }
        }
        return item;
    }

    private void validateFields(String code, String name, Double price, Double existence, Row row) {
        if (code == null || name == null || price == null || existence == null) {
            throw new IllegalArgumentException("Error in row: " + row.getRowNum() +
                    " - Possible error on this data. Code: " + code + ", Name: " + name + ", Price: " + price
                    + ", Existence: " + existence);
        }
    }

    private String getStringValue(Row row, int column) {
        return Optional.ofNullable(row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                .map(Cell::getStringCellValue)
                .map(String::trim)
                .orElse(null);
    }

    private Double getNumericValue(Row row, int column) {
        Cell cell = row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        return null;
    }

}
