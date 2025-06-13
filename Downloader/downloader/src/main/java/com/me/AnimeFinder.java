package com.me;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AnimeFinder {

    public static void getAnime(char slash, ArrayList<String> anime, ArrayList<Integer> nEpisodes, ArrayList<Boolean> abslouteITA) throws IOException{
        File links = new File(System.getProperty("user.dir")+slash+"folders.txt");

        if(!links.exists()){
            links.createNewFile();
        }

        try (FileInputStream olFolders = new FileInputStream(links)) {
            if(links.length() == 0){
                System.out.println("Non sono presenti anime...");
                System.exit(0);
            }else reader(olFolders, anime, nEpisodes, abslouteITA);
        }
    }

    private static void reader(FileInputStream x, ArrayList<String> arr, ArrayList<Integer> nEps, ArrayList<Boolean> absITA) throws IOException{
        StringBuilder str = new StringBuilder(), eps = new StringBuilder();
        int ch;
        char carattere;
        boolean num = false, abs = false;
        while((ch = x.read()) != -1){
            carattere = (char) ch;
            if(carattere == '\n'){
                arr.add(str.toString());
                nEps.add(Integer.valueOf(eps.toString().trim()));
                absITA.add(abs);
                str.delete(0, str.length());
                eps.delete(0, eps.length());
                num = false;
                abs = false;
            }else{
                if(carattere == ' '){
                    num = true;
                    continue;
                }

                if(num && carattere == '@'){
                    abs = true;
                    continue;
                }
                
                if(num)
                    eps.append(carattere);
                else str.append(carattere);
            }
        }
        arr.add(str.toString());
        nEps.add(Integer.valueOf(eps.toString().trim()));
    }
    
}
