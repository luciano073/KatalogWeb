package br.com.katalog.katalogweb.adapters;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.ItemFilmBinding;
import br.com.katalog.katalogweb.fragments.ImageShowFragment;
import br.com.katalog.katalogweb.listeners.FilmClickListener;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.Film;

/**
 * Created by luciano on 24/08/2016.
 */
public class FilmSearchAdapter extends
        RecyclerView.Adapter<FilmSearchAdapter.ViewHolder> {

    List<Film> mFilms;
    FilmClickListener mListener;
    AppCompatActivity mActivity;
    public FilmSearchAdapter(AppCompatActivity activity, List<Film> films, FilmClickListener listener) {
        mFilms = films;
        this.mListener = listener;
        this.mActivity = activity;
//        BusStation.getBus().register(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFilmBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_film,
                parent,
                false
        );
        ViewHolder vh = new ViewHolder(binding);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Film film = mFilms.get(position);

        holder.binding.setFilm(film);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onFilmClicked(film);
                }
            }
        });

        holder.binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = mActivity.getSupportFragmentManager();
                ImageShowFragment showFragment = ImageShowFragment.newInstance(film.getImageUrl());
                showFragment.show(fm, ImageShowFragment.TAG);
            }
        });
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mFilms != null ? mFilms.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ItemFilmBinding binding;
        public ViewHolder(ItemFilmBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

