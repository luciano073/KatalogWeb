package br.com.katalog.katalogweb.adapters;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.ItemBookBinding;
import br.com.katalog.katalogweb.databinding.ItemFilmBinding;
import br.com.katalog.katalogweb.fragments.ImageShowFragment;
import br.com.katalog.katalogweb.listeners.BookClickListener;
import br.com.katalog.katalogweb.listeners.FilmClickListener;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.Film;

/**
 * Created by luciano on 24/08/2016.
 */
public class BookSearchAdapter extends
        RecyclerView.Adapter<BookSearchAdapter.ViewHolder> {

    List<Book> mBooks;
    BookClickListener mListener;
    AppCompatActivity mActivity;
    public BookSearchAdapter(AppCompatActivity activity, List<Book> books, BookClickListener listener) {
        mBooks = books;
        this.mListener = listener;
        this.mActivity = activity;
//        BusStation.getBus().register(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemBookBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_book,
                parent,
                false
        );
        ViewHolder vh = new ViewHolder(binding);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Book book = mBooks.get(position);

        holder.binding.setBook(book);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onBookClicked(book);
                }
            }
        });

        holder.binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = mActivity.getSupportFragmentManager();
                ImageShowFragment showFragment = ImageShowFragment.newInstance(book.getImageUrl());
                showFragment.show(fm, ImageShowFragment.TAG);
            }
        });
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mBooks != null ? mBooks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ItemBookBinding binding;
        public ViewHolder(ItemBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

