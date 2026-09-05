package com.mahashri.mahashrimart.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {
    @FunctionalInterface
    public interface TransactionWork<T> {
        T run(Connection connection) throws Exception;
    }

    private TransactionManager() {}

    public static <T> T inTransaction(DataSource dataSource, TransactionWork<T> work) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T result = work.run(connection);
                connection.commit();
                connection.setAutoCommit(previousAutoCommit);
                return result;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                if (!connection.isClosed()) connection.setAutoCommit(previousAutoCommit);
            }
        }
    }
}