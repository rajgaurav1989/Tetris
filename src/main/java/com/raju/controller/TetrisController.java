package com.raju.controller;

import com.raju.constants.ProjectConstants;
import com.raju.models.Block;
import com.raju.models.Location;
import com.raju.models.TetrisShape;
import com.raju.service.ShapeService;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class TetrisController {
    private static TetrisController tetrisController;
    private boolean isOpRunning = false;
    private static int topRowIndex = 0;

    private TetrisController() {
    }

    public static TetrisController getInstance() {
        if (tetrisController == null) {
            tetrisController = new TetrisController();
        }
        return tetrisController;
    }

    public EventHandler getKeyPressEventHandler(TetrisShape shape) {
        EventHandler<KeyEvent> handler = event -> handleKeyPress(event, shape);
        return handler;
    }

    private void handleKeyPress(KeyEvent event, TetrisShape shape) {
        if (shape.isTouched() || isOpRunning) {
            return;
        }

        try {
            Node node = shape.getNode();
            switch (event.getCode()) {
                case W:
                    rotate(shape, ProjectConstants.ROTATION_ANGLE);
                    break;
                case S:
                    rotate(shape, (short) -ProjectConstants.ROTATION_ANGLE);
                    break;
                case LEFT:
                    translateHorizontal(shape, true);
                    break;
                case RIGHT:
                    translateHorizontal(shape, false);
                    break;
                case DOWN:
                    translateFall(shape, ProjectConstants.VERTICAL_FALL);
                    break;
                case UP:
                    translateFall(shape, -ProjectConstants.VERTICAL_FALL);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rotate(TetrisShape shape, short angle) throws Exception {
        isOpRunning = true;
        Group node = shape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());

        Transform currentTransform = node.getTransforms().isEmpty() ? null : node.getTransforms().get(0);
        Transform copyTransform = currentTransform;

        Rotate rotateTransform = new Rotate(angle, shape.getMidX(), shape.getMidY(), 0, Rotate.Z_AXIS);

        currentTransform = currentTransform != null ? currentTransform.createConcatenation(rotateTransform) : rotateTransform;
        node.getTransforms().clear();
        node.getTransforms().add(currentTransform);

        bounds = node.localToScene(node.getBoundsInLocal());

        if (!isRotatePossible(shape, angle)) {
            node.getTransforms().clear();
            if (copyTransform != null) {
                node.getTransforms().add(copyTransform);
            } else {
                node.getTransforms().clear();
            }
        }

        isOpRunning = false;
        if (((bounds.getMaxX() - bounds.getMinX()) % ProjectConstants.CELL_SIZE != 0) || ((bounds.getMaxY() - bounds.getMinY()) % ProjectConstants.CELL_SIZE != 0)) {
            System.exit(1);
        }
    }

    private boolean isRotatePossible(TetrisShape shape, short angle) {
        boolean[][] oldShapeInfo = shape.getShapeInfo();
        int numStep = angle < 0 ? 3 : 1;
        shape.setShapeInfo(ShapeService.getInstance().getShape(shape.getShapeInfo(), numStep));

        if (isVerticalTranslateFeasible(shape, 0) && isHorizontalTranslateFeasible(shape, true, 0)) {
            short temp = shape.getWidth();
            shape.setWidth(shape.getHeight());
            shape.setHeight(temp);
            return true;
        }
        shape.setShapeInfo(oldShapeInfo);
        return false;
    }

    private void translateHorizontal(TetrisShape shape, boolean isLeft) throws Exception {
        isOpRunning = true;
        Group node = shape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        int displacement = (isLeft) ? -ProjectConstants.HORIZONTAL_DISPLACEMENT : ProjectConstants.HORIZONTAL_DISPLACEMENT;
        if (isHorizontalTranslateFeasible(shape, isLeft, displacement)) {
            node.translateXProperty().set(node.getTranslateX() + displacement);
            bounds = node.localToScene(node.getBoundsInLocal());
            if (((bounds.getMaxX() - bounds.getMinX()) % ProjectConstants.CELL_SIZE != 0) || ((bounds.getMaxY() - bounds.getMinY()) % ProjectConstants.CELL_SIZE != 0)) {
                System.exit(1);
            }
        }
        isOpRunning = false;
    }

    public void translateFall(TetrisShape shape, float displacement) throws Exception {
        isOpRunning = true;
        Group node = shape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        if (isVerticalTranslateFeasible(shape, displacement)) {
            node.translateYProperty().set(node.getTranslateY() + displacement);
            bounds = node.localToScene(node.getBoundsInLocal());
            if (((bounds.getMaxX() - bounds.getMinX()) % ProjectConstants.CELL_SIZE != 0) || ((bounds.getMaxY() - bounds.getMinY()) % ProjectConstants.CELL_SIZE != 0)) {
                System.exit(1);
            }
        } else {
            shape.setTouched(true);
            updateBlocks(shape, bounds);
        }
        isOpRunning = false;
    }

    private void updateBlocks(TetrisShape shape, Bounds bounds) {
        Group node = shape.getNode();
        bounds = node.localToScene(node.getBoundsInLocal());
        boolean[][] shapeInfo = shape.getShapeInfo();
        int numRows = shapeInfo.length;
        int numCols = shapeInfo[0].length;
        double startX = bounds.getMinX();
        double startY = bounds.getMinY();

        topRowIndex = (int) (startY / ProjectConstants.CELL_SIZE) - 1;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!shapeInfo[row][col]) {
                    continue;
                }
                double xCoord = startX + col * ProjectConstants.CELL_SIZE;
                double yCoord = startY + row * ProjectConstants.CELL_SIZE;

                int rowNum = (int) (yCoord / ProjectConstants.CELL_SIZE) - 1;
                int colNum = (int) (xCoord / ProjectConstants.CELL_SIZE) - 1;

                Block block = ShapeService.getInstance().getBlock(new Location((short) rowNum, (short) colNum));
                block.setFree(false);
            }
        }
    }

    private void mergeLine() {
        for (int index = topRowIndex; index > ProjectConstants.NUM_VERTICAL_BLOCK; index++) {
            if (!isRowFull(index)){
                continue;
            }
            topRowIndex++;

        }
    }

    private boolean isRowFull(int rowIndex) {
        for (int col = 0; col < ProjectConstants.NUM_HORIZONTAL_BLOCKS; col++) {
            Block block = ShapeService.getInstance().getBlock(new Location((short) rowIndex, (short) col));
            if (block.isFree()){
                return false;
            }
        }
        return true;
    }

    private boolean isVerticalTranslateFeasible(TetrisShape tetrisShape, float displacement) {
        Group node = tetrisShape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        if (bounds.getMaxY() + displacement > ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT) {
            return false;
        }

        boolean[][] shapeInfo = tetrisShape.getShapeInfo();
        return isMovementPossible(shapeInfo, bounds.getMinX(), bounds.getMinY() + displacement);
    }

    private boolean isMovementPossible(boolean[][] shapeInfo, double startX, double startY) {
        int numCols = shapeInfo[0].length;
        int numRows = shapeInfo.length;

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!shapeInfo[row][col]) {
                    continue;
                }
                double xCoord = startX + col * ProjectConstants.CELL_SIZE;
                double yCoord = startY + row * ProjectConstants.CELL_SIZE;
                int rowNum = (int) (yCoord / ProjectConstants.CELL_SIZE) - 1;
                int colNum = (int) (xCoord / ProjectConstants.CELL_SIZE) - 1;
                Block block = ShapeService.getInstance().getBlock(new Location((short) rowNum, (short) colNum));
                if (block == null || !block.isFree()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isHorizontalTranslateFeasible(TetrisShape shape, boolean isLeft, int displacement) {
        Group node = shape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());

        if ((isLeft && bounds.getMinX() + displacement < ProjectConstants.CELL_SIZE) ||
                (!isLeft && bounds.getMaxX() + displacement > ProjectConstants.WINDOW_WIDTH + ProjectConstants.CELL_SIZE)) {
            return false;
        }

        boolean[][] shapeInfo = shape.getShapeInfo();
        double startX = bounds.getMinX() + displacement;
        return isMovementPossible(shapeInfo, startX, bounds.getMinY());
    }

}
