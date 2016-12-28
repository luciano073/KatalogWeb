package br.com.katalog.katalogweb.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import br.com.katalog.katalogweb.listeners.ArtistEvenBus;
import br.com.katalog.katalogweb.listeners.FilmEventBus;

/**
 * Created by luciano on 21/10/2016.
 */

public class ArtistDAO implements
        ValueEventListener{
    private DatabaseReference mArtistRef;
    private static ArtistDAO instance = new ArtistDAO();

    public ArtistDAO() {
        this.mArtistRef = FirebaseDatabase.getInstance()
                .getReference(Artist.DatabaseFields.ROOT_DATABASE);
        mArtistRef.keepSynced(true);
        mArtistRef.addValueEventListener(this);
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

    public void unregisterFirebaseEventListener(){
        mArtistRef.removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArtistEvenBus bus = new ArtistEvenBus();
        bus.setTotalArtists(dataSnapshot.getChildrenCount());
        EventBus.getDefault().post(bus);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
