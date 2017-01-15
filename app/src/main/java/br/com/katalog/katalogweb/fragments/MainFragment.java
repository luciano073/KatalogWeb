package br.com.katalog.katalogweb.fragments;


import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.FragmentMainBinding;
import br.com.katalog.katalogweb.models.DigitalMedia;
import br.com.katalog.katalogweb.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public static final String TAG = MainFragment.class.getName();
    private FragmentMainBinding mBinding;
    private DatabaseReference mArtistRef;
    private DatabaseReference mFilmsRef;
    private DatabaseReference mBooksRef;
    private DatabaseReference mBluraysRef;
    private ValueEventListener mFilmListener;
    private ValueEventListener mArtistListener;
    private ValueEventListener mBookListener;
    private ValueEventListener mBlurayListener;
    private boolean mArtistsHasBeenAnimeted;
    private boolean mFilmsHasBeenAnimeted;
    private boolean mBookHasBeenAnimeted;
    private boolean mBlurayHasBeenAnimeted;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistsHasBeenAnimeted = false;
        mFilmsHasBeenAnimeted = false;
        mBlurayHasBeenAnimeted = false;
        mBookHasBeenAnimeted = false;
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_main,
                container,
                false
        );

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
//        ArtistDAO.getInstance();
        mArtistRef = FirebaseDatabase.getInstance()
                .getReference(Constants.ARTISTS_DBREF);
        mFilmsRef = FirebaseDatabase.getInstance()
                .getReference(Constants.FILMS_DBREF);
        mBooksRef = FirebaseDatabase.getInstance()
                .getReference(Constants.BOOKS_DBREF);
        mBluraysRef = FirebaseDatabase.getInstance()
                .getReference(Constants.DIGITAL_MEDIA_DBREF);

    }

    @Override
    public void onResume() {
        super.onResume();
        mArtistListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final TextView tvTotalArtists = mBinding.tvTotalArtists;
                long total = dataSnapshot.getChildrenCount();
                if (!mArtistsHasBeenAnimeted) {
                    ValueAnimator animator = ValueAnimator.ofInt(1, (int) total);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int valor = (int) animation.getAnimatedValue();
                            tvTotalArtists.setText(String.valueOf(valor));
                        }
                    });
                    animator.setStartDelay(200);
                    animator.setDuration(2000);
                    animator.setTarget(tvTotalArtists);
                    animator.start();
                    mArtistsHasBeenAnimeted = true;
                } else {
                    mBinding.tvTotalArtists.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFilmListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final TextView tvTotalFilms = mBinding.tvTotalFilms;
                long total = dataSnapshot.getChildrenCount();
                if (!mFilmsHasBeenAnimeted) {
                    ValueAnimator animator = ValueAnimator.ofInt(1, (int) total);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int valor = (int) animation.getAnimatedValue();
                            tvTotalFilms.setText(String.valueOf(valor));
                        }
                    });
                    animator.setStartDelay(200);
                    animator.setDuration(2000);
                    animator.setTarget(tvTotalFilms);
                    animator.start();
                    mFilmsHasBeenAnimeted = true;
                } else {
                    mBinding.tvTotalFilms.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mBlurayListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final TextView tvTotalMedia = mBinding.tvTotalMedia;
                int totalDVD = 0;
                int totalBluray = 0;
                long total = dataSnapshot.getChildrenCount();
                if (!mBlurayHasBeenAnimeted) {
                    ValueAnimator animator = ValueAnimator.ofInt(1, (int) total);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int valor = (int) animation.getAnimatedValue();
                            tvTotalMedia.setText(String.valueOf(valor));
                        }
                    });
                    animator.setStartDelay(200);
                    animator.setDuration(2000);
                    animator.setTarget(tvTotalMedia);
                    animator.start();
                    mBlurayHasBeenAnimeted = true;
                } else {
                    mBinding.tvTotalMedia.setText(String.valueOf(total));
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DigitalMedia media = snapshot.getValue(DigitalMedia.class);
                    if (media.getMediaType().equalsIgnoreCase(DigitalMedia.BLURAY))
                        totalBluray++;
                    if (media.getMediaType().equalsIgnoreCase(DigitalMedia.DVD))
                        totalDVD++;
                }
                mBinding.tvBluray.setText(String.valueOf(totalBluray));
                mBinding.tvDVD.setText(String.valueOf(totalDVD));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mBookListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final TextView tvTotalBooks = mBinding.tvTotalBooks;
                long total = dataSnapshot.getChildrenCount();
                if (!mBookHasBeenAnimeted) {
                    ValueAnimator animator = ValueAnimator.ofInt(1, (int) total);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int valor = (int) animation.getAnimatedValue();
                            tvTotalBooks.setText(String.valueOf(valor));
                        }
                    });
                    animator.setStartDelay(200);
                    animator.setDuration(2000);
                    animator.setTarget(tvTotalBooks);
                    animator.start();
                    mBookHasBeenAnimeted = true;
                } else {
                    mBinding.tvTotalBooks.setText(String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mArtistRef.addValueEventListener(mArtistListener);
        mFilmsRef.addValueEventListener(mFilmListener);
        mBluraysRef.addValueEventListener(mBlurayListener);
        mBooksRef.addValueEventListener(mBookListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        mArtistRef.removeEventListener(mArtistListener);
        mFilmsRef.removeEventListener(mFilmListener);
        mBluraysRef.removeEventListener(mBlurayListener);
        mBooksRef.removeEventListener(mBookListener);
//        EventBus.getDefault().unregister(this);
    }


}
