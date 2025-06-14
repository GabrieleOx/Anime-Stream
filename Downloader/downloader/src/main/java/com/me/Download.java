package com.me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.Response;

public class Download {
    private final String fileUrl, filePath;

    public Download(String fileUrl, String filePath){
        this.fileUrl = fileUrl;
        this.filePath = filePath;
    }
    
    public void scarica(int indiceStop) throws IOException{
        AsyncHttpClientConfig config = Dsl.config()
            .setConnectTimeout(30_000)
            .setReadTimeout(120_000)
            .setRequestTimeout(600_000)
            .build();

        AsyncHttpClient client = Dsl.asyncHttpClient(config);

        FileOutputStream opStream = new FileOutputStream(getFile(this.fileUrl, this.filePath));
        AtomicLong downloadedBytes = new AtomicLong(0);
        CompletableFuture<Void> downloadComplete = new CompletableFuture<>();

        client.prepareGet(this.fileUrl).execute(new AsyncCompletionHandler<Void>() {
            long totalBytes = -1;

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                opStream.getChannel().write(bodyPart.getBodyByteBuffer());
                long current = downloadedBytes.addAndGet(bodyPart.length());
                //System.out.print("\rScaricati: " + current + " bytes");
                return State.CONTINUE;
            }

            @Override
            public Void onCompleted(Response response) throws Exception {
                String contentLength = response.getHeader("Content-Length");
                if (contentLength != null) {
                    totalBytes = Long.parseLong(contentLength);
                }
                //System.out.println("\nDownload completato.");
                Main.downloadThredStop.set(indiceStop, true);
                opStream.close();
                downloadComplete.complete(null);
                System.exit(0);
                return null;
            }

            @Override
            public void onThrowable(Throwable t) {
                System.err.println("\nErrore durante il download: " + t.getMessage());
                try {
                    opStream.close();
                } catch (IOException ignored) {}
                downloadComplete.completeExceptionally(t);
            }
        });

        // Qui attendi il completamento e poi chiudi il client in modo sicuro
        /*try {
            downloadComplete.join(); // blocca il thread principale finché non finisce
        } finally {
            client.close(); // CHIUSURA SICURA
        }

        System.out.println("Risorse chiuse correttamente.");*/
    }

    private static File getFile(String url, String path){
        if(path.substring(path.length() - 4, path.length()).equals(".mp4"))
            return new File(path);

        StringBuilder fileName = new StringBuilder();
        int posSlash = url.length() - 1;
        while(true){
            if(url.charAt(posSlash) != '/')
                fileName.append(url.charAt(posSlash));
            else break;
            posSlash--;
        }
        fileName.reverse();

        return new File(path + fileName);
    }
}
