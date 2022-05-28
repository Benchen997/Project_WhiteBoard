package WindowUI;

import javax.swing.event.MenuListener;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class MyActionListener extends MouseAdapter implements ActionListener, KeyListener {
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
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            clientWindow.menuBar.peers.removeAll();
            clientWindow.client.packing("client command","update peers");
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
