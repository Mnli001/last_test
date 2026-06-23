package com.grocery.models;

import com.grocery.database.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockRecord {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private LocalDateTime date;
    private String type;

    public StockRecord() {}

    public StockRecord(int id, int productId, String productName, int quantity, LocalDateTime date, String type) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.date = date;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public static List<StockRecord> getAll() throws SQLException {
        List<StockRecord> list = new ArrayList<>();
        String sql = "(SELECT si.id, si.product_id, p.name AS product_name, si.quantity, si.date, 'IN' AS type " +
                     " FROM stock_in si JOIN product p ON si.product_id = p.id) " +
                     "UNION ALL " +
                     "(SELECT so.id, so.product_id, p.name AS product_name, so.quantity, so.date, 'OUT' AS type " +
                     " FROM stock_out so JOIN product p ON so.product_id = p.id) " +
                     "ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new StockRecord(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("type")
                ));
            }
        }
        return list;
    }

    public static boolean recordStockIn(int productId, int qty) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlInsert = "INSERT INTO stock_in (product_id, quantity) VALUES (?, ?)";
            pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setInt(1, productId);
            pstmtInsert.setInt(2, qty);
            pstmtInsert.executeUpdate();

            String sqlUpdate = "UPDATE product SET quantity = quantity + ? WHERE id = ?";
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, qty);
            pstmtUpdate.setInt(2, productId);
            pstmtUpdate.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmtInsert != null) pstmtInsert.close();
            if (pstmtUpdate != null) pstmtUpdate.close();
            if (conn != null) conn.close();
        }
    }

    public static boolean recordStockOut(int productId, int qty) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlCheck = "SELECT quantity FROM product WHERE id = ?";
            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setInt(1, productId);
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    if (currentQty < qty) {
                        throw new SQLException("Борлуулах тоо хэмжээ агуулахын үлдэгдлээс их байна!");
                    }
                } else {
                    throw new SQLException("Бараа олдсонгүй!");
                }
            }

            String sqlInsert = "INSERT INTO stock_out (product_id, quantity) VALUES (?, ?)";
            pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setInt(1, productId);
            pstmtInsert.setInt(2, qty);
            pstmtInsert.executeUpdate();

            String sqlUpdate = "UPDATE product SET quantity = quantity - ? WHERE id = ?";
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, qty);
            pstmtUpdate.setInt(2, productId);
            pstmtUpdate.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (pstmtCheck != null) pstmtCheck.close();
            if (pstmtInsert != null) pstmtInsert.close();
            if (pstmtUpdate != null) pstmtUpdate.close();
            if (conn != null) conn.close();
        }
    }
}
