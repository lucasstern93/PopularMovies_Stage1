package com.udacity.studyMovies.udacitypopularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    public static ArrayList<Movie> movieList;
    public static ArrayList<String> movieImages;
    public static MovieAdapter movieAdapter;
    public static String lastSortBy;
    public static GridView gridViewMovie;
    public static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_movie, new MovieFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_sort_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MovieFragment extends Fragment {

        public MovieFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle onSavedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
            setHasOptionsMenu(true);

            gridViewMovie = (GridView) rootView.findViewById(R.id.gv_movie);
            int ot = getResources().getConfiguration().orientation;
            gridViewMovie.setNumColumns(ot == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
            gridViewMovie.setAdapter(movieAdapter);
            gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movie_id", movieList.get(position).getId());
                intent.putExtra("movie_position", position);
                startActivity(intent);
                }
            });

            toast = Toast.makeText(rootView.getContext(), "", Toast.LENGTH_LONG);
            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelableArrayList("movies", MovieActivity.movieList);
            outState.putStringArrayList("images", MovieActivity.movieImages);
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
                movieList = savedInstanceState.getParcelableArrayList("movies");
                movieImages = savedInstanceState.getStringArrayList("images");
            } else {
                movieList = new ArrayList<Movie>();
                movieImages = new ArrayList<String>();
                movieAdapter = new MovieAdapter(getActivity());
                updateMovies();
            }
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPreferences.getString(getString(R.string.pref_sort_by), 
                    getString(R.string.pref_sort_by_default_value));

            if (lastSortBy != null && !sortBy.equals(lastSortBy)) {
                movieList = new ArrayList<Movie>();
                movieImages = new ArrayList<String>();
                updateMovies();
            }
            lastSortBy = sortBy;
            super.onResume();
        }

        public void updateMovies() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPreferences.getString(getString(R.string.pref_sort_by), getString(R.string.pref_sort_by_default_value));
            new FetchMovieTask().execute(sortBy, null);
        }
    }
}
