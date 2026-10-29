package com.example.computer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Класс-конфигурация Spring: поиск компонентов, подключение файла настроек,
 * объявление источника данных и JdbcTemplate.
 */
@Configuration
@ComponentScan("com.example.computer")
@PropertySource("file:./db.properties")
public class AppConfig {

    /** Источник соединений с базой данных. */
    @Bean
    public DataSource dataSource(@Value("${db.url}") String url,
                                 @Value("${db.username}") String username,
                                 @Value("${db.password}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }

    /** Инструмент выполнения SQL-запросов. */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
