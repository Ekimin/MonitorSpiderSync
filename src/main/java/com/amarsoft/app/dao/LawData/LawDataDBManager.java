package com.amarsoft.app.dao.LawData;

import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;

import java.sql.Connection;
import java.util.List;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataDBManager implements MonitorDao {
    /**
     * 获取机构号
     *
     * @param flowId Azkaban的flowId
     * @return
     */
    public String getBankIdByFlowId(String flowId) {
        //TODO：王军接口
        return null;
    }

    /**
     * 初始化监控任务
     *
     * @param flowId
     */
    public void initMonitor(String flowId) {

    }

    /**
     * 初始化爬虫任务（生成任务）
     *
     * @param monitorModelList 监控名单
     * @param flowId
     */
    public void initSpiderTask(List<MonitorModel> monitorModelList, String flowId) {
        Connection conn = null;

    }

    /**
     * 监控爬虫任务是否完成
     * <li>企业名单中优先级1和2的任务完成就算完成</li>
     *
     * @param monitorModelList
     * @param flowId
     */
    public void monitorSpiderTask(List<MonitorModel> monitorModelList, String flowId) {

    }

    /**
     * 监控同步任务是否完成
     *
     * @param monitorModelList
     * @param flowId
     */
    public void monitorSyncTask(List<MonitorModel> monitorModelList, String flowId) {

    }
}
