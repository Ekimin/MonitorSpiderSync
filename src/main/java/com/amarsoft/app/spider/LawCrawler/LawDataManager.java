package com.amarsoft.app.spider.LawCrawler;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ymhe on 2017/1/9.
 * MonitorSpiderSync 诉讼一级监控
 */
public class LawDataManager implements MonitorSpiderSync {

    public void generateTask(List<MonitorModel> entMonitor) {

    }

    public boolean isSpidered(String flowID) {
        return false;
    }

    public boolean isSynchronized(List<MonitorModel> entList) {
        return false;
    }
}
