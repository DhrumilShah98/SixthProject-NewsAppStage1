package com.example.dhrumilshah.newsappstage1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /**
     * Query URL
     **/
    private String url;

    NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (url == null) {
            return null;
        }
        return QueryUtils.fetchNewsData(url, getContext());
    }
}
