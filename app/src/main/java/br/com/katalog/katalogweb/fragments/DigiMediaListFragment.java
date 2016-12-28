package br.com.katalog.katalogweb.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.activities.BookRegisterActivity;
import br.com.katalog.katalogweb.activities.DigiMediaRegisterActivity;
import br.com.katalog.katalogweb.adapters.DigiMediaAdapter;
import br.com.katalog.katalogweb.adapters.MediaSearchAdapter;
import br.com.katalog.katalogweb.databinding.FragmentDigiMediaListBinding;
import br.com.katalog.katalogweb.listeners.DigiMediaListener;
import br.com.katalog.katalogweb.models.DigitalMedia;
import br.com.katalog.katalogweb.utils.Constants;
import br.com.katalog.katalogweb.utils.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DigiMediaListFragment extends Fragment implements
        SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    public static final String TAG = DigiMediaListFragment.class.getName();
    private FragmentDigiMediaListBinding mBinding;
    private DatabaseReference mMediaRef;
    private DigiMediaAdapter mAdapter;
    private ValueEventListener mListener;
    private List<DigitalMedia> mMediaList;
    private List<DigitalMedia> mMediaListResult;

    public DigiMediaListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaRef = FirebaseDatabase.getInstance()
                .getReference(Constants.DIGITAL_MEDIA_DATABASE_ROOT_NODE);
        mMediaRef.keepSynced(true);

        mAdapter = new DigiMediaAdapter(mMediaRef, new DigiMediaListener() {
            @Override
            public void onDigiMediaClicked(DigitalMedia media) {
                Intent itMediaUpdate = new Intent(getActivity(), DigiMediaRegisterActivity.class);
                itMediaUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                itMediaUpdate.putExtra(DigiMediaRegisterActivity.EXTRA_MEDIA, media);
                startActivity(itMediaUpdate);
            }
        });

        setHasOptionsMenu(true);
        mMediaList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_digi_media_list,
                container,
                false
        );

        mBinding.fabAddNewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DigiMediaRegisterActivity.class));
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

                mMediaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DigitalMedia media = snapshot.getValue(DigitalMedia.class);
                    media.setId(snapshot.getKey());
                    mMediaList.add(media);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mMediaRef.addValueEventListener(mListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaRef.removeEventListener(mListener);
    }

    @Override
    public void onDestroy() {
        mAdapter.cleanup();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_media_list, menu);
        menu.removeItem(R.id.action_search);

        MenuItem searchItem = menu.findItem(R.id.action_search_media);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            mMediaListResult.clear();
            for (DigitalMedia media : mMediaList) {
                if (StringUtils.removeDiacritics(media.getTitle().toLowerCase()).contains(newText))
                    mMediaListResult.add(media);

            }

            MediaSearchAdapter adapter = new MediaSearchAdapter(mMediaListResult, new DigiMediaListener() {
                @Override
                public void onDigiMediaClicked(DigitalMedia media) {
                    Intent itMediaUpdate = new Intent(getActivity(), DigiMediaRegisterActivity.class);
                    itMediaUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    itMediaUpdate.putExtra(DigiMediaRegisterActivity.EXTRA_MEDIA, media);
                    startActivity(itMediaUpdate);
                }
            });
            mBinding.recyclerView.swapAdapter(adapter, false);
        } else {
            mBinding.recyclerView.swapAdapter(mAdapter, false);
        }
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        mMediaListResult = new ArrayList<>();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mBinding.recyclerView.swapAdapter(mAdapter, false);
        mMediaListResult.clear();
        return true;
    }
}
