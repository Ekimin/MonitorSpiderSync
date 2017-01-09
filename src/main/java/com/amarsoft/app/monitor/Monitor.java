package com.amarsoft.app.monitor;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.LocalMonitorModel;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public class Monitor implements MonitorSpiderSync {
    String tableName;

    public Monitor(String tableName){
        this.tableName = tableName;
    }



    public List<String> generateTask(List<MonitorModel> entModels) {
        List<String> serialNos = new LinkedList<String>();
        List<MonitorModel> insertModels = new LinkedList<MonitorModel>();
        List<MonitorModel> updateModels = new LinkedList<MonitorModel>();
        MonitorDao chinaExecutedDao = new MonitorDao(tableName);

        for(MonitorModel entModel:entModels){
            String entName = entModel.getEntName();
            LocalMonitorModel localMonitorModel = chinaExecutedDao.getLocalMonitorModelByName(entName);
            //表示没有该企业信息
            if(localMonitorModel==null){
                insertModels.add(entModel);
            }
            //表示已经爬完
            else if(localMonitorModel.getSpiderstatus().equals("success")||localMonitorModel.getSpiderstatus().equals("failure")){
                updateModels.add(entModel);
            }
            //针对未爬完的处理
            else if(localMonitorModel.getSpiderstatus().equals("running")||localMonitorModel.getSpiderstatus().equals("inserting")){
                serialNos.add(localMonitorModel.getSerialno());
            }
            else {
                //针对spiderstatus为init的情况，如果优先级提高，则进行更新，否则不做任何事情
                if (localMonitorModel.getInspectstate().compareTo(entModel.getInspectState()) > 0) {
                    updateModels.add(entModel);
                } else {
                    serialNos.add(localMonitorModel.getSerialno());
                }
            }
        }

        return serialNos;
    }

    /**
     *tableName为task_executed_daily或者task_lostfaith_daily
     * @param serialNo
     * @return:是否爬完
     */
    public boolean isSpidered(List<String> serialNo) {
        Connection conn = null;
        PreparedStatement ps = null;
        String selectSql = "select spiderstatus from "+tableName+"where serialno = ?";
        ResultSet rs = null;
        try {
            conn = ARE.getDBConnection("bdfin");
            ps = conn.prepareStatement(selectSql);

            for(String serialno:serialNo){
                ps.setString(1,serialno);
                rs = ps.executeQuery();
                if(!rs.getString("spiderstatus").equals("success")&&!rs.getString("spiderstatus").equals("failure")){
                    return  false;
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

    public boolean isSynchorized(List<MonitorModel> entList) {
     return true;
    }
}
