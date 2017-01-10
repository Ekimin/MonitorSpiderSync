package com.amarsoft.app.job;

import com.amarsoft.app.dao.LawData.LawDataDBManager;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataJob implements MonitorJob{

    public void monitorSpiderSync(String flowId) {

    }

    /**
     * 程序主入口
     * @param flowId
     */
    public void run(String flowId) {
        //根据flowId获取相关机构信息
        LawDataDBManager dbManager = new LawDataDBManager();
        String bankId = dbManager.getBankIdByFlowId(flowId); //机构号
        //TODO:List<MonitorModel> monitorModelList = MonitorUniMethod.getEntMonitorUrl(bankId); //获取监控名单

        //生成任务


        //监控任务
    }
}
