package UserAction;


import PaintFunction.Shape;
import Users.UserGroup;
import WindowUI.BoardState;
import WindowUI.ClientWindow;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Client {
    private final ClientCommander inputArgs = new ClientCommander();
    private int port;
    private String hostAddress;
    public String username;
    public Socket socket;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    public Boolean connected = true;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private ClientWindow clientWindow;

    // Constructor
    public Client() {

    }

    /**
     * @param args command line argument to start this program
     */
    public static void main(String[] args) {
        // 1. client object
        Client client = new Client();

        // 2. deal with command line input
        client.handleArgs(args);

        // 3. Ask users for username
        client.username = JOptionPane.showInputDialog("Please enter your username");
        if ((client.username == null) || (client.username.equals(""))) {
            JOptionPane.showMessageDialog(null,"Invalid Username");
            System.exit(0);
        }

        client.connect();
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
            hostAddress = inputArgs.hostAddress;
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

    public void connect() {
        try {
            socket = new Socket(hostAddress,port);
            //socket.bind();
            // output to server
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);

            // input from server
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);

            // send username
            dataOutputStream.writeUTF(username);
            String join = dataInputStream.readUTF();

            if (join.equals("no")) {
                JOptionPane.showMessageDialog(null,
                        "Your request is refused by administrator");
                return;
            }
            JOptionPane.showMessageDialog(null,
                        "Your are in!");

            // creat window object
            clientWindow = new ClientWindow(UserGroup.USER, username,this);

            if (join.equals("yes, and we have board")) {
                clientWindow.menuBar.creatOnClick();
            }
            else if (join.equals("yes, and we have a painted board")) {
                clientWindow.menuBar.creatOnClick();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                ArrayList<Shape> path = (ArrayList<Shape>) objectInputStream.readObject();
                clientWindow.board.paintListener.setPath(path);
                clientWindow.board.repaint();
                clientWindow.state = BoardState.HAS_PAINT;
            }

            Receive receive = new Receive(clientWindow);
            receive.start();

            while (connected) {

                //constantly wait for user input

            }

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,"Server not in response");
        }
        finally {
            try {
                socket.close();
                dataInputStream.close();
                dataOutputStream.close();

            } catch (IOException  | NullPointerException e) {
                JOptionPane.showMessageDialog(null,"Couldn't reach port number");
            }
        }
    }

    /**
     * This class is intended to constantly listen input from server, despite the client
     * weather or not sending a message to server.
     */
    class Receive extends Thread {
        ClientWindow clientWindow;
        public Receive(ClientWindow clientWindow) {
            this.clientWindow = clientWindow;
        }

        @Override
        public void run() {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(dataInputStream);
            JSONParser parser = new JSONParser();
            while (connected) {
                try {
                    if (bufferedInputStream.available() > 0 ) {
                        JSONObject serverCommand = (JSONObject) parser.parse(dataInputStream.readUTF());
                        unpacking(serverCommand);
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }



    public void packing(String request, Object data) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("command",request);
        jsonObject.put("data",data);

        try {
            dataOutputStream.writeUTF(jsonObject.toJSONString());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void unpacking(JSONObject serverCommand)  {
        String command = (String) serverCommand.get("command");
        String data = (String) serverCommand.get("data");

        switch (command) {
            case "server command":
                switch (data) {
                    case "actively-stop-server" -> {
                        JOptionPane.showMessageDialog(clientWindow,
                                "The administrator has close the window");
                        System.exit(0);
                    }
                    case "new board" -> clientWindow.menuBar.creatOnClick();
                    case "open" -> {
                        clientWindow.menuBar.creatOnClick();
                        ObjectInputStream objectInputStream;
                        try {
                            objectInputStream = new ObjectInputStream(socket.getInputStream());
                            ArrayList<Shape> path = (ArrayList<Shape>) objectInputStream.readObject();
                            clientWindow.board.paintListener.setPath(path);
                            clientWindow.board.repaint();
                            // reset input stream with data stream
                            dataInputStream = new DataInputStream(socket.getInputStream());
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    case "You have been kicked" -> {
                        packing("client command", "request-to-exit");
                        JOptionPane.showMessageDialog(clientWindow,
                                "You have been kicked");
                        System.exit(0);
                    }
                }
                break;
            case "drawing":
                Thread t1 = new Thread(()->{
                    unpackShape(serverCommand);
                });
                t1.setName("try to unpack data and give a shape");
                t1.start();
                break;
            case "points":
                Point point = unpackPoint(serverCommand);
                int index = clientWindow.board.paintListener.getPath().size() - 1;
                Shape lastShape = clientWindow.board.paintListener.getPath().get(index);
                lastShape.getPointSet().add(point);
                clientWindow.board.repaint();
                break;
            case "message":
                clientWindow.chatField.chatHistory.append(data);
                break;
            case "update users":
                clientWindow.menuBar.peers.add(new JMenuItem(data));
                SwingUtilities.updateComponentTreeUI(clientWindow.menuBar.peers);
                break;

        }

    }
    public Point unpackPoint(JSONObject serverCommand) {
        String data = (String) serverCommand.get("data");
        JSONParser pointParser = new JSONParser();
        JSONObject jsonPoint = null;
        try {
            jsonPoint = (JSONObject) pointParser.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int x = getInt((long) jsonPoint.get("x"));
        int y = getInt((long) jsonPoint.get("y"));
        Point point = new Point(x,y);
        return point;
    }

    /**
     * @param serverCommand The json object consist of "command" and "data".
     *                      parse the json string back to json, read in relative
     *                      features and then form a shape object.
     */
    public void unpackShape(JSONObject serverCommand) {
        String data = (String) serverCommand.get("data");
        JSONParser shapeParser = new JSONParser();
        try {
            JSONObject shapeData = (JSONObject) shapeParser.parse(data);
            String type = (String) shapeData.get("type");
            switch (type) {
                case "pen" -> unpackPenDrawing(shapeData);
                case "text box" -> unpackTextBox(shapeData);
                default -> {
                    Shape shape = new Shape(type);
                    //-----width of pen
                    double width = (double) shapeData.get("width");
                    shape.setWidth((int) width);
                    // color of the shape
                    long c = (long) shapeData.get("color");
                    int rgb = getInt(c);
                    shape.setColor(new Color(rgb));
                    // coordination relative to container
                    shape.setStartX(getInt((long) shapeData.get("start X")));
                    shape.setStartY(getInt((long) shapeData.get("start Y")));
                    shape.setEndX(getInt((long) shapeData.get("end X")));
                    shape.setEndY(getInt((long) shapeData.get("end Y")));
                    clientWindow.board.paintListener.getPath().add(shape);
                    clientWindow.board.repaint();
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void unpackPenDrawing(JSONObject shapeData) {
        Shape shape = new Shape("pen");
        //-----width of pen
        double width = (double) shapeData.get("width");
        shape.setWidth((int) width);
        // color of the shape
        long c = (long) shapeData.get("color");
        int rgb = getInt(c);
        shape.setColor(new Color(rgb));
        clientWindow.board.paintListener.getPath().add(shape);


    }
    public void unpackTextBox(JSONObject shapeData) {
        Shape shape = new Shape("text box");
        String content = (String) shapeData.get("content");
        System.out.println(content);
        shape.setContent(content);
        //-----width of pen
        double width = (double) shapeData.get("width");
        shape.setWidth((int) width);
        // color of the shape
        long c = (long) shapeData.get("color");
        int rgb = getInt(c);
        shape.setColor(new Color(rgb));
        // coordination
        shape.setStartX(getInt((long) shapeData.get("start X")));
        shape.setStartY(getInt((long) shapeData.get("start Y")));
        clientWindow.board.paintListener.getPath().add(shape);
        clientWindow.board.repaint();
    }
    public int getInt(long n) {
        Long l = n;
        return l.intValue();

    }


}
