package com.grocery.models;

import com.grocery.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private int id;
    private String name;
    private String phone;
    private String address;

    public Supplier() {}

    public Supplier(int id, String name, String phone, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name;
    }

    public static List<Supplier> getAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("address")
                ));
            }
        }
        return list;
    }

    public static boolean insert(String name, String phone, String address) throws SQLException {
        String sql = "INSERT INTO supplier (name, phone, address) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean update(int id, String name, String phone, String address) throws SQLException {
        String sql = "UPDATE supplier SET name = ?, phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM supplier WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}
