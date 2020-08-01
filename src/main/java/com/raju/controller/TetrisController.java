package com.raju.controller;

import com.raju.constants.ProjectConstants;
import com.raju.models.TetrisShape;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class TetrisController {
    private static TetrisController tetrisController;

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
        try {
            Node node = shape.getNode();
            switch (event.getCode()) {
                case W:
                    rotate(shape, ProjectConstants.ROTATION_ANGLE);
                    break;
                case S:
                    rotate(shape, -ProjectConstants.ROTATION_ANGLE);
                    break;
                case LEFT:
                    translateHorizontal(node, true);
                    break;
                case RIGHT:
                    translateHorizontal(node, false);
                    break;
                case DOWN:
                    translateFall(node, ProjectConstants.VERTICAL_FALL);
                    break;
                case UP:
                    translateFall(node, -ProjectConstants.VERTICAL_FALL);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rotate(TetrisShape shape, int angle) throws Exception {
        Node node = shape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        if (bounds.getMaxY() == ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT) {
            return;
        }

        Transform currentTransform = node.getTransforms().isEmpty() ? null : node.getTransforms().get(0);
        Transform copyTransform = currentTransform;

        Rotate rotateTransform = new Rotate(angle, shape.getMidX(), shape.getMidY(), 0, Rotate.Z_AXIS);

        currentTransform = currentTransform != null ? currentTransform.createConcatenation(rotateTransform) : rotateTransform;
        node.getTransforms().clear();
        node.getTransforms().add(currentTransform);

        short temp = shape.getWidth();
        shape.setWidth(shape.getHeight());
        shape.setHeight(temp);

        bounds = node.localToScene(node.getBoundsInLocal());
        if (bounds.getMinX() < ProjectConstants.CELL_SIZE || bounds.getMaxX() > ProjectConstants.WINDOW_WIDTH + ProjectConstants.CELL_SIZE ||
                bounds.getMaxY() > ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT) {
            node.getTransforms().clear();
            node.getTransforms().add(copyTransform);
        }

        if (((bounds.getMaxX() - bounds.getMinX()) % ProjectConstants.CELL_SIZE != 0) || ((bounds.getMaxY() - bounds.getMinY()) % ProjectConstants.CELL_SIZE != 0)) {
            throw new Exception("Serious design error in tetris");
        }
    }

    private void translateHorizontal(Node node, boolean isLeft) throws Exception {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        int displacement = (isLeft) ? -ProjectConstants.HORIZONTAL_DISPLACEMENT : ProjectConstants.HORIZONTAL_DISPLACEMENT;
        if ((isLeft && bounds.getMinX() + displacement >= ProjectConstants.CELL_SIZE) ||
                (!isLeft && bounds.getMaxX() + displacement <= ProjectConstants.WINDOW_WIDTH + ProjectConstants.CELL_SIZE)) {
            node.translateXProperty().set(node.getTranslateX() + displacement);
            bounds = node.localToScene(node.getBoundsInLocal());
            if (((bounds.getMaxX() - bounds.getMinX()) % ProjectConstants.CELL_SIZE != 0) || ((bounds.getMaxY() - bounds.getMinY()) % ProjectConstants.CELL_SIZE != 0)) {
                throw new Exception("Serious design error in tetris");
            }
        }
    }

    public boolean translateFall(Node node, float displacement) throws Exception {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        if (bounds.getMaxY() + displacement <= ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT) {
            node.translateYProperty().set(node.getTranslateY() + displacement);
            bounds = node.localToScene(node.getBoundsInLocal());
            if (((bounds.getMaxX() - bounds.getMinX()) % ProjectConstants.CELL_SIZE != 0) || ((bounds.getMaxY() - bounds.getMinY()) % ProjectConstants.CELL_SIZE != 0)) {
                throw new Exception("Serious design error in tetris");
            }
            return true;
        }
        return false;
    }

}
