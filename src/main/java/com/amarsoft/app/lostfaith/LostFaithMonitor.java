package com.amarsoft.app.lostfaith;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */

public class LostFaithMonitor implements MonitorSpiderSync{


    public List<String> generateTask(List<MonitorModel> entModel) {
        return null;
    }

    public boolean isSpidered(List<String> serialNo) {
        return false;
    }

    public boolean isSynchorized(List<MonitorModel> entList) {
        return false;
    }
}
