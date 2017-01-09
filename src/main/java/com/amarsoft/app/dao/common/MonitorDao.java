package com.amarsoft.app.dao.common;

import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */

/**
 * 一些公有Dao方法
 */
public interface MonitorDao {

    /**
     * 获取机构号
     * @param flowId Azkaban的flowId
     * @return 机构号
     */
    public String getBankIdByFlowId(String flowId);


    public void insertMonitorList(List<MonitorModel> monitorModelList);
}
