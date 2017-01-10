package com.amarsoft.app.spider.LawCrawler;

import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.model.MonitorModel;

import java.util.List;

/**
 * Created by ymhe on 2017/1/9.
 * MonitorSpiderSync 诉讼一级监控
 */
public class LawDataManager implements MonitorSpiderSync {

    /**
     * 生成任务
     * @param entMonitor 产品类型
     * @param flowID 批次号
     */
    public void generateTask(List<MonitorModel> entMonitor,String flowID) {

    }

    /**
     * 判断是否爬取完成
     * @param flowID Azkaban编号
     * @return
     */
    public boolean isSpidered(String flowID) {
        return false;
    }

    /**
     * 判断是否同步完成
     * @param entList 产品类型，企业名单
     * @return
     */
    public boolean isSynchronized(List<MonitorModel> entList) {
        return false;
    }
}
