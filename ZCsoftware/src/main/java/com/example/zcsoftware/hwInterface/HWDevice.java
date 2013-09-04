package com.example.zcsoftware.hwInterface;

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

    private  int imgID;
    private String strName;
    /**
     * type = 1 --> bike
     * type = 2 --> keyboard
     * type = 3 --> trackr
     * type = 4 --> tile
     * type = 5 --> earphone
     */
    private int type;
    private String strID;
    private int iID;
    private String city;

    public HWDevice()
    { }

    /**
     *   constructor by arthur
     *   for testing MainActivity use
     *   --> inside MainActivity.populate()
     */
    public HWDevice(int imgID, String strName, int type, String strID, String city) {
        this.imgID = imgID;
        this.strName = strName;
        this.type = type;
        this.strID = strID;
        this.city = city;
    }

    public HWDevice(String name, int type)
    {
        this.strName = name;
        this.type = type;
        this.strID = name;
    }

    public int getImgID() {
        return imgID;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStrID() {
        return strID;
    }

    public void setStrID(String strID) {
        this.strID = strID;
    }

    public int getiID() {
        return iID;
    }

    public void setiID(int iID) {
        this.iID = iID;
    }

    public String getCity(){
        return city;
    }

    @Override
    public String toString()
    {
        return this.strName;
    }
}
