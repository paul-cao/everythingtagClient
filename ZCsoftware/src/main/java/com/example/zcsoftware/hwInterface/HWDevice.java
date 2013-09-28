package com.example.zcsoftware.hwInterface;

import android.database.Cursor;

import com.example.zcsoftware.DBModel.DeviceDBProvider;

/**
 * Created by user on 7/26/13.
 */
public class HWDevice {

    public static final int ITF_BLUETOOTH = 0;
    public static final int ITF_BLUETOOTH_LOW_ENERGY = 1;
    public static final int ITF_ZIGBEE = 2;
    public static final int ITF_PHONE_HALO = 3;
    public static final int ITF_WIFI = 4;
    public static final int ITF_INVALID = 5;

    public static final String[] ITF2STRING = {"BlueTooth","BlueTooth Low Energy","ZigBee","PhoneHalo","WiFi","Unknown"};

    /**
     * type = 1 --> bike
     * type = 2 --> keyboard
     * type = 3 --> trackr
     * type = 4 --> tile
     * type = 5 --> earphone
     */
    public static final int LGC_BIKE = 0;
    public static final int LGC_KEYBOARD = 1;
    public static final int LGC_TRACKR = 2;
    public static final int LGC_TILE = 3;
    public static final int LGC_EARPHONE = 4;
    public static final int LGC_INVALID = 5;

    public static final String[] LGC2STRING = {"Bike","Keyboard","Trackr","Tile","EarPhone","Unknown"};



    private int iID; //the primary key in EverythingTag DB
    private String MacId;  //MAC address of the device
    private String DevName;  //lily's iPhone	the name of the device, defined by the owner of the device (e.g. arthur's iPhone)
    private String AliasName;  //my white bike	the alias name of device, defined by the app user
    private String ImageName; //the picture of the device, saved in the app
    private String Description; //description of the device
    private int LogicType;   //BIKE/PHONE	the type of the device
    private double  latitude;    //
    private double  longitude;
    private int  HwItfType;    //hardware interface type, eg. Bluetooth, BLE ext.
    private int post;
    private int lost;

    //reserved
    private int RevInt1;
    private int RevInt2;
    private int RevInt3;
    private int RevInt4;
    private int RevInt5;

    private String RevStr1;
    private String RevStr2;
    private String RevStr3;
    private String RevStr4;
    private String RevStr5;



    public HWDevice()

    { }

    /**
     *   constructor by arthur
     *   for testing MainActivity use
     *   --> inside MainActivity.populate()
     */
    public HWDevice(String strImageName, String strName, int type, String strID, String city, int hwType) {
        this.DevName = strName;
        this.LogicType = type;
        this.MacId = strID;
        this.HwItfType = hwType;
        this.ImageName = strImageName;
    }



    public static String getItfString(int id)
    {
        if (id >= ITF_INVALID)
        {
            return ITF2STRING[ITF_INVALID];
        }
        else
        {
            return ITF2STRING[id];
        }
    }


    public static String getLgcString(int id)
    {
        if (id >= LGC_INVALID)
        {
            return LGC2STRING[LGC_INVALID];
        }
        else
        {
            return LGC2STRING[id];
        }
    }


    public int getiID() {
        return iID;
    }

    public void setiID(int iID) {
        this.iID = iID;
    }

    public String getMacId() {
        return MacId;
    }

    public void setMacId(String macId) {
        MacId = macId;
    }

    public String getDevName() {
        return DevName;
    }

    public void setDevName(String devName) {
        DevName = devName;
    }

    public String getAliasName() {
        return AliasName;
    }

    public void setAliasName(String aliasName) {
        AliasName = aliasName;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getLogicType() {
        return LogicType;
    }

    public void setLogicType(int logicType) {
        LogicType = logicType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getHwItfType() {
        return HwItfType;
    }

    public void setHwItfType(int hwItfType) {
        HwItfType = hwItfType;
    }

    public static int getItfBluetooth() {
        return ITF_BLUETOOTH;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public void buildHwDeviceByCursor( Cursor c)
    {
        if (null == c)
        {
            return;
        }
        this.setDevName(c.getString(DeviceDBProvider.NAME_COLUMN));
        this.setLogicType(c.getInt(DeviceDBProvider.LGCTYPE_COLUMN));
        this.setMacId(c.getString(DeviceDBProvider.MAC_COLUMN));
        this.setHwItfType(c.getInt(DeviceDBProvider.ITFTYPE_COLUMN));
        String aaa = c.getString(DeviceDBProvider.ALIAS_COLUMN);
        this.setAliasName(c.getString(DeviceDBProvider.ALIAS_COLUMN));
        this.setImageName(c.getString(DeviceDBProvider.IMAGENAME_COLUMN));
        this.setDescription(c.getString(DeviceDBProvider.DETAILS_COLUMN));
        this.setLatitude(c.getDouble(DeviceDBProvider.LOCLAN_COLUMN));
        this.setLongitude(c.getDouble(DeviceDBProvider.LOCLONG_COLUMN));
        this.setLost(c.getInt(DeviceDBProvider.LOST_COLUMN));
        this.setPost(c.getInt(DeviceDBProvider.POST_COLUMN));
        int l = c.getInt(DeviceDBProvider.ID_COLUMN);
        this.setiID(c.getInt(DeviceDBProvider.ID_COLUMN));


    }

    @Override
    public String toString()
    {
        return this.getDisplayName();

    }

    public String getDisplayName()
    {
        if ((null == this.getAliasName()) || (0 == this.getAliasName().length()))
        {
            return this.getDevName();
        }
        else
        {
            return this.getAliasName();
        }
    }
}
