package com.raju.models;

import com.raju.constants.ProjectConstants;

public class Location {
    short rowNum;
    short colNum;

    public Location(short rowNum, short colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public short getRowNum() {
        return rowNum;
    }

    public void setRowNum(short rowNum) {
        this.rowNum = rowNum;
    }

    public short getColNum() {
        return colNum;
    }

    public void setColNum(short colNum) {
        this.colNum = colNum;
    }

    @Override
    public int hashCode() {
        int hashCode = ProjectConstants.HASH;
        hashCode = ProjectConstants.HASH_MULTIPLIER * hashCode + this.rowNum;
        hashCode = ProjectConstants.HASH_MULTIPLIER * hashCode + this.colNum;
        return hashCode;
    }
}
