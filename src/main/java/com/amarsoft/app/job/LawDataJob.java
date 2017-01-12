package com.amarsoft.app.job;

import com.amarsoft.app.dao.LawData.LawDataDBManager;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.util.List;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataJob implements MonitorJob{

    /**
     * 监控爬虫同步程序是否跑完任务
     * @param flowId Azkaban编号
     * @param bankId 机构号
     */
    public void monitorSpiderSync(String flowId, String modelId,String bankId) {
        String sleepTime = ARE.getProperty("SLEEP_TIME");
        boolean isSpidered = false;
        boolean isSynchronized = false;

        List<MonitorModel> monitorModelList = null; //监控名单
        MonitorUniMethod monitorUniMethod = new MonitorUniMethod();
        //根据flowId获取相关机构信息
        LawDataDBManager dbManager = new LawDataDBManager();

        monitorModelList = monitorUniMethod.getEntMonitorUrl(bankId,modelId); //TODO:获取监控名单

        //生成任务
        dbManager.initSpiderTask(monitorModelList, flowId);

        //监控任务
        dbManager.initMonitor(flowId); //生成监控任务
        while(true){
            dbManager.monitorSpiderTask(monitorModelList, flowId);//监控爬虫任务
            dbManager.monitorSyncTask(monitorModelList, flowId); //监控同步任务
        }

    }

    /**
     * 程序主入口
     * @param flowId
     */
    public void run(String flowId) {

        new LawDataJob().monitorSpiderSync("","被执行人流程模型A","安硕征信EDS测试账号");
    }
}
