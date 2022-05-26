package WebFunction;

import PaintFunction.Shape;
import Users.User;
import Users.UserGroup;
import WindowUI.ServerWindow;
import org.json.simple.JSONObject;

import javax.swing.*;
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
    public boolean running = true;
    public ServerWindow serverWindow;
    //public Socket socket;


    public Server() {
        System.out.println("server is started");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.init();



    }
    public void init() {
        serverWindow = new ServerWindow(this);
        ServerSocket serverSocket = null;
        InputStream inputStream;
        DataInputStream dataInputStream;
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        try {
            // 1. create socket
            serverSocket = new ServerSocket(8888);
            // 2.waiting for response

            while(running) {
                Socket socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                dataInputStream = new DataInputStream(inputStream);
                dataOutputStream = new DataOutputStream(outputStream);
                String username = dataInputStream.readUTF();
                int agree = JOptionPane.showConfirmDialog(serverWindow,
                        "User " + username + " want to join your board, do you agree");

                if (agree == JOptionPane.YES_OPTION) {
                    dataOutputStream.writeUTF("yes");
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
                else {
                    dataOutputStream.writeUTF("no");
                    socket.close();
                }
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
    public void packing(String request, String data) {

        JSONObject toAllClient = new JSONObject();

        toAllClient.put("command",request);
        toAllClient.put("data",data);
      /*  if (request.equals("message")) {
            toAllClient.put("data",data);
        }
        else if (request.equals("drawing")) {
            toAllClient.put("data",((Shape) data).shapeToJSON());
        }*/


        for (User eachUser:clientSet) {
            try {
                eachUser.dataOutputStream.writeUTF(toAllClient.toJSONString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


}
