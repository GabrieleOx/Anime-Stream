package com.me;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

public class EpisodeFinder {

    public static ArrayList<String> getEpisodeList(Scanner scan) throws IOException, ExecutionException, InterruptedException{
        ArrayList<String> episodes = new ArrayList<>(), anime = new ArrayList<>();
        ArrayList<Integer> nEpisodes = new ArrayList<>();
        int i = 1, selectedUrl;
        File links = new File(System.getProperty("user.dir")+"\\folders.txt");

        if(!links.exists()){
            links.createNewFile();
        }

        try (FileInputStream olFolders = new FileInputStream(links)) {
            if(links.length() == 0){
                System.out.println("Non sono presenti anime...");
                System.exit(0);
            }else reader(olFolders, anime, nEpisodes);
        }

        System.out.println("Anime presenti:");
        for(String url : anime)
            System.out.println(getAnimeName(url) + ':');
        do
            selectedUrl = scan.nextInt();
        while(selectedUrl < 1 || selectedUrl > anime.size());

        scan.nextLine();

        selectedUrl--;
        while(true){
            String name = nameComposer(i, anime.get(selectedUrl), nEpisodes.get(selectedUrl));
            if(!isPresent(name))
                break;
            else {
                episodes.add(name);
            }
            i++;
        }

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

        String serie = getAnimeName(baseUrl), ep;
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

    private static void reader(FileInputStream x, ArrayList<String> arr, ArrayList<Integer> nEps) throws IOException{
        StringBuilder str = new StringBuilder(), eps = new StringBuilder();
        int ch;
        char carattere;
        boolean num = false;
        while((ch = x.read()) != -1){
            carattere = (char) ch;
            if(carattere == '\n'){
                arr.add(str.toString());
                nEps.add(Integer.parseInt(eps.toString().trim()));
                str.delete(0, str.length());
                eps.delete(0, eps.length());
                num = false;
            }else{
                if(carattere == ' '){
                    num = true;
                    continue;
                }
                
                if(num)
                    eps.append(carattere);
                else str.append(carattere);
            }
        }
        arr.add(str.toString());
        nEps.add(Integer.parseInt(eps.toString().trim()));
    }

    private static String getAnimeName(String url){
        int slash = 0, urlLen = url.length(), j = urlLen;
        while(slash < 2){
            j--;
            if(url.charAt(j) == '/')
                slash++;
        }
        return url.substring(j+1, urlLen-1);
    }
}

