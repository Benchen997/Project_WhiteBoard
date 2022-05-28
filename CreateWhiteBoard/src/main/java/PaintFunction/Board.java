package PaintFunction;

import WindowUI.BoardState;
import WindowUI.MyBorder;
import WindowUI.ServerWindow;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Board extends JPanel implements MyBorder {
    public PaintListener paintListener;
    private String commandName = "default";
    public JPopupMenu popupMenu = new JPopupMenu();
    public ServerWindow serverWindow;
    public Graphics2D pen;
    private float penSize;
    private Color penColor;
    private String userText;
    public JTextField textField = new JTextField(20);
    public int popX;
    public int popY;

    // constructor
    public Board(ServerWindow serverWindow) {
        this.serverWindow = serverWindow;
        creatPaneBorder(this, "Draw Board");
        this.setBackground(Color.WHITE);
        paintListener = new PaintListener(this,serverWindow);
        this.addMouseListener(paintListener);
        this.addMouseMotionListener(paintListener);
        this.setPopupMenu();
        this.add(popupMenu);
        this.setPenColor(Color.black);
        this.setPenSize(1);

    }

    private void setPopupMenu() {
        JButton confirm = new JButton("Confirm");
        confirm.setActionCommand("confirm");
        confirm.addActionListener(paintListener);
        popupMenu.add(textField);
        popupMenu.add(confirm);

    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setPenSize(float penSize) {
        this.penSize = penSize;
    }

    public void setPenColor(Color penColor) {
        this.penColor = penColor;
    }

    public String getCommandName() {
        return commandName;
    }

    public float getPenSize() {
        return penSize;
    }

    public Color getPenColor() {
        return penColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        pen = (Graphics2D) g;
        // for each shape in the path drawn by mouse

        for (Shape shape: paintListener.getPath()) {
            String type = shape.getType();
            pen.setStroke(new BasicStroke(shape.getWidth()));
            pen.setColor(shape.getColor());

            if (type.equals("text box")) {
                pen.setFont(new Font("Droid Sans Mono",Font.PLAIN,(int) shape.getWidth()));
                pen.drawString(shape.getContent(),shape.getStartX(),shape.getStartY());
                serverWindow.state = BoardState.HAS_PAINT;
            }

            int x = Math.min(shape.getStartX(),shape.getEndX());
            int y = Math.min(shape.getStartY(),shape.getEndY());
            int w = Math.abs(shape.getEndX() - shape.getStartX());
            int h = Math.abs(shape.getEndY() - shape.getStartY());
            switch (type) {
                case "pen" -> {
                    // set current iteration point
                    ArrayList<Point> points = shape.getPointSet();
                    // two point to form a line, so must greater or equal to 2
                    if (points.size() >= 2) {
                        serverWindow.state = BoardState.HAS_PAINT;
                        Point start = points.get(0);
                        for (int j = 1; j < points.size(); j++) {
                            Point end = points.get(j);
                            pen.drawLine(start.x, start.y, end.x, end.y);
                            start = end;
                        }
                    }
                }
                case "rectangle" -> {
                    pen.drawRect(x, y, w, h);
                    serverWindow.state = BoardState.HAS_PAINT;
                }
                case "circle" -> {
                    pen.drawOval(x, y, w, h);
                    serverWindow.state = BoardState.HAS_PAINT;
                }
                case "line" -> {
                    pen.drawLine(shape.getStartX(), shape.getStartY(), shape.getEndX(), shape.getEndY());
                    serverWindow.state = BoardState.HAS_PAINT;
                }
                case "triangle" -> {
                    pen.drawLine(shape.getStartX(), shape.getStartY(), shape.getStartX(), shape.getEndY());
                    pen.drawLine(shape.getStartX(), shape.getEndY(), shape.getEndX(), shape.getEndY());
                    pen.drawLine(shape.getStartX(), shape.getStartY(), shape.getEndX(), shape.getEndY());
                    serverWindow.state = BoardState.HAS_PAINT;
                }
            }

        }
    }

    @Override
    public void creatPaneBorder(JComponent component, String title) {
        component.setBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(MyBorder.borderColor,MyBorder.thickness),
                        title,
                        TitledBorder.CENTER,TitledBorder.TOP,
                        new Font("Droid Sans Mono",Font.PLAIN,18),
                        MyBorder.fontColor));
    }
}
