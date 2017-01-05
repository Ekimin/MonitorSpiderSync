package com.amarsoft.app.dao;

import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by ryang on 2017/1/5.
 */
public class ReadMonitorUrl {

    //读取一级监控表，获得企业名单和对应的url
    public List<MonitorModel> getEntMonitorUrl(String bankID){
        List<MonitorModel> entMonitorUrl = new ArrayList<MonitorModel>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String selectSql = "";




        return entMonitorUrl;
    }
}
