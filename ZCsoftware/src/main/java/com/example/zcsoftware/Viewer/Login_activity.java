package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zcsoftware.CloudService.EverythingTagCloudService;
import com.example.zcsoftware.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 9/4/13.
 */
public class Login_activity extends Activity {
    Intent inComingIntent;
    Intent resultIntent;
    Boolean loginSuccess = false;

    // shared preference related
    SharedPreferences pref;
    Editor editor;

    private EditText username_et;
    private EditText password_et;
    private TextView loginResult_tv;
    private Button login_submit_btn;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // shared preference related
        pref = getApplicationContext().getSharedPreferences("everything_tag_preference", MODE_PRIVATE);
        editor = pref.edit();

        inComingIntent = getIntent();
        Bundle Extras = inComingIntent.getExtras();

        username_et = (EditText)findViewById(R.id.login_username_et);
        password_et = (EditText)findViewById(R.id.login_password_et);
        loginResult_tv = (TextView)findViewById(R.id.loginResult_tv);
        login_submit_btn = (Button)findViewById(R.id.login_submit_btn);

        setUpListener();
    }



    private boolean processSignin()
    {
        String urlStr = "https://gglasspuppy.appspot.com/register/signin";

        HttpPost httpRequest = new HttpPost(urlStr);

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("username", this.username_et.getText().toString()));
        params.add(new BasicNameValuePair("pass", this.password_et.getText().toString()));


        HttpEntity httpentity = null;


        try {
            httpentity = new UrlEncodedFormEntity(params,"UTF-8");

            //StringEntity entity = new StringEntity(strbody);
            httpRequest.setEntity(httpentity);
            // 取得默认的HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            // HttpStatus.SC_OK表示连接成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                //send success
                return true;
            }
            else
            {
                // send error
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }




    }

    /**
     * online login verify here
     * change loginSuccess to true is logged in
     */
    private void setUpListener(){
        // clear user name in the text field
        username_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_et.setText("");
            }
        });
        // clear password in the text field
        password_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_et.setText("");
            }
        });
        // verify part on the submit button
        login_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verify here
                loginSuccess = processSignin();

                if (false == loginSuccess)
                {
                    Toast.makeText(getApplicationContext(), "Login error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    localVeryfy();
                }
            }
        });

    }

    private void localVeryfy(){
        if(loginSuccess == true){
            editor.putString(MainActivity.PREFERENCE_APP_USERNAME,username_et.getText().toString());
            editor.putString(MainActivity.PREFERENCE_APP_PASSWORD,password_et.getText().toString());
            editor.putBoolean(MainActivity.PREFERENCE_APP_LOGINSTATUS, true);
            editor.commit();
            refresh();
        }
    }

    /**
     * refresh textview and button
     * textview: login--->logged in as: username
     * button: submit---->back
     */
    private void refresh(){
        loginResult_tv.setText("logged in as "+username_et.getText().toString());
        login_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        login_submit_btn.setText("back");

        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences(MainActivity.PREFERENCE_APP_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(pref.getBoolean(MainActivity.PREFERENCE_APP_LOGINSTATUS,false)==true && pref.contains(MainActivity.PREFERENCE_APP_LOGINSTATUS)==true)
        {
            Intent intent  = new Intent(this,EverythingTagCloudService.class);
            startService(intent);
        }
    }
}