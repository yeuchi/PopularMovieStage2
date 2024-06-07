package com.ctyeung.popularmoviestage2.data.utilities;

/**
 * Created by ctyeung on 8/19/17.
 */

public class MovieHelper {
    final public static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    final public static String API_KEY_VALUE = "838bb81a894bc77678913ab8b239cfb3";
    final public static String PARAM_API_KEY = "api_key";

    public static final String SORT_POPULAR = "popular";
    public static final String SORT_TOP_RATED = "top_rated";
    public static final String SORT_FAVORITE = "favorite";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ORIGINAL_TITLE = "original_title";
    public static final String KEY_PLOT = "overview";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_RUNTIME = "runtime";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_TRAILER = "key";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_REVIEW_URL = "url";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";

    public final static String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public final static String PARAM_API_VIDEO = "videos";
    public final static String PARAM_API_REVIEW = "reviews";
    public final static String PARAM_API_V = "v";

    public final static int INDEX_THUMBNAIL = 5;
    public final static int INDEX_DETAIL = 3;

    public final static String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static String getSizeByIndex(int i)
    {
        switch (i) {
            case 0:
                return "w92/";

            case 1:
                return "w154/";

            default:
            case 2:
                return "w185/";

            case 3:
                return "w342/";

            case 4:
                return "w500/";

            case 5:
                return "w780/";

            case 6:
                return "original/";
        }
    }

    public static boolean isVideo(String str)
    {
        boolean bRet = (str.indexOf("key")>0)?true:false;
        return bRet;
    }
}
