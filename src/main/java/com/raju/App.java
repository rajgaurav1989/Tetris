package com.raju;

import com.raju.constants.ProjectConstants;
import com.raju.constants.StyleConstants;
import com.raju.controller.TetrisController;
import com.raju.models.Block;
import com.raju.models.Location;
import com.raju.service.ShapeService;
import com.raju.models.TetrisShape;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Camera camera = new PerspectiveCamera();
        Group tetrisGroup = new Group();

        Scene tetrisScene = new Scene(tetrisGroup, ProjectConstants.WINDOW_WIDTH + 2 * ProjectConstants.CELL_SIZE,
                ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT + ProjectConstants.CELL_SIZE);
        tetrisScene.setFill(StyleConstants.TETRIS_BACKGROUND_COLOR);
        tetrisScene.setCamera(camera);

        primaryStage.setTitle(ProjectConstants.PROJECT_TITLE);
        primaryStage.setScene(tetrisScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastUpdateTime = 0;

            TetrisShape tetrisShape = ShapeService.getInstance().getShape();
            Group node = tetrisShape.getNode();
            TetrisShape previousShape = null;
            boolean isTouched = true;

            EventHandler eventHandler;

            @Override
            public void handle(long now) {
                if (now - lastUpdateTime >= ProjectConstants.FRAME_RENDER_TIME) {
                    lastUpdateTime = now;
                    if (TetrisController.isMergeLineRunning){
                        return;
                    }
                    if (isTouched) {
                        isTouched = false;
                        if (previousShape != null) {
                            tetrisShape = ShapeService.getInstance().getShape();
                            node = tetrisShape.getNode();
                        }
                        int posX = Math.floorDiv((ProjectConstants.NUM_HORIZONTAL_BLOCKS - tetrisShape.getWidth()), 2) * (ProjectConstants.CELL_SIZE);
                        node.translateXProperty().set(node.getTranslateX() + posX);
                        node.setTranslateY(ProjectConstants.BUFFER_HEIGHT);
                        node.setFocusTraversable(true);

                        tetrisGroup.getChildren().addAll(node, ShapeService.getInstance().getTetrisBoundary(),ShapeService.getInstance().getGridLines());

                        eventHandler = TetrisController.getInstance().getKeyPressEventHandler(tetrisShape);
                        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
                    }

                    if (node.getTranslateY() <= ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT) {
                        try {
                            TetrisController.getInstance().translateFall(tetrisShape, ProjectConstants.DEFAULT_FALL);
                            if (tetrisShape.isTouched()) {
                                Bounds bounds = node.localToScene(node.getBoundsInLocal());
                                if (bounds.getMinY() <= ProjectConstants.CELL_SIZE){
                                    System.out.println("\n\nGame Over\n\n");
                                    System.exit(0);
                                }
                                previousShape = tetrisShape;
                                node.setFocusTraversable(false);
                                updateChildBlocks(tetrisGroup, previousShape);
                                TetrisController.isMergeLineRunning = true;
                                TetrisController.getInstance().mergeLine();
                                isTouched = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        animationTimer.start();
    }

    private void updateChildBlocks(Group root, TetrisShape shape) {
        Group node = shape.getNode();
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        List<Node> blocks = node.getChildren();

        boolean[][] shapeInfo = shape.getShapeInfo();
        int numRows = shapeInfo.length;
        int numCols = shapeInfo[0].length;

        double startX = bounds.getMinX();
        double startY = bounds.getMinY();

        List<Node> independentBlocks = new ArrayList<>();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!shapeInfo[row][col] || blocks.size() <= 0) {
                    continue;
                }
                Node block = blocks.remove(blocks.size() - 1);
                Bounds b1 = block.localToScene(block.getBoundsInLocal());
                double xCoord = startX + col * ProjectConstants.CELL_SIZE;
                double yCoord = startY + row * ProjectConstants.CELL_SIZE;
                block.setTranslateX(xCoord - b1.getMinX());
                block.setTranslateY(yCoord - b1.getMinY());

                b1 = block.localToScene(block.getBoundsInLocal());
                int rowIndex = (int) (b1.getMinY() / ProjectConstants.CELL_SIZE) - 1;
                int colIndex = (int) (b1.getMinX() / ProjectConstants.CELL_SIZE) - 1;
                Block basicBlock = ShapeService.getInstance().getBlock(new Location((short)rowIndex,(short)colIndex));
                basicBlock.setNode(block);

                independentBlocks.add(block);
            }
        }

        root.getChildren().addAll(independentBlocks);
    }

}
