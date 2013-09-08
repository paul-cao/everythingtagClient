package com.example.zcsoftware.Viewer;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.zcsoftware.hwInterface.HWDevice;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.example.zcsoftware.R;
import java.util.ArrayList;

/**
 * Created by user on 7/28/13.
 *
 * Responsible for
 * 1. new device register
 * 2. local device scanner
 *
 */
public class activity_LocSearchDevSelect extends Activity {

    private static final int STEPLOC_DEVREG = 1;
    private static final int STEPLOC_DEV_GET = 2;
    private static final int STEP_DEV_SEARCH = 3;

    // for sending out the intend to:
    // the list displaying bluetooth devices
    public static final String HWITFSEL_TYPE = "hw_type";

    private Button bt_btn;
    private Button ble_btn;
    private Button wifi_btn;
    private Button ph_btn;
    private Button test_btn;

    private ArrayList<HWDevice> items;
    private ArrayAdapter<HWDevice> adpter;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_type_select);

//        items = new ArrayList<HWDevice>();
//        final ListView lvItems = (ListView)findViewById(R.id.lvLocSearchDevSel);
//        adpter = new ArrayAdapter<HWDevice>(this,android.R.layout.simple_list_item_1,items);
//        lvItems.setAdapter(adpter);

        bt_btn = (Button)findViewById(R.id.addnew_bt_btn);
        ble_btn = (Button)findViewById(R.id.addnew_ble_btn);
        wifi_btn = (Button)findViewById(R.id.addnew_wifi_btn);
        ph_btn = (Button)findViewById(R.id.addnew_ph_btn);
        test_btn = (Button)findViewById(R.id.addnew_test_btn);


        /**
         *      when "Search for Bluetooth device is pressed"
         * */
        bt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_LocSearchDevSelect.this, activity_regdevselect.class);
                intent.putExtra(HWITFSEL_TYPE, HWDevice.ITF_BLUETOOTH);
                setResult(Activity.RESULT_OK,intent);
                startActivity(intent);
            }
        });

        /**
         *      For testing
         * */
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_LocSearchDevSelect.this, DeviceDetail.class);
                //intent.putExtra(HWITFSEL_TYPE, HWDevice.ITF_BLUETOOTH);
                //setResult(Activity.RESULT_OK,intent);
                startActivity(intent);
            }
        });



    }

}