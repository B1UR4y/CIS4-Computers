package com.example.computer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Консольный пользовательский интерфейс: показывает меню, читает ввод
 * и вызывает методы сервиса.
 */
@Component
public class ConsoleMenu {

    private final ComputerService service;
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public ConsoleMenu(ComputerService service) {
        this.service = service;
    }

    /** Главный цикл: показывает меню и выполняет выбранную команду. */
    public void run() {
        try {
            if (!checkConnection()) {
                return;
            }
            boolean running = true;
            while (running) {
                printMenu();
                String choice = readLine("Your choice: ").trim();
                try {
                    switch (choice) {
                        case "1" -> addComputer();
                        case "2" -> showAll();
                        case "3" -> editComputer();
                        case "4" -> deleteComputer();
                        case "5" -> searchByPrice();
                        case "0" -> running = false;
                        default -> System.out.println("Unknown command. Enter a number from 0 to 5.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (DataAccessException e) {
                    System.out.println("Database error: " + rootMessage(e));
                }
            }
            System.out.println("Goodbye!");
        } catch (InputClosedException e) {
            // Поток ввода закончился
            System.out.println();
            System.out.println("Input closed. Goodbye!");
        }
    }

    // ------------------------------------------------------------------
    // Команды меню
    // ------------------------------------------------------------------

    /** Запрашивает поля компьютера и добавляет запись. */
    private void addComputer() {
        System.out.println("--- Add a computer ---");
        String brand = readValue("Brand", null, service::checkBrand);
        String model = readValue("Model", null, service::checkModel);
        String cpu = readValue("CPU", null, service::checkCpu);
        String type = readValue("Type", null, service::checkType);
        int ramGb = readValue("RAM, GB", null, text -> service.checkRam(parseInt(text)));
        BigDecimal price = readValue("Price", null, text -> service.checkPrice(parsePrice(text)));

        Computer saved = service.add(new Computer(brand, model, cpu, type, ramGb, price));
        System.out.println("Added. New id = " + saved.getId());
    }

    /** Выводит все записи. */
    private void showAll() {
        System.out.println("--- All computers ---");
        printTable(service.getAll());
    }

    /** Редактирует запись. Пустой ввод оставляет текущее значение поля. */
    private void editComputer() {
        System.out.println("--- Edit a computer ---");
        Computer c = readExistingComputer("Id of the computer to edit (Enter to cancel): ");
        if (c == null) {
            System.out.println("Cancelled.");
            return;
        }
        System.out.println("Current record:");
        printTable(List.of(c));
        System.out.println("Press Enter to keep the current value.");

        c.setBrand(readValue("Brand", c.getBrand(), service::checkBrand));
        c.setModel(readValue("Model", c.getModel(), service::checkModel));
        c.setCpu(readValue("CPU", c.getCpu(), service::checkCpu));
        c.setType(readValue("Type", c.getType(), service::checkType));
        c.setRamGb(readValue("RAM, GB", c.getRamGb(), text -> service.checkRam(parseInt(text))));
        c.setPrice(readValue("Price", c.getPrice(), text -> service.checkPrice(parsePrice(text))));

        if (service.update(c)) {
            System.out.println("Updated.");
        } else {
            System.out.println("The computer with id " + c.getId() + " no longer exists.");
        }
    }

    /** Удаляет запись. */
    private void deleteComputer() {
        System.out.println("--- Delete a computer ---");
        Computer c = readExistingComputer("Id of the computer to delete (Enter to cancel): ");
        if (c == null) {
            System.out.println("Cancelled.");
            return;
        }
        if (service.delete(c.getId())) {
            System.out.println("Deleted.");
        } else {
            System.out.println("The computer with id " + c.getId() + " no longer exists.");
        }
    }

    /** Ищет записи по диапазону цены. */
    private void searchByPrice() {
        System.out.println("--- Search by price ---");
        BigDecimal min = readValue("Minimum price", null, text -> service.checkPrice(parsePrice(text)));
        BigDecimal max = readValue("Maximum price", null, text -> {
            BigDecimal value = service.checkPrice(parsePrice(text));
            if (value.compareTo(min) < 0) {
                throw new IllegalArgumentException("Maximum price must not be less than minimum price ("
                        + min.toPlainString() + ").");
            }
            return value;
        });
        List<Computer> result = service.searchByPrice(min, max);
        System.out.println("Found: " + result.size());
        printTable(result);
    }

    // ------------------------------------------------------------------
    // Вывод
    // ------------------------------------------------------------------

    private void printMenu() {
        System.out.println();
        System.out.println("===== Computers =====");
        System.out.println("1. Add a computer");
        System.out.println("2. Show all computers");
        System.out.println("3. Edit a computer by id");
        System.out.println("4. Delete a computer by id");
        System.out.println("5. Search by price");
        System.out.println("0. Exit");
    }

    /** Выводит список компьютеров в виде таблицы. */
    private void printTable(List<Computer> computers) {
        if (computers.isEmpty()) {
            System.out.println("(no records)");
            return;
        }
        String format = "%-5s %-12s %-20s %-24s %-12s %7s %14s%n";
        System.out.printf(format, "ID", "BRAND", "MODEL", "CPU", "TYPE", "RAM GB", "PRICE");
        for (Computer c : computers) {
            System.out.printf(format, c.getId(), c.getBrand(), c.getModel(), c.getCpu(), c.getType(),
                    c.getRamGb(), c.getPrice().toPlainString());
        }
    }

    // ------------------------------------------------------------------
    // Чтение и разбор ввода
    // ------------------------------------------------------------------

    /** Читает строку. Если ввод закончился, бросает InputClosedException. */
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }

    /**
     * Читает значение, повторяя вопрос до тех пор, пока ввод не пройдет проверку.
     * Если разбор завершился ошибкой, ее текст выводится на экран.
     *
     * @param name    название поля
     * @param current текущее значение или null; при непустом значении пустой ввод оставляет его
     * @param parser  разбор и проверка введенной строки
     */
    private <T> T readValue(String name, T current, Function<String, T> parser) {
        while (true) {
            String line = readLine(prompt(name, current)).trim();
            if (line.isEmpty() && current != null) {
                return current;
            }
            try {
                return parser.apply(line);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Запрашивает идентификатор, пока не будет введен идентификатор существующей записи.
     * Пустой ввод отменяет операцию, тогда возвращается null.
     */
    private Computer readExistingComputer(String prompt) {
        while (true) {
            String line = readLine(prompt).trim();
            if (line.isEmpty()) {
                return null;
            }
            long id;
            try {
                id = Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Enter a whole number.");
                continue;
            }
            Optional<Computer> found = service.getById(id);
            if (found.isPresent()) {
                return found.get();
            }
            System.out.println("No computer with id " + id + ". Try again.");
        }
    }

    /** Разбирает целое число. */
    private int parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a whole number.");
        }
    }

    /** Разбирает число с дробной частью; разделителем может быть точка или запятая. */
    private BigDecimal parsePrice(String text) {
        try {
            return new BigDecimal(text.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a number.");
        }
    }

    /** Формирует приглашение к вводу; текущее значение, если оно есть, выводится в квадратных скобках. */
    private String prompt(String name, Object current) {
        return current == null ? name + ": " : name + " [" + current + "]: ";
    }

    // ------------------------------------------------------------------
    // Служебные методы
    // ------------------------------------------------------------------

    /** Проверяет подключение к базе данных. */
    private boolean checkConnection() {
        try {
            long count = service.count();
            System.out.println("Connected to the database. Records in table: " + count);
            return true;
        } catch (DataAccessException e) {
            System.out.println("Cannot work with the database: " + rootMessage(e));
            System.out.println("Check that PostgreSQL is running, the table exists and");
            System.out.println("the settings in db.properties are correct (see README.md).");
            return false;
        }
    }

    /** Возвращает сообщение первоначальной причины ошибки. */
    private String rootMessage(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String message = root.getMessage();
        return message == null ? root.getClass().getSimpleName() : message.trim();
    }

    /** Признак того, что поток ввода закончился. */
    private static class InputClosedException extends RuntimeException {
    }
}
