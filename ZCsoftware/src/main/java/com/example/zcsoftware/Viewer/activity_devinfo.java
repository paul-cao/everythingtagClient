package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zcsoftware.DBModel.DeviceDBProvider;
import com.example.zcsoftware.R;

/**
 * Created by user on 7/28/13.
 */
public class activity_devinfo extends Activity {

    public static final String DEV_NAME = "dev_name";
    public static final String DEV_MAC = "dev_mac";
    public static final String DEV_ALIAS = "dev_alias";
    public static final String DEV_DESP = "dev_desp";
    public static final String DEV_TYPE = "dev_type";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_devinfo);

        Intent intent = this.getIntent();

        String dev_name = intent.getStringExtra(DEV_NAME);

        if (null == dev_name)
        {
            return;
        }

        final TextView tvDevNameVal = (TextView) findViewById(R.id.tvDevNameVal);
        tvDevNameVal.setText(dev_name);
        String dev_mac = intent.getStringExtra(DEV_MAC);

        if (null == dev_mac)
        {
            return;
        }
         final TextView tvDevMacVal = (TextView) findViewById(R.id.tvDevMacVal);
        tvDevMacVal.setText(dev_mac);

        final EditText edAlias = (EditText) findViewById(R.id.edDevAlias);
        final EditText edDesp = (EditText) findViewById(R.id.edDevDesp);

        Button btOk = (Button)findViewById(R.id.btDevInfoOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(DEV_NAME,tvDevNameVal.getText().toString());
                intent.putExtra(DEV_MAC, tvDevMacVal.getText().toString());
                intent.putExtra(DEV_ALIAS,edAlias.getText().toString());
                intent.putExtra(DEV_DESP,edDesp.getText().toString());

                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        Button btCal = (Button) findViewById(R.id.btDevInfoCancel);
        btCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });



        ContentResolver cr = this.getContentResolver();

        String where = DeviceDBProvider.KEY_NAME + " = " + "\"" + dev_name + "\"";

        Cursor c = null;
try
{
        c = cr.query(DeviceDBProvider.CONTENT_URI_DEVICE_NO,null,where,null,null);

        if (null == c)
        {
            return;
        }

        if (!c.moveToFirst())
        {
            return;
        }
}
catch (Exception ex)
{
    ex.printStackTrace();
}
        dev_mac = c.getString(DeviceDBProvider.MAC_COLUMN);
        tvDevMacVal.setText(dev_mac);

        String dev_alias = c.getString(DeviceDBProvider.ALIAS_COLUMN);
        edAlias.setText(dev_alias);

        String dev_desp = c.getString(DeviceDBProvider.DETAILS_COLUMN);
        edDesp.setText(dev_desp);

    }
}