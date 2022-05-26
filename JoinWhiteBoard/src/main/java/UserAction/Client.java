package UserAction;


import PaintFunction.Shape;
import Users.UserGroup;
import WindowUI.ClientWindow;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Client {
    public String username;
    public Socket socket;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    public Boolean connected = true;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private ClientWindow clientWindow;
    public static void main(String[] args) {
        Client client = new Client();
        client.username = JOptionPane.showInputDialog("Please enter your username");
        if ((client.username == null) || (client.username.equals(""))) {
            JOptionPane.showMessageDialog(null,"Invalid Username");
            System.exit(0);
        }

        client.connect();
    }

    public void connect() {
        //Scanner scanner = new Scanner(System.in);
        try {
            socket = new Socket("localhost",8888);
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
            Receive receive = new Receive(clientWindow);
            receive.start();
            //System.out.println("please type your word");

            while (connected) {




            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Server not in response");
        }
        try {
            socket.close();
            dataInputStream.close();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
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
            JSONParser parser = new JSONParser();
            while (connected) {
                try {
                    if (dataInputStream.available() > 0 ) {
                        JSONObject serverCommand = (JSONObject) parser.parse(dataInputStream.readUTF());
                        unpacking(serverCommand);
                        /*String response = dataInputStream.readUTF();
                        clientWindow.chatField.chatHistory.append(response);*/
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
    public void unpacking(JSONObject serverCommand) {
        String command = (String) serverCommand.get("command");

        if (command.equals("message")) {
            String data = (String) serverCommand.get("data");
            switch (data) {
                case "actively-stop-server":
                    JOptionPane.showMessageDialog(clientWindow,
                            "The administrator has close the window");
                    System.exit(0);
                    break;
                case "new board":
                    clientWindow.menuBar.creatOnClick();
                    break;
                default:
                    clientWindow.chatField.chatHistory.append(data);
            }

        }
        else if (command.equals("drawing")) {
            Thread t1 = new Thread(()->{
               unpackShape(serverCommand);
            });
            t1.setName("try to unpack data and give a shape");
            t1.start();
        }

    }
    public void unpackShape(JSONObject serverCommand) {
        String data = (String) serverCommand.get("data");
        JSONParser shapeParser = new JSONParser();
        try {
            JSONObject shapeData = (JSONObject) shapeParser.parse(data);
            Shape shape = new Shape((String) shapeData.get("type"));
            //-----width of pen
            double width = (double) shapeData.get("width");
            System.out.println(width);
            shape.setWidth((int) width);
            // color of the shape
            long c = (long) shapeData.get("color");
            int rgb = getInt(c);
            shape.setColor(new Color(rgb));
            System.out.println(rgb);
            // coordination relative to container
            shape.setStartX(getInt((long) shapeData.get("start X")));
            shape.setStartY(getInt((long) shapeData.get("start Y")));
            shape.setEndX(getInt((long) shapeData.get("end X")));
            shape.setEndY(getInt((long) shapeData.get("end Y")));
            clientWindow.board.paintListener.getPath().add(shape);
            clientWindow.board.repaint();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public int getInt(long n) {
        Long l = n;
        int result = l.intValue();
        return result;

    }


}
