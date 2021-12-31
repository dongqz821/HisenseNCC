package ncc.impl.baseapp.voucher;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.baseapp.utils.ConfigUtils;
import ncc.itf.baseapp.voucher.ICMPService;
import uap.ws.rest.client.ResourceContentType;
import uap.ws.rest.client.UAPRSHttpClient;
import uap.ws.rs.exception.UAPRSException;
import ncc.baseapp.utils.OpenAPIUtis;

public class CMPServiceImpl implements ICMPService{

	@Override
	public String saveCMP(String param) {
		OpenAPIUtis utils =  new OpenAPIUtis();
			
		String result = utils.callAPI(JSONObject.parseObject(param));

		return result;
	}

	@Override
	public String writeBack2NC65(JSONObject json) {
		ConfigUtils configUtils = new ConfigUtils();
		String nc65ip = configUtils.getValueFromProperties("nc65signurl");
		String param = JSONObject.toJSONString(json);
		JSONObject param1 = JSONObject.parseObject(param);
		String result = "";
		try {
			result = uaphttp(nc65ip, param1);
		} catch (Exception e) {
			Logger.error(e.getMessage());
            ExceptionUtils.wrappBusinessException("调用接口("+ nc65ip +")发生异常："+e.getMessage());
		}
		
		return result;
	}
	
	public String uaphttp(String url, JSONObject obj) {
		UAPRSHttpClient uap = new UAPRSHttpClient();
		String str = "";
		try {
			str = uap.doPost(url, obj, ResourceContentType.JSON);
		} catch (UAPRSException e) {
		  ExceptionUtils.wrappBusinessException("调用接口("+ url +")发生异常："+e.getMessage());
		}

		return str;
	}

	@SuppressWarnings("null")
	@Override
	public String settlementBack2NC65(JSONObject json) {
		String billcode = json.getString("billcode");//业务单据编号
		String result = "";
		Map<String, Object> bank = new HashMap<String, Object>();
		
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		try {
			Object object = query.executeQuery( "select pk_upbill,def20 from cmp_paybill where dr = 0 and  bill_no = '"+ billcode + "'", new MapProcessor());
			if (object != null) {
				bank = (Map) object;
			}
			bank.put("settlestatus", json.getString("settlestatus"));//结算状态
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException("发生异常："+e.getMessage());
		}
		
		ConfigUtils configUtils = new ConfigUtils();
		String nc65ip = configUtils.getValueFromProperties("nc65signurl");
		String param = JSONObject.toJSONString(bank);
		JSONObject param1 = JSONObject.parseObject(param);
		if (!"".equals(bank.get("def20")) && bank.get("def20") != null  ) {
			result = uaphttp(nc65ip, param1);
		}

		return result;
	}

	@Override
	public String delCMP(String param) {
		OpenAPIUtis utils =  new OpenAPIUtis();
		String result = utils.delAPI(JSONObject.parseObject(param));
		return result;
	}
	
    public JSONObject sendGet(String hsurl) throws Exception {// HTTP GET请求
    	 URL url = null;
//	     String url11 = "https://dccusts.hisense.com/cmp-service/oapi/customer/customerInfo/page?pageIndex=1"+"&pageSize=1"+"&beginTime="+URLEncoder.encode("2021-12-1","utf-8")+"&endTime="+URLEncoder.encode("2021-12-1","utf-8");
    	 try {
    	        url = new URL(hsurl);
//    	        URLEncoder.encode("url","charset");
    	        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    	        X509TrustManager xtm = new X509TrustManager() {

					@Override
					public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
						// TODO Auto-generated method stub
					}

					@Override
					public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return null;
					}
    	           
    	        };
    	 
    	        TrustManager[] tm = {xtm};
    	 
    	        SSLContext ctx = SSLContext.getInstance("TLS");
    	        ctx.init(null, tm, null);
    	 
    	        con.setSSLSocketFactory(ctx.getSocketFactory());
    	        con.setHostnameVerifier(new HostnameVerifier() {

					@Override
					public boolean verify(String var1, SSLSession var2) {
						// TODO Auto-generated method stub
						return true;
					}
    	        });
    	 
    	 
    	        InputStream inStream = (InputStream) con.getInputStream();
    	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	        byte[] buffer = new byte[1024];
    	        int len = 0;
    	        while ((len = inStream.read(buffer)) != -1) {
    	            outStream.write(buffer, 0, len);
    	        }
    	        byte[] b = outStream.toByteArray();//网页的二进制数据
    	        outStream.close();
    	        inStream.close();
    	        String rtn = new String(b, "utf-8");
    	        if (StringUtils.isNotBlank(rtn)) {
    	            JSONObject object = JSONObject.parseObject(rtn);
    	            return object;
    	        }
    	    } catch (Exception e) {
    	        ExceptionUtils.wrappBusinessException("链接接口异常:" + e.getMessage());
    	    }
    	    return null;    	

    }
}
