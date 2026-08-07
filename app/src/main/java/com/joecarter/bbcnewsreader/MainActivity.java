package com.joecarter.bbcnewsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    ArrayList<String> headlines = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<Article> articles = new ArrayList<>();
    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bbc_news_reader));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close

        );

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {

            } else if (id == R.id.nav_favourite) {
                startActivity(new Intent(this, FavouritesActivity.class));
            } else if (id == R.id.nav_help) {
                startActivity(new Intent(this, HelpActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        listView = findViewById(R.id.listViewHeadlines);
        progressBar = findViewById(R.id.progressBar);
        headlines.add(getString(R.string.loading_headlines));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, headlines);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Article selected = articles.get(position);

            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("title", selected.title);
            intent.putExtra("description", selected.description);
            intent.putExtra("link", selected.link);
            intent.putExtra("pubDate", selected.pubDate);

            startActivity(intent);
        });
        prefs = getSharedPreferences("BBCPrefs", MODE_PRIVATE);

        EditText editSearch = findViewById(R.id.editSearch);


        String savedText = prefs.getString("lastSearch", "");
        editSearch.setText(savedText);


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                prefs.edit().putString("lastSearch", s.toString()).apply();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });



        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        downloadRSS();

    }

    private void downloadRSS(){
        progressBar.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() ->{
            ArrayList<Article> tempArticles = new ArrayList<>();
            Article currentArticle = null;
            String currentTag = "";
            try{
                URL url = new URL("http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);

                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        currentTag = parser.getName();

                        if (currentTag.equals("item")) {
                            currentArticle = new Article();
                        }

                    } else if (eventType == XmlPullParser.TEXT) {

                        if (currentArticle != null) {
                            switch (currentTag) {
                                case "title":
                                    currentArticle.title = parser.getText();
                                    break;
                                case "description":
                                    currentArticle.description = parser.getText();
                                    break;
                                case "link":
                                    currentArticle.link = parser.getText();
                                    break;
                                case "pubDate":
                                    currentArticle.pubDate = parser.getText();
                                    break;
                            }
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (parser.getName().equals("item") && currentArticle != null) {
                            tempArticles.add(currentArticle);
                        }
                    }

                    eventType = parser.next();
                }

            } catch (Exception e){
                Article errorArticle = new Article();
                errorArticle.title = getString(R.string.error_loading) + e.getMessage();
                tempArticles.add(errorArticle);

            }
            handler.post(() -> {
                articles.clear();
                articles.addAll(tempArticles);

                headlines.clear();
                for (Article a : articles) {
                    headlines.add(a.title);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            });
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.help))
                    .setMessage(getString(R.string.help_help))
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}