package com.inventory.inventory.infrastructure.adapter.out;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.inventory.domain.model.Item;

public interface ItemsRepository extends JpaRepository<Item, UUID> {
    Item findByCode(String code);
}
