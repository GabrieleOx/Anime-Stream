package com.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

public class Controller {
    public static ArrayList<String> anime = new ArrayList<>(), episodi = null;
    public static ArrayList<Integer> nEpisodes = new ArrayList<>();
    public static ArrayList<Boolean> abslouteITA = new ArrayList<>();

    @FXML
    private ScrollPane scrollEpisodi, scrollAnime;
    private TreeView<File> alberoCartella;

    public void loadEpisodeList(char slash, int selected, ArrayList<Thread> downloadThreads, ArrayList<Thread> stopThreads) throws IOException, ExecutionException, InterruptedException{
        Image i = new Image("/icona.png");
        ImageView loading = new ImageView(i);
        scrollEpisodi.setContent(loading);
        scrollEpisodi.setContent(getEpisodesToPalce(slash, selected, downloadThreads, stopThreads));
    }

    public void loadAnimeList(char slash, ArrayList<Thread> downloadThreads, ArrayList<Thread> stopThreads) throws IOException{
        AnimeFinder.getAnime(slash, anime, nEpisodes, abslouteITA);
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

    private static FlowPane getEpisodesToPalce(char slash, int scelto, ArrayList<Thread> downloadThreads, ArrayList<Thread> stopThreads) throws IOException, ExecutionException, InterruptedException{
        episodi = EpisodeFinder.getEpisodeList(slash, scelto, anime.get(scelto), nEpisodes.get(scelto), abslouteITA.get(scelto));
        FlowPane flussoEpisodi = new FlowPane(Orientation.VERTICAL);
        
        flussoEpisodi.setPrefWidth(330);
        flussoEpisodi.setPrefHeight(22 * (anime.size() * 2));
        flussoEpisodi.setVgap(10);

        File cartella = new File(System.getProperty("user.dir") + slash + "ANIME" + slash);
        if(!cartella.exists())
            cartella.mkdir();
        final File specific = new File(cartella.getAbsolutePath() + slash + EpisodeFinder.getAnimeName(anime.get(scelto)) + slash);
        if(!specific.exists())
            specific.mkdir();

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