package ncc.baseapp.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.impl.baseapp.voucher.CMPServiceImpl;
import ncc.itf.baseapp.voucher.ICMPService;
import nccloud.open.api.auto.token.cur.utils.APICurUtils;
import nccloud.open.api.auto.token.itf.IAPIUtils;

public class OpenAPIUtis {
	public String callAPI(JSONObject json) {
		IAPIUtils util = null;
		try {
			util = new APICurUtils();
		} catch (Exception e) {
			return "{\"code\":\"000\",\"msg\":\"new APICurUtils()调用失败"+e.getMessage()+"\"}";
		}
		//10.19.65.17:80
	//	util.init("127.0.0.1", "8080", "01", "nc65", "13de0c14221d49cd8040", 
	//			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlYpQdW80WF1vTIyU1dFLp+uZivoHun2MmeKUXeLrYveM24bT6Wn2Rq460tY8lpUfdVu8CWDupHfKmoMvvSsdstRwZiFZeFEpy2x3kphUH2D6bt5Bne264k6RVd/pF/EX4tg/FMzLH+8lwgVbA90k5YP0TFyWKci1gHbOEBsdx7uJc3bZw67aoEZGVERnDVU9uCwcv1HLwziPRHkFoIzvQGSFb4DIscFvnub5LjziOc0JL0KpPnRmtC3OFoz6g1PkEak2bhjOA08Q8shbycikwz0okf5Lzit7f3Iui7zLpj+Ua8dE+CBzS+9dWdhZxVeCpkrQN5vsnqWdTZSdGXGjMQIDAQAB"
	//			, "liuxiaolei", "1qaz@WSX");
		ConfigUtils configUtils = new ConfigUtils();

		String nccip = configUtils.getValueFromProperties("nccip");//design
		String port = configUtils.getValueFromProperties("port");
		String biz_center = configUtils.getValueFromProperties("biz_center");
		String client_id = configUtils.getValueFromProperties("client_id");
		String client_secret = configUtils.getValueFromProperties("client_secret");
		String pubKey = configUtils.getValueFromProperties("pubKey");
		String username = configUtils.getValueFromProperties("username");
		String password = configUtils.getValueFromProperties("password");
		
		util.init(nccip, port, biz_center, client_id, client_secret, pubKey, username, password);
		
		String result  = "";
		try {
			String token = util.getToken(); 
			
			util.setApiUrl("nccloud/api/cmp/paybillAdd/addBill");
			result = util.getAPIRetrun(token, json.toJSONString());

			
			JSONObject jsonsucc = JSONObject.parseObject(result);
			JSONObject head = json.getJSONObject("head");
			if (jsonsucc.getBooleanValue("success")) {//成功后调用审批接口	
				JSONObject data = jsonsucc.getJSONObject("data");
				JSONObject jsonjk = new JSONObject();
				
				jsonjk.put("bill_no", data.getString("bill_no"));
				jsonjk.put("pk_approver", "1002AA10000000003ER1");
				jsonjk.put("pk_org", head.getString("pk_org"));
				jsonjk.put("checknote", "同意");
				token = util.getToken();
				util.setApiUrl("nccloud/api/cmp/cmpBillAudit/payBill");
				result = util.getAPIRetrun(token, jsonjk.toJSONString());
				
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
            ExceptionUtils.wrappBusinessException(" 调用API失败："+e.getMessage());
		}
	
		return result;
	}
	
	public String delAPI(JSONObject json) {
		
		String pk_upbill = json.getString("pk_pk");//pk_upbill 获取上游单据主键
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject param = new JSONObject();
		try {
			String object = (String) query.executeQuery("select bill_no, pk_paybill from cmp_paybill  where nvl(dr,0)=0 and pk_upbill ='" + pk_upbill + "'", new ColumnProcessor());
			if (object != null) {
				param.put("bill_no", object);
			}
//			Object[] object = (Object[]) query.executeQuery("select bill_no, pk_paybill from cmp_paybill  where nvl(dr,0)=0 and pk_upbill ='" + pk_upbill + "'", new ArrayProcessor());
//			if (object != null) {			
//				param.put("bill_no", object[0]);
//				param.put("pk_paybill", object[1]);
//			}
			param.putAll(json);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
            ExceptionUtils.wrappBusinessException("调用sql发生异常："+e.getMessage());
		}
		
		IAPIUtils util = null;
		try {
			util = new APICurUtils();
		} catch (Exception e) {
			return "{\"code\":\"000\",\"msg\":\"new APICurUtils()调用失败"+e.getMessage()+"\"}";
		}

		ConfigUtils configUtils = new ConfigUtils();

		String nccip = configUtils.getValueFromProperties("nccip");//design
		String port = configUtils.getValueFromProperties("port");
		String biz_center = configUtils.getValueFromProperties("biz_center");
		String client_id = configUtils.getValueFromProperties("client_id");
		String client_secret = configUtils.getValueFromProperties("client_secret");
		String pubKey = configUtils.getValueFromProperties("pubKey");
		String username = configUtils.getValueFromProperties("username");
		String password = configUtils.getValueFromProperties("password");
		
		util.init(nccip, port, biz_center, client_id, client_secret, pubKey, username, password);
		
		String result  = "";
		try {
			
			String token = util.getToken(); 
			util.setApiUrl("nccloud/api/cmp/cmpBillUnAudit/unPayBill");
			result = util.getAPIRetrun(token, param.toJSONString());

			JSONObject jsonsucc = JSONObject.parseObject(result);
			if (jsonsucc.getBooleanValue("success")) {//成功后调用审批接口	
				JSONObject data = jsonsucc.getJSONObject("data");
				JSONObject jsonjk = new JSONObject();
				
				String user = InvocationInfoProxy.getInstance().getUserId();// g01----00011A10000000003ALB 设置当前操作用户
				
				jsonjk.put("bill_no", param.get("bill_no"));
				jsonjk.put("pk_org", data.getString("pk_org"));
				jsonjk.put("pk_user", user);
				token = util.getToken();
				util.setApiUrl("nccloud/api/cmp/delbill/paybill");
				result = util.getAPIRetrun(token, jsonjk.toJSONString());
			}
					
		} catch (Exception e) {
			Logger.error(e.getMessage());
            ExceptionUtils.wrappBusinessException(" 调用API失败："+e.getMessage());
		}
	
		return result;
	}

}
