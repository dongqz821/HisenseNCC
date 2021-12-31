package nc.bs.obm.pub.rule;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.obm.ebankconfirmpay.AggConfirmPayHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.itf.baseapp.voucher.ICMPService;

public class CommitPayback2Nc65Rule implements IRule<AggConfirmPayHVO> {

	@Override
	public void process(AggConfirmPayHVO[] aggvos) {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		Map bank = null;
		for (int i = 0; i < aggvos.length; i++) {
			//aggvos[0].getParentVO().getAttributeValue("sourcebillpk"); -- 来源单据主键
			try {
				//根据来源单据主键查询 付款结算单 中的def20 ，pk_upbill,Bill_type
	            Object object = query.executeQuery("select def20,pk_upbill,Bill_type from cmp_paybill where dr = '0' and pk_paybill = '"+aggvos[0].getParentVO().getAttributeValue("sourcebillpk")+"'", new MapProcessor());
	            if(object!=null){
	            	bank = (Map) object;
	            }
	        } catch (BusinessException e) {
	            Logger.error(e.getMessage());
	            ExceptionUtils.wrappBusinessException("调用sql发生异常："+e.getMessage());
	        }
			
			//回写nc65支付状态
			if (!"".equals(bank.get("def20")) && bank.get("def20") != null) {
				Map<String, Object> map = new HashMap<>();
				map.put("pk_upbill", bank.get("pk_upbill"));//来源单据主键
				map.put("bill_type", bank.get("Bill_type"));//单据类型
				map.put("def20", bank.get("def20"));//来源系统
				map.put("pay", "2");
				
				String param = JSONObject.toJSONString(map);
				JSONObject json = JSONObject.parseObject(param);
				try {
					//调用nc65接口 回写签字状态
					ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
					String result = gett.writeBack2NC65(json);
//					ExceptionUtils.wrappBusinessException("NC65返回的数据为=" + result);
					JSONObject jsonsucc = JSONObject.parseObject(result);
					if (!"200".equals(jsonsucc.get("code"))) {
						ExceptionUtils.wrappBusinessException("回写65支付失败:" + jsonsucc.getString("msg"));
					}
//					ExceptionUtils.wrappBusinessException("调用65支付接口失败:" + json );
				} catch (Exception e) {
					Logger.error(e.getMessage());
//					throw new BusinessException("回写NC65单据失败，请检查！" + e.getMessage());
		            ExceptionUtils.wrappBusinessException("回写NC65单据失败，请检查!"+e.getMessage());
				}
			}
		}
		
	}
	
}
