package br.com.katalog.katalogweb.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.adapters.CustomFilterArrayAdapter;
import br.com.katalog.katalogweb.adapters.FilmArrayAdapter;
import br.com.katalog.katalogweb.databinding.ActivityDigiMediaRegisterBinding;
import br.com.katalog.katalogweb.models.DigitalMedia;
import br.com.katalog.katalogweb.models.DigitalMediaDAO;
import br.com.katalog.katalogweb.models.Film;

public class DigiMediaRegisterActivity extends AppCompatActivity {

    public static final String EXTRA_MEDIA = "media";
    private ActivityDigiMediaRegisterBinding mBinding;
    private FilmArrayAdapter mFilmArrayAdapter;
    private boolean isNewMedia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_digi_media_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DigitalMedia media = getIntent().getParcelableExtra(EXTRA_MEDIA);

        isNewMedia = media == null;

        if (isNewMedia) {
            mBinding.setMedia(new DigitalMedia());
        } else {
            mBinding.setMedia(media);

            if (media.getMediaType().equalsIgnoreCase(DigitalMedia.BLURAY))
                mBinding.rbBluray.setChecked(true);
            if (media.getMediaType().equalsIgnoreCase(DigitalMedia.DVD))
                mBinding.rbDVD.setChecked(true);
        }

        /*mFilmArrayAdapter = new FilmArrayAdapter(
                this,
                R.layout.custom_item_dropdown_adapter,
                R.id.tvName
        );

        mBinding.actFilm.setAdapter(mFilmArrayAdapter);
        mBinding.actFilm.setThreshold(2);
        mBinding.actFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Film film = (Film) adapterView.getItemAtPosition(i);
                mBinding.getMedia().setFilm(film);
            }
        });*/

        List<String> companies = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.movie_companies))
        );
        CustomFilterArrayAdapter adapterCompany =
                new CustomFilterArrayAdapter(
                        this,
                        R.layout.custom_item_dropdown_adapter,
                        R.id.tvName,
                        companies
                );
        mBinding.actCompany.setAdapter(adapterCompany);
        mBinding.actCompany.setThreshold(1);

        //spinner caseType
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.case_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.spinner.setAdapter(adapter);
        if (!isNewMedia)
            mBinding.spinner.setSelection(media.getCaseType());


    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        if (isNewMedia) {
            actionBar.setTitle(getString(R.string.new_media));
        } else {
            actionBar.setTitle(mBinding.getMedia().getTitle());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void clickSave(View view) {

        if (TextUtils.isEmpty(mBinding.edtTitle.getText().toString().trim())) {
            mBinding.edtTitle.setError(getString(R.string.cant_be_empty));
            mBinding.edtTitle.requestFocus();
            return;
        }

        DigitalMedia media = mBinding.getMedia();
        media.setCaseType(mBinding.spinner.getSelectedItemPosition());
        DigitalMediaDAO mediaDAO = DigitalMediaDAO.getInstance();

        if (mBinding.rbBluray.isChecked())
            media.setMediaType(DigitalMedia.BLURAY);
        if (mBinding.rbDVD.isChecked())
            media.setMediaType(DigitalMedia.DVD);

        if (isNewMedia) {
            mediaDAO.insert(media);
        } else {
            mediaDAO.update(media);
        }

        finish();
    }
}
