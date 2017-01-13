package com.amarsoft.app.job;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.LawData.LawDataDBManager;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;
import com.amarsoft.are.util.CommandLineArgument;
import com.amarsoft.rmi.requestdata.requestqueue.IDataProcessTaskManage;

import java.rmi.Naming;
import java.util.List;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataJob implements MonitorJob {

    /**
     * 监控爬虫同步程序是否跑完任务(主流程)
     *
     * @param flowId Azkaban编号
     * @param bankId 机构号
     */
    public void monitorSpiderSync(String flowId, String modelId, String bankId) {
        String sleepTime = ARE.getProperty("SLEEP_TIME");
        boolean isSpidered = false;
        boolean isSynchronized = false;

        List<MonitorModel> monitorModelList = null; //监控名单
        MonitorUniMethod monitorUniMethod = new MonitorUniMethod();

        //根据flowId获取相关机构信息(企业名单)
        LawDataDBManager dbManager = new LawDataDBManager();
        monitorModelList = monitorUniMethod.getEntMonitorUrl(bankId, modelId); //TODO:获取监控名单

        //监控任务
        dbManager.initMonitor(flowId); //生成监控任务

        //生成爬虫任务
        dbManager.initSpiderTask(monitorModelList, flowId);

        //监控任务

        int monitorCount = 0;

        while (true) {
            monitorCount++;
            ARE.getLog().info("正在监控是否已经爬取完成, 当前监控次数：" + monitorCount);
            if (!isSpidered) {
                isSpidered = dbManager.monitorSpiderTask(monitorModelList, flowId);//监控爬虫任务
            } else if (!isSynchronized) {
                isSynchronized = dbManager.monitorSyncTask(monitorModelList, flowId); //监控同步任务
            }
            break; //TODO：测试
//            try{
//                ARE.getLog().info("睡眠"+ sleepTime +"继续监控");
//                Thread.sleep(Integer.parseInt(sleepTime));
//
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }

        }
        ARE.getLog().info("OK, all processes completed");

    }

    /**
     * @param flowId
     */
    public void run(String flowId) {

    }

    public static void main(String[] args) {
//        ARE.init("etc/are_Law.xml");
//        String bankId = args[0];
//        String modelId = args[1];
//        String flowId = args[3];
//
//        MonitorJob monitorJob = new LawDataJob();
//        monitorJob.monitorSpiderSync(flowId, modelId, bankId);

        if (!ARE.isInitOk()) {
            ARE.init("etc/are_Law.xml");
        }
        CommandLineArgument arg = new CommandLineArgument(args);


        String bankId = arg.getArgument("bankId");//机构编号
        String modelId = arg.getArgument("modelId");//模型编号
        String flowId = arg.getArgument("azkabanExecId");//azkaban执行编号
        MonitorJob monitorJob = new LawDataJob();

        bankId = "EDS";
        modelId = "舆情预警产品A";
        flowId = "jwang";


//        monitorJob.monitorSpiderSync(flowId, modelId, bankId);
//TODO:test only
        ARE.getLog().info("======================远程API方法调用开始===================");
        String registryHost = ARE.getProperty("com.amarsoft.rmi.servlet.RMIInitServlet.registryHost", "192.168.67.236");
        int registryPort = ARE.getProperty("com.amarsoft.rmi.servlet.RMIInitServlet.registryPort", 1098);
        try {
            IDataProcessTaskManage flowManage = (IDataProcessTaskManage)
                    Naming.lookup("rmi://" + registryHost + ":" + registryPort + "/flowManage");

            String jobClassName = LawDataJob.class.getName();

            //更改执行状态：
            ARE.getLog().info(flowManage.updateExeStatus(flowId, jobClassName, "success"));
            ARE.getLog().info("监控完成=============");
        } catch (Exception e) {
            ARE.getLog().error("远程RMI出错", e);
            e.printStackTrace();
        }
    }
}
