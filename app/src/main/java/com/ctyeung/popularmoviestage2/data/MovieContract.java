package com.ctyeung.popularmoviestage2.data;

/**
 * Created by ctyeung on 12/17/17.
 */


        import android.net.Uri;
        import android.provider.BaseColumns;


public class MovieContract {

    /* Add content provider constants to the Contract
     Clients need to know how to access the task data, and it's your job to provide
     these content URI's for the path to that data:
        1) Content authority,
        2) Base content URI,
        3) Path(s) to the movie directory
        4) Content URI for data in the TaskEntry class
      */

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.ctyeung.popularmoviestage2";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MOVIES = "movies";

    /* TaskEntry is an inner class that defines the contents of the task table */
    public static final class MovieEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        // Task table and column names
        public static final String TABLE_NAME = "movies";

        // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_JSON_DETAIL = "jsonDetail";


        /*
        The above table structure looks something like the sample table below.
        With the name of the table and columns on top, and potential contents in rows

        Note: Because this implements BaseColumns, the _id column is generated automatically

        tasks
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        | _id  |    TITLE          |    FAVORITE   |  JSON INFO  |
         - - - - - - - - - - - - - - - - - - - - - -
        |  1   |  Justice League   |       true    |  {.....}    |
         - - - - - - - - - - - - - - - - - - - - - -
        |  2   |   THOR            |       true    |  {.....}    |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        .
        .
        .
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        | 30  |   Blade Runner     |      false    |   {.....}   |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

         */

    }
}
