package ru.mentee.power.crm.domain;


public class Repository {
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    void add(T item);
    Optional<T> findById(UUID id);
    List<T> findAll();
    void remove(UUID id);
}
