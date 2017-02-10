package com.amarsoft.amarmonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amarsoft.are.ARE;
import com.amarsoft.monitorPlugin.sink.ganglia.AbstractGangliaSink.GangliaOp;
import com.amarsoft.monitorPlugin.sink.ganglia.AbstractGangliaSink.GangliaSlope;
import com.amarsoft.monitorPlugin.sink.ganglia.GangliaConf;
import com.amarsoft.monitorPlugin.sink.ganglia.GangliaSink31;

/** 
 * @author zpxiao
 * @since 2017��2��10�� ����10:57:17 
 * ���ƽָ̨���ϱ�
 */
public class AmarMonitorAgent {

	private static String confFilePath;//configure file for javaAgent
	
	private static String instanceType;//��ض�������
	private static String instanceId;//��ض���ʵ������

	private static int collectPeriod = 20;//collect period in second
	private static int metaDataSendPeriod = 60;//collect period in second
	
	private static Pattern pattern = Pattern.compile("[0-9]*");
	
	/**
	 * @param groupName ָ��������
	 * @param name ָ������
	 * @param type ָ����������,ȡֵ���� uint8��uint32��uint16��float��double��string
	 * @param value ָ��ֵ
	 * @param op ����ֵ�Ƚϲ�����,ȡֵ����  GangliaOp.valueOf("EQ")��GangliaOp.valueOf("LE")��GangliaOp.valueOf("EQ")LE��GangliaOp.valueOf("GT")��GangliaOp.valueOf("GE"),EQ����=����LE����<=������LE��<����GT����>����GE����>=��
	 * @param warnTh ������ֵ
	 * @param critTh ������ֵ
	 * @throws IOException
	 */
	public void emitMetric(String groupName, String name, String type, String value,GangliaOp op,
			String warnTh, String critTh) {
		init();
		Thread memThread = new Thread(new EmitMetricThread(groupName,name,type,value,op,warnTh,critTh));
		//memThread.setDaemon(true);
		memThread.setName("EmitMetricThread");
		memThread.start();
	}
	
	/**
	 * ��ʼ��instanceType��instanceId��confFilePath����
	 */
	private static void init(){
		ARE.getLog().debug("��ʼ��");
		if(instanceType==null){
			instanceType = ARE.getProperty("com.amarsoft.amarmonitor.AmarMonitorAgent.instanceType", "java");
		}
		if(instanceId==null){
			instanceId = ARE.getProperty("com.amarsoft.amarmonitor.AmarMonitorAgent.instanceId");
			if(instanceId==null){
				instanceId = ""+getJavaInstanceNum();
			}
		}
		if(confFilePath==null){
			confFilePath = ARE.getProperty("com.amarsoft.amarmonitor.AmarMonitorAgent.confFilePath", ARE.getProperty("APP_HOME")+"/etc/gangliaPlugin.properties");
		}
	}

	protected class EmitMetricThread implements Runnable {
		private String groupName;//ָ��������
		private String name;//ָ������
		private String type;//ָ����������
		private String value;//ָ��ֵ
		private GangliaOp op;//����ֵ�Ƚϲ�����
		private String warnTh;//������ֵ
		private String critTh;//������ֵ
		
		public EmitMetricThread(String groupName, String name, String type, String value,GangliaOp op,String warnTh, String critTh){
			this.groupName = groupName;
			this.name = name;
			this.type = type;
			this.value = value;
			this.op = op;
			this.warnTh = warnTh;
			this.critTh = critTh;
			
		}
		
		public void run() {
			int rate = metaDataSendPeriod/collectPeriod;
			GangliaSink31 ganliaSink = new GangliaSink31();
			ARE.getLog().debug(new StringBuilder().append("confFilePath:").append(confFilePath)
					.append(",instanceType:").append(instanceType).append(",instanceId:").append(instanceId).toString());
			ganliaSink.init(confFilePath, instanceType, instanceId);

			try {
				if(ARE.getLog().isInfoEnabled()){
					ARE.getLog().info(new StringBuilder().append("���ƽָ̨���ϱ�").append("groupName:")
							.append(this.groupName).append(",name:").append(this.name).append(",type:").append(this.type)
							.append(",value:").append(this.value).toString());
				}
				ganliaSink.emitMetric(this.groupName, this.name, this.type, this.value, new GangliaConf(),
						GangliaSlope.valueOf("both"), op, warnTh, critTh, rate);
			} catch (Exception e) {
				if(ARE.getLog().isErrorEnabled()){
					ARE.getLog().error(new StringBuilder().append("���ƽָ̨���ϱ�����,").append("groupName:")
							.append(this.groupName).append(",name:").append(this.name).toString(),e);
				}
				e.printStackTrace();
			} 
		}
	}
	
	private static int getJavaInstanceNum() {
		int javaProcessNum = 0;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		try {
			File procFileSystem = new File("/proc/");

			File[] procFiles = procFileSystem.listFiles();

			for (int i = 0; i < procFiles.length; i++) {
				if (!procFiles[i].isDirectory()) {
					continue;
				}

				if (!isNumeric(procFiles[i].getName())) {
					continue;
				}

				// System.out.println("opening the file:" +
				// procFiles[i].getName());
				File[] processFile = procFiles[i].listFiles();
				for (int j = 0; j < processFile.length; j++) {
					if (processFile[j].isFile() && processFile[j].getName().equals("cmdline")) {
						FileInputStream input = new FileInputStream(processFile[j]);
						byte[] buf = new byte[4096];
						input.read(buf);
						input.close();

						for (int k = 0; k < buf.length; k++) {
							if (buf[k] == 0)
								buf[k] = ' ';
						}
						String cmdLine = new String(buf);
						// System.out.println("opening the file:cmdLine=" +
						// cmdLine);
						cmdLine = cmdLine.toUpperCase();
						// System.out.println("opening the file:cmdLine=" +
						// cmdLine);
						if (cmdLine.contains("JAVA") && cmdLine.contains("CLASSPATH")
								&& cmdLine.contains(instanceType.toUpperCase())) {
							javaProcessNum++;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("javaProcessNum=" + javaProcessNum);
		return javaProcessNum;
	}
	
	private static boolean isNumeric(String str) {
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
