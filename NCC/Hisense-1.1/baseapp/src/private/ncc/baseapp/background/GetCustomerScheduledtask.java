package ncc.baseapp.background;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.bd.cust.baseinfo.ICustBaseInfoService;
import nc.itf.bd.cust.baseinfo.ICustSupplierService;
import nc.itf.bd.supplier.baseinfo.ISupplierBaseInfoService;
import nc.itf.uap.IUAPQueryBS;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.vo.bd.cust.CustSupplierVO;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import ncc.baseapp.utils.ConfigUtils;
import ncc.itf.baseapp.voucher.ICMPService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;


public class GetCustomerScheduledtask implements IBackgroundWorkPlugin {
	@SuppressWarnings({ "unchecked"})
	@Override
	public PreAlertObject executeTask(BgWorkingContext ds) throws BusinessException {
 		ICMPService service = NCLocator.getInstance().lookup(ICMPService.class);
		ConfigUtils configUtils = new ConfigUtils();
		String customerurl = configUtils.getValueFromProperties("customerurl");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//��ʽת��
		Date date1 = new Date();//��ʼʱ��
		Calendar calendars = Calendar.getInstance(); //�õ�����  
		calendars.setTime(date1);//�ѵ�ǰʱ�丳������  
		calendars.add(Calendar.DAY_OF_MONTH, -1);  //��ȡǰһ���ʱ��
		Date dBefore = calendars.getTime(); //�õ�ǰ�����ʱ��
		String str = df.format(dBefore);
		String beginTime = (String) str.toString().subSequence(0, 10);
		
		UFDate date2 = new UFDate();//����ʱ��
		String endTime = (String) date2.toString().subSequence(0, 10);
		try {

			JSONObject jsons = service.httpsSendGet(customerurl+"?"+"pageIndex=1&pageSize=10000&beginTime="+beginTime+"&endTime="+endTime);
//			String jsons1 = "{\"success\":true,\"code\":200,\"data\":{\"pageIndex\":0,\"pageSize\":10,\"totalCount\":1548,\"pageData\":[{\"customerId\":\"fb71z450dba243c0a9df05ib516a2bea\",\"name\":\"��ĳĳ\",\"gender\":\"2\",\"nation\":null,\"cardType\":\"06\",\"cardId\":\"91379213567299689Z\",\"customerType\":\"0\",\"maritalStatus\":null,\"birthday\":\"1993-08-12 00:00:00\",\"age\":null,\"companyContacts\":null,\"telphoneCode\":null,\"mobile\":\"15764236994\",\"telphone\":null,\"country\":null,\"province\":null,\"city\":null,\"county\":null,\"address\":\"1\",\"email\":null,\"highestEducation\":null,\"systemSource\":\"0\",\"annualIncome\":null,\"profession\":null,\"hobby\":null,\"tagName\":null,\"createId\":\"6df98c8e64e265f31a985f43\",\"createTime\":\"2021-12-01 14:48:28\",\"updateId\":null,\"updateTime\":\"2021-12-02 00:41:27\",\"startDate\":null,\"endDate\":null,\"beginTime\":null,\"endTime\":null,\"tagInfos\":[],\"roleName\":null,\"qyzb\":null}],\"pageCount\":155,\"offset\":0,\"prePage\":1,\"nextPage\":2,\"hasPrePage\":false,\"hasNextPage\":true},\"message\":\"�ɹ�\",\"currentTime\":\"2021-12-08 17:37:57\"}";
//			JSONObject jsons = JSONObject.parseObject(jsons1);

			JSONObject data = (JSONObject) jsons.get("data");
			IBillcodeManage codeService = NCLocator.getInstance().lookup(IBillcodeManage.class);
			List<Object> pageData = (List<Object>) data.get("pageData");
			List<CustomerVO> list1 = new ArrayList<CustomerVO>();
			Map<String, Object> map = null;

			for (int i = 0; i < pageData.size(); i++) {
				list1.clear();
				map = (Map<String, Object>) pageData.get(i);
				 //���ݿͻ���̨�ӿڵ�������������¹�Ӧ�̵���
				CustomerVO customerVO = null;
	            IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	            String custype = (String)map.get("customerType");
	            if ("0".equals(custype)) {//���˿ͻ�
//	            	if (true) {
//						continue;
//					}
	            	List<CustomerVO> list2 = (List<CustomerVO>) query.retrieveByClause(CustomerVO.class, "nvl(dr,0)=0 and taxpayerid='"+map.get("cardId")+"'");
	            	if(list2!=null && list2.size()>0){
	 	            	customerVO = list2.get(0);
	 	            }
	            	if (customerVO == null) {//û�пͻ� ����
						nc.vo.bd.cust.CustomerVO  cusvo = new nc.vo.bd.cust.CustomerVO();
						cusvo.setName(map.get("name")==null?"":(String)map.get("name"));
						cusvo.setDef6(map.get("customerId")==null?"":(String)map.get("customerId"));//�ͻ���ʶ
						cusvo.setPk_custclass("10011A10000000000DCL");//�ⲿ����
						cusvo.setTaxpayerid((String)map.get("cardId"));
//						cusvo.setCode("04100000");//�ͻ�����
						cusvo.setCreationtime(new UFDateTime());
						cusvo.setCreator("00011A10000000003ALB");//Ĭ��01	
						cusvo.setCustprop(0);//�ͻ����� -- �ⲿ��λ
						cusvo.setCuststate(1);//�ͻ�״̬ -- ��׼
						cusvo.setPk_country("0001Z010000000079UJJ");//����/����(����)   �й�
						cusvo.setPk_currtype("1002Z0100000000001K1"); //ע���ʽ���� - - �����
						cusvo.setPk_group("00011A10000000000MDP");
						cusvo.setPk_org("00011A10000000000MDP");
						cusvo.setTel1((String) map.get("mobile")); // �绰
						cusvo.setEmail((String) map.get("email")); // ����
						String aBillCode = codeService.getBillCode_RequiresNew("customer", cusvo.getPk_group(), cusvo.getPk_org(), cusvo);
                        cusvo.setCode(aBillCode);
						cusvo.setEnablestate(2);
						cusvo.setPk_format("FMT0Z000000000000000");//���ݸ�ʽ-- ���ļ���
						cusvo.setPk_timezone("0001Z010000000079U2P");//ʱ��  ����ʱ��(UTC+08:00)
						list1.add(cusvo);
						BaseDAO dao = new BaseDAO();
						try {
							dao.insertVOList(list1);
						} catch (Exception e) {
							e.printStackTrace();
						}
	            	}else {//����
	            		customerVO.setDef6(map.get("customerId")==null?"":(String)map.get("customerId"));//�ͻ���ʶ
						customerVO.setName(map.get("name")==null?"":(String)map.get("name"));
						customerVO.setTel1((String) map.get("mobile")); // �绰
						customerVO.setEmail((String) map.get("email")); // ����
						customerVO.setModifiedtime(new UFDateTime());//����޸�ʱ��
						customerVO.setTs(new UFDateTime());
			            BaseDAO dao = new BaseDAO();
			            try {
							dao.updateVO(customerVO);//���»�����Ϣ
						} catch (Exception e) {
							e.printStackTrace();
						}
	            	}
	            }else if("1".equals(custype)) {//��˾�ͻ�
	            	List<CustomerVO> list3 = (List<CustomerVO>) query.retrieveByClause(CustomerVO.class, "nvl(dr,0)=0 and (taxpayerid='"+map.get("cardId")+"' or name = '"+ map.get("name") +"')");//��Ӧ��
	 				if(list3!=null && list3.size()>0){
	 					customerVO = list3.get(0);
	 	            }
	            	if (customerVO == null) {//������Ӧ��
	            		customerVO = new CustomerVO();
	            		customerVO.setIsvat(UFBoolean.FALSE);
	        			customerVO.setFrozenflag(UFBoolean.FALSE);
	        			customerVO.setIssupplier(UFBoolean.TRUE);
	        			customerVO.setTaxpayerid(map.get("cardId")==null?"":(String)map.get("cardId"));
	        			customerVO.setIsfreecust(UFBoolean.FALSE);
	        			customerVO.setPk_custclass("10011A10000000000DCK");//�����ⲿ��λ
	        			customerVO.setPk_group("00011A10000000000MDP");
	        			customerVO.setEnablestate(2);
	        			customerVO.setCustprop(0);
	        			customerVO.setDataoriginflag(0);
	        			customerVO.setPk_org("00011A10000000000MDP");//����
	        			customerVO.setName((String) map.get("name"));
	        			customerVO.setShortname((String) map.get("name"));
	        			customerVO.setPk_format("FMT0Z000000000000000");
	        			customerVO.setPk_country((String) map.get("pk_country"));
	        			customerVO.setPk_supplier((String) map.get("pk_supplier"));
	        			customerVO.setMnecode((String) map.get("mnecode"));
	        			customerVO.setCuststate(1);
	        			customerVO.setPk_timezone((String) map.get("pk_timezone"));
	        			customerVO.setPk_customer((String) map.get("pk_supplier"));
	        			customerVO.setDef6((String) map.get("def5"));
	        			customerVO.setTel1((String) map.get("mobile")); // �绰
	        			customerVO.setEmail((String) map.get("email")); // ����
	        			customerVO.setTs(new UFDateTime());
	        			customerVO.setPk_country("0001Z010000000079UJJ");//����/����(����)   �й�
	        			customerVO.setPk_currtype("1002Z0100000000001K1");//ע���ʽ���� - - �����
	        			customerVO.setPk_timezone("0001Z010000000079U2P"); // ʱ����ʽ
	        			customerVO.setPk_format("FMT0Z000000000000000"); //���ݸ�ʽ
	        			customerVO.setDef6(map.get("customerId")==null?"":(String)map.get("customerId"));//�ͻ���ʶ
	        			customerVO.setCreator("00011A10000000003ALB");
	        			String aBillCode = codeService.getBillCode_RequiresNew("customer", customerVO.getPk_group(), customerVO.getPk_org(), customerVO);
	        			customerVO.setCode(aBillCode);
	    				//����ͻ�
	    				try {
	    					customerVO.setDef2("10011A1000000009KOX0");//�Ƿ�Ͷ�ʵ�λ��Ĭ�Ϸ�
	    					customerVO.setDef4("4");//֤�����ͣ�Ĭ������
	    					customerVO.setStatus(VOStatus.NEW);
	    					BaseDAO dao = new BaseDAO();
	    					dao.insertVO(customerVO);
						} catch (Exception e) { 
							ExceptionUtils.wrappBusinessException("�����쳣:" + e.getMessage());
						}
	            	}else {//����
	            		System.out.println("��������");
	            	}
	            }
			}
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("��ȡ�ӿ������쳣:" + e.getMessage());
		}
		return null;
	}

}
