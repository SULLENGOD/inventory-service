package com.inventory.inventory.application.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.inventory.application.dto.ItemRequestDto;
import com.inventory.inventory.domain.model.Item;
import com.inventory.inventory.domain.model.UpdateItemDto;
import com.inventory.inventory.infrastructure.adapter.out.ItemsRepository;

@Service
public class InventoryService {
    
    Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Item> findAll() {
        return itemsRepository.findAll();
    }

    public Item addItem(ItemRequestDto newItem) {
        return itemsRepository.save(modelMapper.map(newItem, Item.class));
    }

    public Item getItem(String code) {
        return itemsRepository.findByCode(code);
    }

    public Item updateItem(UpdateItemDto updateItem, String code) {

        Item targetItem = itemsRepository.findByCode(code);

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updateItem, targetItem);

        logger.info(updateItem.getName());

        return itemsRepository.save(targetItem);
    }

    public void deleteItem(String itemCode) {
        Item targetItem = itemsRepository.findByCode(itemCode);

        itemsRepository.delete(targetItem);
    }

}
