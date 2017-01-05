package com.amarsoft.app.chinaexecuted;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.chinaexecuted.ChinaExecutedDao;
import com.amarsoft.app.model.MonitorModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public class ChinaExecutedMonitor implements MonitorSpiderSync {
    public List<String> generateTask(List<MonitorModel> entModels) {
        List<String> serialnos = new LinkedList<String>();
        List<MonitorModel> insertModels = new LinkedList<MonitorModel>();
        List<MonitorModel> updateModels = new LinkedList<MonitorModel>();
        ChinaExecutedDao chinaExecutedDao = new ChinaExecutedDao();

        for(MonitorModel entModel:entModels){
            String entName = entModel.getEnterprisename();
            //有该企业的信息
            if(chinaExecutedDao.isHasEntName(entName)){

            }
            else {
                insertModels.add(entModel);
            }

        }








        return serialnos;
    }

    public boolean isSpidered(List<String> serialNo) {
        return false;
    }

    public boolean isSynchorized(List<MonitorModel> entList) {
        return false;
    }
}
