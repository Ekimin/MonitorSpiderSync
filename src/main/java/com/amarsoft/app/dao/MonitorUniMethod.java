package com.amarsoft.app.dao;

import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ryang on 2017/1/5.
 */
public class MonitorUniMethod {

    /**
     * 获取机构号
     * @param flowId Azkaban的flowId
     * @return 机构号
     */
    public String getBankIdByFlowId(String flowId){
        //TODO:王军接口

        return null;
    }

    /**读取一级监控表，获得企业名单和对应的url
     *
     * @param bankID
     * @return:监控表列表
     */
    public List<MonitorModel> getEntMonitorUrl(String bankID,String modelId){
        List<MonitorModel> entMonitorUrl = new ArrayList<MonitorModel>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String selectSql = "select serinalno,enterprisename,idno,monitorurl,stockblock,inspectlevel,inspectstate,inputtime from spider_inspect_entitynew where bankID = ? and  inspectstate = 'Y' and modelId = ?";

        try {
            conn = ARE.getDBConnection("78_crsbjt");
            ps = conn.prepareStatement(selectSql);
            ps.setString(1,bankID);
            ps.setString(2,modelId);
            rs = ps.executeQuery();
            while (rs.next()){
                MonitorModel monitorModel = new MonitorModel();
                monitorModel.setSerialNo(rs.getString("serinalno"));
                monitorModel.setEntName(rs.getString("enterprisename"));
                monitorModel.setIdNo(rs.getString("idno"));
                monitorModel.setMonitorUrl(rs.getString("monitorurl"));
                monitorModel.setStockBlock(rs.getString("stockblock"));
                monitorModel.setInspectLevel(rs.getString("inspectlevel"));
                monitorModel.setInspectState(rs.getString("inspectstate"));
                monitorModel.setInputTime(rs.getString("inputtime"));
                entMonitorUrl.add(monitorModel);
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
        return entMonitorUrl;
    }
}
