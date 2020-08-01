package com.raju.models;

public class Block {
    private Location location;
    private boolean isFree = true;

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
}
