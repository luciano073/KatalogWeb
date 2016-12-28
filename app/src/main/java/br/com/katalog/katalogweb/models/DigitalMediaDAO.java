package br.com.katalog.katalogweb.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.katalog.katalogweb.utils.Constants;

import static br.com.katalog.katalogweb.R.string.colors;
import static br.com.katalog.katalogweb.R.string.drawings;

/**
 * Created by luciano on 17/12/2016.
 */

public class DigitalMediaDAO {

    private DatabaseReference mMediaRef;
    private static DigitalMediaDAO instance = new DigitalMediaDAO();

    private DigitalMediaDAO(){
        mMediaRef = FirebaseDatabase.getInstance()
                .getReference(Constants.DIGITAL_MEDIA_DATABASE_ROOT_NODE);
    }

    public static DigitalMediaDAO getInstance(){
        return instance;
    }


    public String insert (DigitalMedia media){
        String mediaKey = mMediaRef.push().getKey();
        media.setId(mediaKey);
        save(media);
        return mediaKey;
    }

    public void delete (DigitalMedia media){

        mMediaRef.child(media.getId()).removeValue();
    }

    public void save (final DigitalMedia media){
        mMediaRef.child(media.getId())
                .setValue(media)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        connectFilm(media);
                    }
                });
    }

    public void update (DigitalMedia media){

        save(media);
    }


    private void connectFilm(DigitalMedia media) {
        Film film = media.getFilm();

        if (film != null && film.getTitle() != null) {

            mMediaRef.child(media.getId())
                    .child(Constants.DIGITAL_MEDIA_CHILD_FILM)
                    .child(film.getId())
                    .setValue(film.getNormalizedTitle());
        }

    }


    public Film retrieveFilm(DataSnapshot dataSnapshot) {
        Film film = new Film();

        DataSnapshot filmSnapshot = dataSnapshot.child(Constants.DIGITAL_MEDIA_CHILD_FILM);
        for (DataSnapshot snapshotF : filmSnapshot.getChildren()) {
            film.setId(snapshotF.getKey());
            film.setTitle(snapshotF.getValue(String.class));
        }
        return film;
    }


}
