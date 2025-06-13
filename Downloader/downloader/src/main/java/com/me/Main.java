package com.me;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) throws Exception {
        ArrayList<String> episodi;
        int nEp;

        try (Scanner scan = new Scanner(System.in)) {
            episodi = EpisodeFinder.getEpisodeList(scan);
            System.out.println("Scegli un episodio tra i precedentemnte visulizzati:");
            do{
                nEp = scan.nextInt();
            }while(nEp < 1 || nEp > episodi.size());
            scan.nextLine();
        }

        File cartella = new File(System.getProperty("user.dir") + "\\ANIME\\");
            if(!cartella.exists())
                cartella.mkdir();

        Download.videoDownloader(episodi.get(nEp-1), cartella.getAbsolutePath() + '\\');
    }
}
