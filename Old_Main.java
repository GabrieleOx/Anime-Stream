package com.example;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.Response;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String fileUrl;
        Scanner scan = new Scanner(System.in);
        System.out.println("Inserisci l'URL del video:");
        fileUrl = scan.nextLine().trim();
        AsyncHttpClient client = Dsl.asyncHttpClient();

        FileOutputStream opStream = new FileOutputStream("C:\\Users\\gabriele.ossola\\Desktop\\video.mp4");

        client.prepareGet(fileUrl).execute(new AsyncCompletionHandler<FileOutputStream>(){

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
                throws Exception{
                    opStream.getChannel().write(bodyPart.getBodyByteBuffer());
                    return State.CONTINUE;
                }
            
            @Override
            public FileOutputStream onCompleted(Response response)
                throws Exception{
                    return opStream;
                }
        });
    }
}
