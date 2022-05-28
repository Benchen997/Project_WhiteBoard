package WebFunction;

import Users.User;
import WindowUI.ServerWindow;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
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
    private final ServerCommander inputArgs = new ServerCommander();
    private static int port;
    public ArrayList<User> clientSet = new ArrayList<>();
    public int clientCount = 0;
    private int autoUid = 0;
    public boolean running = true;
    public ServerWindow serverWindow;


    // the constructor
    public Server() {

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.handleArgs(args);
        server.init();


    }
    /**
     * Parse command information and assign filename and port number to start the server.
     * Any error happened will terminate the program early.
     * @param args command line argument.
     */
    private void handleArgs(String[] args) {
        JCommander jCommander = new JCommander(inputArgs);
        jCommander.setProgramName("Concurrent whiteboard");

        // 1. parse first
        try {
            jCommander.parse(args);
            port = inputArgs.port;
        }
        catch (ParameterException e) {
            System.out.println(e.getMessage());
            showUsage(jCommander);
        }
        // 2. if user asking for help
        if (inputArgs.isHelp()) {
            showUsage(jCommander);
        }
    }

    private void showUsage(JCommander jCommander) {
        jCommander.usage();
        System.exit(0);
    }

    /**
     * Initiate server socket, constantly listen for user connection
     * acting as middle server to accept and transfer all the change made on the GUI
     * every new user apply for connection will be able to see all the content on GUI
     * every new user will be added to peer list
     * if administrator refuse the connection, the socket is closed
     */
    public void init() {
        System.out.println("server is started");
        serverWindow = new ServerWindow(this);
        ServerSocket serverSocket = null;
        InputStream inputStream;
        DataInputStream dataInputStream;
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        try {
            // 1. create socket
            serverSocket = new ServerSocket(port);
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
                    switch (serverWindow.state) {
                        case NO_PAINT -> dataOutputStream.writeUTF("yes");
                        case WHITE_PAINT -> dataOutputStream.writeUTF("yes, and we have board");
                        case HAS_PAINT -> {
                            dataOutputStream.writeUTF("yes, and we have a painted board");
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(serverWindow.board.paintListener.getPath());
                            objectOutputStream.flush();
                        }
                    }
                    System.out.println(username);
                    User user = new User(username, socket,autoUid);
                    autoUid += 1;
                    clientCount += 1;
                    clientSet.add(user);

                    // add new user to list
                    JMenuItem item = new JMenuItem(username);
                    item.setActionCommand("kick" + username);
                    item.setToolTipText("right click to kick this user");
                    item.addActionListener(serverWindow.serverActionListener);
                    serverWindow.menuBar.peers.add(item);

                    // to inform all the existed users that we have a new user.
                    packing("update users", username);

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

    /**
     * packing the data type and data content into json object, and then iterate
     * to every existed user by using the user's output stream.
     * @param request what type of data
     * @param data what is the data
     */
    public void packing(String request, String data) {

        JSONObject toAllClient = new JSONObject();

        toAllClient.put("command",request);
        toAllClient.put("data",data);
        for (User eachUser:clientSet) {
            try {
                eachUser.dataOutputStream.writeUTF(toAllClient.toJSONString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Send users with current paint in form of serialized object.
     */
    public void packExistedPaint() {
        for (User eachUser:clientSet) {
            try {
                eachUser.setObjectOutputStream();
                eachUser.objectOutputStream.writeObject(serverWindow.board.paintListener.getPath());
                System.out.println("can i send obj?");
                eachUser.setDataOutputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param request what type of the data
     * @param data what is the dta
     * @param username the particular user that server wish to contact
     */
    public void packToSingleUser(String request, String data, String username) {
        JSONObject toClient = new JSONObject();

        toClient.put("command",request);
        toClient.put("data",data);

        for (User targetUser:clientSet) {
            if (targetUser.getUsername().equals(username)) {
                try {
                       targetUser.dataOutputStream.writeUTF(toClient.toJSONString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

}
