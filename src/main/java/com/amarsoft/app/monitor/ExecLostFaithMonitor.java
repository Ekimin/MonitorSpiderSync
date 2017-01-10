package com.amarsoft.app.monitor;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public class ExecLostFaithMonitor implements MonitorSpiderSync {
    String tableName;
    String monitorTable;

    public ExecLostFaithMonitor(String tableName, String monitorTable){
        this.tableName = tableName;
        this.monitorTable = monitorTable;
    }

    public void generateTask(List<MonitorModel> entModels,String flowID) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            conn = ARE.getDBConnection("bdfin");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *tableName为task_executed_daily或者task_lostfaith_daily
     * @param flowID
     * @return:是否爬完
     */
    public boolean isSpidered(String flowID) {
        return false;
    }

    public boolean isSynchorized(List<MonitorModel> entList) {
     return true;
    }
}
