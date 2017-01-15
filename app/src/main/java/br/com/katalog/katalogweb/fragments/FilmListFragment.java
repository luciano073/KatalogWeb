package br.com.katalog.katalogweb.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.activities.FilmRegisterActivity;
import br.com.katalog.katalogweb.adapters.FilmAdapter;
import br.com.katalog.katalogweb.adapters.FilmSearchAdapter;
import br.com.katalog.katalogweb.databinding.FragmentFilmListBinding;
import br.com.katalog.katalogweb.listeners.FilmClickListener;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.models.FilmDAO;
import br.com.katalog.katalogweb.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilmListFragment extends Fragment implements
        SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    public static final String TAG = FilmListFragment.class.getName();
    private FragmentFilmListBinding mBinding;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mFilmsRef;
    private FilmAdapter mAdapter;
    private List<Film> mFilmList;
    private List<Film> mFilmListResult;
    private ValueEventListener mListener;

    public FilmListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilmsRef = FirebaseDatabase.getInstance().getReference(Constants.FILMS_DBREF);
        mFilmsRef.keepSynced(true);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAdapter = new FilmAdapter(getContext(), mFilmsRef, new FilmClickListener() {
            @Override
            public void onFilmClicked(Film film) {
                Intent itFilmUpdate = new Intent(getActivity(), FilmRegisterActivity.class);
                itFilmUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                itFilmUpdate.putExtra(FilmRegisterActivity.EXTRA_FILM, film);
                startActivity(itFilmUpdate);
            }
        });
        setHasOptionsMenu(true);
//        EventBus.getDefault().register(this);
        mFilmList = new ArrayList<>();
//        setRetainInstance(true); this causes a crash in imageView onClickevent on FilmAdapter
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_film_list,
                container,
                false
        );


        mBinding.fabAddNewFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FilmRegisterActivity.class));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true); //to list in descendent order
        layoutManager.setStackFromEnd(true);  //descendent order
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(mAdapter);
        return mBinding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBinding.progressBar.setVisibility(View.GONE);
                if (dataSnapshot.getChildrenCount() == 0) {
                    mBinding.empty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.empty.setVisibility(View.GONE);
                }
                mFilmList.clear(); //To avoid duplicated objects in the list
                for (DataSnapshot filmSnapshot : dataSnapshot.getChildren()) {
                    final Film film = filmSnapshot.getValue(Film.class);
                    film.setId(filmSnapshot.getKey());
                    FilmDAO filmDAO = FilmDAO.getInstance();
                    film.setDirector(filmDAO.retrieveDirector(filmSnapshot));
                    film.setWriter(filmDAO.retrieveWriter(filmSnapshot));
                    film.setCast(filmDAO.retrieveCast(filmSnapshot));

                    mFilmList.add(film);
//                    Log.i(TAG, "film: " + film.getTitle());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mFilmsRef.addValueEventListener(mListener);
    }

    @Override
    public void onStop() {
        mFilmsRef.removeEventListener(mListener);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mAdapter.cleanup();
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_book_list, menu);
        menu.removeItem(R.id.action_search);

        MenuItem searchItem = menu.findItem(R.id.action_search_book);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        mFilmListResult = new ArrayList<>();
        Collections.sort(mFilmList, Film.titleComparetor);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mBinding.recyclerView.swapAdapter(mAdapter, false);
        mFilmListResult.clear();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {

            mFilmListResult.clear();
            for (Film f : mFilmList) {
                if (f.getNoDiacriticsTitle().toLowerCase().contains(newText)) {
                    mFilmListResult.add(f);
                }
            }

            FilmSearchAdapter adapter = new FilmSearchAdapter(
                    (AppCompatActivity) getActivity(),
                    mFilmListResult,
                    new FilmClickListener() {
                        @Override
                        public void onFilmClicked(Film film) {
                            Intent itFilmUpdate = new Intent(getActivity(), FilmRegisterActivity.class);
                            itFilmUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            itFilmUpdate.putExtra(FilmRegisterActivity.EXTRA_FILM, film);
                            startActivity(itFilmUpdate);
                        }
                    }
            );
            mBinding.recyclerView.swapAdapter(adapter, false);

        } else {
            mBinding.recyclerView.swapAdapter(mAdapter, false);
        }
//        mBinding.recyclerView.swapAdapter(mAdapter, false);
        return false;
    }
}
