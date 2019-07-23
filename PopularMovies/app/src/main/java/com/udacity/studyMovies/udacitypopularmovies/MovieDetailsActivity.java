package com.udacity.studyMovies.udacitypopularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by lucas on 17/09/2018.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    public static Movie movie;
    public static Intent intent;
    public static TextView movie_title, movie_release_date, movie_average_rating, movie_overview;
    public static ImageView movie_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_movie_details, new MovieDetailsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static class MovieDetailsFragment extends Fragment {

        public MovieDetailsFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
            initComponents(rootView);
            setValues(rootView);
            return rootView;
        }

        public void initComponents(View rootView){
            movie = new Movie();

            MovieDetailsActivity.intent = getActivity().getIntent();

            int movie_position = intent.getIntExtra("movie_position", 0);
            movie = MovieActivity.movieList.get(movie_position);

            movie_title = (TextView)rootView.findViewById(R.id.movie_title);
            movie_release_date = (TextView)rootView.findViewById(R.id.movie_release_date);
            movie_average_rating = (TextView)rootView.findViewById(R.id.movie_average_rating);
            movie_poster = (ImageView)rootView.findViewById(R.id.movie_poster);
            movie_overview = (TextView)rootView.findViewById(R.id.movie_overview);
        }

        public static void setValues(View rootView){
            movie_title.setText(movie.getOriginal_title());
            movie_title.setVisibility(View.VISIBLE);
            movie_release_date.setText(movie.getRelease_date());
            movie_average_rating.setText("Rating: " + movie.getVote_average() + " / 10");
            movie_overview.setText(movie.getOverview());

            String movie_poster_url;

            if (movie.getPoster_path() == MovieAPI.IMAGE_NOT_FOUND) {
                movie_poster_url = MovieAPI.IMAGE_NOT_FOUND;
            }else {
                movie_poster_url = MovieAPI.IMAGE_URL + MovieAPI.IMAGE_SIZE_185 + "/" + movie.getPoster_path();
            }

            Picasso.with(rootView.getContext()).load(movie_poster_url).into(movie_poster);
            movie_poster.setVisibility(View.VISIBLE);
        }
    }
}
