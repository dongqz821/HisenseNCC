package ncc.itf.baseapp.sell;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSONObject;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import ncc.baseapp.utils.ConfigUtils;

@Path(value="/manage/sell")
public class IServiceSell {
	@Path(value = "/save")
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String saveVoucher(JSONObject param) {
		
		InvocationInfoProxy.getInstance().setGroupId("00011A10000000000MDP");
		InvocationInfoProxy.getInstance().setUserId("00011A10000000003ALB");// g01----00011A10000000003ALB
		ConfigUtils configUtils = new ConfigUtils();

		InvocationInfoProxy.getInstance().setUserDataSource(configUtils.getValueFromProperties("ncds"));
		NCLocator.getInstance().lookup(ISecurityTokenCallback.class).token("NCSystem".getBytes(), "pfxx".getBytes());
		IServiceSellHandler service = NCLocator.getInstance().lookup(IServiceSellHandler.class);
		
		String result = "";
		try {
			result = service.execute(param);
		} catch (Exception e) {
			return "{\"success\":\"false\",\"code\":\"300\",\"msg\":\"µ÷ÓÃÊ§°Ü"+e.getMessage()+"\"}";
		}
		
		return  result;
	}
}
