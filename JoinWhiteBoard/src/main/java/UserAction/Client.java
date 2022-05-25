package UserAction;


import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Client {
    String username;
    Socket socket;
    InputStream inputStream;
    DataInputStream dataInputStream;
    Boolean connected = true;
    OutputStream outputStream;
    DataOutputStream dataOutputStream;

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
        Scanner scanner = new Scanner(System.in);
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
            Receive receive = new Receive();
            receive.start();
            System.out.println("please type your word");

            while (connected) {

                String input = scanner.nextLine();

                if (input.equals("exit")) {
                    packing("message","request-to-exit");
                    connected = false;
                }
                else {
                    packing("message",input);
                }

            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Server not in response");
        }
        try {
            socket.close();
            dataInputStream.close();
            dataOutputStream.close();
            //System.out.println("if i can really close them");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This class is intended to constantly listen input from server, despite the client
     * weather or not sending a message to server.
     */
    class Receive extends Thread {
        @Override
        public void run() {
            while (connected) {
                try {
                    if (dataInputStream.available() > 0 ) {
                        String response = dataInputStream.readUTF();
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void packing(String request, Object data) throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command",request);
        jsonObject.put("data",data);

        dataOutputStream.writeUTF(jsonObject.toJSONString());
        dataOutputStream.flush();


    }

}
