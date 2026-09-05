package com.mahashri.mahashrimart.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class JdbcDao {
    protected final DataSource dataSource;

    protected JdbcDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection connection() throws SQLException {
        return dataSource.getConnection();
    }
}