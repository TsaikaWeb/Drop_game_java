package com.Tsaika;

import java.rmi.server.ExportException;
import java.sql.*;
import java.util.ArrayList;
public class Database {
    public String host, user, password;
    public Connection connection;
    private Object Connection;

    public Database(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;

    }

    public void addRecord(String name, int score) {
        try {
            String sql = String.format("INSERT INTO droptsaika(name,score) VALUES('%s', %d)", name, score);
            Statement st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRecords() {
        ArrayList<String> result = new ArrayList<String>();
        try {
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM player");
            while (res.next()) {
                int score = res.getInt(3);
                String name = res.getString(2);
                String date = res.getString(4);
            }
        } catch (Exception e) {
        }

    }

    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            Connection = DriverManager.getConnection(host, user, password);
            // создание таблицы

            System.out.println("Database has been created!");
        } catch (Exception ex) {
            System.out.println("Connection failed...");

            System.out.println(ex);

        }
    }
}
