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
        String beginDate = (String)keyMap.get("beginDate");//获取定时任务阈值参数-开始日期
        String endDate = (String)keyMap.get("endDate");//获取定时任务阈值参数-截至日期
        String pageSize = (String)keyMap.get("pageSize");//获取定时任务阈值参数-条数
        UFDate date = new UFDate();//结束时间
        if(beginDate==null) {
            beginDate = (String) date.toString().subSequence(0, 10)+" 00:00:00";
        }else {
            beginDate = beginDate +" 00:00:00";
        }
        if(endDate==null) {
//          endDate = new UFDate().getDateBefore(1).toStdString().substring(0, 10);//获取前一天的时间
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
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式转化
//		Date date1 = new Date();//开始时间
//		Calendar calendars = Calendar.getInstance(); //得到日历  
//		calendars.setTime(date1);//把当前时间赋给日历 
//		calendars.add(Calendar.DAY_OF_MONTH, -1);  //获取前两天的时间
//		Date dBefore = calendars.getTime(); //得到前两天的时间
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
                ExceptionUtils.wrappBusinessException("调用sql发生异常<查询分期id>："+e.getMessage());
            }  
           
			for (int i = 0; i < pageData.size(); i++) {
				list.clear();
				map = (Map<String, Object>) pageData.get(i);
				  //根据客户中台接口的主键新增或更新供应商档案
				DefdocVO defdocVO = null;
				String stageId = (String) map.get("stageId");//分期ID
				if(stageId == null || "".equals(stageId)){
					continue;
				}
                
	            List<DefdocVO> list1 = (List<DefdocVO>) query.retrieveByClause(DefdocVO.class, "nvl(dr,0)=0 and def2='"+map.get("sourceId")+"'");
	            if(list1!=null && list1.size()>0){
	            	defdocVO = list1.get(0);
	            }
	            if (defdocVO == null) {
	            	nc.vo.bd.defdoc.DefdocVO  docvo = new nc.vo.bd.defdoc.DefdocVO();
	            	docvo.setDef1((String) map.get("roomId"));//房间id
	            	docvo.setShortname( map.get("roomShortName")==null?"":(String) map.get("roomShortName"));//房间简称
	            	docvo.setPk_defdoclist("10011A1000000000N6DK");//自定义档案列表主键  -- 房号
	            	docvo.setName(map.get("roomName")==null?"":(String) map.get("roomName"));//名称放他们的 房间号
//	            	docvo.setCode((String) map.get("roomNumber"));//房号
	            	docvo.setDef2((String) map.get("sourceId"));//明源id
	            	docvo.setCreationtime(new UFDateTime());
	            	docvo.setDataoriginflag(0);//分布式
	            	docvo.setDatatype(1);//数据类型
	            	docvo.setEnablestate(2);//启用状态 -- 已起用
	            	//内部编码
	            	docvo.setPk_group("00011A10000000000MDP");
	            	docvo.setPk_org(maporg.get(stageId)==null?"00011A10000000000MDP":(String) maporg.get(stageId));
	            	docvo.setCode(map.get("roomName")==null?"":(String) map.get("roomName"));
	            	list.add(docvo);
					BaseDAO dao = new BaseDAO();
					dao.insertVOList(list);
				}else {
					System.out.println("不做处理");
				}
			}
	
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("获取接口数据异常:" + e.getMessage());
		}
		return null;
	}
	
}
