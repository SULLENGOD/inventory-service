package com.inventory.inventory.application.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.inventory.application.dto.ItemRequestDto;
import com.inventory.inventory.domain.model.Item;
import com.inventory.inventory.infrastructure.adapter.out.ItemsRepository;

@Service
public class InventoryService {

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

}
