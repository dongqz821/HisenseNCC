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
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//��ʽת��
		Date date1 = new Date();//��ʼʱ��
		Calendar calendars = Calendar.getInstance(); //�õ�����  
		calendars.setTime(date1);//�ѵ�ǰʱ�丳������  
		calendars.add(Calendar.DAY_OF_MONTH, -2);  //��ȡǰ�����ʱ��
		Date dBefore = calendars.getTime(); //�õ�ǰ�����ʱ��
		String str = df.format(dBefore);
		String beginTime = (String) str.toString().subSequence(0, 10);
		
		UFDate date2 = new UFDate();//����ʱ��
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
				  //���ݿͻ���̨�ӿڵ�������������¹�Ӧ�̵���
				CustomerVO customerVO = null;
	            IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	            List<CustomerVO> list2 = (List<CustomerVO>) query.retrieveByClause(CustomerVO.class, "nvl(dr,0)=0 and taxpayerid='"+map.get("cardId")+"'");
	            if(list2!=null && list2.size()>0){
	            	customerVO = list2.get(0);
	            }
				if (customerVO == null) {
//					map.get("customerType");//�ͻ�����;0���� 1��˾
					nc.vo.bd.cust.CustomerVO  cusvo = new nc.vo.bd.cust.CustomerVO();
					cusvo.setName((String) map.get("name"));
					cusvo.setDef6((String) map.get("customerId"));//�ͻ���ʶ
					cusvo.setTaxpayerid((String) map.get("cardId"));//��˰��ʶ���
					cusvo.setPk_custclass("10011A10000000000DCL");//�ⲿ����
					cusvo.setCode("04100000");//�ͻ�����
					cusvo.setCreationtime(new UFDateTime());
					cusvo.setCreator("00011A10000000003ALB");//Ĭ��01	
					cusvo.setCustprop(0);//�ͻ����� -- �ⲿ��λ
					cusvo.setCuststate(1);//�ͻ�״̬ -- ��׼
					cusvo.setPk_country("0001Z010000000079UJJ");//����/����(����)   �й�
					cusvo.setPk_currtype("1002Z0100000000001K1"); //ע���ʽ���� - - �����
					cusvo.setPk_group("00011A10000000000MDP");
					cusvo.setPk_org("00011A10000000000MDP");
					cusvo.setEnablestate(2);
					cusvo.setPk_format("FMT0Z000000000000000");//���ݸ�ʽ-- ���ļ���
					cusvo.setPk_timezone("0001Z010000000079U2P");//ʱ��  ����ʱ��(UTC+08:00)
					list1.add(cusvo);
					BaseDAO dao = new BaseDAO();
					dao.insertVOList(list1);
				}else {
					customerVO.setDef6((String) map.get("customerId"));//�ͻ���ʶ
					customerVO.setName((String) map.get("name"));
					customerVO.setTaxpayerid((String) map.get("cardId"));//��˰��ʶ���
					customerVO.setCode("04100001");//�ͻ�����
					customerVO.setModifiedtime(new UFDateTime());//����޸�ʱ��
		            BaseDAO dao = new BaseDAO();
		            dao.updateVO(customerVO);//���»�����Ϣ
				}
			}
	      
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("��ȡ�ӿ������쳣:" + e.getMessage());
		}
		return null;
	}

}
