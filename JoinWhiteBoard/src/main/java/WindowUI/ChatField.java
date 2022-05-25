package WindowUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class ChatField extends JPanel implements MyBorder{
    public JTextArea input = new JTextArea(10,20);
    public JTextArea chatHistory = new JTextArea();
    public JScrollPane scrollPane = new JScrollPane(chatHistory);
    public JScrollPane inputPane = new JScrollPane(input);
    public JButton send = new JButton("send");

    public ChatField() {
        this.setLayout(new BorderLayout());
        setInput();
        setChatHistory();
        this.add(scrollPane, BorderLayout.NORTH);
        this.add(inputPane, BorderLayout.CENTER);
        setSend();

        this.add(send, BorderLayout.SOUTH);

    }
    private void setChatHistory() {
        chatHistory.setToolTipText("chat history");
        chatHistory.setEditable(false);
        creatPaneBorder(scrollPane,"Chat");
        scrollPane.setPreferredSize(new Dimension(100,750));
    }
    private void setInput(){
        input.setToolTipText("please enter your message");
        creatPaneBorder(inputPane, "Message");
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    chatHistory.append("sent\n");
                }
            }
        });

    }
    private void setSend() {
        send.setPreferredSize(new Dimension(input.getWidth()/2,30));
        send.addActionListener(e -> chatHistory.append("sent\n"));
    }

    @Override
    public void creatPaneBorder(JComponent component, String title) {
        component.setBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(borderColor, thickness),
                        title,
                        TitledBorder.CENTER,TitledBorder.TOP,
                        new Font("Droid Sans Mono",Font.PLAIN,18),
                        fontColor));
    }
}
