package com.amarsoft.app.job;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.chinaexecuted.ChinaExecutedMonitor;

import java.util.LinkedList;
import java.util.List;

/**被执行人监控程序
 * Created by ryang on 2017/1/9.
 */
public class ChinaExecutedJob implements MonitorJob{

    /**
     *监控程序是否爬取完成、是否同步
     * @param flowId
     */
    public void monitorSpiderSync(String flowId) {

        List<MonitorModel> monitorModelList = new LinkedList<MonitorModel>();
        MonitorSpiderSync monitorSpiderSync = new ChinaExecutedMonitor("task_executed_daily","monitor_executed_org");

        //生成任务
        monitorSpiderSync.generateTask(monitorModelList,flowId);

        //监控任务是否完成





    }

    public void run(String flowId) {

    }
}
