package com.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

public class Controller {
    public static ArrayList<String> anime = new ArrayList<>(), episodi = null;
    public static ArrayList<Integer> nEpisodes = new ArrayList<>();
    public static ArrayList<Boolean> abslouteITA = new ArrayList<>();

    @FXML
    private ScrollPane scrollEpisodi, scrollAnime;
    private TreeView<File> alberoCartella;

    public void loadAnimeList(char slash) throws IOException {
        scrollAnime.setContent(getAnimeToPlace(slash));
    }

    private static FlowPane getAnimeToPlace(char slash) throws IOException{
        AnimeFinder.getAnime(slash, anime, nEpisodes, abslouteITA);
        FlowPane flussoAnime = new FlowPane(Orientation.VERTICAL);
        flussoAnime.setPrefHeight(14 * (anime.size() * 2));
        flussoAnime.setPrefWidth(205);
        flussoAnime.setVgap(10);
        ToggleGroup animeGroup = new ToggleGroup();

        for(String singoloAnime : anime){
            RadioButton r = new RadioButton(EpisodeFinder.getAnimeName(singoloAnime));
            r.setToggleGroup(animeGroup);
            r.setFont(new Font("Comic Sans MS", 12));
            flussoAnime.getChildren().add(r);
        }
        return flussoAnime;
    }
}