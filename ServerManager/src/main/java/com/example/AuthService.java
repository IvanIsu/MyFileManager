package com.example;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static String getNickByLoginAndPassword(String login, int password){
        String sql = "SELECT username, userpassword FROM users WHERE userlogin = '" + login + "'";

        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            String nickName = rs.getString(1);
            int passBd = rs.getInt(2);
            if(password == passBd){
                return nickName;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }return null;
    }

    public static boolean createNewUserInBase (String login, String nick, int password){

        String query = "INSERT INTO users (userlogin, username, userpassword) VALUES (?, ?, ?);";
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, login);
            ps.setString(2, nick);
            ps.setInt(3, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        return false;
    }


    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ServerManager/FileManageUsersDB.db");
            stmt = connection.createStatement();
            System.out.println("Connected to DataBase " + stmt.toString() + " " + connection.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
