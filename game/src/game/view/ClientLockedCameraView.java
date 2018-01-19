package game.view;

import game.message.playerMoveMessage.*;
import game.model.Game.GameModel;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

import static Util.Const.*;

//TODO this file is WIP
public class ClientLockedCameraView {

    private GameModel model;

    private AnimationTimer animator;

    private Stage window;

    public ClientLockedCameraView(GameModel model) {
        this.model = model;
    }

    public void start(Stage window) {
        this.window = window;

        window.setResizable(true);
        window.setTitle("King Slayer");

        Group root = new Group();

        Canvas canvasBG = new Canvas(INIT_SCREEN_WIDTH, INIT_SCREEN_HEIGHT);
        Canvas canvasFG = new Canvas(INIT_SCREEN_WIDTH, INIT_SCREEN_HEIGHT);

        GraphicsContext gcFG = canvasFG.getGraphicsContext2D();
        GraphicsContext gcBG = canvasBG.getGraphicsContext2D();

        window.widthProperty().addListener(l -> {
            canvasBG.setWidth(window.getWidth());
        });

        window.heightProperty().addListener(l -> {
            canvasBG.setHeight(window.getHeight());
        });

        window.setFullScreen(true);


        root.getChildren().add(canvasBG);
        root.getChildren().add(canvasFG);

        Scene scene = new Scene(root);

        double[] scaleFactor = {1.0};

        double[] pos = {0, 0};

        AnimationTimer animator = new AnimationTimer() {
            @Override
            public void handle(long arg0) {
                model.update();
                gcFG.clearRect(0, 0, 100000000, 10000000);

                double gameW = scaleFactor[0] * window.getWidth() / TILE_PIXELS;
                double gameH = scaleFactor[0] * window.getHeight() / TILE_PIXELS;

                double xDelta = model.playerB.getX() * TILE_PIXELS * scaleFactor[0] - window.getWidth()/2 - pos[0];
                double yDelta = model.playerB.getY() * TILE_PIXELS * scaleFactor[0] - window.getHeight()/2 - pos[1];

                gcFG.transform(new Affine(Affine.translate(xDelta, yDelta)));
                gcBG.transform(new Affine(Affine.translate(xDelta, yDelta)));

                pos[0] += xDelta;
                pos[1] += yDelta;

                model.draw(gcFG, (int)(model.playerB.getX() - gameW / 2), (int)(model.playerB.getY() - gameH / 2), (int)gameW, (int)gameH);
            }
        };

        scene.setOnScroll(e -> {
            if (e.getDeltaY() < 0) {
                scaleFactor[0] *= 0.9;
                gcFG.transform(new Affine(Affine.scale(0.9, 0.9)));
                gcBG.transform(new Affine(Affine.scale(0.9, 0.9)));
            } else {
                scaleFactor[0] *= 1.1;
                gcFG.transform(new Affine(Affine.scale(1.1, 1.1)));
                gcBG.transform(new Affine(Affine.scale(1.1, 1.1)));
            }
        });

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                window.setFullScreen(true);
            }
            if (e.getCode() == KeyCode.S) {
                //     gcFG.transform(new Affine(Affine.translate(0, -100)));
                //     gcBG.transform(new Affine(Affine.translate(0, -100)));
                // model.drawBG(gcBG);
                model.processMessage(new PlayerUp(0));
            }
            //
            if (e.getCode() == KeyCode.W) {

                //      gcFG.transform(new Affine(Affine.translate(0, 100)));
                //      gcBG.transform(new Affine(Affine.translate(0, 100)));
                // model.drawBG(gcBG);
                model.processMessage(new PlayerDown(0));
            }
            //
            if (e.getCode() == KeyCode.D) {

                //    gcFG.transform(new Affine(Affine.translate(-100, 0)));
                //    gcBG.transform(new Affine(Affine.translate(-100, 0)));
                //  model.drawBG(gcBG);
                model.processMessage(new PlayerLeft(0));
            }
            //
            if (e.getCode() == KeyCode.A) {

                //    gcFG.transform(new Affine(Affine.translate(100, 0)));
                //     gcBG.transform(new Affine(Affine.translate(100, 0)));
                // model.drawBG(gcBG);
                model.processMessage(new PlayerRight(0));
            }
            //
            //           model.playerA.right();
            if (e.getCode() == KeyCode.UP)
                model.processMessage(new PlayerUp(1));
            if (e.getCode() == KeyCode.DOWN)
                model.processMessage(new PlayerDown(1));
            if (e.getCode() == KeyCode.LEFT)
                model.processMessage(new PlayerLeft(1));
            if (e.getCode() == KeyCode.RIGHT)
                model.processMessage(new PlayerRight(1));
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W)
                model.processMessage(new PlayerStopVert(0));
            //     model.playerA.stopVert();
            if (e.getCode() == KeyCode.S)
                model.processMessage(new PlayerStopVert(0));
            //        model.playerA.stopVert();
            if (e.getCode() == KeyCode.A)
                model.processMessage(new PlayerStopHorz(0));
            //         model.playerA.stopHorz();
            if (e.getCode() == KeyCode.D)
                model.processMessage(new PlayerStopHorz(0));
            //         model.playerA.stopHorz();
            if (e.getCode() == KeyCode.RIGHT)
                model.processMessage(new PlayerStopHorz(1));
            if (e.getCode() == KeyCode.LEFT)
                model.processMessage(new PlayerStopHorz(1));
            if (e.getCode() == KeyCode.UP)
                model.processMessage(new PlayerStopVert(1));
            if (e.getCode() == KeyCode.DOWN)
                model.processMessage(new PlayerStopHorz(1));
        });

        window.setScene(scene);
        window.show();
        animator.start();
    }
}
