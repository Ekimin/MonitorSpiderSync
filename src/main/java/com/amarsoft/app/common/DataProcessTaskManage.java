package com.amarsoft.app.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amarsoft.are.jbo.BizObject;
import com.amarsoft.are.jbo.BizObjectManager;
import com.amarsoft.are.jbo.JBOException;
import com.amarsoft.are.jbo.JBOFactory;
import com.amarsoft.are.lang.DateX;

/**
 * @author jwang13
 * 数据处理流程表操作API
 */


public class DataProcessTaskManage {
   
	//根据机构号，模型id，任务阶段，执行状态获取任务编号（唯一标识）
	@SuppressWarnings("unchecked")
	public List<String> getUnfinishedTasknoByOrg(String bankId,String modelId,String taskStage,String exesTatus){
		List <String> returnList = new ArrayList<String>();
		try {
			BizObjectManager DataPcocessTaskBm = JBOFactory.getBizObjectManager("job.inspect.DATA_PROCESS_TASK");
			List<BizObject> tasknoList = DataPcocessTaskBm.createQuery(
					"select taskno from O where bankid =:bankid and mondelid =:modelid and taskstage =:taskstage and exesTatus")
					.setParameter("bankid", bankId).setParameter("modelid", modelId)
					.setParameter("taskstage", taskStage).setParameter("exestatus", exesTatus)
					.getResultList(false);
			for(BizObject bo : tasknoList){
				String taskno = bo.getAttribute("taskno").getString();
				returnList.add(taskno);
			}
		} catch (JBOException e) {
			e.printStackTrace();
		}
		return returnList;
	}
	
	//Map<taskno,exestatus>
	public void updateExeStatus(Map<String,String> tasknoMap){
		try {
			BizObjectManager DataPcocessTaskBm = JBOFactory.getBizObjectManager("job.inspect.DATA_PROCESS_TASK");
			String time = DateX.format(new Date(),"yyyy/MM/dd HH:mm:ss");
			for(String taskno : tasknoMap.keySet()){
				String exestatus = tasknoMap.get(taskno);
				if("init".equals(exestatus)){
					DataPcocessTaskBm.createQuery("update O set exestatus =:exestatus,starttime =:starttime where taskno =:taskno")
					.setParameter("exestatus", exestatus).setParameter("starttime", time)
					.setParameter("taskno", taskno).executeUpdate();
				}
				else if("success".equals(exestatus) || "failure".equals(exestatus)){
					DataPcocessTaskBm.createQuery("update O set exestatus =:exestatus,endtime =:endtime where taskno =:taskno")
					.setParameter("exestatus", exestatus).setParameter("endtime", time)
					.setParameter("taskno", taskno).executeUpdate();
				}
				else{
					DataPcocessTaskBm.createQuery("update O set exestatus =:exestatus where taskno =:taskno")
					.setParameter("exestatus", exestatus).setParameter("endtime", time)
					.setParameter("taskno", taskno).executeUpdate();
				}
			}
			
		} catch (JBOException e) {
			e.printStackTrace();
		}
	}
}
