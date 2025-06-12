package com.me;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

public class EpisodeFinder {

    public static ArrayList<String> getEpisodeList(Scanner scan) throws IOException, ExecutionException, InterruptedException{
        String [] baseUrl = new String[3];
        baseUrl[0] = "https://srv30.sake.streampeaker.org/DDL/ANIME/OnePieceITA/";
        baseUrl[1] = "https://srv27.baiku.streampeaker.org/DDL/ANIME/KuroshitsujiMidoriNoMajo-hen/";
        baseUrl[2] = "https://srv23.shiro.streampeaker.org/DDL/ANIME/Kuroshitsuji/";
        ArrayList<String> episodes = new ArrayList<>();
        int i = 1, selectedUrl, n;

        System.out.println("Anime presenti:\n1)One piece ITA\n2)Black butler: Strega verde\n3)Black butler");
        do{
            selectedUrl = scan.nextInt();
        }while(selectedUrl < 1 || selectedUrl > 3);

        System.out.println("In che ordine di grandezza sono gli episodi (1 -> da 1 a 9; 10 -> da 1 a 99 ecc...)?");
        n = scan.nextInt();

        scan.nextLine();

        selectedUrl--;
        while(true){
            String name = nameComposer(i, baseUrl[selectedUrl], n);
            if(!isPresent(name))
                break;
            else {
                episodes.add(name);
            }
            i++;
        }
        System.out.println("[Episodi presenti:");
        for(String s : episodes){
            System.out.println(s + ";");
        }
        System.out.println("]");

        return episodes;
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

    private static String nameComposer(int epNumber, String baseUrl, int totEpisodi){
        StringBuilder nEpisode = new StringBuilder();
        nEpisode.append("_Ep_");
        nEpisode.append(numeroEpisodio(epNumber, totEpisodi));

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
        }else {
            nEpisode.append("_SUB_ITA");
            ep = serie + nEpisode + ".mp4";
        }

        System.out.println(baseUrl + ep);
        return baseUrl + ep;
    }

    private static String numeroEpisodio(int n, int nEpisodes){
        int nZeroes = 0, nCiphers = 0, nCopy = n;
        while(nEpisodes > 9){
            nZeroes++;
            nEpisodes /= 10;
        }
        while(nCopy > 9){
            nCiphers++;
            nCopy /= 10;
        }
        StringBuilder num = new StringBuilder();
        for(int i = 0; i < (nZeroes - nCiphers); i++)
            num.append(0);
        num.append(n);
        return num.toString();
    }
}

