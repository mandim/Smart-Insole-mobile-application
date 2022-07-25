package info.smartinsole.sqlite.http_client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;

public class HttpsPostClient {

    private UploadTask mUploadTask;

    private Context context;
    private String https_url;
    private JsonObject postData;


    public HttpsPostClient(Context context, String https_url) {
        this.context = context;
        this.https_url = https_url;
    }

    public void setJSONObject(JsonObject postData){
        this.postData = postData;
    }
    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startUpload() {
        cancelUpload();
        mUploadTask = new UploadTask();
        mUploadTask.execute(https_url);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing UploadTask execution.
     */
    public void cancelUpload() {
        if (mUploadTask != null) {
            mUploadTask.cancel(true);
            mUploadTask = null;
        }
    }

    /**
     * Implementation of AsyncTask that runs a network operation on a background thread.
     */
    private class UploadTask extends AsyncTask<String, Integer, UploadTask.Result> {

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the
         * upload task has completed, either the result value or exception can be a non-null
         * value. This allows you to pass exceptions to the UI thread that were thrown during
         * doInBackground().
         */
        class Result {
            public String mResultValue;
            public Exception mException;

            public Result(String resultValue) {
                mResultValue = resultValue;
            }

            public Result(Exception exception) {
                mException = exception;
            }
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                cancel(true);
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected Result doInBackground(String... urls) {
            Result result = null;
            if (!isCancelled() && urls != null && urls.length > 0) {
                String urlString = urls[0];
                try {
                    URL url = new URL(urlString);
                    upload(url);
                    result = new Result("DONE");
                } catch (Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }

        /**
         * Send Callback a progress update.
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * Updates the Callback with the result.
         */
        @Override
        protected void onPostExecute(Result result) {
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(Result result) {
        }

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it uploads the "body" in JSON form. Otherwise,
         * it will throw an IOException.
         */
        public void upload(URL url) throws IOException {
            HttpsURLConnection con = null;
            OutputStream out = null;
            try {
                con = (HttpsURLConnection) url.openConnection();

                // set Timeout
                con.setConnectTimeout(5000);
                // Set the Request Method
                con.setRequestMethod("POST");
                // Set the Request Content-Type Header Parameter
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                // Set Response Format Type
                con.setRequestProperty("Accept", "application/json");
                // Ensure the Connection Will Be Used to Send Content
                con.setDoOutput(true);
                // Open communications link (network traffic occurs here).
                //con.connect();

                int responseCode = con.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

                out = con.getOutputStream();

                if (out != null) {
                    // Converts Stream to String with max length of 500.
                    byte[] input = postData.toString().getBytes(StandardCharsets.UTF_8);
                    out.write(input, 0, input.length);
                    out.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (out != null) {
                    out.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            }
        }

        private void printHttpsCert(HttpsURLConnection con) {
            if (con != null) {
                try {
                    System.out.println("Response Code : " + con.getResponseCode());
                    System.out.println("Cipher Suite : " + con.getCipherSuite());
                    System.out.println("\n");

                    Certificate[] certs = con.getServerCertificates();
                    for (Certificate cert : certs) {
                        System.out.println("Cert Type : " + cert.getType());
                        System.out.println("Cert Hash Code : " + cert.hashCode());
                        System.out.println("Cert Public Key Algorithm : "
                                + cert.getPublicKey().getAlgorithm());
                        System.out.println("Cert Public Key Format : "
                                + cert.getPublicKey().getFormat());
                        System.out.println("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}