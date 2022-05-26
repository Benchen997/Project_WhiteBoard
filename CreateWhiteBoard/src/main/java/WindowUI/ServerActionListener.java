package WindowUI;

import Users.User;
import WindowUI.ServerWindow;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class ServerActionListener extends WindowAdapter implements ActionListener, KeyListener {
    // Attribute
    public ServerWindow serverWindow;

    // Constructor
    public ServerActionListener(ServerWindow serverWindow) {
        this.serverWindow = serverWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        serverWindow.board.setCommandName(actionCommand);
        switch (actionCommand) {
            case "create":
                Thread t = new Thread(()-> {
                    serverWindow.server.packing("message","new board");
                });
                t.start();
                break;
            case "send message":
                sendMessage();
                break;
            default:
                break;


        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        //super.windowClosing(e);
        int result = JOptionPane.showConfirmDialog(null,
                "Are you sure you want leave?");
        if (result == JOptionPane.YES_OPTION) {
            serverWindow.server.packing("message","actively-stop-server");
            serverWindow.server.running = false;
            System.exit(0);
        }
    }
    private void sendMessage() {
        String adInput = serverWindow.chatField.input.getText();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String chatLog = "\n\r"+ "Administrator" + " " +formatter.format(date)+"\n\r"+ adInput;
        // send to server and update history window.
        Thread t = new Thread(() -> {
            serverWindow.server.packing("message",chatLog);
            serverWindow.chatField.chatHistory.append(chatLog);
            serverWindow.chatField.input.setText(null);

        });
        t.start();
    }

}
