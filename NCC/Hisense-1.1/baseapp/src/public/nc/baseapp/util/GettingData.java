package nc.baseapp.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

import com.alibaba.fastjson.JSONObject;
import com.cloudera.impala.jdbc42.internal.apache.http.impl.client.CloseableHttpClient;
import com.cloudera.impala.jdbc42.internal.apache.http.impl.client.HttpClients;
import org.apache.commons.io.IOUtils;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.baseapp.utils.ConfigUtils;

import java.security.cert.CertificateException;

import nc.baseapp.util.GettingData;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;
import nccloud.security.impl.SignatureTookKit;
import sun.misc.BASE64Decoder;

import java.security.cert.X509Certificate;
  
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GettingData {
	
	public String getUsercodeByToken(String token) {
		String usercode = "";
		InvocationInfoProxy.getInstance().setGroupId("00011A10000000000MDP");
		InvocationInfoProxy.getInstance().setUserId("00011A10000000003ALB");// g01----00011A10000000003ALB
		ConfigUtils configUtils = new ConfigUtils();

		InvocationInfoProxy.getInstance().setUserDataSource(configUtils.getValueFromProperties("ncds"));
		NCLocator.getInstance().lookup(ISecurityTokenCallback.class).token("NCSystem".getBytes(), "pfxx".getBytes());
		
		Map<String, String> map = new HashMap<>();
		map.put("X-Ldp-Token", token);
		map.put("X-Realm", "employee");
		String param = JSONObject.toJSONString(map);// 将数组转化成json格式
		// 1、使用token调用uua接口获取用户信息
		String userInfo = doGet_Requeries(configUtils.getValueFromProperties("userInfo"), param);
		JSONObject json = JSONObject.parseObject(userInfo);// json数据格式转换成json对象
		JSONObject data = json.getJSONObject("data");
		String psncode = data.getString("userAccount");
		
		// 2、使用用户信息的员工编码获取NC用户编码
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);// 可以执行sql语句的类
		String sql = "select a.user_code from sm_user a left join bd_psndoc b on a.pk_psndoc = b.pk_psndoc where b.code='"
				+ psncode + "'";
		try {
			usercode = (java.lang.String) query.executeQuery(sql, new ColumnProcessor());// 执行sql语句并返回特定的值，且强转成String类型
			
		} catch (Exception e) {
			return "{\"success\":\"false\",\"code\":\"300\",\"msg\":\"查询失败"+e.getMessage()+"\"}";
		}
		return usercode;// 返回usercode
	}

	public String doPost_Requires(String urlStr, String param) {
		StringBuffer sb = new StringBuffer("");
//        HttpResponse res = new HttpServletResponse();
		try {
			// http://10.30.6.182:9080/open/login
			// 创建连接
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json;");
			connection.setRequestProperty("encoding", "UTF-8");
			connection.setConnectTimeout(12000);
			connection.setReadTimeout(12000);
			connection.connect();
			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(param.getBytes("UTF-8"));
			out.flush();
			out.close();
			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String lines;
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes());
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			ExceptionUtils.wrappBusinessException("调用接口(" + urlStr + ")发生异常：" + e.getMessage());
		}
		return sb.toString();
	}
	
	public String doGet_Requeries(String urlStr, String param) {
		 	HttpURLConnection conn = null;
	        InputStream is = null;
	        BufferedReader br = null;
	        StringBuilder result = new StringBuilder();
	        try{
	            //创建远程url连接对象
	            URL url = new URL(urlStr);
	            //https协议的跳过认证
	            if("https".equalsIgnoreCase(url.getProtocol())){
					createSSLClientDefault();//如果是https请求，跳过认证
		        }
	          //通过远程url连接对象打开一个连接，强转成HTTPURLConnection类
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            //设置连接超时时间和读取超时时间
	            conn.setConnectTimeout(15000);
	            conn.setReadTimeout(60000);
	            conn.setRequestProperty("Accept", "application/json");
	            JSONObject json = JSONObject.parseObject(param);
	            String token = json.getString("X-Ldp-Token");
	            String employee = json.getString("X-Realm");
	            conn.setRequestProperty("X-Ldp-Token", token);
				conn.setRequestProperty("X-Realm", employee);
	            //发送请求
	            conn.connect();
	            //通过conn取得输入流，并使用Reader读取
	            if (200 == conn.getResponseCode()){//状态码
	                is = conn.getInputStream();
	                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	                String line;
	                while ((line = br.readLine()) != null){
	                    result.append(line);
	                    System.out.println(line);
	                }
	            }else if(404 == conn.getResponseCode()) {
	            	System.out.println("请求的网页不存在");
	            }else if(503 == conn.getResponseCode()) {
	            	System.out.println("服务不可用");
	            }
	            else{
	                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
	            }
	        }catch (MalformedURLException e){
	            e.printStackTrace();
	        }catch (IOException e){
	            e.printStackTrace();
	        }catch (Exception e){
	            e.printStackTrace();
	        }finally {
	            try{
	                if(br != null){
	                    br.close();
	                }
	                if(is != null){
	                    is.close();
	                }
	            }catch (IOException ioe){
	                ioe.printStackTrace();
	            }
	            conn.disconnect();
	        }
	        return result.toString();
	}

	public String httpsyanzheng(String url, String usercode)throws Exception {
		ConfigUtils configUtils = new ConfigUtils();
		String client_name = configUtils.getValueFromProperties("client_name");
		String client_id = configUtils.getValueFromProperties("client_id_dqz");
		String client_security = configUtils.getValueFromProperties("client_security");
		String busicentercode = configUtils.getValueFromProperties("busicentercode");
		URL u = new URL(url);
		URLConnection uc = null;
		try {
//			String client_name = "NCC1107"; 
//			String client_id = "1";
//			String client_security = "123456";
			
			if("https".equalsIgnoreCase(u.getProtocol())){
				createSSLClientDefault();//如果是https请求，跳过认证
	        }
	        uc = u.openConnection();
	        uc.setConnectTimeout(2000);
	        uc.setReadTimeout(2000);
	        uc.setDoOutput(true);
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			uc.setRequestProperty("Content-Length", "10000");
			uc.setRequestProperty("userid", usercode);
			uc.setRequestProperty("busicentercode", busicentercode);//账套编码
			HttpURLConnection hc = (HttpURLConnection) uc;
			hc.setRequestMethod("POST");
			OutputStream os = null;
			DataOutputStream dos = null;
			String returnFlag = "";
			InputStream is = null;
			try {
				StringBuffer sb = new StringBuffer();
				String ts = (System.currentTimeMillis() + "").substring(0, 6);
				String keys = usercode + client_security + ts;
				String security = new Base64().encodeToString(SignatureTookKit.digestSign(usercode.getBytes("UTF-8"), keys.getBytes("UTF-8")));
				//response.write();//response.getWriter().writer()
	            boolean isPass = SignatureTookKit.digestVerify(usercode.getBytes("UTF-8"), keys.getBytes("UTF-8"), new BASE64Decoder().decodeBuffer(security));
				if(isPass) {
					System.out.print("true");
				}else{
					System.out.print("false");
	            }
				sb.append("type=type_security&client_name=" + client_name + "&usercode=" + usercode + "&client_id=" + client_id + "&security="+security+ "&ts=" + ts);
				String sss = sb.toString().replaceAll("\\+","%2B");
				os = hc.getOutputStream();
				dos = new DataOutputStream(os);
				dos.writeBytes(sss);
				dos.flush();
	
				is = hc.getInputStream();
				int ch;
	
				while ((ch = is.read()) != -1) { 
					returnFlag += String.valueOf((char) ch);
				}
	
				System.out.println(returnFlag);
				
				return returnFlag.toString();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dos != null) {
					try {
						dos.close();
					} catch (Exception e2) {
					}
				}
				if (os != null) {
					try {
						os.close();
					} catch (Exception e2) {
					}
				}
				if (is != null)
					try {
						is.close();
					} catch (Exception e2) {
					}
			}
		}catch (Exception e1){
			e1.printStackTrace();
		}
		
        return IOUtils.toString(uc.getInputStream());
	}
	
	public static CloseableHttpClient createSSLClientDefault() {
        try {
        	 HostnameVerifier hv = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
        	    };
    	    trustAllHttpsCertificates();
    	    HttpsURLConnection.setDefaultHostnameVerifier(hv);

        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        return HttpClients.createDefault();
    }
	private static void trustAllHttpsCertificates() throws Exception {
	    TrustManager[] trustAllCerts = new TrustManager[1];
	    TrustManager tm = new miTM();
	    trustAllCerts[0] = tm;
	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, null);
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	static class miTM implements TrustManager,X509TrustManager {
	    public X509Certificate[] getAcceptedIssuers() {
	        return null;
	    }
	  
	    public boolean isServerTrusted(X509Certificate[] certs) {
	        return true;
	    }
	  
	    public boolean isClientTrusted(X509Certificate[] certs) {
	        return true;
	    }
	  
	    public void checkServerTrusted(X509Certificate[] certs, String authType)
	            throws CertificateException {
	        return;
	    }
	  
	    public void checkClientTrusted(X509Certificate[] certs, String authType)
	            throws CertificateException {
	        return;
	    }
	}
	
}