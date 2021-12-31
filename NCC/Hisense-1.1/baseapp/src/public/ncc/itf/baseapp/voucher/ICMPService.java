package ncc.itf.baseapp.voucher;

import com.alibaba.fastjson.JSONObject;

public interface ICMPService {
	/**
	 * 接口
	 * @return
	 */
	public String saveCMP(String param);//调用OpenAPI 新增
	
	public String writeBack2NC65(JSONObject param);//调用NC65签字回写的 / 支付回写
	
	public String settlementBack2NC65(JSONObject param);//调用NC65结算回写的
	
	public String delCMP(String param);//调用OpenAPI 取消审批 和删除
	
	public JSONObject httpsSendGet (String url) throws Exception;
	
}
