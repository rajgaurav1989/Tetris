package com.raju.models;

import javafx.scene.Group;
import javafx.scene.Node;

public class TetrisShape {
    private Group node;
    private short width;
    private short height;
    private float midX;
    private float midY;
    private boolean[][] shapeInfo;
    private boolean isTouched = false;

    public TetrisShape(Group node, boolean[][] shapeInfo, short width, short height, float midX, float midY) {
        this.node = node;
        this.width = width;
        this.height = height;
        this.midX = midX;
        this.midY = midY;
        this.shapeInfo = shapeInfo;
    }

    public Group getNode() {
        return node;
    }

    public void setNode(Group node) {
        this.node = node;
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public float getMidX() {
        return midX;
    }

    public void setMidX(float midX) {
        this.midX = midX;
    }

    public float getMidY() {
        return midY;
    }

    public void setMidY(float midY) {
        this.midY = midY;
    }

    public boolean[][] getShapeInfo() {
        return shapeInfo;
    }

    public void setShapeInfo(boolean[][] shapeInfo) {
        this.shapeInfo = shapeInfo;
    }

    public boolean isTouched() {
        return isTouched;
    }

    public void setTouched(boolean touched) {
        isTouched = touched;
    }
}
