package ncc.itf.baseapp.voucher;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSONObject;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.ISecurityTokenCallback;
import nc.ui.uif2.NCAction;
import ncc.baseapp.utils.ConfigUtils;
import uap.ws.rest.resource.AbstractUAPRestResource;

@Path(value="/dingxm/cmp/")
public class IServiceForNC65CMP extends AbstractUAPRestResource{
	
	@Path(value = "/add")
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String saveVoucher(JSONObject param) {
		
		InvocationInfoProxy.getInstance().setGroupId("00011A10000000000MDP");
		InvocationInfoProxy.getInstance().setUserId("00011A10000000003ALB");// g01----00011A10000000003ALB
		ConfigUtils configUtils = new ConfigUtils();

		InvocationInfoProxy.getInstance().setUserDataSource(configUtils.getValueFromProperties("ncds"));
		NCLocator.getInstance().lookup(ISecurityTokenCallback.class).token("NCSystem".getBytes(), "pfxx".getBytes());
		ICMPService service = NCLocator.getInstance().lookup(ICMPService.class);
		
		String result = "";
		try {
			result = service.saveCMP(param.toJSONString());
		} catch (Exception e) {
//			ExceptionUtils.wrappBusinessException("调用NccAPI接口异常:" + e.getMessage());
			return "{\"success\":\"false\",\"code\":\"200\",\"msg\":\"调用NccAPI失败"+e.getMessage()+"\"}";
		}
		
		return  result;
	}

	@Path(value = "/del")
	@POST
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String delVoucher(JSONObject param) {
		
		InvocationInfoProxy.getInstance().setGroupId("00011A10000000000MDP");
		InvocationInfoProxy.getInstance().setUserId("00011A10000000003ALB");// g01----00011A10000000003ALB
		ConfigUtils configUtils = new ConfigUtils();

		InvocationInfoProxy.getInstance().setUserDataSource(configUtils.getValueFromProperties("ncds"));
		NCLocator.getInstance().lookup(ISecurityTokenCallback.class).token("NCSystem".getBytes(), "pfxx".getBytes());
		ICMPService service = NCLocator.getInstance().lookup(ICMPService.class);
		String result = "";
		try {
			result = service.delCMP(param.toJSONString());
		} catch (Exception e) {
//			ExceptionUtils.wrappBusinessException("调用NccAPI接口异常:" + e.getMessage());
			return "{\"success\":\"false\",\"code\":\"200\",\"msg\":\"调用NccAPI失败"+e.getMessage()+"\"}";
		}
			
		return  result;
	}

	@Override
	public String getModule() {
		return "baseapp";
	}
	
}
