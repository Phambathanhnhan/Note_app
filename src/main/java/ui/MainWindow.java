package ui;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Note;
import network.HttpClient;
import utils.FileExporter;
import utils.XmlBackup;
import utils.XmlRestore;
import java.io.File;
import java.util.List;

public class MainWindow {
    private ListView<Note> listNotes = new ListView<>();
    private TextField txtTitle = new TextField();
    private TextArea txtContent = new TextArea();
    private TextField txtSearch = new TextField();
    private int currentUserId;
    private Note selectedNote = null;

    public void start(Stage stage, int userId) {
        this.currentUserId = userId;

        Button btnAdd = new Button("Add New");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnLogout = new Button("Logout");
        Button btnSearch = new Button("Search");
        Button btnExportTxt = new Button("Export TXT");
        Button btnExportXml = new Button("Backup XML");
        Button btnImportXml = new Button("Import XML");

        btnDelete.getStyleClass().add("button-delete");
        btnLogout.getStyleClass().add("button-secondary");

        Runnable loadData = new Runnable() {
            @Override
            public void run() {
                String res = HttpClient.sendget("http://localhost:8080/NoteApp/api/note?userid=" + currentUserId + "&keyword=" + txtSearch.getText());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        listNotes.getItems().clear();
                        if(!res.isEmpty() && !res.equals("error") && !res.contains("404")) {
                            String[] items = res.split(";;");
                            for(String item : items) {
                                String[] parts = item.split("\\|");
                                if(parts.length == 3) {
                                    listNotes.getItems().add(new Note(Integer.parseInt(parts[0]), parts[1], parts[2]));
                                }
                            }
                        }
                    }
                });
            }
        };

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { new Thread(loadData).start(); }
        });

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = "action=add&userid=" + currentUserId + "&title=" + txtTitle.getText() + "&content=" + txtContent.getText();
                        HttpClient.sendpost("http://localhost:8080/NoteApp/api/note", data);
                        new Thread(loadData).start();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() { txtTitle.clear(); txtContent.clear(); }
                        });
                    }
                }).start();
            }
        });

        btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(selectedNote != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data = "action=update&noteid=" + selectedNote.id + "&title=" + txtTitle.getText() + "&content=" + txtContent.getText();
                            HttpClient.sendpost("http://localhost:8080/NoteApp/api/note", data);
                            new Thread(loadData).start();
                        }
                    }).start();
                }
            }
        });

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(selectedNote != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data = "action=delete&noteid=" + selectedNote.id;
                            HttpClient.sendpost("http://localhost:8080/NoteApp/api/note", data);
                            new Thread(loadData).start();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() { txtTitle.clear(); txtContent.clear(); selectedNote = null; }
                            });
                        }
                    }).start();
                }
            }
        });

        btnLogout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                LoginWindow login = new LoginWindow();
                login.start(new Stage());
            }
        });

        btnExportTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedNote != null) {
                    FileChooser fc = new FileChooser();
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files", "*.txt"));
                    File file = fc.showSaveDialog(stage);
                    if (file != null) FileExporter.exporttxt(file, selectedNote.title, selectedNote.content);
                }
            }
        });

        btnExportXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (listNotes.getItems().isEmpty()) return;
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
                File file = fc.showSaveDialog(stage);
                if (file != null) XmlBackup.exportxml(file, listNotes.getItems());
            }
        });

        btnImportXml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
                File file = fc.showOpenDialog(stage);

                if (file != null) {
                    List<Note> importedNotes = XmlRestore.importxml(file);
                    List<Note> currentNotes = listNotes.getItems();

                    for (Note newNote : importedNotes) {
                        Note existingNote = null;

                        for (Note c : currentNotes) {
                            if (c.title.equals(newNote.title)) {
                                existingNote = c;
                                break;
                            }
                        }

                        if (existingNote != null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Duplicate Detected");
                            alert.setHeaderText("Note '" + newNote.title + "' already exists!");
                            alert.setContentText("How would you like to proceed?");

                            ButtonType btnReplace = new ButtonType("Replace");
                            ButtonType btnKeepBoth = new ButtonType("Keep Both");
                            ButtonType btnSkip = new ButtonType("Skip", ButtonBar.ButtonData.CANCEL_CLOSE);

                            alert.getButtonTypes().setAll(btnReplace, btnKeepBoth, btnSkip);
                            java.util.Optional<ButtonType> result = alert.showAndWait();

                            if (result.isPresent() && result.get() == btnReplace) {
                                int updateId = existingNote.id;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String data = "action=update&noteid=" + updateId + "&title=" + newNote.title + "&content=" + newNote.content;
                                        HttpClient.sendpost("http://localhost:8080/NoteApp/api/note", data);
                                    }
                                }).start();
                            } else if (result.isPresent() && result.get() == btnKeepBoth) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String data = "action=add&userid=" + currentUserId + "&title=" + newNote.title + " (Copy)&content=" + newNote.content;
                                        HttpClient.sendpost("http://localhost:8080/NoteApp/api/note", data);
                                    }
                                }).start();
                            }
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String data = "action=add&userid=" + currentUserId + "&title=" + newNote.title + "&content=" + newNote.content;
                                    HttpClient.sendpost("http://localhost:8080/NoteApp/api/note", data);
                                }
                            }).start();
                        }
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try { Thread.sleep(500); } catch (Exception e) {}
                            loadData.run();
                        }
                    }).start();
                }
            }
        });

        listNotes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> obs, Note oldVal, Note newVal) {
                if (newVal != null) {
                    selectedNote = newVal;
                    txtTitle.setText(newVal.title);
                    txtContent.setText(newVal.content);
                }
            }
        });

        HBox topBox = new HBox(10, txtSearch, btnSearch);
        VBox leftBox = new VBox(10, topBox, listNotes, new HBox(10, btnExportTxt, btnExportXml, btnImportXml));
        HBox btnBox = new HBox(10, btnAdd, btnUpdate, btnDelete, btnLogout);

        Label lblTitleInput = new Label("Title:");
        Label lblContentInput = new Label("Content:");
        lblTitleInput.getStyleClass().add("title-label");
        lblContentInput.getStyleClass().add("title-label");

        VBox rightBox = new VBox(10, lblTitleInput, txtTitle, lblContentInput, txtContent, btnBox);
        HBox root = new HBox(15, leftBox, rightBox);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 750, 450);

        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found");
        }

        stage.setScene(scene);
        stage.setTitle("Note Management");
        stage.show();

        new Thread(loadData).start();
    }
}