package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> search(String text);

    @Query("select i from Item i where i.owner.id = :userId")
    List<Item> getAllByUserId(@Param("userId") Long userId);

    List<Item> findAllByRequestId(Long itemId);
}
