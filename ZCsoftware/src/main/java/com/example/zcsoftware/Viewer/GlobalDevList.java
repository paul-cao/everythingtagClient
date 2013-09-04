package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.Intent;
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

import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 8/2/13.
 */
public class GlobalDevList extends Activity {
    public static final int VIEW_ID_REG = 0;
    public static final int VIEW_ID_LOC = 1;
    public static final int VIEW_ID_GLO = 2;
    public static final int VIEW_ID_DATA = 3;

    private List<HWDevice> locDevList = new ArrayList<HWDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_activity);
        poputateLocDevList(); /** initialize instances */
        populateListView(); /** generate list view */
        registerClickCallback(); /** register click effects */
    }

    private void poputateLocDevList() {
        /**
         *  hard coded dummy items
         */
        //locDevList.add(new HWDevice(R.drawable.type3, "trackr",3,"18IU908UIU90","Nanchang, China"));
        //locDevList.add(new HWDevice(R.drawable.type2, "keyboard",2,"I1829IHUH801","Shenyang, China"));
        //locDevList.add(new HWDevice(R.drawable.type2, "Keyboard",2,"129879127912","San Jose, CA USA"));
        locDevList.add(new HWDevice(R.drawable.type1, "Bike",1,"001060AA36F8","San Jose, CA USA"));
        locDevList.add(new HWDevice(R.drawable.type5, "earphone",5,"I1829IHUH801","Beijing, China"));
        locDevList.add(new HWDevice(R.drawable.type3, "trackr",3,"21HOI1390123","San Francisco, CA USA"));
        locDevList.add(new HWDevice(R.drawable.type4, "tile",4,"129879127912","Cupertino, CA USA"));
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
            HWDevice currentLocDev = locDevList.get(position);

            // Fill the image by type
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
            imageView.setImageResource(currentLocDev.getImgID());

            // local device name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_dev_alias);
            nameText.setText(currentLocDev.getStrName());

            // local device city:
            TextView cityText = (TextView) itemView.findViewById(R.id.item_dev_position);
            cityText.setText(currentLocDev.getCity());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String message = "You clicked position " + "null"
                    //        + " Which is hw called " + currentLocDev.getStrName();
                    //Toast.makeText(LocalDevList.this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(GlobalDevList.this,cloudSignup.class);

                    intent.putExtra("URL","gglasspuppy.appspot.com/register/index");
                    //intent.putExtra("URL","www.google.com");
                    //intent.putExtra("Mac",currentLocDev.getStrID());

                    startActivity(intent);
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
                + " Which is hw called " + clickedDev.getStrName();
                Toast.makeText(GlobalDevList.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}