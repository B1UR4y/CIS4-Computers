package com.example.computer;

import java.math.BigDecimal;

/**
 * Сущность "Компьютер": строка таблицы computers.
 */
public class Computer {

    /** Идентификатор. До сохранения в базе данных равен null. */
    private Long id;
    /** Производитель. */
    private String brand;
    /** Модель. */
    private String model;
    /** Процессор. */
    private String cpu;
    /** Тип компьютера. */
    private String type;
    /** Объем оперативной памяти в гигабайтах. */
    private int ramGb;
    /** Цена. */
    private BigDecimal price;

    /** Создает компьютер без идентификатора. */
    public Computer(String brand, String model, String cpu, String type, int ramGb, BigDecimal price) {
        this(null, brand, model, cpu, type, ramGb, price);
    }

    /** Создает компьютер со всеми полями. */
    public Computer(Long id, String brand, String model, String cpu, String type, int ramGb, BigDecimal price) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.cpu = cpu;
        this.type = type;
        this.ramGb = ramGb;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRamGb() {
        return ramGb;
    }

    public void setRamGb(int ramGb) {
        this.ramGb = ramGb;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Computer{id=" + id + ", brand='" + brand + "', model='" + model + "', cpu='" + cpu
                + "', type='" + type + "', ramGb=" + ramGb + ", price=" + price + "}";
    }
}
