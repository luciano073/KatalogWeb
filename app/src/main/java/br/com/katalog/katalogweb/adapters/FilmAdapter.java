package br.com.katalog.katalogweb.adapters;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.fragments.ImageShowFragment;
import br.com.katalog.katalogweb.listeners.FilmClickListener;
import br.com.katalog.katalogweb.listeners.FilmEventBus;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.models.FilmDAO;
import br.com.katalog.katalogweb.utils.Constants;
import br.com.katalog.katalogweb.utils.FilmViewHolder;

/**
 * Created by luciano on 28/08/2016.
 */
public class FilmAdapter extends FirebaseRecyclerAdapter<Film, FilmViewHolder> {

    private static final String TAG = "FilmAdapter";
    FilmClickListener mListener;
    Context mContext;

    public FilmAdapter(Context context, Query ref, FilmClickListener listener) {
        super(Film.class, R.layout.item_film, FilmViewHolder.class, ref);
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected Film parseSnapshot(DataSnapshot snapshot) {
        final Film film = snapshot.getValue(Film.class);
        film.setId(snapshot.getKey());
        FilmDAO filmDAO = FilmDAO.getInstance();

        film.setDirector(filmDAO.retrieveDirector(snapshot));

        film.setWriter(filmDAO.retrieveWriter(snapshot));

        film.setCast(filmDAO.retrieveCast(snapshot));

        filmDAO.cleanUp();

        return film;
    }

    @Override
    protected void populateViewHolder(final FilmViewHolder viewHolder, final Film model, int position) {

        viewHolder.setFilm(model);

        viewHolder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFilmClicked(model);
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

        viewHolder.mBinding.executePendingBindings();


    }


}
