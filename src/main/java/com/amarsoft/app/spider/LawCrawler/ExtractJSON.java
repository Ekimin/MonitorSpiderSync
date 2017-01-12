package com.amarsoft.app.spider.LawCrawler;

import com.amarsoft.are.ARE;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Created by ymhe on 2017/1/11.
 * MonitorSpiderSync
 */


/**
 * json解析
 */
public class ExtractJSON {


    /**
     * 从json中解析出总页数
     *
     * @throws ScriptException
     */
    public static int getSumPage(String jsonStr, String paramStr) throws ScriptException {
        JSONArray jsonArray = getJSONArray(jsonStr);
        int pageSum = 0;
        if (jsonArray.size() > 0) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int count = Integer.valueOf(jsonObject.getString("Count"));
            pageSum = count % 20 == 0 ? count / 20 : count / 20 + 1;
            if (!paramStr.contains("上传日期")) {
                //(new IOHandle()).updateExpectNum(count,paramStr);
            } else {
                //IncrTaskManager.updateReturnRecordsNum(paramStr, count);
                //Crawler.incr.setReturnnum(count);
            }
        }
        return pageSum;
    }

    /**
     * 从json字符串中提取出当前的记录数
     */
    public static int getCount(String jsonStr) throws Exception {
        try {
            int count = -1;
            JSONArray jsonArray = getJSONArray(jsonStr);
            if (jsonArray.size() > 0) {
                count = Integer.valueOf(jsonArray.getJSONObject(0).getString("Count"));
            }
            return count;
        } catch (Exception e) {
            ARE.getLog().error("在解析返回的记录数时出现异常", e);
            throw e;
        }
    }

    /**
     * 从json字符串中中解析出诉讼数据
     *
     * @throws ScriptException
     */
    public static JSONArray getJSONArray(String jsonStr) throws ScriptException {
        //	ARE.getLog().info("返回的json："+jsonStr);
        //JSONArray jsonArray=new JSONArray();
        //ARE.getLog().info("返回的json："+jsonStr);
        //JSONArray jsonArray = JSONArray.fromObject(jsonStr);

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("javascript");
        JSONArray jsonArray = JSONArray.fromObject(engine.eval("(" + jsonStr + ")"));



		/*if(!(jsonStr.contains("404")||jsonStr.contains("502"))){
        ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("javascript");
		 jsonArray =JSONArray.fromObject(engine.eval("(" + jsonStr + ")")) ;
		}*/
//		jsonStr = jsonStr.substring(1,jsonStr.length() - 1);
//		jsonStr = jsonStr.replace("\\", "");
//		jsonStr = jsonStr.replace("\\\"", "\"").replace("\\\\n", "\\n");
//		JSONArray jsonArray = JSONArray.fromObject(jsonStr);
        return jsonArray;
    }

    /**
     * 获取有效jsonArray
     */
    public static JSONArray getJSONArray(JSONArray jsonArray) {
        JSONArray effectJSONArray = new JSONArray();
        if (jsonArray.size() == 0 || jsonArray.size() == 1) {
            return effectJSONArray;
        }
        for (int i = 1; i < jsonArray.size(); i++) {
            effectJSONArray.add(jsonArray.getJSONObject(i));
        }
        return effectJSONArray;
    }

}

