package com.example.server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private static final String APP_TITLE = "ChatRoom Server";

    public TextField portInput;
    public ToggleButton startServerButton;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("launcher.fxml")));
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image(String.valueOf(this.getClass().getResource("img/favicon.png"))));
        stage.setTitle(APP_TITLE);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        Platform.setImplicitExit(true);
        stage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void startServer(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

        int port = Integer.parseInt(getPortInput().getText());

        new Server(stage, port);
    }

    public TextField getPortInput() {
        return portInput;
    }

    public void setPortInput(TextField portInput) {
        this.portInput = portInput;
    }
}
