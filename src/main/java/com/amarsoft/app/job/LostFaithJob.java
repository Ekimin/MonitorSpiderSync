package com.amarsoft.app.job;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.app.spider.lostfaith.LostFaithMonitor;
import com.amarsoft.are.ARE;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ryang on 2017/1/10.
 */
public class LostFaithJob implements MonitorJob{
    public void monitorSpiderSync(String flowId) {
        //TODO：修改流程中的状态为running
        int sleepTime = Integer.valueOf(ARE.getProperty("sleepTime"));
        boolean isSpidered = false;
        boolean isSynchorized = false;
        List<MonitorModel> monitorModelList = null;
        MonitorSpiderSync monitorSpiderSync = new LostFaithMonitor("task_lostfaith_daily","monitor_lostfaith_org");
        //TODO:调用API读取企业名单
        MonitorUniMethod readMonitorUrl = new MonitorUniMethod();
        monitorModelList = readMonitorUrl.getEntMonitorUrl(flowId);
        //生成任务
        monitorSpiderSync.generateTask(monitorModelList,flowId);

        //监控任务是否完成
        while (true){
            if(!isSpidered) {
                ARE.getLog().info("正在监控是否已经爬取完成");
                isSpidered = monitorSpiderSync.isSpidered(flowId);
                if(!isSpidered){
                    try {
                        Thread.sleep(sleepTime*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                ARE.getLog().info("正在监控是否已经同步完成");
                isSynchorized = monitorSpiderSync.isSynchronized(monitorModelList);
                if(isSynchorized){
                    //TODO:更新流程表的状态为success
                    return;
                }
                try {
                    Thread.sleep(sleepTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run(String flowId) {

    }

    public static void main(String[] args) {
        ARE.init("etc/are.xml");
        MonitorJob monitorJob = new LostFaithJob();
        monitorJob.monitorSpiderSync("123");
    }
}
