package ui;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.HttpClient;

public class RegisterWindow {
    public void start(Stage stage) {
        TextField txtUser = new TextField();
        txtUser.setPromptText("Enter new username");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Enter new password");

        Button btnRegister = new Button("Confirm Registration");
        Button btnBack = new Button("Back to Login");

        // Gắn class CSS
        btnBack.getStyleClass().add("button-secondary");

        Label lblTitle = new Label("CREATE NEW ACCOUNT");
        lblTitle.getStyleClass().add("title-label");

        Label lblMsg = new Label();

        btnRegister.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String u = txtUser.getText().trim();
                String p = txtPass.getText().trim();

                if (u.isEmpty() || p.isEmpty()) {
                    lblMsg.setText("Username and Password are required!");
                    lblMsg.setStyle("-fx-text-fill: red;");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = "action=register&username=" + u + "&password=" + p;
                        String res = HttpClient.sendpost("http://localhost:8080/NoteApp/api/login", data);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (res.equals("success")) {
                                    lblMsg.setText("Registration successful! Please login.");
                                    lblMsg.setStyle("-fx-text-fill: green;");
                                } else {
                                    lblMsg.setText("Username exists or Network error!");
                                    lblMsg.setStyle("-fx-text-fill: red;");
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                LoginWindow login = new LoginWindow();
                login.start(new Stage());
            }
        });

        HBox btnBox = new HBox(15, btnRegister, btnBack);
        VBox box = new VBox(15, lblTitle, txtUser, txtPass, btnBox, lblMsg);
        box.setPadding(new Insets(25));

        Scene scene = new Scene(box, 400, 250);

        // Nạp CSS
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found");
        }

        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }
}