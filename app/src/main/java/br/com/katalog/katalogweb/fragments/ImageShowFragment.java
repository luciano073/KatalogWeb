package br.com.katalog.katalogweb.fragments;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.FragmentImageShowBinding;

/**
 * Created by luciano on 22/09/2016.
 */

public class ImageShowFragment extends DialogFragment {
    public static final String TAG = "ImageShowFragment";
    FragmentImageShowBinding binding;
//    ImageLoader imageLoader;

    public ImageShowFragment() {
        // Required empty public constructor
    }

    public static ImageShowFragment newInstance(String imageUri){
        Bundle bundle = new Bundle();
        bundle.putString("uri", imageUri);
        ImageShowFragment fragment = new ImageShowFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ImageDialog);
//        mImageUri = getArguments().getString("uri");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_image_show,
                container,
                false
        );

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding.setImageUrl(getArguments().getString("uri"));

        binding.btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog(); //dica para controlar o tamanho da tela do dialog
        if (dialog != null) {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int) (width / 0.65);
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        /*Display display = getActivity().getWindowManager().getDefaultDisplay();
        int height = (int) (display.getHeight() * 0.9);
        int width = (int) (height * 0.65);
        binding.mainView.getLayoutParams().height = height;
        binding.mainView.getLayoutParams().width = width;*/
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
