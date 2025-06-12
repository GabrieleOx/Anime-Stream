package com.me;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

public class EpisodeFinder {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        //OnePiece_Ep_001_ITA.mp4
        String baseUrl = "https://srv30.sake.streampeaker.org/DDL/ANIME/OnePieceITA/";
        ArrayList<String> episodes = new ArrayList<>();
        int i = 1;
        PrintStream originalOut = System.out;
        System.setOut(null);
        while(true){
            String name = nameComposer(i, baseUrl);
            if(!isPresent(name))
                break;
            else {
                episodes.add(name.substring(baseUrl.length(), name.length()));
            }
            i++;
        }
        System.setOut(originalOut);
        System.out.println(episodes);
    }

    public static boolean isPresent(String url) throws IOException, InterruptedException, ExecutionException{
        boolean ret [] = new boolean[1];
        changeRet(ret, false);
        try (AsyncHttpClient client = Dsl.asyncHttpClient()) {

            client.prepareHead(url).execute().toCompletableFuture().thenAccept(response -> {
                int statusCode = response.getStatusCode();
                if (statusCode == 200) {
                    changeRet(ret, true);
                } else changeRet(ret, false);
            }).join();  // Aspetta che finisca la richiesta async
        }
        return ret[0];
    }

    private static void changeRet(boolean [] arr, boolean val){
        arr[0] = val;
    }

    private static String nameComposer(int epNumber, String baseUrl){
        StringBuilder nEpisode = new StringBuilder();
        nEpisode.append(Math.abs(epNumber));
        int len = nEpisode.length();
        nEpisode.delete(0, len);
        nEpisode.append("_Ep_");
        if(len == 1){
            nEpisode.append(0);
            nEpisode.append(0);
            nEpisode.append(epNumber);
        }else if(len == 2){
            nEpisode.append(0);
            nEpisode.append(epNumber);
        }else nEpisode.append(epNumber);

        int slash = 0, urlLen = baseUrl.length(), j = urlLen;
        while(slash < 2){
            j--;
            if(baseUrl.charAt(j) == '/')
                slash++;
        }
        String serie = baseUrl.substring(j+1, urlLen-1), ep;
        boolean ita = serie.substring(serie.length() - 3, serie.length()).equals("ITA");
        
        if(ita){
            nEpisode.append("_ITA");
            ep = serie.substring(0, serie.length() - 3) + nEpisode + ".mp4";
        }else ep = serie + nEpisode + ".mp4";

        return baseUrl + ep;
    }
}

