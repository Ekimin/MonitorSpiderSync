package com.amarsoft.app.dao.BaiduData;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ymhe on 2017/1/18.
 * MonitorSpiderSync
 */
public class BaiduDataDBManager implements MonitorDao, MonitorSpiderSync {

    /**
     * 初始化监控任务
     *
     * @param flowId
     */
    @Override
    public void initMonitor(String flowId) {

    }

    @Override
    public void initSpiderTask(List<MonitorModel> monitorModelList, String flowId) {

    }

    @Override
    public void generateTask(List<MonitorModel> entMonitor, String flowID) {

    }

    @Override
    public boolean isSpidered(String flowID) {
        return false;
    }

    @Override
    public boolean isSynchronized(List<MonitorModel> entList) {
        return false;
    }
}
