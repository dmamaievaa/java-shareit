package ru.practicum.shareit.item.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.practicum.shareit.exception.InvalidItemException;
import ru.practicum.shareit.item.model.Item;

public class ItemValidator {
    private static final Logger log = LoggerFactory.getLogger(ItemValidator.class);

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            log.error("Validation failed: Item name cannot be null or blank");
            throw new InvalidItemException("Item name cannot be null or blank");
        }
    }

    public static void validateAvailability(Boolean available) {
        if (available == null) {
            log.error("Validation failed: Item availability cannot be null");
            throw new InvalidItemException("Item availability cannot be null");
        }
    }

    public static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            log.error("Validation failed: Item description cannot be null or blank");
            throw new InvalidItemException("Item description cannot be null or blank");
        }
    }

    public static void validateItem(Item item) {
        log.info("Validating item: {}", item);
        validateName(item.getName());
        validateAvailability(item.getAvailable());
        validateDescription(item.getDescription());
        log.info("Item validation passed.");
    }

    public static Item itemPatch(Item currentItem, Item updatedItem) {
        log.info("Patching item: {}", currentItem);

        if (updatedItem.getName() != null && !updatedItem.getName().isBlank()) {
            currentItem.setName(updatedItem.getName());
            log.info("Item name updated to: {}", updatedItem.getName());
        }

        if (updatedItem.getDescription() != null && !updatedItem.getDescription().isBlank()) {
            currentItem.setDescription(updatedItem.getDescription());
            log.info("Item description updated to: {}", updatedItem.getDescription());
        }

        if (updatedItem.getAvailable() != null) {
            currentItem.setAvailable(updatedItem.getAvailable());
            log.info("Item availability updated to: {}", updatedItem.getAvailable());
        }

        log.info("Item patching completed.");
        return currentItem;
    }
}
