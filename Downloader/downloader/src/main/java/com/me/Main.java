package com.me;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

//Gabriele ricorda: per avviare javafx con maven usa il comando "mvn javafx:run" dalla cartella del progetto (qui demo)//
//ma prima sul pc IL devi eseguire su un cmd esterno su cui poi avvii il prog -> set PATH=C:\Users\gabriele.ossola\Maven\apache-maven-3.9.10\bin;%PATH%
//per arrivare sulla cartella da quella utente: cd Documents\codes\videodownloader\anime-stream\Downloader\downloader
public class Main extends Application {

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ArrayList<Thread> downloadThreads = new ArrayList<>(), stopThreads = new ArrayList<>();

        char c;
        String os = System.getProperty("os.name");

        if(os.contains("Windows"))
            c = '\\';
        else c = '/';
        final char slash = c;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        Image icon = new Image(getClass().getResource("/icona.png").toString());

        Controller controller = loader.getController();
        controller.loadAnimeList(slash, downloadThreads, stopThreads);

        stage.getIcons().add(icon);
        stage.setTitle("Anime Downloader");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            int downloadInCorso = 0, downloadTrovati;
            for(int i = 0; i < AnimeDownloader.downloadThredStop.size(); i++)
                if(!AnimeDownloader.downloadThredStop.get(i))
                    downloadInCorso++;
                
            if(downloadInCorso > 0){
                String str = "Ci sono ancora " + downloadInCorso + " download in corso:\nVuoi che l'app si riduca ad icona e termini i download in\nautonomia per poi chiudersi?";
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, str, ButtonType.YES, ButtonType.NO);
                alert.setHeaderText(null);
                alert.setTitle("Conferma uscita");

                ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

                if (result == ButtonType.YES) {
                    e.consume();
                    stage.setIconified(true);
                    downloadTrovati = downloadInCorso;
                    while(downloadTrovati > 0){
                        downloadTrovati = 0;
                        for(int i = 0; i < AnimeDownloader.downloadThredStop.size(); i++)
                            if(!AnimeDownloader.downloadThredStop.get(i))
                                downloadTrovati++;
                    }
                    System.exit(0);
                } else {
                    System.exit(0);
                }
            }
        });

        stage.show();
    }
}