package com.example.networkchatserver1;

import java.io.IOException;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class Client extends Application {

    private BufferedReader in;
    private PrintWriter out;
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        chatArea = new TextArea();
        chatArea.setEditable(false);

        VBox inputBox = new VBox();
        inputBox.setSpacing(10);
        inputBox.setPadding(new Insets(10));

        messageField = new TextField();
        messageField.setPromptText("Введите свое сообщение");

        sendButton = new Button("Отправить");
        sendButton.setDefaultButton(true);
        sendButton.setDisable(true);

        inputBox.getChildren().addAll(messageField, sendButton);

        root.setCenter(chatArea);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Клиент чата");
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            close();
        });
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        TextInputDialog dialog = new TextInputDialog("localhost");
        dialog.setTitle("Подключение к серверу");
        dialog.setHeaderText("Введите IP-адрес или имя хоста сервера");
        dialog.setContentText("Сервер:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(server -> {
            try {
                Socket socket = new Socket(server, 5000);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                Thread messageReceiver = new Thread(this::receiveMessages);
                messageReceiver.start();

                messageField.setOnAction(event -> sendMessage());
                sendButton.setOnAction(event -> sendMessage());

                messageField.setDisable(false);
                sendButton.setDisable(false);
                messageField.requestFocus();
            } catch (IOException e) {
                showError("Ошибка подключения к серверу: " + e.getMessage());
            }
        });
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.clear();
        }
    }

    private void receiveMessages() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String finalInputLine = inputLine;
                Platform.runLater(() -> chatArea.appendText(finalInputLine + "\n"));
            }
            System.out.println("Сервер отключен");
            Platform.runLater(() -> {
                chatArea.appendText("Сервер отключен\n");
                messageField.setDisable(true);
                sendButton.setDisable(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (out != null) {
                out.println("Досвидания");
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (out != null) {
                out.close();
            }
            primaryStage.close();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}