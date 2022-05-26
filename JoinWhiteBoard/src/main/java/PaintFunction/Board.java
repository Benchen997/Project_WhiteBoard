package PaintFunction;

import WindowUI.BoardState;
import WindowUI.ClientWindow;
import WindowUI.MyBorder;

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
    public ClientWindow clientWindow;
    public Graphics2D pen;
    private float penSize;
    private Color penColor;

    // constructor
    public Board(ClientWindow clientWindow) {
        this.clientWindow = clientWindow;
        creatPaneBorder(this, "Draw Board");
        this.setBackground(Color.WHITE);
        paintListener = new PaintListener(this);
        this.addMouseListener(paintListener);
        this.addMouseMotionListener(paintListener);
        this.setPopupMenu();
        this.add(popupMenu);

    }

    private void setPopupMenu() {
        JMenuItem undo = new JMenuItem("undo");
        JMenuItem clear = new JMenuItem("clear");
        popupMenu.add(undo);
        popupMenu.add(clear);
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
            int x = Math.min(shape.getStartX(),shape.getEndX());
            int y = Math.min(shape.getStartY(),shape.getEndY());
            int w = Math.abs(shape.getEndX() - shape.getStartX());
            int h = Math.abs(shape.getEndY() - shape.getStartY());
            switch (type) {
                case "pen":
                    // set current iteration point
                    ArrayList<Point> points = shape.getPointSet();
                    // two point to form a line, so must greater or equal to 2
                    if (points.size() >= 2) {
                        clientWindow.state = BoardState.HAS_PAINT;
                        Point start = points.get(0);
                        for (int j = 1; j < points.size(); j++) {
                            Point end = points.get(j);
                            pen.drawLine(start.x,start.y,end.x,end.y);
                            start = end;
                        }
                    }
                    break;
                case "rectangle":
                    pen.drawRect(x,y,w,h);
                    clientWindow.state = BoardState.HAS_PAINT;
                    break;
                case "circle":
                    pen.drawOval(x,y,w,h);
                    clientWindow.state = BoardState.HAS_PAINT;
                    break;
                case "line":
                    pen.drawLine(shape.getStartX(),shape.getStartY(),shape.getEndX(),shape.getEndY());
                    clientWindow.state = BoardState.HAS_PAINT;
                    break;
                case "triangle":
                    pen.drawLine(shape.getStartX(),shape.getStartY(),shape.getStartX(),shape.getEndY());
                    pen.drawLine(shape.getStartX(),shape.getEndY(),shape.getEndX(),shape.getEndY());
                    pen.drawLine(shape.getStartX(),shape.getStartY(),shape.getEndX(),shape.getEndY());
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
