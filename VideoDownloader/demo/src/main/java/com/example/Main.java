package com.example;

import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.Response;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Inserisci l'URL del video:");
        String fileUrl = scan.nextLine().trim();
        scan.close();

        AsyncHttpClientConfig config = Dsl.config()
            .setConnectTimeout(30_000)
            .setReadTimeout(120_000)
            .setRequestTimeout(600_000)
            .build();

        AsyncHttpClient client = Dsl.asyncHttpClient(config);

        FileOutputStream opStream = new FileOutputStream("C:\\Users\\gabriele.ossola\\Desktop\\video.mp4");
        AtomicLong downloadedBytes = new AtomicLong(0);
        CompletableFuture<Void> downloadComplete = new CompletableFuture<>();

        client.prepareGet(fileUrl).execute(new AsyncCompletionHandler<Void>() {
            long totalBytes = -1;

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                opStream.getChannel().write(bodyPart.getBodyByteBuffer());
                long current = downloadedBytes.addAndGet(bodyPart.length());
                if (totalBytes > 0) {
                    int percent = (int) ((current * 100) / totalBytes);
                    System.out.print("\rDownload: " + percent + "%");
                } else {
                    System.out.print("\rScaricati: " + current + " byte");
                }
                return State.CONTINUE;
            }

            @Override
            public Void onCompleted(Response response) throws Exception {
                String contentLength = response.getHeader("Content-Length");
                if (contentLength != null) {
                    totalBytes = Long.parseLong(contentLength);
                }
                System.out.println("\nDownload completato.");
                opStream.close();
                downloadComplete.complete(null);
                return null;
            }

            @Override
            public void onThrowable(Throwable t) {
                System.err.println("\nErrore durante il download: " + t.getMessage());
                try {
                    opStream.close();
                } catch (Exception ignored) {}
                downloadComplete.completeExceptionally(t);
            }
        });

        // ✅ Qui attendi il completamento e poi chiudi il client in modo sicuro
        try {
            downloadComplete.join(); // blocca il thread principale finché non finisce
        } finally {
            client.close(); // CHIUSURA SICURA
        }

        System.out.println("Risorse chiuse correttamente.");
    }
}
