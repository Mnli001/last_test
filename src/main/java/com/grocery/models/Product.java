package com.grocery.models;

import com.grocery.database.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private LocalDate expirationDate;
    private Integer supplierId;
    private String supplierName;

    public Product() {}

    public Product(int id, String name, String category, double price, int quantity, LocalDate expirationDate, Integer supplierId, String supplierName) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    @Override
    public String toString() {
        return name;
    }

    public static List<Product> getAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name AS supplier_name FROM product p " +
                     "LEFT JOIN supplier s ON p.supplier_id = s.id ORDER BY p.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Date dateVal = rs.getDate("expiration_date");
                LocalDate expDate = (dateVal != null) ? dateVal.toLocalDate() : null;
                int supplierIdVal = rs.getInt("supplier_id");
                Integer supId = rs.wasNull() ? null : supplierIdVal;

                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    expDate,
                    supId,
                    rs.getString("supplier_name")
                ));
            }
        }
        return list;
    }

    public static boolean insert(String name, String category, double price, int quantity, LocalDate expirationDate, Integer supplierId) throws SQLException {
        String sql = "INSERT INTO product (name, category, price, quantity, expiration_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            if (expirationDate != null) {
                pstmt.setDate(5, Date.valueOf(expirationDate));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            if (supplierId != null) {
                pstmt.setInt(6, supplierId);
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean update(int id, String name, String category, double price, int quantity, LocalDate expirationDate, Integer supplierId) throws SQLException {
        String sql = "UPDATE product SET name = ?, category = ?, price = ?, quantity = ?, expiration_date = ?, supplier_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            if (expirationDate != null) {
                pstmt.setDate(5, Date.valueOf(expirationDate));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            if (supplierId != null) {
                pstmt.setInt(6, supplierId);
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setInt(7, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}
