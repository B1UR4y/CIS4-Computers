package com.example.computer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Доступ к данным: операции с таблицей computers.
 */
public interface ComputerDao {

    /**
     * Добавляет запись.
     *
     * @return идентификатор новой записи
     */
    long save(Computer computer);

    /** Возвращает все записи, отсортированные по идентификатору. */
    List<Computer> findAll();

    /** Возвращает запись по идентификатору или пустой результат, если записи нет. */
    Optional<Computer> findById(long id);

    /**
     * Обновляет запись с идентификатором computer.getId().
     *
     * @return true, если запись найдена и обновлена
     */
    boolean update(Computer computer);

    /**
     * Удаляет запись по идентификатору.
     *
     * @return true, если запись существовала и была удалена
     */
    boolean deleteById(long id);

    /** Возвращает записи с ценой от min до max, границы включены. */
    List<Computer> findByPriceBetween(BigDecimal min, BigDecimal max);

    /** Возвращает количество записей. */
    long count();
}
