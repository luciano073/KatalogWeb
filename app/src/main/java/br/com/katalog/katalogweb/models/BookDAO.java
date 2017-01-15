package br.com.katalog.katalogweb.models;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import br.com.katalog.katalogweb.utils.Constants;

/**
 * Created by luciano on 17/12/2016.
 */

public class BookDAO {

    private DatabaseReference mBookRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mArtistRef;
    private StorageReference mStorageRef;
    private static BookDAO instance = new BookDAO();

    private BookDAO (){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mBookRef = FirebaseDatabase.getInstance()
                .getReference(Constants.BOOKS_DBREF);
        mArtistRef = FirebaseDatabase.getInstance()
                .getReference(Constants.ARTISTS_DBREF);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static BookDAO getInstance(){
        return instance;
    }


    public String insert (Book book){
        String bookKey = mDatabaseRef.child("books").push().getKey();
        book.setId(bookKey);
        executeAtomicUpdates(book);
        return bookKey;
    }



    public void save (final Book book){
        mBookRef.child(book.getId())
                .setValue(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        connectArtists(book);
                    }
                });
    }

    public void update (Book newBook, Book oldBook){
        String bookKey = newBook.getId();
        String oldImageUrl  = oldBook.getImageUrl();
        Artist oldWriter    = oldBook.getWriter();
        Artist oldColors    = oldBook.getColors();
        Artist oldDrawings  = oldBook.getDrawings();
        Map<String, Object> childUpdates = new HashMap<>();

        if (oldImageUrl != null && !newBook.getImageUrl().equals(oldImageUrl)) {
            deleteImage(oldImageUrl);
        }

        if (oldColors.getId() != null && !oldColors.equals(newBook.getColors())){
                childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                        + oldColors.getId() + "/books/"  + bookKey, null);

        }

        if (oldWriter.getId() != null && !oldWriter.equals(newBook.getWriter())){
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + oldWriter.getId() + "/books/"  + bookKey, null);
        }

        if (oldDrawings.getId() != null && !oldDrawings.equals(newBook.getDrawings())){
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + oldDrawings.getId() + "/books/"  + bookKey, null);
        }
        mDatabaseRef.updateChildren(childUpdates);

        executeAtomicUpdates(newBook);
    }

    private void executeAtomicUpdates(final Book book) {
        String bookKey = book.getId();
        Artist colors = book.getColors();
        Artist writer = book.getWriter();
        Artist drawings = book.getDrawings();

        Map<String, Object> bookValues = book.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/books/" + bookKey, bookValues);

        if (colors != null && colors.getName() != null) {
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + colors.getId() + "/books/" + bookKey, true);

        }
        if (writer != null && writer.getName() != null) {
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + writer.getId() + "/books/"  + bookKey, true);

        }
        if (drawings != null && drawings.getName() != null) {
            childUpdates.put(Constants.ARTIST_WORKS_DBREF_WITH_SLASH
                    + drawings.getId() + "/books/"  + bookKey, true);

        }



        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                connectArtists(book);
            }
        });

    }

    public void delete (Book book){
        if (book.getImageUrl() != null){
            deleteImage(book.getImageUrl());
        }
        disconnectArtists(book);
        mBookRef.child(book.getId()).removeValue();
    }

    private void deleteImage(String imgUrl) {
        Uri uri = Uri.parse(imgUrl);
        String imgNode = uri.getLastPathSegment();
        mStorageRef.child(imgNode).delete();
    }

    private void connectArtists(Book book) {
        Artist colors = book.getColors();
        Artist writer = book.getWriter();
        Artist drawings = book.getDrawings();

        if (colors != null && colors.getName() != null) {
            mBookRef.child(book.getId())
                    .child(Constants.BOOK_COLORS_CHILD_DBREF)
                    .child(colors.getId())
                    .setValue(colors.getName());
        }

        if (writer != null && writer.getName() != null) {
            mBookRef.child(book.getId())
                    .child(Constants.BOOK_WRITER_CHILD_DBREF)
                    .child(writer.getId())
                    .setValue(writer.getName());
        }

        if (drawings != null && drawings.getName() != null) {
            mBookRef.child(book.getId())
                    .child(Constants.BOOK_DRAWINGS_CHILD_DBREF)
                    .child(drawings.getId())
                    .setValue(drawings.getName());
        }
    }

    protected void disconnectArtists (Book book){
        Artist colors = book.getColors();
        Artist drawings = book.getDrawings();
        Artist writer = book.getWriter();
        if (colors != null && colors.getName() != null){
            mArtistRef.child(colors.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_PAINTED)
                    .child(book.getId())
                    .removeValue();
        }
        if (drawings != null && drawings.getName() != null){
            mArtistRef.child(drawings.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_DREW)
                    .child(book.getId())
                    .removeValue();
        }
        if (writer != null && writer.getName() != null){
            mArtistRef.child(colors.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_WROTE)
                    .child(book.getId())
                    .removeValue();
        }
    }


    public Artist retrieveWriter(DataSnapshot dataSnapshot) {
        Artist writer = new Artist();

        DataSnapshot writerSnapshot = dataSnapshot.child(Constants.BOOK_WRITER_CHILD_DBREF);
        for (DataSnapshot snapshotD : writerSnapshot.getChildren()) {
            writer.setId(snapshotD.getKey());
            writer.setName(snapshotD.getValue(String.class));
        }
        return writer;
    }

    public Artist retrieveColors(DataSnapshot dataSnapshot){
        Artist colors = new Artist();

        DataSnapshot colorsSnapshot = dataSnapshot.child(Constants.BOOK_COLORS_CHILD_DBREF);
        for (DataSnapshot snapshotC : colorsSnapshot.getChildren()){
            colors.setId(snapshotC.getKey());
            colors.setName(snapshotC.getValue(String.class));
        }
        return colors;
    }

    public Artist retrieveDrawings(DataSnapshot dataSnapshot){
        Artist drawings = new Artist();

        DataSnapshot drawingsSnapshot = dataSnapshot.child(Constants.BOOK_DRAWINGS_CHILD_DBREF);
        for (DataSnapshot snapshotD : drawingsSnapshot.getChildren()){
            drawings.setId(snapshotD.getKey());
            drawings.setName(snapshotD.getValue(String.class));
        }
        return drawings;
    }
}
