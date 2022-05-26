package WindowUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class MyActionListener implements ActionListener, KeyListener {
    ClientWindow clientWindow;
    public MyActionListener(ClientWindow clientWindow) {
        this.clientWindow = clientWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        clientWindow.board.setCommandName(command);
        System.out.println(command);
        switch (command) {
            case "send message":
                sendMessage();
                break;
            default:
                Thread t1= new Thread(() -> {

                    clientWindow.client.packing("message",command);

                });
                t1.start();
        }
    }
    private void sendMessage() {
        String userInput = clientWindow.chatField.input.getText();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String chatLog = "\n\r"+ clientWindow.client.username + " " +formatter.format(date)+"\n\r"+ userInput;
        // send to server and update history window.
        Thread t = new Thread(() -> {
            clientWindow.client.packing("message",chatLog);
            clientWindow.chatField.chatHistory.append(chatLog);
            clientWindow.chatField.input.setText(null);

        });
        t.start();
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
}
