package com.example.zcsoftware.hwInterface;

/**
 * Created by user on 7/26/13.
 */
public class HwScannerFactory {
    public static HwScanner createHwScanner(int type)
    {
        if (type == HWDevice.ITF_BLUETOOTH)
        {
            return new HwScannerBluetooth();
        }
        else
        {
            return null;
        }
    }
}
