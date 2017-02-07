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
    public void monitorSpiderSync(String batchId,String modelId,String bankId){
      int rmiSleepTime = Integer.valueOf(ARE.getProperty("rmiSleepTime","60"));
      String jobClassName = LostFaithJob.class.getName();
      boolean isChangedRunning = false;
      boolean isChangedSuccess = false;
      MonitorUniMethod monitorUniMethod = new MonitorUniMethod();

      //修改状态为running
      while(!isChangedRunning){
          ARE.getLog().info("修改job为running");
          isChangedRunning = monitorUniMethod.updateFlowStatusByRMI(batchId,jobClassName,"running");
          if(!isChangedRunning){
              try {
                  ARE.getLog().info("调用远程RMI服务出错，休眠"+rmiSleepTime+"秒");
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
      MonitorSpiderSync monitorSpiderSync = new LostFaithMonitor("task_lostfaith_daily", "monitor_lostfaith_org");
      monitorModelList = monitorUniMethod.getEntMonitorUrl(bankId,modelId);
      //生成任务
      monitorSpiderSync.generateTask(monitorModelList, batchId);

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
                while(!isChangedSuccess) {
                    ARE.getLog().info("修改job为success");
                    isChangedSuccess = monitorUniMethod.updateFlowStatusByRMI(batchId, jobClassName, "success");
                    if(!isChangedSuccess){
                        try {
                            ARE.getLog().info("调用远程RMI服务出错，休眠"+rmiSleepTime+"秒");
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

    public static void main(String[] args) {
        if(!ARE.isInitOk()){
            ARE.init("etc/are_lostfaith_daily.xml");
        }

        CommandLineArgument arg = new CommandLineArgument(args);
        String bankId = arg.getArgument("bankId");//机构编号
        String modelId = arg.getArgument("modelId");//模型编号
        String batchId = arg.getArgument("azkabanExecId");//azkaban执行编号

 /*       bankId = "EDSTest";
        modelId = "失信被执行人流程模型A";
        batchId = "jwang";
*/
        ARE.setProperty("BANKID",bankId);

        MonitorJob monitorJob = new LostFaithJob();


        monitorJob.monitorSpiderSync(batchId,modelId,bankId);
    }
}
