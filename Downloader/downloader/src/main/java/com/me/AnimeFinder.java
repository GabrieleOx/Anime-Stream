package com.me;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AnimeFinder {

    public static void getAnime(char slash, ArrayList<String> anime, ArrayList<Integer> nEpisodes, ArrayList<Boolean> abslouteITA, ArrayList<Integer> startValues) throws IOException{
        File links = new File(System.getProperty("user.dir")+slash+"folders.txt");

        if(!links.exists()){
            links.createNewFile();
        }

        try (FileInputStream olFolders = new FileInputStream(links)) {
            if(links.length() == 0){
                System.out.println("Non sono presenti anime...");
                System.exit(0);
            }else reader(olFolders, anime, nEpisodes, abslouteITA, startValues);
        }
    }

    private static void reader(FileInputStream x, ArrayList<String> arr, ArrayList<Integer> nEps, ArrayList<Boolean> absITA, ArrayList<Integer> starters) throws IOException{
        StringBuilder str = new StringBuilder(), eps = new StringBuilder(), starterStr = new StringBuilder();
        int ch, spaces = 0;
        char carattere;
        boolean num = false, abs = false;
        while((ch = x.read()) != -1){
            carattere = (char) ch;
            if(carattere == '\n'){
                if(starterStr.toString().trim().length() > 0){
                    if(Integer.parseInt(starterStr.toString().trim()) > 0)
                        starters.add(Integer.valueOf(starterStr.toString().trim()));
                    starters.add(1);
                }else starters.add(1);

                arr.add(str.toString());
                nEps.add(Integer.valueOf(eps.toString().trim()));
                absITA.add(abs);
                str.delete(0, str.length());
                eps.delete(0, eps.length());
                starterStr.delete(0, starterStr.length());
                num = false;
                abs = false;
                spaces = 0;
            }else{
                if(carattere == ' '){
                    num = true;
                    spaces++;
                    continue;
                }

                if(num && carattere == '@'){
                    abs = true;
                    
                    continue;
                }

                if(spaces > 1 && (carattere >= '0' && carattere <= '9')){ // da sistemare per onePieceSubIta
                    starterStr.append(carattere);
                    continue;
                }
                
                if(num)
                    eps.append(carattere);
                else str.append(carattere);
            }
        }
        arr.add(str.toString());
        nEps.add(Integer.valueOf(eps.toString().trim()));
        absITA.add(abs);

        if(starterStr.toString().trim().length() > 0){
            if(Integer.parseInt(starterStr.toString().trim()) > 0)
                starters.add(Integer.valueOf(starterStr.toString().trim()));
            starters.add(1);
        }else starters.add(1);
    }
    
}
