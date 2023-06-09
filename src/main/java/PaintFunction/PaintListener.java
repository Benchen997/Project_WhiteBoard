package PaintFunction;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class PaintListener implements MouseListener, MouseMotionListener, ActionListener {
    private int currentX, currentY, oldX, oldY;
    private Boolean pressed = false;
    private ArrayList<Shape> path = new ArrayList<>();
    private Color color;
    private Graphics g;
    private Shape[] shapeArray;
    public int index = 0;

    private Board board;


    public PaintListener(Board board) {
        this.board = board;
    }

    // initial pen
    public void setGr(Graphics g) {
        this.g = g;

    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /* mouse click finish when pressed and then released
        * thus press + release = click
        * in addition, mouse click happen when you press button and release button at same
        * location */
        System.out.println("mouse clicked");
        if (e.getButton() == MouseEvent.BUTTON3) {
            System.out.println("menu poped");
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        /* immediately detected when mouse pressed*/
        if (!board.getCommandName().equals("default")) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                pressed = true;
                //board.pen.setStroke(new BasicStroke(board.getPenSize()));
                Curve curve = new Curve();
                curve.getPointSet().add(e.getPoint());
                path.add(curve);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        /* when the mouse move into the board area
        *  change its icon to pen with user's name tag on */
        String command = board.getCommandName();

        if (command.equals("pen")) {
            board.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        else if (command.equals("rectangle")
                ||command.equals("triangle")
                ||command.equals("circle")
                ||command.equals("line")) {
            board.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
        else {
            board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    @Override
    public void mouseExited(MouseEvent e) {
        /* when the mouse move out to the board area, back
        *  to normal icon */
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressed) {
            Shape lastShape = path.get(path.size() - 1);
            lastShape.getPointSet().add(e.getPoint());
            board.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public ArrayList<Shape> getPath() {
        return path;
    }
}
