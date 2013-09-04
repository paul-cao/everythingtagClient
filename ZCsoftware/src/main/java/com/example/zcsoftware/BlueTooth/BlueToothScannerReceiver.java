package com.example.zcsoftware.BlueTooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.zcsoftware.hwInterface.HWDevice;

import java.util.ArrayList;

/**
 * Created by user on 7/28/13.
 */

//scanner is responsible for get all of device information have found
public class BlueToothScannerReceiver extends BroadcastReceiver {
    ArrayAdapter<HWDevice> lvItems;
    ArrayList<HWDevice> data;

    public BlueToothScannerReceiver(ArrayAdapter<HWDevice> lv, ArrayList<HWDevice> array)
    {
        lvItems = lv;
        data = array;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView
            HWDevice temp = new HWDevice();
            temp.setStrName(device.getName());
            temp.setStrID(device.getAddress());
            data.add(temp);
            lvItems.notifyDataSetChanged();
        }
    }
}
