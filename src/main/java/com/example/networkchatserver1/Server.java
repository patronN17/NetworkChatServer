package com.example.networkchatserver1;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server(String serverName, int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер " + serverName + " запущен по порту " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключен новый клиент");
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) {
        String serverName = "MyServer";
        int port = 1234;
        new Server(serverName, port);
    }
}
