package br.com.katalog.katalogweb.utils;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.katalog.katalogweb.databinding.ItemFilmBinding;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.Film;

public class FilmViewHolder extends RecyclerView.ViewHolder {
        public ItemFilmBinding mBinding;

        public FilmViewHolder(View view) {
            super(view);
            mBinding = DataBindingUtil.bind(view);
        }

        public void setFilm(Film film) {
            mBinding.setFilm(film);
        }

        /*public void setDirector(Artist director){
            mBinding.setDirector(director);
        }*/

    }