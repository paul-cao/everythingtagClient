package com.example.zcsoftware.CloudService;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Xml;

import com.example.zcsoftware.DBModel.DeviceDBProvider;
import com.example.zcsoftware.R;
import com.example.zcsoftware.Viewer.MainActivity;
import com.example.zcsoftware.Viewer.cloudSignup;
import com.example.zcsoftware.hwInterface.HWDevice;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Taotao on 9/6/13.
 */
public class EverythingTagCloudService extends Service {
    String username ;
    String password ;

    public static final String DEV_UPDATETIME = "updateTime";
    public static final String DEV_GEOINFO = "geoinfo";

    Timer readTimer ;
    Timer reportTimer;

    BroadcastReceiver mReceiver;

    BluetoothAdapter mBluetoothAdapter;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences pref;
        pref = getApplicationContext().getSharedPreferences(MainActivity.PREFERENCE_APP_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (pref.contains(MainActivity.PREFERENCE_APP_USERNAME))
        {
            this.username = pref.getString(MainActivity.PREFERENCE_APP_USERNAME,"");
        }

        if (pref.contains(MainActivity.PREFERENCE_APP_PASSWORD))
        {
            this.password = pref.getString(MainActivity.PREFERENCE_APP_PASSWORD,"");
        }


        if (null != readTimer)
        {
            readTimer.cancel();
        }

        readTimer = new Timer("readDevice");

        readTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readDeviceInfoFromCloud();
                    }
                }).start();

            }
        },0,60*1000);


        //////

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (null != reportTimer)
        {
            reportTimer.cancel();
        }

        reportTimer = new Timer("reportDevice");

        reportTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

                if (null == mReceiver)
                {
                    mReceiver = new BroadcastReceiver()
                    {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            class ReportTask extends AsyncTask<String,String,String>
                            {

                                @Override
                                protected String doInBackground(String... strings) {
                                    reportDeviceInfo(HWDevice.getItfString(HWDevice.ITF_BLUETOOTH),strings[0]);
                                    return "";
                                }
                            }

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

                                //reportDeviceInfo(HWDevice.getItfString(HWDevice.ITF_BLUETOOTH),device.getAddress());

                                new ReportTask().execute(device.getAddress());
                            }
                        }

                    };

                }
                else
                {
                    unregisterReceiver(mReceiver);
                }
                //registerReceiver(mReceiver, filter);



                MainActivity.VAR_BLUETOOTH_DISCOVERY.block();

                mBluetoothAdapter.cancelDiscovery();
                mBluetoothAdapter.startDiscovery();
                registerReceiver(mReceiver, filter);

            }
        },0,60*1000);

        return 0;
    }
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Map<String,String> parseReadInfo(String strInfo)
    {
        Map<String,String> retMap = new HashMap<String, String>();

        if (null == strInfo)
        {
            return retMap;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //List<Message> messages = new ArrayList<Message>();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(new InputSource(new ByteArrayInputStream(strInfo.getBytes("utf-8"))));
            Element root = dom.getDocumentElement();
            NodeList devices = root.getElementsByTagName("device");

            for (int i = 0; i < devices.getLength(); i++)
            {
                Node device = devices.item(i);

                NodeList properties = device.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++)
                {
                    String proName = properties.item(j).getNodeName();
                    if (proName.equalsIgnoreCase(EverythingTagCloudService.DEV_UPDATETIME))
                    {
                        //update_time
                        String updatetime = properties.item(j).getFirstChild().getNodeValue();
                        retMap.put(EverythingTagCloudService.DEV_UPDATETIME,updatetime);
                    }
                    else if (proName.equalsIgnoreCase(EverythingTagCloudService.DEV_GEOINFO))
                    {
                        //geoinfo
                        String geoinfo = properties.item(j).getFirstChild().getNodeValue();
                        retMap.put(EverythingTagCloudService.DEV_GEOINFO,geoinfo);
                    }
                    else
                    {
                        //unknown
                    }
                }
            }


        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }



        return retMap;
    }


    private void processReadDeviceFromCloud(HWDevice dev)
    {
        String url = "https://gglasspuppy.appspot.com/read/?devtype=" + HWDevice.getItfString(dev.getHwItfType()) +
                "&macinfo=" + dev.getMacId() + "&username=" + username + "&pass=" + password;

        //new DownloadImageTask().execute(url);
        HttpGet httpRequest = new HttpGet(url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse httpResponse = null;
        try
        {
            httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //SEND SUCCESS
                int k = 0;

                String content = EntityUtils.toString(httpResponse.getEntity());
                Map<String,String> retMap = EverythingTagCloudService.parseReadInfo(content);

                String strtime = retMap.get(EverythingTagCloudService.DEV_UPDATETIME);
                if (null == strtime)
                {
                    return;
                }
                //this.strUpateTime = strtime;

                String strLocation = retMap.get(EverythingTagCloudService.DEV_GEOINFO);
                if (null == strLocation)
                {
                    return;
                }

                double lanVal = dev.getLatitude();
                double logVal = dev.getLongitude();
                StringTokenizer tokenizer = new StringTokenizer(strLocation,",");
                String strlan = tokenizer.nextToken();
                String strlog = tokenizer.nextToken();

                double dlan = Double.valueOf(strlan);
                double dlog = Double.valueOf(strlog);
                int itfType = dev.getHwItfType();
                String macinfo = dev.getMacId();

                if ((Math.abs(dlan - lanVal) < 0.0001) && (Math.abs(dlog - logVal) < 0.0001))
                {
                    //equal
                    sendNotification(strLocation);
                    return ;
                }
                else
                {
                    //update database
                    ContentResolver cr = EverythingTagCloudService.this.getContentResolver();
                    DeviceDBProvider.updateLocationinfo(cr,dlan,dlog,itfType,macinfo);

                    //send notification
                    sendNotification(strLocation);

                }
            }
            else
            {
                //send error
                return ;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    private void sendNotification(String strloc)
    {
        Notification noti = new Notification();
        noti.defaults |= Notification.DEFAULT_LIGHTS;
        noti.defaults |= Notification.DEFAULT_SOUND;

        noti.tickerText = "Location of your device is update";
        noti.icon = R.drawable.ttype4;

        PendingIntent contentIntent = PendingIntent.getActivity
                (EverythingTagCloudService.this, 0,new Intent(EverythingTagCloudService.this,cloudSignup.class), 0);

        noti.setLatestEventInfo(this.getApplicationContext(),"EverythingTag Location update","Your device is at " + strloc,contentIntent);

        NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,noti);

    }


    private void readDeviceInfoFromCloud()
    {
        ContentResolver cr = this.getContentResolver();
        String where = DeviceDBProvider.KEY_POST + " = " + String.valueOf(DeviceDBProvider.SETPOST_VALUE);
        Cursor c = cr.query(DeviceDBProvider.CONTENT_URI_DEVICE_ALL,null,where,null,null);
        List<HWDevice> devList = new ArrayList<HWDevice>();
        if ((null == c) || (c.getCount() == 0))
        {

                return;
        }

        if (c.moveToFirst())
        {
            do
            {

                HWDevice temp = new HWDevice();
                temp.buildHwDeviceByCursor(c);
                devList.add(temp);

            }while(c.moveToNext());
        }

        c.close();

        for (HWDevice dev : devList)
        {

            processReadDeviceFromCloud(dev);
            try {
                Thread.sleep(1000);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }


    private String generateXMLBody(String devtype, String macinfo, String geoinfo)
    {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "devices");
            //serializer.attribute("", "number", String.valueOf(messages.size()));
            //for (Message msg: messages)
            {
                serializer.startTag("", "device");

                //erializer.attribute("", "date", msg.getDate());
                serializer.startTag("", "devtype");
                serializer.text(devtype);
                serializer.endTag("", "devtype");


                serializer.startTag("", "macinfo");
                serializer.text(macinfo);
                serializer.endTag("", "macinfo");


                serializer.startTag("", "geoinfo");
                serializer.text(geoinfo);
                serializer.endTag("", "geoinfo");


            }
            serializer.endTag("", "device");

            serializer.endTag("", "devices");

            serializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return writer.toString();
    }

    private void reportDeviceInfo(String devtype, String macinfo)
    {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // Require coarse accuracy
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Low power consumption
        criteria.setAltitudeRequired(false); // No need for altitude
        criteria.setBearingRequired(false); // No need for bearing
        criteria.setSpeedRequired(false); // No need for speed
        criteria.setCostAllowed(false); // Permit to have an associated cost

        String serviceString = Context.LOCATION_SERVICE;
        LocationManager locationManager;
        locationManager = (LocationManager)getSystemService(serviceString);
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(bestProvider);

        String strLoc = null;

        if (null == location)
        {
            strLoc = "1.89,2.99";
        }
        else
        {
            strLoc = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        }





        String strbody = generateXMLBody(devtype,macinfo,strLoc);

        if (null == strbody)
        {
            return ;
        }

        String urlStr = "https://gglasspuppy.appspot.com/report/";

        HttpPost httpRequest = new HttpPost(urlStr);

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("", strbody));

        HttpEntity httpentity = null;


        try {
            httpentity = new UrlEncodedFormEntity(params,"UTF-8");

            StringEntity entity = new StringEntity(strbody);
            //httpRequest.setEntity(httpentity);
            httpRequest.setEntity(entity);
            // 取得默认的HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 取得HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            // HttpStatus.SC_OK表示连接成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                //send success
            }
            else
            {
                // send error
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }





    }



}
