package com.amarsoft.app.job;

import com.amarsoft.app.common.DataProcessTaskManage;
import com.amarsoft.app.common.IDataProcessTaskManage;
import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.MonitorUniMethod;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.app.spider.lostfaith.LostFaithMonitor;
import com.amarsoft.are.ARE;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**失信监控Job
 * Created by ryang on 2017/1/10.
 */
public class LostFaithJob implements MonitorJob{
    private static String registryHost = ARE.getProperty("com.amarsoft.rmi.servlet.RMIInitServlet.registryHost","localhost");
    private static int registryPort = ARE.getProperty("com.amarsoft.rmi.servlet.RMIInitServlet.registryPort",1098);

    public void monitorSpiderSync(String flowId,String modelId,String bankId) {

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
        ARE.init("etc/are.xml");
        String bankId = args[0];
        String modelId = args[1];
        String flowId = args[3];
        MonitorJob monitorJob = new LostFaithJob();
        monitorJob.monitorSpiderSync(flowId,modelId,bankId);
    }
}
