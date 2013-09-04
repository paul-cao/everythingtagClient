package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;

/**
 * Created by Taotao on 8/14/13.
 */
public class SearchDevice extends Activity implements Runnable{

    //private ImageView signalView;
    TextView distance_tv;
    private ImageView dot_iv;
    private ImageView signal_ndot_iv;
    int imagesToShow[] = { R.drawable.signal_dot_0 };

    BroadcastReceiver mReceiver;
    BroadcastReceiver mReceiver2;

    //Thread thread;
    //BluetoothAdapter mBluetoothAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_inof);

        Intent data = this.getIntent();

        final String name = data.getStringExtra("DeviceName");
        TextView tvName = (TextView)findViewById(R.id.tvSearchName);
        distance_tv = (TextView)findViewById(R.id.tvSearchDistanceValue);

        //signalView = (ImageView)findViewById(R.id.imageView);

        // signal none-dot-part image view
        signal_ndot_iv = (ImageView)findViewById(R.id.imageView);

        // dot image view
        dot_iv = (ImageView)findViewById(R.id.signal_dot);

        dot_iv.setImageResource(R.drawable.signal_dot_0);
        animate(dot_iv, imagesToShow, 0, true);

        tvName.setText(name);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final TextView tvRSSI = (TextView)findViewById(R.id.tvSearchRSSIValue);
        tvRSSI.setText("Acquiring RSSI value...");

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        /**
         *  Receiver #1
         *  handles RSSI and Signal View
         */
        mReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getName()!=null && device.getName().equals(name))
                    {
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                            //信号强度。
                            short rssi = intent.getExtras().getShort(
                                    BluetoothDevice.EXTRA_RSSI);
                            tvRSSI.setText(String.valueOf(rssi));

                            /**
                             * Graphic Part
                             */
                            if(rssi< -70){
                                imagesToShow[0]=R.drawable.signal_dot_1;
                                signal_ndot_iv.setImageResource(R.drawable.signal_1);
                                distance_tv.setText("very far");
                            } else if( rssi< -55 ){
                                imagesToShow[0]=R.drawable.signal_dot_2;
                                signal_ndot_iv.setImageResource(R.drawable.signal_2);
                                distance_tv.setText("far");
                            } else if( rssi< -44 ){
                                imagesToShow[0]=R.drawable.signal_dot_3;
                                signal_ndot_iv.setImageResource(R.drawable.signal_3);
                                distance_tv.setText("near");
                            } else if( rssi< 0 ){
                                imagesToShow[0]=R.drawable.signal_dot_4;
                                signal_ndot_iv.setImageResource(R.drawable.signal_4);
                                distance_tv.setText("face to face");
                            }
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    }
                }
            }
        };
        registerReceiver(mReceiver, filter);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        /**
         *  Receiver #2
         *  Handles end of receiving
         */
        mReceiver2 = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                {
                    mBluetoothAdapter.startDiscovery();
                }
            }
        };
        registerReceiver(mReceiver2, filter2);
        //if (!mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();
        }
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,1);
//        startActivityForResult(discoverableIntent,11);
        BluetoothAdapter mBluetoothAdapter1 = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter1.startDiscovery();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
       // if (resCode == Activity.RESULT_OK)

        BluetoothAdapter mBluetoothAdapter1 = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter1.disable();
        mBluetoothAdapter1.enable();
        {
            try
            {

                //Thread.sleep(10000);
                // BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                //while(true)
                {
                    mBluetoothAdapter1.startDiscovery();
                    //mBluetoothAdapter1.startDiscovery();
                    //mBluetoothAdapter1.startDiscovery();
                    //mBluetoothAdapter1.startDiscovery();

                    try
                    {
                        //Thread.sleep(10000);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();;
                    }
                }
        //thread.start();
        }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

    /**
     * The function responsible for fade-in fade-out effect
     *
     * @imageView <-- The View which displays the images
     * @images[] <-- Holds R references to the images to display
     * @imageIndex <-- index of the first image to show in images[]
     * @forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.
     */
    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

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

        animation.setAnimationListener(new Animation.AnimationListener() {
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

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(mReceiver!=null) unregisterReceiver(mReceiver);
        if(mReceiver2!=null) unregisterReceiver(mReceiver2);
    }

    @Override
    public void run() {

    }
}