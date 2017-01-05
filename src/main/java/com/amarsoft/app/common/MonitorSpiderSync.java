package com.amarsoft.app.common;

import java.util.List;

/**
 * Created by ryang on 2017/1/5.
 */
public interface MonitorSpiderSync {
    //生成任务
    //params:产品类型
    //return:生成任务后所对应的serialno
    public List<String> generatTask();
    //是否爬完
    //params:产品类型,生成任务后所对应的serialno
    //return:判断监控任务表serialno对应的任务是否全部爬取完成
    public boolean isSpidered(List<String> serialNo);
    //是否同步完
    //params:产品类型，企业名单
    //return:判断企业名单对应的采集数据是否全部同步完成
    public boolean isSynchorized(List<String> entList);
}
