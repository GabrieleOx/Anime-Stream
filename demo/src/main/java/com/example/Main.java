package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

//Gabriele ricorda: per avviare javafx con maven usa il comando "mvn javafx:run" dalla cartella del progetto (qui demo)//
//ma prima sul pc IL devi eseguire su un cmd esterno su cui poi avvii il prog -> set PATH=C:\Users\gabriele.ossola\Maven\apache-maven-3.9.10\bin;%PATH%
//per arrivare sulla cartella da quella utente: cd Documents\codes\videodownloader\anime-stream\demo
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        Image icon = new Image(getClass().getResource("/icona.png").toString());

        stage.getIcons().add(icon);
        stage.setTitle("Anime Downloader");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(scene);
        stage.show();
    }
}

//Stages examples:
/*Group root = new Group();
        Scene scene = new Scene(root, Color.RED);

        Image icon = new Image(getClass().getResource("/AnimeIcon.png").toString());
        stage.getIcons().add(icon);
        stage.setTitle("Stage demo program");
        stage.setWidth(420);
        stage.setHeight(420);
        //stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("YOU CAN'T ESCAPE unless you press q");
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("q"));

        stage.setScene(scene);
        stage.show();*/

//Scenes exapmles:
/*Group root = new Group();
        Scene scene = new Scene(root, 600, 600, Color.LIGHTBLUE);
        Stage stage = new Stage();

        Text text = new Text();
        text.setText("Testooooooooo");
        text.setX(50);
        text.setY(50);
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.LIMEGREEN);

        Line line = new Line();
        line.setStartX(200);
        line.setStartY(200);
        line.setEndX(500);
        line.setEndY(200);
        line.setStrokeWidth(5);
        line.setStroke(Color.RED);
        line.setOpacity(0.5);
        line.setRotate(95);
        
        Rectangle rectangle = new Rectangle();
        rectangle.setX(100);
        rectangle.setY(100);
        rectangle.setWidth(160);
        rectangle.setHeight(120);
        rectangle.setFill(Color.ANTIQUEWHITE);
        rectangle.setStrokeWidth(3);
        rectangle.setStroke(Color.BLACK);

        Polygon triangle = new Polygon();
        triangle.getPoints().setAll(
            200.0, 200.0,
            300.0, 300.0,
            200.0, 300.0,
            300.0, 200.0
        );
        triangle.setFill(Color.BEIGE);
        triangle.setStrokeWidth(10);
        triangle.setStroke(Color.WHEAT);

        Circle circle = new Circle();
        circle.setCenterX(400);
        circle.setCenterY(400);
        circle.setRadius(100);
        circle.setFill(Color.BLUEVIOLET);
        circle.setStroke(Color.BURLYWOOD);
        circle.setStrokeWidth(10);

        Image image = new Image("/AnimeIcon.png");
        ImageView imageView = new ImageView(image);
        imageView.setX(10);
        imageView.setY(399);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);

        root.getChildren().add(text);
        root.getChildren().add(line);
        root.getChildren().add(rectangle);
        root.getChildren().add(triangle);
        root.getChildren().add(circle);
        root.getChildren().add(imageView);
        stage.setScene(scene);
        stage.show();*/