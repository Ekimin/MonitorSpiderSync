package com.amarsoft.app.spider.LawCrawler;

/**
 * Created by ymhe on 2017/1/11.
 * MonitorSpiderSync
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amarsoft.are.ARE;

public class ChinaJudgementImage {

//	private CloseableHttpClient httpClient;

    private String __jsl_clearance;
    private String jsluid;
    private String cookies = "";

    private String usableIp = "";

    private int usablePort = 0;

    public String getUsableIp() {
        return usableIp;
    }

    public void setUsableIp(String usableIp) {
        this.usableIp = usableIp;
    }

    public int getUsablePort() {
        return usablePort;
    }

    public void setUsablePort(int usablePort) {
        this.usablePort = usablePort;
    }

    /*	public CloseableHttpClient getHttpClient() {
            return httpClient;
        }*/
    public String getCookies() {
        return jsluid + "; " + __jsl_clearance + ";";
    }



	/*
    public static void main(String[] args)throws Exception{
		String url ="http://sinafenqi.oicp.net/SinafqServer/Assistant/assistant";
		CloseableHttpClient httpClient = CatchHelp.createHttpsClient();
		CatchManager cm = new CatchManager();
		cm.execute(url, null, httpClient);
		System.out.println(cm.getResponseResult());
	}
	*/

    public static String parseImage(byte[] image) throws Exception {
        //转base64
        String b64 = SerializeObjectToString(image);
        //开始识别
        VerifyCodeServiceStub stub = new VerifyCodeServiceStub();
        VerifyCodeServiceStub.GetVerifyCode cs = new VerifyCodeServiceStub.GetVerifyCode();
        cs.setIn0(b64);
        cs.setIn1("jpg");
        String captcha = stub.getVerifyCode(cs).getOut();
        return captcha;
    }

    public static String SerializeObjectToString(Object o) {
        String ret = null;
        // 序列化使用的输出流
        ObjectOutputStream OOS = null;
        // 序列化后数据流给ByteArrayOutputStream 来保存。
        // ByteArrayOutputStream 可转成字符串或字节数组
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        // ByteArrayInputStream 可接收一个字节数组 "byte[] "。供反序列化做参数
        // ByteArrayInputStream BAIS=null;
        // 反序列化使用的输入流
        // ObjectInputStream OIS=null;
        try {
            OOS = new ObjectOutputStream(BAOS);
            OOS.writeObject(o);
            byte[] buff = BAOS.toByteArray();
			/*
			 * 由于byte[]数组是从ObjectOutputStream之后得来的，那么是不可以new
			 * String(buff)的，因为其中包含了不可见字符，根本不是一个字符串
			 */
            // System.out.println("序列化成一个byte[]的字符串形式 : " + buff);
            // 转换成字符串
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            ret = encoder.encode(buff);
            OOS.close();
        } catch (Exception e) {
        }
        return ret;
    }


    public List<String> getDetailIdList(String html) throws Exception {
        List<String> result = new ArrayList<String>();
        Pattern p = Pattern.compile("<a href=[\\w\\W]+?查看");
        Matcher m = p.matcher(html);
        while (m.find()) {
            String v = m.group(0);
            int iDot1 = v.indexOf("id=\"");
            int iDot2 = v.indexOf("\"", iDot1 + 4);
            if (iDot1 > -1 && iDot2 > iDot1) {
                result.add(v.substring(iDot1 + 4, iDot2));
            }
        }
        return result;
    }





	/*public static void run(String[] names)throws Exception{
		for(int i=0;i<names.length;i++){
			ShiXinImage sxi = new ShiXinImage();
			//获取图片
			byte[] image = sxi.getImage("http://shixin.court.gov.cn/image.jsp");
			String sCode = parseImage(image);
			//获取列表
			String sList = sxi.getList("http://shixin.court.gov.cn/search", sCode, names[i]);
			//获取所有详情id号
			System.out.println(sList);
			List<String> idlist = sxi.getDetailIdList(sList);
			String singid = idlist.get(0);
			//关闭连接
			if(sxi.getHttpClient()!=null)sxi.getHttpClient().close();
			//获取详情
			String sDetaillUrl = "http://shixin.court.gov.cn/detail";
			String details = sxi.getDetailList(sDetaillUrl, singid);
		//	for(String detail : details){
		//		System.out.println("=======" + details);
		//	}
		}
	}*/

    public static void main(String[] args) throws Exception {
        ChinaJudgementImage china = new ChinaJudgementImage();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        SpiderMethod sp = new SpiderMethod();
        Object[] oImage = sp.getBytes("http://wenshu.court.gov.cn/waf_captcha",
                "utf-8", "", null, httpClient);
        String code = china.parseImage((byte[]) oImage[0]);
        System.out.println("code为：" + code);
    }

}
