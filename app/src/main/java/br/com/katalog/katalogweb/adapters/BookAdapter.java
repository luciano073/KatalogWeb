package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import org.greenrobot.eventbus.EventBus;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.fragments.ImageShowFragment;
import br.com.katalog.katalogweb.listeners.BookClickListener;
import br.com.katalog.katalogweb.listeners.BookEventBus;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.BookDAO;
import br.com.katalog.katalogweb.utils.BookViewHolder;

/**
 * Created by luciano on 17/12/2016.
 */
public class BookAdapter extends FirebaseRecyclerAdapter<Book, BookViewHolder> {
    private BookClickListener mListener;
    private Context mContext;


    public BookAdapter(Context context, Query ref, BookClickListener listener) {
        super(Book.class, R.layout.item_book, BookViewHolder.class, ref);
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected Book parseSnapshot(DataSnapshot snapshot) {
        Book book = snapshot.getValue(Book.class);
        book.setId(snapshot.getKey());

        BookDAO bookDAO = BookDAO.getInstance();
        book.setWriter(bookDAO.retrieveWriter(snapshot));
        book.setDrawings(bookDAO.retrieveDrawings(snapshot));
        book.setColors(bookDAO.retrieveColors(snapshot));

        return book;
    }

    @Override
    protected void populateViewHolder(BookViewHolder viewHolder, final Book model, int position) {

        viewHolder.setBook(model);

        viewHolder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onBookClicked(model);
                }
            }
        });

        viewHolder.mBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                ImageShowFragment showFragment = ImageShowFragment.newInstance(model.getImageUrl());
                showFragment.show(fm, ImageShowFragment.TAG);
            }
        });
    }
}
