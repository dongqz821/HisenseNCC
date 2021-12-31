/*      */ package nc.impl.cmp.settlement;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import nc.bs.arap.bill.ArapBillDAO;
/*      */ import nc.bs.cmp.netpayment.event.SettlementNetPayStatProcessForERM;
/*      */ import nc.bs.cmp.pub.IsExistRecordResultSetProcessor;
/*      */ import nc.bs.dao.BaseDAO;
/*      */ import nc.bs.dao.DAOException;
/*      */ import nc.bs.framework.common.NCLocator;
/*      */ import nc.bs.logging.Logger;
/*      */ import nc.bs.uap.lock.PKLock;
/*      */ import nc.cmp.bill.util.CmpBillInterfaceProxy;
/*      */ import nc.cmp.bill.util.SysInit;
/*      */ import nc.cmp.pub.exception.ExceptionHandler;
/*      */ import nc.cmp.settlement.validate.SettleValidate;
/*      */ import nc.cmp.utils.CMPFactory;
import nc.vo.pub.BusinessException;
/*      */ import nc.cmp.utils.CmpInterfaceProxy;
/*      */ import nc.cmp.utils.CmpUtils;
/*      */ import nc.cmp.utils.DataUtil;
/*      */ import nc.cmp.utils.FireEvent;
/*      */ import nc.cmp.utils.InterfaceLocator;
/*      */ import nc.cmp.utils.Lists;
/*      */ import nc.cmp.utils.SettleUtils;
/*      */ import nc.impl.cmp.settlement.action.SettlementEditSaveAction;
/*      */ import nc.impl.cmp.settlement.action.SettlementReverseSettleAction;
/*      */ import nc.impl.cmp.settlement.action.SettlementSettleAction;
/*      */ import nc.impl.cmp.settlement.datapower.SettleDataPower;
/*      */ import nc.itf.cmp.busi.SettleNotifyPayTypeBusiBillServiceProxy;
/*      */ import nc.itf.cmp.settlement.ISettlementQueryService;
/*      */ import nc.itf.cmp.settlement.ISettlementService;
/*      */ import nc.itf.cmp.settlement.ISettlementServiceRequiresNew;
/*      */ import nc.itf.cmp.settlement.ProcessTemplate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
/*      */ import nc.itf.obm.europ.IEuropService;
/*      */ import nc.jdbc.framework.processor.BeanProcessor;
/*      */ import nc.pubitf.cmp.paybill.ICmpPayBillPubQueryService;
/*      */ import nc.pubitf.obm.payroll.IDFGZBillStateChange;
/*      */ import nc.pubitf.tmpub.util.SagasBizPubUtil;
/*      */ import nc.pubitf.uapbd.IBalaTypePubService;
/*      */ import nc.pubitf.uapbd.IBankaccPubQueryService;
/*      */ import nc.vo.arap.basebill.BaseAggVO;
/*      */ import nc.vo.bd.balatype.BalaTypeVO;
/*      */ import nc.vo.bd.bankaccount.BankAccSubVO;
/*      */ import nc.vo.bd.bankaccount.BankAccbasVO;
/*      */ import nc.vo.cmp.BusiInfo;
/*      */ import nc.vo.cmp.BusiStateTrans;
/*      */ import nc.vo.cmp.BusiStatus;
/*      */ import nc.vo.cmp.CMPExecStatus;
/*      */ import nc.vo.cmp.NetPayExecInfo;
/*      */ import nc.vo.cmp.SettleStatus;
/*      */ import nc.vo.cmp.SettleType;
/*      */ import nc.vo.cmp.bankaccbook.constant.OperateTypeEnum;
/*      */ import nc.vo.cmp.bill.BillAggVO;
/*      */ import nc.vo.cmp.bill.BillDetailVO;
/*      */ import nc.vo.cmp.exception.CmpAuthorizationException;
/*      */ import nc.vo.cmp.originalbalance.ErrMsg;
/*      */ import nc.vo.cmp.settlement.BillRegVO;
/*      */ import nc.vo.cmp.settlement.CheckException;
/*      */ import nc.vo.cmp.settlement.CmpMsg;
/*      */ import nc.vo.cmp.settlement.SettleContext;
/*      */ import nc.vo.cmp.settlement.SettleEnumCollection;
/*      */ import nc.vo.cmp.settlement.SettlementAggVO;
/*      */ import nc.vo.cmp.settlement.SettlementBodyVO;
/*      */ import nc.vo.cmp.settlement.SettlementHeadVO;
/*      */ import nc.vo.cmp.settlement.util.BankAccountBookVOChangeUtil;
/*      */ import nc.vo.cmp.settlement.util.SettlePubSecurityUtil;
/*      */ import nc.vo.cmp.validate.CMPValidate;
/*      */ import nc.vo.fbm.endore.PaymentStatusEnum;
/*      */ import nc.vo.ml.NCLangRes4VoTransl;
/*      */ import nc.vo.obm.europ.EuropExportVO;
/*      */ import nc.vo.obm.payroll.DfgzPaymentVO;
/*      */ import nc.vo.obm.payroll.contant.DFGZConst;
/*      */ import nc.vo.pub.AggregatedValueObject;
/*      */ import nc.vo.pub.BusinessException;
/*      */ import nc.vo.pub.CircularlyAccessibleValueObject;
/*      */ import nc.vo.pub.SuperVO;
/*      */ import nc.vo.pub.lang.UFBoolean;
/*      */ import nc.vo.pub.lang.UFDate;
/*      */ import nc.vo.pub.lang.UFDouble;
/*      */ import nc.vo.pubapp.AppContext;
/*      */ import nc.vo.tm.exception.NtbAlarmException;
/*      */ import nc.vo.tmpub.util.SqlUtil;
/*      */ import nc.vo.util.BDPKLockUtil;
import ncc.itf.baseapp.voucher.ICMPService;
/*      */ import nccloud.dto.tmpub.TbbCtrlInfo;
/*      */ import nccloud.itf.tmpub.util.PerformanceLog;
/*      */ import nccloud.pubitf.cmp.settlementmanegement.SettlementHeadSagaInfoAdapter;
/*      */ import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSONObject;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SettlementServiceImpl
/*      */   implements ISettlementService
/*      */ {
/*  103 */   private final ISettlementServiceRequiresNew service = (ISettlementServiceRequiresNew)NCLocator.getInstance().lookup(ISettlementServiceRequiresNew.class);
/*  104 */   private PerformanceLog plog = new PerformanceLog();
/*      */ 	 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void checkBalance(SettlementBodyVO[] bodys, boolean isOpp, boolean isCurrent, CmpMsg msg) throws BusinessException {
/*  121 */     List<String> tipAccount = CmpUtils.makeList();
/*  122 */     List<String> authorAccount = CmpUtils.makeList();
/*  123 */     List<String> refuseAccount = CmpUtils.makeList();
/*  124 */     List<ErrMsg> checkPlusList = CmpUtils.makeList();
/*  125 */     List<ErrMsg> checkSubList = CmpUtils.makeList();
/*  126 */     List<String> authorList = null;
/*  127 */     if (msg != null) {
/*  128 */       authorList = msg.getAuthList();
/*      */     }
/*  130 */     addCheckList(bodys, checkPlusList, checkSubList, isOpp, authorList);
/*      */ 
/*      */     
/*  133 */     StringBuffer outerErrorMsg = new StringBuffer("");
/*      */     
/*  135 */     if (CmpUtils.isListNotNull(checkPlusList)) {
/*  136 */       if (!isCurrent) {
/*  137 */         checkPlusList = InterfaceLocator.getBalanceCheck().checkLimitationReal(checkPlusList, false, true, isOpp);
/*      */       } else {
/*      */         
/*  140 */         checkPlusList = InterfaceLocator.getBalanceCheck().checkLimitationCurrent(checkPlusList, false, true, isOpp);
/*      */       } 
/*      */       
/*  143 */       for (int i = 0; i < checkPlusList.size(); i++) {
/*  144 */         ErrMsg errmsg = (ErrMsg)checkPlusList.get(i);
/*  145 */         if (errmsg.getErrStatus() == 1) {
/*  146 */           if (errmsg.getPk_account() != null && 
/*  147 */             !tipAccount.contains(errmsg.getPk_account())) {
/*  148 */             if (errmsg.getErrMsg() != null && 
/*  149 */               !"".equals(errmsg.getErrMsg())) {
/*  150 */               tipAccount.add(errmsg.getPk_account() + "|" + errmsg
/*  151 */                   .getErrMsg());
/*      */             } else {
/*  153 */               tipAccount.add(errmsg.getPk_account());
/*      */             } 
/*      */           }
/*  156 */         } else if (errmsg.getErrStatus() == 2) {
/*  157 */           if (errmsg.getPk_account() != null && 
/*  158 */             !refuseAccount.contains(errmsg.getPk_account())) {
/*  159 */             refuseAccount.add(errmsg.getPk_account());
/*  160 */             if (errmsg.getErrMsg() != null && 
/*  161 */               !"".equals(errmsg.getErrMsg())) {
/*  162 */               refuseAccount.add(errmsg.getPk_account() + "|" + errmsg
/*  163 */                   .getErrMsg());
/*      */             } else {
/*  165 */               refuseAccount.add(errmsg.getPk_account());
/*      */             } 
/*      */           } 
/*  168 */         } else if (errmsg.getErrStatus() == 3 && 
/*  169 */           errmsg.getPk_account() != null && 
/*  170 */           !authorAccount.contains(errmsg.getPk_account())) {
/*  171 */           if (errmsg.getErrMsg() != null && 
/*  172 */             !"".equals(errmsg.getErrMsg())) {
/*  173 */             authorAccount.add(errmsg.getPk_account() + "|" + errmsg
/*  174 */                 .getErrMsg());
/*      */           } else {
/*  176 */             authorAccount.add(errmsg.getPk_account());
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*  181 */         if (outerErrorMsg != null && 
/*  182 */           !"".equals(outerErrorMsg.toString())) {
/*  183 */           outerErrorMsg.append("\\|");
/*      */         }
/*  185 */         outerErrorMsg.append(errmsg.getOuterErrorMsg());
/*      */       } 
/*      */     } 
/*  188 */     if (CmpUtils.isListNotNull(checkSubList)) {
/*  189 */       if (!isCurrent) {
/*  190 */         checkSubList = InterfaceLocator.getBalanceCheck().checkLimitationReal(checkSubList, false, false, isOpp);
/*      */       } else {
/*      */         
/*  193 */         checkSubList = InterfaceLocator.getBalanceCheck().checkLimitationCurrent(checkSubList, false, false, isOpp);
/*      */       } 
/*      */ 
/*      */       
/*  197 */       for (int i = 0; i < checkSubList.size(); i++) {
/*  198 */         ErrMsg errmsg = (ErrMsg)checkSubList.get(i);
/*  199 */         if (errmsg.getErrStatus() == 1) {
/*  200 */           if (errmsg.getPk_account() != null && 
/*  201 */             !tipAccount.contains(errmsg.getPk_account())) {
/*  202 */             if (errmsg.getErrMsg() != null && 
/*  203 */               !"".equals(errmsg.getErrMsg())) {
/*  204 */               tipAccount.add(errmsg.getPk_account() + "|" + errmsg
/*  205 */                   .getErrMsg());
/*      */             } else {
/*  207 */               tipAccount.add(errmsg.getPk_account());
/*      */             } 
/*      */           }
/*  210 */         } else if (errmsg.getErrStatus() == 2) {
/*  211 */           if (errmsg.getPk_account() != null && 
/*  212 */             !refuseAccount.contains(errmsg.getPk_account())) {
/*  213 */             if (errmsg.getErrMsg() != null && 
/*  214 */               !"".equals(errmsg.getErrMsg())) {
/*  215 */               refuseAccount.add(errmsg.getPk_account() + "|" + errmsg
/*  216 */                   .getErrMsg());
/*      */             } else {
/*  218 */               refuseAccount.add(errmsg.getPk_account());
/*      */             } 
/*      */           }
/*  221 */         } else if (errmsg.getErrStatus() == 3 && 
/*  222 */           errmsg.getPk_account() != null && 
/*  223 */           !authorAccount.contains(errmsg.getPk_account())) {
/*  224 */           if (errmsg.getErrMsg() != null && 
/*  225 */             !"".equals(errmsg.getErrMsg())) {
/*  226 */             authorAccount.add(errmsg.getPk_account() + "|" + errmsg
/*  227 */                 .getErrMsg());
/*      */           } else {
/*  229 */             authorAccount.add(errmsg.getPk_account());
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/*  234 */         if (outerErrorMsg != null && 
/*  235 */           !"".equals(outerErrorMsg.toString())) {
/*  236 */           outerErrorMsg.append("\\|");
/*      */         }
/*  238 */         outerErrorMsg.append(errmsg.getOuterErrorMsg());
/*      */       } 
/*      */     } 
/*  241 */     if (tipAccount.size() != 0 || refuseAccount.size() != 0 || authorAccount
/*  242 */       .size() != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  262 */       StringBuffer tempStr = new StringBuffer("");
/*  263 */       if (outerErrorMsg != null && !"".equals(outerErrorMsg.toString())) {
/*  264 */         String[] outerErrorMsgs = outerErrorMsg.toString().split("\\|");
/*      */         
/*  266 */         for (String temp : outerErrorMsgs) {
/*  267 */           tempStr.append(temp).append("\n");
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  279 */       CmpAuthorizationException cmpex = null;
/*      */       
/*  281 */       if ("".equals(tempStr.toString())) {
/*      */         
/*  283 */         cmpex = new CmpAuthorizationException(NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0", "03607set-0787"));
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  288 */         cmpex = new CmpAuthorizationException(tempStr.toString());
/*      */       } 
/*      */       
/*  291 */       cmpex.setTipAccount(tipAccount);
/*  292 */       cmpex.setRefuseAccount(refuseAccount);
/*  293 */       cmpex.setAuthorAccount(authorAccount);
/*  294 */       throw cmpex;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handleWorkFlow(CmpMsg msg, boolean isHanding) throws BusinessException {
/*  302 */     SettlementAggVO aggVO = CmpInterfaceProxy.INSTANCE.getQueryService().findBeanByMsg(msg);
/*  303 */     SettlementHeadVO head = (SettlementHeadVO)aggVO.getParentVO();
/*  304 */     if (isHanding) {
/*  305 */       head.setAduitstatus(Integer.valueOf(1));
/*      */     } else {
/*      */       
/*  308 */       head.setAduitstatus(Integer.valueOf(3));
/*      */     } 
/*  310 */     head.setLastupdatedate(msg.getLastOperatorDate());
/*  311 */     head.setLastupdater(msg.getLastOperator());
/*  312 */     InterfaceLocator.getDao().updateHead(head);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isFillEbank(String pk_tradertype, String pk_org, String pk_group) throws BusinessException {
/*  320 */     return SettleNotifyPayTypeBusiBillServiceProxy.getService(pk_group, pk_tradertype)
/*      */       
/*  322 */       .isAutoFillEbankInfo(pk_org, pk_tradertype, pk_group);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBalance(SettlementBodyVO[] bodys, boolean isOpp, boolean isCurrent) throws BusinessException {
/*  328 */     List<ErrMsg> plusList = CmpUtils.makeList();
/*  329 */     List<ErrMsg> subList = CmpUtils.makeList();
/*  330 */     addUpdateList(bodys, plusList, subList, isOpp);
/*  331 */     if (plusList.size() != 0) {
/*  332 */       if (!isCurrent) {
/*  333 */         InterfaceLocator.getBalanceUpdate().increaseOrDecreaseRealtime(plusList, true);
/*      */       } else {
/*  335 */         InterfaceLocator.getBalanceUpdate().increaseOrDecreaseCurrent(plusList, true);
/*      */       } 
/*      */     }
/*      */     
/*  339 */     if (subList.size() != 0) {
/*  340 */       if (!isCurrent) {
/*  341 */         InterfaceLocator.getBalanceUpdate().increaseOrDecreaseRealtime(subList, false);
/*      */       } else {
/*  343 */         InterfaceLocator.getBalanceUpdate().increaseOrDecreaseCurrent(subList, false);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void addUpdateList(SettlementBodyVO[] bodys, List<ErrMsg> plusList, List<ErrMsg> subList, boolean isOpp) throws BusinessException {
/*  350 */     List<SettlementBodyVO> bodyList = CmpUtils.makeList();
/*      */     
/*  352 */     for (int i = 0; i < bodys.length; i++) {
/*  353 */       SettlementBodyVO body = bodys[i];
/*  354 */       if (body.getPk_account() != null || body
/*  355 */         .getPk_cashaccount() != null || body
/*  356 */         .getPk_notetype() != null) {
/*  357 */         bodyList.add(body);
/*      */       }
/*      */     } 
/*  360 */     if (bodyList == null || bodyList.size() == 0) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  365 */     for (int i = 0; i < bodyList.size(); i++) {
/*  366 */       SettlementBodyVO body = (SettlementBodyVO)bodyList.get(i);
/*  367 */       ErrMsg errmsg = new ErrMsg();
/*  368 */       errmsg.setPk_currtype(body.getPk_currtype());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  377 */       if (body.getPk_account() != null && 
/*  378 */         !body.getPk_account().equals("")) {
/*  379 */         errmsg.setPk_account(body.getPk_account());
/*  380 */         errmsg.setBankAccount(true);
/*  381 */       } else if (body.getPk_cashaccount() != null && 
/*  382 */         !body.getPk_cashaccount().equals("")) {
/*  383 */         errmsg.setPk_account(body.getPk_cashaccount());
/*  384 */         errmsg.setBankAccount(false);
/*      */       } 
/*      */       
/*  387 */       if (body.getDirection().intValue() == 1) {
/*  388 */         errmsg.setPrimal(body.getPay());
/*  389 */         errmsg.setLocal(body.getPaylocal());
/*      */       } else {
/*  391 */         errmsg.setPrimal(body.getReceive());
/*  392 */         errmsg.setLocal(body.getReceivelocal());
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  397 */       errmsg.setPk_org(body.getPk_org());
/*  398 */       errmsg.setPk_corp(body.getPk_group());
/*      */       
/*  400 */       errmsg.setCurrentDate((body.getBill_date() == null) ? body
/*  401 */           .getBill_date() : new UFDate());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  419 */       errmsg.setGlobalocal(UFDouble.ZERO_DBL);
/*  420 */       errmsg.setGrouplocal(UFDouble.ZERO_DBL);
/*  421 */       if (body.getDirection().intValue() == 1) {
/*  422 */         if (!isOpp) {
/*  423 */           subList.add(errmsg);
/*      */         } else {
/*  425 */           plusList.add(errmsg);
/*      */         }
/*      */       
/*  428 */       } else if (!isOpp) {
/*  429 */         plusList.add(errmsg);
/*      */       } else {
/*  431 */         subList.add(errmsg);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addCheckList(SettlementBodyVO[] bodys, List<ErrMsg> checkPlusList, List<ErrMsg> checkSubList, boolean isOpp, List<String> authorList) {
/*  454 */     List<SettlementBodyVO> bodyList = CmpUtils.makeList();
/*      */     
/*  456 */     for (int i = 0; i < bodys.length; i++) {
/*  457 */       SettlementBodyVO body = bodys[i];
/*  458 */       if ((body.getPk_account() != null || body.getPk_cashaccount() != null) && UFBoolean.FALSE
/*  459 */         .equals(body.getIsauthpass())) {
/*  460 */         bodyList.add(body);
/*      */       }
/*      */     } 
/*  463 */     if (bodyList == null || bodyList.size() == 0) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  468 */     List<SettlementBodyVO> bankList = null;
/*  469 */     if (!CheckException.checkContionsIsNull(authorList)) {
/*  470 */       Map<String, List<SettlementBodyVO>> map = new HashMap<String, List<SettlementBodyVO>>();
/*  471 */       for (SettlementBodyVO body : bodyList) {
/*  472 */         String pk_account = null;
/*      */         
/*  474 */         if (body.getPk_account() != null && 
/*  475 */           !body.getPk_account().equals("")) {
/*  476 */           pk_account = body.getPk_account();
/*      */         }
/*  478 */         if (body.getPk_cashaccount() != null && 
/*  479 */           !body.getPk_cashaccount().equals("")) {
/*  480 */           pk_account = body.getPk_cashaccount();
/*      */         }
/*      */         
/*  483 */         bankList = (List)map.get(pk_account);
/*  484 */         if (bankList == null) {
/*  485 */           bankList = CmpUtils.makeList();
/*  486 */           map.put(pk_account, bankList);
/*      */         } 
/*  488 */         bankList.add(body);
/*      */       } 
/*      */       
/*  491 */       for (String accountId : authorList) {
/*  492 */         if (map.containsKey(accountId)) {
/*  493 */           map.remove(accountId);
/*      */         }
/*      */       } 
/*  496 */       bodyList.clear();
/*  497 */       for (List<SettlementBodyVO> bodysList : map.values()) {
/*  498 */         bodyList.addAll(bodysList);
/*      */       }
/*  500 */       for (SettlementBodyVO body : bodyList) {
/*  501 */         body.setIsauthpass(UFBoolean.FALSE);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  506 */     for (SettlementBodyVO body : bodyList) {
/*  507 */       ErrMsg errmsg = new ErrMsg();
/*      */       
/*  509 */       errmsg.setPk_org(body.getPk_org());
/*  510 */       errmsg.setPk_currtype(body.getPk_currtype());
/*      */       
/*  512 */       if (body.getPk_account() != null && 
/*  513 */         !body.getPk_account().equals("")) {
/*  514 */         errmsg.setPk_account(body.getPk_account());
/*  515 */         errmsg.setBankAccount(true);
/*  516 */       } else if (body.getPk_cashaccount() != null && 
/*  517 */         !body.getPk_cashaccount().equals("")) {
/*  518 */         errmsg.setPk_account(body.getPk_cashaccount());
/*  519 */         errmsg.setBankAccount(false);
/*      */       } 
/*      */       
/*  522 */       if (body.getDirection().intValue() == 1) {
/*  523 */         errmsg.setBillType("F5");
/*  524 */         errmsg.setPrimal(body.getPay());
/*  525 */         errmsg.setLocal(body.getPaylocal());
/*  526 */         errmsg.setGrouplocal(body.getGrouppaylocal());
/*  527 */         errmsg.setGlobalocal(body.getGlobalpaylocal());
/*  528 */         if (!isOpp) {
/*      */           
/*  530 */           checkSubList.add(errmsg);
/*      */           
/*      */           continue;
/*      */         } 
/*  534 */         checkPlusList.add(errmsg);
/*      */         
/*      */         continue;
/*      */       } 
/*  538 */       errmsg.setBillType("F4");
/*  539 */       errmsg.setPrimal(body.getReceive());
/*  540 */       errmsg.setLocal(body.getReceivelocal());
/*  541 */       errmsg.setGrouplocal(body.getGroupreceivelocal());
/*  542 */       errmsg.setGlobalocal(body.getGlobalreceivelocal());
/*  543 */       if (!isOpp) {
/*      */         
/*  545 */         checkPlusList.add(errmsg);
/*      */         
/*      */         continue;
/*      */       } 
/*  549 */       checkSubList.add(errmsg);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  558 */   public ProcessTemplate getTemplate(String name) throws BusinessException { return CMPFactory.createTemplateMethod(name); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  565 */   public SettleContext handleAudit(SettleContext context) throws BusinessException { return context; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  573 */   public SettleContext handleCancelAudit(SettleContext context) throws BusinessException { return context; }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettleContext handleCancelSettle(SettleContext context) throws BusinessException {
/*  579 */     this.plog.start("handleCancelSettle");
/*  580 */     List<String> pks = CmpUtils.makeList();
/*  581 */     List<SettlementAggVO> beanList = context.getBeanList();
			   	   
			   Map<String, Object> map = new HashMap<>();
				
/*  582 */     this.plog.start("checkCloseSettleAccount");
/*  583 */     SettleValidate.checkCloseSettleAccount((SettlementAggVO[])beanList
/*  584 */         .toArray(new SettlementAggVO[0]));
/*  585 */     this.plog.end("checkCloseSettleAccount");
/*  586 */     for (SettlementAggVO settlementAggVO : beanList) {
/*      */     
/*  588 */       SettlementHeadVO headvo = (SettlementHeadVO)settlementAggVO.getParentVO();

				 map.put("billcode", headvo.getBillcode());//业务单据编号
				 map.put("settlestatus", "0");//结算状态 headvo.getSettlestatus()
				 
/*  589 */       headvo.setSettlestatus(Integer.valueOf(SettleStatus.NONESETTLE.getStatus()));
				 
/*      */     } 
/*      */     
/*  592 */     this.plog.start("checkInformer");
/*  593 */     checkInformer(beanList);
/*  594 */     this.plog.end("checkInformer");
/*      */     
/*  596 */     Map<String, SettlementAggVO> errMap = new HashMap<String, SettlementAggVO>();
/*  597 */     Map<String, SettlementAggVO> successMap = new HashMap<String, SettlementAggVO>();
/*  598 */     if (beanList.size() == 0) {
/*  599 */       this.plog.end("handleCancelSettle");
/*  600 */       return context;
/*  601 */     }  if (beanList.size() == 1) {
				
/*      */       
/*  604 */       SettlementReverseSettleAction reverseSettleAction = new SettlementReverseSettleAction(new SettlementAggVO[] { (SettlementAggVO)beanList.get(0) });
/*      */       
/*  606 */       reverseSettleAction.setNotifyBusiBill(true);
/*      */       try {
/*  608 */         this.plog.start("SettlementReverseSettleAction#handleProcess");
				   
					String param = JSONObject.toJSONString(map);
					JSONObject json = JSONObject.parseObject(param);
					//调用nc65接口 回写 取消结算 dongqingzheng 
					try {
						ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
						String result = gett.settlementBack2NC65(json);
						JSONObject jsonsucc = JSONObject.parseObject(result);
						if (!"200".equals(jsonsucc.get("code"))) {
							ExceptionUtils.wrappBusinessException("回写失败:" + jsonsucc.getString("msg"));
						}
					} catch (Exception e) {
						ExceptionUtils.wrappBusinessException("回写NC65单据失败!"+e.getMessage());
					}
					
					
/*  609 */         reverseSettleAction.handleProcess();
/*  610 */         this.plog.end("SettlementReverseSettleAction#handleProcess");
/*      */       } finally {
/*      */         
/*  613 */         ISettlementQueryService settlementQueryService = (ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class);
/*  614 */         this.plog.start("querySettlementAggVOsByPks");
/*      */         
/*  616 */         SettlementAggVO[] settlementAggVOs = settlementQueryService.querySettlementAggVOsByPks(new String[] {
/*  617 */               ((SettlementAggVO)beanList.get(0)).getParentVO().getPrimaryKey() });
/*  618 */         this.plog.end("querySettlementAggVOsByPks");
/*  619 */         context.setBeanList(Arrays.asList(settlementAggVOs));
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  625 */       ISettlementServiceRequiresNew newService = (ISettlementServiceRequiresNew)NCLocator.getInstance().lookup(ISettlementServiceRequiresNew.class);
/*  626 */       for (int i = 0; i < beanList.size(); i++) {
/*      */         try {
/*  628 */           pks.add(((SettlementAggVO)beanList.get(i)).getParentVO().getPrimaryKey());
/*  629 */           newService.handleCancelSettle_RequiresNew((SettlementAggVO)beanList.get(i));
/*  630 */           successMap
/*  631 */             .put(
/*  632 */               NCLangRes4VoTransl.getNCLangRes()
/*  633 */               .getStrByID("3607set_0", "03607set-0897", null, new String[] {
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/*  638 */                   ((SettlementAggVO)beanList.get(i))
/*  639 */                   .getParentVO()
/*  640 */                   .getAttributeValue("billcode")
/*      */                   
/*  642 */                   .toString()
/*      */                 
/*  644 */                 }), beanList.get(i));
						String param = JSONObject.toJSONString(map);
						JSONObject json = JSONObject.parseObject(param);
						//调用nc65接口 回写 取消结算 dongqingzheng 
						try {
							ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
							String result = gett.settlementBack2NC65(json);
							JSONObject jsonsucc = JSONObject.parseObject(result);
							if (!"200".equals(jsonsucc.get("code"))) {
								ExceptionUtils.wrappBusinessException("回写失败:" + jsonsucc.getString("msg"));
							}
						} catch (Exception e) {
							ExceptionUtils.wrappBusinessException("回写NC65单据失败!"+e.getMessage());
						}
						
						
/*  645 */         } catch (Exception e) {
/*  646 */           errMap.put(e.getMessage(), null);
/*      */         } 
/*      */       } 
/*      */       
/*  650 */       ISettlementQueryService settlementQueryService = (ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class);
/*  651 */       this.plog.start("querySettlementAggVOsByPks");
/*      */       
/*  653 */       SettlementAggVO[] settlementAggVOs = settlementQueryService.querySettlementAggVOsByPks((String[])pks.toArray(new String[0]));
/*  654 */       this.plog.end("querySettlementAggVOsByPks");
/*  655 */       context.setBeanList(Arrays.asList(settlementAggVOs));
/*  656 */       context.setErrMap(errMap);
/*  657 */       context.setSuccessMap(successMap);
/*      */     } 
/*  659 */     this.plog.end("handleCancelSettle");
/*  660 */     return context;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkInformer(List<SettlementAggVO> beanList) throws BusinessException {
/*  665 */     this.plog.start("checkInformer");
/*  666 */     List<String> pk_busibills = new ArrayList<String>();
/*  667 */     for (SettlementAggVO settlementAggVO : beanList) {
/*      */ 
/*      */       
/*  670 */       SettlementHeadVO settlementHeadVO = (SettlementHeadVO)settlementAggVO.getParentVO();
/*  671 */       pk_busibills.add(settlementHeadVO.getPk_busibill());
/*      */     } 
/*      */ 
/*      */     
/*  675 */     String sql = " select count(*) from cmp_informer  where dr = 0 and  " + SqlUtil.buildSqlForIn("pk_lower", (String[])pk_busibills
/*  676 */         .toArray(new String[0]));
/*  677 */     BaseDAO baseDAO = new BaseDAO();
/*      */     
/*      */     try {
/*  680 */       this.plog.start("executeQuery");
/*  681 */       Object isExits = baseDAO.executeQuery(sql, new IsExistRecordResultSetProcessor());
/*      */       
/*  683 */       this.plog.end("executeQuery");
/*  684 */       if (UFBoolean.TRUE.equals(isExits))
/*      */       {
/*  686 */         throw ExceptionHandler.createException(
/*  687 */             NCLangRes4VoTransl.getNCLangRes().getStrByID("3607settle4_0", "03607settle4-0009"));
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*  694 */     catch (DAOException e) {
/*  695 */       this.plog.end("checkInformer");
/*  696 */       ExceptionHandler.consume(e);
/*  697 */       throw ExceptionHandler.createException(e);
/*      */     } 
/*  699 */     this.plog.end("checkInformer");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettleContext handleCancelSign(SettleContext context) throws BusinessException {
/*  706 */     this.plog.start("handleCancelSign");
/*  707 */     List<String> pks = CmpUtils.makeList();
/*  708 */     List<SettlementAggVO> beanList = context.getBeanList();
/*      */     
/*  710 */     SettlementAggVO[] settlementAggVOs = (SettlementAggVO[])beanList.toArray(new SettlementAggVO[0]);
/*  711 */     this.plog.start("CMPValidate.validate");
/*  712 */     CMPValidate.validate("signdate", settlementAggVOs);
/*  713 */     this.plog.end("CMPValidate.validate");
/*  714 */     this.plog.start("validataUserhasPermission");
/*  715 */     SettleDataPower.validataUserhasPermission("unsgin", settlementAggVOs);
/*      */     
/*  717 */     this.plog.end("validataUserhasPermission");
/*      */     
/*  719 */     Map<String, SettlementAggVO> errMap = new HashMap<String, SettlementAggVO>();
/*  720 */     Map<String, SettlementAggVO> successMap = new HashMap<String, SettlementAggVO>();
/*      */ 
/*      */     
/*  723 */     Map<String, TbbCtrlInfo> tbbInfo = new HashMap<String, TbbCtrlInfo>();
/*  724 */     context.setTbbInfo(tbbInfo);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  736 */     ISettlementServiceRequiresNew newService = (ISettlementServiceRequiresNew)NCLocator.getInstance().lookup(ISettlementServiceRequiresNew.class);
/*  737 */     for (int i = 0; i < beanList.size(); i++) {
/*      */       try {
/*  739 */         pks.add(((SettlementAggVO)beanList.get(i)).getParentVO().getPrimaryKey());
/*  740 */         newService.handleCancelSign_RequiresNew((SettlementAggVO)beanList.get(i));
/*      */         
/*  742 */         successMap
/*  743 */           .put(
/*  744 */             NCLangRes4VoTransl.getNCLangRes()
/*  745 */             .getStrByID("3607set_0", "03607set-0898", null, new String[] {
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  750 */                 ((SettlementAggVO)beanList.get(i))
/*  751 */                 .getParentVO()
/*  752 */                 .getAttributeValue("billcode")
/*      */                 
/*  754 */                 .toString()
/*      */               
/*  756 */               }), beanList.get(i));
/*      */       }
/*  758 */       catch (NtbAlarmException e) {
/*  759 */         TbbCtrlInfo info = e.getInfo();
/*  760 */         tbbInfo.put(info.getPk(), info);
/*      */       
/*      */       }
/*  763 */       catch (Exception e) {
/*  764 */         errMap.put(e.getMessage(), beanList.get(i));
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  769 */     ISettlementQueryService settlementQueryService = (ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class);
/*  770 */     this.plog.start("querySettlementAggVOsByPks");
/*      */     
/*  772 */     settlementAggVOs = settlementQueryService.querySettlementAggVOsByPks((String[])pks.toArray(new String[0]));
/*  773 */     this.plog.end("querySettlementAggVOsByPks");
/*  774 */     context.setBeanList(Arrays.asList(settlementAggVOs));
/*  775 */     context.setErrMap(errMap);
/*  776 */     context.setSuccessMap(successMap);
/*      */     
/*  778 */     this.plog.end("handleCancelSign");
/*  779 */     return context;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettleContext handleCombinSettle(SettleContext context) throws BusinessException {
/*  786 */     CMPValidate.validate((AggregatedValueObject[])CmpUtils.covertListToArrays(context.getBeanList(), SettlementAggVO.class));
/*      */ 
/*      */ 
/*      */     
/*  790 */     SettlementSettleAction settleActoin = new SettlementSettleAction((SettlementAggVO[])CmpUtils.covertListToArrays(context.getBeanList(), SettlementAggVO.class));
/*      */     
/*  792 */     settleActoin.setGenerateOne(true);
/*  793 */     settleActoin.getControlMap().put("isAddLock", Boolean.valueOf(true));
/*  794 */     settleActoin.setNotifyBusiBill(true);
/*      */     
/*  796 */     settleActoin.handleProcess();
/*      */     
/*  798 */     List<String> list = new ArrayList<String>();
/*  799 */     for (SettlementAggVO settlementAggVO : context.getBeanList()) {
/*  800 */       list.add(settlementAggVO.getParentVO().getPrimaryKey());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  805 */     SettlementAggVO[] settlementAggVOs = ((ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class)).querySettlementAggVOsByPks((String[])list.toArray(new String[0]));
/*      */     
/*  807 */     String pk = settlementAggVOs[0].getParentVO().getPrimaryKey();
/*  808 */     SagasBizPubUtil.checkFrozen(new SettlementHeadSagaInfoAdapter(), new String[] { pk });
/*      */ 
/*      */     
/*  811 */     context.setBeanList(Arrays.asList(settlementAggVOs));
/*      */     
/*  813 */     return context;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettleContext handleEdit(SettleContext context) throws BusinessException {
/*  830 */     List<SettlementAggVO> beanList = context.getBeanList();
/*      */     
/*  832 */     checkEurope(beanList);
/*      */     
/*  834 */     for (int i = 0; i < beanList.size(); i++) {
/*      */ 
/*      */       
/*      */       try {
/*  838 */         SettlementEditSaveAction editSaveAction = new SettlementEditSaveAction(true, (SettlementAggVO)beanList.get(i));
/*  839 */         editSaveAction.handleProcess();
/*  840 */         SettlementAggVO settagg = editSaveAction.getSettleAggs()[0];
/*  841 */         beanList.remove(i);
/*  842 */         beanList.add(settagg);
/*  843 */       } catch (Exception e) {
/*  844 */         ExceptionHandler.handleException(e);
/*      */       } 
/*      */     } 
/*      */     
/*  848 */     return context;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkEurope(List<SettlementAggVO> beanList) throws BusinessException {
/*  854 */     for (int i = 0, length = beanList.size(); i < length; i++) {
/*  855 */       SettlementAggVO settlementAggVO = (SettlementAggVO)beanList.get(i);
/*      */ 
/*      */       
/*  858 */       String pk_org = (String)settlementAggVO.getParentVO().getAttributeValue("pk_org");
/*      */       
/*  860 */       UFBoolean cmp52 = null;
/*      */       try { 
/*  862 */         cmp52 = SysInit.getParaBoolean(pk_org, "CMP52");
/*  863 */       } catch (BusinessException e) {
/*  864 */         ExceptionHandler.consume(e);
/*      */       } 
/*  866 */       if (UFBoolean.TRUE.equals(cmp52)) {
/*      */         
/*  868 */         SettlementBodyVO[] settlementBodyVOs = (SettlementBodyVO[])settlementAggVO.getChildrenVO();
/*      */         
/*  870 */         Set<String> set = new HashSet<String>();
/*  871 */         for (SettlementBodyVO settlementBodyVO : settlementBodyVOs) {
/*  872 */           String pk_balatype = settlementBodyVO.getPk_balatype();
/*  873 */           if (pk_balatype != null) {
/*  874 */             set.add(pk_balatype);
/*      */           }
/*      */         } 
/*      */         
/*  878 */         if (set.size() > 0) {
/*  879 */           for (String pk_balatype : set) {
/*      */ 
/*      */             
/*  882 */             IBalaTypePubService balaTypePubService = InterfaceLocator.getInterfaceLocator().getBalaQry();
/*      */             
/*  884 */             BalaTypeVO btvo = balaTypePubService.findBalaTypeVOByPK(pk_balatype);
/*      */             
/*  886 */             if (UFBoolean.TRUE.equals(btvo.getDirectincome()) || UFBoolean.TRUE
/*  887 */               .equals(btvo.getConsignpay()))
/*      */             {
/*      */               
/*  890 */               ExceptionHandler.createandthrowException(
/*  891 */                   NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0", "03607set-0862"));
/*      */             }
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettleContext handleSettle(SettleContext context) throws BusinessException {
/*  917 */     this.plog.start("handleSettle");
/*  918 */     this.plog.start("makeList");
/*  919 */     List<String> pks = CmpUtils.makeList();
/*  920 */     this.plog.end("makeList");
/*  921 */     List<SettlementAggVO> beanList = context.getBeanList();
/*      */     
/*  923 */     this.plog.start("filterSettleInfo4HandSettleFlag4UnSettle");
/*      */     
/*  925 */     beanList = SettleUtils.filterSettleInfo4HandSettleFlag4UnSettle((SettlementAggVO[])beanList
/*  926 */         .toArray(new SettlementAggVO[0]));
/*  927 */     this.plog.end("filterSettleInfo4HandSettleFlag4UnSettle");
/*      */ 
/*      */ 
/*      */     
/*  931 */     Map<String, SettlementAggVO> errMap = new HashMap<String, SettlementAggVO>();
/*  932 */     Map<String, SettlementAggVO> successMap = new HashMap<String, SettlementAggVO>();
				
				Map<String, Object> map = new HashMap<>();
//				SettleValidate.checkCloseSettleAccount((SettlementAggVO[])beanList.toArray(new SettlementAggVO[0]));
				for (SettlementAggVO settlementAggVO : beanList) {
					SettlementHeadVO headvo = (SettlementHeadVO)settlementAggVO.getParentVO();
					map.put("billcode", headvo.getBillcode());//业务单据编号
					map.put("settlestatus", "1");//结算状态 headvo.getSettlestatus()
/*      */     } 
				
/*  933 */     if (beanList.size() == 1) {
/*  934 */       this.plog.start("SettlementSettleAction.handleProcess");
/*      */       
/*  936 */       SettlementSettleAction settleAction = new SettlementSettleAction(new SettlementAggVO[] { (SettlementAggVO)beanList.get(0) });
/*  937 */       settleAction.setNotifyBusiBill(true);

				 String param = JSONObject.toJSONString(map);
				 JSONObject json = JSONObject.parseObject(param);
				 //调用nc65接口 回写结算 dongqingzheng 
				 try {
					 ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
					 String result = gett.settlementBack2NC65(json);
					 JSONObject jsonsucc = JSONObject.parseObject(result);
						if (!"200".equals(jsonsucc.get("code"))) {
							ExceptionUtils.wrappBusinessException("回写失败:" + jsonsucc.getString("msg"));
						}
				} catch (Exception e) {
					ExceptionUtils.wrappBusinessException("回写NC65单据失败!"+e.getMessage());
				}
				

/*  938 */       settleAction.handleProcess();
/*  939 */       this.plog.end("SettlementSettleAction.handleProcess");
/*  940 */       context.setBeanList(CmpUtils.covertArraysToList(settleAction
/*  941 */             .getSettleAggs()));
/*  942 */     } else if (beanList.size() > 1) {
/*      */ 
/*      */       
/*  945 */       Map<String, SettlementAggVO> ntbSettlementAggVOs = CmpUtils.makeMap();
/*      */ 
/*      */       
/*  948 */       ISettlementServiceRequiresNew newService = (ISettlementServiceRequiresNew)NCLocator.getInstance().lookup(ISettlementServiceRequiresNew.class);
/*  949 */       for (int i = 0; i < beanList.size(); i++) {
/*      */         try {
/*  951 */           this.plog.start("ISettlementServiceRequiresNew.handleSettle_RequiresNew");
/*  952 */           pks.add(((SettlementAggVO)beanList.get(i)).getParentVO().getPrimaryKey());
/*      */           
/*  954 */           SettlementAggVO settlementAggVO = newService.handleSettle_RequiresNew((SettlementAggVO)beanList.get(i));
/*  955 */           this.plog.end("ISettlementServiceRequiresNew.handleSettle_RequiresNew");
/*  956 */           ntbSettlementAggVOs.put(settlementAggVO.getParentVO()
/*  957 */               .getPrimaryKey(), settlementAggVO);
/*  958 */           successMap
/*  959 */             .put(
/*  960 */               NCLangRes4VoTransl.getNCLangRes()
/*  961 */               .getStrByID("3607set_0", "03607set-0899", null, new String[] {
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/*  966 */                   ((SettlementAggVO)beanList.get(i))
/*  967 */                   .getParentVO()
/*  968 */                   .getAttributeValue("billcode")
/*      */                   
/*  970 */                   .toString()
/*      */                 
/*  972 */                 }), beanList.get(i));

					 String param = JSONObject.toJSONString(map);
					 JSONObject json = JSONObject.parseObject(param);
					 //调用nc65接口 回写结算 dongqingzheng 
					 try {
						 ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
						 String result = gett.settlementBack2NC65(json);
						 JSONObject jsonsucc = JSONObject.parseObject(result);
							if (!"200".equals(jsonsucc.get("code"))) {
								ExceptionUtils.wrappBusinessException("回写失败:" + jsonsucc.getString("msg"));
							}
					 } catch (Exception e) {
						ExceptionUtils.wrappBusinessException("回写NC65单据失败"+e.getMessage());
					 }
					 

/*  973 */         } catch (Exception e) {
/*  974 */           errMap.put(e.getMessage(), null);
/*      */         } 
/*      */       } 
/*      */       
/*  978 */       this.plog.start("ISettlementQueryService.querySettlementAggVOsByPks");
/*      */       
/*  980 */       ISettlementQueryService settlementQueryService = (ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class);
/*      */       
/*  982 */       SettlementAggVO[] settlementAggVOs = settlementQueryService.querySettlementAggVOsByPks((String[])pks.toArray(new String[0]));
/*  983 */       this.plog.end("ISettlementQueryService.querySettlementAggVOsByPks");
/*      */       
/*  985 */       this.plog.start("ntbermsg");
/*  986 */       for (SettlementAggVO settlementAggVO : settlementAggVOs) {
/*  987 */         if (ntbSettlementAggVOs.containsKey(settlementAggVO
/*  988 */             .getParentVO().getPrimaryKey())) {
/*  989 */           settlementAggVO.getParentVO().setAttributeValue("ntberrmsg", ((SettlementAggVO)ntbSettlementAggVOs
/*      */ 
/*      */               
/*  992 */               .get(settlementAggVO.getParentVO()
/*  993 */                 .getPrimaryKey())).getParentVO()
/*  994 */               .getAttributeValue("ntberrmsg"));
/*      */         }
/*      */       } 
/*      */       
/*  998 */       this.plog.end("ntbermsg");
/*  999 */       context.setBeanList(Arrays.asList(settlementAggVOs));
/* 1000 */       context.setErrMap(errMap);
/* 1001 */       context.setSuccessMap(successMap);
/*      */     } 
/* 1003 */     this.plog.end("handleSettle");
/* 1004 */     return context;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettleContext handleSign(SettleContext context) throws BusinessException {
/* 1017 */     this.plog.start("handleSign");
/* 1018 */     List<String> pks = CmpUtils.makeList();
/* 1019 */     List<SettlementAggVO> beanList = context.getBeanList();
/*      */     
/* 1021 */     SettlementAggVO[] settlementAggVOs = (SettlementAggVO[])beanList.toArray(new SettlementAggVO[0]);
/* 1022 */     this.plog.start("CMPValidate.validate");
/* 1023 */     CMPValidate.validate(settlementAggVOs);
/* 1024 */     this.plog.end("CMPValidate.validate");
/* 1025 */     this.plog.start("validataUserhasPermission");
/* 1026 */     SettleDataPower.validataUserhasPermission("sign", settlementAggVOs);
/*      */     
/* 1028 */     this.plog.end("validataUserhasPermission");
/*      */ 
/*      */ 
/*      */     
/* 1032 */     ISettlementServiceRequiresNew newService = (ISettlementServiceRequiresNew)NCLocator.getInstance().lookup(ISettlementServiceRequiresNew.class);
/*      */     
/* 1034 */     Map<String, SettlementAggVO> errMap = new HashMap<String, SettlementAggVO>();
/* 1035 */     Map<String, SettlementAggVO> successMap = new HashMap<String, SettlementAggVO>();
/*      */ 
/*      */     
/* 1038 */     Map<String, TbbCtrlInfo> tbbInfo = new HashMap<String, TbbCtrlInfo>();
/* 1039 */     context.setTbbInfo(tbbInfo);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1081 */     Map<String, SettlementAggVO> ntbSettlementAggVOs = CmpUtils.makeMap();
/*      */     
/* 1083 */     for (int i = 0; i < beanList.size(); i++) {
/*      */       try {
/* 1085 */         pks.add(((SettlementAggVO)beanList.get(i)).getParentVO().getPrimaryKey());
/*      */         
/* 1087 */         SettlementAggVO settlementAggVO = newService.handleSign_RequiresNew((SettlementAggVO)beanList.get(i));
/*      */         
/* 1089 */         ntbSettlementAggVOs.put(settlementAggVO.getParentVO()
/* 1090 */             .getPrimaryKey(), settlementAggVO);
/* 1091 */         successMap
/* 1092 */           .put(
/* 1093 */             NCLangRes4VoTransl.getNCLangRes()
/* 1094 */             .getStrByID("3607set_0", "03607set-0900", null, new String[] {
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1099 */                 ((SettlementAggVO)beanList.get(i))
/* 1100 */                 .getParentVO()
/* 1101 */                 .getAttributeValue("billcode")
/*      */                 
/* 1103 */                 .toString()
/*      */               
/* 1105 */               }), beanList.get(i));
/*      */       
/*      */       }
/* 1108 */       catch (NtbAlarmException e) {
/* 1109 */         TbbCtrlInfo info = e.getInfo();
/* 1110 */         tbbInfo.put(info.getPk(), info);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1122 */     ISettlementQueryService settlementQueryService = (ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class);
/* 1123 */     this.plog.start("querySettlementAggVOsByPks");
/*      */     
/* 1125 */     settlementAggVOs = settlementQueryService.querySettlementAggVOsByPks((String[])pks.toArray(new String[0]));
/* 1126 */     this.plog.end("querySettlementAggVOsByPks");
/*      */     
/* 1128 */     this.plog.start("netberrmsg");
/* 1129 */     for (SettlementAggVO settlementAggVO : settlementAggVOs) {
/* 1130 */       if (ntbSettlementAggVOs.containsKey(settlementAggVO
/* 1131 */           .getParentVO().getPrimaryKey())) {
/* 1132 */         settlementAggVO.getParentVO().setAttributeValue("ntberrmsg", ((SettlementAggVO)ntbSettlementAggVOs
/*      */ 
/*      */             
/* 1135 */             .get(settlementAggVO.getParentVO()
/* 1136 */               .getPrimaryKey())).getParentVO()
/* 1137 */             .getAttributeValue("ntberrmsg"));
/*      */       }
/*      */     } 
/*      */     
/* 1141 */     this.plog.end("netberrmsg");
/* 1142 */     context.setBeanList(Arrays.asList(settlementAggVOs));
/* 1143 */     context.setErrMap(errMap);
/* 1144 */     context.setSuccessMap(successMap);
/*      */     
/* 1146 */     this.plog.end("handleSign");
/* 1147 */     return context;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public BillRegVO findBusiUIClassname(String billtype) throws BusinessException {
/* 1153 */     StringBuffer sb = new StringBuffer();
/* 1154 */     sb.append("select ")
/* 1155 */       .append("cmp_billtypereg.serverclassname, ")
/* 1156 */       .append("cmp_billtypereg.billtype, cmp_billtypereg.uiclassname, ")
/* 1157 */       .append("cmp_billtypereg.modelcode, cmp_billtypereg.settlementinfofetcher, ")
/* 1158 */       .append("cmp_billtypereg.pk_billtypereg ")
/* 1159 */       .append("from cmp_billtypereg ")
/* 1160 */       .append("where cmp_billtypereg.billtype =  '")
/* 1161 */       .append(billtype)
/* 1162 */       .append("'")
/* 1163 */       .append("or rtrim(cmp_billtypereg.billtype) in ")
/* 1164 */       .append("(select rtrim(bd_billtype.parentbilltype) from bd_billtype where bd_billtype.pk_billtypecode = '")
/* 1165 */       .append(billtype).append("')");
/*      */     
/* 1167 */     String sql = sb.toString();
/* 1168 */     return (BillRegVO)(new BaseDAO()).executeQuery(sql, new BeanProcessor(BillRegVO.class));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettlementAggVO updateSettle(SettlementAggVO aggvo, String[] headField, String[] bodyfield) throws BusinessException {
/* 1185 */     BaseDAO basedao = new BaseDAO();
/*      */     
/* 1187 */     aggvo.getParentVO().setStatus(1);
/*      */     
/* 1189 */     if (headField != null) {
/*      */       
/* 1191 */       basedao.updateVO((SettlementHeadVO)aggvo.getParentVO(), headField);
/*      */     } else {
/* 1193 */       basedao.updateVO((SettlementHeadVO)aggvo.getParentVO(), new String[] { "dr" });
/*      */     } 
/*      */     
/* 1196 */     if (bodyfield != null) {
/* 1197 */       for (SettlementBodyVO body : (SettlementBodyVO[])aggvo
/* 1198 */         .getChildrenVO()) {
/* 1199 */         body.setStatus(1);
/*      */       }
/* 1201 */       basedao.updateVOArray((SettlementBodyVO[])aggvo.getChildrenVO(), bodyfield);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1207 */     return ((ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class)).querySettlementAggVOsByPks(new String[] {
/* 1208 */           ((SettlementHeadVO)aggvo.getParentVO())
/* 1209 */           .getPrimaryKey()
/*      */         })[0];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBodySettleStatus(SettleStatus oldstatus, SettleStatus newstatus, SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1226 */     BaseDAO baseDAO = new BaseDAO();
/* 1227 */     List<String> field = CmpUtils.makeList();
/*      */ 
/*      */ 
/*      */     
/* 1231 */     Set<String> headPks = new HashSet<String>();
/* 1232 */     List<String> pks = CmpUtils.makeList();
/*      */     
/* 1234 */     for (SettlementBodyVO settlementBodyVO : bodyVOs) {
/* 1235 */       pks.add(settlementBodyVO.getPk_detail());
/* 1236 */       settlementBodyVO.setStatus(1);
/* 1237 */       settlementBodyVO.setSettlestatus(Integer.valueOf(newstatus.getStatus()));
/* 1238 */       field.add("settlestatus");
/* 1239 */       headPks.add(settlementBodyVO.getPk_settlement());
/*      */     } 
/*      */ 
/*      */     
/* 1243 */     SettlementAggVO[] aggvos = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks((String[])headPks.toArray(new String[0]));
/*      */     
/* 1245 */     SettlePubSecurityUtil.verifySign(SettlePubSecurityUtil.CONST_ACTIONCODE_SIGN, false, aggvos);
/*      */     
/* 1247 */     SettleUtils.handleBodys4Account(bodyVOs, true);
/*      */     
/* 1249 */     if (oldstatus.equals(SettleStatus.PAYFAIL) && newstatus
/* 1250 */       .equals(SettleStatus.PRECHANGE)) {
/*      */       
/* 1252 */       SettlePubSecurityUtil.verifySign(SettlePubSecurityUtil.CONST_ACTIONCODE_CHANGESAVE, false, aggvos);
/*      */ 
/*      */     
/*      */     }
/* 1256 */     else if ((oldstatus.equals(SettleStatus.PRECHANGE) && newstatus
/* 1257 */       .equals(SettleStatus.NONESETTLE)) || (oldstatus
/* 1258 */       .equals(SettleStatus.NONESETTLE) && newstatus
/* 1259 */       .equals(SettleStatus.PRECHANGE))) {
/*      */ 
/*      */ 
/*      */       
/* 1263 */       field.addAll(CmpUtils.covertArraysToList(this.settleChangeField));
/*      */ 
/*      */       
/* 1266 */       List<SettlementBodyVO> oldBodys = CmpInterfaceProxy.INSTANCE.getQueryService().queryBodysByDetailPKs(pks);
/* 1267 */       for (SettlementBodyVO oldbody : oldBodys) {
/* 1268 */         if (oldstatus.getStatus() == oldbody.getSettlestatus().intValue()) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/* 1273 */         ExceptionHandler.createandthrowException(
/* 1274 */             NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set_0", "03607set-0861"));
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1283 */       BankAccountBookVOChangeUtil.getInstance().writeBankAccByBankAcc(OperateTypeEnum.UNAPPLY, (SettlementBodyVO[])oldBodys
/*      */           
/* 1285 */           .toArray(new SettlementBodyVO[0]));
/*      */       
/* 1287 */       BankAccountBookVOChangeUtil.getInstance().writeBankAccByBankAcc(OperateTypeEnum.APPLY, bodyVOs);
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1292 */     else if (oldstatus.equals(SettleStatus.PRECHANGE) && newstatus
/* 1293 */       .equals(SettleStatus.PAYFAIL)) {
/*      */       
/* 1295 */       SettlePubSecurityUtil.verifySign(SettlePubSecurityUtil.CONST_ACTIONCODE_CHANGEUNAUDIT, false, aggvos);
/*      */ 
/*      */       
/* 1298 */       field.addAll(CmpUtils.covertArraysToList(this.settleChangeField));
/*      */     } 
/*      */ 
/*      */     
/* 1302 */     List<SettlementBodyVO> updateBodyvos = new ArrayList<SettlementBodyVO>();
/* 1303 */     SettlementHeadVO[] headvos = new SettlementHeadVO[aggvos.length];
/*      */     
/* 1305 */     SettlementAggVO[] newAggVOs = (SettlementAggVO[])aggvos.clone();
/* 1306 */     Map<String, SettlementBodyVO> curBodyMap = new HashMap<String, SettlementBodyVO>();
/* 1307 */     List<SettlementBodyVO> newBodyList = null;
/* 1308 */     for (SettlementBodyVO body : bodyVOs) {
/* 1309 */       curBodyMap.put(body.getPk_detail(), body);
/*      */     }
/* 1311 */     for (SettlementAggVO newAggvo : newAggVOs) {
/* 1312 */       newBodyList = CmpUtils.makeList();
/* 1313 */       for (SettlementBodyVO oldbodyVO : (SettlementBodyVO[])newAggvo
/* 1314 */         .getChildrenVO()) {
/* 1315 */         if (null != curBodyMap.get(oldbodyVO.getPk_detail())) {
/* 1316 */           newBodyList.add(curBodyMap.get(oldbodyVO.getPk_detail()));
/*      */         } else {
/* 1318 */           newBodyList.add(oldbodyVO);
/*      */         } 
/*      */       } 
/* 1321 */       newAggvo.setChildrenVO((CircularlyAccessibleValueObject[])newBodyList.toArray(new SettlementBodyVO[0]));
/* 1322 */       updateBodyvos.addAll(newBodyList);
/*      */     } 
/*      */     
/* 1325 */     for (int i = 0; i < headvos.length; i++) {
/* 1326 */       SettlementAggVO settlementAggVO = aggvos[i];
/*      */       
/* 1328 */       SettlementHeadVO head = (SettlementHeadVO)settlementAggVO.getParentVO();
/* 1329 */       headvos[i] = head;
/*      */     } 
/*      */     
/* 1332 */     SettlePubSecurityUtil.signdata(newAggVOs);
/* 1333 */     field.add("code");
/*      */ 
/*      */     
/* 1336 */     SettleUtils.setSettleStatus(newAggVOs);
/*      */     
/* 1338 */     for (SettlementAggVO agggVo : newAggVOs) {
/* 1339 */       SettlePubSecurityUtil.addBillCaSignRecord(agggVo, SettlePubSecurityUtil.CONST_ACTIONCODE_CHANGEAUDIT, false);
/*      */     }
/*      */ 
/*      */     
/* 1343 */     baseDAO.updateVOArray((SuperVO[])updateBodyvos.toArray(new SettlementBodyVO[0]), (String[])field
/* 1344 */         .toArray(new String[0]));
/*      */     
/* 1346 */     baseDAO.updateVOArray(headvos, new String[] { "settlestatus", "code" });
/*      */ 
/*      */     
/* 1349 */     for (SettlementAggVO settlementAggVO : aggvos) {
/*      */       
/* 1351 */       SettlementHeadVO settlementHeadVO = (SettlementHeadVO)settlementAggVO.getParentVO();
/* 1352 */       if ("DS".equals(settlementHeadVO.getTradertypecode())) {
/* 1353 */         updateDfgzPayment(settlementAggVO);
/*      */       }
/*      */     } 
/*      */     
/* 1357 */     for (SettlementAggVO aggvo : aggvos)
/*      */     {
/* 1359 */       SettleUtils.deleteObmLog(aggvo);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updateDfgzPayment(SettlementAggVO settlementAggVO) throws BusinessException {
/* 1367 */     SettlementHeadVO settlementHeadVO = (SettlementHeadVO)settlementAggVO.getParentVO();
/*      */     
/* 1369 */     SettlementBodyVO[] bodyVOs = (SettlementBodyVO[])settlementAggVO.getChildrenVO();
/*      */     
/* 1371 */     String pk_cmppaybill = settlementHeadVO.getPk_busibill();
/*      */ 
/*      */     
/* 1374 */     ICmpPayBillPubQueryService cmpPayBillPubQueryService = (ICmpPayBillPubQueryService)NCLocator.getInstance().lookup(ICmpPayBillPubQueryService.class);
/*      */ 
/*      */     
/* 1377 */     BillAggVO[] billAggVOs = cmpPayBillPubQueryService.findBillByPrimaryKey(new String[] { pk_cmppaybill });
/* 1378 */     if (billAggVOs == null || billAggVOs.length < 1) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1383 */     BillDetailVO[] billDetailVOs = (BillDetailVO[])billAggVOs[0].getAllChildrenVO();
/*      */     
/* 1385 */     for (SettlementBodyVO body : bodyVOs) {
/* 1386 */       String pk_cmppaybilldail = body.getPk_billdetail();

/* 1387 */     for (BillDetailVO billDetailVO : billDetailVOs) {
/* 1388 */         if (pk_cmppaybilldail != null && pk_cmppaybilldail
/* 1389 */           .equals(billDetailVO
/* 1390 */             .getPrimaryKey())) {
/*      */           
/* 1392 */           String pk_account = body.getPk_account();
/* 1393 */           DfgzPaymentVO pvo = new DfgzPaymentVO();
/*      */           
/* 1395 */           pvo.setYurref(billDetailVO.getPk_upperbill_detail());
/*      */           
/* 1397 */           pvo.setPk_dbtacc(pk_account);
/*      */ 
/*      */ 
/*      */           
/* 1401 */           IBankaccPubQueryService bankaccPubQueryService = (IBankaccPubQueryService)NCLocator.getInstance().lookup(IBankaccPubQueryService.class);
/*      */ 
/*      */           
/* 1404 */           Map<String, BankAccSubVO> pk_accountsMap = bankaccPubQueryService.queryBankAccSubByPk(new String[] { "code", "pk_bankaccbas" }, new String[] { pk_account });
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1409 */           BankAccSubVO bankAccSubVO = (BankAccSubVO)pk_accountsMap.get(pk_account);
/* 1410 */           pvo.setDbtacc(bankAccSubVO.getCode());
/*      */           
/* 1412 */           pvo.setPaytotalnum(billDetailVO.getPay_primal());
/* 1413 */           pvo.setC_ccynbr(billDetailVO.getPk_currtype());
/*      */           
/* 1415 */           pvo.setBusnar(billDetailVO.getMemo());
/*      */           
/* 1417 */           pvo.setSrcvbillno(billDetailVO.getBill_no());
/* 1418 */           pvo.setSrcpkid(billDetailVO.getPrimaryKey());
/* 1419 */           pvo.setSrcbilltype(billDetailVO.getBill_type());
/* 1420 */           pvo.setSrcfuncode("36070PBM");
/* 1421 */           pvo.setSrcsystem(DFGZConst.SYS_CMP);
/*      */           
/* 1423 */           pvo.setCreateid(DataUtil.getCurrentUser());
/* 1424 */           pvo.setPk_org(billDetailVO.getPk_org());
/* 1425 */           pvo.setPk_group(billDetailVO.getPk_group());
/*      */ 
/*      */           
/* 1428 */           IDFGZBillStateChange dFGZBillStateChange = (IDFGZBillStateChange)NCLocator.getInstance().lookup(IDFGZBillStateChange.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1434 */           BankAccbasVO bankaccbasvo = ((IBankaccPubQueryService)NCLocator.getInstance().lookup(IBankaccPubQueryService.class)).queryAccbasInfByAccID(bankAccSubVO
/* 1435 */               .getPk_bankaccbas(), new String[] { "isinneracc" });
/*      */ 
/*      */           
/* 1438 */           if (bankaccbasvo != null && UFBoolean.TRUE
/* 1439 */             .equals(bankaccbasvo
/* 1440 */               .getIsinneracc())) {
/*      */             
/* 1442 */             dFGZBillStateChange
/* 1443 */               .updateDfgzBillStateToAvailableNoCheck(pvo);
/*      */           } else {
/* 1445 */             dFGZBillStateChange.updateDfgzBillStateToAvailable(pvo);
/*      */           } 
/*      */         }  }
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String[] settleChangeField = { 
/* 1459 */       "pk_balatype", "pk_account", "pk_bank", "pk_cashaccount", "memo", "pk_notetype", "pk_notenumber", "notenumber", "fundformcode", "accountnum", "direct_ecds" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private FireEvent event;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettlementAggVO[] handleBodySettle(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1470 */     checkIsEuropeAndCanBodySettle(bodyVOs);
/*      */     
/* 1472 */     CMPValidate.validate(bodyVOs[0].getPk_org());
/*      */     
/* 1474 */     Set<String> pk_settlements = new HashSet<String>();
/* 1475 */     for (SettlementBodyVO settlementBodyVO : bodyVOs) {
/* 1476 */       settlementBodyVO.setHandworkedupdate(UFBoolean.TRUE);
/* 1477 */       pk_settlements.add(settlementBodyVO.getPk_settlement());
/*      */     } 
/*      */     
/* 1480 */     SettlementAggVO[] settlementAggVOs = bodySettle(bodyVOs);
/* 1481 */     List<SettlementHeadVO> headlist = new ArrayList<SettlementHeadVO>();
/* 1482 */     SettleUtils.setSettleStatus(settlementAggVOs);
/*      */     
/* 1484 */     for (SettlementAggVO settlementAggVO : settlementAggVOs) {
/*      */ 
/*      */       
/* 1487 */       SettlementHeadVO settlementHeadVO = (SettlementHeadVO)settlementAggVO.getParentVO();
/* 1488 */       headlist.add(settlementHeadVO);
/*      */       
/* 1490 */       if (Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()).equals(settlementHeadVO
/* 1491 */           .getSettlestatus())) {
/*      */ 
/*      */         
/* 1494 */         CMPExecStatus cmpExecStatus = null;
/* 1495 */         String djdl = SettleUtils.getDjdlByPk_tradetype(settlementHeadVO
/* 1496 */             .getPk_tradetype(), settlementHeadVO
/* 1497 */             .getPk_group());
/* 1498 */         if ("fj".equals(djdl) || "fk".equals(djdl) || "bx".equals(djdl)) {
/* 1499 */           cmpExecStatus = CMPExecStatus.PayFinish;
/* 1500 */         } else if ("sj".equals(djdl) || "sk".equals(djdl)) {
/* 1501 */           cmpExecStatus = CMPExecStatus.ReciveFinish;
/* 1502 */         } else if ("hj".equals(djdl)) {
/* 1503 */           cmpExecStatus = CMPExecStatus.PayFinish;
/*      */         } 
/*      */ 
/*      */         
/* 1507 */         BusiInfo busiInfo = SettleUtils.convertSettleBeanToBusiInfo(settlementAggVO);
/* 1508 */         busiInfo.setOperator(settlementHeadVO.getLastupdater());
/* 1509 */         busiInfo.setOperatorDate(settlementHeadVO.getLastupdatedate());
/*      */         
/* 1511 */         busiInfo.setBudgetCheck(settlementAggVO.isBudgetCheck());
/* 1512 */         busiInfo.setHasZjjhCheck(settlementAggVO.isHasZjjhCheck());
/* 1513 */         busiInfo.setSettle(settlementAggVO.isSettle());
/* 1514 */         busiInfo.setJkCheck(settlementAggVO.isErmCheck());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1520 */         SettleNotifyPayTypeBusiBillServiceProxy busiServcie = SettleNotifyPayTypeBusiBillServiceProxy.getService(settlementHeadVO.getPk_group(), settlementHeadVO.getPk_tradetype());
/*      */ 
/*      */         
/* 1523 */         busiServcie.execStatuesChange(busiInfo, cmpExecStatus);
/*      */       } 
/*      */     } 
/*      */     
/* 1527 */     (new BaseDAO()).updateVOList(headlist);
/* 1528 */     return CmpInterfaceProxy.INSTANCE.getQueryService()
/* 1529 */       .querySettlementAggVOsByPks((String[])pk_settlements
/* 1530 */         .toArray(new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettlementAggVO[] handleCancelBodySettle(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1538 */     checkIsEuropeAndCanBodyUnSettle(bodyVOs);
/*      */     
/* 1540 */     CMPValidate.validate(bodyVOs[0].getPk_org(), bodyVOs[0].getTallydate());
/*      */     
/* 1542 */     Set<String> pk_settlements = new HashSet<String>();
/* 1543 */     for (SettlementBodyVO settlementBodyVO : bodyVOs) {
/* 1544 */       settlementBodyVO.setHandworkedupdate(null);
/* 1545 */       pk_settlements.add(settlementBodyVO.getPk_settlement());
/*      */     } 
/* 1547 */     SettlementAggVO[] settlementAggVOs = bodyUnSettle(bodyVOs);
/* 1548 */     List<SettlementHeadVO> headlist = new ArrayList<SettlementHeadVO>();
/* 1549 */     SettleUtils.setSettleStatus(settlementAggVOs);
/*      */     
/* 1551 */     for (SettlementAggVO aggVO : settlementAggVOs) {
/*      */ 
/*      */       
/* 1554 */       SettleUtils.convertSettlementAggVOToBusiBodyPkToSettleBodyListMap(new SettlementAggVO[] { aggVO });
/* 1555 */       BusiInfo busiInfo = SettleUtils.convertSettleBeanToBusiInfo(aggVO);
/* 1556 */       busiInfo.setBudgetCheck(aggVO.isBudgetCheck());
/* 1557 */       busiInfo.setHasZjjhCheck(aggVO.isHasZjjhCheck());
/* 1558 */       busiInfo.setSettle(aggVO.isSettle());
/* 1559 */       busiInfo.setJkCheck(aggVO.isErmCheck());
/*      */       
/* 1561 */       SettlementHeadVO headVO = (SettlementHeadVO)aggVO.getParentVO();
/* 1562 */       headlist.add(headVO);
/*      */ 
/*      */ 
/*      */       
/* 1566 */       SettleNotifyPayTypeBusiBillServiceProxy busiServcie = SettleNotifyPayTypeBusiBillServiceProxy.getService(headVO.getPk_group(), headVO.getPk_tradetype());
/*      */ 
/*      */       
/* 1569 */       SettlementHeadVO headvo = (SettlementHeadVO)aggVO.getParentVO();
/* 1570 */       if (headvo.getSettlestatus() != null) {
/* 1571 */         CMPExecStatus execstatus = CMPExecStatus.UNPayed;
/*      */         
/* 1573 */         if (headvo.getSettlestatus().equals(
/* 1574 */             Integer.valueOf(SettleStatus.PAYING.getStatus())) || headvo
/* 1575 */           .getSettlestatus().equals(
/* 1576 */             Integer.valueOf(SettleStatus.CHANGEING.getStatus()))) {
/* 1577 */           execstatus = CMPExecStatus.Paying;
/*      */         }
/* 1579 */         else if (headvo.getSettlestatus().equals(
/* 1580 */             Integer.valueOf(SettleStatus.RECEIVING.getStatus()))) {
/* 1581 */           execstatus = CMPExecStatus.Reciving;
/* 1582 */         } else if (headvo.getSettlestatus().equals(
/* 1583 */             Integer.valueOf(SettleStatus.PAYFAIL.getStatus()))) {
/* 1584 */           execstatus = CMPExecStatus.PayFail;
/* 1585 */         } else if (headvo.getSettlestatus().equals(
/* 1586 */             Integer.valueOf(SettleStatus.RECEVICEFAIL.getStatus()))) {
/* 1587 */           execstatus = CMPExecStatus.ReciveFail;
/* 1588 */         } else if (headvo.getSettlestatus().equals(
/* 1589 */             Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus())) && headvo
/* 1590 */           .getDirection().equals(SettleEnumCollection.Direction.PAY.VALUE)) {
/* 1591 */           execstatus = CMPExecStatus.PayFinish;
/* 1592 */         } else if (headvo.getSettlestatus().equals(
/* 1593 */             Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus())) && headvo
/* 1594 */           .getDirection().equals(SettleEnumCollection.Direction.REC.VALUE)) {
/* 1595 */           execstatus = CMPExecStatus.ReciveFinish;
/*      */         } 
/* 1597 */         busiServcie.execStatuesChange(busiInfo, execstatus);
/*      */       } 
/*      */     } 
/*      */     
/* 1601 */     (new BaseDAO()).updateVOList(headlist);
/*      */     
/* 1603 */     return CmpInterfaceProxy.INSTANCE.getQueryService()
/* 1604 */       .querySettlementAggVOsByPks((String[])pk_settlements
/* 1605 */         .toArray(new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private SettlementAggVO[] bodySettle(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1611 */     SettleUtils.addDynamicLock(bodyVOs);
/* 1612 */     SettleUtils.checkTs(bodyVOs);
/*      */     
/* 1614 */     Set<String> pk_settlements = new HashSet<String>();
/* 1615 */     for (SettlementBodyVO settlementBodyVO : bodyVOs) {
/* 1616 */       pk_settlements.add(settlementBodyVO.getPk_settlement());
/* 1617 */       settlementBodyVO.setStatus(1);
/* 1618 */       settlementBodyVO.setTallydate(DataUtil.getUFDate());
/* 1619 */       settlementBodyVO.setSettlestatus(Integer.valueOf(SettleStatus.SUCCESSSETTLE
/* 1620 */             .getStatus()));
/*      */     } 
/*      */     
/* 1623 */     BaseDAO baseDAO = new BaseDAO();
/* 1624 */     baseDAO.updateVOArray(bodyVOs);
/*      */     
/* 1626 */     if (Integer.valueOf(1).equals(bodyVOs[0].getDirection()))
/*      */     {
/*      */       
/* 1629 */       BankAccountBookVOChangeUtil.getInstance().writeBankAccByBankAcc(OperateTypeEnum.UNAPPLY, bodyVOs);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1634 */     return CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks((String[])pk_settlements
/* 1635 */         .toArray(new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SettlementBodyVO bodySettle(SettlementBodyVO settlementBodyVO) throws BusinessException {
/* 1644 */     SettlementAggVO[] settlementAggVOs = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks(new String[] { settlementBodyVO
/* 1645 */           .getPk_settlement() });
/*      */     
/* 1647 */     SettleUtils.addDynamicLock(settlementAggVOs);
/* 1648 */     SettleUtils.checkTs(settlementAggVOs);
/*      */     
/* 1650 */     getEvent().fireBeforeSettle(settlementAggVOs);
/*      */     
/* 1652 */     settlementBodyVO.setTallydate(DataUtil.getUFDate());
/* 1653 */     settlementBodyVO.setStatus(1);
/* 1654 */     settlementBodyVO
/* 1655 */       .setSettlestatus(Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()));
/*      */     
/* 1657 */     if (SettleEnumCollection.Direction.PAY.VALUE.equals(settlementBodyVO.getDirection()))
/*      */     {
/*      */       
/* 1660 */       BankAccountBookVOChangeUtil.getInstance().writeBankAccByBankAcc(OperateTypeEnum.UNAPPLY, new SettlementBodyVO[] { settlementBodyVO });
/*      */     }
/*      */ 
/*      */     
/* 1664 */     BaseDAO basedao = new BaseDAO();
/*      */     
/* 1666 */     basedao.updateVO(settlementBodyVO);
/*      */     
/* 1668 */     for (SettlementBodyVO body : (SettlementBodyVO[])settlementAggVOs[0]
/* 1669 */       .getChildrenVO()) {
/* 1670 */       if (body.getPrimaryKey().equals(settlementBodyVO.getPrimaryKey())) {
/*      */         
/* 1672 */         body.setTallydate(DataUtil.getUFDate());
/* 1673 */         body.setStatus(1);
/* 1674 */         body.setSettlestatus(Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()));
/*      */       } 
/*      */     } 
/* 1677 */     SettleUtils.setSettleStatus(settlementAggVOs);
/* 1678 */     if (SettleStatus.SUCCESSSETTLE.getStatus() == ((SettlementHeadVO)settlementAggVOs[0]
/* 1679 */       .getParentVO()).getSettlestatus().intValue()) {
/*      */       
/* 1681 */       ((SettlementHeadVO)settlementAggVOs[0].getParentVO())
/* 1682 */         .setSettledate(DataUtil.getUFDate());
/* 1683 */       settlementAggVOs[0].getParentVO().setStatus(1);
/* 1684 */       basedao.updateVO((SettlementHeadVO)settlementAggVOs[0]
/* 1685 */           .getParentVO());
/* 1686 */       updateSettleInfo(Boolean.valueOf(true), settlementAggVOs);
/*      */     } 
/*      */ 
/*      */     
/* 1690 */     getEvent().fireAfterSettle(settlementAggVOs);
/* 1691 */     return settlementBodyVO;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private SettlementAggVO[] bodyUnSettle(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1697 */     SettleUtils.addDynamicLock(bodyVOs);
/* 1698 */     SettleUtils.checkTs(bodyVOs);
/*      */     
/* 1700 */     Set<String> pk_settlements = new HashSet<String>();
/* 1701 */     for (SettlementBodyVO settlementBodyVO : bodyVOs) {
/* 1702 */       pk_settlements.add(settlementBodyVO.getPk_settlement());
/*      */     }
/* 1704 */     setBodyUnSettleStatus(bodyVOs);
/* 1705 */     BaseDAO baseDAO = new BaseDAO();
/* 1706 */     baseDAO.updateVOArray(bodyVOs);
/*      */     
/* 1708 */     if (Integer.valueOf(1).equals(bodyVOs[0].getDirection()))
/*      */     {
/* 1710 */       BankAccountBookVOChangeUtil.getInstance().writeBankAccByBankAcc(OperateTypeEnum.APPLY, bodyVOs);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1715 */     return CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks((String[])pk_settlements
/* 1716 */         .toArray(new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setBodyUnSettleStatus(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1724 */     IBalaTypePubService balaTypePubService = InterfaceLocator.getInterfaceLocator().getBalaQry();
/* 1725 */     Map<String, BalaTypeVO> map = new HashMap<String, BalaTypeVO>();
/* 1726 */     for (SettlementBodyVO settlementBodyVO : bodyVOs) {
/* 1727 */       settlementBodyVO.setStatus(1);
/* 1728 */       String pk_balatype = settlementBodyVO.getPk_balatype();
/* 1729 */       BalaTypeVO btvo = (BalaTypeVO)map.get(pk_balatype);
/* 1730 */       if (btvo == null) {
/* 1731 */         btvo = balaTypePubService.findBalaTypeVOByPK(pk_balatype);
/* 1732 */         map.put(pk_balatype, btvo);
/*      */       } 
/*      */       
/* 1735 */       if (btvo != null) {
/*      */         
/* 1737 */         if (UFBoolean.TRUE.equals(btvo.getDirectincome()) || UFBoolean.TRUE
/* 1738 */           .equals(btvo.getConsignpay()))
/*      */         {
/* 1740 */           Integer settleStatus = Integer.valueOf(SettleStatus.PAYING.getStatus());
/*      */           
/* 1742 */           if (settlementBodyVO.getDirection().intValue() == 0) {
/* 1743 */             settleStatus = Integer.valueOf(SettleStatus.RECEIVING.getStatus());
/*      */           }
/*      */           
/* 1746 */           settlementBodyVO.setSettlestatus(settleStatus);
/*      */         }
/* 1748 */         else if (UFBoolean.TRUE.equals(btvo.getCash()))
/*      */         {
/*      */           
/* 1751 */           ExceptionHandler.createandthrowException(
/* 1752 */               NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0185"));
/*      */ 
/*      */         
/*      */         }
/*      */         else
/*      */         {
/*      */           
/* 1759 */           settlementBodyVO.setSettlestatus(Integer.valueOf(SettleStatus.NONESETTLE
/* 1760 */                 .getStatus()));
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1765 */         ExceptionHandler.createandthrowException(
/* 1766 */             NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0187"));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private SettlementBodyVO bodyUnSettle(SettlementBodyVO settlementBodyVO) throws BusinessException {
/* 1779 */     SettlementAggVO[] settlementAggVOs = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks(new String[] { settlementBodyVO
/* 1780 */           .getPk_settlement() });
/* 1781 */     SettleUtils.addDynamicLock(settlementAggVOs);
/* 1782 */     SettleUtils.checkTs(settlementAggVOs);
/*      */     
/* 1784 */     getEvent().fireBeforeReverseSettle(settlementAggVOs);
/*      */     
/* 1786 */     settlementBodyVO.setTallydate(null);
/* 1787 */     settlementBodyVO.setStatus(1);
/*      */     
/* 1789 */     if (SettleEnumCollection.Direction.PAY.VALUE.equals(settlementBodyVO.getDirection())) {
/* 1790 */       settlementBodyVO.setSettlestatus(Integer.valueOf(SettleStatus.PAYING.getStatus()));
/*      */       
/* 1792 */       BankAccountBookVOChangeUtil.getInstance().writeBankAccByBankAcc(OperateTypeEnum.APPLY, new SettlementBodyVO[] { settlementBodyVO });
/*      */     } else {
/*      */       
/* 1795 */       settlementBodyVO
/* 1796 */         .setSettlestatus(Integer.valueOf(SettleStatus.RECEIVING.getStatus()));
/*      */     } 
/*      */     
/* 1799 */     BaseDAO basedao = new BaseDAO();
/*      */     
/* 1801 */     basedao.updateVO(settlementBodyVO);
/*      */     
/* 1803 */     for (SettlementBodyVO body : (SettlementBodyVO[])settlementAggVOs[0]
/* 1804 */       .getChildrenVO()) {
/* 1805 */       if (body.getPrimaryKey().equals(settlementBodyVO.getPrimaryKey())) {
/* 1806 */         body.setTallydate(null);
/* 1807 */         body.setStatus(1);
/* 1808 */         body.setSettlestatus(settlementBodyVO.getSettlestatus());
/*      */         break;
/*      */       } 
/*      */     } 
/* 1812 */     if (SettleStatus.SUCCESSSETTLE.getStatus() == ((SettlementHeadVO)settlementAggVOs[0]
/* 1813 */       .getParentVO()).getSettlestatus().intValue()) {
/* 1814 */       SettleUtils.setSettleStatus(settlementAggVOs);
/* 1815 */       ((SettlementHeadVO)settlementAggVOs[0].getParentVO())
/* 1816 */         .setSettledate(null);
/* 1817 */       settlementAggVOs[0].getParentVO().setStatus(1);
/* 1818 */       basedao.updateVO((SettlementHeadVO)settlementAggVOs[0]
/* 1819 */           .getParentVO());
/*      */       
/* 1821 */       updateSettleInfo(Boolean.valueOf(false), settlementAggVOs);
/*      */     } 
/*      */ 
/*      */     
/* 1825 */     getEvent().fireAfterReverseSettle(settlementAggVOs);
/* 1826 */     return settlementBodyVO;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkIsEuropeAndCanBodySettle(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1831 */     UFBoolean isEurope = SysInit.getParaBoolean(bodyVOs[0].getPk_org(), "CMP52");
/*      */ 
/*      */     
/* 1834 */     if (UFBoolean.TRUE.equals(isEurope)) {
/*      */ 
/*      */       
/* 1837 */       IBalaTypePubService balaTypePubService = InterfaceLocator.getInterfaceLocator().getBalaQry();
/* 1838 */       Map<String, BalaTypeVO> map = new HashMap<String, BalaTypeVO>();
/*      */       
/* 1840 */       for (SettlementBodyVO settlementBodyVO : bodyVOs)
/*      */       {
/* 1842 */         String pk_balatype = settlementBodyVO.getPk_balatype();
/* 1843 */         BalaTypeVO btvo = (BalaTypeVO)map.get(pk_balatype);
/* 1844 */         if (btvo == null) {
/* 1845 */           btvo = balaTypePubService.findBalaTypeVOByPK(pk_balatype);
/* 1846 */           map.put(pk_balatype, btvo);
/*      */         } 
/*      */         
/* 1849 */         if (btvo != null)
/*      */         {
/* 1851 */           if (UFBoolean.TRUE.equals(btvo.getDirectincome()) || UFBoolean.TRUE
/* 1852 */             .equals(btvo.getConsignpay()))
/*      */           {
/*      */             
/* 1855 */             if (!Integer.valueOf(SettleStatus.PAYING.getStatus()).equals(settlementBodyVO.getSettlestatus()) && 
/*      */               
/* 1857 */               !Integer.valueOf(SettleStatus.RECEIVING.getStatus()).equals(settlementBodyVO.getSettlestatus()))
/*      */             {
/* 1859 */               ExceptionHandler.createandthrowException(
/* 1860 */                   NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0184"));
/*      */ 
/*      */ 
/*      */             
/*      */             }
/*      */ 
/*      */           
/*      */           }
/* 1868 */           else if (UFBoolean.TRUE.equals(btvo.getCash()))
/*      */           {
/*      */             
/* 1871 */             ExceptionHandler.createandthrowException(
/* 1872 */                 NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0185"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/* 1880 */           else if (settlementBodyVO.getSettlestatus() != null && 
/*      */             
/* 1882 */             !Integer.valueOf(SettleStatus.NONESETTLE.getStatus()).equals(settlementBodyVO.getSettlestatus()))
/*      */           {
/* 1884 */             ExceptionHandler.createandthrowException(
/* 1885 */                 NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0186"));
/*      */ 
/*      */           
/*      */           }
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */           
/* 1897 */           ExceptionHandler.createandthrowException(
/* 1898 */               NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0116"));
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1909 */       ExceptionHandler.createandthrowException(
/* 1910 */           NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0116"));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkIsEuropeAndCanBodyUnSettle(SettlementBodyVO... bodyVOs) throws BusinessException {
/* 1921 */     UFBoolean isEurope = SysInit.getParaBoolean(bodyVOs[0].getPk_org(), "CMP52");
/*      */ 
/*      */     
/* 1924 */     if (UFBoolean.TRUE.equals(isEurope)) {
/*      */ 
/*      */       
/* 1927 */       IBalaTypePubService balaTypePubService = InterfaceLocator.getInterfaceLocator().getBalaQry();
/* 1928 */       Map<String, BalaTypeVO> map = new HashMap<String, BalaTypeVO>();
/*      */       
/* 1930 */       for (SettlementBodyVO settlementBodyVO : bodyVOs)
/*      */       {
/* 1932 */         String pk_balatype = settlementBodyVO.getPk_balatype();
/* 1933 */         BalaTypeVO btvo = (BalaTypeVO)map.get(pk_balatype);
/* 1934 */         if (btvo == null) {
/* 1935 */           btvo = balaTypePubService.findBalaTypeVOByPK(pk_balatype);
/* 1936 */           map.put(pk_balatype, btvo);
/*      */         } 
/*      */         
/* 1939 */         if (btvo != null)
/*      */         {
/* 1941 */           if (UFBoolean.TRUE.equals(btvo.getDirectincome()) || UFBoolean.TRUE
/* 1942 */             .equals(btvo.getConsignpay()))
/*      */           {
/*      */             
/* 1945 */             if (!Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()).equals(settlementBodyVO
/* 1946 */                 .getSettlestatus()))
/*      */             {
/* 1948 */               ExceptionHandler.createandthrowException(
/* 1949 */                   NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0188"));
/*      */ 
/*      */ 
/*      */             
/*      */             }
/*      */ 
/*      */           
/*      */           }
/* 1957 */           else if (UFBoolean.TRUE.equals(btvo.getCash()))
/*      */           {
/*      */             
/* 1960 */             ExceptionHandler.createandthrowException(
/* 1961 */                 NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0185"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/* 1969 */           else if (settlementBodyVO.getSettlestatus() != null && 
/*      */             
/* 1971 */             !Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()).equals(settlementBodyVO.getSettlestatus()))
/*      */           {
/* 1973 */             ExceptionHandler.createandthrowException(
/* 1974 */                 NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0189"));
/*      */ 
/*      */           
/*      */           }
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */           
/* 1986 */           ExceptionHandler.createandthrowException(
/* 1987 */               NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0116"));
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1998 */       ExceptionHandler.createandthrowException(
/* 1999 */           NCLangRes4VoTransl.getNCLangRes().getStrByID("3607set1_0", "03607set1-0116"));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettlementBodyVO handleInformerAssociate(String key, UFDouble money, int direction) throws BusinessException {
/* 2013 */     String condition = "(settlestatus is null or settlestatus <>'5') and bankrelated_code='" + key + "' and dr = 0 and direction=" + direction;
/*      */ 
/*      */     
/* 2016 */     if (SettleEnumCollection.Direction.PAY.VALUE.equals(Integer.valueOf(direction))) {
/*      */       
/* 2018 */       condition = condition + " and pay_last=" + money;
/*      */     } else {
/*      */       
/* 2021 */       condition = condition + " and receive=" + money;
/*      */     } 
/*      */ 
/*      */     
/* 2025 */     SettlementBodyVO[] list = (SettlementBodyVO[])CmpInterfaceProxy.INSTANCE.getQueryService().queryBeanByCondition(SettlementBodyVO.class, condition).toArray(new SettlementBodyVO[0]);
/* 2026 */     if (list == null || list.length != 1)
/*      */     {
/* 2028 */       return null;
/*      */     }
/*      */     
/* 2031 */     return bodySettle(list[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SettlementBodyVO handleInformerCancelAssociate(String key, UFDouble money, int direction) throws BusinessException {
/* 2041 */     int status = SettleStatus.SUCCESSSETTLE.getStatus();
/* 2042 */     String condition = "settlestatus=" + status + " and " + "bankrelated_code" + "='" + key + "' and dr = 0 and direction=" + direction;
/*      */ 
/*      */     
/* 2045 */     if (SettleEnumCollection.Direction.PAY.VALUE.equals(Integer.valueOf(direction))) {
/*      */       
/* 2047 */       condition = condition + " and pay_last=" + money;
/*      */     } else {
/*      */       
/* 2050 */       condition = condition + " and receive=" + money;
/*      */     } 
/*      */     
/* 2053 */     Collection<SettlementBodyVO> list = CmpInterfaceProxy.INSTANCE.getQueryService().queryBeanByCondition(SettlementBodyVO.class, condition);
/*      */     
/* 2055 */     if (list == null || list.size() != 1)
/*      */     {
/* 2057 */       return null;
/*      */     }
/* 2059 */     return this.bodyUnSettle(list.toArray(new SettlementBodyVO[0])[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updateSettleInfo(Boolean isSettle, SettlementAggVO... settlementAggVOs) throws BusinessException {
/* 2071 */     for (SettlementAggVO settlementAggVO : settlementAggVOs) {
/*      */ 
/*      */       
/* 2074 */       SettlementHeadVO settlementHeadVO = (SettlementHeadVO)settlementAggVO.getParentVO();
/* 2075 */       CMPExecStatus cmpExecStatus = null;
/* 2076 */       String djdl = SettleUtils.getDjdlByPk_tradetype(settlementHeadVO
/* 2077 */           .getPk_tradetype(), settlementHeadVO
/* 2078 */           .getPk_group());
/*      */       
/* 2080 */       if (Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()).equals(settlementHeadVO
/* 2081 */           .getSettlestatus())) {
/*      */ 
/*      */ 
/*      */         
/* 2085 */         if ("fj".equals(djdl) || "fk".equals(djdl) || "bx".equals(djdl)) {
/* 2086 */           cmpExecStatus = CMPExecStatus.PayFinish;
/* 2087 */         } else if ("sj".equals(djdl) || "sk".equals(djdl)) {
/* 2088 */           cmpExecStatus = CMPExecStatus.ReciveFinish;
/* 2089 */         } else if ("hj".equals(djdl)) {
/* 2090 */           cmpExecStatus = CMPExecStatus.PayFinish;
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 2095 */       else if ("fj".equals(djdl) || "fk".equals(djdl) || "bx".equals(djdl)) {
/* 2096 */         cmpExecStatus = CMPExecStatus.Paying;
/* 2097 */       } else if ("sj".equals(djdl) || "sk".equals(djdl)) {
/* 2098 */         cmpExecStatus = CMPExecStatus.Reciving;
/* 2099 */       } else if ("hj".equals(djdl)) {
/* 2100 */         cmpExecStatus = CMPExecStatus.Paying;
/*      */       } 
/*      */ 
/*      */       
/* 2104 */       BusiInfo busiInfo = SettleUtils.convertSettleBeanToBusiInfo(settlementAggVO);
/* 2105 */       busiInfo.setOperator(settlementHeadVO.getLastupdater());
/* 2106 */       busiInfo.setOperatorDate(settlementHeadVO.getLastupdatedate());
/*      */       
/* 2108 */       busiInfo.setBudgetCheck(settlementAggVO.isBudgetCheck());
/* 2109 */       busiInfo.setHasZjjhCheck(settlementAggVO.isHasZjjhCheck());
/* 2110 */       busiInfo.setSettle(settlementAggVO.isSettle());
/* 2111 */       busiInfo.setJkCheck(settlementAggVO.isErmCheck());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2117 */       SettleNotifyPayTypeBusiBillServiceProxy busiServcie = SettleNotifyPayTypeBusiBillServiceProxy.getService(settlementHeadVO.getPk_group(), settlementHeadVO.getPk_tradetype());
/*      */ 
/*      */       
/* 2120 */       busiServcie.execStatuesChange(busiInfo, cmpExecStatus);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public FireEvent getEvent() {
/* 2127 */     if (this.event == null) {
/* 2128 */       this.event = new FireEvent("11bc9226-f881-46bd-b3bf-38a70424ac8b");
/*      */     }
/* 2130 */     return this.event;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public List<EuropExportVO> exportEuropFile(SettlementAggVO[] settlementAggVOs) throws BusinessException {
/* 2136 */     Map<String, String> yurrefBillNos = getyurrefs(settlementAggVOs);
/*      */     
/* 2138 */     IEuropService europService = (IEuropService)NCLocator.getInstance().lookup(IEuropService.class);
/*      */ 
/*      */     
/* 2141 */     List<EuropExportVO> europExportVOs = europService.exportEuropFile(new ArrayList(yurrefBillNos.keySet()));
/* 2142 */     for (EuropExportVO europExportVO : europExportVOs) {
/* 2143 */       europExportVO.setvBillCode((String)yurrefBillNos.get(europExportVO
/* 2144 */             .getYurref()));
/*      */     }
/* 2146 */     return europExportVOs;
/*      */   }
/*      */ 
/*      */   
/*      */   private Map<String, String> getyurrefs(SettlementAggVO[] settlementAggVOs) throws BusinessException, DAOException {
/* 2151 */     Map<String, String> yurrefBillNos = new HashMap<String, String>();
/* 2152 */     for (SettlementAggVO settlementAggVO : settlementAggVOs) {
/*      */       
/* 2154 */       CircularlyAccessibleValueObject[] bodys = settlementAggVO.getChildrenVO();
/* 2155 */       for (CircularlyAccessibleValueObject body : bodys) {
/* 2156 */         SettlementBodyVO bodyVO = (SettlementBodyVO)body;
/* 2157 */         yurrefBillNos.put(bodyVO.getPrimaryKey(), bodyVO.getBillcode());
/*      */       } 
/*      */     } 
/*      */     
/* 2161 */     return yurrefBillNos;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public List<EuropExportVO> exportEuropFile(List<SettlementBodyVO> settlementBodyVOs) throws BusinessException {
/* 2167 */     Map<String, String> billnos = new HashMap<String, String>();
/* 2168 */     List<String> list = new ArrayList<String>();
/* 2169 */     for (SettlementBodyVO settlementBodyVO : settlementBodyVOs) {
/*      */       
/* 2171 */       list.add(settlementBodyVO.getPrimaryKey());
/* 2172 */       billnos.put(settlementBodyVO.getPrimaryKey(), settlementBodyVO
/* 2173 */           .getBillcode());
/*      */     } 
/*      */     
/* 2176 */     IEuropService europService = (IEuropService)NCLocator.getInstance().lookup(IEuropService.class);
/*      */     
/* 2178 */     List<EuropExportVO> europExportVOs = europService.exportEuropFile(list);
/* 2179 */     for (EuropExportVO europExportVO : europExportVOs) {
/* 2180 */       europExportVO.setvBillCode((String)billnos.get(europExportVO.getYurref()));
/*      */     }
/*      */     
/* 2183 */     return europExportVOs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2189 */   private BaseDAO dao = null;
/*      */ 
/*      */   
/*      */   public void dealDirectEcdsResult(HashMap<String, HashMap<String, String>> directEcdsResult) throws BusinessException {
/* 2193 */     for (Map.Entry<String, HashMap<String, String>> entry : directEcdsResult.entrySet()) {
/* 2194 */       String mapKey = (String)entry.getKey();
/* 2195 */       HashMap<String, String> mapValue = (HashMap)entry.getValue();
/*      */ 
/*      */       
/* 2198 */       Map<String, SettlementBodyVO> bodyMap = new HashMap<String, SettlementBodyVO>();
/* 2199 */       List<SettlementBodyVO> bodyList = new ArrayList<SettlementBodyVO>();
/*      */       
/* 2201 */       SettlementAggVO[] settleAggVOs = getSettlementQueryService().querySettlementAggVOsByPks(new String[] { mapKey });
/* 2202 */       if (settleAggVOs != null && settleAggVOs.length > 0) {
/* 2203 */         SettlementAggVO settleAggVO = settleAggVOs[0];
/*      */         
/* 2205 */         SettlementHeadVO headVO = (SettlementHeadVO)settleAggVO.getParentVO();
/*      */         
/* 2207 */         if (headVO.getSettlestatus() != null && headVO
/* 2208 */           .getSettlestatus().equals(
/* 2209 */             Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()))) {
/*      */           return;
/*      */         }
/*      */ 
/*      */         
/* 2214 */         addLock(settleAggVOs);
/*      */         
/*      */         try {
/* 2217 */           SettlementHeadVO oldHeadVO = (SettlementHeadVO)CmpUtils.CloneObj(headVO);
/*      */           
/* 2219 */           SettlementBodyVO[] bodys = (SettlementBodyVO[])settleAggVO.getChildrenVO();
/*      */           
/* 2221 */           headVO.setItems(bodys);
/* 2222 */           String flagD = "0";
/* 2223 */           for (SettlementBodyVO body : bodys) {
/* 2224 */             bodyMap.put(body.getPk_detail(), body);
/* 2225 */             if (body.getDirect_ecds().equals(UFBoolean.TRUE) && (
/* 2226 */               body.getSettlestatus() == null || body.getSettlestatus().intValue() == SettleStatus.NONESETTLE.getStatus() || body.getSettlestatus().intValue() == SettleStatus.PAYFAIL.getStatus())) {
/* 2227 */               flagD = "1";
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 2233 */           setBodySettleStatusByPaymentstatus(mapValue, bodyMap);
/*      */ 
/*      */           
/* 2236 */           List<SettlementAggVO> aggLst = SettleUtils.filterSettleInfo4NetSettleFlagUnSettle(settleAggVOs);
/* 2237 */           if ("1".equals(flagD) && aggLst.size() == 0) {
/* 2238 */             setHeadSettleStatus(headVO);
/*      */           }
/*      */           
/* 2241 */           if (headVO.getSettlestatus() != null && headVO
/* 2242 */             .getSettlestatus().equals(
/* 2243 */               Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()))) {
/*      */             
/* 2245 */             UFDate date = new UFDate();
/* 2246 */             String pk_user = AppContext.getInstance().getPkUser();
/* 2247 */             headVO.setSettledate(date);
/* 2248 */             headVO.setPk_executor(pk_user);
/* 2249 */             headVO.setSettletype(Integer.valueOf(SettleType.E_BANK.getStatus()));
/* 2250 */             if (headVO.getIsbusieffect() == null || headVO
/* 2251 */               .getIsbusieffect().equals(UFBoolean.FALSE)) {
/*      */               
/* 2253 */               headVO.setEffectstatus(Integer.valueOf(BusiStatus.Effet
/* 2254 */                     .getBillStatusSubKind()));
/* 2255 */               headVO.setIsbusieffect(UFBoolean.TRUE);
/*      */             } 
/*      */             
/* 2258 */             headVO.setLastupdatedate(date);
/* 2259 */             headVO.setLastupdater(pk_user);
/* 2260 */             SettleUtils.fillSettlementVOInfo(date, "tallydate", bodys);
/*      */           } 
/*      */ 
/*      */           
/* 2264 */           notifyBusi(settleAggVO);
/* 2265 */           if (headVO.getSettlestatus() != null && headVO
/* 2266 */             .getSettlestatus().equals(
/* 2267 */               Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()))) {
/* 2268 */             if (oldHeadVO.getIsbusieffect() == null || oldHeadVO
/* 2269 */               .getIsbusieffect().equals(UFBoolean.FALSE)) {
/*      */ 
/*      */               
/* 2272 */               headVO.setEffectstatus(Integer.valueOf(BusiStatus.Effet
/* 2273 */                     .getBillStatusSubKind()));
/* 2274 */               headVO.setIsbusieffect(UFBoolean.TRUE);
/*      */               
/* 2276 */               updateBusiInfo(headVO, oldHeadVO);
/*      */             } 
/*      */           }
/* 2279 */           for (int i = 0; i < headVO.getItems().length; i++) {
/* 2280 */             bodyList.add(headVO.getItems()[i]);
/*      */           }
/*      */ 
/*      */           
/* 2284 */           getBaseDAO().updateVO(headVO);
/* 2285 */           getBaseDAO().updateVOList(bodyList);
/*      */ 
/*      */           
/* 2288 */           notifyPlugin(headVO);
/* 2289 */         } catch (BusinessException e) {
/* 2290 */           Logger.error(e.getMessage(), e);
/* 2291 */           throw new RuntimeException(e);
/* 2292 */         } catch (Exception e) {
/* 2293 */           Logger.error(e.getMessage(), e);
/* 2294 */           throw new RuntimeException(e);
/*      */         } finally {
/*      */           try {
/* 2297 */             unLock(settleAggVOs);
/* 2298 */           } catch (BusinessException e) {
/* 2299 */             Logger.error(e.getMessage(), e);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notifyPlugin(SettlementHeadVO headVO) throws BusinessException {
/* 2321 */     if (headVO.getSettlestatus() != null && headVO
/* 2322 */       .getSettlestatus().equals(
/* 2323 */         Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()))) {
/* 2324 */       String pk_settlement = headVO.getPk_settlement();
/*      */       
/* 2326 */       SettlementAggVO[] newSettleAggs = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks(new String[] { pk_settlement });
/*      */ 
/*      */       
/* 2329 */       SettlementAggVO aggVO = newSettleAggs[0];
/* 2330 */       aggVO.setParentVO(headVO);
/*      */ 
/*      */       
/* 2333 */       BankAccountBookVOChangeUtil.getInstance().wirteBankAccByBankAcc(OperateTypeEnum.USE, new SettlementAggVO[] { aggVO });
/*      */ 
/*      */       
/* 2336 */       fireEvent(CMPExecStatus.Paying, CMPExecStatus.PayFinish, aggVO, false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fireEvent(CMPExecStatus before, CMPExecStatus now, SettlementAggVO info, boolean isBefore) throws BusinessException {
/* 2353 */     Object[] object = new Object[2];
/* 2354 */     object[0] = now;
/* 2355 */     object[1] = info;
/*      */     
/* 2357 */     if (isBefore) {
/* 2358 */       getEvent().fireBeforeStatusChange(object);
/*      */     } else {
/* 2360 */       getEvent().fireAfterStatusChange(object);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updateBusiInfo(SettlementHeadVO newhead, SettlementHeadVO oldHead) throws BusinessException {
/* 2383 */     SettleNotifyPayTypeBusiBillServiceProxy busiServcie = SettleNotifyPayTypeBusiBillServiceProxy.getService(newhead.getPk_group(), newhead.getPk_tradetype());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2388 */     if (!SettleUtils.isObjEqual(oldHead.getEffectstatus(), 
/* 2389 */         Integer.valueOf(BusiStatus.Effet.getBillStatusSubKind())) && 
/* 2390 */       SettleUtils.isObjEqual(newhead.getBusistatus(), 
/* 2391 */         Integer.valueOf(BusiStatus.Sign.getBillStatusKind()))) {
/* 2392 */       SettlementAggVO aggvo = new SettlementAggVO();
/* 2393 */       aggvo.setParentVO(newhead);
/* 2394 */       BusiInfo busiInfo = SettleUtils.convertSettleBeanToBusiInfo(aggvo);
/* 2395 */       busiInfo.setOperator(newhead.getLastupdater());
/* 2396 */       busiInfo.setOperatorDate(newhead.getLastupdatedate());
/*      */ 
/*      */       
/* 2399 */       BusiStateTrans effectTrans = new BusiStateTrans();
/*      */       
/* 2401 */       effectTrans.setFrom(BusiStatus.Sign);
/* 2402 */       effectTrans.setTo(BusiStatus.Effet);
/* 2403 */       busiServcie.effectStateChange(busiInfo, effectTrans);
/*      */ 
/*      */       
/* 2406 */       CMPExecStatus cmpExecStatus = null;
/* 2407 */       String djdl = SettleUtils.getDjdlByPk_tradetype(newhead
/* 2408 */           .getPk_tradetype(), newhead.getPk_group());
/* 2409 */       if ("fj".equals(djdl) || "fk".equals(djdl) || "bx".equals(djdl)) {
/* 2410 */         cmpExecStatus = CMPExecStatus.PayFinish;
/* 2411 */       } else if ("sj".equals(djdl) || "sk".equals(djdl)) {
/* 2412 */         cmpExecStatus = CMPExecStatus.ReciveFinish;
/* 2413 */       } else if ("hj".equals(djdl)) {
/* 2414 */         cmpExecStatus = CMPExecStatus.PayFinish;
/*      */       } 
/* 2416 */       busiServcie.execStatuesChange(busiInfo, cmpExecStatus);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notifyBusi(SettlementAggVO aggvo) throws BusinessException {
/* 2427 */     SettlementHeadVO head = (SettlementHeadVO)aggvo.getParentVO();
/* 2428 */     if (head.getSettlestatus() == null || head
/* 2429 */       .getSettlestatus().intValue() == SettleStatus.NONESETTLE
/* 2430 */       .getStatus()) {
/*      */       return;
/*      */     }
/*      */     
/* 2434 */     String pk_billtype = SettleUtils.getbilltype(head.getPk_tradetype(), head
/* 2435 */         .getPk_group());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2440 */     SettlementNetPayStatProcessForERM.startSagas(head);
/* 2441 */     SettleNotifyPayTypeBusiBillServiceProxy busiService = SettleNotifyPayTypeBusiBillServiceProxy.getService(head.getPk_group(), pk_billtype);
/*      */ 
/*      */     
/* 2444 */     List<SettlementBodyVO> bodys = CmpUtils.covertArraysToList((SettlementBodyVO[])aggvo.getChildrenVO());
/* 2445 */     NetPayExecInfo pay = new NetPayExecInfo();
/* 2446 */     pay.setBillid(head.getPk_busibill());
/* 2447 */     pay.setPk_org(head.getPk_org());
/* 2448 */     pay.setOperateDate(head.getLastupdatedate());
/* 2449 */     pay.setOperator(head.getLastupdater());
/* 2450 */     pay.setBilltype(head.getPk_tradetype());
/* 2451 */     List<String> busiDetails = Lists.newArrayList();
/* 2452 */     Map<String, CMPExecStatus> statusMap = new HashMap<String, CMPExecStatus>();
/*      */     
/* 2454 */     Map<String, Set<CMPExecStatus>> tempStatusMap = CmpUtils.makeMap();
/* 2455 */     for (SettlementBodyVO body : bodys) {
/* 2456 */       if (body.getSettlestatus() != null && body
/* 2457 */         .getSettlestatus().intValue() == SettleStatus.SUCCESSSETTLE
/* 2458 */         .getStatus()) {
/*      */         
/* 2460 */         if (tempStatusMap.get(body.getPk_billdetail()) == null) {
/* 2461 */           tempStatusMap.put(body.getPk_billdetail(), new HashSet());
/*      */         }
/*      */         
/* 2464 */         ((Set)tempStatusMap.get(body.getPk_billdetail())).add(CMPExecStatus.PayFinish); continue;
/*      */       } 
/* 2466 */       if (body.getSettlestatus() != null && body
/* 2467 */         .getSettlestatus().intValue() == SettleStatus.PAYFAIL
/* 2468 */         .getStatus()) {
/*      */         
/* 2470 */         if (tempStatusMap.get(body.getPk_billdetail()) == null) {
/* 2471 */           tempStatusMap.put(body.getPk_billdetail(), new HashSet());
/*      */         }
/*      */         
/* 2474 */         ((Set)tempStatusMap.get(body.getPk_billdetail())).add(CMPExecStatus.PayFail); continue;
/*      */       } 
/* 2476 */       if (body.getSettlestatus() != null && body
/* 2477 */         .getSettlestatus().intValue() == SettleStatus.PAYING
/* 2478 */         .getStatus()) {
/*      */         
/* 2480 */         if (tempStatusMap.get(body.getPk_billdetail()) == null) {
/* 2481 */           tempStatusMap.put(body.getPk_billdetail(), new HashSet());
/*      */         }
/*      */         
/* 2484 */         ((Set)tempStatusMap.get(body.getPk_billdetail())).add(CMPExecStatus.Paying); continue;
/*      */       } 
/* 2486 */       if (body.getSettlestatus() != null && body
/* 2487 */         .getSettlestatus().intValue() == SettleStatus.RECEIVING
/* 2488 */         .getStatus()) {
/*      */         
/* 2490 */         if (tempStatusMap.get(body.getPk_billdetail()) == null) {
/* 2491 */           tempStatusMap.put(body.getPk_billdetail(), new HashSet());
/*      */         }
/*      */         
/* 2494 */         ((Set)tempStatusMap.get(body.getPk_billdetail())).add(CMPExecStatus.Reciving);
/*      */         
/*      */         continue;
/*      */       } 
/* 2498 */       if (tempStatusMap.get(body.getPk_billdetail()) == null) {
/* 2499 */         tempStatusMap.put(body.getPk_billdetail(), new HashSet());
/*      */       }
/*      */       
/* 2502 */       ((Set)tempStatusMap.get(body.getPk_billdetail())).add(CMPExecStatus.UNPayed);
/*      */     } 
/*      */ 
/*      */     
/* 2506 */     for (String key : tempStatusMap.keySet()) {
/* 2507 */       busiDetails.add(key);
/* 2508 */       if (((Set)tempStatusMap.get(key)).size() > 1) {
/* 2509 */         if (((Set)tempStatusMap.get(key)).contains(CMPExecStatus.Paying) || ((Set)tempStatusMap
/* 2510 */           .get(key)).contains(CMPExecStatus.UNPayed)) {
/*      */           
/* 2512 */           statusMap.put(key, CMPExecStatus.Paying); continue;
/*      */         } 
/* 2514 */         statusMap.put(key, CMPExecStatus.SomePayFinish);
/*      */         continue;
/*      */       } 
/* 2517 */       statusMap.put(key, (CMPExecStatus)(((Set)tempStatusMap.get(key)).toArray(new CMPExecStatus[0])[0]));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2525 */     if ((head.getPk_billtype().equals("F3") || head.getPk_billtype()
/* 2526 */       .equals("F5")) && head
/* 2527 */       .getSettlestatus().intValue() == SettleStatus.SUCCESSSETTLE
/* 2528 */       .getStatus()) {
/*      */       
/* 2530 */       BillAggVO billAggVO = null;
/*      */       
/* 2532 */       if (head.getPk_billtype().equals("F3")) {
/* 2533 */         BaseAggVO baseAggVO = (new ArapBillDAO()).queryBillByPrimaryKey(head
/* 2534 */             .getPk_busibill(), head.getPk_billtype());
/* 2535 */       } else if (head.getPk_billtype().equals("F5")) {
/*      */         
/* 2537 */         BillAggVO[] arrayOfBillAggVO = CmpBillInterfaceProxy.INSTANCE.getPayBillPubQueryService().findBillByPrimaryKey(new String[] { head
/* 2538 */               .getPk_busibill() });
/* 2539 */         if (arrayOfBillAggVO != null && arrayOfBillAggVO.length > 0) {
/* 2540 */           billAggVO = arrayOfBillAggVO[0];
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 2545 */       if (billAggVO != null) {
/* 2546 */         CircularlyAccessibleValueObject[] bVOs = billAggVO.getChildrenVO();
/* 2547 */         for (CircularlyAccessibleValueObject bVO : bVOs) {
/* 2548 */           statusMap.put(bVO.getPrimaryKey(), CMPExecStatus.PayFinish);
/*      */         }
/*      */       } 
/*      */     } else {
/*      */       return;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2557 */     if (head.getSettlestatus().intValue() == SettleStatus.SUCCESSSETTLE.getStatus()) {
/* 2558 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.PayFinish);
/* 2559 */     } else if (head.getSettlestatus().intValue() == SettleStatus.SUCCESSPART
/* 2560 */       .getStatus()) {
/* 2561 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.SomePayFinish);
/* 2562 */     } else if (head.getSettlestatus().intValue() == SettleStatus.PAYFAIL.getStatus()) {
/* 2563 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.PayFail);
/* 2564 */     } else if (head.getSettlestatus().intValue() == SettleStatus.PAYING.getStatus()) {
/* 2565 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.Paying);
/* 2566 */     } else if (head.getSettlestatus().intValue() == SettleStatus.RECEIVING.getStatus()) {
/* 2567 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.Reciving);
/* 2568 */     } else if (head.getSettlestatus().intValue() == SettleStatus.RECEVICEFAIL
/* 2569 */       .getStatus()) {
/* 2570 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.ReciveFail);
/* 2571 */     } else if (head.getSettlestatus().intValue() == SettleStatus.CHANGEING.getStatus()) {
/* 2572 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.Paying);
/* 2573 */     } else if (head.getSettlestatus().intValue() == SettleStatus.CHANGEFAIL
/* 2574 */       .getStatus()) {
/* 2575 */       statusMap.put(head.getPk_busibill(), CMPExecStatus.PayFail);
/*      */     } 
/*      */     
/* 2578 */     pay.setDetailid((String[])CmpUtils.covertListToArrays(busiDetails, String.class));
/* 2579 */     pay.setExecStatusMap(statusMap);
/* 2580 */     busiService.netPayExecChange(pay);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setHeadSettleStatus(SettlementHeadVO head) throws BusinessException {
/* 2592 */     SettlementAggVO[] newSettleAggs = CmpInterfaceProxy.INSTANCE.getQueryService().querySettlementAggVOsByPks(new String[] { head
/* 2593 */           .getPk_settlement() });
/*      */     
/* 2595 */     List<SettlementAggVO> aggLst = SettleUtils.filterSettleInfo4HandSettle(newSettleAggs);
/* 2596 */     List<SettlementBodyVO> bodylst = CmpUtils.makeList();
/* 2597 */     if (aggLst.size() > 0) {
/* 2598 */       for (SettlementBodyVO settlementBodyVO : (SettlementBodyVO[])((SettlementAggVO)aggLst
/* 2599 */         .get(0)).getChildrenVO()) {
/* 2600 */         bodylst.add(settlementBodyVO);
/*      */       }
/*      */       
/* 2603 */       bodylst.addAll(CmpUtils.covertArraysToList(head.getItems()));
/* 2604 */       SettlementAggVO aggvo = new SettlementAggVO();
/*      */       
/* 2606 */       aggvo.setParentVO(head);
/* 2607 */       aggvo.setChildrenVO((CircularlyAccessibleValueObject[])bodylst.toArray(new SettlementBodyVO[0]));
/* 2608 */       SettleUtils.setSettleStatus(new SettlementAggVO[] { aggvo });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setBodySettleStatusByPaymentstatus(HashMap<String, String> mapValue, Map<String, SettlementBodyVO> bodyMap) throws BusinessException {
/* 2623 */     List<SettlementBodyVO> bodyList = new ArrayList<SettlementBodyVO>();
/* 2624 */     for (Map.Entry<String, String> entry : mapValue.entrySet()) {
/* 2625 */       String key = (String)entry.getKey();
/* 2626 */       String value = (String)entry.getValue();
/* 2627 */       SettlementBodyVO bodyVO = (SettlementBodyVO)bodyMap.get(key);
/* 2628 */       if (StringUtils.isNotBlank(value) && PaymentStatusEnum.TRADESUCCESS.toIntValue() == Integer.parseInt(value)) {
/*      */         
/* 2630 */         if (bodyVO != null) {
/* 2631 */           bodyVO.setSettlestatus(Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()));
/* 2632 */           bodyVO.setPaydate(new UFDate());
/* 2633 */           bodyVO.setTallydate(new UFDate());
/* 2634 */           bodyVO.setTallystatus(Integer.valueOf(SettleStatus.SUCCESSSETTLE.getStatus()));
/* 2635 */           bodyList.add(bodyVO);
/*      */         }  continue;
/* 2637 */       }  if (StringUtils.isNotBlank(value) && PaymentStatusEnum.TRADEFAIL.toIntValue() == Integer.parseInt(value)) {
/*      */         
/* 2639 */         if (bodyVO != null) {
/* 2640 */           bodyVO.setSettlestatus(Integer.valueOf(SettleStatus.PAYFAIL.getStatus()));
/* 2641 */           bodyList.add(bodyVO);
/*      */         }  continue;
/* 2643 */       }  if (StringUtils.isNotBlank(value) && PaymentStatusEnum.TRADEUNDOWM.toIntValue() == Integer.parseInt(value))
/*      */       {
/* 2645 */         if (bodyVO != null) {
/* 2646 */           bodyVO.setSettlestatus(Integer.valueOf(SettleStatus.PAYING.getStatus()));
/* 2647 */           bodyList.add(bodyVO);
/*      */         } 
/*      */       }
/*      */     } 
/* 2651 */     if (null == bodyList || bodyList.size() == 0) {
/*      */       return;
/*      */     }
/*      */     
/* 2655 */     getBaseDAO().updateVOList(bodyList);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2664 */   public static ISettlementQueryService getSettlementQueryService() { return (ISettlementQueryService)NCLocator.getInstance().lookup(ISettlementQueryService.class); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addLock(SettlementAggVO[] aggvos) throws BusinessException {
/* 2674 */     if (aggvos == null || aggvos.length == 0) {
/*      */       return;
/*      */     }
/* 2677 */     String[] ids = new String[aggvos.length];
/* 2678 */     List<String> bodyIds = Lists.newArrayList();
/* 2679 */     dealData(ids, bodyIds, aggvos);
/*      */     
/* 2681 */     BDPKLockUtil.lockString(ids);
/* 2682 */     BDPKLockUtil.lockString((String[])bodyIds.toArray(new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unLock(SettlementAggVO[] aggvos) throws BusinessException {
/* 2692 */     if (aggvos == null || aggvos.length == 0) {
/*      */       return;
/*      */     }
/* 2695 */     String[] ids = new String[aggvos.length];
/* 2696 */     List<String> bodyIds = Lists.newArrayList();
/* 2697 */     dealData(ids, bodyIds, aggvos);
/*      */     
/* 2699 */     PKLock.getInstance().releaseBatchLock(ids, null, "cmp_settlement");
/* 2700 */     PKLock.getInstance().releaseBatchLock(
/* 2701 */         (String[])CmpUtils.covertListToArrays(bodyIds, String.class), null, "cmp_detail");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void dealData(String[] ids, List<String> bodyIds, SettlementAggVO[] aggvos) throws BusinessException {
/* 2707 */     int count = 0;
/* 2708 */     for (SettlementAggVO settlementAggVO : aggvos) {
/* 2709 */       ids[count] = settlementAggVO.getParentVO().getPrimaryKey();
/* 2710 */       count++;
/* 2711 */       for (SettlementBodyVO body : (SettlementBodyVO[])settlementAggVO
/* 2712 */         .getChildrenVO()) {
/* 2713 */         bodyIds.add(body.getPrimaryKey());
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private BaseDAO getBaseDAO() {
/* 2719 */     if (this.dao == null) {
/* 2720 */       this.dao = new BaseDAO();
/*      */     }
/* 2722 */     return this.dao;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2729 */   public boolean queryAutoSettle(SettlementAggVO settlementAggVO) throws BusinessException { return AutoSettleUtil.canAutoSettle(settlementAggVO); }
/*      */ }


/* Location:              D:\WORK\NCC202105_GOLD\ncc_home\modules\cmp\META-INF\lib\cmp_settlementmanagement.jar!/nc/impl/cmp/settlement/SettlementServiceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */