package com.amarsoft.app.spider.LawCrawler;

/**
 * Created by ymhe on 2017/1/11.
 * MonitorSpiderSync
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;


import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.amarsoft.are.ARE;


public class SpiderMethod {

    private static String cookie;
    private int count = 0;

    private int timeoutCount = 3;
    private int executeCount = 0;

    private int socketTimeout = 15000;

    private int connectTimeout = 15000;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    protected RequestConfig createRequestConfig(String sProxyIp, int iProxyPort) {
        RequestConfig config = null;
        sProxyIp = ARE.getProperty("PROXY_IP", "");
        iProxyPort = ARE.getProperty("PROXY_PORT", 0);
        if (sProxyIp.length() > 0 && iProxyPort > 0) {
        }
        org.apache.http.HttpHost proxy = new org.apache.http.HttpHost(sProxyIp, iProxyPort, "http");
        int socket_time_out = ARE.getProperty("socket_time_out", 8000);
        int connection_time_out = ARE.getProperty("connection_time_out", 10000);
        config = RequestConfig.custom()
                .setSocketTimeout(socket_time_out)
                .setConnectTimeout(connection_time_out)
                .setProxy(proxy).build();
        return config;

    }

    /**
     * 创建Get请求的公共头部信息
     * */
    private static void createGetHeader(HttpGet httpGet,String sourceUrl){
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Connection", "keep-alive");
        //httpGet.setHeader("Cookie", "_gscu_1241932522=52754564a5pz8g37; yunsuo_session_verify=0d92b5a3d022d6c0c3f632923a897590; _gscu_2116842793=565426314bab6t76; _gscs_2116842793=565426317df6n276|pv:2; _gscbrs_2116842793=1");
        //httpGet.setHeader("Cookie","_gscu_1241932522=52754564a5pz8g37; _gscu_1049835508=567062918do4i437; yunsuo_session_verify=b6a3668d4feb040fba8da5bcfcd0936f; ASP.NET_SessionId=vwhv0giiu2oiz3lw2yzovxo2; wzwsconfirm=4161180b813da5e34b140044c4f7e8f0; wzwstemplate=Nw==; ccpassport=bfcbc8d0abeb101a99924bd38d2108ed; wzwschallenge=-1; _gsref_2116842793=http://wenshu.court.gov.cn/list/list/?sorttype=1; _gscu_2116842793=565426314bab6t76; _gscs_2116842793=t57415604nzmbxs54|pv:3; _gscbrs_2116842793=1");
        httpGet.setHeader("Host", "wenshu.court.gov.cn");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
        httpGet.setHeader("Referer", sourceUrl);
    }

    /**
     * 创建请求的公共头部信息
     * */
    public static void createHeader(HttpPost post,String sourceUrl){
        post.setHeader("Accept","*/*");
        post.setHeader("Accept-Encoding","gzip, deflate");
        post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        post.setHeader("Connection","keep-alive");
        //post.setHeader("Content-Length","78");
        post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        post.setHeader("Cache-Control", "max-age=0");
        //	post.setHeader("Cookie", "_gscu_1241932522=52754564a5pz8g37; yunsuo_session_verify=0d92b5a3d022d6c0c3f632923a897590; _gscu_2116842793=565426314bab6t76; _gscs_2116842793=565426317df6n276|pv:4; _gscbrs_2116842793=1");
        post.setHeader("Host", "wenshu.court.gov.cn");
        post.setHeader("Origin", "http://wenshu.court.gov.cn");
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
        post.setHeader("Referer", sourceUrl);
        //伪造IP
        //	post.setHeader("X-FORWARDED-FOR", generatePseudoRandomIP());
    }

    /**
     * 抓取中国裁判文书网的诉讼数据
     *
     * @param sourceUrl  入口url
     * @param contentUrl
     * @param params     传递的请求参数
     * @param charSet
     * @throws Exception
     * @throws ClientProtocolException
     */
    public String getJSON(String sourceUrl, String contentUrl, Map<String, String> params, String charSet) throws Exception {
        String cookie = "";
        String jsonStr = "{}";
        String jsonstr = "{}";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost post = null;
        HttpGet get = null;
        //	httpClient.getParams().setParameter(ConnRouteParams.LOCAL_ADDRESS, "58.243.0.162");
        String queryValue = params.get("Param");
        try {
            post = new HttpPost(sourceUrl);


            get = new HttpGet(sourceUrl);
            createGetHeader(get, sourceUrl);
            //IPModel ipModel = getSelectModel();
            RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
            //params.put("Param", "");
            //RequestConfig config= null;
            /*if (ipModel != null){
				ARE.getLog().info("通过代理IP[ "+ipModel.getIp()+" ]爬取中国裁判文书网");
				config = createRequestConfig("106.34.108.219",3128);
			}*/
            //config = createRequestConfig("1.209.188.180",8080);
			/*if(config!=null){
				ARE.getLog().debug("user proxy");
				post.setConfig(config);
			}*/

            createHeader(post, sourceUrl);//创建请求头部信息
            ARE.getLog().info("获得头部信息结束");

            get.setConfig(config);
            ARE.getLog().info("开始以get方式获取请求");
            response = httpClient.execute(get);
            ARE.getLog().info("开始获取状态码");
            int status = response.getStatusLine().getStatusCode();

            ARE.getLog().info("返回的状态码：" + status);
            if (status == HttpStatus.SC_MOVED_TEMPORARILY || status == HttpStatus.SC_MOVED_PERMANENTLY) {//重定向
                Header locationHeader = response.getFirstHeader("Location");
                String localtionUrl = locationHeader.getValue();
                ARE.getLog().info("重定向到URL：" + localtionUrl);
                return getJSON(localtionUrl, contentUrl, params, charSet);
            } /*else if (status == 403) {
				ARE.getLog().info("IP地址["+ipModel.getIp()+"]被禁止访问，随机选则一个IP地址后继续访问");
				if (ipModel != null) {
					update(ipModel);
				}
				ipModel = getSelectModel();
				if (count ++ < 5) {
					getJSON(sourceUrl,contentUrl,params,charSet);
				} else {
					ARE.getLog().error("针对IP地址被进行访问，已随机选择5个代理IP");
				}

			}*/ else {
                if (status == HttpStatus.SC_OK) {
                    ARE.getLog().info("开始检查头文件是否含有cookie标志");
                    boolean flag = response.containsHeader("Set-Cookie");
                    ARE.getLog().info("结束检查头文件是否含有cookie标志");
                    if (flag) {
                        ARE.getLog().info("开始获取cookie");
                        cookie = response.getFirstHeader("Set-Cookie").getValue();//获取服务器端返回的cookie
                        ARE.getLog().info("获取cookie结束");
                        if (cookie.indexOf(";") > -1) {
                            ARE.getLog().info("开始对cookie进行字符串过滤");
                            cookie = cookie.substring(0, cookie.indexOf(";"));
                            ARE.getLog().info("cookie进行字符串过滤结束");
                        }
                    }
                }
				/*if (status == 521) {
					cookie = response.getFirstHeader("Set-Cookie").getValue();//获取服务器端返回的cookie
					if (cookie.indexOf(";") > -1) {
						cookie = cookie.substring(0, cookie.indexOf(";"));
					}
					String returnValue = EntityUtils.toString(response.getEntity(), charSet);
					if (returnValue.trim().startsWith("<script>")) {
						returnValue = returnValue.replace("<script>", "").replace("</script>", "");
						returnValue = "var document={};function setTimeout(fun,t){};function alert(msg){};"+returnValue;
					}
					ScriptEngineManager factory = new ScriptEngineManager();
					ScriptEngine engine = factory.getEngineByName("javascript");
					engine.eval(returnValue);
					String dc = engine.get("dc").toString();
					if (dc.indexOf(";") > -1) {
						dc = dc.substring(0, dc.indexOf(";"));
					}
					cookie = cookie+";"+dc+";";
				}*/

                post = new HttpPost(contentUrl);//请求内容
                createHeader(post, sourceUrl);
                post.setHeader("Cookie", cookie);
                //	post.setConfig(config);
                this.setCookie(cookie);
                params.put("Param", queryValue);
                post.setEntity(new UrlEncodedFormEntity(createFormData(params), charSet));
                ARE.getLog().info("开始以post方式获取请求");
                post.setConfig(config);

                response = httpClient.execute(post);
                status = response.getStatusLine().getStatusCode();
                System.out.println("获取详情返回的状态码：" + status);
                jsonStr = EntityUtils.toString(response.getEntity(), charSet);

                if (jsonStr.equals("\"remind\"")) {

                    //网站访问过于频繁,需通过验证码验证才能继续访问

                    ARE.getLog().info("开始获取图片验证码");
                    Object[] oImage = getBytes("http://wenshu.court.gov.cn/User/ValidateCode",
                            charSet, "", null, httpClient);
                    //识别验证码


                    String projectPath = System.getProperty("user.dir");
                    //验证码图片的临时存储路径

                    StringBuffer imagePath = new StringBuffer();
                    imagePath.append(projectPath);
                    imagePath.append("/data/image/image.jpg");

                    File file = new File(imagePath.toString());
                    if (file != null) {
                        if (!file.getParentFile().exists()) {
                            if (!file.getParentFile().mkdirs()) {
                                System.err.println("创建目录文件所在的目录失败！");
                            }
                        }
                        // System.out.println(file.getAbsolutePath());
                        file.createNewFile();
                    }
                    ARE.getLog().info("开始存储图片到" + imagePath.toString());

                    java.io.FileOutputStream fos = new java.io.FileOutputStream(imagePath.toString());
                    fos.write((byte[]) oImage[0]);
                    fos.close();
                    ARE.getLog().info("存储图片完毕");
                    ChinaJudgementImage cjImage = new ChinaJudgementImage();
                    ARE.getLog().info("开始解析图片验证码");
                    String sCode = cjImage.parseImage((byte[]) oImage[0]);
                    sCode = sCode.replace("\n\n", "");

                    ARE.getLog().info("解析出的图片验证码为sCode =" + sCode);

                    Properties postParams = new Properties();
                    postParams.setProperty("ValidateCode", sCode);
                    ARE.getLog().info("开始提交验证码");

                    Object[] oFinal1 = getHtml("http://wenshu.court.gov.cn/Content/CheckVisitCode",
                            charSet, "", null, postParams, httpClient);

                    return getJSON(sourceUrl, contentUrl, params, charSet);

                }

                return jsonStr;

            }
            //ARE.getLog().info("返回jsonStr字符串"+jsonStr);
        } catch (SocketTimeoutException e) {
            return retryExecute(sourceUrl, contentUrl, params, charSet, "网络读取超时", queryValue, httpClient);
        } catch (ConnectTimeoutException e) {
            return retryExecute(sourceUrl, contentUrl, params, charSet, "网络连接超时", queryValue, httpClient);
        } catch (Exception e) {
            if (e.toString().startsWith("java.net")) {
                return retryExecute(sourceUrl, contentUrl, params, charSet, "网络连接失败", queryValue, httpClient);
            } else {
                if (params.get("Param").equals("") && !queryValue.equals("")) {
                    params.put("Param", queryValue);
                }
                ARE.getLog().info(e);
                ARE.getLog().info("连接失败，睡眠1分钟");
                Thread.sleep(Integer.parseInt(ARE.getProperty("NoConnectionSleepTime", "1")) * 60 * 1000);
                throw e;
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
                response.close();
            }
            if (post != null) {
                post.abort();
            }
            if (get != null) {
                get.abort();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }


    }


    //重新抓取，当网络连接超时的时候使用到
    protected String retryExecute(String sourceUrl, String contentUrl, Map<String, String> params, String charSet, String title, String queryValue, CloseableHttpClient httpClient) throws Exception {
        if (executeCount < this.timeoutCount) {
            this.executeCount++;
            ARE.getLog().info("开始第" + executeCount + "次重新抓取");
            return getJSON(sourceUrl, contentUrl, params, charSet);
        } else {
            if (params.get("Param").equals("") && !queryValue.equals("")) {
                params.put("Param", queryValue);
            }
            //如果尝试3次都连接失败，休眠30分钟
            ARE.getLog().info("尝试失败，睡眠30分钟");
            Thread.sleep(Integer.parseInt(ARE.getProperty("NoConnectionSleepTime", "30")) * 60 * 1000);
            throw new Exception(title + ",已尝试了" + executeCount + "次");

        }
    }



    /**
     * 生成伪随机IP地址
     */
    private static String generatePseudoRandomIP() {
        String pseudoRandomIP = "";
        for (int i = 0; i <= 3; i++) {
            pseudoRandomIP = pseudoRandomIP + generateRandomInt(256);
            if (i != 3) {
                pseudoRandomIP = pseudoRandomIP + ".";
            }
        }
        return pseudoRandomIP;
    }

    /**
     * 生成一个随机整数
     */
    private static int generateRandomInt(int n) {
        Random random = new Random();
        return random.nextInt(n);
    }

    /**
     * 创建关联文书的头部信息
     */
    public static void createGetRelativeFileRequestHeader(HttpPost post, String sourceUrl) {
        post.setHeader("Accept", "*/*");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.setHeader("Cache-Control", "max-age=0");
        //post.setHeader("Cookie", "");
        post.setHeader("Host", "wenshu.court.gov.cn");
        post.setHeader("Origin", "http://wenshu.court.gov.cn");
        post.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
        post.setHeader("Referer", sourceUrl);
        post.setHeader("X-Requested-With", "XMLHttpRequest");
    }

    /**
     * 创建form提交参数
     */
    private List<NameValuePair> createFormData(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return list;
    }


    public static void main(String[] args) throws Exception {
        ARE.init("etc/are.xml");

        Map<String, String> params = createParams();
        params.put("Param", "上传日期:2016-01-12 TO 2016-01-13,案件类型:民事案件,裁判日期:2015-05-16 TO 2015-05-17");
        String sourceUrl = "http://wenshu.court.gov.cn/List/List?sorttype=1";
        String contentUrl = "http://wenshu.court.gov.cn/List/ListContent";
        String charSet = "utf-8";
        SpiderMethod method = new SpiderMethod();
        //String jsonStr = method.getJSON(sourceUrl, contentUrl, params, charSet,httpClient);
        //	System.out.println("jsonStr = "+jsonStr);

		/*Scanner in = new Scanner(new File("src/json.txt"));
		String json = "";
		while (in.hasNextLine()) {
			json += in.nextLine();
		}
		in.close();

		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("javascript");
		engine.eval("var document={};function setTimeout(fun,t){};function alert(msg){};"+json);
		String dc = engine.get("dc").toString();

		System.out.println("dc = "+dc);*/

    }

    /**
     * 构建查询参数
     */
    public static Map<String, String> createParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Param", "");
        params.put("Index", "1");
        params.put("Page", "20");
        params.put("Order", "法院层级");
        params.put("Direction", "asc");
        return params;
    }




    public Object[] getBytes(String url, String charset, String requestCookies, Properties requestHeader, CloseableHttpClient httpClient) throws Exception {
        HttpRequestBase httpMethod = null;
        CloseableHttpResponse response = null;
        byte[] image = null;
        String cookies = "";
        Object[] bytes = new Object[3];
        try {

            httpMethod = new HttpGet(url);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(this.socketTimeout)
                    .setConnectTimeout(this.connectTimeout)
                    .build();

            httpMethod.setConfig(requestConfig);
            if (requestCookies != null && requestCookies.trim().length() > 0) {
                ARE.getLog().info("set request cookie:" + requestCookies);
                httpMethod.addHeader("Cookie", requestCookies);
            }
            httpMethod.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
            if (requestHeader != null) {
                java.util.Iterator it = requestHeader.keySet().iterator();
                while (it.hasNext()) {
                    String sKey = it.next().toString();
                    String sValue = requestHeader.getProperty(sKey);
                    httpMethod.addHeader(sKey, sValue);
                }
            }
            response = httpClient.execute(httpMethod);
            image = readBytesFromResponse(response);
            //cookies = readCookiesFromResposne(response);
            bytes[0] = image;
            bytes[1] = response.getStatusLine().getStatusCode();
            bytes[2] = cookies;
        } catch (SocketTimeoutException e) {
            //	retryExecute(url,null,"网络读取超时",httpClient);
            bytes = retryGetBytes("网络读取超时", url, charset, requestCookies, requestHeader, httpClient);
        } catch (ConnectTimeoutException e) {
            //	retryExecute(url,null,"网络连接超时",httpClient);
            bytes = retryGetBytes("网络连接超时", url, charset, requestCookies, requestHeader, httpClient);

        } catch (Exception e) {
            if (e.toString().startsWith("java.net")) {
                //	retryExecute(url,null,"网络连接失败",httpClient);
                bytes = retryGetBytes("网络连接失败", url, charset, requestCookies, requestHeader, httpClient);

            } else {
                ARE.getLog().info(e);
                e.printStackTrace();

                throw e;
            }
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        }
        return bytes;

    }


    private static byte[] readBytesFromResponse(CloseableHttpResponse response) throws Exception {
        InputStream is = response.getEntity().getContent();
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = is.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, rc);
            //if(rc<1024)break;
        }
        is.close();
        swapStream.close();

        return swapStream.toByteArray();
    }

    public static Object[] getHtml(String url, String charset, String requestCookies, Properties requestHeader, Properties postParams, CloseableHttpClient httpClient) throws Exception {
        HttpRequestBase httpMethod = null;
        CloseableHttpResponse response = null;
        try {

            ARE.getLog().info("connect to " + url);
            if (postParams == null) {
                httpMethod = new HttpGet(url);
            } else {
                httpMethod = new HttpPost(url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                java.util.Iterator it = postParams.keySet().iterator();
                while (it.hasNext()) {
                    String sKey = it.next().toString();
                    if (sKey.length() == 0) continue;
                    String sValue = postParams.get(sKey).toString();
                    nameValuePairs.add(new BasicNameValuePair(sKey, sValue));
                    ARE.getLog().info("填充post参数:" + sKey + "=" + sValue);
                }
                ARE.getLog().info("post参数编码：" + charset);
                ((HttpPost) httpMethod).setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            }

            if (requestCookies != null && requestCookies.trim().length() > 0) {
                ARE.getLog().info("set request cookie:" + requestCookies);
                httpMethod.addHeader("Cookie", requestCookies);
            }
            httpMethod.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
            if (requestHeader != null) {
                java.util.Iterator it = requestHeader.keySet().iterator();
                while (it.hasNext()) {
                    String sKey = it.next().toString();
                    String sValue = requestHeader.getProperty(sKey);
                    httpMethod.addHeader(sKey, sValue);
                }
            }
            response = httpClient.execute(httpMethod);
            String sHtml = EntityUtils.toString(response.getEntity(), charset);
            //String cookies = readCookiesFromResposne(response);
            return new Object[]{sHtml, response.getStatusLine().getStatusCode(), ""};

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
                response.close();
            }
        }
    }

    public Object[] retryGetHtml(String title, String url, String charset, String requestCookies, Properties requestHeader, Properties postParams, CloseableHttpClient httpClient) throws Exception {
        Object[] bytes = null;
        if (executeCount < this.timeoutCount) {
            this.executeCount++;
            ARE.getLog().info("开始第" + executeCount + "次重新抓取");

            bytes = getHtml(url, charset, requestCookies, requestHeader, postParams, httpClient);

        } else {
            throw new Exception(title + ",已尝试了" + executeCount + "次");
        }
        return bytes;
    }

    public Object[] retryGetBytes(String title, String url, String charset, String requestCookies, Properties requestHeader, CloseableHttpClient httpClient) throws Exception {
        Object[] bytes = null;
        if (executeCount < this.timeoutCount) {
            this.executeCount++;
            ARE.getLog().info("开始第" + executeCount + "次重新抓取");
            bytes = getBytes(url, charset, requestCookies, requestHeader, httpClient);

        } else {
            throw new Exception(title + ",已尝试了" + executeCount + "次");
        }
        return bytes;
    }

}
