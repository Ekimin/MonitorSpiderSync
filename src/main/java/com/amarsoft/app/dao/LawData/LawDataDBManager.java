package com.amarsoft.app.dao.LawData;

import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataDBManager implements MonitorDao{
    /**
     * 获取机构号
     * @param flowId Azkaban的flowId
     * @return
     */
    public String getBankIdByFlowId(String flowId) {
        //TODO：王军接口
        return null;
    }

    public void initMonitor(String flowId) {

    }

    public void initSpiderTask(List<MonitorModel> monitorModelList) {

    }

    public void insertMonitorList(List<MonitorModel> monitorModelList) {

    }
}
