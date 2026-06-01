package ui;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.HttpClient;

public class LoginWindow {
    public void start(Stage stage) {
        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");

        Button btnLogin = new Button("Login");
        Button btnGoRegister = new Button("No account? Register now");

        // Gắn class CSS cho nút đăng ký
        btnGoRegister.getStyleClass().add("button-secondary");

        Label lblTitle = new Label("NOTE MANAGEMENT SYSTEM");
        lblTitle.getStyleClass().add("title-label"); // Gắn class tiêu đề

        Label lblMsg = new Label();

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = "action=login&username=" + txtUser.getText() + "&password=" + txtPass.getText();
                        String res = HttpClient.sendpost("http://localhost:8080/NoteApp/api/login", data);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (res.equals("-1")) {
                                    lblMsg.setText("Invalid username or password!");
                                    lblMsg.setStyle("-fx-text-fill: red;");
                                } else if (res.equals("error") || res.isEmpty() || res.contains("404")) {
                                    lblMsg.setText("Network error or Tomcat is down!");
                                    lblMsg.setStyle("-fx-text-fill: red;");
                                } else {
                                    try {
                                        int userId = Integer.parseInt(res.trim());
                                        stage.close();
                                        MainWindow mainWin = new MainWindow();
                                        mainWin.start(new Stage(), userId);
                                    } catch (Exception ex) {
                                        lblMsg.setText("System processing error!");
                                        lblMsg.setStyle("-fx-text-fill: red;");
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        btnGoRegister.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                RegisterWindow regWin = new RegisterWindow();
                regWin.start(new Stage());
            }
        });

        VBox box = new VBox(15, lblTitle, txtUser, txtPass, btnLogin, btnGoRegister, lblMsg);
        box.setPadding(new Insets(25));

        Scene scene = new Scene(box, 320, 300);

        // Nạp CSS
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found");
        }

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}