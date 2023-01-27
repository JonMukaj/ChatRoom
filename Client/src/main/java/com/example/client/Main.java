package com.example.client;

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
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private static final String APP_TITLE = "ChatRoom Client";

    public TextField usernameInput;
    public TextField addressInput;
    public TextField portInput;
    public ToggleButton connectButton;

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


    public void connect(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

        String username = getUsernameInput().getText();
        String address = getAddressInput().getText();
        int port = Integer.parseInt(getPortInput().getText());

        new Client(stage, username, address, port);
    }

    public TextField getUsernameInput() {
        return usernameInput;
    }

    public void setUsernameInput(TextField usernameInput) {
        this.usernameInput = usernameInput;
    }

    public TextField getAddressInput() {
        return addressInput;
    }

    public void setAddressInput(TextField addressInput) {
        this.addressInput = addressInput;
    }

    public TextField getPortInput() {
        return portInput;
    }

    public void setPortInput(TextField portInput) {
        this.portInput = portInput;
    }

    public ToggleButton getConnectButton() {
        return connectButton;
    }

    public void setConnectButton(ToggleButton connectButton) {
        this.connectButton = connectButton;
    }
}
