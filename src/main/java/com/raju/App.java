package com.raju;

import com.raju.constants.ProjectConstants;
import com.raju.constants.StyleConstants;
import com.raju.controller.TetrisController;
import com.raju.service.ShapeService;
import com.raju.shape.TetrisShape;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

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
        primaryStage.show();

        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastUpdateTime = 0;

            TetrisShape tetrisShape = ShapeService.getInstance().getShape();
            Node node = tetrisShape.getNode();
            TetrisShape previousShape = null;
            boolean isTouched = true;

            EventHandler eventHandler , previousEventHandler ;

            @Override
            public void handle(long now) {
                if (now - lastUpdateTime >= ProjectConstants.FRAME_RENDER_TIME) {
                    lastUpdateTime = now;
                    if (isTouched) {
                        isTouched = false ;
                        if (previousShape != null) {
                            tetrisShape = ShapeService.getInstance().getShape();
                            node = tetrisShape.getNode();
                        }
                        int posX = Math.floorDiv((ProjectConstants.NUM_HORIZONTAL_BLOCKS - tetrisShape.getWidth()), 2) * (ProjectConstants.CELL_SIZE);
                        node.translateXProperty().set(node.getTranslateX() + posX);
                        node.setTranslateY(ProjectConstants.BUFFER_HEIGHT);
                        node.setFocusTraversable(true);

                        if (previousShape != null){
                            primaryStage.removeEventHandler(KeyEvent.KEY_PRESSED,previousEventHandler);
                        }
                        tetrisGroup.getChildren().addAll(node, ShapeService.getInstance().getTetrisBoundary());

                        eventHandler = TetrisController.getInstance().getKeyPressEventHandler(tetrisShape);
                        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
                    }

                    if (node.getTranslateY() <= ProjectConstants.WINDOW_HEIGHT + ProjectConstants.BUFFER_HEIGHT) {
                        try {
                            boolean isMoved = TetrisController.getInstance().translateFall(node, ProjectConstants.DEFAULT_FALL);
                            isTouched = !isMoved;
                            if (isMoved){
                                previousShape = tetrisShape;
                                previousEventHandler = eventHandler;
                                node.setFocusTraversable(false);
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
}
