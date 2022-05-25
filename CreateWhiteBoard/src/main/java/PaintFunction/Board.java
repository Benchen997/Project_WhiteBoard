package PaintFunction;

import WindowUI.BoardState;
import WindowUI.MainWindow;
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
    public MainWindow mainWindow;
    public Graphics2D pen;
    private float penSize;
    private Color penColor;

    // constructor
    public Board(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        creatPaneBorder(this, "Draw Board");
        this.setBackground(Color.WHITE);
        paintListener = new PaintListener(this);
        this.addMouseListener(paintListener);
        this.addMouseMotionListener(paintListener);
        this.setPopupMenu();
        this.add(popupMenu);
        //Graphics pen = this.getGraphics();
        //paintListener.setGr(pen);
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
            // set current iteration point
            pen.setStroke(new BasicStroke(shape.getWidth()));
            pen.setColor(shape.getColor());
            ArrayList<Point> points = shape.getPointSet();
            // two point to form a line, so must greater or equal to 2
            if (points.size() >= 2) {
                mainWindow.state = BoardState.HAS_PAINT;

                Point start = points.get(0);
                for (int j = 1; j < points.size(); j++) {
                    Point end = points.get(j);
                    //pen.setStroke(new BasicStroke(penSize));
                    pen.drawLine(start.x,start.y,end.x,end.y);
                    start = end;
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
