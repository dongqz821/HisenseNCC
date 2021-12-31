package ncc.baseapp.background;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.uap.IUAPQueryBS;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import ncc.baseapp.utils.ConfigUtils;
import ncc.itf.baseapp.voucher.ICMPService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;


public class GetCustomerScheduledtask implements IBackgroundWorkPlugin {
	@SuppressWarnings({ "unchecked"})
	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		ICMPService service = NCLocator.getInstance().lookup(ICMPService.class);
		ConfigUtils configUtils = new ConfigUtils();
		String customerurl = configUtils.getValueFromProperties("customerurl");
//		List<Object> list = new ArrayList<>();
//		Map<String, Object> map = new HashMap<String, Object>();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式转化
		Date date1 = new Date();//开始时间
		Calendar calendars = Calendar.getInstance(); //得到日历  
		calendars.setTime(date1);//把当前时间赋给日历  
		calendars.add(Calendar.DAY_OF_MONTH, -2);  //获取前两天的时间
		Date dBefore = calendars.getTime(); //得到前两天的时间
		String str = df.format(dBefore);
		String beginTime = (String) str.toString().subSequence(0, 10);
		
		UFDate date2 = new UFDate();//结束时间
		String endTime = (String) date2.toString().subSequence(0, 10);
		
//		map.put("pageIndex", 1);
//		map.put("pageSize", 10000);
//		map.put("beginTime", strdata);
//		map.put("endTime", strdata);
//		list.add(map);
//		String param = StringUtils.join(list, ","); 	
//		param.subSequence(1, 12);//2021-12-1

		try {
			JSONObject jsons = service.sendGet(customerurl+"?"+"pageIndex=1&pageSize=1000&beginTime="+beginTime+"&endTime="+endTime);
			JSONObject data = (JSONObject) jsons.get("data");
			List<Object> pageData = (List<Object>) data.get("pageData");
			List<CustomerVO> list1 = new ArrayList<CustomerVO>();
			Map<String, Object> map = null;
			for (int i = 0; i < pageData.size(); i++) {
				map = (Map<String, Object>) pageData.get(i);
				  //根据客户中台接口的主键新增或更新供应商档案
				CustomerVO customerVO = null;
	            IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	            List<CustomerVO> list2 = (List<CustomerVO>) query.retrieveByClause(CustomerVO.class, "nvl(dr,0)=0 and taxpayerid='"+map.get("cardId")+"'");
	            if(list2!=null && list2.size()>0){
	            	customerVO = list2.get(0);
	            }
				if (customerVO == null) {
//					map.get("customerType");//客户类型;0个人 1公司
					nc.vo.bd.cust.CustomerVO  cusvo = new nc.vo.bd.cust.CustomerVO();
					cusvo.setName((String) map.get("name"));
					cusvo.setDef6((String) map.get("customerId"));//客户标识
					cusvo.setTaxpayerid((String) map.get("cardId"));//纳税人识别号
					cusvo.setPk_custclass("10011A10000000000DCL");//外部个人
					cusvo.setCode("04100000");//客户编码
					cusvo.setCreationtime(new UFDateTime());
					cusvo.setCreator("00011A10000000003ALB");//默认01	
					cusvo.setCustprop(0);//客户类型 -- 外部单位
					cusvo.setCuststate(1);//客户状态 -- 核准
					cusvo.setPk_country("0001Z010000000079UJJ");//国家/地区(待定)   中国
					cusvo.setPk_currtype("1002Z0100000000001K1"); //注册资金币种 - - 人民币
					cusvo.setPk_group("00011A10000000000MDP");
					cusvo.setPk_org("00011A10000000000MDP");
					cusvo.setEnablestate(2);
					cusvo.setPk_format("FMT0Z000000000000000");//数据格式-- 中文简体
					cusvo.setPk_timezone("0001Z010000000079U2P");//时区  北京时间(UTC+08:00)
					list1.add(cusvo);
					BaseDAO dao = new BaseDAO();
					dao.insertVOList(list1);
				}else {
					customerVO.setDef6((String) map.get("customerId"));//客户标识
					customerVO.setName((String) map.get("name"));
					customerVO.setTaxpayerid((String) map.get("cardId"));//纳税人识别号
					customerVO.setCode("04100001");//客户编码
					customerVO.setModifiedtime(new UFDateTime());//最后修改时间
		            BaseDAO dao = new BaseDAO();
		            dao.updateVO(customerVO);//更新基本信息
				}
			}
	      
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("获取接口数据异常:" + e.getMessage());
		}
		return null;
	}

}
