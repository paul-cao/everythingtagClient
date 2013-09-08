package com.example.zcsoftware.hwInterface;

import java.util.ArrayList;

/**
 * Created by user on 7/26/13.
 */
public class HwScannerBluetooth implements HwScanner {

    private static String[] testdata ={"TestDevice1","TestDevice2","TestDevice3","TestDevice4",
                       "TestDevice5","TestDevice6","TestDevice7","TestDevice8",
                       "TestDevice9","TestDevice10","TestDevice11","TestDevice12"};
    @Override
    public ArrayList<HWDevice> searchHwDevice() {
        ArrayList<HWDevice> temp = new ArrayList<HWDevice>();
        for (int i = 0; i < testdata.length; i++)
        {
            //temp.add(new HWDevice(testdata[i],HWDevice.ITF_BLUETOOTH));
        }

        return temp;
    }

}
