package com.example.snakegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SnakeGame extends Application {
    // variable
    static int snake_speed = 8;
    static int width = 40;
    static int height = 25;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 40;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static Random rand = new Random();

    public enum Dir {                                   //contains directions for controls
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public void start(Stage primaryStage) {
        try {
            newFood();


            VBox root = new VBox();
            Canvas c = new Canvas(width * cornersize, height * cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / snake_speed) {
                        lastTick = now;
                        tick(gc);
                    }
                }

            }.start();


            Scene scene = new Scene(root, width * cornersize, height * cornersize);

            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.ENTER) {
                    snake_speed = 8;
                    newFood();
                    gameOver = false;
                    snake.clear();
                    snake.add(new Corner(width / 2, height / 2));
                    snake.add(new Corner(width / 2, height / 2));
                    snake.add(new Corner(width / 2, height / 2));


                }
                if (key.getCode() == KeyCode.UP) {
                    direction = Dir.up;
                }
                if (key.getCode() == KeyCode.LEFT) {
                    direction = Dir.left;
                }
                if (key.getCode() == KeyCode.DOWN) {
                    direction = Dir.down;
                }
                if (key.getCode() == KeyCode.RIGHT) {
                    direction = Dir.right;
                }




            });

            // add start snake parts
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tick
    public static void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 150));
            gc.fillText("GAME OVER" , 450, 500);
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 120));
            gc.fillText("Press Enter to restart" , 350, 650);

            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                //game over if snake touches a corner
                if (snake.get(0).y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > height) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > width) {
                    gameOver = true;
                }
                break;

        }

        // eat food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            //snake grows
            snake.add(new Corner(-1, -1));
            newFood();
        }

        // self destroy
        for (int i = 1; i < snake.size(); i++) {
            //if the snake hits itself > game over
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        // setting background color
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        // score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + (snake_speed - 9), 10, 30);

        Image im = new Image("https://usapple.org/wp-content/uploads/2019/10/apple-pink-lady.png",false);
        gc.setFill(new ImagePattern (im));
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);



        // snake
        for (Corner c : snake) {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.BLUE);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);

        }

    }

    // creating new food
    public static void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            snake_speed++;
            break;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}