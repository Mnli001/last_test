package com.grocery.controllers;

import com.grocery.App;
import com.grocery.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Хэрэглэгчийн нэр болон нууц үгээ оруулна уу.");
            return;
        }

        User user = User.login(username, password);
        if (user != null) {
            MainController.setCurrentUser(user);
            try {
                App.setRoot("main");
            } catch (IOException e) {
                lblError.setText("Үндсэн цэсийг ачаалахад алдаа гарлаа: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            lblError.setText("Хэрэглэгчийн нэр эсвэл нууц үг буруу байна!");
        }
    }
}
