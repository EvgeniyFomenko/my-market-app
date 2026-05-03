package ru.practicum.mymarketapp;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public final class PostgresqlTestContainer {
    @Container // декларируем объект учитываемым тест-контейнером
    @ServiceConnection // автоматически назначаем параметры соединения с контейнером
    static final PostgreSQLContainer<?> mysqlContainer =
            new PostgreSQLContainer<>("postgres");
}
