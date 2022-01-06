package ncc.baseapp.background;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pub.billcode.itf.IBillcodeManage;
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
import nccloud.base.collection.MapList;

public class GetRoomScheduledtask implements IBackgroundWorkPlugin {
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public PreAlertObject executeTask(BgWorkingContext context) throws BusinessException {
		
		LinkedHashMap<String, Object> keyMap = context.getKeyMap();
        String beginDate = (String)keyMap.get("beginDate");//��ȡ��ʱ������ֵ����-��ʼ����
        String endDate = (String)keyMap.get("endDate");//��ȡ��ʱ������ֵ����-��������
        String pageSize = (String)keyMap.get("pageSize");//��ȡ��ʱ������ֵ����-����
        UFDate date = new UFDate();//����ʱ��
        if(beginDate==null) {
            beginDate = (String) date.toString().subSequence(0, 10)+" 00:00:00";
        }else {
            beginDate = beginDate +" 00:00:00";
        }
        if(endDate==null) {
//          endDate = new UFDate().getDateBefore(1).toStdString().substring(0, 10);//��ȡǰһ���ʱ��
        	endDate = (String) date.toString().subSequence(0, 10)+" 23:59:59";
        }else {
            endDate = endDate +" 23:59:59";
        }
        if(pageSize==null) {
        	pageSize = "50000";
        }
        String beginTime= "";
        String endTime = "";
        try {
			 beginTime = URLEncoder.encode(beginDate, "UTF-8");
			 endTime = URLEncoder.encode(endDate, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		ICMPService service = NCLocator.getInstance().lookup(ICMPService.class);
		ConfigUtils configUtils = new ConfigUtils();
		String roomurl = configUtils.getValueFromProperties("roomurl");
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//��ʽת��
//		Date date1 = new Date();//��ʼʱ��
//		Calendar calendars = Calendar.getInstance(); //�õ�����  
//		calendars.setTime(date1);//�ѵ�ǰʱ�丳������ 
//		calendars.add(Calendar.DAY_OF_MONTH, -1);  //��ȡǰ�����ʱ��
//		Date dBefore = calendars.getTime(); //�õ�ǰ�����ʱ��
//		String str = df.format(dBefore);
//		String beginTime = (String) str.toString().subSequence(0, 10);
		
		try {
			JSONObject jsons = service.httpsSendGet(roomurl+"?"+"pageIndex=1&pageSize="+pageSize+"&beginTime="+beginTime+"&endTime="+endTime);
			JSONObject data = (JSONObject) jsons.get("data");
			List<Object> pageData = (List<Object>) data.get("pageData");
			Map<String, Object> map = null;
			List<DefdocVO> list = new ArrayList<DefdocVO>();
			
			IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			Map<String, Object> maporg = new HashMap<String, Object>();
            try {
                List<Map<String, Object>> list1 = (List) query.executeQuery("select pk_duty_org,def2 from bd_project where dr = '0' and  def2 != '~'", new MapListProcessor());
                if(list1!=null){
                	for (int i = 0; i < list1.size(); i++) {
                		maporg.put((String) list1.get(i).get("def2"), list1.get(i).get("pk_duty_org"));
					}
                }
            } catch (BusinessException e) {
                ExceptionUtils.wrappBusinessException("����sql�����쳣<��ѯ����id>��"+e.getMessage());
            }  
           
			for (int i = 0; i < pageData.size(); i++) {
				list.clear();
				map = (Map<String, Object>) pageData.get(i);
				  //���ݿͻ���̨�ӿڵ�������������¹�Ӧ�̵���
				DefdocVO defdocVO = null;
				String stageId = (String) map.get("stageId");//����ID
				if(stageId == null || "".equals(stageId)){
					continue;
				}
                
	            List<DefdocVO> list1 = (List<DefdocVO>) query.retrieveByClause(DefdocVO.class, "nvl(dr,0)=0 and def2='"+map.get("sourceId")+"'");
	            if(list1!=null && list1.size()>0){
	            	defdocVO = list1.get(0);
	            }
	            if (defdocVO == null) {
	            	nc.vo.bd.defdoc.DefdocVO  docvo = new nc.vo.bd.defdoc.DefdocVO();
	            	docvo.setDef1((String) map.get("roomId"));//����id
	            	docvo.setShortname( map.get("roomShortName")==null?"":(String) map.get("roomShortName"));//������
	            	docvo.setPk_defdoclist("10011A1000000000N6DK");//�Զ��嵵���б�����  -- ����
	            	docvo.setName(map.get("roomName")==null?"":(String) map.get("roomName"));//���Ʒ����ǵ� �����
//	            	docvo.setCode((String) map.get("roomNumber"));//����
	            	docvo.setDef2((String) map.get("sourceId"));//��Դid
	            	docvo.setCreationtime(new UFDateTime());
	            	docvo.setDataoriginflag(0);//�ֲ�ʽ
	            	docvo.setDatatype(1);//��������
	            	docvo.setEnablestate(2);//����״̬ -- ������
	            	//�ڲ�����
	            	docvo.setPk_group("00011A10000000000MDP");
	            	docvo.setPk_org(maporg.get(stageId)==null?"00011A10000000000MDP":(String) maporg.get(stageId));
	            	docvo.setCode(map.get("roomName")==null?"":(String) map.get("roomName"));
	            	list.add(docvo);
					BaseDAO dao = new BaseDAO();
					dao.insertVOList(list);
				}else {
					System.out.println("��������");
				}
			}
	
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("��ȡ�ӿ������쳣:" + e.getMessage());
		}
		return null;
	}
	
}
