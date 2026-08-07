package com.joecarter.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
/**
 * This Activity shows all the articles the user saved as favourites.
 * It loads them from the database, displays the titles in a ListView,
 * and lets the user click to open the full details again.
 * Long‑pressing an item deletes it from the database and updates the list.
 */
public class FavouritesActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Article> favourites = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourites);

        listView = findViewById(R.id.listViewFavourites);
        db = new DatabaseHelper(this);

        // Load favourites from the database
        favourites = db.getAllFavourites();

        // Extract just the titles for the ListView
        titles.clear();
        for (Article a : favourites){
            titles.add(a.title);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);
        // Clicking a favourite opens the details page again
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Article selected = favourites.get(position);

            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("title", selected.title);
            intent.putExtra("description", selected.description);
            intent.putExtra("link", selected.link);
            intent.putExtra("pubDate", selected.pubDate);

            startActivity(intent);
        });
        // Long‑press deletes the favourite
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Article selected = favourites.get(position);

            db.deleteFavourite(selected.title);

            favourites.remove(position);
            titles.remove(position);
            adapter.notifyDataSetChanged();
            // Required Snackbar
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.article_removed),
                    Snackbar.LENGTH_LONG).show();

            return true;
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