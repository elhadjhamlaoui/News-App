package com.app_republic.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    private listAdapter mAdapter;
    private static String Guardian_REQUEST_URL;
    private static final int NEWS_LOADER_ID = 1;
    private TextView empty;
    private ProgressBar loadingIndicator;
    private ArrayList<Article> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Guardian_REQUEST_URL = getString(R.string.gardien_url_base);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        empty = findViewById(R.id.empty);
        loadingIndicator = findViewById(R.id.loading);

        mAdapter = new listAdapter(this, articles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(GONE);
            empty.setText(R.string.no_internet);
        }
    }

    public static class NewsLoader extends AsyncTaskLoader<List<Article>> {
        private String mUrl;

        NewsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Article> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            return Utils.getData(mUrl, getContext());
        }
    }

    // refresh the news when pressing back button
    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(Guardian_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String topic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        uriBuilder.appendQueryParameter(getString(R.string.query_topic), topic);
        uriBuilder.appendQueryParameter(getString(R.string.query_order_by), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_key_val));
        uriBuilder.appendQueryParameter(getString(R.string.from_date), getString(R.string.from_date_val));
        uriBuilder.appendQueryParameter(getString(R.string.page_size), getString(R.string.page_size_val));
        uriBuilder.appendQueryParameter(getString(R.string.show_fields), getString(R.string.show_fields_val));
        uriBuilder.appendQueryParameter(getString(R.string.show_tags), getString(R.string.show_tags_val));
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        clearAdapter();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> list) {
        loadingIndicator.setVisibility(GONE);
        empty.setText(R.string.no_news);
        clearAdapter();
        if (list != null && !list.isEmpty()) {
            empty.setVisibility(GONE);
            articles.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void clearAdapter() {
        articles.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
