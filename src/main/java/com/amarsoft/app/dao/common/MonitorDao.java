package com.amarsoft.app.dao.common;

import com.amarsoft.app.model.LocalMonitorModel;
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


public class MonitorDao {

    /**
     *
     * @param entName
     * @return:该entName所对应的一条记录
     */
    private String tableName;

    public MonitorDao(String tableName){
        this.tableName = tableName;
    }

    public LocalMonitorModel getLocalMonitorModelByName(String entName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            LocalMonitorModel localMonitorModel = new LocalMonitorModel();
            conn = ARE.getDBConnection("bdfin");
            String selectSql = "select serialno,spiderstatus,inspectlevel from "+tableName+" where enterprisename = '" +entName+"'";
            ps = conn.prepareStatement(selectSql);
            rs = ps.executeQuery();
            if(rs.next()){
                localMonitorModel.setSerialno(rs.getString("serialno"));
                localMonitorModel.setSpiderstatus(rs.getString("spiderstatus"));
                localMonitorModel.setInspectlevel(rs.getString("inspectlevel"));
                return localMonitorModel;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                if(rs!=null){
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


        return null;
    }

    public List<String> insertMonitorList(List<MonitorModel> monitorModelList) {
        return null;
    }

    public List<String>  updateMonitorList(List<MonitorModel> monitorModelList) {
        return null;
    }
}
