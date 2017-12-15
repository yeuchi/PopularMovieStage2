### PopularMovieStage2

- To fetch trailers you will want to make a request to the /movie/{id}/videos endpoint.
- To fetch reviews you will want to make a request to the /movie/{id}/reviews endpoint
- You should use an Intent to open a youtube link in either the native app or a web browser of choice.

![screen shot 2017-12-14 at 6 37 14 pm](https://user-images.githubusercontent.com/1282659/34021156-e60eec88-e0fd-11e7-8a34-8e6642d5d0c6.png)

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
