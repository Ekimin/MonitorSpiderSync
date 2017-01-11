package com.amarsoft.app.job;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.are.ARE;
import java.util.List;

/**被执行人监控程序
 * Created by ryang on 2017/1/9.
 */
public class ChinaExecutedJob implements MonitorJob{

    /**
     *监控程序是否爬取完成、是否同步
     * @param flowId
     */
    public void monitorSpiderSync(String flowId,String bankId) {
        //TODO：修改流程中的状态为running


        int sleepTime = Integer.valueOf(ARE.getProperty("sleepTime"));
        boolean isSpidered = false;
        boolean isSynchronized = false;
        List<MonitorModel> monitorModelList = null;
        MonitorSpiderSync monitorSpiderSync = new ChinaExecutedMonitor("task_executed_daily","monitor_executed_org");

        MonitorUniMethod readMonitorUrl = new MonitorUniMethod();
        monitorModelList = readMonitorUrl.getEntMonitorUrl(bankId);

        //生成任务
        ARE.getLog().info("开始生成任务");
        monitorSpiderSync.generateTask(monitorModelList,flowId);
        ARE.getLog().info("生成任务完成");
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
                isSynchronized = monitorSpiderSync.isSynchronized(monitorModelList);
                if(isSynchronized){
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
       /* MonitorJob monitorJob = new ChinaExecutedJob();
        monitorJob.monitorSpiderSync(flowId);*/
    }

    public static void main(String[] args) {
        ARE.init("etc/are.xml");
        String bankId = args[0];
        String flowId = args[3];
        MonitorJob monitorJob = new ChinaExecutedJob();
        monitorJob.monitorSpiderSync(flowId,bankId);
    }
}
