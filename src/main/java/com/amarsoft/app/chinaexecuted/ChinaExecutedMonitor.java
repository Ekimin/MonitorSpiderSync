package com.amarsoft.app.chinaexecuted;

import com.amarsoft.app.common.MonitorSpiderSync;

import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public class ChinaExecutedMonitor implements MonitorSpiderSync {
    public List<String> generatTask(MonitorSpiderSync monitorSpiderSync) {
        return null;
    }

    public boolean isSpidered(MonitorSpiderSync monitorSpiderSync, List<String> serialNo) {
        return false;
    }

    public boolean isSynchorized(MonitorSpiderSync monitorSpiderSync, List<String> entList) {
        return false;
    }
}
