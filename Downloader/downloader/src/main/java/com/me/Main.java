package com.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



public class Main {
    public static volatile ArrayList<Boolean> downloadThredStop = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);
        ArrayList<Thread> downloadThreads = new ArrayList<>(), stopThreads = new ArrayList<>();
        ArrayList<String> anime = new ArrayList<>(), episodi = null;
        ArrayList<Integer> nEpisodes = new ArrayList<>();
        ArrayList<Boolean> abslouteITA = new ArrayList<>();
        int scelto, oldScelto = -1;
        char c;
        String os = System.getProperty("os.name");

        if(os.contains("Windows"))
            c = '\\';
        else c = '/';
        final char slash = c;

        AnimeFinder.getAnime(slash, anime, nEpisodes, abslouteITA);

        File cartella = new File(System.getProperty("user.dir") + slash + "ANIME" + slash), specific = null;
        if(!cartella.exists())
            cartella.mkdir();

        do {

            if(os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            System.out.println("Scegli un anime tra i presenti:");
            for(int i = 0; i < anime.size(); i++)
                System.out.println((i+1) + ") " + EpisodeFinder.getAnimeName(anime.get(i)) + ':');

            do
                scelto = scan.nextInt();
            while(scelto < 1 || scelto > anime.size());
            scelto--;

            if(scelto != oldScelto){
                episodi = EpisodeFinder.getEpisodeList(slash, scelto, anime.get(scelto), nEpisodes.get(scelto), abslouteITA.get(scelto));
                specific = new File(cartella.getAbsolutePath() + slash + EpisodeFinder.getAnimeName(anime.get(scelto)) + slash);
                if(!specific.exists())
                    specific.mkdir();
            }

            addDownload(downloadThreads, stopThreads, episodi, specific, episodeChooser(scan, episodi), slash);
            oldScelto = scelto;

            System.out.println("Inserisci 1 per iniziare altri download\noppure altro per uscire:");
        } while (scan.nextInt() == 1);

        int downloadInCorso = 0, downloadTrovati;
        for(int i = 0; i < downloadThredStop.size(); i++)
            if(!downloadThredStop.get(i))
                downloadInCorso++;
            
        if(downloadInCorso > 0){
            System.out.println("Ci sono ancora " + downloadInCorso + " download in corso:\nAppena finiranno vedrai un messaggio relativo:");
            downloadTrovati = downloadInCorso;
            while(downloadTrovati > 0){
                downloadTrovati = 0;
                for(int i = 0; i < downloadThredStop.size(); i++)
                    if(!downloadThredStop.get(i))
                        downloadTrovati++;
                System.out.print("\rDownload in corso.  ");
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.print("\rDownload in corso.. ");
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.print("\rDownload in corso...");
                TimeUnit.MILLISECONDS.sleep(500);
            }
            System.out.println("\nTutti i download sono stati completati.\n");
        }
    }

    private static int episodeChooser(Scanner scan, ArrayList<String> episodes){
        boolean ultimoGiro = false, goOn = false;
        int start = 0, end = 50, indice, max = episodes.size(), selected;

        do {
            if(max <= end){
                end = max;
                ultimoGiro = true;
                System.out.println("Vengono mostrati gli ultimi episodi disponibili:");
            }

            System.out.println("\"Episodi da " + (start+1) + " a " + end + ':');
            indice = start;
            String x;
            while(indice < end){
                x = episodes.get(indice);
                System.out.println(getSingleEp(x));
                indice++;
            }
            if(!ultimoGiro)
                System.out.println("Se l'episodio ricercato è tra quelli mostrati inserire il numero di quello richiesto altrimenti un numero esterno:");
            else System.out.println("Inserire il numeor dell'episodio da scaricare, uno esterno per uscire:");
            selected = scan.nextInt();

            if(selected < start+1 || selected > end){
                start = end;
                end += 50;
                goOn = true;
            }else goOn = false;
            
        } while (goOn && !ultimoGiro);

        if(goOn && ultimoGiro)
            System.exit(0);

        scan.nextLine();
        return selected;
    }

    private static String getSingleEp(String episodeUrl){
        int j = episodeUrl.length()-4;
        while(episodeUrl.charAt(j-1) != '/')
            j--;
        return episodeUrl.substring(j, episodeUrl.length() - 4);
    }

    private static void addDownload(ArrayList<Thread> start, ArrayList<Thread> stop, ArrayList<String> episodi, File cartella, int nEp, char slash){
        downloadThredStop.add(false);
        start.add(new Thread(() ->{
            final int iStop = downloadThredStop.size()-1;
            Download d = new Download(episodi.get(nEp-1), cartella.getAbsolutePath() + slash);
            try {
                d.scarica(iStop);
            } catch (IOException e) {}
        }));
        start.get(downloadThredStop.size()-1).start();
        stop.add(new Thread(() -> {
            final int iS = downloadThredStop.size()-1;
            while(!downloadThredStop.get(iS));
            start.get(iS).interrupt();
        }));
        stop.get(downloadThredStop.size()-1).start();
    }
}
