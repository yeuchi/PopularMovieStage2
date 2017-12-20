
### Stage 2: Trailers, Reviews, and Favorites
### User Experience
In this stage you’ll add additional functionality to the app you built in Stage 1.


You’ll add more information to your movie details view:

- You’ll allow users to view and play trailers ( either in the youtube app or a web browser).
- You’ll allow users to read reviews of a selected movie.
- You’ll also allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.
- You’ll modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

### Stage 2 - API Hints

- To fetch trailers you will want to make a request to the /movie/{id}/videos endpoint.
- To fetch reviews you will want to make a request to the /movie/{id}/reviews endpoint
- You should use an Intent to open a youtube link in either the native app or a web browser of choice.

### Implementation demo

Below screenshots demonstrate pages: main -> detail -> trailer or review.

### 1080 x 1920 - 1 size only

### Portrait layout
![screen shot 2017-12-14 at 6 40 21 pm](https://user-images.githubusercontent.com/1282659/34021237-46d6f678-e0fe-11e7-9793-7cc41c5cb069.png)
![screen shot 2017-12-14 at 6 37 14 pm](https://user-images.githubusercontent.com/1282659/34021156-e60eec88-e0fd-11e7-8a34-8e6642d5d0c6.png)
![screen shot 2017-12-15 at 5 03 35 pm](https://user-images.githubusercontent.com/1282659/34063806-2cf4f874-e1ba-11e7-84a0-f73e299ce8bb.png)
![screen shot 2017-12-15 at 5 02 38 pm](https://user-images.githubusercontent.com/1282659/34063809-327879c4-e1ba-11e7-88f4-d3486a27b5a9.png)

### Landscape layout
![main](https://user-images.githubusercontent.com/1282659/34073577-eac8d346-e261-11e7-81c1-c3ea69da0d6e.png)
![detail](https://user-images.githubusercontent.com/1282659/34073578-ec7a7f46-e261-11e7-81ab-acb66608f5b9.png)
![fork](https://user-images.githubusercontent.com/1282659/34073579-eedd7a90-e261-11e7-8a1d-2e0386371248.png)

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

![screen shot 2017-12-19 at 6 37 02 pm](https://user-images.githubusercontent.com/1282659/34185963-3c3d2f9a-e4ee-11e7-94f0-185cf6f608b1.png)
![screen shot 2017-12-19 at 6 37 27 pm](https://user-images.githubusercontent.com/1282659/34185964-3c509076-e4ee-11e7-96c1-44732bf56dea.png)

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
