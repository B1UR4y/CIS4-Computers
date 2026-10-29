package com.example.computer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Бизнес-логика: проверка данных и операции над компьютерами.
 * При неверных данных бросает IllegalArgumentException с описанием ошибки.
 */
@Service
public class ComputerService {

    /** Максимально допустимая цена. */
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999.99");

    private final ComputerDao dao;

    @Autowired
    public ComputerService(ComputerDao dao) {
        this.dao = dao;
    }

    /** Проверяет и добавляет компьютер. Возвращает его с присвоенным идентификатором. */
    public Computer add(Computer computer) {
        validate(computer);
        computer.setId(dao.save(computer));
        return computer;
    }

    /** Возвращает все компьютеры. */
    public List<Computer> getAll() {
        return dao.findAll();
    }

    /** Возвращает компьютер по идентификатору. */
    public Optional<Computer> getById(long id) {
        return dao.findById(id);
    }

    /** Проверяет и обновляет компьютер. Возвращает false, если записи нет. */
    public boolean update(Computer computer) {
        validate(computer);
        return dao.update(computer);
    }

    /** Удаляет компьютер. Возвращает false, если записи нет. */
    public boolean delete(long id) {
        return dao.deleteById(id);
    }

    /** Возвращает компьютеры с ценой от min до max включительно. */
    public List<Computer> searchByPrice(BigDecimal min, BigDecimal max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Both price bounds are required.");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum price must not be greater than maximum price.");
        }
        return dao.findByPriceBetween(min, max);
    }

    /** Возвращает количество компьютеров. */
    public long count() {
        return dao.count();
    }

    /** Проверяет производителя. */
    public String checkBrand(String value) {
        return checkText(value, "Brand", 50);
    }

    /** Проверяет модель. */
    public String checkModel(String value) {
        return checkText(value, "Model", 100);
    }

    /** Проверяет процессор. */
    public String checkCpu(String value) {
        return checkText(value, "CPU", 100);
    }

    /** Проверяет тип. */
    public String checkType(String value) {
        return checkText(value, "Type", 30);
    }

    /** Проверяет объем оперативной памяти: должен быть положительным. */
    public int checkRam(int ramGb) {
        if (ramGb <= 0) {
            throw new IllegalArgumentException("RAM must be a positive number of gigabytes.");
        }
        return ramGb;
    }

    /** Проверяет цену и округляет ее до сотых. */
    public BigDecimal checkPrice(BigDecimal value) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException("Price must be zero or greater.");
        }
        BigDecimal price = value.setScale(2, RoundingMode.HALF_UP);
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException("Price is too large (maximum " + MAX_PRICE + ").");
        }
        return price;
    }

    /** Проверяет все поля компьютера и приводит их к единому виду. */
    private void validate(Computer c) {
        c.setBrand(checkBrand(c.getBrand()));
        c.setModel(checkModel(c.getModel()));
        c.setCpu(checkCpu(c.getCpu()));
        c.setType(checkType(c.getType()));
        c.setRamGb(checkRam(c.getRamGb()));
        c.setPrice(checkPrice(c.getPrice()));
    }

    /** Проверяет, что текст не пуст и не длиннее допустимого. Возвращает текст без крайних пробелов. */
    private String checkText(String value, String name, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be empty.");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(name + " is too long (maximum " + maxLength + " characters).");
        }
        return trimmed;
    }
}
