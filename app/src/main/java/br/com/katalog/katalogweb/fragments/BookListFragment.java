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
import br.com.katalog.katalogweb.activities.BookRegisterActivity;
import br.com.katalog.katalogweb.activities.FilmRegisterActivity;
import br.com.katalog.katalogweb.adapters.BookAdapter;
import br.com.katalog.katalogweb.adapters.BookSearchAdapter;
import br.com.katalog.katalogweb.adapters.FilmSearchAdapter;
import br.com.katalog.katalogweb.databinding.FragmentBookListBinding;
import br.com.katalog.katalogweb.listeners.BookClickListener;
import br.com.katalog.katalogweb.listeners.FilmClickListener;
import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.BookDAO;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.utils.Constants;
import br.com.katalog.katalogweb.utils.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookListFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener {

    public static final String TAG = BookListFragment.class.getName();
    private FragmentBookListBinding mBinding;
    private DatabaseReference mBookRef;
    private BookAdapter mAdapter;
    private ValueEventListener mListener;
    private List<Book> mBookList;
    private List<Book> mBookListResult;


    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookRef = FirebaseDatabase.getInstance().getReference(Constants.BOOK_DATABASE_ROOT_NODE);
        mBookRef.keepSynced(true);
        mAdapter = new BookAdapter(getContext(), mBookRef, new BookClickListener() {
            @Override
            public void onBookClicked(Book book) {
                Intent itBookUpdate = new Intent(getActivity(), BookRegisterActivity.class);
                itBookUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                itBookUpdate.putExtra(BookRegisterActivity.EXTRA_BOOK, book);
                startActivity(itBookUpdate);
            }
        });

        setHasOptionsMenu(true);

        mBookList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_book_list,
                container,
                false
        );

        mBinding.fabAddNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookRegisterActivity.class));
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
                mBookList.clear();//To avoid duplicated objects in the list
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);
                    book.setId(bookSnapshot.getKey());

                    BookDAO bookDAO = BookDAO.getInstance();
                    book.setWriter(bookDAO.retrieveWriter(bookSnapshot));
                    book.setDrawings(bookDAO.retrieveDrawings(bookSnapshot));
                    book.setColors(bookDAO.retrieveColors(bookSnapshot));

                    mBookList.add(book);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mBookRef.addValueEventListener(mListener);
    }

    @Override
    public void onStop() {
        mBookRef.removeEventListener(mListener);
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
        mBookListResult = new ArrayList<>();
        Collections.sort(mBookList, Book.tilteComparator);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mBinding.recyclerView.swapAdapter(mAdapter, false);
        mBookListResult.clear();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {

            mBookListResult.clear();
            for (Book book : mBookList) {
                if (book.getNoDiacriticsTitle().toLowerCase().contains(newText)) {
                    mBookListResult.add(book);
                }
            }

            BookSearchAdapter adapter = new BookSearchAdapter(
                    (AppCompatActivity) getActivity(),
                    mBookListResult,
                    new BookClickListener() {
                        @Override
                        public void onBookClicked(Book book) {
                            Intent itBookUpdate = new Intent(getActivity(), BookRegisterActivity.class);
                            itBookUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            itBookUpdate.putExtra(BookRegisterActivity.EXTRA_BOOK, book);
                            startActivity(itBookUpdate);
                        }
                    }
            );
            mBinding.recyclerView.swapAdapter(adapter, false);

        } else {
            mBinding.recyclerView.swapAdapter(mAdapter, false);
        }
        return false;
    }
}
