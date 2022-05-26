package PaintFunction;

import org.json.simple.JSONObject;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author: Tianjia Chen
 * Student Number: 903737
 * Email: tianjiac@student.unimelb.edu.au
 */
public class Shape implements Serializable {
    private int startX, startY, endX, endY;
    private ArrayList<Point> pointSet = new ArrayList<>();
    private float width;
    private Color color;
    private String type;

    public Shape(String type) {
        this.type = type;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Point> getPointSet() {
        return pointSet;
    }

    public JSONObject shapeToJSON() {

        JSONObject shapeInJson = new JSONObject();
        shapeInJson.put("type",type);
        shapeInJson.put("width",width);
        shapeInJson.put("color", color.hashCode());
        shapeInJson.put("start X", startX);
        shapeInJson.put("start Y", startY);
        shapeInJson.put("end X", endX);
        shapeInJson.put("end Y", endY);
        return shapeInJson;
    }
}
