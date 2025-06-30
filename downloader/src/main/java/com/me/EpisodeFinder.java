package com.me;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EpisodeFinder {

    public static ArrayList<String> getEpisodeList(char slash, String animeScelto, int indiceEpisodi, boolean itaAssoluto, int start) throws IOException, ExecutionException, InterruptedException{
        final OkHttpClient client = new OkHttpClient();
        
        int i = start;
        ArrayList<String> episodes = new ArrayList<>();

        while(true){
            String name = nameComposer(i, animeScelto, indiceEpisodi, itaAssoluto);
            if(!isPresent(name, client))
                break;
            else {
                episodes.add(name);
            }
            i++;
            if(i > start + 100)
                break;
        }

        return episodes;
    }

    public static boolean isPresent(String url, OkHttpClient client) throws IOException, InterruptedException, ExecutionException{
        boolean ret = false;

        Call call = client.newCall(new Request.Builder().url(url).build()); //è una richiesta sincrona.....
        try (Response res = call.execute()) {
            int statusCode = res.code();
            ret = statusCode == 200;
        }

        return ret;
    }

    private static String nameComposer(int epNumber, String baseUrl, int totEpisodi, boolean absITA){
        StringBuilder nEpisode = new StringBuilder();
        nEpisode.append("_Ep_");
        nEpisode.append(numeroEpisodio(epNumber, totEpisodi));

        String serie = getAnimeName(baseUrl), ep;
        boolean ita = serie.substring(serie.length() - 3, serie.length()).equals("ITA"), subIta = serie.substring(serie.length() - 6, serie.length()).equals("SUBITA");
        
        if(absITA){
            nEpisode.append("_ITA");
            ep = serie + nEpisode + ".mp4";
        }else if(ita && !subIta){
            nEpisode.append("_ITA");
            ep = serie.substring(0, serie.length() - 3) + nEpisode + ".mp4";
        }else if(subIta){
            nEpisode.append("_SUB_ITA");
            ep = serie.substring(0, serie.length()-6) + nEpisode + ".mp4";
        }else {
            nEpisode.append("_SUB_ITA");
            ep = serie + nEpisode + ".mp4";
        }

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

    public static String getAnimeName(String url){
        int slash = 0, urlLen = url.length(), j = urlLen;
        while(slash < 2){
            j--;
            if(url.charAt(j) == '/')
                slash++;
        }
        return url.substring(j+1, urlLen-1);
    }
}

