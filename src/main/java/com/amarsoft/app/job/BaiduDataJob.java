package com.amarsoft.app.job;

import com.amarsoft.app.dao.LawData.LawDataDBManager;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;
import com.amarsoft.are.util.CommandLineArgument;

import java.util.List;

/**
 * Created by ymhe on 2017/1/18.
 * MonitorSpiderSync
 */
public class BaiduDataJob implements MonitorJob {


    public static void main(String[] args) {
        if (!ARE.isInitOk()) {
            ARE.init("etc/are_Law.xml");
        }
        CommandLineArgument arg = new CommandLineArgument(args);

        String bankId = arg.getArgument("bankId");//机构编号
        String modelId = arg.getArgument("modelId");//模型编号
        String flowId = arg.getArgument("azkabanExecId");//azkaban执行编号

        //TODO:测试数据
//        bankId = "EDS";
//        modelId = "舆情预警产品A";
//        flowId = "jwang";

        MonitorJob monitorJob = new BaiduDataJob();
        if (bankId == null) {
            ARE.setProperty("BANKID", "noBankId");//日志文件按银行编号存储区分
        } else {
            ARE.setProperty("BANKID", bankId);//日志文件按银行编号存储区分
        }
        monitorJob.monitorSpiderSync(flowId, modelId, bankId);
    }

    @Override
    public void monitorSpiderSync(String flowId, String modelId, String bankId) {
        String sleepTime = ARE.getProperty("SLEEP_TIME");
        boolean isSpidered = false;
        boolean isSynchronized = false;

        List<MonitorModel> monitorModelList = null; //监控名单
        MonitorUniMethod monitorUniMethod = new MonitorUniMethod();

        //根据flowId获取相关机构信息(企业名单)
        monitorModelList = monitorUniMethod.getEntMonitorUrl(bankId, modelId);

    }

    @Override
    public void run(String flowId) {

    }
}
