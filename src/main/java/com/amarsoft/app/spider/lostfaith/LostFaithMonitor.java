package com.amarsoft.app.spider.lostfaith;

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

//失信监控
public class LostFaithMonitor extends ExecLostFaithMonitor implements MonitorSpiderSync{

    public LostFaithMonitor(String tableName,String monitorTable){
        super(tableName,monitorTable);
    }
    @Override
    public boolean isSynchronized(List<MonitorModel> entList){
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String selectEntSql ="select count(1) from cb_lostfaith_ent_daily where iname like ? and issynchorized = 0";
        String selectPerSql = "select count(1) from cb_lostfaith_person_daily where iname like ? and issynchorized = 0";

        try {
            conn = ARE.getDBConnection("bdfin");
            ps1 = conn.prepareStatement(selectEntSql);
            ps2 = conn.prepareStatement(selectPerSql);

            for(MonitorModel monitorModel :entList){
                if(monitorModel.getInspectLevel().compareTo("2")>0){
                    continue;
                }
                String entName = monitorModel.getEntName();
                ps1.setString(1,entName+"%");
                ps2.setString(1,entName+"%");
                rs1 = ps1.executeQuery();
                rs2 = ps2.executeQuery();

                if(rs1.next()&&rs2.next()){
                    if(rs1.getInt(1)!=0||rs2.getInt(1)!=0){
                        return  false;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(rs1!=null) {
                    rs1.close();
                }
                if(rs2!=null){
                    rs2.close();
                }
                if(ps2!=null){
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
        return true;
    }


}
