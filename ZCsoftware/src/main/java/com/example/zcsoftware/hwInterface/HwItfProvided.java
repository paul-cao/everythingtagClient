package com.example.zcsoftware.hwInterface;

/**
 * Created by user on 7/26/13.
 */
public interface HwItfProvided {



    boolean isHwItfEnable(int type);
    boolean isHwItfOpen(int type);
    void Enable(int type);

}
