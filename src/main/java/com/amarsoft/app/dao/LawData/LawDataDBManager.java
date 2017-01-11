package com.amarsoft.app.dao.LawData;

import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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
        Connection connEnt = null;
        Connection connTask = null;
        PreparedStatement ps = null;

        String sqlEnt = "select"; //
        String sqlTask = "";
        int batch = 500;

        try {
            connEnt = ARE.getDBConnection("78_crsbjt");

            ps = connEnt.prepareStatement(sqlEnt);
            String serialNo = "";
            for (MonitorModel monitorModel : monitorModelList) {
                serialNo = UUID.randomUUID().toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void initSpiderTask(List<MonitorModel> monitorModelList, InspectInfo) {

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
