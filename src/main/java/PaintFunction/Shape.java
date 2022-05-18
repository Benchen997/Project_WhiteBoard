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

    public ArrayList<Point> getPointSet() {
        return pointSet;
    }


}
