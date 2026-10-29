package com.example.computer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Точка входа в приложение.
 */
public class Main {

    /** Имя файла с параметрами подключения к базе данных. */
    private static final String SETTINGS_FILE = "db.properties";

    public static void main(String[] args) {
        // Без файла настроек подключиться к базе данных невозможно
        Path settings = Path.of(SETTINGS_FILE).toAbsolutePath();
        if (!Files.exists(settings)) {
            System.out.println("Settings file not found: " + settings);
            System.out.println("Copy db.properties.example to db.properties, set the password and");
            System.out.println("start the program from the directory that contains it (see README.md).");
            System.exit(1);
        }

        // Контекст закрывается автоматически при выходе из блока
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            context.getBean(ConsoleMenu.class).run();
        }
    }
}
