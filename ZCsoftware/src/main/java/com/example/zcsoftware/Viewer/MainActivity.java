package com.example.zcsoftware.Viewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.TabActivity;
import android.widget.TabHost;

import com.example.zcsoftware.CloudService.EverythingTagCloudService;
import com.example.zcsoftware.R;

import android.os.ConditionVariable;

/**
 * Created by user on 7/28/13.
 */
public class MainActivity extends TabActivity {
    // TabSpec Names
    private static final String ADD_SPEC = "ADD new";
    private static final String GLOBAL_SPEC = "on cloud";
    private static final String LOCAL_SPEC = "SAVED";

    public static final ConditionVariable VAR_BLUETOOTH_DISCOVERY = new ConditionVariable(true);
    public static final String PREFERENCE_APP_ID = "everything_tag_preference";
    public static final String PREFERENCE_APP_USERNAME = "username";
    public static final String PREFERENCE_APP_PASSWORD = "password";
    public static final String PREFERENCE_APP_LOGINSTATUS = "loggedin";


    //private final ReentrantLook lock=new ReentrantLook();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_2_tabhost);
        TabHost tabHost = getTabHost();

        // save Tab
        TabHost.TabSpec add_spec = tabHost.newTabSpec(ADD_SPEC);
        add_spec.setIndicator(ADD_SPEC, getResources().getDrawable(R.drawable.transp));
        Intent add_Intent = new Intent(this, activity_LocSearchDevSelect.class);
        add_spec.setContent(add_Intent);

        // local Tab
        TabHost.TabSpec local_spec = tabHost.newTabSpec(LOCAL_SPEC);
        local_spec.setIndicator(LOCAL_SPEC, getResources().getDrawable(R.drawable.transp));
        Intent local_Intent = new Intent(this, LocalDevList.class);
        local_spec.setContent(local_Intent);

        // Global Tab
        TabHost.TabSpec global_spec = tabHost.newTabSpec(GLOBAL_SPEC);
        global_spec.setIndicator(GLOBAL_SPEC, getResources().getDrawable(R.drawable.transp));
        Intent global_Intent = new Intent(this, GlobalDevList.class);
        global_spec.setContent(global_Intent);

        // Adding all TabSpec to TabHost
        tabHost.addTab(local_spec); // Adding local_spec tab
        tabHost.addTab(global_spec); // Adding global tab
        tabHost.addTab(add_spec); // Adding add_spec tab



        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences(MainActivity.PREFERENCE_APP_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(pref.getBoolean(MainActivity.PREFERENCE_APP_LOGINSTATUS,false)==true || pref.contains(MainActivity.PREFERENCE_APP_LOGINSTATUS)==true)
        {
            Intent intent  = new Intent(this,EverythingTagCloudService.class);
            startService(intent);
        }
    }
}
