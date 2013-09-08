package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zcsoftware.DBModel.DeviceDBProvider;
import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;
import com.example.zcsoftware.hwInterface.HwItfProvided;
import com.example.zcsoftware.hwInterface.HwItfProvidedImpl;

import java.util.ArrayList;

/**
 * Created by user on 7/26/13.
 */
public class activity_regdevselect extends Activity {

    private static final String STEP_KEY = "steps";
    private static final String STEP_DEVNAME = "dev_name";
    private static final String STEP_DEVID = "dev_id";

    private static final int STEPREG_INIT = 0;
    private static final int STEPREG_ITF_GET = 1;
    private static final int STEPREG_DEVSEL = 5;
    private static final int STEPREG_ENABLE_BLUETOOTH_DISCOVERY = 3;

    private static int hwType = HWDevice.ITF_INVALID;
    private int device_type_from_intent;
    private int REQUEST_ENABLE_BT;

    private static int iCurStep = STEPREG_INIT;

    ArrayList<HWDevice> nameList ;
    ArrayAdapter<HWDevice> listAdapter ;
    BroadcastReceiver mReceiver;
    BluetoothAdapter mBluetoothAdapter;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.regdev_select);

        Intent intent = this.getIntent();
        if (null == intent)
        {
            //internal error
            processReturn(Activity.RESULT_CANCELED);
        }

        Bundle extras = intent.getExtras();
        if( extras == null ){
            return;
        }

        // we got the requested device type from the intent
        device_type_from_intent = extras.getInt(activity_LocSearchDevSelect.HWITFSEL_TYPE);
        hwType = device_type_from_intent;

        final ListView lvDev = (ListView) findViewById(R.id.lvRegDevList);
        nameList = new ArrayList<HWDevice>();
        listAdapter = new ArrayAdapter<HWDevice>(this,android.R.layout.simple_list_item_1,nameList);
        lvDev.setAdapter(listAdapter);
        lvDev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HWDevice dev_name = (HWDevice)lvDev.getItemAtPosition(position);
                Intent intent = new Intent(activity_regdevselect.this,DeviceDetail.class);

                intent.putExtra(DeviceDetail.DEV_NAME,dev_name.toString());
                intent.putExtra(DeviceDetail.DEV_MAC,dev_name.getMacId());
                startActivityForResult(intent,STEPREG_DEVSEL);

            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    HWDevice temp = new HWDevice();
                    temp.setDevName(device.getName());
                    temp.setMacId(device.getAddress());
                    nameList.add(temp);
                    listAdapter.notifyDataSetChanged();
                }
            }

        };
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter==null){
            Toast.makeText(getApplicationContext(), "No bluetooth detected", 0).show();
            finish();
        }

//        mBluetoothAdapter.disable();
//        mBluetoothAdapter.enable();
        mBluetoothAdapter.startDiscovery();

    }


//    // when "select interface is pressed"
//    private void startItfSelect()
//    {
//        //select interface type of device
//        ContentResolver cr = this.getContentResolver();
//        //cr.delete(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,null,null);
//        Intent intent = new Intent(activity_regdevselect.this,activity_hwInterfaceSelect.class);
//        //intent.putExtra(STEP_KEY,STEPREG_ITF_GET);
//        startActivityForResult(intent,STEPREG_ITF_GET);
//        iCurStep = STEPREG_ITF_GET;
//    }

    //return next step
    private void ProcessDevReg(int iStep, Intent val)
    {
        switch(iStep)
        {
//            case STEPREG_INIT:
//            {
//                startItfSelect();
//            }
//            break;


            case STEPREG_ITF_GET:
            {
                //list all of device of this type, save info into database after user select

                //init hw itf type
                //hwType = val.getIntExtra(activity_hwInterfaceSelect.HWITFSEL_TYPE,HWDevice.ITF_BLUETOOTH);

                //if (hwType == HWDevice.ITF_INVALID)
                //{
                    //return;
                //}

                //loadAvailableDevice();

                HwItfProvided provider = new HwItfProvidedImpl();

                if (!(provider.isHwItfOpen(hwType)))
                {
                    provider.Enable(hwType);
                }

                provider.startDiscovery(hwType);

            }
                break;


            case STEPREG_DEVSEL:
                //back to main activity
                //get the device information, save data and back to main activity
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

                ContentResolver cr = this.getContentResolver();

                String where = DeviceDBProvider.KEY_ITFTYPE + " = "  + String.valueOf(hwType)  +"  AND "
                                + DeviceDBProvider.KEY_MAC + " = " +"\"" +  dev_mac +"\"" + " AND " +DeviceDBProvider.KEY_NAME
                                + " = "  +"\"" + dev_name +"\"";

                if (cr.query(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,null,where,null,null).getCount() == 0)
                {

                    ContentValues values = new ContentValues();
                    values.put(DeviceDBProvider.KEY_NAME,dev_name);
                    values.put(DeviceDBProvider.KEY_MAC,dev_mac);
                    values.put(DeviceDBProvider.KEY_ALIAS,dev_alias);
                    values.put(DeviceDBProvider.KEY_DETAILS,dev_desp);
                    values.put(DeviceDBProvider.KEY_ITFTYPE,hwType);
                    values.put(DeviceDBProvider.KEY_IMAGENAME,dev_imagename);
                    values.put(DeviceDBProvider.KEY_LGCTYPE,dev_lgctype);
                    values.put(DeviceDBProvider.KEY_POST,dev_post);
                    values.put(DeviceDBProvider.KEY_LOST,dev_lost);

                    cr.insert(DeviceDBProvider.CONTENT_URI,values);
                }




                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mBluetoothAdapter.cancelDiscovery();
                //this.unregisterReceiver()
                this.setResult(Activity.RESULT_OK);
                this.finish();
            }
                break;
        }
        return ;
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        if (resCode == Activity.RESULT_CANCELED)
        {
            //error process
        }
        else
        {
            ProcessDevReg(reqCode,data);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(mReceiver);
    }



    public void processReturn(int iRet)
    {
        Intent it = new Intent();
        this.setResult(iRet);
        this.finish();
    }
}