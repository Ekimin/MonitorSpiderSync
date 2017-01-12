package com.amarsoft.app.dao.LawData;

import com.amarsoft.app.common.DateManager;
import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.LawCrawler.ExtractJSON;
import com.amarsoft.app.spider.LawCrawler.SpiderMethod;
import com.amarsoft.are.ARE;
import com.amarsoft.are.lang.DateX;
import com.amarsoft.are.util.StringFunction;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataDBManager implements MonitorDao {
    String sourceUrl = ARE.getProperty("source_url", "http://wenshu.court.gov.cn/List/List?sorttype=1");
    String contentUrl = ARE.getProperty("content_url", "http://wenshu.court.gov.cn/List/ListContent");

    /**
     * 获取机构号
     *
     * @param flowId Azkaban的flowId
     * @return
     */
    public String getBankIdByFlowId(String flowId) {
        //TODO：王军接口
        return null;
    }

    /**
     * 初始化监控任务
     *
     * @param flowId
     */
    public void initMonitor(String flowId) {

    }

    /**
     * 初始化爬虫任务（生成任务）
     *
     * @param monitorModelList 监控名单
     * @param flowId
     */
    public void initSpiderTask(List<MonitorModel> monitorModelList, String flowId) {
        Connection connTask = null;
        PreparedStatement ps_Task = null;
        Statement st = null;
        ResultSet rs = null;

        String sqlMaxId = "select MAX(SERIALNO) from QY_CRAWLER_TASK"; //
        String sqlTask = "insert into QY_CRAWLER_TASK (SERIALNO, QUERYPARAM, STATUS, CREATETIME, PIORITY) values " +
                "(?,?,?,?,?)";
        int batch = 500;
        long currentSerialNo = 0;
        try {
            connTask = ARE.getDBConnection("78_crsbjt");
            st = connTask.createStatement();
            rs = st.executeQuery(sqlMaxId);
            if (rs.next()) {
                currentSerialNo = Long.parseLong(rs.getString(1));
                ARE.getLog().info("任务表目前最大ID=" + currentSerialNo);
            }

            ps_Task = connTask.prepareStatement(sqlTask);
            String currentDate = DateManager.getCurrentDate("yyyyMMdd");
            ARE.getLog().info("currentDate = " + currentDate);
            int batchCount = 0;
            for (MonitorModel monitorModel : monitorModelList) {
                currentSerialNo++;
                ps_Task.setString(1, String.valueOf(currentSerialNo)); //serialNo

                String entName = monitorModel.getEntName();
                String monitorReg = monitorModel.getStockBlock(); //监控时间区间

                String queryStr = null;
                if (entName != null && !entName.equals("")) {
                    if (monitorReg != null && monitorReg.length() > 20) {
                        String minDate = monitorReg.substring(1, 11).replace("/", "-");
                        String maxDate = monitorReg.substring(12, 22);
                        if (minDate.equals("0001-01-01")) {
                            queryStr = "全文检索:" + entName;
                        } else {
                            queryStr = "全文检索:" + entName + ",上传日期:" + minDate + " TO " + maxDate;
                        }
                    } else {
                        String minDate = monitorReg.substring(0, 10).replace("/", "-");
                        String maxDate = DateManager.getDate(minDate, -1);
                        queryStr = "全文检索:" + entName + ",上传日期:" + minDate + " TO " + maxDate;
                    }
                }
                int num = getReturnNum(queryStr);
                if (num == 0) {
                    //没有数据，直接生产任务标记完成
                    ps_Task.setString(2, queryStr); //queryStr
                    ps_Task.setString(3, "2"); //status：2=完成
                    ps_Task.setString(4, StringFunction.getTodayNow()); //inputtime
                    ps_Task.setString(5, monitorModel.getInspectLevel());//优先级

                    ps_Task.addBatch();
                    batchCount++;
                    if (batchCount >= 500) {
                        ps_Task.executeBatch();
                        batchCount = 0;
                    }
                } else if (num > 100) {
                    String date = DateManager.getCurrentDate("yyyy-MM-dd");
                    List<String> judgmentConditions = createJudgmentCondition(date);
                    for (String judgmentCondition : judgmentConditions) {
                        String newQuery = queryStr + "," + judgmentCondition;
                        ps_Task.setString(2, newQuery); //newQuery
                        ps_Task.setString(3, "0"); //status：0=等待爬取状态
                        ps_Task.setString(4, StringFunction.getTodayNow()); //inputtime
                        ps_Task.setString(5, monitorModel.getInspectLevel());//优先级

                        ps_Task.addBatch();
                        batchCount++;
                        if (batchCount >= 500) {
                            ps_Task.executeBatch();
                            batchCount = 0;
                        }
                    }
                } else {
                    ps_Task.setString(2, entName); //entName
                    ps_Task.setString(3, "0"); //status：0=等待爬取状态
                    ps_Task.setString(4, StringFunction.getTodayNow()); //inputtime
                    ps_Task.setString(5, monitorModel.getInspectLevel());//优先级

                    ps_Task.addBatch();
                    batchCount++;
                    if (batchCount >= 500) {
                        ps_Task.executeBatch();
                        batchCount = 0;
                    }
                }

            }
            ps_Task.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 监控爬虫任务是否完成
     * <li>企业名单中优先级1和2的任务完成就算完成</li>
     *
     * @param monitorModelList
     * @param flowId
     */
    public void monitorSpiderTask(List<MonitorModel> monitorModelList, String flowId) {

    }

    /**
     * 监控同步任务是否完成
     *
     * @param monitorModelList
     * @param flowId
     */
    public void monitorSyncTask(List<MonitorModel> monitorModelList, String flowId) {

    }

    /**
     * 返回企业网页的总页数
     *
     * @return
     */
    public int getReturnNum(String paramStr) {
        int pageSum = 0;
        try {
            ARE.getLog().info("[" + paramStr + "]");
            Map<String, String> params = LawDataDBManager.createParams();
            params.put("Param", paramStr);
            SpiderMethod spiderMethod = new SpiderMethod();
            String jsonStr = spiderMethod.getJSON(sourceUrl, contentUrl, params, "utf-8");
            pageSum = ExtractJSON.getSumPage(jsonStr, paramStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageSum;
    }

    public static Map<String, String> createParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Param", "");
        params.put("Index", "1");
        params.put("Page", "20");
        params.put("Order", "裁判日期");
        params.put("Direction", "desc");
        return params;
    }

    /**
     * 构造裁判日期查询条件
     */
    private static List<String> createJudgmentCondition(String curDate) {
        DateX datex = new DateX(curDate);
        String template = "裁判日期:preDate TO curDate";
        String condition = "";
        String yearNum = ARE.getProperty("yearNum", "10");
        int num = 0;
        try {
            num = Integer.parseInt(yearNum);
        } catch (Exception e) {
            ARE.getLog().error("配置文件中设置的抓取的年份计数不是整数，将其默认设置为10");
            num = 10;
        }
        List<String> list = new ArrayList<String>();
        String endDate = DateX.format(datex.getAbsoluteRelativeDate(0, -num), "yyyy-MM-dd");
        String preDate = "";
        while (true) {
            // 2010之后的的裁判日期区间设为10天，2010之前的裁判区间为一个季度
            if (curDate.compareTo("2010-01-01") > 0) {
                preDate = DateX.format(datex.getAbsoluteRelativeDate(1, -3), "yyyy-MM-dd");
            } else {
                preDate = DateX.format(datex.getAbsoluteRelativeDate(1, -12), "yyyy-MM-dd");
            }
            condition = template.replace("preDate", preDate).replace("curDate", curDate);
            list.add(condition);
            curDate = preDate;
            datex = new DateX(curDate);
            if (curDate.compareTo(endDate) <= 0) {
                break;
            }
        }
        return list;
    }

}
