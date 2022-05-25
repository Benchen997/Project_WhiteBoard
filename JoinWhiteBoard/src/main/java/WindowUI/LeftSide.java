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
    JButton[] buttons = new JButton[5];
    //public PaintListener paintListener;

    public LeftSide(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.setLayout(new BorderLayout());
        this.setLetToolBar();
        this.add(letToolBar, BorderLayout.CENTER);
        this.setLeftBottom();
        this.add(leftBottom, BorderLayout.SOUTH);
        //this.paintListener = new PaintListener(mainWindow);

    }

    private void setLetToolBar() {
        letToolBar.setOrientation(SwingConstants.VERTICAL);
        letToolBar.setLayout(new GridLayout(4,2));
        letToolBar.setFloatable(true);
        creatPaneBorder(letToolBar, "Tools");

        // normal pen icon
        JButton pen = setButton("pen.png");
        pen.addActionListener(toolButtonListener);
        pen.addActionListener(e -> changeSelection(pen));
        pen.setActionCommand("pen");

        // rectangle button
        JButton rectangle = setButton("rectangle.png");
        rectangle.addActionListener(toolButtonListener);
        rectangle.addActionListener(e -> changeSelection(rectangle));
        rectangle.setActionCommand("rectangle");

        // line button
        JButton line = setButton("Line.png");
        line.addActionListener(toolButtonListener);
        line.addActionListener(e -> changeSelection(line));
        line.setActionCommand("line");

        // triangle button
        JButton triangle = setButton("triangle.png");
        triangle.addActionListener(toolButtonListener);
        triangle.addActionListener(e -> changeSelection(triangle));
        triangle.setActionCommand("triangle");

        // circle button
        JButton circle = setButton("circle.png");
        circle.addActionListener(toolButtonListener);
        circle.addActionListener(e -> changeSelection(circle));
        circle.setActionCommand("circle");

        // color button
        JButton colorPicker = setButton("color-picker.png");
        colorPicker.addActionListener(e -> changeColor());
        colorPicker.setActionCommand("color-picker");

        buttons[0] = pen;
        buttons[1] = line;
        buttons[2] = rectangle;
        buttons[3] = triangle;
        buttons[4] = circle;

        letToolBar.add(pen);
        letToolBar.add(line);
        letToolBar.add(rectangle);
        letToolBar.add(triangle);
        letToolBar.add(circle);
        letToolBar.add(colorPicker);
    }
    private void setLeftBottom() {
        JSlider sizeSlider = new JSlider(1,30,1);
        sizeSlider.setOrientation(SwingConstants.VERTICAL);
        sizeSlider.setToolTipText("Resize the pen");
        sizeSlider.addChangeListener(e -> changeSize(sizeSlider));
        leftBottom.add(sizeSlider,BorderLayout.CENTER);
    }
    private JButton setButton(String filename) {
        URL url = this.getClass().getResource("/img/" + filename);
        JButton button = new JButton(new ImageIcon(Objects.requireNonNull(url)));
        button.addActionListener(e -> mainWindow.chatField.chatHistory.append(filename + "\n"));
        return button;
    }
    private void changeSelection(JButton thisButton) {
        for (JButton button:buttons) {
            if (button.getBackground() == Color.GRAY) {
                button.setBackground(null);
            }
        }
        thisButton.setBackground(Color.GRAY);
    }
    public void changeColor() {
        Color userSelectedColor = JColorChooser.showDialog(mainWindow,"please choose your color",
                mainWindow.board.getPenColor());
        mainWindow.board.setPenColor(userSelectedColor);
    }
    private void changeSize(JSlider sizeSlider) {
        mainWindow.board.setPenSize(sizeSlider.getValue());
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
