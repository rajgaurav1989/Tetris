package com.raju.models;

import javafx.scene.Node;

public class Block {
    private Location location;
    private boolean isFree = true;
    private Node node;

    public Block(){
    }

    public Block(Location location, boolean isFree) {
        this.location = location;
        this.isFree = isFree;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return "Block{" +
                "location=" + location +
                ", isFree=" + isFree +
                '}';
    }
}
