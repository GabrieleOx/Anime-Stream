package com.me;

import java.util.ArrayList;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) throws Exception {
        String filePath;
        ArrayList<String> episodi;
        int nEp;

        try (Scanner scan = new Scanner(System.in)) {
            episodi = EpisodeFinder.getEpisodeList(scan);
            System.out.println("Scegli un episodio tra i precedentemnte visulizzati:");
            do{
                nEp = scan.nextInt();
            }while(nEp < 1 || nEp > episodi.size());
            scan.nextLine();
            System.out.println("""
                Inserisci la posizione (percorso della cartella) dove verr\u00e0 salvato il file:
                Inserisci anche il nome del file alla fine se vuoi dargli un particolare nome con estensione ".mp4":
            """);
            filePath = scan.nextLine().trim();
        }

        Download.videoDownloader(episodi.get(nEp-1), filePath);
    }
}
