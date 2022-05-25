package WindowUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Objects;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class LeftSide extends JPanel implements MyBorder{
    public MainWindow mainWindow;
    public JToolBar letToolBar = new JToolBar();
    public JPanel leftBottom = new JPanel();
    public ToolButtonListener toolButtonListener = new ToolButtonListener();

    public LeftSide(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.setLayout(new BorderLayout());
        this.setLetToolBar();
        this.add(letToolBar, BorderLayout.CENTER);
        this.add(leftBottom, BorderLayout.SOUTH);

    }

    private void setLetToolBar() {
        letToolBar.setOrientation(SwingConstants.VERTICAL);
        letToolBar.setLayout(new GridLayout(4,2));
        letToolBar.setFloatable(true);
        creatPaneBorder(letToolBar, "Tools");

        // normal pen icon
        JButton pen = setButton("pen.png");
        pen.addActionListener(toolButtonListener);
        //pen.addActionListener(e -> pen.setBorder(BorderFactory.createLoweredBevelBorder()));
        pen.setActionCommand("pen");

        // rectangle button
        JButton rectangle = setButton("rectangle.png");
        rectangle.addActionListener(toolButtonListener);

        rectangle.setActionCommand("rectangle");
        // line button
        JButton line = setButton("Line.png");
        line.addActionListener(toolButtonListener);
        line.setActionCommand("line");

        // triangle button
        JButton triangle = setButton("triangle.png");
        triangle.addActionListener(toolButtonListener);
        triangle.setActionCommand("triangle");

        // circle button
        JButton circle = setButton("circle.png");
        circle.addActionListener(toolButtonListener);
        circle.setActionCommand("circle");

        letToolBar.add(pen);
        letToolBar.add(line);
        letToolBar.add(rectangle);
        letToolBar.add(triangle);
        letToolBar.add(circle);
    }
    private JButton setButton(String filename) {
        URL url = this.getClass().getResource("/img/" + filename);
        JButton button = new JButton(new ImageIcon(Objects.requireNonNull(url)));
        button.addActionListener(e -> mainWindow.chatField.chatHistory.append(filename + "\n"));
        return button;
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
    private class ToolButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            mainWindow.board.setCommandName(command);

        }
    }
}
