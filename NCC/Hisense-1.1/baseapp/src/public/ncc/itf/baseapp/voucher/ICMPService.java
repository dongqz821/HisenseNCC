package ncc.itf.baseapp.voucher;

import com.alibaba.fastjson.JSONObject;

public interface ICMPService {
	/**
	 * �ӿ�
	 * @return
	 */
	public String saveCMP(String param);//����OpenAPI ����
	
	public String writeBack2NC65(JSONObject param);//����NC65ǩ�ֻ�д�� / ֧����д
	
	public String settlementBack2NC65(JSONObject param);//����NC65�����д��
	
	public String delCMP(String param);//����OpenAPI ȡ������ ��ɾ��
	
	public JSONObject httpsSendGet (String url) throws Exception;
	
}
