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
			//aggvos[0].getParentVO().getAttributeValue("sourcebillpk"); -- ��Դ��������
			try {
				//������Դ����������ѯ ������㵥 �е�def20 ��pk_upbill,Bill_type
	            Object object = query.executeQuery("select def20,pk_upbill,Bill_type from cmp_paybill where dr = '0' and pk_paybill = '"+aggvos[0].getParentVO().getAttributeValue("sourcebillpk")+"'", new MapProcessor());
	            if(object!=null){
	            	bank = (Map) object;
	            }
	        } catch (BusinessException e) {
	            Logger.error(e.getMessage());
	            ExceptionUtils.wrappBusinessException("����sql�����쳣��"+e.getMessage());
	        }
			
			//��дnc65֧��״̬
			if (!"".equals(bank.get("def20")) && bank.get("def20") != null) {
				Map<String, Object> map = new HashMap<>();
				map.put("pk_upbill", bank.get("pk_upbill"));//��Դ��������
				map.put("bill_type", bank.get("Bill_type"));//��������
				map.put("def20", bank.get("def20"));//��Դϵͳ
				map.put("pay", "2");
				
				String param = JSONObject.toJSONString(map);
				JSONObject json = JSONObject.parseObject(param);
				try {
					//����nc65�ӿ� ��дǩ��״̬
					ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
					String result = gett.writeBack2NC65(json);
//					ExceptionUtils.wrappBusinessException("NC65���ص�����Ϊ=" + result);
					JSONObject jsonsucc = JSONObject.parseObject(result);
					if (!"200".equals(jsonsucc.get("code"))) {
						ExceptionUtils.wrappBusinessException("��д65֧��ʧ��:" + jsonsucc.getString("msg"));
					}
//					ExceptionUtils.wrappBusinessException("����65֧���ӿ�ʧ��:" + json );
				} catch (Exception e) {
					Logger.error(e.getMessage());
//					throw new BusinessException("��дNC65����ʧ�ܣ����飡" + e.getMessage());
		            ExceptionUtils.wrappBusinessException("��дNC65����ʧ�ܣ�����!"+e.getMessage());
				}
			}
		}
		
	}
	
}
