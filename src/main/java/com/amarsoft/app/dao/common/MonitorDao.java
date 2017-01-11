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
     * 初始化监控任务
     * @param flowId
     */
    public void initMonitor(String flowId);

    /**
     * 生成爬虫任务
     * @param monitorModelList
     * @param flowId
     */
    public void initSpiderTask(List<MonitorModel> monitorModelList,String flowId);





}
