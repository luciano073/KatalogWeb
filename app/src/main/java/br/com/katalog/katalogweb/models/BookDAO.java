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

import java.util.List;

import br.com.katalog.katalogweb.utils.Constants;

import static br.com.katalog.katalogweb.R.string.books;
import static br.com.katalog.katalogweb.R.string.cast;

/**
 * Created by luciano on 17/12/2016.
 */

public class BookDAO {

    private DatabaseReference mBookRef;
    private DatabaseReference mArtistRef;
    private StorageReference mStorageRef;
    private static BookDAO instance = new BookDAO();

    private BookDAO (){
        mBookRef = FirebaseDatabase.getInstance()
                .getReference(Constants.BOOK_DATABASE_ROOT_NODE);
        mArtistRef = FirebaseDatabase.getInstance()
                .getReference(Constants.ARTIST_DATABASE_ROOT_NODE);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static BookDAO getInstance(){
        return instance;
    }


    public String insert (Book book){
        String bookKey = mBookRef.push().getKey();
        book.setId(bookKey);
        save(book);
        return bookKey;
    }

    public void delete (Book book){
        if (book.getImageUrl() != null){
            deleteImage(book.getImageUrl());
        }
        disconnectArtists(book);
        mBookRef.child(book.getId()).removeValue();
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
        String oldImageUrl  = oldBook.getImageUrl();
        Artist oldWriter    = oldBook.getWriter();
        Artist oldColors    = oldBook.getColors();
        Artist oldDrawings  = oldBook.getDrawings();

        if (oldImageUrl != null && !newBook.getImageUrl().equals(oldImageUrl)) {
            deleteImage(oldImageUrl);
        }

        if (oldColors.getId() != null && !oldColors.equals(newBook.getColors())){
            mArtistRef.child(oldColors.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_PAINTED)
                    .child(newBook.getId())
                    .removeValue();
        }

        if (oldWriter.getId() != null && !oldWriter.equals(newBook.getWriter())){
            mArtistRef.child(oldWriter.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_WROTE)
                    .child(newBook.getId())
                    .removeValue();
        }

        if (oldDrawings.getId() != null && !oldDrawings.equals(newBook.getDrawings())){
            mArtistRef.child(oldDrawings.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_DREW)
                    .child(newBook.getId())
                    .removeValue();
        }

        save(newBook);
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
            mArtistRef.child(colors.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_PAINTED)
                    .child(book.getId())
                    .setValue(book.getTitle());
            mBookRef.child(book.getId())
                    .child(Constants.BOOK_DATABASE_CHILD_COLORS)
                    .child(colors.getId())
                    .setValue(colors.getName());
        }

        if (writer != null && writer.getName() != null) {
            mArtistRef.child(writer.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_WROTE)
                    .child(book.getId())
                    .setValue(book.getTitle());
            mBookRef.child(book.getId())
                    .child(Constants.BOOK_DATABASE_CHILD_WRITER)
                    .child(writer.getId())
                    .setValue(writer.getName());
        }

        if (drawings != null && drawings.getName() != null) {
            mArtistRef.child(drawings.getId())
                    .child(Constants.ARTIST_DATABASE_CHILD_DREW)
                    .child(book.getId())
                    .setValue(book.getTitle());
            mBookRef.child(book.getId())
                    .child(Constants.BOOK_DATABASE_CHILD_DRAWINGS)
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

        DataSnapshot writerSnapshot = dataSnapshot.child(Constants.BOOK_DATABASE_CHILD_WRITER);
        for (DataSnapshot snapshotD : writerSnapshot.getChildren()) {
            writer.setId(snapshotD.getKey());
            writer.setName(snapshotD.getValue(String.class));
        }
        return writer;
    }

    public Artist retrieveColors(DataSnapshot dataSnapshot){
        Artist colors = new Artist();

        DataSnapshot colorsSnapshot = dataSnapshot.child(Constants.BOOK_DATABASE_CHILD_COLORS);
        for (DataSnapshot snapshotC : colorsSnapshot.getChildren()){
            colors.setId(snapshotC.getKey());
            colors.setName(snapshotC.getValue(String.class));
        }
        return colors;
    }

    public Artist retrieveDrawings(DataSnapshot dataSnapshot){
        Artist drawings = new Artist();

        DataSnapshot drawingsSnapshot = dataSnapshot.child(Constants.BOOK_DATABASE_CHILD_DRAWINGS);
        for (DataSnapshot snapshotD : drawingsSnapshot.getChildren()){
            drawings.setId(snapshotD.getKey());
            drawings.setName(snapshotD.getValue(String.class));
        }
        return drawings;
    }
}
