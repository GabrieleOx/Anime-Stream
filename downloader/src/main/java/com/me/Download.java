package com.me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Download {
    private final String fileUrl, filePath;

    public Download(String fileUrl, String filePath){
        this.fileUrl = fileUrl;
        this.filePath = filePath;
    }
    
    public void scarica(int indiceStop) throws IOException{
        // 1. Configura OkHttpClient con timeout equivalenti
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        // 2. Prepara stream e variabili
        FileOutputStream opStream = new FileOutputStream(getFile(this.fileUrl, this.filePath));
        AtomicLong downloadedBytes = new AtomicLong(0);
        CompletableFuture<Void> downloadComplete = new CompletableFuture<>();

        // 3. Costruisci richiesta GET
        Request request = new Request.Builder()
                .url(this.fileUrl)
                .build();

        // 4. Avvia download asincrono
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("\nErrore durante il download: " + e.getMessage());
                AnimeDownloader.downloadThredStop.set(indiceStop, true);
                try {
                    opStream.close();
                } catch (IOException ignored) {}
                downloadComplete.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException("Risposta non valida: " + response));
                    return;
                }

                long totalBytes = -1;
                String clHeader = response.header("Content-Length");
                if (clHeader != null) {
                    totalBytes = Long.parseLong(clHeader);
                }

                try (ResponseBody body = response.body();
                    InputStream input = body.byteStream()) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = input.read(buffer)) != -1) {
                        opStream.write(buffer, 0, bytesRead);
                        downloadedBytes.addAndGet(bytesRead);
                        // System.out.print("\rScaricati: " + downloadedBytes.get() + " bytes");
                    }

                    AnimeDownloader.downloadThredStop.set(indiceStop, true);
                    opStream.close();
                    downloadComplete.complete(null);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        });

        // 5. Attesa del completamento in modo "bloccante" (come il while del tuo codice)
        try {
            downloadComplete.join();  // blocca finché non completa
        } finally {
            System.out.println("Risorse chiuse correttamente.");
        }
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
