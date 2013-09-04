package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.zcsoftware.DBModel.DeviceDBProvider;
import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 8/2/13.
 */
public class LocalDevList extends Activity {
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

        /** hard code for testing */
        //locDevList.add(new HWDevice(R.drawable.type1, "Bike",1,"001060AA36F8","San Jose"));
        //locDevList.add(new HWDevice(R.drawable.type2, "Keyboard",2,"129879127912","San Jose"));
        //locDevList.add(new HWDevice(R.drawable.type3, "trackr",3,"21HOI1390123","San Francisco"));
        //locDevList.add(new HWDevice(R.drawable.type3, "trackr",3,"18IU908UIU90","Nanchang"));
        //locDevList.add(new HWDevice(R.drawable.type2, "keyboard",2,"I1829IHUH801","Shenyang"));

        /** database part */
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,null,null,null,null);

        if (null == c)
        {
            return;
        }

        if (c.moveToFirst())
        {
            do
            {
                HWDevice temp = new HWDevice(R.drawable.bt, "Bike",1,"001060AA36F8","San Jose");
                temp.setStrName(c.getString(DeviceDBProvider.NAME_COLUMN));
                temp.setType(c.getInt(DeviceDBProvider.TYPE_COLUMN));
                temp.setStrID(c.getString(DeviceDBProvider.IDENTIFY_COLUMN));
                //items.add(temp);
                if(!locDevList.contains(temp)){
                    locDevList.add(0,temp);
                }
            }
            while(c.moveToNext());
        }
        //adpter.notifyDataSetChanged();
    }

    private void populateListView() {
        ArrayAdapter<HWDevice> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.dev_listview);
        //list.setItemsCanFocus(true);
        list.setAdapter(adapter);
    }
    private class MyListAdapter extends ArrayAdapter<HWDevice> {
        public MyListAdapter() {
            super(LocalDevList.this, R.layout.item_view, locDevList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            itemView.setClickable(true);

            // Find the car to work with.
            final HWDevice currentLocDev = locDevList.get(position);

            // Fill the image by type
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
            imageView.setImageResource(currentLocDev.getImgID());
            //imageView.setClickable(true);
            imageView.setFocusable(false);

            // local device name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_dev_alias);
            nameText.setText(currentLocDev.getStrName());

            nameText.setFocusable(false);

            // local device city:
            TextView cityText = (TextView) itemView.findViewById(R.id.item_dev_position);
            cityText.setText(currentLocDev.getCity());

            cityText.setFocusable(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String message = "You clicked position " + "null"
                    //        + " Which is hw called " + currentLocDev.getStrName();
                    //Toast.makeText(LocalDevList.this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LocalDevList.this,SearchDevice.class);

                    intent.putExtra("DeviceName",currentLocDev.getStrName());
                    intent.putExtra("Mac",currentLocDev.getStrID());

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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int k = position;
                HWDevice clickedDev = locDevList.get(position);
                //String message = "You clicked position " + position
                //        + " Which is hw called " + clickedDev.getStrName();
                //Toast.makeText(LocalDevList.this, message, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LocalDevList.this,SearchDevice.class);

                intent.putExtra("DeviceName",clickedDev.getStrName());
                intent.putExtra("Mac",clickedDev.getStrID());

                startActivity(intent);

            }
        });
/*
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {

                HWDevice clickedDev = locDevList.get(position);
                String message = "You clicked position " + position
                        + " Which is hw called " + clickedDev.getStrName();
                Toast.makeText(LocalDevList.this, message, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LocalDevList.this,SearchDevice.class);

                intent.putExtra("DeviceName",clickedDev.getStrName());
                intent.putExtra("Mac",clickedDev.getStrID());

                startActivity(intent);
            }
        });
*/
    }

    @Override
    public void onResume(){
        super.onResume();
        //poputateLocDevList(); /** initialize instances */
        //populateListView(); /** generate list view */
        //registerClickCallback(); /** register click effects */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}