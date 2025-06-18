package com.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

public class Controller {
    public static ArrayList<String> anime = new ArrayList<>(), episodi = null;
    public static ArrayList<Integer> nEpisodes = new ArrayList<>(), startVals = new ArrayList<>();
    public static ArrayList<Boolean> abslouteITA = new ArrayList<>();
    private static Thread findEpisodes = new Thread();

    @FXML
    private volatile ScrollPane scrollEpisodi, scrollAnime;
    @FXML
    private TreeView<String> alberoCartella;

    public void loadEpisodeList(char slash, int selected, ArrayList<Thread> downloadThreads, ArrayList<Thread> stopThreads) throws IOException, ExecutionException, InterruptedException{

        File cartella = new File(System.getProperty("user.dir") + slash + "ANIME" + slash);
        if(!cartella.exists())
            cartella.mkdir();
        File specific = new File(cartella.getAbsolutePath() + slash + EpisodeFinder.getAnimeName(anime.get(selected)) + slash);
        if(!specific.exists())
            specific.mkdir();

        TreeItem<String> root = new TreeItem<>(EpisodeFinder.getAnimeName(anime.get(selected)), new ImageView(new Image("/folder.png")));
        for(String f : specific.list())
            root.getChildren().add(new TreeItem<>(f));

        alberoCartella.setRoot(root);
         
        ImageView loading = new ImageView(new Image("/loading.gif"));
        loading.setFitWidth(scrollEpisodi.getWidth());
        loading.setPreserveRatio(true);
        scrollEpisodi.setContent(loading);

        int [] starter = {startVals.get(selected)};

        TextInputDialog episodioRicercato = new TextInputDialog("0");
        ImageView alert = new ImageView(new Image("/alert.png"));
        alert.setFitWidth(120);
        alert.setPreserveRatio(true);
        episodioRicercato.setGraphic(alert);
        episodioRicercato.setResizable(false);
        episodioRicercato.setTitle("Scelta Episodio");
        episodioRicercato.setHeaderText("Inserisci il numero dell'episodio che stai cercando\nper caricare solo quelli vicini oppure 0 per\nusare l'automatico");
        episodioRicercato.setContentText("Numero episodio:");

        Optional<String> result = episodioRicercato.showAndWait();

        result.ifPresent(input -> {
            int epScelto = Integer.parseInt(input.trim());
            if(epScelto > 0 && epScelto < nEpisodes.get(selected)*10)
                if(epScelto-50 > 0)
                    starter[0] = epScelto-50;
                else starter[0] = 0;
        });

        startVals.set(selected, starter[0]);

        if(findEpisodes.isAlive())
            findEpisodes.interrupt();
        
        findEpisodes = new Thread(() -> {
            FlowPane f;
            try {
                f = getEpisodesToPalce(slash, selected, downloadThreads, stopThreads, specific);
                Platform.runLater(() -> {
                    scrollEpisodi.setContent(f);
                });
            } catch (IOException | ExecutionException | InterruptedException e) {
                Image im = new Image("/fail.png");
                ImageView failedToLoad = new ImageView(im);

                failedToLoad.setFitWidth(scrollEpisodi.getWidth() - 2);
                failedToLoad.setFitHeight(scrollEpisodi.getHeight() - 2);
                
                Platform.runLater(() -> {
                    scrollEpisodi.setContent(failedToLoad);
                });
            } 
        });
        findEpisodes.start();
    }

    public void loadAnimeList(char slash, ArrayList<Thread> downloadThreads, ArrayList<Thread> stopThreads) throws IOException{
        AnimeFinder.getAnime(slash, anime, nEpisodes, abslouteITA, startVals);
        FlowPane flussoAnime = new FlowPane(Orientation.VERTICAL);
        ToggleGroup animeGroup = new ToggleGroup();

        flussoAnime.setPrefHeight(14 * (anime.size() * 2));
        flussoAnime.setPrefWidth(205);
        flussoAnime.setVgap(10);

        for(int i = 0; i < anime.size(); i++){
            RadioButton r = new RadioButton(EpisodeFinder.getAnimeName(anime.get(i)));
            r.setToggleGroup(animeGroup);
            r.setFont(new Font("Comic Sans MS", 12));
            final int index = i;
            r.setOnAction(e -> {
                try {
                    loadEpisodeList(slash, index, downloadThreads, stopThreads);
                } catch (IOException | ExecutionException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            });
            flussoAnime.getChildren().add(r);
        }
        scrollAnime.setContent(flussoAnime);
    }

    private static FlowPane getEpisodesToPalce(char slash, int scelto, ArrayList<Thread> downloadThreads, ArrayList<Thread> stopThreads, File specific) throws IOException, ExecutionException, InterruptedException{
        episodi = EpisodeFinder.getEpisodeList(slash, scelto, anime.get(scelto), nEpisodes.get(scelto), abslouteITA.get(scelto), startVals.get(scelto));
        FlowPane flussoEpisodi = new FlowPane(Orientation.VERTICAL);
        
        flussoEpisodi.setPrefWidth(330);
        flussoEpisodi.setPrefHeight(50 * (anime.size()));
        flussoEpisodi.setVgap(10);
        flussoEpisodi.setHgap(10);

        for(int i = 0; i < episodi.size(); i++){
            Button b = new Button(AnimeDownloader.getSingleEp(episodi.get(i)));
            b.setFont(new Font("Comic Sans MS", 12));
            final int index = i+1;
            b.setOnAction(e -> {
                AnimeDownloader.addDownload(downloadThreads, stopThreads, episodi, specific, index, slash);
            });
            flussoEpisodi.getChildren().add(b);
        }
        return flussoEpisodi;
    }
}