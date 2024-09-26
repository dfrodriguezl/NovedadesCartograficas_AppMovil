package co.gov.dane.novedades;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileDownloader {

    private ExecutorService executorService;
    private Context context;
    private Handler handler;

    public FileDownloader(Context context) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void downloadFile(String fileUrl, String destinationPath,
                             String fileName,
                             FileDownloadListener listener) {

        Boolean existNetworkConnection = isNetworkAvailable();

        if (existNetworkConnection) {
            Future<?> future = executorService.submit(() -> {
                InputStream input = null;
                FileOutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(fileUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

//                expect HTTP 200 OK, so we don´t mistakenly save error report
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException("Servidor retornó HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    }

                    int fileLength = connection.getContentLength();

//                download the file
                    input = new BufferedInputStream(connection.getInputStream());
                    output = new FileOutputStream(new File(destinationPath, fileName));

                    byte[] data = new byte[1024];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
//                        Calculate progress
                        int progress = (int) (total * 100 / fileLength);
//                        Update progress on UI thread
                        handler.post(() -> {
                            if (listener != null) {
                                listener.onProgressUpdate(progress);
                            }
                        });

                    }
                    output.flush();

//                notify listener of successful download
                    if (listener != null) {
                        listener.onDownloadComplete();
                    }

                } catch (Exception e) {
                    if (listener != null) {
                        e.printStackTrace();
                        handler.post(() -> {
                            listener.onDownloadFailed(e.getMessage());
                        });
                    }
                } finally {
//                close connections and streams
                    try {
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException ignored) {
                    }

                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            });
        } else {
            if (listener != null) {
                listener.onDownloadFailed("No hay conexión a internet");
            }
        }

    }

    public void shutdown() {
        executorService.shutdown();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public interface FileDownloadListener {
        void onDownloadComplete();

        void onDownloadFailed(String errorMessage);

        void onProgressUpdate(int progress);
    }
}
