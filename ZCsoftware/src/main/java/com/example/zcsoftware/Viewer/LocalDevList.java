package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
        /** database part */
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

            if (null ==  currentLocDev.getImageName())
            {
                imageView.setImageResource(R.drawable.bt);
            }
            else
            {
                //imageView.setImageResource(currentLocDev.getImgID());
                imageView.setImageBitmap(BitmapFactory.decodeFile(currentLocDev.getImageName()));
                //imageView.setClickable(true);
                imageView.setFocusable(false);
            }

            // local device name:
            TextView nameText = (TextView) itemView.findViewById(R.id.item_dev_alias);
            nameText.setText(currentLocDev.getDisplayName());

            nameText.setFocusable(false);


            ImageView imageViewDetail = (ImageView)itemView.findViewById(R.id.item_dev_edit);
            // local device city:
            TextView cityText = (TextView) itemView.findViewById(R.id.item_dev_position);
            //cityText.setText(currentLocDev.getCity());

            cityText.setFocusable(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String message = "You clicked position " + "null"
                    //        + " Which is hw called " + currentLocDev.getStrName();
                    //Toast.makeText(LocalDevList.this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LocalDevList.this,SearchDevice.class);

                    if ((null != currentLocDev.getAliasName()) && (0 < currentLocDev.getAliasName().length()))
                    {
                        intent.putExtra(SearchDevice.DEV_NAME,currentLocDev.getAliasName());
                    }
                    else
                    {
                        intent.putExtra(SearchDevice.DEV_NAME,currentLocDev.getDevName());
                    }
                    intent.putExtra(SearchDevice.DEV_MAC,currentLocDev.getMacId());
                    intent.putExtra(SearchDevice.DEV_IMAGENAME,currentLocDev.getImageName());

                    startActivity(intent);

                }
            });



            imageViewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LocalDevList.this,DeviceDetail.class);

                    intent.putExtra(DeviceDetail.DEV_NAME,currentLocDev.getDevName());
                    intent.putExtra(DeviceDetail.DEV_MAC,currentLocDev.getMacId());
                    intent.putExtra(DeviceDetail.DEV_ALIAS,currentLocDev.getAliasName());
                    intent.putExtra(DeviceDetail.DEV_DESP,currentLocDev.getDescription());
                    intent.putExtra(DeviceDetail.DEV_LGCTYPE,currentLocDev.getLogicType());
                    intent.putExtra(DeviceDetail.DEV_IMGNAME,currentLocDev.getImageName());
                    intent.putExtra(DeviceDetail.DEV_HWTYPE,currentLocDev.getHwItfType());
                    intent.putExtra(DeviceDetail.DEV_SHOW_DEL,"yes");
                    int m = currentLocDev.getiID();
                    intent.putExtra(DeviceDetail.DEV_ID,currentLocDev.getiID());

                    startActivityForResult(intent, 1);
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

                intent.putExtra(DeviceDetail.DEV_NAME,clickedDev.getDevName());
                intent.putExtra(DeviceDetail.DEV_MAC,clickedDev.getMacId());


                intent.putExtra("Mac",clickedDev.getMacId());

                startActivityForResult(intent, 1);

            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent val)
    {
        if (resCode == Activity.RESULT_OK)
        {
            if (null == val)
            {
                return;
            }
            {
                String dev_name = val.getStringExtra(DeviceDetail.DEV_NAME);
                String dev_mac = val.getStringExtra(DeviceDetail.DEV_MAC);
                String dev_alias = val.getStringExtra(DeviceDetail.DEV_ALIAS);
                String dev_desp = val.getStringExtra(DeviceDetail.DEV_DESP);
                String dev_imagename = val.getStringExtra(DeviceDetail.DEV_IMGNAME);
                int dev_lgctype = val.getIntExtra(DeviceDetail.DEV_LGCTYPE,0);
                int dev_post = val.getIntExtra(DeviceDetail.DEV_POST,0);
                int dev_lost = val.getIntExtra(DeviceDetail.DEV_LOST,0);
                int id =  val.getIntExtra(DeviceDetail.DEV_ID,10000);
                int hwtype =  val.getIntExtra(DeviceDetail.DEV_HWTYPE,0);

                ContentResolver cr = this.getContentResolver();

                String where = DeviceDBProvider.KEY_ID + " = "  + String.valueOf(id);

                Cursor c = cr.query(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,null,where,null,null);
                if (c.getCount() == 0)
                {

                    ContentValues values = new ContentValues();
                    values.put(DeviceDBProvider.KEY_NAME,dev_name);
                    values.put(DeviceDBProvider.KEY_MAC,dev_mac);
                    values.put(DeviceDBProvider.KEY_ALIAS,dev_alias);
                    values.put(DeviceDBProvider.KEY_DETAILS,dev_desp);
                    values.put(DeviceDBProvider.KEY_ITFTYPE,hwtype);
                    values.put(DeviceDBProvider.KEY_IMAGENAME,dev_imagename);
                    values.put(DeviceDBProvider.KEY_LGCTYPE,dev_lgctype);
                    values.put(DeviceDBProvider.KEY_POST,dev_post);
                    values.put(DeviceDBProvider.KEY_LOST,dev_lost);

                    cr.insert(DeviceDBProvider.CONTENT_URI,values);
                }
                else
                {
                    //modify record
                    ContentValues values = new ContentValues();
                    values.put(DeviceDBProvider.KEY_ALIAS,dev_alias);
                    values.put(DeviceDBProvider.KEY_DETAILS,dev_desp);
                    values.put(DeviceDBProvider.KEY_IMAGENAME,dev_imagename);
                    values.put(DeviceDBProvider.KEY_LGCTYPE,dev_lgctype);

                    DeviceDBProvider.updateRecord(cr,id,values);
                }


                c.close();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        poputateLocDevList(); /** initialize instances */
        populateListView(); /** generate list view */
        //registerClickCallback(); /** register click effects */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}