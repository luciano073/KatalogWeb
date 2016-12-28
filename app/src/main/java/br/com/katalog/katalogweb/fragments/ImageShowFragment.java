package br.com.katalog.katalogweb.fragments;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;

import br.com.katalog.katalogweb.R;
import br.com.katalog.katalogweb.databinding.FragmentImageShowBinding;

import static android.R.attr.button;

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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
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
        /*View view = inflater.inflate(R.layout.fragment_image_show, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_dialog);
        Button button = (Button) view.findViewById(R.id.btn_dismiss);*/

       /* DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(15))
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mImageUri, imageView, options);*/

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/

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
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

        }


    }

}
