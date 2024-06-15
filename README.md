
# Udacity project: PopularMovies - Stage 2 <img src="https://user-images.githubusercontent.com/1282659/68507718-2fb17200-0232-11ea-84c5-c22890309e46.png" width="40"> 

### Updated: june 15, 2024
- java to Kotlin
- callback to event driven
- coroutine
- db cursor to room

### Android Studio
Iguana 2023.2.1 Patch 2 April 3, 2024\
<img width="500" src="https://github.com/yeuchi/LinearRegression/assets/1282659/4faf30c4-4425-4201-846b-b5bd32c9fc42"/>

### Test Device
Google Pixel6a 

# Origina Java Implementation

### (Trailers, Reviews, and Favorites) 

Google Play: https://play.google.com/store/apps/details?id=com.ctyeung.popularmoviestage2

### User Experience
- users to view and play trailers ( either in the youtube app or a web browser).
- users to read reviews of a selected movie.
- users to mark a movie as a favorite in the details view by tapping a button(star). 
  This is for a local movies collection that will maintain and does not require an API request*.
- main view to include an additional pivot to show their favorites collection.

### Stage 2 - API Hints

- To fetch trailers you will want to make a request to the /movie/{id}/videos endpoint.
- To fetch reviews you will want to make a request to the /movie/{id}/reviews endpoint
- Use an Intent to open a youtube link in either the native app or a web browser of choice.

### Implementation demo

Below screenshots demonstrate pages: main -> detail -> trailer or review.

### 1080 x 1920 - 1 size only

### Portrait layout
<img src="https://user-images.githubusercontent.com/1282659/68701257-fd628600-054b-11ea-942f-1be434e65d25.jpg" width="200">  <img src="https://user-images.githubusercontent.com/1282659/68701265-02bfd080-054c-11ea-95f5-59b8f01c5663.jpg" width="200">  <img src="https://user-images.githubusercontent.com/1282659/68701277-094e4800-054c-11ea-9904-538beb13c9cf.jpg" width="200">  <img src="https://user-images.githubusercontent.com/1282659/68701280-0b180b80-054c-11ea-9bda-d9400c2f4a25.jpg" width="200"> 

### Landscape layout

<img src="https://user-images.githubusercontent.com/1282659/68701383-474b6c00-054c-11ea-804f-a27f424b1faa.jpg" width="400"><img src="https://user-images.githubusercontent.com/1282659/68701393-4b778980-054c-11ea-9802-94901ef5c1f1.jpg" width="400">
<img src="https://user-images.githubusercontent.com/1282659/68701402-4fa3a700-054c-11ea-800e-2da047b14efa.jpg" width="400"><img src="https://user-images.githubusercontent.com/1282659/68701413-529e9780-054c-11ea-8340-8752afbc811a.jpg" width="400">

Below screenshots demonstrate content provider with SQLiteDatabase CRUD operators: 

### create
final String CREATE_TABLE = "CREATE TABLE "  + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID                 + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE        + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_JSON_DETAIL  + " TEXT NOT NULL);";
                
### query
cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

### update - not used

### insert
db.insert(TABLE_NAME, null, values);

### delete
db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+COLUMN_TITLE+"='"+selection+"'");

<img src="https://user-images.githubusercontent.com/1282659/34185963-3c3d2f9a-e4ee-11e7-94f0-185cf6f608b1.png" width="200"> <img src="https://user-images.githubusercontent.com/1282659/34185964-3c509076-e4ee-11e7-96c1-44732bf56dea.png" width="200">

### display favorite sorted (DESC)

<img src="https://user-images.githubusercontent.com/1282659/34188751-91aaf39a-e4fd-11e7-85db-f9578ebfe6f3.png" width="200"> <img src="https://user-images.githubusercontent.com/1282659/34187621-fa4d4a70-e4f7-11e7-8f00-2bf928dac3df.png" width="200">

### Refinements in the future (for product grade)
- tests and error handling.
- more layouts for different format + sizes.
- more graphics for trailer + reviews.
- more verbose; example: no trailer, no review, toasts
- more comments in code
- more modularized code; decouple; refactor and use appropriate design patterns for more general usage.

### Common Project Requirements

### MEETS SPECIFICATIONS
App is written solely in the Java Programming Language.

App conforms to common standards found in the Android Nanodegree General Project Guidelines.

### User Interface - Layout

### MEETS SPECIFICATIONS
UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.

Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.

UI contains a screen for displaying the details for a selected movie.

Movie Details layout contains title, release date, movie poster, vote average, and plot synopsis.

Movie Details layout contains a section for displaying trailer videos and user reviews.

### User Interface - Function

### MEETS SPECIFICATIONS
When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.

When a movie poster thumbnail is selected, the movie details screen is launched.

When a trailer is selected, app uses an Intent to launch the trailer.

In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite.

### Network API Implementation

### MEETS SPECIFICATIONS
In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.

App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those details when the user selects a movie.

App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those details when the user selects a movie.

### Data Persistence

### MEETS SPECIFICATIONS
The titles and ids of the user's favorite movies are stored in a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever the user favorites or unfavorites a movie.

When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the ContentProvider.
