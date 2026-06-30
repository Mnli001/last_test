package com.grocery.controllers;

import com.grocery.App;
import com.grocery.models.Product;
import com.grocery.models.StockRecord;
import com.grocery.models.Supplier;
import com.grocery.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MainController {

    
    private static User currentUser;

    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML private Label lblUserRole;

    
    @FXML private Label lblTotalProducts;
    @FXML private Label lblExpiredAlerts;
    @FXML private Label lblLowStockAlerts;
    @FXML private Label lblTotalSuppliers;
    @FXML private TableView<Product> tblExpiringSoon;
    @FXML private TableColumn<Product, String> colExpName;
    @FXML private TableColumn<Product, String> colExpCategory;
    @FXML private TableColumn<Product, Integer> colExpQty;
    @FXML private TableColumn<Product, LocalDate> colExpDate;

    
    @FXML private TextField txtSearchProduct;
    @FXML private ComboBox<String> cmbFilterCategory;
    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, Integer> colProdId;
    @FXML private TableColumn<Product, String> colProdName;
    @FXML private TableColumn<Product, String> colProdCategory;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, Integer> colProdQty;
    @FXML private TableColumn<Product, LocalDate> colProdExp;
    @FXML private TableColumn<Product, String> colProdSupplier;

    
    @FXML private TextField txtProdName;
    @FXML private ComboBox<String> cmbProdCategory;
    @FXML private TextField txtProdPrice;
    @FXML private TextField txtProdQty;
    @FXML private DatePicker dpProdExp;
    @FXML private ComboBox<Supplier> cmbProdSupplier;

    
    @FXML private ComboBox<Product> cmbStockProduct;
    @FXML private TextField txtStockQty;
    @FXML private TableView<StockRecord> tblStockHistory;
    @FXML private TableColumn<StockRecord, Integer> colStockId;
    @FXML private TableColumn<StockRecord, String> colStockProduct;
    @FXML private TableColumn<StockRecord, Integer> colStockQty;
    @FXML private TableColumn<StockRecord, String> colStockType;
    @FXML private TableColumn<StockRecord, LocalDateTime> colStockDate;

    
    @FXML private TableView<Supplier> tblSuppliers;
    @FXML private TableColumn<Supplier, Integer> colSupId;
    @FXML private TableColumn<Supplier, String> colSupName;
    @FXML private TableColumn<Supplier, String> colSupPhone;
    @FXML private TableColumn<Supplier, String> colSupAddress;

    
    @FXML private TextField txtSupName;
    @FXML private TextField txtSupPhone;
    @FXML private TextArea txtSupAddress;

    
    @FXML private Label lblReportTitle;
    @FXML private Label lblReportSummary;
    @FXML private TableView<Product> tblReport;
    @FXML private TableColumn<Product, Integer> colRepId;
    @FXML private TableColumn<Product, String> colRepName;
    @FXML private TableColumn<Product, String> colRepCategory;
    @FXML private TableColumn<Product, Double> colRepPrice;
    @FXML private TableColumn<Product, Integer> colRepQty;
    @FXML private TableColumn<Product, LocalDate> colRepExp;

    
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private ObservableList<StockRecord> stockList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        
        if (currentUser != null && lblUserRole != null) {
            lblUserRole.setText(currentUser.getUsername().toUpperCase() + " (" + 
                               (currentUser.getRole().equals("ADMIN") ? "Админ" : "Ажилтан") + ")");
        }

        
        setupTableColumns();

        
        refreshAllData();
    }

    private void setupTableColumns() {
        
        colExpName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colExpCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colExpQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colExpDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        
        colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProdCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colProdPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProdQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colProdExp.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        colProdSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        
        colStockId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStockProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colStockQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStockType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStockDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        
        colSupId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSupPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colSupAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        
        colRepId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRepName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colRepCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colRepPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colRepQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRepExp.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
    }

    private void refreshAllData() {
        try {
            
            List<Supplier> dbSuppliers = Supplier.getAll();
            supplierList.setAll(dbSuppliers);
            tblSuppliers.setItems(supplierList);
            cmbProdSupplier.setItems(supplierList);

            
            List<Product> dbProducts = Product.getAll();
            productList.setAll(dbProducts);
            tblProducts.setItems(productList);
            cmbStockProduct.setItems(productList);

            
            List<StockRecord> dbStock = StockRecord.getAll();
            stockList.setAll(dbStock);
            tblStockHistory.setItems(stockList);

            
            List<String> categories = dbProducts.stream()
                .map(Product::getCategory)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
            cmbFilterCategory.setItems(FXCollections.observableArrayList(categories));
            cmbProdCategory.setItems(FXCollections.observableArrayList(categories));

            
            updateDashboardMetrics();

        } catch (SQLException e) {
            showErrorAlert("Өгөгдлийн сангийн алдаа", "Мэдээллийг уншихад алдаа гарлаа.", e.getMessage());
        }
    }

    private void updateDashboardMetrics() {
        lblTotalProducts.setText(String.valueOf(productList.size()));
        lblTotalSuppliers.setText(String.valueOf(supplierList.size()));

        
        long lowStockCount = productList.stream().filter(p -> p.getQuantity() < 10).count();
        lblLowStockAlerts.setText(String.valueOf(lowStockCount));

        
        LocalDate today = LocalDate.now();
        List<Product> expiringSoonList = productList.stream()
            .filter(p -> p.getExpirationDate() != null && 
                         (p.getExpirationDate().isBefore(today) || ChronoUnit.DAYS.between(today, p.getExpirationDate()) <= 7))
            .collect(Collectors.toList());
        lblExpiredAlerts.setText(String.valueOf(expiringSoonList.size()));

        
        tblExpiringSoon.setItems(FXCollections.observableArrayList(expiringSoonList));
    }

    
    

    @FXML
    void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Системээс гарах");
        alert.setHeaderText("Та системээс гарахдаа итгэлтэй байна уу?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentUser = null;
                App.setRoot("login");
            } catch (IOException e) {
                showErrorAlert("Алдаа", "Нэвтрэх цонхыг нээхэд алдаа гарлаа.", e.getMessage());
            }
        }
    }

    
    @FXML
    void handleAddProduct(ActionEvent event) {
        if (!validateProductInput()) return;

        try {
            String name = txtProdName.getText().trim();
            String category = cmbProdCategory.getValue() != null ? cmbProdCategory.getValue().trim() : "";
            double price = Double.parseDouble(txtProdPrice.getText().trim());
            int qty = Integer.parseInt(txtProdQty.getText().trim());
            LocalDate expDate = dpProdExp.getValue();
            Supplier selectedSupplier = cmbProdSupplier.getValue();
            Integer supplierId = selectedSupplier != null ? selectedSupplier.getId() : null;

            if (Product.insert(name, category, price, qty, expDate, supplierId)) {
                showInfoAlert("Амжилттай", "Шинэ бараа амжилттай нэмэгдлээ.");
                clearProductFields(null);
                refreshAllData();
            }
        } catch (SQLException e) {
            showErrorAlert("Бараа нэмэхэд алдаа гарлаа", "Мэдээллийг хадгалахад алдаа гарлаа.", e.getMessage());
        } catch (NumberFormatException e) {
            showErrorAlert("Буруу утга", "Үнэ болон тоо хэмжээ хэсэгт тоон утга оруулна уу.", e.getMessage());
        }
    }

    @FXML
    void handleEditProduct(ActionEvent event) {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("Сонголт", "Засах бараагаа хүснэгтээс сонгоно уу.");
            return;
        }
        if (!validateProductInput()) return;

        try {
            String name = txtProdName.getText().trim();
            String category = cmbProdCategory.getValue() != null ? cmbProdCategory.getValue().trim() : "";
            double price = Double.parseDouble(txtProdPrice.getText().trim());
            int qty = Integer.parseInt(txtProdQty.getText().trim());
            LocalDate expDate = dpProdExp.getValue();
            Supplier selectedSupplier = cmbProdSupplier.getValue();
            Integer supplierId = selectedSupplier != null ? selectedSupplier.getId() : null;

            if (Product.update(selected.getId(), name, category, price, qty, expDate, supplierId)) {
                showInfoAlert("Амжилттай", "Барааны мэдээлэл шинэчлэгдлээ.");
                clearProductFields(null);
                refreshAllData();
            }
        } catch (SQLException e) {
            showErrorAlert("Бараа засварлахад алдаа гарлаа", "Мэдээллийг хадгалахад алдаа гарлаа.", e.getMessage());
        }
    }

    @FXML
    void handleDeleteProduct(ActionEvent event) {
        
        if (currentUser != null && currentUser.getRole().equals("EMPLOYEE")) {
            showWarningAlert("Хандах эрхгүй", "Та бараа устгах эрхгүй байна. Зөвхөн админ устгах боломжтой.");
            return;
        }

        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("Сонголт", "Устгах бараагаа хүснэгтээс сонгоно уу.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Бараа устгах");
        alert.setHeaderText("Та [" + selected.getName() + "] барааг устгахдаа итгэлтэй байна уу?");
        alert.setContentText("Анхаар! Уг бараатай холбоотой бүх орлого, зарлагын түүх хамт устгагдах болно.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (Product.delete(selected.getId())) {
                    showInfoAlert("Амжилттай", "Бараа устгагдлаа.");
                    clearProductFields(null);
                    refreshAllData();
                }
            } catch (SQLException e) {
                showErrorAlert("Бараа устгахад алдаа гарлаа", "Устгах үйлдэл амжилтгүй боллоо.", e.getMessage());
            }
        }
    }

    @FXML
    void selectProductFromTable(MouseEvent event) {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtProdName.setText(selected.getName());
            cmbProdCategory.setValue(selected.getCategory());
            txtProdPrice.setText(String.valueOf(selected.getPrice()));
            txtProdQty.setText(String.valueOf(selected.getQuantity()));
            dpProdExp.setValue(selected.getExpirationDate());
            
            
            if (selected.getSupplierId() != null) {
                for (Supplier s : supplierList) {
                    if (s.getId() == selected.getSupplierId()) {
                        cmbProdSupplier.setValue(s);
                        break;
                    }
                }
            } else {
                cmbProdSupplier.setValue(null);
            }
        }
    }

    @FXML
    void clearProductFields(ActionEvent event) {
        txtProdName.clear();
        cmbProdCategory.setValue(null);
        txtProdPrice.clear();
        txtProdQty.clear();
        dpProdExp.setValue(null);
        cmbProdSupplier.setValue(null);
        tblProducts.getSelectionModel().clearSelection();
    }

    private boolean validateProductInput() {
        if (txtProdName.getText().trim().isEmpty()) {
            showWarningAlert("Дутуу мэдээлэл", "Барааны нэрийг оруулна уу.");
            return false;
        }
        try {
            double price = Double.parseDouble(txtProdPrice.getText().trim());
            if (price <= 0) {
                showWarningAlert("Буруу үнэ", "Барааны үнэ 0-ээс их байх ёстой.");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarningAlert("Буруу үнэ", "Барааны үнэ хэсэгт зөвхөн тоо оруулна уу.");
            return false;
        }
        try {
            int qty = Integer.parseInt(txtProdQty.getText().trim());
            if (qty < 0) {
                showWarningAlert("Буруу тоо хэмжээ", "Барааны тоо хэмжээ 0-ээс бага байж болохгүй.");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarningAlert("Буруу тоо хэмжээ", "Барааны тоо хэмжээ хэсэгт зөвхөн бүхэл тоо оруулна уу.");
            return false;
        }
        return true;
    }

    @FXML
    void filterProducts() {
        String search = txtSearchProduct.getText().toLowerCase().trim();
        String selectedCategory = cmbFilterCategory.getValue();

        ObservableList<Product> filtered = productList.filtered(p -> {
            boolean matchesSearch = p.getName().toLowerCase().contains(search) ||
                                    (p.getCategory() != null && p.getCategory().toLowerCase().contains(search));
            boolean matchesCategory = selectedCategory == null || selectedCategory.isEmpty() ||
                                      (p.getCategory() != null && p.getCategory().equals(selectedCategory));
            return matchesSearch && matchesCategory;
        });
        tblProducts.setItems(filtered);
    }

    @FXML
    void clearProductFilters(ActionEvent event) {
        txtSearchProduct.clear();
        cmbFilterCategory.setValue(null);
        tblProducts.setItems(productList);
    }

    
    @FXML
    void handleStockIn(ActionEvent event) {
        Product selected = cmbStockProduct.getValue();
        if (selected == null) {
            showWarningAlert("Сонголт", "Орлого авах бараагаа сонгоно уу.");
            return;
        }

        try {
            int qty = Integer.parseInt(txtStockQty.getText().trim());
            if (qty <= 0) {
                showWarningAlert("Буруу утга", "Орлогын тоо хэмжээ 0-ээс их байх ёстой.");
                return;
            }

            if (StockRecord.recordStockIn(selected.getId(), qty)) {
                showInfoAlert("Амжилттай", "Орлого амжилттай бүртгэгдлээ.");
                txtStockQty.clear();
                cmbStockProduct.setValue(null);
                refreshAllData();
            }
        } catch (NumberFormatException e) {
            showWarningAlert("Буруу утга", "Тоо хэмжээ хэсэгт зөвхөн бүхэл тоо оруулна уу.");
        } catch (SQLException e) {
            showErrorAlert("Орлого авахад алдаа гарлаа", "Гүйлгээ амжилтгүй боллоо.", e.getMessage());
        }
    }

    @FXML
    void handleStockOut(ActionEvent event) {
        Product selected = cmbStockProduct.getValue();
        if (selected == null) {
            showWarningAlert("Сонголт", "Зарлага гаргах бараагаа сонгоно уу.");
            return;
        }

        try {
            int qty = Integer.parseInt(txtStockQty.getText().trim());
            if (qty <= 0) {
                showWarningAlert("Буруу утга", "Зарлагын тоо хэмжээ 0-ээс их байх ёстой.");
                return;
            }

            if (StockRecord.recordStockOut(selected.getId(), qty)) {
                showInfoAlert("Амжилттай", "Зарлага амжилттай бүртгэгдлээ.");
                txtStockQty.clear();
                cmbStockProduct.setValue(null);
                refreshAllData();
            }
        } catch (NumberFormatException e) {
            showWarningAlert("Буруу утга", "Тоо хэмжээ хэсэгт зөвхөн бүхэл тоо оруулна уу.");
        } catch (SQLException e) {
            showErrorAlert("Зарлага гаргахад алдаа гарлаа", "Гүйлгээ амжилтгүй боллоо.", e.getMessage());
        }
    }

    
    @FXML
    void handleAddSupplier(ActionEvent event) {
        String name = txtSupName.getText().trim();
        String phone = txtSupPhone.getText().trim();
        String address = txtSupAddress.getText().trim();

        if (name.isEmpty()) {
            showWarningAlert("Дутуу мэдээлэл", "Нийлүүлэгчийн нэрийг оруулна уу.");
            return;
        }

        try {
            if (Supplier.insert(name, phone, address)) {
                showInfoAlert("Амжилттай", "Нийлүүлэгч нэмэгдлээ.");
                clearSupplierFields(null);
                refreshAllData();
            }
        } catch (SQLException e) {
            showErrorAlert("Алдаа", "Нийлүүлэгч нэмэхэд алдаа гарлаа.", e.getMessage());
        }
    }

    @FXML
    void handleEditSupplier(ActionEvent event) {
        Supplier selected = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("Сонголт", "Засварлах нийлүүлэгчээ сонгоно уу.");
            return;
        }

        String name = txtSupName.getText().trim();
        String phone = txtSupPhone.getText().trim();
        String address = txtSupAddress.getText().trim();

        if (name.isEmpty()) {
            showWarningAlert("Дутуу мэдээлэл", "Нийлүүлэгчийн нэрийг оруулна уу.");
            return;
        }

        try {
            if (Supplier.update(selected.getId(), name, phone, address)) {
                showInfoAlert("Амжилттай", "Нийлүүлэгчийн мэдээлэл шинэчлэгдлээ.");
                clearSupplierFields(null);
                refreshAllData();
            }
        } catch (SQLException e) {
            showErrorAlert("Алдаа", "Нийлүүлэгчийн мэдээлэл засварлахад алдаа гарлаа.", e.getMessage());
        }
    }

    @FXML
    void handleDeleteSupplier(ActionEvent event) {
        if (currentUser != null && currentUser.getRole().equals("EMPLOYEE")) {
            showWarningAlert("Хандах эрхгүй", "Та нийлүүлэгч устгах эрхгүй байна. Зөвхөн админ устгах боломжтой.");
            return;
        }

        Supplier selected = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("Сонголт", "Устгах нийлүүлэгчээ сонгоно уу.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Нийлүүлэгч устгах");
        alert.setHeaderText("Та [" + selected.getName() + "] нийлүүлэгчийг устгахдаа итгэлтэй байна уу?");
        alert.setContentText("Санамж: Энэ нийлүүлэгчтэй холбоотой бараанууд устгагдахгүй бөгөөд нийлүүлэгчийн мэдээлэл нь хоосон болж шинэчлэгдэнэ.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (Supplier.delete(selected.getId())) {
                    showInfoAlert("Амжилттай", "Нийлүүлэгч устгагдлаа.");
                    clearSupplierFields(null);
                    refreshAllData();
                }
            } catch (SQLException e) {
                showErrorAlert("Алдаа", "Нийлүүлэгч устгахад алдаа гарлаа.", e.getMessage());
            }
        }
    }

    @FXML
    void selectSupplierFromTable(MouseEvent event) {
        Supplier selected = tblSuppliers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtSupName.setText(selected.getName());
            txtSupPhone.setText(selected.getPhone());
            txtSupAddress.setText(selected.getAddress());
        }
    }

    @FXML
    void clearSupplierFields(ActionEvent event) {
        txtSupName.clear();
        txtSupPhone.clear();
        txtSupAddress.clear();
        tblSuppliers.getSelectionModel().clearSelection();
    }

    
    @FXML
    void loadGeneralReport(ActionEvent event) {
        lblReportTitle.setText("Нийт барааны тайлан (Агуулахын үнэлгээ)");
        tblReport.setItems(productList);
        
        double totalVal = productList.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        lblReportSummary.setText("Агуулахын нийт үнэлгээ: " + String.format("%,.2f ₮", totalVal));
    }

    @FXML
    void loadExpirationReport(ActionEvent event) {
        lblReportTitle.setText("Дуусах хугацаа дөхсөн эсвэл дууссан барааны тайлан");
        LocalDate today = LocalDate.now();
        ObservableList<Product> filtered = productList.filtered(p -> 
            p.getExpirationDate() != null && 
            (p.getExpirationDate().isBefore(today) || ChronoUnit.DAYS.between(today, p.getExpirationDate()) <= 7)
        );
        tblReport.setItems(filtered);
        lblReportSummary.setText("Анхааруулгатай барааны тоо: " + filtered.size());
    }

    @FXML
    void loadLowStockReport(ActionEvent event) {
        lblReportTitle.setText("Дутагдалтай / Бага үлдэгдэлтэй барааны тайлан (Үлдэгдэл < 10)");
        ObservableList<Product> filtered = productList.filtered(p -> p.getQuantity() < 10);
        tblReport.setItems(filtered);
        lblReportSummary.setText("Үлдэгдэл бага барааны тоо: " + filtered.size());
    }

    
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}







