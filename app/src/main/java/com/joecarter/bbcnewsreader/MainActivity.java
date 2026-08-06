package com.joecarter.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BBC News Reader");

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
        headlines.add("Loading headlines..");
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


        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        downloadRSS();

    }
    private void downloadRSS(){
        progressBar.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() ->{
            ArrayList<String> tempArticles = new ArrayList<>();
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
                errorArticle.title = "Error loading RSS: " + e.getMessage();
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
}