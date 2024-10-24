package ru.practicum.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorIdOrderByCreatedAsc(Long userId);
}