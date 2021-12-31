package nc.baseapp.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.baseapp.utils.ConfigUtils;

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

}