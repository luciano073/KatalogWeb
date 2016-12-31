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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.listeners.FilmEventBus;
import br.com.katalog.katalogweb.utils.Constants;



/**
 * Created by luciano on 20/10/2016.
 */

public class FilmDAO implements
        DAO<Film>,
        ValueEventListener {
    private static final String TAG = "DAO";
    private DatabaseReference filmDatabase;
    private DatabaseReference artistDatabase;
    private StorageReference imageDatabase;
    private ArrayList<DataSnapshot> snapshots;
    private ArrayList<Film> films;
    private static FilmDAO instance = new FilmDAO();


    private FilmDAO() {
        filmDatabase = FirebaseDatabase.getInstance()
                .getReference(Constants.FILM_DATABASE_ROOT_NODE);
        filmDatabase.keepSynced(true);
        artistDatabase = FirebaseDatabase.getInstance()
                .getReference(Artist.DatabaseFields.ROOT_DATABASE);
        artistDatabase.keepSynced(true);
        imageDatabase = FirebaseStorage.getInstance()
                .getReference();
        snapshots = new ArrayList<DataSnapshot>();
        films = new ArrayList<Film>();
//        EventBus.getDefault().register(this);
        filmDatabase.addValueEventListener(this);

    }

    public static FilmDAO getInstance() {
        return instance;
    }


    public void update(Film film, Film oldFilm) {

        String oldImageUrl = oldFilm.getImageUrl();
        Artist oldDirector = oldFilm.getDirector();
        Artist oldWriter = oldFilm.getWriter();
        List<Artist> oldCast = oldFilm.getCast();

        if (oldImageUrl != null && !film.getImageUrl().equals(oldImageUrl)) {
            deleteImage(oldImageUrl);
        }

        for (Artist artist : oldCast) {
            /*if (film.getCast() == null || film.getCast().size() == 0){
                artistDatabase.child(artist.getId())
                        .child(Artist.DatabaseFields.ACTED)
                        .child(film.getId())
                        .removeValue();
            } else*/
            if (!film.getCast().contains(artist)) {
                artistDatabase.child(artist.getId())
                        .child(Artist.DatabaseFields.ACTED)
                        .child(film.getId())
                        .removeValue();

                filmDatabase.child(film.getId())
                        .child(Film.DatabaseFields.CAST)
                        .child(artist.getId())
                        .removeValue();
            }
        }

        if (oldDirector.getId() != null && !oldDirector.equals(film.getDirector())) {
            artistDatabase.child(oldDirector.getId())
                    .child(Artist.DatabaseFields.DIRECTED)
                    .child(film.getId())
                    .removeValue();
        }

        if (oldWriter.getId() != null && !oldWriter.equals(film.getWriter())) {
            artistDatabase.child(oldWriter.getId())
                    .child(Artist.DatabaseFields.WROTE)
                    .child(film.getId())
                    .removeValue();
        }

        save(film);

    }


    private void deleteImage(String imgUrl) {
        Uri uri = Uri.parse(imgUrl);
        String imgNode = uri.getLastPathSegment();
        imageDatabase.child(imgNode).delete();
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


    @Override
    public void delete(Film film) {
        if (film.getImageUrl() != null) {
            deleteImage(film.getImageUrl());
        }
        desconnectArtists(film);

        filmDatabase.child(film.getId()).removeValue();

    }


    public void unregisterBus() {
//        EventBus.getDefault().unregister(this);
    }

    public void unregisterFirebaseListener() {
        filmDatabase.removeEventListener(this);
    }

    @Override
    public List<Film> getAll() {
        //Learn how to work with asynchronous firebase listener
        final ArrayList<Film> list = new ArrayList<>();
        /*filmDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Film film = snapshot.getValue(Film.class);
                    film.setId(snapshot.getKey());
                    list.add(film);
                }
                EventBus.getDefault().post(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        return list;
    }

    @Override
    public String insert(Film film) {
        String filmKey = filmDatabase.push().getKey();
        film.setId(filmKey);
        save(film);

        return filmKey;
    }

    private void connectArtists(Film film) {
        Artist director = film.getDirector();
        Artist writer = film.getWriter();
        List<Artist> cast = film.getCast();
//        Log.d(TAG, "director: " + director.getName());

        if (director != null && director.getName() != null) {
            artistDatabase.child(director.getId())
                    .child(Artist.DatabaseFields.DIRECTED)
                    .child(film.getId())
                    .setValue(film.getTitle());
            filmDatabase.child(film.getId())
                    .child(Film.DatabaseFields.DIRECTOR)
                    .child(director.getId())
                    .setValue(director.getName());
        }

        if (writer != null && writer.getName() != null) {
            artistDatabase.child(writer.getId())
                    .child(Artist.DatabaseFields.WROTE)
                    .child(film.getId())
                    .setValue(film.getTitle());
            filmDatabase.child(film.getId())
                    .child(Film.DatabaseFields.WRITER)
                    .child(writer.getId())
                    .setValue(writer.getName());
        }

        if (cast != null && cast.size() > 0) {
            for (Artist artist : cast) {
                artistDatabase.child(artist.getId())
                        .child(Artist.DatabaseFields.ACTED)
                        .child(film.getId())
                        .setValue(film.getTitle());
                filmDatabase.child(film.getId())
                        .child(Film.DatabaseFields.CAST)
                        .child(artist.getId())
                        .setValue(artist.getName());
            }
        }
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
                    artistDatabase.child(directorKey)
                            .child(Artist.DatabaseFields.DIRECTED)
                            .child(film.getId())
                            .removeValue();
                }

                if (dataSnapshot.hasChild(Film.DatabaseFields.WRITER)) {
                    String writerKey = "";
                    DataSnapshot writerSnapshot = dataSnapshot.child(Film.DatabaseFields.WRITER);
                    for (DataSnapshot snapshot : writerSnapshot.getChildren()) {
                        writerKey = snapshot.getKey();
                    }
                    artistDatabase.child(writerKey)
                            .child(Artist.DatabaseFields.WROTE)
                            .child(film.getId())
                            .removeValue();
                }

                if (dataSnapshot.hasChild(Film.DatabaseFields.CAST)) {
                    ArrayList<String> artistsKey = new ArrayList<String>();
                    DataSnapshot castSnapshot = dataSnapshot.child(Film.DatabaseFields.CAST);
                    for (DataSnapshot snapshot : castSnapshot.getChildren()) {
                        artistsKey.add(snapshot.getKey());
                    }

                    for (String key : artistsKey) {
                        artistDatabase.child(key)
                                .child(Artist.DatabaseFields.ACTED)
                                .child(film.getId())
                                .removeValue();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
//        ArrayList<Film> films = new ArrayList<>();
        FilmEventBus eventBus = new FilmEventBus();
        eventBus.setTotalFilms(dataSnapshot.getChildrenCount());
        EventBus.getDefault().postSticky(eventBus);
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            snapshots.add(snapshot);
            Film film = snapshot.getValue(Film.class);
            film.setId(snapshot.getKey());
            films.add(film);
//            EventBus.getDefault().post(film);
        }
//        EventBus.getDefault().post(films);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    public void cleanUp() {
        filmDatabase.removeEventListener(this);
//        EventBus.getDefault().unregister(this);
    }

    public Artist retrieveDirector(DataSnapshot dataSnapshot) {
        Artist director = new Artist();

        DataSnapshot directorSnapshot = dataSnapshot.child(Constants.FILM_DATABASE_CHILD_DIRECTOR);
        for (DataSnapshot snapshotD : directorSnapshot.getChildren()) {
            director.setId(snapshotD.getKey());
            director.setName(snapshotD.getValue(String.class));
        }
        return director;
    }


    public Artist retrieveWriter(DataSnapshot dataSnapshot) {
        Artist writer = new Artist();

        DataSnapshot writerSnapshot = dataSnapshot.child(Constants.FILM_DATABASE_CHILD_WRITER);
        for (DataSnapshot snapshotD : writerSnapshot.getChildren()) {
            writer.setId(snapshotD.getKey());
            writer.setName(snapshotD.getValue(String.class));
        }
        return writer;
    }

    public ArrayList<Artist> retrieveCast(DataSnapshot dataSnapshot) {
        ArrayList<Artist> cast = new ArrayList<>();

        DataSnapshot castSnapshot = dataSnapshot.child(Constants.FILM_DATABASE_CHILD_CAST);
        for (DataSnapshot snapshotC : castSnapshot.getChildren()) {
            Artist artist = new Artist();
            artist.setId(snapshotC.getKey());
            artist.setName(snapshotC.getValue(String.class));
            cast.add(artist);
        }

        return cast;
    }


}
