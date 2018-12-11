package com.example.benq4kdemo;

import android.app.Application;

/**
 * Container of media data.
 *
 * @author felix.hu
 */
public class DemoApplication extends Application {
    private final static String TAG ="MediaContainerApplication";
    // singleton
    private static DemoApplication instance;

    private String hardwareName = null;
    
    private int[] panelSize = {0,0};

    private int[] osdSize = {0,0};
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * @return singleton instance.
     */
    public static DemoApplication getInstance() {
        return instance;
    }


    public final void setHardwareName(final String name) {
        hardwareName = name;
    }

    public final String getHardwareName() {
        return hardwareName;
    }
    
    public final void setPanelSize(final int[] config) {
        panelSize = config;
    }

    public final int[] getPanelSize() {
        return panelSize;
    }

    public final void setOsdSize(final int[] config) {
        osdSize = config;
    }

    public final int[] getOsdSize() {
        return osdSize;
    }

}
