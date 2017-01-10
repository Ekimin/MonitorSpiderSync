package com.amarsoft.app.common;

import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public interface MonitorSpiderSync {

    /**
     * 生成任务
     *
     * @param entMonitor 产品类型
     * @param flowID 批次号
     */
    public void generateTask(List<MonitorModel> entMonitor,String flowID);

    /**
     * 是否爬完
     *
     * @param flowID Azkaban编号
     * @return 判断监控任务表serialno对应的任务是否全部爬取完成
     */
    public boolean isSpidered(String flowID);


    /**
     * 是否同步完
     *
     * @param entList 产品类型，企业名单
     * @return 判断企业名单对应的采集数据是否全部同步完成
     */
    public boolean isSynchorized(List<MonitorModel> entList);
}
