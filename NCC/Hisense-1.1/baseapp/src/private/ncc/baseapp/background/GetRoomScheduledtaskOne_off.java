package ncc.baseapp.background;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import nc.jdbc.framework.processor.MapProcessor;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.uap.IUAPQueryBS;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.baseapp.utils.ConfigUtils;
import ncc.itf.baseapp.voucher.ICMPService;

public class GetRoomScheduledtaskOne_off implements IBackgroundWorkPlugin {
	@SuppressWarnings("unchecked")
	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		ICMPService service = NCLocator.getInstance().lookup(ICMPService.class);
		ConfigUtils configUtils = new ConfigUtils();
		String roomurl = configUtils.getValueFromProperties("roomurl");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//��ʽת��
		Date date1 = new Date();//��ʼʱ��
		Calendar calendars = Calendar.getInstance(); //�õ�����  
		calendars.setTime(date1);//�ѵ�ǰʱ�丳������ 
		calendars.add(Calendar.DAY_OF_MONTH, -1);  //��ȡǰ�����ʱ��
		Date dBefore = calendars.getTime(); //�õ�ǰ�����ʱ��
		String str = df.format(dBefore);
		String beginTime = (String) str.toString().subSequence(0, 10);
		
		UFDate date2 = new UFDate();//����ʱ��
		String endTime = (String) date2.toString().subSequence(0, 10);
		
		try {
			JSONObject jsons = service.httpsSendGet(roomurl+"?"+"pageIndex=1&pageSize=10000&beginTime="+"2021-01-1"+"&endTime="+endTime);
			JSONObject data = (JSONObject) jsons.get("data");
			List<Object> pageData = (List<Object>) data.get("pageData");
			Map<String, Object> map = null;
			List<DefdocVO> list = new ArrayList<DefdocVO>();
			for (int i = 0; i < pageData.size(); i++) {
				map = (Map<String, Object>) pageData.get(i);
				  //���ݿͻ���̨�ӿڵ�������������¹�Ӧ�̵���
				DefdocVO defdocVO = null;
				String stageId = (String) map.get("stageId");//����ID
				if(stageId == null || "".equals(stageId)){
					continue;
				}
				IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
				Map bank = null;
                try {
                    Object object = query.executeQuery("select pk_duty_org from bd_project where def2 = '"+stageId+"'", new MapProcessor());
                    if(object!=null){
                    	bank = (Map) object;
                    }
                } catch (BusinessException e) {
                    ExceptionUtils.wrappBusinessException("����sql�����쳣��"+e.getMessage());
                }     
                
	            List<DefdocVO> list1 = (List<DefdocVO>) query.retrieveByClause(DefdocVO.class, "nvl(dr,0)=0 and def2='"+map.get("sourceId")+"'");
	            if(list1!=null && list1.size()>0){
	            	defdocVO = list1.get(0);
	            }
	            if (defdocVO == null) {
	            	nc.vo.bd.defdoc.DefdocVO  docvo = new nc.vo.bd.defdoc.DefdocVO();
	            	docvo.setDef1((String) map.get("roomId"));//����id
	            	docvo.setShortname((String) map.get("roomShortName"));//������
	            	docvo.setPk_defdoclist("10011A1000000000N6DK");//�Զ��嵵���б�����  -- ����
	            	docvo.setName((String) map.get("roomNumber"));//���Ʒ����ǵ� �����
	            	docvo.setCode((String) map.get("roomNumber"));//����
	            	docvo.setDef2((String) map.get("sourceId"));//��Դid
	            	docvo.setCreationtime(new UFDateTime());
	            	docvo.setDataoriginflag(0);//�ֲ�ʽ
	            	docvo.setDatatype(1);//��������
	            	docvo.setEnablestate(2);//����״̬ -- ������
	            	//�ڲ�����
	            	docvo.setPk_group("00011A10000000000MDP");
	            	docvo.setPk_org((String) bank.get("pk_duty_org"));
	            	list.add(docvo);
					BaseDAO dao = new BaseDAO();
					dao.insertVOList(list);
				}else {
					System.out.println("��");
				}
			}
	
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("��ȡ�ӿ������쳣:" + e.getMessage());
		}
		return null;
	}
	
}
