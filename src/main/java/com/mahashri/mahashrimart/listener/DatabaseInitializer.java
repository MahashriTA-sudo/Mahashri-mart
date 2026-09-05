package com.mahashri.mahashrimart.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

final class DatabaseInitializer {
    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private DatabaseInitializer() {}

    static void initialize(DataSource dataSource) throws IOException, SQLException {
        try (Connection connection = dataSource.getConnection()) {
            executeScript(connection, "db/schema.sql");
            executeScript(connection, "db/seed.sql");
        }
        log.info("MahashriMart database initialized");
    }

    private static void executeScript(Connection connection, String resource) throws IOException, SQLException {
        try (InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) throw new IOException("Missing database resource: " + resource);
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            Arrays.stream(script.split(";"))
                    .map(String::trim)
                    .filter(statement -> !statement.isBlank())
                    .forEach(statement -> execute(connection, statement));
        }
    }

    private static void execute(Connection connection, String statement) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not execute database statement.", ex);
        }
    }
}