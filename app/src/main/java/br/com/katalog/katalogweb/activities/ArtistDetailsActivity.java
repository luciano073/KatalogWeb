package br.com.katalog.katalogweb.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.adapters.WorkArtistAdapter;
import br.com.katalog.katalogweb.databinding.ActivityArtistDetailsBinding;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.utils.Constants;

public class ArtistDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private ValueEventListener mListener;
    private List<Object> mObjects;
    private Artist mArtist;
    private ActivityArtistDetailsBinding mBinding;
    private WorkArtistAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_artist_details);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mObjects = new ArrayList<>();
        mArtist = getIntent().getParcelableExtra("artist");
        getSupportActionBar().setTitle(mArtist.getName());
        mBinding.setArtist(mArtist);
        mAdapter = new WorkArtistAdapter(mObjects);
        initList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        mDatabaseRef.removeEventListener(mListener);
        super.onDestroy();
    }

    private void initList() {

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //recupera filmes do artista com denormalização
                /*for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Film film = snapshot.getValue(Film.class);
                    mObjects.add(film);
                    mAdapter.notifyDataSetChanged();
                    mBinding.tvNumberWorks.setText(getResources().getString(
                            R.string.artist_works,
                            mObjects.size()
                    ));
                }*/

                //sem denormalização
                mBinding.progressBar.setVisibility(View.GONE);
                mObjects.clear(); //evita duplicação de dados
                mBinding.tvNumberWorks.setText(getResources().getString(
                        R.string.artist_works,
                        mObjects.size()
                ));

                DataSnapshot snapFilms = dataSnapshot.child("films");
                for (DataSnapshot snapshot : snapFilms.getChildren()){
                    final String filmId = snapshot.getKey();
                    mDatabaseRef.child(Constants.FILMS_DBREF)
                            .child(filmId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Film film = dataSnapshot.getValue(Film.class);
                                    film.setId(filmId);
                                    mObjects.add(film);
                                    mAdapter.notifyDataSetChanged();
                                    mBinding.tvNumberWorks.setText(getResources().getString(
                                            R.string.artist_works,
                                            mObjects.size()
                                    ));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

                DataSnapshot snapBooks = dataSnapshot.child("books");
                for (DataSnapshot snapshot : snapBooks.getChildren()){
                    final String bookId = snapshot.getKey();
                    mDatabaseRef.child(Constants.BOOKS_DBREF)
                            .child(bookId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Book book = dataSnapshot.getValue(Book.class);
                                    book.setId(bookId);
                                    mObjects.add(book);
                                    mAdapter.notifyDataSetChanged();
                                    mBinding.tvNumberWorks.setText(getResources().getString(
                                            R.string.artist_works,
                                            mObjects.size()
                                    ));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseRef.child(Constants.ARTIST_WORKS_DBREF)
                .child(mArtist.getId())
                .addValueEventListener(mListener);

    }
}
