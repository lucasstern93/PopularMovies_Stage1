package com.udacity.studyMovies.udacitypopularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lucas on 17/09/2018.
 */

public class FetchMovieTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String sortBy = params[0];

        Uri builtUri = Uri.parse(MovieAPI.API_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter("api_key", "2fd9583534760854c23a2efd1f97a0ab")
                .build();

        String response;

        try {
            response  = getJSON(builtUri);
            return response;
        } catch (Exception e) {
            MovieActivity.toast.setText("Connection Error");
            MovieActivity.toast.setDuration(Toast.LENGTH_SHORT);
            MovieActivity.toast.show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            loadMovie(response);
        } else {
            MovieActivity.toast.setText("No Internet Connection");
            MovieActivity.toast.setDuration(Toast.LENGTH_SHORT);
            MovieActivity.toast.show();
        }

    }

    public static void loadMovie (String jsonString) {
        MovieActivity.movieList.clear();
        MovieActivity.movieImages.clear();

        try {
            if (jsonString != null) {
                JSONObject moviesObject = new JSONObject(jsonString);

                JSONArray moviesArray = moviesObject.getJSONArray("results");

                for (int i = 0; i <= moviesArray.length(); i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);

                    Movie movieItem = new Movie();
                    movieItem.setId(movie.getInt("id"));
                    movieItem.setTitle(movie.getString("title"));
                    movieItem.setBackdrop_path(movie.getString("backdrop_path"));
                    movieItem.setOriginal_title(movie.getString("original_title"));
                    movieItem.setOriginal_language(movie.getString("original_language"));

                    if (movie.getString("overview") == "null") {
                        movieItem.setOverview("No overview was found");
                    } else {
                        movieItem.setOverview(movie.getString("overview"));
                    }

                    if (movie.getString("release_date") == "null") {
                        movieItem.setRelease_date("Unknown release date");
                    } else {
                        String initialDate = movie.getString("release_date");
                        SimpleDateFormat input = new SimpleDateFormat("yyyy-dd-mm");
                        SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy");

                        try {
                            Date newDate = input.parse(initialDate);
                            movieItem.setRelease_date(output.format(newDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    movieItem.setPopularity(movie.getString("popularity"));
                    movieItem.setVote_average(movie.getString("vote_average"));
                    movieItem.setPoster_path(movie.getString("poster_path"));

                    if (movie.getString("poster_path") == "null") {
                        MovieActivity.movieImages.add(MovieAPI.IMAGE_NOT_FOUND);
                        movieItem.setPoster_path(MovieAPI.IMAGE_NOT_FOUND);
                    } else {
                        MovieActivity.movieImages.add(MovieAPI.IMAGE_URL + MovieAPI.IMAGE_SIZE_185 + movie.getString("poster_path"));
                    }

                    MovieActivity.movieList.add(movieItem);
                    MovieActivity.movieAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getJSON(Uri builtUri)
    {
        InputStream inputStream;

        StringBuffer buffer;

        HttpURLConnection urlConnection = null;

        BufferedReader reader = null;

        String movieJSON = null;

        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();

            buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            movieJSON = buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {

                }
            }
        }

        return movieJSON;
    }

}
