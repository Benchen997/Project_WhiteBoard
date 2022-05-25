package WebFunction;

import Users.User;
import Users.UserGroup;
import WindowUI.MainWindow;
import org.json.simple.JSONArray;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Server {
    public ArrayList<User> clientSet = new ArrayList<>();
    public int clientCount = 0;
    private int autoUid = 0;
    //public Socket socket;


    public Server() {
        System.out.println("server is started");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.init();


    }
    public void init() {
        ServerSocket serverSocket = null;
        InputStream inputStream;
        DataInputStream dataInputStream;
        try {
            // 1. create socket
            serverSocket = new ServerSocket(8888);
            // 2.waiting for response

            while(true) {
                Socket socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
                String username = dataInputStream.readUTF();
                System.out.println(username);
                clientCount += 1;
                User user = new User(username,UserGroup.USER, socket,autoUid);
                autoUid += 1;
                clientSet.add(user);

                Thread serverThread = new ServerThread(this,user);
                serverThread.setName("Client " + clientCount);
                System.out.println("currently, we have " + clientCount + " people access our server");

                serverThread.start();



            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(serverSocket).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
