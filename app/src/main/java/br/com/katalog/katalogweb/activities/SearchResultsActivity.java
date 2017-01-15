package br.com.katalog.katalogweb.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.adapters.GenericAdapter;
import br.com.katalog.katalogweb.listeners.OnGetDataListener;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.Database;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.utils.Constants;

public class SearchResultsActivity extends AppCompatActivity {

    private List<Film> mFilms;
    private List<Artist> mArtists;
    private List<Book> mBooks;
    private List<Object> mListResult;
    private boolean hadReadDataOnServer;
    private Database mDatabase;
    private GenericAdapter mGenericAdapter;
    //    DatabaseReference mDatabaseRef;
//    ValueEventListener mFilmListener;
//    ValueEventListener mBookListener;
//    ValueEventListener mArtistListener;
    private ListView lvSearch;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

//        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mListResult = new ArrayList<>();
        mArtists = new ArrayList<>();
        mFilms = new ArrayList<>();
        mBooks = new ArrayList<>();
        mDatabase = new Database();

        lvSearch = (ListView) findViewById(R.id.lvSearch);
        lvSearch.setEmptyView(findViewById(R.id.empty));
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mGenericAdapter = new GenericAdapter(this, mListResult);
        lvSearch.setAdapter(mGenericAdapter);

        if (savedInstanceState != null) {
            mArtists = savedInstanceState.getParcelableArrayList("artistList");
            mBooks = savedInstanceState.getParcelableArrayList("bookList");
            mFilms = savedInstanceState.getParcelableArrayList("filmList");
            hadReadDataOnServer = savedInstanceState.getBoolean("readServer");
        } else {
//            initList();
//            checkInfoInServer(Constants.ARTISTS_DBREF);
//            mArtists = getIntent().getParcelableArrayListExtra("artists");
//            mBooks = getIntent().getParcelableArrayListExtra("books");
//            mFilms = getIntent().getParcelableArrayListExtra("films");
//            readDataOnServer();
        }


        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = adapterView.getItemAtPosition(i);
                if (o instanceof Artist) {
                    Artist artist = (Artist) o;
                    Intent intent = new Intent(getApplicationContext(), ArtistDetailsActivity.class);
//                    intent.addFlags();
                    intent.putExtra("artist", artist);
                    startActivity(intent);
                }
            }
        });

        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("filmList", (ArrayList<? extends Parcelable>) mFilms);
        outState.putParcelableArrayList("bookList", (ArrayList<? extends Parcelable>) mBooks);
        outState.putParcelableArrayList("artistList", (ArrayList<? extends Parcelable>) mArtists);
        outState.putBoolean("readServer", hadReadDataOnServer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search_results).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase.getValueEventListener() != null)
            mDatabase.removeValueListener();
    }

    private void handleIntent(Intent intent) {


        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.length() < 3) {
                Toast.makeText(this, "Termo deve conter no minimo 3 letras", Toast.LENGTH_SHORT).show();
                finish();
            }
            getSupportActionBar().setTitle("Busca pelo termo: " + query);

            if (hadReadDataOnServer) {
                localFilter(query);

            } else {
                serverFilter(query);
            }
        }


    }

    private void localFilter(String query) {
        mListResult.clear();
        mProgressBar.setVisibility(View.GONE);

        for (Film film : mFilms) {
            if (film.getNoDiacriticsTitle().toLowerCase().contains(query))
                mListResult.add(film);
        }
        for (Book book : mBooks) {
            if (book.getNoDiacriticsTitle().toLowerCase().contains(query))
                mListResult.add(book);
        }
        for (Artist artist : mArtists) {
            if (artist.getNoDiacriticsName().toLowerCase().contains(query))
                mListResult.add(artist);
        }
        mGenericAdapter.notifyDataSetChanged();
    }

    private void serverFilter(final String query) {

        mListResult.clear();

        mDatabase.mReadDataOnce(Constants.FILMS_DBREF, new OnGetDataListener() {
            @Override
            public void onStart() {
                hadReadDataOnServer = true;
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot snapshot : data.getChildren()) {
                    Film film = snapshot.getValue(Film.class);
                    film.setId(snapshot.getKey());
                    mFilms.add(film);
                    if (film.getNoDiacriticsTitle().toLowerCase().contains(query))
                        mListResult.add(film);
                }
                mGenericAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

        mDatabase.mReadDataOnce(Constants.BOOKS_DBREF, new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot snapshot : data.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    book.setId(snapshot.getKey());
                    mBooks.add(book);
                    if (book.getNoDiacriticsTitle().toLowerCase().contains(query))
                        mListResult.add(book);
                }
                mGenericAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

        mDatabase.mReadDataOnce(Constants.ARTISTS_DBREF, new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot snapshot : data.getChildren()) {
                    Artist artist = snapshot.getValue(Artist.class);
                    artist.setId(snapshot.getKey());
                    mArtists.add(artist);
                    if (artist.getNoDiacriticsName().toLowerCase().contains(query))
                        mListResult.add(artist);
                }
                mGenericAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }


}
