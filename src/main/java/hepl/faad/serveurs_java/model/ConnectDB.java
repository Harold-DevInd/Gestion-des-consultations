package hepl.faad.serveurs_java.model;

import java.sql.*;
import java.util.logging.*;

public class ConnectDB {
    private static Connection conn = null;

    public Connection getConn() {
        return conn;
    }

    public ConnectDB() {
        try {
            if (conn == null || conn.isClosed()) {
                String sCon = "jdbc:mysql://127.0.0.1:3306/PourStudent";
                String sUser = "Student";
                String sPwd = "PassStudent1_";
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(sCon, sUser, sPwd);
            }
        }
        catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void close() {
        try {
            conn.close();
            System.out.println("Closing DB connection");
        }
        catch (SQLException ex) {
            Logger.getLogger(ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
try {
        Class.forName("com.mysql.cj.jdbc.Driver");
String host = System.getProperty("db.host", "127.0.0.1");
String port = System.getProperty("db.port", "3306");
String db = System.getProperty("db.name", "PourStudent");
String user = System.getProperty("db.user", "Student");
String pass = System.getProperty("db.pass", "PassStudent1_");

String url = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000",
        host, port, db);

conn = DriverManager.getConnection(url, user, pass);
        }*/
