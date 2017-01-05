package com.amarsoft.app.dao.chinaexecuted;

import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public class ChinaExecutedDao implements MonitorDao{

    public boolean isHasEntName(String entName) {
        return false;
    }

    public void insertMonitorList(List<MonitorModel> monitorModelList) {

    }

    public void updateMonitorList(List<MonitorModel> monitorModelList) {

    }
}
