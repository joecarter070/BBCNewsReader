package com.joecarter.bbcnewsreader;

/**
 * This is a basic data class that holds the info for one news article.
 * Each Article just stores the title, description, link, and date
 * so the Activities can pass this data around easily.
 */
public class Article {
    public String title;
    public String description;
    public String link;
    public String pubDate;
}