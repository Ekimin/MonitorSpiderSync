package com.amarsoft.app.common;

import com.amarsoft.app.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.app.dao.ReadMonitorUrl;
import com.amarsoft.app.lostfaith.LostFaithMonitor;
import com.amarsoft.are.ARE;

import java.util.*;

/**
 * Created by ryang on 2017/1/5.
 */

//所有程序总的入口
public class Monitor {

    //入口参数为机构号
    public void monitorSpiderSync(String bankID){
        //存储企业名单
        List<String> chinaExecutedMonitorList = new LinkedList<String>();
        List<String> lostFaithMonitorList = new LinkedList<String>();
        List<String> chinaExecutedSerialno = new LinkedList<String>();
        List<String> lostFaithSerialno = new LinkedList<String>();
        boolean isChinaExecutedSpiderd = false;
        boolean isLostFaithSpidered = false;
        boolean isChinaExecutedSync = false;
        boolean isLostFaithSync = false;
        ReadMonitorUrl readMonitorUrl = new ReadMonitorUrl();

        //根据机构号获得企业名单和监控url
        Map<String,String> entMonitorUrl = new HashMap<String,String>();
        entMonitorUrl = readMonitorUrl.getEntMonitorUrl(bankID);

        //根据url对监控的内容进行划分
        for(String ent : entMonitorUrl.keySet()){
            String monitorUrl = entMonitorUrl.get(ent);
            if(monitorUrl.contains("")){
                chinaExecutedMonitorList.add(ent);
            }
            if(monitorUrl.contains("")){
                lostFaithMonitorList.add(ent);
            }
        }

        MonitorSpiderSync chinaExecutedMonitor = new ChinaExecutedMonitor();
        MonitorSpiderSync lostFaithMonitor = new LostFaithMonitor();

        //生成任务
        chinaExecutedSerialno = chinaExecutedMonitor.generatTask();





        //一直监控是否爬取完成和同步完成，直到全部完成后退出
        while(true){
            if(!isChinaExecutedSync){

            }



            if(isChinaExecutedSync&&isLostFaithSync){
                return;
            }

        }



    }





    public static void main(String[] args) {
        ARE.init("etc/are.xml");


    }
}
