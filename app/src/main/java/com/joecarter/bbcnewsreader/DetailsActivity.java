package com.joecarter.bbcnewsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

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

        buttonOpenBrowser.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        });
        buttonSaveFavourite.setOnClickListener(v -> {
            db.insertFavourite(title, description, link, pubDate);
            Snackbar.make(buttonSaveFavourite, "Saved to favourites", Snackbar.LENGTH_LONG).show();

        });

    }
}