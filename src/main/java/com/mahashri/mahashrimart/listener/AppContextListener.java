package com.mahashri.mahashrimart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.mahashri.mahashrimart.service.ApplicationServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
    public static final String DATA_SOURCE = "mahashrimart.dataSource";
    public static final String SERVICES = "mahashrimart.services";
    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);
    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv().getOrDefault(
                "MAHASHRIMART_DB_URL",
                "jdbc:h2:mem:mahashrimart;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"));
        config.setDriverClassName("org.h2.Driver");
        config.setUsername(System.getenv().getOrDefault("MAHASHRIMART_DB_USER", "sa"));
        config.setPassword(System.getenv().getOrDefault("MAHASHRIMART_DB_PASSWORD", ""));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("MahashriMartPool");
        dataSource = new HikariDataSource(config);
        try {
            DatabaseInitializer.initialize(dataSource);
            event.getServletContext().setAttribute(DATA_SOURCE, dataSource);
            event.getServletContext().setAttribute(SERVICES, new ApplicationServices(dataSource));
        } catch (Exception ex) {
            dataSource.close();
            throw new IllegalStateException("MahashriMart could not initialize its database.", ex);
        }
        log.info("MahashriMart application started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (dataSource != null) dataSource.close();
        log.info("MahashriMart application stopped");
    }

    public static HikariDataSource dataSource(ServletContext context) {
        return (HikariDataSource) context.getAttribute(DATA_SOURCE);
    }

    public static ApplicationServices services(ServletContext context) {
        return (ApplicationServices) context.getAttribute(SERVICES);
    }
}