package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.zcsoftware.DBModel.DeviceDBProvider;
import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 8/2/13.
 */
public class GlobalDevList extends Activity {
    private String userName = "";
    private String password = "";
    private boolean isLoggedIn = false;

    private Intent loginIntent;
    public static final int VIEW_ID_REG = 0;
    public static final int VIEW_ID_LOC = 1;
    public static final int VIEW_ID_GLO = 2;
    public static final int VIEW_ID_DATA = 3;
	
	private int LOGIN_REQUEST = 0;
	
	// shared preference related
    SharedPreferences pref;
    SharedPreferences.Editor editor;// for log out


    private List<HWDevice> locDevList = new ArrayList<HWDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // shared preference related
        setContentView(R.layout.device_list_activity);
        poputateLocDevList(); /** initialize instances */
        populateListView(); /** generate list view */
        registerClickCallback(); /** register click effects */

        readUserInformation();
    }

    private void readUserInformation()
    {
        pref = getApplicationContext().getSharedPreferences(MainActivity.PREFERENCE_APP_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (pref.contains(MainActivity.PREFERENCE_APP_USERNAME))
        {
            this.userName = pref.getString(MainActivity.PREFERENCE_APP_USERNAME,"");
        }

        if (pref.contains(MainActivity.PREFERENCE_APP_PASSWORD))
        {
            this.password = pref.getString(MainActivity.PREFERENCE_APP_PASSWORD,"");
        }

    }

    private void poputateLocDevList() {
        /**
         *  hard coded dummy items
         */
        ContentResolver cr = getContentResolver();

        locDevList.clear();

        Cursor c = cr.query(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,null,null,null,null);

        if (null == c)
        {
            return;
        }

        if (c.moveToFirst())
        {
            do
            {
                HWDevice temp = new HWDevice();
                temp.buildHwDeviceByCursor(c);
                //items.add(temp);
                if(!locDevList.contains(temp)){
                    locDevList.add(0,temp);
                }
            }
            while(c.moveToNext());
        }
        //adpter.notifyDataSetChanged();

        c.close();

    }
    /**
     *  Direct the user to login or sign up
     */
    private void welcome(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Everything Tag Server:")
                .setMessage("Please login to continue.")
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    // guides to login
                    public void onClick(DialogInterface dialog, int id) {
                        loginIntent = new Intent(GlobalDevList.this, Login_activity.class);
                        /**
                         * Adds the FLAG_ACTIVITY_NO_HISTORY flag
                         * So that when press back button, wont go back to login view.
                        */
                        loginIntent.setFlags(loginIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivityForResult(loginIntent,LOGIN_REQUEST);
                    }
                })
                .setNeutralButton("Signup", new DialogInterface.OnClickListener() {
                    // guides to signup
                    public void onClick(DialogInterface dialog, int id) {
                        loginIntent = new Intent(GlobalDevList.this, cloudSignup.class);
                        loginIntent.setFlags(loginIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // same with above
                        loginIntent.putExtra("URL","https://gglasspuppy.appspot.com/register/index");
                        startActivity(loginIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();        // create and show the alert dialog
    }

    private void populateListView() {
        ArrayAdapter<HWDevice> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.dev_listview);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<HWDevice> {
        public MyListAdapter() {
            super(GlobalDevList.this, R.layout.item_view, locDevList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            // Find the car to work with.
            final HWDevice currentLocDev = locDevList.get(position);

            // Fill the image by type
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
            //imageView.setImageResource(currentLocDev.getImgID());
            imageView.setImageBitmap(BitmapFactory.decodeFile(currentLocDev.getImageName()));

            // local device name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_dev_alias);
            nameText.setText(currentLocDev.getDevName());



            // local device city:
            TextView cityText = (TextView) itemView.findViewById(R.id.item_dev_position);
            //cityText.setText(currentLocDev.getImageName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pref.getBoolean(MainActivity.PREFERENCE_APP_LOGINSTATUS,false)==false || pref.contains(MainActivity.PREFERENCE_APP_LOGINSTATUS)==false){
                        toastThis("haven't logged in");
                        welcome();// leads to dialog
                    } else{
                        /**
                         * if already logged in
                         * do something
                         */
                        toastThis("loggedin");
                        promptSendScannerToCloud(currentLocDev);
                    }

                }
            });
            return itemView;
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.dev_listview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {

                HWDevice clickedDev = locDevList.get(position);

                String message = "You clicked position " + position
                + " Which is hw called " + clickedDev.getDevName();
                Toast.makeText(GlobalDevList.this, message, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void promptSendScannerToCloud(HWDevice currentLocDevVal)
    {

        final HWDevice currentLocDev = currentLocDevVal;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Everything Tag Server:")
                .setMessage("Do you want send this device to server?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // guides to login
                    public void onClick(DialogInterface dialog, int id) {
                        sendScannerInfotoCloud(currentLocDev);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();        // create and show the alert dialog
    }

    private void sendScannerInfotoCloud(HWDevice currentLocDev)
    {
        class DownloadImageTask extends AsyncTask<String,String,String> {


            @Override
            protected String doInBackground(String... strings) {

                HttpPost httpRequest = new HttpPost(strings[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = null;
                try
                {
                    httpResponse = httpclient.execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        //SEND SUCCESS
                        int k = 0;
                        return "Success to send device to cloud ";
                    }
                    else
                    {
                        //send error
                        return "Failed to send device to cloud ";
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

                return "Failed to send device to cloud ";
            }

            @Override
            protected void onPostExecute(String result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GlobalDevList.this);
                builder.setTitle("Everything Tag Server:")
                        .setMessage(result)
                        .setCancelable(false)
                        .setIcon(R.drawable.ic_launcher)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            // guides to login
                            public void onClick(DialogInterface dialog, int id) {
                                //loginIntent = new Intent(GlobalDevList.this, Login_activity.class);
                                //startActivity(loginIntent);
                                return;
                            }
                        })

                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // something is wrong
                                // does not go back to previous act
                                // instead, it quits entire app
                                return;
                            }
                        });
                builder.create().show();        // create and show the alert dialog

            }
        }

        readUserInformation();
        //String message = "You clicked position " + "null"
        //        + " Which is hw called " + currentLocDev.getStrName();
        //Toast.makeText(LocalDevList.this, message, Toast.LENGTH_LONG).show();
        String urlInfo = "https://gglasspuppy.appspot.com/scanner/?";
        urlInfo += "devtype=" + HWDevice.getItfString(currentLocDev.getHwItfType());
        urlInfo += "&" + "macinfo=" + currentLocDev.getMacId();
        urlInfo += "&" + "username=" + userName;
        urlInfo += "&" + "pass=" + password;

        ContentResolver cr = GlobalDevList.this.getContentResolver();
        DeviceDBProvider.setDeviceCloudPost(cr,currentLocDev.getHwItfType(),currentLocDev.getMacId(),DeviceDBProvider.SETPOST_VALUE);

        new DownloadImageTask().execute(urlInfo);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void toastThis( String msg ){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onResume(){
        super.onResume();
        poputateLocDevList(); /** initialize instances */
        populateListView(); /** generate list view */
        //registerClickCallback(); /** register click effects */
    }


}