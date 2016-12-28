package br.com.katalog.katalogweb.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.adapters.ArtistArrayAdapter;
import br.com.katalog.katalogweb.databinding.FragmentDialogCastBinding;
import br.com.katalog.katalogweb.models.Artist;
import br.com.katalog.katalogweb.models.ArtistDAO;

/**
 * Created by luciano on 21/10/2016.
 */

public class CastDialogFragment extends DialogFragment {
    private static final String DIALOG_TAG = "cast_dialog";
    private Artist artist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CastDialog);
//        getDialog().setTitle("Novo artista");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentDialogCastBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_dialog_cast,
                container,
                false
        );

        getDialog().setTitle(getString(R.string.add_cast));

        binding.actCast.requestFocus();

        //Abre o teclado virtual ao exibir o Dialog
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        );

        binding.actCast.setAdapter(new ArtistArrayAdapter(
                getContext(),
                R.layout.custom_item_dropdown_adapter,
                R.id.tvName
        ));
        binding.actCast.setThreshold(1);
        binding.actCast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                artist = (Artist) parent.getItemAtPosition(position);
            }
        });

        binding.btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artistName = binding.actCast.getText().toString().trim();
                Activity activity = getActivity();
                if (artist == null && artistName.length() < 4){
                    binding.actCast.setError(getString(R.string.short_name));
                    binding.actCast.selectAll();
                }else if(artist == null && !TextUtils.isEmpty(artistName)){
                    artist = ArtistDAO.getInstance().createAndInsert(artistName);
                }

                if (activity instanceof AddingCast && artist != null){
                    AddingCast listener = (AddingCast) activity;
                    listener.artistAdded(artist);
                    dismiss();
                }
            }
        });

        return binding.getRoot();
    }

    public void open(FragmentManager fm){
        if (fm.findFragmentByTag(DIALOG_TAG) == null){
            show(fm, DIALOG_TAG);
        }
    }

    @Override
    public void onDestroy() {
        ArtistDAO.getInstance().unregisterFirebaseEventListener();
        super.onDestroy();
//        getDialog().getWindow().setLayout(500, 300);
    }

    public interface AddingCast{
        void artistAdded(Artist artist);
    }
}
