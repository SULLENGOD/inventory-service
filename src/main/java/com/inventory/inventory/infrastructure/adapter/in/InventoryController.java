package com.inventory.inventory.infrastructure.adapter.in;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.inventory.inventory.application.service.InventoryService;
import com.inventory.inventory.application.service.XlsxService;
import com.inventory.inventory.domain.model.Item;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    @Autowired
    private XlsxService xlsxService;

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/file")
    public ResponseEntity<Object> uploadInventoryFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("The archive is empty");
        }

        try {
            List<Item> items = xlsxService.processFile(file);
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknow error");
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Item>> getAll() {
        List<Item> items = inventoryService.findAll();

        return ResponseEntity.ok(items);
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkApi() {
        return ResponseEntity.ok("its online! .l.");
    }

}
