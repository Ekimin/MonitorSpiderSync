package com.amarsoft.app.dao.common;

import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public interface MonitorDao {



    public List<String> insertMonitorList(List<MonitorModel> monitorModelList);

    public List<String> updateMonitorList(List<MonitorModel> monitorModelList);
}
