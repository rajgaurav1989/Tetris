package com.raju.constants;

import java.util.Arrays;
import java.util.List;

public class ProjectConstants {
    public static final String PROJECT_TITLE = "Raj's Sudoku";
    public static final long FRAME_RENDER_TIME = (100000l / 32) * 100000l;

    public static final int CELL_SIZE = 22;
    public static final int CELL_DEPTH = 1;

    public static final float BUFFER_HEIGHT = CELL_SIZE;
    public static final int NUM_HORIZONTAL_BLOCKS = 30;
    public static final int NUM_VERTICAL_BLOCK = 30;

    public static final float WINDOW_HEIGHT = NUM_VERTICAL_BLOCK * CELL_SIZE;
    public static final float WINDOW_WIDTH = NUM_HORIZONTAL_BLOCKS * CELL_SIZE;

    public static final int ROTATION_ANGLE = 90;
    public static final int HORIZONTAL_DISPLACEMENT = CELL_SIZE;
    public static final int VERTICAL_FALL = CELL_SIZE;
    public static final int DEFAULT_FALL = CELL_SIZE;

    public static final float MIN_LEFT_X_COORD = CELL_SIZE;
    public static final float MAX_RIGHT_X_COORD = WINDOW_WIDTH + MIN_LEFT_X_COORD;
    public static final float MIN_TOP_Y_COORD = CELL_SIZE;
    public static final float MAX_BOTTOM_Y_COORD = WINDOW_HEIGHT + MIN_TOP_Y_COORD;

    public static final int HASH = 5;
    public static final int HASH_MULTIPLIER = 89;

}
