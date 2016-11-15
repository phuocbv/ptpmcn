/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DA CUOI
 */
public class JDBC {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/mydb?characterEncoding=utf8";
    static final String USER = "root";
    static final String PASS = "";

    private static Connection conn = null;

    public static Connection connectDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public static ResultSet selectSQL(String sql) {
        ResultSet rs = null;
        try {     
            Statement stmt = connectDB().createStatement();;
            conn.setAutoCommit(false);
            rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static boolean updateSQL(String sql) {
        boolean result = false;

        return result;
    }

    public static boolean insertSQL(String sql) {
        boolean result = false;

        return result;
    }

    public static boolean deleteSQL(String sql) {
        boolean result = false;

        return result;
    }

    public static Connection getConn() {
        return conn;
    }
    
    public static void commit() {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void rollback() {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex1) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    public static void closeConnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
