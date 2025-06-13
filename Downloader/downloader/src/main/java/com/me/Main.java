package com.me;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) throws Exception {
        ArrayList<String> anime = new ArrayList<>(), episodi;
        ArrayList<Integer> nEpisodes = new ArrayList<>();
        ArrayList<Boolean> abslouteITA = new ArrayList<>();
        int nEp, scelto;
        char c;
        String s;

        if(System.getProperty("os.name").equals("Windows"))
            c = '\\';
        else c = '/';

        final char slash = c;

        AnimeFinder.getAnime(slash, anime, nEpisodes, abslouteITA);

        System.out.println("Scegli un anime tra i presenti:");
        for(int i = 0; i < anime.size(); i++)
            System.out.println((i+1) + ") " + EpisodeFinder.getAnimeName(anime.get(i)) + ':');

        try (Scanner scan = new Scanner(System.in)) {

            do
                scelto = scan.nextInt();
            while(scelto < 1 || scelto > anime.size());
            scelto--;

            episodi = EpisodeFinder.getEpisodeList(slash, scelto, anime.get(scelto), nEpisodes.get(scelto), abslouteITA.get(scelto));

            System.out.println("Episodi disponibili:");
            for(String str : episodi)
                System.out.println(getSingleEp(str));
            
            System.out.println("Scegli un episodio:");
            do{
                nEp = scan.nextInt();
            }while(nEp < 1 || nEp > episodi.size());
            scan.nextLine();
        }

        File cartella = new File(System.getProperty("user.dir") + slash + "ANIME" + slash);
            if(!cartella.exists())
                cartella.mkdir();

        Download.videoDownloader(episodi.get(nEp-1), cartella.getAbsolutePath() + slash);
    }

    private static String getSingleEp(String episodeUrl){
        int j = episodeUrl.length()-4;
        while(episodeUrl.charAt(j-1) != '/')
            j--;
        return episodeUrl.substring(j, episodeUrl.length() - 4);
    }
}
