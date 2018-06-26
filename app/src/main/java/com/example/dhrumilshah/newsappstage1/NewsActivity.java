package com.example.dhrumilshah.newsappstage1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    private static final int NEWS_LOADER_ID = 1;
    private static final String GUARDIANS_REQUEST_URL = "http://content.guardianapis.com/search?section=games&show-tags=contributor&format=json&lang=en&order-by=newest&show-fields=thumbnail&page-size=50&api-key=fc7e9c59-0ea7-496f-ac5c-2296be591711";
    private static final String GUARDIANS_GAMES_URL = "https://www.theguardian.com/games";
    private NewsArrayAdapter newsAdapter;
    private ListView newsListView;
    private TextView emptyStateTextView;
    private View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        emptyStateTextView = findViewById(R.id.empty_state);
        loadingIndicator = findViewById(R.id.loading_indicator);
        newsListView = findViewById(R.id.list);

        newsAdapter = new NewsArrayAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(newsAdapter);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        if(networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }else{
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(getString(R.string.no_internet_connection));
            emptyStateTextView.setVisibility(View.VISIBLE);
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = newsAdapter.getItem(position);
                if (currentNews != null) {
                    String webUrl = currentNews.getWebUrl();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    if(webUrl != null){
                        webIntent.setData(Uri.parse(webUrl));
                    }else{
                        webIntent.setData(Uri.parse(GUARDIANS_GAMES_URL));
                    }
                    startActivity(webIntent);
                }
            }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, GUARDIANS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        loadingIndicator.setVisibility(View.GONE);
        emptyStateTextView.setText(getString(R.string.no_news_found));
        newsListView.setEmptyView(emptyStateTextView);
        newsAdapter.clear();
        if(news != null && !news.isEmpty()){
            newsAdapter.addAll(news);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }
}
