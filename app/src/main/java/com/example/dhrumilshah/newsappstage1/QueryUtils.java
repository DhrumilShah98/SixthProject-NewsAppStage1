package com.example.dhrumilshah.newsappstage1;

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

class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int OK_RESPONSE_CODE = 200;

    private QueryUtils() {
    }

    static List<News> fetchNewsData(String queryUrl, Context context) {
        URL url = createUrl(queryUrl, context);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.http_request_error_message), e);
        }
        return extractNews(jsonResponse, context);
    }

    private static URL createUrl(String queryUrl, Context context) {
        URL url = null;
        try {
            url = new URL(queryUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_building_url), e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url, Context context) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod(context.getString(R.string.method_GET));
            urlConnection.connect();

            if (urlConnection.getResponseCode() == OK_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream, context);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.error_response_code_message) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_retrieving_json_result), e);
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

    private static String readFromStream(InputStream inputStream, Context context) throws IOException {
        StringBuilder outputString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader isr = new InputStreamReader(inputStream, Charset.forName(context.getString(R.string.utf_8)));
            BufferedReader br = new BufferedReader(isr);
            String currentLine = br.readLine();
            while (currentLine != null) {
                outputString.append(currentLine);
                currentLine = br.readLine();
            }
        }
        return outputString.toString();
    }

    private static List<News> extractNews(String jsonResponse, Context context) {
        List<News> news = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.has(context.getString(R.string.response_object))){
                JSONObject responseJSONObject = jsonObject.getJSONObject(context.getString(R.string.response_object));
                if(responseJSONObject.has(context.getString(R.string.results_array))) {
                    JSONArray resultsJSONArray = responseJSONObject.getJSONArray(context.getString(R.string.results_array));
                    for (int i = 0; i < resultsJSONArray.length(); i++) {
                        JSONObject currentJSONObject = resultsJSONArray.getJSONObject(i);
                        String title;
                        if(currentJSONObject.has(context.getString(R.string.title))) {
                            title = currentJSONObject.getString(context.getString(R.string.title));
                        }else{
                            title = null;
                        }
                        String section;
                        if(currentJSONObject.has(context.getString(R.string.section_name))) {
                            section = currentJSONObject.getString(context.getString(R.string.section_name));
                        }else{
                            section = null;
                        }
                        String publicationDateTime;
                        if(currentJSONObject.has(context.getString(R.string.web_publication_date_and_time))) {
                            publicationDateTime = currentJSONObject.getString(context.getString(R.string.web_publication_date_and_time));
                        }else{
                            publicationDateTime = null;
                        }
                        String webUrl;
                        if(currentJSONObject.has(context.getString(R.string.web_url))) {
                            webUrl = currentJSONObject.getString(context.getString(R.string.web_url));
                        }else{
                            webUrl = null;
                        }
                        String thumbnail;
                        if(currentJSONObject.has(context.getString(R.string.fields_object))) {
                            JSONObject currentFieldsObject = currentJSONObject.getJSONObject(context.getString(R.string.fields_object));
                            if(currentFieldsObject.has(context.getString(R.string.thumbnail))) {
                                thumbnail = currentFieldsObject.getString(context.getString(R.string.thumbnail));
                            }else{
                                thumbnail = null;
                            }
                        }
                        else{
                            thumbnail = null;
                        }
                        ArrayList<String> authors = new ArrayList<>();
                        if(currentJSONObject.has(context.getString(R.string.tags_array))) {
                            JSONArray currentTagsArray = currentJSONObject.getJSONArray(context.getString(R.string.tags_array));
                            if (currentTagsArray == null || currentTagsArray.length() == 0) {
                                authors = null;
                            } else {
                                for (int j = 0; j < currentTagsArray.length(); j++) {
                                    JSONObject currentObjectInTags = currentTagsArray.getJSONObject(j);
                                    authors.add(currentObjectInTags.getString(context.getString(R.string.web_title_in_tags_array)));
                                }
                            }
                        }else{
                            authors = null;
                        }
                        news.add(new News(title, section, publicationDateTime, webUrl, thumbnail, authors));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_parsing_news_json_result), e);
        }
        return news;
    }
}