package WebFunction;

import PaintFunction.Shape;
import Users.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class ServerThread extends Thread{
    private final Server threadOwner;
    private final User user;
    private boolean connected = true;

    public ServerThread(Server threadOwner, User user) {
        System.out.println("==========================");
        System.out.println("A new thread is initiated");
        this.threadOwner = threadOwner;
        this.user = user;
        for (User eachUser: threadOwner.clientSet) {
            ArrayList<DataOutputStream> outputStreams = new ArrayList<>();
            outputStreams.add(eachUser.dataOutputStream);
        }

    }

    @Override
    public void run() {

        try {
            // 1. input stream receive message from client

            // 2. output stream send message to client

            // 3.
            JSONParser parser = new JSONParser();

            // persistent connection with client.

            while (connected) {
                if (user.dataInputStream.available() > 0) {
                    JSONObject command = (JSONObject) parser.parse(user.dataInputStream.readUTF());
                    unpacking(command);
                }

            }

        } catch (IOException | ParseException e) {
            System.out.println("client rest socket");
        } finally {
            for (int i = 0; i < threadOwner.clientSet.size(); i++) {

                if (threadOwner.serverWindow.menuBar.peers.getItem(i + 1).getText().equals(user.getUsername())) {
                    threadOwner.serverWindow.menuBar.peers.remove(i + 1);
                }
                if (threadOwner.clientSet.get(i).getUid() == user.getUid()) {
                    threadOwner.clientSet.remove(i);
                }
            }
            threadOwner.clientCount -= 1;
            System.out.println(this.getName() + " has left");
        }
        //
    }
    private void packing(String request, Object data) {

        JSONObject toAllClient = new JSONObject();

        toAllClient.put("command",request);
        toAllClient.put("data",data);

        for (User eachUser: threadOwner.clientSet) {
            if (eachUser.getUid() == user.getUid()) {
                continue;
            }
            try {
                eachUser.dataOutputStream.writeUTF(toAllClient.toJSONString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
    private void packing(JSONObject clientCommand) {
        for (User eachUser: threadOwner.clientSet) {
            if (eachUser.getUid() == user.getUid()) {
                continue;
            }
            try {
                eachUser.dataOutputStream.writeUTF(clientCommand.toJSONString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
    public void unpacking(JSONObject clientCommand) throws IOException {
        String command = (String) clientCommand.get("command");
        String data = (String) clientCommand.get("data");
        switch (command) {
            case "client command":
                switch (data) {
                    case "request-to-exit" -> {
                        JOptionPane.showMessageDialog(threadOwner.serverWindow,
                                user.getUsername() + " has left");
                        user.dataOutputStream.writeUTF("Happy to see you again");
                        connected = false;
                    }
                    case "update peers" -> updatePeers();
                }
                break;
            case "drawing":
                Thread t1 = new Thread(()->{
                    unpackShape(clientCommand);
                    packing(clientCommand);
                });
                t1.setName("try to unpack data and give a shape");
                t1.start();
                break;
            case "points":
                Point point = unpackPoint(clientCommand);
                int index = threadOwner.serverWindow.board.paintListener.getPath().size() - 1;
                PaintFunction.Shape lastShape = threadOwner.serverWindow.board.paintListener.getPath().get(index);
                lastShape.getPointSet().add(point);
                threadOwner.serverWindow.board.repaint();
                packing(clientCommand);
                break;
            case "message":
                threadOwner.serverWindow.chatField.chatHistory.append(data);
                packing("message",data);
                //packing(clientCommand);
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
        return new Point(x,y);
    }

    /**
     * @param clientCommand The json object consist of "command" and "data".
     *                      parse the json string back to json, read in relative
     *                      features and then form a shape object.
     */
    public void unpackShape(JSONObject clientCommand) {
        String data = (String) clientCommand.get("data");
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
                    threadOwner.serverWindow.board.paintListener.getPath().add(shape);
                    threadOwner.serverWindow.board.repaint();
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void unpackPenDrawing(JSONObject shapeData) {
        PaintFunction.Shape shape = new Shape("pen");
        //-----width of pen
        double width = (double) shapeData.get("width");
        shape.setWidth((int) width);
        // color of the shape
        long c = (long) shapeData.get("color");
        int rgb = getInt(c);
        shape.setColor(new Color(rgb));
        threadOwner.serverWindow.board.paintListener.getPath().add(shape);

    }
    public void unpackTextBox(JSONObject shapeData) {
        Shape shape = new Shape("text box");
        shape.setContent((String) shapeData.get("content"));
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
        threadOwner.serverWindow.board.paintListener.getPath().add(shape);
        threadOwner.serverWindow.board.repaint();
    }
    public void updatePeers() {

        for (int i = 0; i <= threadOwner.clientSet.size(); i++) {
            String name = threadOwner.serverWindow.menuBar.peers.getItem(i).getText();
            JSONObject toClient = new JSONObject();
            toClient.put("command","update users");
            toClient.put("data",name);
            try {
                user.dataOutputStream.writeUTF(toClient.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public int getInt(long n) {
        Long l = n;
        return l.intValue();

    }
}
