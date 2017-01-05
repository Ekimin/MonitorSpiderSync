package com.amarsoft.app.common;

import com.amarsoft.app.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.app.dao.ReadMonitorUrl;
import com.amarsoft.app.lostfaith.LostFaithMonitor;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.util.*;

/**
 * Created by ryang on 2017/1/5.
 */

//所有程序总的入口
public class MonitorJob {

    //入口参数为机构号
    public void monitorSpiderSync(String bankID){
        List<MonitorModel> chinaExecutedMonitorList = new LinkedList<MonitorModel>();
        List<MonitorModel> lostFaithMonitorList = new LinkedList<MonitorModel>();
        List<String> chinaExecutedSerialno = new LinkedList<String>();
        List<String> lostFaithSerialno = new LinkedList<String>();
        boolean isChinaExecutedSpiderd = false;
        boolean isLostFaithSpidered = false;
        boolean isChinaExecutedSync = false;
        boolean isLostFaithSync = false;
        ReadMonitorUrl readMonitorUrl = new ReadMonitorUrl();

        //根据机构号获得企业名单和监控url
        List<MonitorModel> entMonitorUrl = new ArrayList<MonitorModel>();
        entMonitorUrl =  readMonitorUrl.getEntMonitorUrl(bankID);
        //根据url对监控的内容进行划分
        for(MonitorModel monitorModel:entMonitorUrl){
            String monitorUrl = monitorModel.getMonitorurl();
            if(monitorUrl.contains("")){
                chinaExecutedMonitorList.add(monitorModel);
            }
            if(monitorUrl.contains("")){
                lostFaithMonitorList.add(monitorModel);
            }
        }



        MonitorSpiderSync chinaExecutedMonitor = new ChinaExecutedMonitor("task_executed_daily");
        MonitorSpiderSync lostFaithMonitor = new LostFaithMonitor("task_lostfaith_daily");

        //生成任务
        updateBuilding();
        chinaExecutedSerialno = chinaExecutedMonitor.generateTask(entMonitorUrl);

        updateRunning();

        //一直监控是否爬取完成和同步完成，直到全部完成后退出
        while(true){
            if(!isChinaExecutedSync){
                if(!isChinaExecutedSpiderd){
                    isChinaExecutedSpiderd = chinaExecutedMonitor.isSpidered(chinaExecutedSerialno);
                }
                else{
                    isChinaExecutedSync = chinaExecutedMonitor.isSynchorized(chinaExecutedMonitorList);
                }
            }



            if(isChinaExecutedSync&&isLostFaithSync){
                //更新模型表
                updateModel();
                return;
            }

        }



    }





    public static void main(String[] args) {
        ARE.init("etc/are.xml");
        MonitorJob monitorJob = new MonitorJob();
        monitorJob.monitorSpiderSync("123");
    }
}
