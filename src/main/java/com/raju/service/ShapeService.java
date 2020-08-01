package com.raju.service;

import com.raju.constants.ProjectConstants;
import com.raju.constants.StyleConstants;
import com.raju.models.TetrisShape;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;

import java.util.*;

public class ShapeService {
    private final int LINE_WIDTH = 2;
    private static ShapeService shapeService;
    private Map<Integer, boolean[][]> shapeMap;

    private Random rand = new Random();
    private static final boolean[][] T_SHAPE_DESCRIPTION = new boolean[][]{{false, true, false}, {true, true, true}};
    private static final boolean[][] LEFT_L_DESCRIPTION = new boolean[][]{{true, false}, {true, false}, {true, true}};
    private static final boolean[][] RIGHT_L_DESCRIPTION = new boolean[][]{{false, true}, {false, true}, {true, true}};
    private static final boolean[][] LEFT_Z_DESCRIPTION = new boolean[][]{{true, true, false}, {false, true, true}};
    private static final boolean[][] RIGHT_Z_DESCRIPTION = new boolean[][]{{false, true, true}, {true, true, false}};
    private static final boolean[][] LINE_DESCRIPTION = new boolean[][]{{true, true, true, true}};
    private static final boolean[][] CUBE_DESCRIPTION = new boolean[][]{{true, true}, {true, true}};

    private static final List<boolean[][]> shapeList = Arrays.asList(T_SHAPE_DESCRIPTION, LEFT_L_DESCRIPTION, RIGHT_L_DESCRIPTION, LEFT_Z_DESCRIPTION,
            RIGHT_Z_DESCRIPTION, LINE_DESCRIPTION, CUBE_DESCRIPTION);

    private static final short NUM_ROTATIONS = 4;

    private ShapeService() {
        shapeMap = new HashMap<>();
        for (int shapeIndex = 0; shapeIndex < shapeList.size(); shapeIndex++) {
            shapeMap.put(shapeIndex, shapeList.get(shapeIndex));
        }
    }

    public static ShapeService getInstance() {
        if (shapeService == null) {
            shapeService = new ShapeService();
        }
        return shapeService;
    }

    public TetrisShape getShape() {
        int randomShapeIndex = rand.nextInt(shapeList.size());
        int numRotations = rand.nextInt(NUM_ROTATIONS);

        boolean[][] shapeInfo = getShape(shapeMap.get(randomShapeIndex), numRotations);

        List<Node> shapeBox = getShapeHelper(shapeInfo);
        Group shapeNode = new Group();
        shapeNode.getChildren().addAll(shapeBox);

        short width = (short) shapeInfo[0].length;
        short height = (short) shapeInfo.length;

        int midWidth = Math.floorDiv(width, 2);
        int midHeight = Math.floorDiv(height, 2);
        float midX = (ProjectConstants.CELL_SIZE * midWidth);
        float midY = (ProjectConstants.CELL_SIZE * midHeight);

        return new TetrisShape(shapeNode, shapeInfo, width, height, midX, midY);
    }

    public boolean[][] getShape(boolean[][] input, int rotationStep) {
        if (rotationStep == 0) {
            return input;
        }
        int originalRowSize = input.length;
        int originalColSize = input[0].length;

        int rowSize = originalRowSize;
        int colSize = originalColSize;

        if (rotationStep != 2) {
            rowSize = originalColSize;
            colSize = originalRowSize;
        }

        boolean[][] output = new boolean[rowSize][colSize];

        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                int copyRowIndex = 0, copyColIndex = 0;
                if (rotationStep == 2) {
                    copyRowIndex = originalRowSize - 1 - row;
                    copyColIndex = originalColSize - 1 - col;
                } else {
                    copyRowIndex = rotationStep == 1 ? originalRowSize - 1 - col : col;
                    copyColIndex = rotationStep == 1 ? row : originalColSize - 1 - row;
                }
                output[row][col] = input[copyRowIndex][copyColIndex];
            }
        }
        return output;
    }

    private List<Node> getShapeHelper(boolean[][] shapeInfo) {
        List<Node> boxes = new ArrayList<>();
        float initPosX = ProjectConstants.CELL_SIZE / 2;
        float initPosY = ProjectConstants.CELL_SIZE / 2;
        for (int row = 0; row < shapeInfo.length; row++) {
            for (int col = 0; col < shapeInfo[row].length; col++) {
                if (!shapeInfo[row][col]) {
                    continue;
                }

                Box box = new Box(ProjectConstants.CELL_SIZE, ProjectConstants.CELL_SIZE, ProjectConstants.CELL_DEPTH);
                float currentX = initPosX + (col) * (ProjectConstants.CELL_SIZE);
                float currentY = initPosY + (row) * (ProjectConstants.CELL_SIZE);
                box.translateXProperty().set(currentX);
                box.translateYProperty().set(currentY);

                PhongMaterial material = new PhongMaterial(Color.ORANGE);
                box.setMaterial(material);

                boxes.add(box);
                boxes.add(getRectangle(Arrays.asList(
                        new Point3D(currentX + LINE_WIDTH - ProjectConstants.CELL_SIZE / 2, currentY + LINE_WIDTH - ProjectConstants.CELL_SIZE / 2, 0),
                        new Point3D(currentX - LINE_WIDTH + ProjectConstants.CELL_SIZE / 2, currentY + LINE_WIDTH - ProjectConstants.CELL_SIZE / 2, 0),
                        new Point3D(currentX - LINE_WIDTH + ProjectConstants.CELL_SIZE / 2, currentY - LINE_WIDTH + ProjectConstants.CELL_SIZE / 2, 0),
                        new Point3D(currentX + LINE_WIDTH - ProjectConstants.CELL_SIZE / 2, currentY - LINE_WIDTH + ProjectConstants.CELL_SIZE / 2, 0)
                )));
            }
        }
        return boxes;
    }

    private Node getRectangle(List<Point3D> pointList) {
        List<Line> lineList = new ArrayList<>();
        short n = (short) pointList.size();
        for (int index = 0; index < n; index++) {
            Point3D start = pointList.get(index % n);
            Point3D end = pointList.get((index + 1) % n);
            Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            line.setStroke(StyleConstants.LINE_COLOR_IN_SHAPE);
            line.setStrokeWidth(LINE_WIDTH);
            lineList.add(line);
        }
        Group node = new Group();
        node.getChildren().addAll(lineList);
        return node;
    }

    public Node getTetrisBoundary() {
        List<Node> boundary = new ArrayList<>();
        double boundaryHeight = ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT;
        double rightBoundaryXCord = ProjectConstants.WINDOW_WIDTH + ProjectConstants.CELL_SIZE;
        boundary.add(getTetrisBoundaryLine(ProjectConstants.CELL_SIZE / 2, 0, ProjectConstants.CELL_SIZE / 2, boundaryHeight,
                StyleConstants.BOUNDARY_COLOR, ProjectConstants.CELL_SIZE));
        boundary.add(getTetrisBoundaryLine(rightBoundaryXCord + ProjectConstants.CELL_SIZE / 2, 0d,
                rightBoundaryXCord + ProjectConstants.CELL_SIZE / 2, boundaryHeight, StyleConstants.BOUNDARY_COLOR, ProjectConstants.CELL_SIZE));
        boundary.add(getTetrisBoundaryLine(0, boundaryHeight + ProjectConstants.CELL_SIZE / 2, rightBoundaryXCord + ProjectConstants.CELL_SIZE,
                boundaryHeight + ProjectConstants.CELL_SIZE / 2, StyleConstants.BOUNDARY_COLOR, ProjectConstants.CELL_SIZE));
        boundary.add(getTetrisBoundaryLine(ProjectConstants.CELL_SIZE, ProjectConstants.CELL_SIZE / 2, rightBoundaryXCord, ProjectConstants.CELL_SIZE / 2,
                StyleConstants.BOUNDARY_COLOR, ProjectConstants.CELL_SIZE));
        Group boundaryNode = new Group();
        boundaryNode.getChildren().addAll(boundary);
        return boundaryNode;
    }

    private Line getTetrisBoundaryLine(double x1, double y1, double x2, double y2, Color color, double width) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(color);
        line.setStrokeWidth(width);
        return line;
    }
}
