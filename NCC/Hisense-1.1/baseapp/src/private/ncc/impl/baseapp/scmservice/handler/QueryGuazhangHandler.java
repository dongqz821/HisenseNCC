package ncc.impl.baseapp.scmservice.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.oraclesql.OracleSqlIn;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.itf.baseapp.sell.IServiceSellHandler;

public class QueryGuazhangHandler implements IServiceSellHandler{
	/*请求参数格式
	 * { "busitype": "SCMGZ", "qryparam": [{ "pk_projectconts":
	 * "10011A10000000000DFR-10011A10000000009MCH" }, { "pk_projectconts":
	 * "10011A10000000000DFY-10011A100000000BT5ZJ" } ] }
	 */
	@SuppressWarnings({ "unchecked" })
	public String execute(JSONObject param) {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String busitype = param.getString("busitype");
		Map<String, Object> map = new HashMap<String, Object>();
		JSONArray pk_project =  param.getJSONArray("pk_project");
		JSONArray pk_conts = param.getJSONArray("pk_conts");
//		for (int i = 0; i < qryparam.size(); i++) {
//			map = (Map<String, Object>) qryparam.get(i);
//			map.put("pk_project", map.get("pk_projectconts").toString().substring(0,19));//项目pk
//			map.put("pk_conts", map.get("pk_projectconts").toString().substring(21,40));//合同pk
//		}
		String cond1 = "";
		String cond2 = "";
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		for (int i = 0; i < pk_project.size(); i++) {
			list1.add(pk_project.get(i).toString());
		}
		for (int i = 0; i < pk_conts.size(); i++) {
			list2.add(pk_conts.get(i).toString());
		}
		cond1 = OracleSqlIn.getOracleSQLIn(list1, list1.size(), "b.cprojectid");
		cond2 = OracleSqlIn.getOracleSQLIn(list2, list2.size(), "a.vdef20");
		Map<String, Object> mapic = new HashMap<String, Object>();
        try {
            List<Map<String, Object>> list3 = (List<Map<String, Object>>) query.executeQuery("SELECT substr(a.taudittime,0,10) signdate,a.vdef20 pk_cont,b.cprojectid pk_project,SUM(b.ncaltaxmny) totalmny FROM ic_saleout_h a inner join ic_saleout_b b on a.cgeneralhid = b.cgeneralhid "
            		+ "where a.dr = 0 and b.dr = 0 and a.fbillflag = 3 and ("+cond1+") and ("+cond2+") GROUP BY substr(a.taudittime,0,10),a.vdef20,b.cprojectid", new MapListProcessor());
            if(list1!=null){
            	mapic.put("data", list3);
            }
        } catch (BusinessException e) {
            ExceptionUtils.wrappBusinessException("调用sql发生异常<销售出库>："+e.getMessage());
        }  
		
		mapic.put("success","true");
		mapic.put("code", "200");
		mapic.put("msg", "调用成功!");
		return JSONObject.toJSONString(mapic);
	}

}
