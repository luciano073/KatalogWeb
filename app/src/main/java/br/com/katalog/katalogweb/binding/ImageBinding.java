package br.com.katalog.katalogweb.binding;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import br.com.katalog.katalogweb.models.Book;
import br.com.katalog.katalogweb.models.Film;
import br.com.katalog.katalogweb.utils.ImageUtils;

import static br.com.katalog.katalogweb.R.id.imageView;
import static br.com.katalog.katalogweb.R.string.length;

/**
 * Created by luciano on 18/09/2016.
 */
public class ImageBinding {

    @BindingAdapter({"android:src", "bind:placeholder", "bind:byteImg"})
    public static void loadImage(final ImageView imageView, final String url, final Drawable placeholder, byte[] byteImg){

        if (byteImg == null || byteImg.length == 0) {
            Picasso.with(imageView.getContext())
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .placeholder(placeholder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            PropertyValuesHolder anim1 =
                                    PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                            PropertyValuesHolder anim2 =
                                    PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                            ObjectAnimator animator =
                                    ObjectAnimator.ofPropertyValuesHolder(imageView, anim1, anim2);
                            animator.setDuration(1000);
                            animator.setInterpolator(new BounceInterpolator());
                            animator.start();
                        /*imageView.animate()
                                .rotation(360)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(1000);*/
                        }

                        @Override
                        public void onError() {
                            Picasso.with(imageView.getContext())
                                    .load(url)
                                    .fit()
                                    .placeholder(placeholder)
                                    .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            imageView.animate()
                                                    .rotation(360)
                                                    .setInterpolator(new OvershootInterpolator())
                                                    .setDuration(1000);
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });
        }else {
            imageView.setImageBitmap(ImageUtils.byte2Bitmap(byteImg));
        }
    }

    @BindingAdapter({"android:src", "bind:placeholder"})
    public static void loadImage(final ImageView imageView, final String url, final Drawable placeholder){

        Picasso.with(imageView.getContext())
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit()
                .placeholder(placeholder)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        PropertyValuesHolder anim1 =
                                PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                        PropertyValuesHolder anim2 =
                                PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                        ObjectAnimator animator =
                                ObjectAnimator.ofPropertyValuesHolder(imageView, anim1, anim2);
                        animator.setDuration(1000);
                        animator.setInterpolator(new BounceInterpolator());
                        animator.start();
                    /*imageView.animate()
                            .rotation(360)
                            .setInterpolator(new OvershootInterpolator())
                            .setDuration(1000);*/
                    }

                    @Override
                    public void onError() {
                        Picasso.with(imageView.getContext())
                                .load(url)
                                .fit()
                                .placeholder(placeholder)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        imageView.animate()
                                                .rotation(360)
                                                .setInterpolator(new OvershootInterpolator())
                                                .setDuration(1000);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                });
    }

    @BindingAdapter({"android:src", "bind:placeholder"})
    public static void loadImage(final ImageView view, Object object, final Drawable placeholder){
        if (object instanceof Book){
            final Book book = (Book) object;
            /*Picasso.with(view.getContext())
                    .load(book.getImageUrl())
                    .fit()
                    .placeholder(placeholder)
                    .into(view);*/

            Picasso.with(view.getContext())
                    .load(book.getImageUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .placeholder(placeholder)
                    .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            PropertyValuesHolder anim1 =
                                    PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                            PropertyValuesHolder anim2 =
                                    PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                            ObjectAnimator animator =
                                    ObjectAnimator.ofPropertyValuesHolder(view, anim1, anim2);
                            animator.setDuration(1000);
                            animator.setInterpolator(new BounceInterpolator());
                            animator.start();
                    /*imageView.animate()
                            .rotation(360)
                            .setInterpolator(new OvershootInterpolator())
                            .setDuration(1000);*/
                        }

                        @Override
                        public void onError() {
                            Picasso.with(view.getContext())
                                    .load(book.getImageUrl())
                                    .fit()
                                    .placeholder(placeholder)
                                    .into(view, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            view.animate()
                                                    .rotation(360)
                                                    .setInterpolator(new OvershootInterpolator())
                                                    .setDuration(1000);
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });
        }

        if (object instanceof Film){
            final Film film = (Film) object;
            /*Picasso.with(view.getContext())
                    .load(film.getImageUrl())
                    .fit()
                    .placeholder(placeholder)
                    .into(view);*/

            Picasso.with(view.getContext())
                    .load(film.getImageUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .placeholder(placeholder)
                    .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            PropertyValuesHolder anim1 =
                                    PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                            PropertyValuesHolder anim2 =
                                    PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                            ObjectAnimator animator =
                                    ObjectAnimator.ofPropertyValuesHolder(view, anim1, anim2);
                            animator.setDuration(1000);
                            animator.setInterpolator(new BounceInterpolator());
                            animator.start();
                    /*view.animate()
                            .rotation(360)
                            .setInterpolator(new OvershootInterpolator())
                            .setDuration(1000);*/
                        }

                        @Override
                        public void onError() {
                            Picasso.with(view.getContext())
                                    .load(film.getImageUrl())
                                    .fit()
                                    .placeholder(placeholder)
                                    .into(view, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            view.animate()
                                                    .rotation(360)
                                                    .setInterpolator(new OvershootInterpolator())
                                                    .setDuration(1000);
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                        }
                    });
        }
    }
}
