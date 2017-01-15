package br.com.katalog.katalogweb.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.utils.Constants;

/**
 * Created by luciano on 21/10/2016.
 */

public class ArtistDAO{
    private DatabaseReference mArtistRef;
    private DatabaseReference mBookRef;
    private DatabaseReference mFilmRef;
    private ValueEventListener mBookEventListener;
    private static ArtistDAO instance = new ArtistDAO();

    public ArtistDAO() {
        this.mArtistRef = FirebaseDatabase.getInstance()
                .getReference(Artist.DatabaseFields.ROOT_DATABASE);
        this.mBookRef = FirebaseDatabase.getInstance()
                .getReference(Constants.BOOKS_DBREF);
        this.mFilmRef = FirebaseDatabase.getInstance()
                .getReference(Constants.FILMS_DBREF);
        this.mBookEventListener = initBookListener();

    }

    private ValueEventListener initBookListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public static ArtistDAO getInstance(){
        return instance;
    }


    public Artist createAndInsert(String name){
        Artist artist = new Artist();
        artist.setId(mArtistRef.push().getKey());
        artist.setName(name);
        mArtistRef.child(artist.getId()).setValue(artist);
        return artist;
    }

    public List<Book> retrieveBooks(DataSnapshot dataSnapshot){
        List<Book> books = new ArrayList<>();

        DataSnapshot snapshotWrote = dataSnapshot.child(Constants.ARTIST_DATABASE_CHILD_WROTE);
        for (DataSnapshot snapshot : snapshotWrote.getChildren()){
            String id = snapshot.getKey();
            if (id != null) {
                Book book = new Book();
            }
        }
        return books;
    }
}
