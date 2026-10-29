package com.example.computer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Реализация доступа к данным на основе JdbcTemplate.
 */
@Repository
public class JdbcComputerDao implements ComputerDao {

    /** Преобразует строку результата запроса в объект Computer. */
    private static final RowMapper<Computer> ROW_MAPPER = (rs, rowNum) -> new Computer(
            rs.getLong("id"),
            rs.getString("brand"),
            rs.getString("model"),
            rs.getString("cpu"),
            rs.getString("type"),
            rs.getInt("ram_gb"),
            rs.getBigDecimal("price"));

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcComputerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Computer c) {
        // Запрос возвращает идентификатор, выданный базой данных
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO computers (brand, model, cpu, type, ram_gb, price) "
                        + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                c.getBrand(), c.getModel(), c.getCpu(), c.getType(), c.getRamGb(), c.getPrice());
        return Objects.requireNonNull(id);
    }

    @Override
    public List<Computer> findAll() {
        return jdbcTemplate.query(
                "SELECT id, brand, model, cpu, type, ram_gb, price FROM computers ORDER BY id",
                ROW_MAPPER);
    }

    @Override
    public Optional<Computer> findById(long id) {
        List<Computer> result = jdbcTemplate.query(
                "SELECT id, brand, model, cpu, type, ram_gb, price FROM computers WHERE id = ?",
                ROW_MAPPER, id);
        return result.stream().findFirst();
    }

    @Override
    public boolean update(Computer c) {
        // Количество измененных строк равно нулю, если записи с таким идентификатором нет
        int rows = jdbcTemplate.update(
                "UPDATE computers SET brand = ?, model = ?, cpu = ?, type = ?, ram_gb = ?, price = ? "
                        + "WHERE id = ?",
                c.getBrand(), c.getModel(), c.getCpu(), c.getType(), c.getRamGb(), c.getPrice(), c.getId());
        return rows > 0;
    }

    @Override
    public boolean deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM computers WHERE id = ?", id) > 0;
    }

    @Override
    public List<Computer> findByPriceBetween(BigDecimal min, BigDecimal max) {
        return jdbcTemplate.query(
                "SELECT id, brand, model, cpu, type, ram_gb, price FROM computers "
                        + "WHERE price BETWEEN ? AND ? ORDER BY price, id",
                ROW_MAPPER, min, max);
    }

    @Override
    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM computers", Long.class);
        return count == null ? 0 : count;
    }
}
