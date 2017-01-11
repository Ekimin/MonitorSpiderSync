package com.amarsoft.app.spider.chinaexecuted;

import com.amarsoft.app.common.MonitorSpiderSync;

import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.monitor.ExecLostFaithMonitor;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by ryang on 2017/1/5.
 */
public class ChinaExecutedMonitor extends ExecLostFaithMonitor implements MonitorSpiderSync {

    public ChinaExecutedMonitor(String tableName,String monitorTable){
        super(tableName,monitorTable);
    }

    @Override
    public boolean isSynchronized(List<MonitorModel> entList) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String selectSql ="select count(1) from cb_executed_daily where pname like ? and issynchorized = 0";

        try {
            conn = ARE.getDBConnection("bdfin");
            ps = conn.prepareStatement(selectSql);

            for(MonitorModel monitorModel :entList){
                if(monitorModel.getInspectLevel().compareTo("2")>0){
                    continue;
                }
                String entName = monitorModel.getEntName();
                ps.setString(1,entName+"%");
                rs = ps.executeQuery();
                if(rs.next()){
                    if(rs.getInt(1)!=0) {
                        return false;
                    }
                }
            }

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
}
