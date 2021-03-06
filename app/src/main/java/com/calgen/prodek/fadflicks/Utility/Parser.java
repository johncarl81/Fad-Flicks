package com.calgen.prodek.fadflicks.Utility;

import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.calgen.prodek.fadflicks.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gurupad on 05-Jul-16.
 */
public class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    /**
     * @param movieData a JSON formatted string data fetched from movieDb.
     * @return Array of strings containing formatted poster URLs. null value if movieData is null.
     */
    public static String[] getAllMoviePosterUrls(String movieData) {
        JSONObject jsonObject;
        String[] posterUrls = null;
        String BASE_IMAGE_URL = BuildConfig.BASE_IMAGE_URL;
        String IMAGE_SIZE = BuildConfig.IMAGE_SIZE;
        if (movieData == null)
            return null;
        try {
            jsonObject = new JSONObject(movieData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            posterUrls = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                //To get complete URL relative path must be suffixed to the base path
                //along with necessary parameters,if any.
                String relativePath = posterUrls[i] = jsonArray.getJSONObject(i).getString("poster_path");
                Uri uri = Uri.parse(BASE_IMAGE_URL)
                        .buildUpon()
                        .appendEncodedPath(IMAGE_SIZE)
                        .appendEncodedPath(relativePath)
                        .build();

                posterUrls[i] = uri.toString();
            }
        } catch (JSONException e) {
            Log.e(TAG, "getAllMoviePosterUrls: JSONException", e);
        }
        return posterUrls;
    }

    /**
     * @param movieData JSON formatted string data fetched from movieDb.
     * @param posterUrl Search key based on which details of a particular movie are fetched from {@code movieData}
     * @return JSONObject containing details of the movie searched. {@code null} if not found.
     */
    public static JSONObject getMovieDetailsByUrl(String movieData, String posterUrl) {
        JSONObject movieDetails;
        try {
            JSONObject jsonObject = new JSONObject(movieData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                movieDetails = jsonArray.getJSONObject(i);
                if (posterUrl.endsWith(movieDetails.getString("poster_path"))) {         //because posterUrl is not a relative path
                    return movieDetails;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "getAllMoviePosterUrls: JSONException", e);
        }
        return null;
    }

    /**
     * @param backdropUrl Relative image path that is supposed to be appended.
     * @return Complete Url to locate the image resource.
     */
    public static String formatImageUrl(String backdropUrl) {
        String BASE_IMAGE_URL = BuildConfig.BASE_IMAGE_URL;
        String IMAGE_SIZE = BuildConfig.IMAGE_SIZE;
        Uri uri = Uri.parse(BASE_IMAGE_URL)
                .buildUpon()
                .appendEncodedPath(IMAGE_SIZE)
                .appendEncodedPath(backdropUrl)
                .build();
        return uri.toString();
    }

    // TODO: 12-Jul-16 It would be nice to add Object... params as the parameter

    /**
     * @param formatString the string that is supposed to be formatted
     * @param start        index from where to start
     * @param end          index where to end
     * @return formatted {@code SpannableString}
     */
    public static SpannableString formatIntoSpannableString(String formatString, int start, int end) {
        SpannableString spannableString = new SpannableString(formatString);
        spannableString.setSpan(new RelativeSizeSpan(1f), start, end, 0);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
        return spannableString;
    }

    /**
     * @param releaseDate date in the form yyyy-mm-dd
     * @return date in friendly form like JUN 2015
     */
    public static String formatReleaseDate(String releaseDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        Date date;
        try {
            date = formatter.parse(releaseDate);
            formatter = new SimpleDateFormat("MMM yyyy");
            return formatter.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "formatReleaseDate:", e);
        }
        return releaseDate;
    }
}
