package com.amarsoft.app.job;

import com.amarsoft.rmi.requestdata.requestqueue.IDataProcessTaskManage;
import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.lostfaith.LostFaithMonitor;
import com.amarsoft.are.ARE;
import com.amarsoft.are.util.CommandLineArgument;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

/**失信监控Job
 * Created by ryang on 2017/1/10.
 */
public class LostFaithJob implements MonitorJob{
    public void monitorSpiderSync(String flowId,String modelId,String bankId) {
        String registryHost = ARE.getProperty("registryHost","192.168.67.236");
        int registryPort = ARE.getProperty("registryPort",1098);

        ARE.getLog().info("======================远程API方法调用开始===================");
        try {
            IDataProcessTaskManage flowManage = (IDataProcessTaskManage)
                    Naming.lookup("rmi://"+registryHost+":"+registryPort+"/flowManage");

            String jobClassName = LostFaithJob.class.getName();

            //更改执行状态：
            ARE.getLog().info(flowManage.updateExeStatus(flowId,jobClassName,"running"));


            int sleepTime = Integer.valueOf(ARE.getProperty("sleepTime"));
            boolean isSpidered = false;
            boolean isSynchronized = false;
            List<MonitorModel> monitorModelList = null;
            MonitorSpiderSync monitorSpiderSync = new LostFaithMonitor("task_lostfaith_daily","monitor_lostfaith_org");

            MonitorUniMethod readMonitorUrl = new MonitorUniMethod();
            monitorModelList = readMonitorUrl.getEntMonitorUrl(modelId,bankId);
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
                    isSynchronized = monitorSpiderSync.isSynchronized(monitorModelList);
                    if(isSynchronized){
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
    }

    public void run(String flowId) {

    }

    public static void main(String[] args) {
        if(!ARE.isInitOk()){
            ARE.init("etc/are_lostfaith_daily.xml");
        }

        CommandLineArgument arg = new CommandLineArgument(args);
        String bankId = arg.getArgument("bankId");//机构编号
        String modelId = arg.getArgument("modelId");//模型编号
        String flowId = arg.getArgument("azkabanExecId");//azkaban执行编号
        MonitorJob monitorJob = new LostFaithJob();
       /* bankId = "EDSTest";
        modelId = "失信被执行人流程模型A";
        flowId = "jwang";*/

        monitorJob.monitorSpiderSync(flowId,modelId,bankId);
    }
}
