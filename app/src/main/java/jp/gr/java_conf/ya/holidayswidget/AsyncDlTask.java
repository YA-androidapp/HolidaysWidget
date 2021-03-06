package jp.gr.java_conf.ya.holidayswidget; // Copyright (c) 2018 YA <ya.androidapp@gmail.com> All rights reserved.

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public final class AsyncDlTask extends AsyncTask<URL, Integer, String[]> {
    private AsyncCallback _asyncCallback = null;

    public interface AsyncCallback {
        void onPreExecute();

        void onPostExecute(String[] result);

        void onProgressUpdate(int progress);

        void onCancelled();
    }

    public AsyncDlTask(AsyncCallback asyncCallback) {
        this._asyncCallback = asyncCallback;
    }

    @Override
    protected String[] doInBackground(URL... urls) {
        final int count = urls.length;
        final String[] results = new String[count];
        Arrays.fill(results, "");

        for (int i = 0; i < count; i++) {
            results[i] = downloadText(urls[i]);
            publishProgress((int) ((i / (float) count) * 100));
            if (isCancelled())
                break;
        }
        return results;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this._asyncCallback.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this._asyncCallback.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        this._asyncCallback.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        this._asyncCallback.onCancelled();
    }

    protected String downloadText(final URL url) {
        try {
            Log.v("Hol", url.toString());

            final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Language", "jp");
            httpURLConnection.connect();

            final InputStream inputStream = httpURLConnection.getInputStream();
            final InputStreamReader objReader = new InputStreamReader(inputStream, "Shift_JIS");
            final BufferedReader bufferedReader = new BufferedReader(objReader);
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String utfLine = new String(line.getBytes(), "UTF-8");
                sb.append(utfLine).append("\n");
            }
            final String result = sb.toString();
            inputStream.close();

            return result;
        } catch (final IOException e) {
        }
        return "";
    }

}
