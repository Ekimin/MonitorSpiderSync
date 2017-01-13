package com.amarsoft.app.job;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.are.ARE;
import com.amarsoft.are.util.CommandLineArgument;
import com.amarsoft.rmi.requestdata.requestqueue.IDataProcessTaskManage;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**被执行人监控程序
 * Created by ryang on 2017/1/9.
 */
public class ChinaExecutedJob implements MonitorJob{
    private static String registryHost = ARE.getProperty("com.amarsoft.rmi.servlet.RMIInitServlet.registryHost","192.168.67.236");
    private static int registryPort = ARE.getProperty("com.amarsoft.rmi.servlet.RMIInitServlet.registryPort",1098);

    /**
     *监控程序是否爬取完成、是否同步
     * @param flowId
     */
    public void monitorSpiderSync(String flowId,String modelId,String bankId) {
        String jobClassName = ChinaExecutedJob.class.getName();
        ARE.getLog().info("======================远程API方法调用开始===================");
        try {
            IDataProcessTaskManage flowManage = (IDataProcessTaskManage)
                    Naming.lookup("rmi://"+registryHost+":"+registryPort+"/flowManage");

            //更改执行状态：
            ARE.getLog().info(flowManage.updateExeStatus(flowId,jobClassName,"running"));

            int sleepTime = Integer.valueOf(ARE.getProperty("sleepTime"));
            boolean isSpidered = false;
            boolean isSynchronized = false;
            List<MonitorModel> monitorModelList = null;
            MonitorSpiderSync monitorSpiderSync = new ChinaExecutedMonitor("task_executed_daily","monitor_executed_org");

            MonitorUniMethod readMonitorUrl = new MonitorUniMethod();
            monitorModelList = readMonitorUrl.getEntMonitorUrl(bankId,modelId);

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
                        //修改状态为success
                        flowManage.updateExeStatus(flowId,jobClassName,"success");
                        return;
                    }
                    try {
                        Thread.sleep(sleepTime*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (MalformedURLException e) {
            ARE.getLog().info("url格式异常");
        } catch (RemoteException e) {
            ARE.getLog().info("创建对象异常");
            e.printStackTrace();
        } catch (NotBoundException e) {
            ARE.getLog().info("对象未绑定");
        }
        ARE.getLog().info("======================远程API方法调用结束===================");
    }

    public void run(String flowId) {
       /* MonitorJob monitorJob = new ChinaExecutedJob();
        monitorJob.monitorSpiderSync(flowId);*/
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
        String flowId = arg.getArgument("azkabanExecId");//azkaban执行编号

        MonitorJob monitorJob = new ChinaExecutedJob();
        bankId = "EDSTest";
        modelId = "被执行人流程模型A";
        flowId = "jwang";

        monitorJob.monitorSpiderSync(flowId,modelId,bankId);
    }
}
