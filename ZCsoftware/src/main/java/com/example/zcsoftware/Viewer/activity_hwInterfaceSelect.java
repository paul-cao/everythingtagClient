package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;
import com.example.zcsoftware.hwInterface.HwItfProvided;
import com.example.zcsoftware.hwInterface.HwItfProvidedImpl;

/**
 * Created by user on 7/26/13.
 */

public class activity_hwInterfaceSelect extends Activity {

    public static final String HWITFSEL_TYPE = "hw_type";

    private int getHwItfSelect()
    {

        final RadioButton rtBlt = (RadioButton)findViewById(R.id.hwitfsel_blue);
        if (rtBlt.isChecked())
        {
            return HWDevice.ITF_BLUETOOTH;
        }

        final RadioButton rtBle = (RadioButton)findViewById(R.id.hwitfsel_ble);
        if (rtBle.isChecked())
        {
            return HWDevice.ITF_BLUETOOTH_LOW_ENERGY;
        }

        //final RadioButton rtZig = (RadioButton)findViewById(R.id.hwitfsel_zig);
        //if (rtZig.isChecked())
        {
          //  return HWDevice.ITF_ZIGBEE;
        }

        final RadioButton rthal = (RadioButton)findViewById(R.id.hwitf_sel_halo);
        if (rthal.isChecked())
        {
            return HWDevice.ITF_PHONE_HALO;
        }

        final RadioButton rtWifi = (RadioButton)findViewById(R.id.hwitf_sel_wifi);

        return HWDevice.ITF_WIFI;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.hwinf_select);

        HwItfProvided hwProvider = new HwItfProvidedImpl();

        final RadioButton rtBlt = (RadioButton)findViewById(R.id.hwitfsel_blue);
        rtBlt.setEnabled(hwProvider.isHwItfEnable(HWDevice.ITF_BLUETOOTH));

        final RadioButton rtBle = (RadioButton)findViewById(R.id.hwitfsel_ble);
        rtBle.setEnabled(hwProvider.isHwItfEnable(HWDevice.ITF_BLUETOOTH_LOW_ENERGY));

        //final RadioButton rtZig = (RadioButton)findViewById(R.id.hwitfsel_zig);
        //rtZig.setEnabled(hwProvider.isHwItfEnable(HWDevice.ITF_ZIGBEE));

        final RadioButton rthal = (RadioButton)findViewById(R.id.hwitf_sel_halo);
        rthal.setEnabled(hwProvider.isHwItfEnable(HWDevice.ITF_PHONE_HALO));

        final RadioButton rtWifi = (RadioButton)findViewById(R.id.hwitf_sel_wifi);
        rtWifi.setEnabled(hwProvider.isHwItfEnable(HWDevice.ITF_WIFI));

        Button btnSel = (Button)findViewById(R.id.hwitfsel_btSure);

        btnSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(HWITFSEL_TYPE,getHwItfSelect());
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

    }
}