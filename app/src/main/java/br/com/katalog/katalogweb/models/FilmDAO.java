package br.com.katalog.katalogweb.models;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.katalog.katalogweb.utils.Constants;


/**
 * Created by luciano on 20/10/2016.
 */

public class FilmDAO implements
        DAO<Film> {
    private static final String TAG = "DAO";
    private DatabaseReference filmDatabase;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference artistDatabase;
    private StorageReference imageDatabase;
    private ArrayList<DataSnapshot> snapshots;
    private ArrayList<Film> films;
    private Film mFilm;
    private static FilmDAO instance = new FilmDAO();


    private FilmDAO() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        filmDatabase = FirebaseDatabase.getInstance()
                .getReference(Constants.FILMS_DBREF);
        filmDatabase.keepSynced(true);
        artistDatabase = FirebaseDatabase.getInstance()
                .getReference(Artist.DatabaseFields.ROOT_DATABASE);
        artistDatabase.keepSynced(true);
        imageDatabase = FirebaseStorage.getInstance()
                .getReference();

    }


    public static FilmDAO getInstance() {
        return instance;
    }


    public void update(Film film, Film oldFilm) {

        String filmKey = film.getId();
        String oldImageUrl = oldFilm.getImageUrl();
        Artist oldDirector = oldFilm.getDirector();
        Artist oldWriter = oldFilm.getWriter();
        List<Artist> oldCast = oldFilm.getCast();
        Map<String, Object> childUpdates = new HashMap<>();

        if (oldImageUrl != null && !film.getImageUrl().equals(oldImageUrl)) {
            deleteImage(oldImageUrl);
        }

        //Check for changes on artists relationship
        for (Artist artist : oldCast) {
            if (film.getCast() == null || film.getCast().size() == 0) {

                childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                        + artist.getId() + "/films/" + filmKey, null);

            } else if (!film.getCast().contains(artist)) {
                childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                        + artist.getId() + "/films/"  + filmKey, null);

            }
        }

        if (oldDirector.getId() != null && !oldDirector.equals(film.getDirector())) {

            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + oldDirector.getId() + "/films/"  + filmKey, null);
        }

        if (oldWriter.getId() != null && !oldWriter.equals(film.getWriter())) {

            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + oldWriter.getId() + "/films/"  + filmKey, null);
        }
        mDatabaseRef.updateChildren(childUpdates);
        executeAtomicUpdates(film);


    }

    @Override
    public String insert(final Film film) {
        String filmKey = mDatabaseRef.child("Films").push().getKey();//filmDatabase.push().getKey();
        film.setId(filmKey);
        executeAtomicUpdates(film);
        return filmKey;
    }

    private void executeAtomicUpdates(final Film film) {
        String filmKey = film.getId();
        Artist director = film.getDirector();
        Artist writer = film.getWriter();
        List<Artist> cast = film.getCast();

        Map<String, Object> filmValues = film.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Films/" + filmKey, filmValues);

        if (director != null && director.getName() != null) {
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + director.getId() + "/films/" + filmKey, true);
            childUpdates.put("/film-contributors/" + filmKey
                    + "/directors/" + director.getId(), director.toMap());

        }
        if (writer != null && writer.getName() != null) {
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + writer.getId() + "/films/"  + filmKey, true);
            childUpdates.put("/film-contributors/" + filmKey
                    + "/writers/" + writer.getId(), writer.toMap());
        }

        Map<String, Object> castValues = new HashMap<>();
        if (cast != null && cast.size() > 0) {
            for (Artist artist : cast) {
                castValues.put(artist.getId(), artist.toMap());
                childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                        + artist.getId() + "/films/"  + filmKey, true);

            }
            //atualiza todo o diretorio "cast" evita checagem nas atualizações
            childUpdates.put("/film-contributors/" + filmKey
                    + "/cast/", castValues);
        }else {
            childUpdates.put("/film-contributors/" + filmKey
                    + "/cast/", null);
        }



        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                connectArtists(film);
            }
        });

    }


    private void deleteImage(String imgUrl) {
        Uri uri = Uri.parse(imgUrl);
        String imgNode = uri.getLastPathSegment();
        imageDatabase.child(imgNode).delete();
    }


    @Override
    public void delete(Film film) {
        if (film.getImageUrl() != null) {
            deleteImage(film.getImageUrl());
        }
        desconnectArtists(film);

        filmDatabase.child(film.getId()).removeValue();

    }


    private void connectArtists(Film film) {
        Artist director = film.getDirector();
        Artist writer = film.getWriter();
        List<Artist> cast = film.getCast();
//        Log.d(TAG, "director: " + director.getName());
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> castValues = new HashMap<>();



        if (director != null && director.getName() != null) {

            filmDatabase.child(film.getId())
                    .child(Film.DatabaseFields.DIRECTOR)
                    .child(director.getId())
                    .setValue(director.getName());
        }

        if (writer != null && writer.getName() != null) {

            filmDatabase.child(film.getId())
                    .child(Film.DatabaseFields.WRITER)
                    .child(writer.getId())
                    .setValue(writer.getName());
        }

        if (cast != null && cast.size() > 0) {
            for (Artist artist : cast) {

                /*filmDatabase.child(film.getId())
                        .child(Film.DatabaseFields.CAST)
                        .child(artist.getId())
                        .setValue(artist.getName());*/
                castValues.put(artist.getId(), artist.getName());
            }
        }
        childUpdates.put("/Films/" + film.getId() + "/Cast/", castValues);
        mDatabaseRef.updateChildren(childUpdates);
    }

    private void desconnectArtists(final Film film) {

        filmDatabase.child(film.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(Film.DatabaseFields.DIRECTOR)) {
                    String directorKey = "";
                    DataSnapshot directorSnapshot = dataSnapshot.child(Film.DatabaseFields.DIRECTOR);
                    for (DataSnapshot snapshot : directorSnapshot.getChildren()) {
                        directorKey = snapshot.getKey();
                    }

                }

                if (dataSnapshot.hasChild(Film.DatabaseFields.WRITER)) {
                    String writerKey = "";
                    DataSnapshot writerSnapshot = dataSnapshot.child(Film.DatabaseFields.WRITER);
                    for (DataSnapshot snapshot : writerSnapshot.getChildren()) {
                        writerKey = snapshot.getKey();
                    }

                }

                if (dataSnapshot.hasChild(Film.DatabaseFields.CAST)) {
                    ArrayList<String> artistsKey = new ArrayList<String>();
                    DataSnapshot castSnapshot = dataSnapshot.child(Film.DatabaseFields.CAST);
                    for (DataSnapshot snapshot : castSnapshot.getChildren()) {
                        artistsKey.add(snapshot.getKey());
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void save(final Film film) {
        filmDatabase.child(film.getId())
                .setValue(film)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        connectArtists(film);
                    }
                });
    }

    @Override
    public Film getById(Object id) {
        //Same asynchronous problem
        return null;
    }



    public void cleanUp() {
//        EventBus.getDefault().unregister(this);
    }

    public Artist retrieveDirector(DataSnapshot dataSnapshot) {
        Artist director = new Artist();

        DataSnapshot directorSnapshot = dataSnapshot.child(Constants.FILM_DIRECTOR_CHILD_DBREF);
        for (DataSnapshot snapshotD : directorSnapshot.getChildren()) {
            director.setId(snapshotD.getKey());
            director.setName(snapshotD.getValue(String.class));
        }
        return director;
    }


    public Artist retrieveWriter(DataSnapshot dataSnapshot) {
        Artist writer = new Artist();

        DataSnapshot writerSnapshot = dataSnapshot.child(Constants.FILM_WRITER_CHILD_DBREF);
        for (DataSnapshot snapshotD : writerSnapshot.getChildren()) {
            writer.setId(snapshotD.getKey());
            writer.setName(snapshotD.getValue(String.class));
        }
        return writer;
    }

    public ArrayList<Artist> retrieveCast(DataSnapshot dataSnapshot) {
        ArrayList<Artist> cast = new ArrayList<>();

        DataSnapshot castSnapshot = dataSnapshot.child(Constants.FILM_CAST_CHILD_DBREF);
        for (DataSnapshot snapshotC : castSnapshot.getChildren()) {
            Artist artist = new Artist();
            artist.setId(snapshotC.getKey());
            artist.setName(snapshotC.getValue(String.class));
            cast.add(artist);
        }

        return cast;
    }


    public void unregisterBus() {
//        EventBus.getDefault().unregister(this);
    }


    @Override
    public List<Film> getAll() {

        return null;
    }

    public void setupFilmArtists(final Film film) {
        mDatabaseRef.child(Constants.FILM_CONTRIBUTORS_DBREF)
                .child(film.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("directors")) {
                            DataSnapshot snapD = dataSnapshot.child("directors");
                            for (DataSnapshot snapshot : snapD.getChildren()) {
                                Artist director = snapshot.getValue(Artist.class);
                                director.setId(snapshot.getKey());
                                film.setDirector(director);
                            }
                        }

                        if (dataSnapshot.hasChild("writers")) {
                            DataSnapshot snapW = dataSnapshot.child("writers");
                            for (DataSnapshot snapshot : snapW.getChildren()) {
                                Artist writer = snapshot.getValue(Artist.class);
                                writer.setId(snapshot.getKey());
                                film.setWriter(writer);
                            }
                        }

                        if (dataSnapshot.hasChild("cast")) {
                            List<Artist> cast = new ArrayList<Artist>();
                            DataSnapshot snapC = dataSnapshot.child("cast");
                            for (DataSnapshot snapshot : snapC.getChildren()) {
                                Artist artist = snapshot.getValue(Artist.class);
                                artist.setId(snapshot.getKey());
                                cast.add(artist);
                            }
                            film.setCast(cast);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


}
