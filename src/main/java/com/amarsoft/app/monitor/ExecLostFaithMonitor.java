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

    public void generateTask(List<MonitorModel> entModels) {

    }

    /**
     *tableName为task_executed_daily或者task_lostfaith_daily
     * @param flowID
     * @return:是否爬完
     */
    public boolean isSpidered(String flowID) {
        Connection conn = null;
        PreparedStatement ps = null;
        String selectSql = "select spiderstatus from "+tableName+"where serialno = ?";
        ResultSet rs = null;
        try {
            conn = ARE.getDBConnection("bdfin");
            ps = conn.prepareStatement(selectSql);

            /*for(String serialno:serialNo){
                ps.setString(1,serialno);
                rs = ps.executeQuery();
                if(!rs.getString("spiderstatus").equals("success")&&!rs.getString("spiderstatus").equals("failure")){
                    return  false;
                }
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(rs!=null) {
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
                if(conn!=null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean isSynchronized(List<MonitorModel> entList) {
     return true;
    }
}
