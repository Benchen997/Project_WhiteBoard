package WebFunction;

import Users.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class ServerThread extends Thread{
    private final Server threadOwner;
    private User user;
    private ArrayList<DataOutputStream> outputStreams = new ArrayList<>();


    public ServerThread(Server threadOwner, User user) {
        System.out.println("==========================");
        System.out.println("A new thread is initiated");
        this.threadOwner = threadOwner;
        this.user = user;
        for (User eachUser: threadOwner.clientSet) {
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

            while (true) {
                if (user.dataInputStream.available() > 0) {
                    JSONObject command = (JSONObject) parser.parse(user.dataInputStream.readUTF());

                    if (command.get("data").equals("request-to-exit") && command.get("command").equals("message")) {
                        user.dataOutputStream.writeUTF("Happy to see you again");
                        break;
                    }
                    String clientMessage = (String) command.get("data");

                    System.out.println(clientMessage);
                    threadOwner.serverWindow.chatField.chatHistory.append(clientMessage);

                    packing("message",clientMessage);
                }

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < threadOwner.clientSet.size(); i++) {

                if (threadOwner.clientSet.get(i).getUid() == user.getUid()) {
                    threadOwner.clientSet.remove(i);
                }
            }
            threadOwner.clientCount -= 1;
            System.out.println(this.getName() + " has left");
        }
        //
    }
    public void packing(String request, Object data) {

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
}
