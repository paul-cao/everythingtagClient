package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.view.animation.*;
import com.example.zcsoftware.R;
import android.view.animation.Animation.AnimationListener;

/**
 * Created by arthur on 9/2/13.
 */
public class Test_signalView extends Activity {

    private ImageView signalView;
    private ImageView dot_iv;
    int imagesToShow[] = { R.drawable.signal_dot_0 };

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_signal_view);

        signalView = (ImageView)findViewById(R.id.imageView);

        dot_iv = (ImageView)findViewById(R.id.signal_dot);
        dot_iv.setImageResource(R.drawable.signal_dot2);
        animate(dot_iv, imagesToShow, 0, true);
    }

    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 1000; // Configure time values here
        int timeBetween = 200;
        int fadeOutDuration = 400;

        //imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation((float)0.1, (float)1.0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);
        Animation fadeOut = new AlphaAnimation((float)1.0, (float)1.0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1,forever); //Calls itself until it gets to the end of the array
                }
                else {
                    if (forever == true){
                        animate(imageView, images, 0,forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }
}