package com.amarsoft.app.LawCrawler;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ymhe on 2017/1/9.
 * MonitorSpiderSync 诉讼一级监控
 */
public class LawCrawlerMonitor implements MonitorSpiderSync {
    public List<String> generateTask(List<MonitorModel> entMonitor) {

        return null;
    }

    public boolean isSpidered(List<String> serialNo) {
        return false;
    }

    public boolean isSynchorized(List<MonitorModel> entList) {
        return false;
    }
}
