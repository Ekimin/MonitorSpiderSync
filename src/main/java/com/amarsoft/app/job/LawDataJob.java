package com.amarsoft.app.job;

import com.amarsoft.amarmonitor.AmarMonitorAgent;
import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.LawData.LawDataDBManager;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;
import com.amarsoft.are.util.CommandLineArgument;
import com.amarsoft.monitorPlugin.sink.ganglia.AbstractGangliaSink;
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
     * @param batchId Azkaban编号
     * @param bankId  机构号
     */
    public void monitorSpiderSync(String batchId, String modelId, String bankId) {
        String sleepTime = ARE.getProperty("SLEEP_TIME");
        int rmiSleepTime = Integer.valueOf(ARE.getProperty("rmiSleepTime", "60"));
        String jobClassName = LawDataJob.class.getName();
        boolean isChangedRunning = false;
        boolean isChangedSuccess = false;
        MonitorUniMethod monitorUniMethod = new MonitorUniMethod();
        //调用RMI接口改流程状态
        while (!isChangedRunning) {
            ARE.getLog().info("修改该job为running");
            isChangedRunning = monitorUniMethod.updateFlowStatusByRMI(batchId, jobClassName, "running");
            //如果RMI服务未启动或者出现其他异常时，发邮件通知并休眠
            if (!isChangedRunning) {
                try {
                    ARE.getLog().info("调用RMI服务出错，休眠" + (rmiSleepTime / 1000) + "秒");
                    AmarMonitorAgent agent = new AmarMonitorAgent();
                    agent.emitMetric("Inspect_RMI_Exception", "Inspect_RMI_Data_Process_Task_Update_Exception", "uint32",
                            "1", AbstractGangliaSink.GangliaOp.valueOf("GE"), "1", "2");
                    Thread.sleep(rmiSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        boolean isSpidered;
        boolean isSynchronized;

        List<MonitorModel> monitorModelList = null; //监控名单

        //根据batchId获取相关机构信息(企业名单)
        monitorModelList = monitorUniMethod.getEntMonitorUrl(bankId, modelId);
        LawDataDBManager dbManager = new LawDataDBManager();
        //监控任务
        ARE.getLog().info("========开始生成监控任务=======");
        dbManager.initMonitor(batchId); //生成监控任务
        ARE.getLog().info("========生成监控任务完成=======");

        //生成爬虫任务
        dbManager.initSpiderTask(monitorModelList, batchId);
        dbManager.updateMonitorStatus(batchId, "running");//爬虫任务生成完毕，等待爬取，开始监控
        //监控任务
        int monitorCount = 0;
        isSpidered = dbManager.monitorSpiderTask(monitorModelList, batchId);//监控爬虫任务
        while (true) {
            monitorCount++;
            ARE.getLog().info("正在监控是否已经爬取完成, 当前监控次数：" + monitorCount);
            if (!isSpidered) {//未完成,睡眠一段时间继续监控
                try {
                    ARE.getLog().info("睡眠" + sleepTime + "毫秒继续监控");
                    Thread.sleep(Integer.parseInt(sleepTime));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isSpidered = dbManager.monitorSpiderTask(monitorModelList, batchId);//监控爬虫任务

            } else {
                ARE.getLog().info("=====================爬虫任务完成，开始监控同步程序====================");
                break;
            }
        }

        // 监控同步任务是否完成
        monitorCount = 0;
        isSynchronized = dbManager.monitorSyncTask(monitorModelList, batchId); //监控同步任务
        while (true) {
            monitorCount++;
            ARE.getLog().info("正在监控是否已经同步完成, 当前监控次数：" + monitorCount);
            if (!isSynchronized) {
                try {
                    ARE.getLog().info("睡眠" + sleepTime + "毫秒继续监控");
                    Thread.sleep(Integer.parseInt(sleepTime));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isSynchronized = dbManager.monitorSyncTask(monitorModelList, batchId); //监控同步任务
            } else {
                break;
            }
        }

        ARE.getLog().info("数据同步完成，更新监控表状态---------");
        dbManager.updateMonitorStatus(batchId, "success");
        ARE.getLog().info("监控表状态更新完毕，开始更新进程表状态---------");
        do {
            isChangedSuccess = monitorUniMethod.updateFlowStatusByRMI(batchId, jobClassName, "success");
            //TODO:如果RMI服务未启动或者出现其他异常时，发邮件通知并休眠
            if (isChangedSuccess) {
                ARE.getLog().info("更新进程表状态完成，监控完毕---------");
                break;
            } else {
                ARE.getLog().info("远程RMI调用出错，等待" + rmiSleepTime + "毫秒继续尝试---------");
                AmarMonitorAgent agent = new AmarMonitorAgent();
                agent.emitMetric("Inspect_RMI_Exception", "Inspect_RMI_Data_Process_Task_Update_Exception", "uint32",
                        "1", AbstractGangliaSink.GangliaOp.valueOf("GE"), "1", "2");
                try {
                    Thread.sleep(rmiSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (true);
    }

    /**
     * @param batchId
     */
    public void run(String batchId) {

    }

    public static void main(String[] args) {
        if (!ARE.isInitOk()) {
            ARE.init("etc/are_Law.xml");
        }
        CommandLineArgument arg = new CommandLineArgument(args);

        String bankId = arg.getArgument("bankId");//机构编号
        String modelId = arg.getArgument("modelId");//模型编号
        String batchId = arg.getArgument("azkabanExecId");//azkaban执行编号,非流程表中的batchid

        //TODO:TEST ONLY
//        bankId = "EDSTest";
//        modelId = "诉讼结构化A";
//        batchId = "33077";


        MonitorJob monitorJob = new LawDataJob();
        if (bankId == null) {
            ARE.setProperty("BANKID", "noBankId");//日志文件按银行编号存储区分
        } else {
            ARE.setProperty("BANKID", bankId);//日志文件按银行编号存储区分
        }
        monitorJob.monitorSpiderSync(batchId, modelId, bankId);
    }
}
