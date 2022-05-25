package PaintFunction;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public abstract class Shape implements Serializable {
    private int startX, startY, endX, endY;
    private ArrayList<Point> pointSet = new ArrayList<>();
    private float width;
    private Color color;


    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public void setPointSet(ArrayList<Point> pointSet) {
        this.pointSet = pointSet;
    }

    public ArrayList<Point> getPointSet() {
        return pointSet;
    }


}
