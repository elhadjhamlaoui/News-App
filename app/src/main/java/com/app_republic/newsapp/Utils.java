package com.app_republic.newsapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class Utils {

    private static String LOG_TAG;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int RESPONSE_CODE_SUCCESS = 200;

    /**
     * Query the Guardian dataset and return a list of {@link Article} s.
     */
    static List<Article> getData(String requestUrl, Context context) {
        LOG_TAG = context.getString(R.string.utils_tag);
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractArticles(jsonResponse, context);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url, Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(context.getString(R.string.method_get));
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == RESPONSE_CODE_SUCCESS) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream, context);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.error_response_code) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_occurred), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream, Context context) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(context.getString(R.string.utf8)));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Article> extractArticles(String jsonResponse, Context context) {

        ArrayList<Article> list = new ArrayList<>();

        try {

            JSONObject jsonObj = new JSONObject(jsonResponse);
            JSONObject response = jsonObj.getJSONObject(context.getString(R.string.response));
            JSONArray results = response.getJSONArray(context.getString(R.string.results));
            int length = results.length();

            for (int i = 0; i < length; i++) {
                JSONObject obj = results.getJSONObject(i);

                String section = obj.getString(context.getString(R.string.sectionName));
                String date = "";
                if (obj.has(context.getString(R.string.webPublicationDate))) {
                    date = obj.getString(context.getString(R.string.webPublicationDate));
                }
                String title = obj.getString(context.getString(R.string.webTitle));
                String url = obj.getString(context.getString(R.string.webUrl));
                JSONObject fields = obj.getJSONObject(context.getString(R.string.fields));
                String description = fields.getString(context.getString(R.string.trailText));
                String authorName = "";
                JSONArray tags = obj.getJSONArray(context.getString(R.string.tags));
                if (tags.length() != 0) {
                    JSONObject author = tags.getJSONObject(0);
                    if (author.has(context.getString(R.string.webTitle))) {
                        authorName = author.getString(context.getString(R.string.webTitle));
                    }
                }
                Article article = new Article(title, description, authorName, date, section, url);
                list.add(article);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}