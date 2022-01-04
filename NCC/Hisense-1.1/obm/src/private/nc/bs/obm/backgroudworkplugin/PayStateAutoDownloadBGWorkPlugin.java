package nc.bs.obm.backgroudworkplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.alibaba.fastjson.JSONObject;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.naming.Context;
import nc.bs.logging.Logger;
import nc.bs.obm.config.BankTypeConfiguration;
import nc.bs.obm.config.ObmConfigTool;
import nc.bs.obm.pubface.ELogUtil;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.pa.html.IAlertMessage;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.bs.trade.business.HYPubBO;
import nc.itf.obm.ebanklog.IEbankLogQueryService;
import nc.itf.obm.ebankpaylog.IBusilogService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.obm.BillObmStateVO;
import nc.vo.obm.config.BankTypeConfigVO;
import nc.vo.obm.ebankautodownlog.EbankAutoDownLogVO;
import nc.vo.obm.ebankdownload.EbankDownLoadAggVO;
import nc.vo.obm.ebankdownload.EbankDownLoadVO;
import nc.vo.obm.ebankpaylog.EBankPayLogAggVO;
import nc.vo.obm.ebankpaylog.EBankPayLogHVO;
import nc.vo.obm.ebankpaylog.EBankPayLogVO;
import nc.vo.obm.log.ObmLog;
import nc.vo.obm.ml.MLObm;
import nc.vo.obm.obmvo.PayStateQueryVO;
import nc.vo.obm.pa.PayStateAlertMsg;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.tmpub.util.ArrayUtil;
import nc.vo.tmpub.util.SqlUtil;
import nc.vo.tmpub.util.StringUtil;
import ncc.itf.baseapp.voucher.ICMPService;

public class PayStateAutoDownloadBGWorkPlugin
  implements IBackgroundWorkPlugin
{
  private static Context getLocator()
  {
    return NCLocator.getInstance();
  }
  
  public PreAlertObject executeTask(BgWorkingContext context)
    throws BusinessException
  {
    PreAlertObject pao = new PreAlertObject();
    String userid = context.getPk_user();
    if (StringUtil.isNull(userid)) {
      userid = "alert_user";
    }
    String pk_org = String.valueOf(context.getKeyMap().get("pk_org"));
    EbankDownLoadVO[] cvo = getDwonLoadVO(context);
    IAlertMessage remsg = null;
    int msgType = 1;
    String errmsg = "";
    if ((cvo != null) && (cvo.length > 0))
    {
      EBankPayLogVO[] logs = getLogs(cvo);
      if ((logs != null) && (logs.length > 0))
      {
        try
        {
          remsg = downloadPayState(logs, userid, pk_org);
        }
        catch (Exception e)
        {
          ObmLog.error("预警平台自动下载支付状态时出现错误：" + e.getMessage(), e, getClass(), "download");
          msgType = 3;
          errmsg = e.getMessage();
        }
      }
      else
      {
        msgType = 2;
        errmsg = MLObm.getStr00252();
      }
    }
    else
    {
      msgType = 2;
      errmsg = MLObm.getStr00253();
    }
    if (msgType != 1)
    {
      EbankAutoDownLogVO[] logs = saveErrorLogs(pk_org, errmsg);
      remsg = getMessage(msgType, null, errmsg);
    }
    pao.setReturnType(PreAlertReturnType.RETURNFORMATMSG);
    pao.setReturnObj(remsg);
    pao.setMsgTitle(MLObm.getStr00920());
    if (msgType == 3) {
      throw new BusinessException(errmsg);
    }
    return pao;
  }
  
  private IAlertMessage getMessage(int msgType, EbankAutoDownLogVO[] logvo, String errmsg)
  {
    PayStateAlertMsg retmsg = new PayStateAlertMsg();
    Object[][] bodyValue = (Object[][])null;
    switch (msgType)
    {
    case 0: 
      retmsg.setMsgType(1);
      break;
    case 1: 
      retmsg.setMsgType(0);
      break;
    case 2: 
      retmsg.setMsgType(1);
      break;
    case 3: 
      retmsg.setMsgType(1);
      break;
    }
    if ((logvo != null) && (logvo.length > 0))
    {
      bodyValue = new Object[logvo.length][];
      for (int i = 0; i < logvo.length; i++) {
        bodyValue[i] = new Object[]{logvo[i].getAttributeValue("unitname"), logvo[i].getAttributeValue("curacc"), logvo[i].getAttributeValue("bankname"), logvo[i].getAttributeValue("down_date"), logvo[i].getAttributeValue("down_time"), logvo[i].getMsg() };
      }
    }
    else
    {
      bodyValue = new String[][] { { errmsg } };
    }
    retmsg.setBodyValue(bodyValue);
    return retmsg;
  }
  
  private EbankDownLoadVO[] getDwonLoadVO(BgWorkingContext context)
    throws MetaDataException
  {
    LinkedHashMap<String, Object> keyMap = new LinkedHashMap();
    keyMap = context.getKeyMap();
    String pk_org = String.valueOf(keyMap.get("pk_org"));
    String inwhere = "";
    String pk_funtype = (String)keyMap.get("funtype");
    String whereSql = " pk_org='" + pk_org + "' ";
    if (StringUtil.isNull(pk_funtype))
    {
      whereSql = whereSql + " and deffuntype='Y'";
    }
    else
    {
      inwhere = SqlUtil.buildSqlForIn("pk_ebank_download_h", pk_funtype.split(","));
      whereSql = whereSql + " and " + inwhere;
    }
    NCObject[] nvo = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(EbankDownLoadAggVO.class, whereSql, false);
    EbankDownLoadAggVO aggvo = new EbankDownLoadAggVO();
    if (nvo != null) {
      aggvo = (EbankDownLoadAggVO)nvo[0].getContainmentObject();
    }
    EbankDownLoadVO[] cvo = (EbankDownLoadVO[])aggvo.getChildrenVO();
    UFBoolean issupport = null;
    if (cvo != null)
    {
      for (int i = 0; i < cvo.length; i++)
      {
        issupport = cvo[i].getIspaystatedownload();
        if ((issupport == null) || (!issupport.booleanValue())) {
          cvo[i] = null;
        }
      }
      cvo = (EbankDownLoadVO[])ArrayUtil.shrinkArray(cvo);
    }
    return cvo;
  }
  
  private EBankPayLogVO[] getLogs(EbankDownLoadVO[] loadsetvos)
  {
    Set<String> accountsets = ELogUtil.getPropValueSet(loadsetvos, "bankaccbas");
    if ((accountsets == null) || (accountsets.size() == 0))
    {
      ObmLog.error("自动下载指令状态，查询日志时出现错误,下载设置记录中没有任何可用账号！", getClass(), "getLogs");
      return null;
    }
    String accSql = SqlUtil.buildSqlForIn("dbtacc", (String[])accountsets.toArray(new String[0]));
    StringBuffer wheresql = new StringBuffer(accSql);
    wheresql.append(" and paystate=2 and useflag = 0 and dr=0 ");
    Collection<EBankPayLogVO> needdownvos = new ArrayList();
    String msg = "";
    try
    {
      IEbankLogQueryService logquerysev = (IEbankLogQueryService)getLocator().lookup(IEbankLogQueryService.class.getName());
      EBankPayLogVO[] alllogs = logquerysev.queryPayLogByCondition(wheresql.toString());
      int all = alllogs == null ? 0 : alllogs.length;
      if (all == 0) {
        throw new BusinessException(MLObm.getStr00256() + wheresql.toString());
      }
      int notinsetnum = 0;
      int notindaysnum = 0;
      for (EBankPayLogVO logvo : alllogs) {
        if (!isLogInDownloadSet(logvo, loadsetvos)) {
          notinsetnum++;
        } else if (!isLogInSetDays(logvo)) {
          notindaysnum++;
        } else {
          needdownvos.add(logvo);
        }
      }
      msg = MLObm.getStr00257();
      msg = ELogUtil.msg(msg, new String[] { String.valueOf(all), String.valueOf(notinsetnum), 
        String.valueOf(notindaysnum), String.valueOf(needdownvos.size()) });
      ObmLog.debug("自动下载日志：" + msg, getClass(), "getLogs");
    }
    catch (BusinessException e)
    {
      ObmLog.error("自动下载指令状态，查询指令时出现错误:" + e.getMessage(), e, getClass(), "getLogs");
    }
    return (EBankPayLogVO[])needdownvos.toArray(new EBankPayLogVO[0]);
  }
  
  private boolean isLogInDownloadSet(EBankPayLogVO log, EbankDownLoadVO[] loadsetvos)
  {
    if ((loadsetvos == null) || (log == null)) {
      return false;
    }
    boolean resutl = false;
    for (EbankDownLoadVO vo : loadsetvos)
    {
      resutl = (vo.getBanktype() != null) && (vo.getBanktype().equals(log.getBanktype()));
      resutl = (resutl) && (vo.getBankaccbas() != null) && (vo.getBankaccbas().equals(log.getDbtacc()));
      resutl = (resutl) && (vo.getPk_currtype() != null) && (vo.getPk_currtype().equals(log.getC_ccynbr()));
      if (resutl) {
        break;
      }
    }
    return resutl;
  }
  
  private boolean isLogInSetDays(EBankPayLogVO log)
  {
    if (log == null) {
      return false;
    }
    String netbankinftpcode = log.getBanktypecode();
    BankTypeConfigVO bankconf = ObmConfigTool.bankTypeConfig().getBankTypeConfig(netbankinftpcode);
    if (bankconf == null) {
      ObmLog.error("预警平台自动下载支付状态支付指令[" + log.getYurref() + "]的银行[" + netbankinftpcode + "]不在banktype.xml中设置", getClass(), "isLogInSetDays");
    }
    UFDate nowDate = new UFDate(false);
    UFDate logdate = log.getSenddate();
    if ((logdate == null) || (bankconf == null)) {
      return false;
    }
    int zfcxdays = 5;
    if (bankconf.getAttributeValue("zfcxdays") != null) {
      zfcxdays = bankconf.getZfcxdays() < 0 ? 0 : bankconf.getZfcxdays();
    }
    UFDate mindate = nowDate.getDateBefore(zfcxdays);
    return logdate.after(mindate);
  }
  
  private IAlertMessage downloadPayState(EBankPayLogVO[] logs, String userid, String corpPk)
    throws BusinessException
  {
    Map<String, EBankPayLogVO> logmap = ELogUtil.arrayToMapYurref(logs);
    PayStateQueryVO queryparamvo = new PayStateQueryVO();
    queryparamvo.setPk_corp(corpPk);
    queryparamvo.setUseid(userid);
    queryparamvo.setYurrefs((String[])logmap.keySet().toArray(new String[0]));
    queryparamvo.setSrcsystem("ALERT");
    IAlertMessage remsg = null;
    

    IBusilogService busilogsev = (IBusilogService)NCLocator.getInstance().lookup(IBusilogService.class);
    int len1 = logs.length;
    PayStateQueryVO[] statequeryvos = new PayStateQueryVO[len1];
    EBankPayLogAggVO[] aggvos = new EBankPayLogAggVO[len1];
    HYPubBO hy = new HYPubBO();
    for (int i = 0; i < len1; i++)
    {
      EBankPayLogAggVO aggvo = new EBankPayLogAggVO();
      EBankPayLogHVO hvo = (EBankPayLogHVO)hy.queryByPrimaryKey(EBankPayLogHVO.class, logs[i].getPk_ebank_paylog_h());
      EBankPayLogVO[] bvos = new EBankPayLogVO[1];
      bvos[0] = logs[i];
      aggvo.setParentVO(hvo);
      aggvo.setChildrenVO(bvos);
      aggvos[i] = aggvo;
      EBankPayLogHVO headvo = (EBankPayLogHVO)aggvo.getParent();
      PayStateQueryVO statequery = new PayStateQueryVO();
      statequery.setPayFunc("zfcx");
      statequery.setUseid(userid);
      statequery.setPk_corp(headvo.getPk_org());
      statequery.setSrcsystem(headvo.getSrcsystem());
      statequery.setSrcbilltype(headvo.getSrcbilltype());
      statequery.setSrcbillcode(headvo.getSrcbillcode());
      EBankPayLogVO[] logvos = (EBankPayLogVO[])aggvo.getChildrenVO();
      Collection<String> yurrefs = new Vector();
      for (EBankPayLogVO vo : logvos) {
        if (vo.getYurref() != null) {
          yurrefs.add(vo.getYurref());
        }
      }
      statequery.setYurrefs((String[])yurrefs.toArray(new String[0]));
      statequeryvos[i] = statequery;
    }
    try
    {
      BillObmStateVO[] psvo = busilogsev.queryPayStatePL(statequeryvos, aggvos);
      
      for (int i = 0; i < psvo.length; i++) {
    	  EBankPayLogHVO hvo = (EBankPayLogHVO)hy.queryByPrimaryKey(EBankPayLogHVO.class, logs[i].getPk_ebank_paylog_h());
    	  IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
  		  Map bank = null;
    	  try {
			   //根据业务单据主键 查询 付款结算单 中的def20 ，pk_upbill,Bill_type
               Object object = query.executeQuery("select a.def20,a.pk_upbill,a.Bill_type from cmp_paybill a inner join cmp_settlement b on a.pk_paybill = b.pk_busibill where b.pk_settlement ='"+hvo.getSrcpkid()+"'", new MapProcessor());
               if(object!=null){
            	   bank = (Map) object;
               }
	       } catch (BusinessException e) {
	           Logger.error(e.getMessage());
	           ExceptionUtils.wrappBusinessException("调用sql发生异常："+e.getMessage());
	       }
	      //回写nc65支付状态
    	  if (psvo[i].getPayState()==0) {
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
					JSONObject jsonsucc = JSONObject.parseObject(result);
					if (!"200".equals(jsonsucc.get("code"))) {
						ExceptionUtils.wrappBusinessException("回写65支付失败:" + jsonsucc.getString("msg"));
					}
				} catch (Exception e) {
					Logger.error(e.getMessage());
		            ExceptionUtils.wrappBusinessException("回写NC65单据失败，请检查!"+e.getMessage());
				}
			}
    	  }
      }
      
      EbankAutoDownLogVO[] palogs = saveLogs(psvo, corpPk, logmap);
      
      remsg = getMessage(1, palogs, "");
    }
    catch (Exception e)
    {
      ObmLog.error("预警平台自动下载状态时出现错误：" + e.getMessage(), e, getClass(), "download");
      throw new BusinessException(e.getMessage());
    }
    return remsg;
  }
  
  private EbankAutoDownLogVO[] saveLogs(BillObmStateVO[] retvos, String pk_org, Map<String, EBankPayLogVO> logmap)
  {
    if ((retvos == null) || (retvos.length == 0)) {
      return null;
    }
    EbankAutoDownLogVO[] logvos = new EbankAutoDownLogVO[retvos.length];
    EBankPayLogVO qvo = null;
    String yurref = "";
    for (int i = 0; i < retvos.length; i++)
    {
      yurref = retvos[i].getYurref();
      StringBuffer identify = new StringBuffer(MLObm.getStr00259() + yurref);
      qvo = (EBankPayLogVO)logmap.get(yurref);
      logvos[i] = new EbankAutoDownLogVO();
      logvos[i].setPk_org(pk_org);
      logvos[i].setAttributeValue("bankname", retvos[i].getBizData("bankname"));
      logvos[i].setAttributeValue("patype", "2");
      if (qvo == null) {
        qvo = (EBankPayLogVO)logmap.get(retvos[i].getDstyurref());
      }
      if (qvo != null)
      {
        logvos[i].setAttributeValue("pk_bankaccbas", qvo.getPk_bankaccsub());
        UFDouble trsamt = qvo.getTrsamt();
        if (trsamt != null) {
          trsamt = trsamt.setScale(2, 4);
        }
        identify.append(MLObm.getStr00260()).append(qvo.getSenddate()).append(MLObm.getStr00261()).append(trsamt);
        logvos[i].setAttributeValue("curacc", qvo.getDbtacc());
        if (StringUtil.isNull(logvos[i].getBankname())) {
          logvos[i].setAttributeValue("bankname", qvo.getDbtbranchname());
        }
        logvos[i].setAttributeValue("unitname", qvo.getDbtaccname());
        UFTime time = new UFTime(System.currentTimeMillis());
        logvos[i].setAttributeValue("down_date", new UFDate());
        logvos[i].setAttributeValue("down_time", time);
        logvos[i].setStatus(2);
      }
      if (retvos[i].getInterruptException() != null)
      {
        logvos[i].setDown_state("2");
        logvos[i].setMsg("[" + identify.toString() + "]" + retvos[i].getMsg() + retvos[i].getInterruptException().getMessage());
      }
      else
      {
        logvos[i].setDown_state("1");
        logvos[i].setMsg("[" + identify.toString() + "]" + retvos[i].getMsg());
      }
      if ((logvos[i].getMsg() != null) && (logvos[i].getMsg().getBytes().length > 300))
      {
        byte[] bytes = logvos[i].getMsg().getBytes();
        byte[] maxbytes = new byte[290];
        System.arraycopy(bytes, 0, maxbytes, 0, 290);
        logvos[i].setMsg(new String(maxbytes));
      }
    }
    try
    {
      MDPersistenceService.lookupPersistenceService().saveBill(logvos);
    }
    catch (BusinessException e)
    {
      ObmLog.error(e.getMessage(), e, getClass(), "saveLogs()");
    }
    return logvos;
  }
  
  private EbankAutoDownLogVO[] saveErrorLogs(String pk_org, String errmsg)
  {
    String[] pk_orgs = pk_org.split(",");
    if (pk_orgs.length > 1) {
      pk_org = pk_orgs[0];
    }
    EbankAutoDownLogVO[] logvos = new EbankAutoDownLogVO[1];
    logvos[0] = new EbankAutoDownLogVO();
    logvos[0].setPk_org(pk_org);
    logvos[0].setDown_state("2");
    

    String msg = errmsg;
    if ((msg != null) && (msg.getBytes().length > 100))
    {
      byte[] bytes = msg.getBytes();
      byte[] maxbytes = new byte[100];
      System.arraycopy(bytes, 0, maxbytes, 0, 100);
      msg = new String(maxbytes);
    }
    logvos[0].setAttributeValue("msg", msg);
    
    logvos[0].setAttributeValue("curacc", "-");
    logvos[0].setAttributeValue("bankname", "-");
    logvos[0].setAttributeValue("unitname", "-");
    UFTime time = new UFTime(System.currentTimeMillis());
    logvos[0].setAttributeValue("down_date", new UFDate());
    logvos[0].setAttributeValue("down_time", time);
    logvos[0].setAttributeValue("patype", "2");
    logvos[0].setStatus(2);
    if ((logvos[0].getMsg() != null) && (logvos[0].getMsg().getBytes().length > 100))
    {
      byte[] bytes = logvos[0].getMsg().getBytes();
      byte[] maxbytes = new byte[100];
      System.arraycopy(bytes, 0, maxbytes, 0, 100);
      logvos[0].setMsg(new String(maxbytes));
    }
    try
    {
      MDPersistenceService.lookupPersistenceService().saveBill(logvos);
    }
    catch (BusinessException e)
    {
      ObmLog.error(e.getMessage(), e, getClass(), "saveLogs()");
    }
    return logvos;
  }
}
