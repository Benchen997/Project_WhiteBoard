package PaintFunction;

import WindowUI.ServerWindow;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class PaintListener implements MouseListener, MouseMotionListener, ActionListener {
    private Boolean pressed = false;
    private ArrayList<Shape> path = new ArrayList<>();
    private final ServerWindow serverWindow;
    private final Board board;


    public PaintListener(Board board,ServerWindow serverWindow) {
        this.serverWindow = serverWindow;
        this.board = board;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("confirm")) {
            board.setUserText(board.textField.getText());
            board.textField.setText(null);
            board.popupMenu.setVisible(false);
            System.out.println(board.getUserText());
            Shape shape = new Shape("text box");
            shape.setContent(board.getUserText());
            shape.setColor(board.getPenColor());
            shape.setWidth(board.getPenSize());
            shape.setStartX(board.popX);
            shape.setStartY(board.popY);
            path.add(shape);
            board.repaint();
            Thread t = new Thread(()-> packingTextData(shape));
            t.start();

        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /* mouse click finish when pressed and then released
         * thus press + release = click
         * in addition, mouse click happen when you press button and release button at same
         * location */
        if (e.getButton() == MouseEvent.BUTTON1 && board.getCommandName().equals("text box")) {
            System.out.println("mouse clicked");
            board.popupMenu.show(e.getComponent(),e.getX(),e.getY());
            board.popX = e.getX();
            board.popY = e.getY();
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        String command = board.getCommandName();
        if (e.getButton() == MouseEvent.BUTTON1 &&
                ! command.equals("default")
                && ! command.equals("text box")) {
            pressed = true;
            Shape shape = new Shape(command);
            shape.setColor(board.getPenColor());
            shape.setWidth(board.getPenSize());
            shape.setStartX(e.getX());
            shape.setStartY(e.getY());
            shape.getPointSet().add(e.getPoint());
            path.add(shape);
            if (command.equals("pen")) {
                packingPenData(shape);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        String command = board.getCommandName();
        if (!command.equals("default") && ! command.equals("text box")) {
            Shape lastShape = path.get(path.size() - 1);
            lastShape.setEndX(e.getX());
            lastShape.setEndY(e.getY());
            board.repaint();
            if (!command.equals("pen")) {
                packingDrawData(lastShape);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        /* when the mouse move into the board area
         *  change its icon to pen with user's name tag on */
        String command = board.getCommandName();

        switch (command) {
            case "pen" -> board.setCursor(new Cursor(Cursor.HAND_CURSOR));
            case "rectangle", "triangle", "circle", "line" -> board.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            case "text box" -> board.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            default -> board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    @Override
    public void mouseExited(MouseEvent e) {
        /* when the mouse move out to the board area, back
         *  to normal icon */
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressed && board.getCommandName().equals("pen")) {
            Shape lastShape = path.get(path.size() - 1);
            lastShape.getPointSet().add(e.getPoint());
            board.repaint();
            Thread senPenData = new Thread(()-> packingPointData(e.getPoint()));
            senPenData.start();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void setPath(ArrayList<Shape> path) {
        this.path = path;

    }
    public ArrayList<Shape> getPath() {
        return path;
    }
    public void packingDrawData(Shape lastShape) {
        /* json data format example
        *  command : drawing
        *  data: Shape object */
        System.out.println(lastShape.getColor().toString());
        serverWindow.server.packing("drawing",lastShape.shapeToJSON().toJSONString());

    }
    public void packingPenData(Shape shape) {
        serverWindow.server.packing("drawing",shape.penToJSON().toJSONString());
    }
    public void packingPointData(Point point) {
        JSONObject jsonPoint = new JSONObject();
        jsonPoint.put("x",point.x);
        jsonPoint.put("y",point.y);
        serverWindow.server.packing("points",jsonPoint.toJSONString());
    }
    public void packingTextData(Shape shape) {
        serverWindow.server.packing("drawing",shape.textBoxToJSON().toJSONString());
    }
}