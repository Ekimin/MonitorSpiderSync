package com.amarsoft.app.monitor;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 用于被执行人和失信生成任务和判断爬虫是否爬取完成
 * Created by ryang on 2017/1/5.
 */
public class ExecLostFaithMonitor implements MonitorSpiderSync,MonitorDao {
    String tableName;
    String monitorTable;

    public ExecLostFaithMonitor(String tableName, String monitorTable){
        this.tableName = tableName;
        this.monitorTable = monitorTable;
    }

    /**
     *
     * @param entModels:企业任务list
     * @param flowID 批次号
     */
    public void generateTask(List<MonitorModel> entModels,String flowID) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        String insertTaskSql = "insert into "+ tableName +"(serialno,enterprisename,idno,inspectlevel,inspectstate,spiderstatus,spiderpage,inputtime,flowid) values(?,?,?,?,?,?,?,?,?)";
        String insertMonitorSql = "insert into " + monitorTable +"(flowid,spiderstatus,inputtime) values (?,?,?)";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            conn = ARE.getDBConnection("bdfin");
            conn.setAutoCommit(false);
            ps1 = conn.prepareStatement(insertTaskSql);


            for(MonitorModel entModel:entModels){
                ps1.setString(1, UUID.randomUUID().toString());
                ps1.setString(2,entModel.getEntName());
                ps1.setString(3,entModel.getIdNo());
                String inspectLevel = entModel.getInspectLevel();
                if(inspectLevel.compareTo("2")<=0){
                    ps1.setString(4,"1");
                }
                else{
                    ps1.setString(4,"2");
                }
                ps1.setString(5,entModel.getInspectState());
                ps1.setString(6,"init");
                ps1.setString(7,"1");
                ps1.setString(8,dateFormat.format(new Date()));
                ps1.setString(9,flowID);
                ps1.addBatch();
            }
            ARE.getLog().info("开始往任务表里面插入数据");
            ps1.executeBatch();
            conn.commit();
            ps1.clearBatch();
            ARE.getLog().info("插入数据完成");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(ps2!=null) {
                    ps2.close();
                }
                if(ps1!=null){
                    ps1.close();
                }
                if(conn!=null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *tableName为task_executed_daily或者task_lostfaith_daily
     * 判断该批次对应的任务表中的数据是否全部爬取完成（只监控优先级为高的数据）
     * @param flowID
     * @return:是否爬完
     */
    public boolean isSpidered(String flowID) {
        Connection conn = null;
        String selectSql = "select count(*) from "+tableName+" where flowid = '" +flowID+"' and  spiderstatus !='success' and spiderstatus !='failure' and inspectlevel = '1'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ARE.getDBConnection("bdfin");
            ps = conn.prepareStatement(selectSql);
            rs = ps.executeQuery();
            if(rs.next()){
               int count = rs.getInt(1);
               if(count == 0){
                   return true;
               }
               else {
                   return false;
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

        return false;
    }

    public boolean isSynchronized(List<MonitorModel> entList) {
     return true;
    }

    /**
     * 初始化监控表
     *
     * @param flowId
     */
    public void initMonitor(String flowId) {
        Connection conn = null;
        PreparedStatement ps = null;
        String insertMonitorSql = "insert into " + monitorTable +"(flowid,spiderstatus,inputtime) values (?,?,?)";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            conn = ARE.getDBConnection("bdfin");
            ps = conn.prepareStatement(insertMonitorSql);
            ps.setString(1,flowId);
            ps.setString(2,"init");
            ps.setString(3,dateFormat.format(new Date()));
            ARE.getLog().info("开始往监控表里面插入该批次的信息");
            ps.execute();
            ARE.getLog().info("插入批次信息完成");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(ps!=null) {
                    ps.close();
                }
                if(conn!=null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化爬虫任务表
     * @param monitorModelList
     */
    public void initSpiderTask(List<MonitorModel> monitorModelList,String flowId) {
        Connection conn = null;
        PreparedStatement ps = null;
        String insertTaskSql = "insert into "+ tableName +"(serialno,enterprisename,idno,inspectlevel,inspectstate,spiderstatus,spiderpage,inputtime,flowid) values(?,?,?,?,?,?,?,?,?)";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            conn = ARE.getDBConnection("bdfin");
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(insertTaskSql);


            for(MonitorModel entModel:monitorModelList){
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2,entModel.getEntName());
                ps.setString(3,entModel.getIdNo());
                String inspectLevel = entModel.getInspectLevel();
                if(inspectLevel.compareTo("2")<=0){
                    ps.setString(4,"1");
                }
                else{
                    ps.setString(4,"2");
                }
                ps.setString(5,entModel.getInspectState());
                ps.setString(6,"init");
                ps.setString(7,"1");
                ps.setString(8,dateFormat.format(new Date()));
                ps.setString(9,flowId);
                ps.addBatch();
            }
            ARE.getLog().info("开始往任务表里面插入数据");
            ps.executeBatch();
            conn.commit();
            ps.clearBatch();
            ARE.getLog().info("插入数据完成");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(ps!=null) {
                    ps.close();
                }
                if(conn!=null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
