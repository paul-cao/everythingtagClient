package com.example.zcsoftware.hwInterface;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by user on 7/26/13.
 */
public class HwItfProvidedImpl implements HwItfProvided{
    @Override
    public boolean isHwItfEnable(int type) {

        if (type == HWDevice.ITF_BLUETOOTH)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean isHwItfOpen(int type) {
        if (type == HWDevice.ITF_BLUETOOTH)
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return mBluetoothAdapter.isEnabled();
        }
        else
        {
            return false;
        }
    }

    @Override
    public void Enable(int type) {
        if (type == HWDevice.ITF_BLUETOOTH)
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.enable();
        }
    }

    public void startDiscovery(int type)
    {
        if (type == HWDevice.ITF_BLUETOOTH)
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.startDiscovery();
        }
    }

    public void cancelDiscovery(int type)
    {
        if (type == HWDevice.ITF_BLUETOOTH)
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.cancelDiscovery();
        }

    }
}
