package com.mahashri.mahashrimart;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public final class EmbeddedServer {
    private EmbeddedServer() {}

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        File webapp = new File("target/mahashrimart");
        if (!webapp.isDirectory()) {
            throw new IllegalStateException("The exploded webapp is missing. Run mvn package before starting Tomcat.");
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("target/tomcat");
        tomcat.getConnector().setURIEncoding("UTF-8");
        Context context = tomcat.addWebapp("", webapp.getAbsolutePath());
        context.setParentClassLoader(EmbeddedServer.class.getClassLoader());
        tomcat.start();
        tomcat.getServer().await();
    }
}