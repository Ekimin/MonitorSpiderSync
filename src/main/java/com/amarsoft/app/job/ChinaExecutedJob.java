package com.amarsoft.app.job;

import com.amarsoft.amarmonitor.AmarMonitorAgent;
import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.are.ARE;
import com.amarsoft.are.util.CommandLineArgument;
import com.amarsoft.monitorPlugin.sink.ganglia.AbstractGangliaSink;

import java.util.List;

/**被执行人监控程序
 * Created by ryang on 2017/1/9.
 */
public class ChinaExecutedJob implements MonitorJob{
    /**
     *监控程序是否爬取完成、是否同步
     * @param batchId
     */
    public void monitorSpiderSync(String batchId,String modelId,String bankId) {
        int rmiSleepTime = Integer.valueOf(ARE.getProperty("rmiSleepTime","60"));
        String jobClassName = ChinaExecutedJob.class.getName();
        boolean isChangedRunning = false;
        boolean isChangedSuccess = false;
        MonitorUniMethod monitorUniMethod = new MonitorUniMethod();
        while(!isChangedRunning){
            ARE.getLog().info("修改该job为running");
            isChangedRunning =  monitorUniMethod.updateFlowStatusByRMI(batchId, jobClassName, "running");
            //TODO:如果RMI服务未启动或者出现其他异常时，发邮件通知并休眠
            if(!isChangedRunning){
                try {
                    ARE.getLog().info("调用RMI服务出错，休眠"+rmiSleepTime+"秒");
                    AmarMonitorAgent agent = new AmarMonitorAgent();
                    agent.emitMetric("Inspect_RMI_Exception", "Inspect_RMI_Data_Process_Task_Update_Exception", "uint32", "1", AbstractGangliaSink.GangliaOp.valueOf("GE"), "1", "2");
                    Thread.sleep(rmiSleepTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        int sleepTime = Integer.valueOf(ARE.getProperty("sleepTime"));
        boolean isSpidered = false;
        boolean isSynchronized = false;
        List<MonitorModel> monitorModelList = null;
        MonitorSpiderSync monitorSpiderSync = new ChinaExecutedMonitor("task_executed_daily", "monitor_executed_org");
        monitorModelList = monitorUniMethod.getEntMonitorUrl(bankId, modelId);
        //生成任务
        ARE.getLog().info("开始生成任务");
        monitorSpiderSync.generateTask(monitorModelList, batchId);
        ARE.getLog().info("生成任务完成");
        //监控任务是否完成
        while (true) {
            if (!isSpidered) {
                ARE.getLog().info("正在监控是否已经爬取完成");
                isSpidered = monitorSpiderSync.isSpidered(batchId);

                if (!isSpidered) {
                    try {
                        Thread.sleep(sleepTime * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                ARE.getLog().info("正在监控是否已经同步完成");
                isSynchronized = monitorSpiderSync.isSynchronized(monitorModelList);
                if (isSynchronized) {
                    //修改状态为success
                    while (!isChangedSuccess) {
                        ARE.getLog().info("修改该job为success");
                        isChangedSuccess =  monitorUniMethod.updateFlowStatusByRMI(batchId, jobClassName, "success");
                        //TODO:如果RMI服务未启动或者出现其他异常时，发邮件通知并休眠
                        if(!isChangedSuccess){
                            try {
                                ARE.getLog().info("调用RMI服务出错，休眠"+rmiSleepTime+"秒");
                                AmarMonitorAgent agent = new AmarMonitorAgent();
                                agent.emitMetric("Inspect_RMI_Exception", "Inspect_RMI_Data_Process_Task_Update_Exception", "uint32", "1", AbstractGangliaSink.GangliaOp.valueOf("GE"), "1", "2");
                                Thread.sleep(rmiSleepTime*1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return;
                }
                try {
                    Thread.sleep(sleepTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
}

    public void run(String batchId) {
    }

    /**
     *
     * @param args
     * args[0] bankId
     * args[3] AZ编号
     */
    public static void main(String[] args) {
        if(!ARE.isInitOk()){
            ARE.init("etc/are_executed_daily.xml");
        }

        CommandLineArgument arg = new CommandLineArgument(args);
        String bankId = arg.getArgument("bankId");//机构编号
        String modelId = arg.getArgument("modelId");//模型编号
        String batchId = arg.getArgument("azkabanExecId");//azkaban执行编号

        ARE.setProperty("BANKID",bankId);

        MonitorJob monitorJob = new ChinaExecutedJob();
        monitorJob.monitorSpiderSync(batchId,modelId,bankId);
    }
}
