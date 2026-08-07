# BBC News Reader App
A simple Android app that loads BBC news headlines from an RSS feed and lets users read details, save favourites, and browse help/about information. Built for the final Android assignment.

---

## Overview
This app downloads the BBC RSS feed and displays the headlines in a ListView. Users can tap an article to see more details, open it in a browser, or save it to their favourites. The app also includes a navigation drawer, a fragment, a help screen, French localization, and a small SQLite database for storing saved articles.

---

## Features

### Core UI Components
- ListView showing all RSS headlines  
- Details screen with full article info  
- EditText for searching/filtering headlines  
- Button to open article in browser  
- Button to save article to favourites  
- ProgressBar while RSS is loading  
- Toast shown on app start  
- Snackbar shown when saving or deleting favourites  

### Activities & Navigation
- MainActivity: loads RSS, shows headlines  
- DetailsActivity: shows full article info  
- FavouritesActivity: shows saved articles  
- HelpActivity: displays help screen  
- AboutActivity: hosts the AboutFragment  

### Fragment
- AboutFragment: simple fragment showing app info  

### Toolbar
- Toolbar includes the app title and version number (v1.0) as required.

### Navigation Drawer
- Links to Home, Favourites, Help, and About screens.

### Database
- SQLite database storing favourite articles  
- Supports insert, load, and delete  
- Used in FavouritesActivity  

### AsyncTask
- RSS feed is downloaded using an AsyncTask  
- XML parsed using XmlPullParser  
- Runs in background thread  

### SharedPreferences
- Saves the user’s last search text  
- Automatically restores it when reopening the app  

### French Localization
- Full `values-fr-rCA` folder with translated strings  
- App switches languages when device language changes  

---

## How It Works
1. MainActivity starts and shows a loading message.  
2. AsyncTask downloads the RSS feed from BBC.  
3. Headlines are displayed in a ListView.  
4. Clicking a headline opens DetailsActivity.  
5. Users can save articles to favourites (SQLite).  
6. FavouritesActivity shows saved items and allows deletion.  
7. Navigation drawer lets users switch screens easily.  
8. Help and About screens provide extra info.  

---

## How to Run
1. Clone the repository.  
2. Open the project in Android Studio.  
3. Run on an emulator or physical device (API 33+ recommended).
