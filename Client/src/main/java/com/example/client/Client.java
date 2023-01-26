package com.example.client;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {


    public Client(Stage stage, String username, String address, int port) {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(80);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(20);

        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(75);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(20);

        grid.getRowConstraints().addAll(row0, row1);
        grid.getColumnConstraints().addAll(column0, column1);

        Scene scene = new Scene(grid, 600, 520);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();

        new ClientSocket(username, address, port, grid);
    }

    public static class ClientSocket {

        private Socket socket = null;
        private DataInputStream in = null;
        private DataOutputStream out = null;
        private final LinkedBlockingQueue<String> MESSAGES;
        private final LinkedBlockingQueue<String> MESSAGES_BY_CLIENT;

        private final TextFlow CHAT = new TextFlow();
        private final TextArea CHAT_INPUT = new TextArea();
        private final Button BUTTON_SEND = new Button("SEND");
        private String username;


        public ClientSocket(String username, String address, int port, GridPane grid) {

            this.MESSAGES_BY_CLIENT = new LinkedBlockingQueue<>();
            this.MESSAGES = new LinkedBlockingQueue<>();

            try {
                socket = new Socket(address, port);

                in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());

                String serverMessage = in.readUTF();
                System.out.println(serverMessage);

                setUsername(username);
                out.writeUTF(username);

                serverMessage = in.readUTF();
                Text text = new Text(serverMessage);
                CHAT.getChildren().add(text);

                CHAT.getStyleClass().add("server-chat");
                CHAT.setPadding(new Insets(5, 5, 5, 5));
                GridPane.setHgrow(CHAT, Priority.ALWAYS);
                GridPane.setVgrow(CHAT, Priority.ALWAYS);
                ScrollPane sp = new ScrollPane();
                sp.setContent(CHAT);
                GridPane.setConstraints(sp, 0, 0);

                Circle statusCircle = new Circle(0, 0, 10);
                statusCircle.getStyleClass().add("status-circle");
                GridPane.setValignment(statusCircle, VPos.TOP);
                GridPane.setHgrow(statusCircle, Priority.ALWAYS);
                GridPane.setVgrow(statusCircle, Priority.ALWAYS);
                GridPane.setConstraints(statusCircle, 1, 0);

                Label statusLabel = new Label("Online");
                statusLabel.getStyleClass().add("status-label");
                GridPane.setValignment(statusLabel, VPos.TOP);
                GridPane.setHalignment(statusLabel, HPos.CENTER);
                GridPane.setHgrow(statusLabel, Priority.ALWAYS);
                GridPane.setVgrow(statusLabel, Priority.ALWAYS);
                GridPane.setConstraints(statusLabel, 1, 0);

                Label usernameLabel = new Label("User: " + username);
                usernameLabel.getStyleClass().add("status-username");
                GridPane.setValignment(usernameLabel, VPos.TOP);
                GridPane.setHalignment(usernameLabel, HPos.LEFT);
                GridPane.setHgrow(usernameLabel, Priority.ALWAYS);
                GridPane.setVgrow(usernameLabel, Priority.ALWAYS);
                usernameLabel.setPadding(new Insets(30, 0, 0, 0));
                GridPane.setConstraints(usernameLabel, 1, 0);

                CHAT_INPUT.getStyleClass().add("chat-input");
                CHAT_INPUT.setPromptText("Enter message...");
                GridPane.setHgrow(CHAT_INPUT, Priority.ALWAYS);
                GridPane.setVgrow(CHAT_INPUT, Priority.ALWAYS);
                GridPane.setConstraints(CHAT_INPUT, 0, 1);

                BUTTON_SEND.getStyleClass().add("button-send");
                GridPane.setHgrow(BUTTON_SEND, Priority.ALWAYS);
                GridPane.setVgrow(BUTTON_SEND, Priority.ALWAYS);
                GridPane.setHalignment(BUTTON_SEND, HPos.RIGHT);
                GridPane.setValignment(BUTTON_SEND, VPos.TOP);
                GridPane.setConstraints(BUTTON_SEND, 0, 2);


                grid.getChildren().addAll(sp, CHAT_INPUT, BUTTON_SEND, statusCircle, statusLabel, usernameLabel);
                setUpButtonSend();
            } catch (Exception e) {
                System.out.println("error " + e.getMessage());
            }

            Thread userInput = new Thread(() -> {

                try {
                    String message = "";
                    while (!message.equals("bye")) {
                        try {
                            message = MESSAGES_BY_CLIENT.take();
                            out.writeUTF(message);
                        } catch (IOException | InterruptedException ignored) {
                        }
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    in.close();
                    out.close();
                    socket.close();
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println("Error here " + e.getMessage());
                }
            });
            userInput.start();

            Thread readMessagesToClient = new Thread(() -> {
                String message;
                while (true) {
                    try {
                        message = MESSAGES.take();
                        addMessageToChat(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            readMessagesToClient.start();

            ReadMessagesFromServer server = new ReadMessagesFromServer(socket);
            new Thread(server).start();
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void addMessageToChat(String message) {
            Platform.runLater(() -> {
                Text text = new Text("\n" + message);

                if (message.contains(username + ": ") && (!username.equals(""))) {
                    text.setStyle("-fx-fill:#004AFF;-fx-font-weight:bold");
                }
                CHAT.getChildren().add(text);
            });
        }

        private class ReadMessagesFromServer implements Runnable {
            DataInputStream in = null;
            DataOutputStream out = null;
            Socket socket;

            ReadMessagesFromServer(Socket socket) {
                this.socket = socket;
            }

            public void run() {
                try {
                    in = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));
                    out = new DataOutputStream(socket.getOutputStream());

                    while (true) {
                        try {
                            String line = in.readUTF();
                            MESSAGES.put(line);
                        } catch (IOException | InterruptedException ignored) {
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        public void setUpButtonSend() {
            BUTTON_SEND.setOnAction(e -> {
                String message = CHAT_INPUT.getText();
                MESSAGES_BY_CLIENT.add(message);
                CHAT_INPUT.setText("");
            });
        }
    }
}
