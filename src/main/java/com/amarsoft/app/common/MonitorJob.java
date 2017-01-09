package com.amarsoft.app.common;

import com.amarsoft.app.chinaexecuted.ChinaExecutedMonitor;
import com.amarsoft.app.dao.ReadMonitorUrl;
import com.amarsoft.app.lostfaith.LostFaithMonitor;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.are.ARE;

import java.util.*;

/**
 * Created by ryang on 2017/1/5.
 * 监控逻辑：
 * 1.获得未完成的taskNO
 * 2.获得企业名单
 * 3.根据url对任务进行分类
 * 4.根据分类的结果生成相应的任务
 * 5.根据任务表的serialno监控所有的任务是否爬取完成，根据企业名单监控所有的数据是否同步完成
 * 6.全部同步完成后修改Job状态后退出
 *
 */

//所有程序总的入口
public class MonitorJob {

    //入口参数为机构号
    public void monitorSpiderSync(String bankID,String modelID,String taskState){
        List<MonitorModel> chinaExecutedMonitorList = new LinkedList<MonitorModel>();
        List<MonitorModel> lostFaithMonitorList = new LinkedList<MonitorModel>();
        List<String> chinaExecutedSerialno = new LinkedList<String>();
        List<String> lostFaithSerialno = new LinkedList<String>();
        boolean isChinaExecutedSpiderd = false;
        boolean isLostFaithSpidered = false;
        boolean isChinaExecutedSync = false;
        boolean isLostFaithSync = false;
        //存储未完成的TASKNO
       /* List<String> chinaExecutedBuildingTaskNO = null;
        List<String> chinaExecutedRunningTaskNO =null;*/
       /* List<String> lostFaithTaskNO = null;*/
        ReadMonitorUrl readMonitorUrl = new ReadMonitorUrl();
        DataProcessTaskManage dataProcessTaskManage = new DataProcessTaskManage();

        //获得未完成的taskNO
    /*    chinaExecutedBuildingTaskNO = dataProcessTaskManage.getUfinishedTasknoByOrg(bankID,modelID,taskState,"building");
        chinaExecutedRunningTaskNO = dataProcessTaskManage.getUfinishedTasknoByOrg(bankID,modelID,taskState,"running");*/
      /*  for(String runningTask : chinaExecutedRunningTaskNO){
            chinaExecutedBuildingTaskNO.add(runningTask);
        }*/


        //根据机构号获得企业名单和监控url
        List<MonitorModel> entMonitorUrl = new ArrayList<MonitorModel>();
        entMonitorUrl =  readMonitorUrl.getEntMonitorUrl(bankID);
        //根据url对监控的内容进行划分
        for(MonitorModel monitorModel:entMonitorUrl){
            String monitorUrl = monitorModel.getMonitorUrl();
            if(monitorUrl.contains("http://zhixing.court.gov.cn/search/")){
                chinaExecutedMonitorList.add(monitorModel);
            }
            if(monitorUrl.contains("http://shixin.court.gov.cn/")){
                lostFaithMonitorList.add(monitorModel);
            }
        }



        MonitorSpiderSync chinaExecutedMonitor = new ChinaExecutedMonitor("task_executed_daily");
        MonitorSpiderSync lostFaithMonitor = new LostFaithMonitor("task_lostfaith_daily");

        //生成任务
        dataProcessTaskManage.updateExeStatus();

        //生成任务后获得企业相对应的serialno
        chinaExecutedSerialno = chinaExecutedMonitor.generateTask(entMonitorUrl);

        updateRunning();

        //一直监控是否爬取完成和同步完成，直到全部完成后退出
        while(true){
            if(!isChinaExecutedSync){
                if(!isChinaExecutedSpiderd){
                    isChinaExecutedSpiderd = chinaExecutedMonitor.isSpidered(chinaExecutedSerialno);
                }
                else{
                    isChinaExecutedSync = chinaExecutedMonitor.isSynchorized(chinaExecutedMonitorList);
                }
            }



            if(isChinaExecutedSync&&isLostFaithSync){
                //更新模型表
                updateModel();
                return;
            }

        }



    }





    public static void main(String[] args) {
        ARE.init("etc/are.xml");
        MonitorJob monitorJob = new MonitorJob();
        monitorJob.monitorSpiderSync("123");
    }
}
