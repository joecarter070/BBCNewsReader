package com.joecarter.bbcnewsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * This Activity shows the full details of whatever article the user clicked.
 * Basically it displays the title, description, and date, and lets the user
 * either open the article in a browser or save it to favourites.
 * It's essentially the "details page" of the app.
 */
public class DetailsActivity extends AppCompatActivity {
    TextView textTitle, textDescription, textDate;
    Button buttonOpenBrowser, buttonSaveFavourite;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        db = new DatabaseHelper(this);

        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textDate = findViewById(R.id.textDate);
        buttonOpenBrowser = findViewById(R.id.buttonOpenBrowser);
        buttonSaveFavourite = findViewById(R.id.buttonSaveFavourite);


        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String link = getIntent().getStringExtra("link");
        String pubDate = getIntent().getStringExtra("pubDate");

        textTitle.setText(title);
        textDescription.setText(description);
        textDate.setText(pubDate);

        // Opens the article in the user's browser
        buttonOpenBrowser.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        });

        // Saves the article to the favourites database
        buttonSaveFavourite.setOnClickListener(v -> {
            db.insertFavourite(title, description, link, pubDate);
            Snackbar.make(buttonSaveFavourite, getString(R.string.saved), Snackbar.LENGTH_LONG).show();

        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }
    //help dialog
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