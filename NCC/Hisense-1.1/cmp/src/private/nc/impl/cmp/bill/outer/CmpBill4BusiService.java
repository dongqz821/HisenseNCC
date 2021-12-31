/*     */ package nc.impl.cmp.bill.outer;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import nc.baseapp.util.GettingData;
/*     */ import nc.bs.dao.DAOException;
/*     */ import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
/*     */ import nc.cmp.bill.util.CmpBillInterfaceProxy;
/*     */ import nc.cmp.bill.util.ICMPBillStatus;
/*     */ import nc.cmp.bill.util.PayBillSagasUtil;
/*     */ import nc.cmp.bill.util.SysInit;
/*     */ import nc.cmp.utils.ArrayUtil;
/*     */ import nc.cmp.utils.CmpInterfaceProxy;
/*     */ import nc.cmp.utils.CmpQueryModulesUtil;
/*     */ import nc.cmp.utils.CmpUtils;
/*     */ import nc.cmp.utils.SettleUtils;
/*     */ import nc.cmp.utils.UFDoubleUtils;
/*     */ import nc.itf.cmp.bill.ICmpBillSendMessageToDap;
/*     */ import nc.itf.cmp.busi.ISettleNotifyBusiService;
/*     */ import nc.itf.cmp.busi.ISettleNotifyPayTypeBusiBillService;
/*     */ import nc.md.data.access.NCObject;
/*     */ import nc.vo.cmp.BusiInfo;
/*     */ import nc.vo.cmp.BusiStateTrans;
/*     */ import nc.vo.cmp.BusiStatus;
/*     */ import nc.vo.cmp.CMPExecStatus;
/*     */ import nc.vo.cmp.CMPExecStatusToPayStatus;
/*     */ import nc.vo.cmp.NetPayExecInfo;
/*     */ import nc.vo.cmp.bill.BillAggVO;
/*     */ import nc.vo.cmp.bill.BillDetailVO;
/*     */ import nc.vo.cmp.bill.BillEnumCollection;
/*     */ import nc.vo.cmp.bill.BillUtils;
/*     */ import nc.vo.cmp.bill.BillVO;
/*     */ import nc.vo.cmp.bill.SendToDapBatchVO;
/*     */ import nc.vo.cmp.settlement.SettleEnumCollection;
/*     */ import nc.vo.cmp.settlement.SettlementBodyVO;
/*     */ import nc.vo.cmp.settlement.batch.BusiStateChangeVO;
/*     */ import nc.vo.ml.NCLangRes4VoTransl;
/*     */ import nc.vo.pub.AggregatedValueObject;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.SuperVO;
/*     */ import nc.vo.pub.lang.UFBoolean;
/*     */ import nc.vo.pub.lang.UFDouble;
/*     */ import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import ncc.itf.baseapp.voucher.ICMPService;
/*     */ import nccloud.pub.tmpub.ms.vo.SagasOperEnum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CmpBill4BusiService
/*     */   extends AbstractBusiService<BillAggVO>
/*     */   implements ISettleNotifyBusiService, ISettleNotifyPayTypeBusiBillService
/*     */ {
/*     */   public Object getBillVO(BusiInfo info) throws BusinessException {
/*  66 */     return (BillAggVO)CmpInterfaceProxy.INSTANCE.getPersistenceQueryService()
/*  67 */       .queryBillOfVOByPK(BillAggVO.class, info.getPk_bill(), false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public boolean isAutoFillEbankInfo(String pkOrg, String pkBilltype, String pkGroup) throws BusinessException { return false; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void effectStateChange(BusiInfo busiInfo, BusiStateTrans trans) throws BusinessException{
/*  79 */     NCObject queryBean = getConcreatBillVO(busiInfo.getPk_bill());
/*     */     
/*  81 */     AggregatedValueObject busiagg = BillUtils.getAggVOFromNcObject(queryBean);
/*  82 */     BillVO head = (BillVO)busiagg.getParentVO();
/*     */     
/*  84 */     if (BusiStatus.Effet.equals(trans.getTo())) {
/*  85 */       if (BillEnumCollection.EffecfFlag.EFFECT.getValue().equals(head.getEffect_flag())) {
/*  86 */         ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes()
/*  87 */             .getStrByID("3607mng1_0", "03607mng1-0241"));
/*     */       }
/*  89 */       head.setEffect_date(busiInfo.getOperatorDate());
/*  90 */       head.setEffect_flag(BillEnumCollection.EffecfFlag.EFFECT.getValue());
/*  91 */       head.setPk_effect_user(busiInfo.getOperator());
/*     */       
/*  93 */       settleNotifyBusFireEvent(busiagg, "360702");
/*  94 */     } else if (BusiStatus.EffectNever.equals(trans.getTo())) {
/*  95 */       head.setEffect_date(null);
/*  96 */       head.setEffect_flag(BillEnumCollection.EffecfFlag.NONEFFECT.getValue());
/*  97 */       head.setPk_effect_user(null);
/*  98 */       settleNotifyBusFireEvent(busiagg, "360704");
/*     */     } 
/* 100 */     head.setStatus(1);
/* 101 */     this.basedao.updateVOArray(new BillVO[] { head }, new String[] { "effect_date", "effect_flag", "pk_effect_user" });
/* 102 */     busiagg = BillUtils.getAggVOFromNcObject(queryBean);
/*     */ 
/*     */     
/* 105 */     Map<String, List<SettlementBodyVO>> detailMap = CmpInterfaceProxy.INSTANCE.getQueryService().queryMap(busiInfo);
/*     */     
/* 107 */     BillAggVO clonebusiagg = new BillAggVO();
/*     */ 
/*     */     
/* 110 */     ICmpBillSendMessageToDap iCmpBillSendMessageToDap = (ICmpBillSendMessageToDap)NCLocator.getInstance().lookup(ICmpBillSendMessageToDap.class);
/*     */ 
/*     */     
/* 113 */     PayBillSagasUtil payutil = new PayBillSagasUtil();
			  
/* 114 */     if (trans.getTo().equals(BusiStatus.Effet)) {
				//回写nc65 --dongqingzheng
				if (!"".equals(head.getDef20()) && head.getDef20() != null  ) {
					Map<String, Object> map = new HashMap<>();
					map.put("pk_upbill", head.getPk_upbill());//来源单据主键
					map.put("busistatus", head.getBill_status());//单据状态
					map.put("bill_type", head.getBill_type());//单据类型
					map.put("def20", head.getDef20());//来源系统
					
					String param = JSONObject.toJSONString(map);
					JSONObject json = JSONObject.parseObject(param);
					//调用nc65接口 回写签字状态
					try {
						ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
						String result = gett.writeBack2NC65(json);
						JSONObject jsonsucc = JSONObject.parseObject(result);
						if (!"200".equals(jsonsucc.get("code"))) {
							ExceptionUtils.wrappBusinessException("回写失败:" + jsonsucc.getString("msg"));
						}			
					} catch (Exception e) {
						Logger.error(e.getMessage());
//						throw new BusinessException("回写NC65单据失败，请检查！" + e.getMessage());
			            ExceptionUtils.wrappBusinessException("回写NC65单据失败，请检查!"+e.getMessage());
					}
				    
				}
/* 115 */       payutil.frozenAndAddSaga(SagasOperEnum.VOUCHER, new AggregatedValueObject[] { busiagg });
/* 116 */       iCmpBillSendMessageToDap.sendMessageToDap(busiagg, clonebusiagg, detailMap);
/* 117 */     } else if (trans.getTo().equals(BusiStatus.EffectNever)) {
				//回写nc65 --dongqingzheng
				if (!"".equals(head.getDef20()) && head.getDef20() != null  ) {
					Map<String, Object> map = new HashMap<>();
					map.put("pk_upbill", head.getPk_upbill());//来源单据主键
					map.put("busistatus", head.getBill_status());//单据状态
					map.put("bill_type", head.getBill_type());//单据类型
					map.put("def20", head.getDef20());//来源系统
					
					String param = JSONObject.toJSONString(map);
					JSONObject json = JSONObject.parseObject(param);
					try {
						//调用nc65接口 回写签字状态
						ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
//						String result = gett.writeBack2NC65(json);
//						JSONObject jsonsucc = JSONObject.parseObject(result);
//						if (!"200".equals(jsonsucc.get("code"))) {
//							ExceptionUtils.wrappBusinessException("回写失败:" + jsonsucc.getString("msg"));
						ExceptionUtils.wrappBusinessException("回写失败:" + json);
//						}	
					} catch (Exception e) {
						Logger.error(e.getMessage());
//						throw new BusinessException("回写NC65单据失败，请检查！" + e.getMessage());
			            ExceptionUtils.wrappBusinessException("回写NC65单据失败，请检查!"+e.getMessage());
					}
					
				}
/* 118 */       payutil.frozenAndAddSaga(SagasOperEnum.UNVOUCHER, new AggregatedValueObject[] { busiagg });
/* 119 */       iCmpBillSendMessageToDap.sendMessageToDapDelete(busiagg, clonebusiagg, detailMap);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 125 */   public void setoffRed(NetPayExecInfo payInfo, Map<String, SettlementBodyVO[]> value) throws BusinessException { CmpBillInterfaceProxy.INSTANCE.getPayBillPubService().SettleRedHandleSaveAndSign(payInfo, value); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyBusiDealWithSendToDap(BusiStateChangeVO... busiStateChangeVOs) throws BusinessException {
/* 131 */     if (busiStateChangeVOs == null || busiStateChangeVOs.length == 0) {
/*     */       return;
/*     */     }
/* 134 */     List<BusiInfo> busiInfosList = CmpUtils.makeList();
/* 135 */     List<SendToDapBatchVO> sendToDapBatchVOList = CmpUtils.makeList();
/* 136 */     for (BusiStateChangeVO busiStateChangeVO : busiStateChangeVOs) {
/* 137 */       if (busiStateChangeVO.getBusibill() == null) {
/* 138 */         busiInfosList.add(busiStateChangeVO.getBusiInfo());
/*     */       }
/*     */     } 
/* 141 */     BusiInfo[] busiInfos = (BusiInfo[])busiInfosList.toArray(new BusiInfo[0]);
/* 142 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 143 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 144 */     List<BillDetailVO> childrenList = ArrayUtil.makeList();
/*     */     
/* 146 */     Map<String, AggregatedValueObject> headPkToAggMap = SettleUtils.convertAggregatedValueObjectsToHeadPkToAggMap(billaggs);
/*     */ 
/*     */     
/* 149 */     Map<String, String> map = new HashMap<String, String>();
/*     */     
/* 151 */     ICmpBillSendMessageToDap iCmpBillSendMessageToDap = CmpBillInterfaceProxy.INSTANCE.getCmpBillSendMessageToDap();
/* 152 */     for (BusiStateChangeVO busiStateChangeVO : busiStateChangeVOs) {
/* 153 */       BusiInfo busiInfo = busiStateChangeVO.getBusiInfo();
/* 154 */       AggregatedValueObject busiagg = busiStateChangeVO.getBusibill();
/* 155 */       if (busiagg == null) {
/* 156 */         busiagg = (AggregatedValueObject)headPkToAggMap.get(busiInfo.getPk_bill());
/*     */       }
/*     */       
/* 159 */       BillVO head = (BillVO)busiagg.getParentVO();
/* 160 */       billVOList.add(head);
/*     */       
/* 162 */       if (BusiStatus.Effet.equals(busiStateChangeVO.getBusiState().getTo())) {
/* 163 */         if (BillEnumCollection.EffecfFlag.EFFECT.getValue().equals(head.getEffect_flag())) {
/* 164 */           ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl.getNCLangRes()
/* 165 */               .getStrByID("3607mng1_0", "03607mng1-0241"));
/*     */         }
/* 167 */         head.setEffect_date(busiInfo.getOperatorDate());
/* 168 */         head.setEffect_flag(BillEnumCollection.EffecfFlag.EFFECT.getValue());
/* 169 */         head.setPk_effect_user(busiInfo.getOperator());
/* 170 */         if (UFBoolean.TRUE.equals(head.getIs_cf())) {
/* 171 */           BillDetailVO[] children = (BillDetailVO[])busiagg.getChildrenVO();
/* 172 */           for (BillDetailVO billDetailVO : children) {
/* 173 */             billDetailVO.setCf_status(SettleEnumCollection.CommissionPayStatus.CommissionPaySusscess.VALUE);
/*     */           }
/*     */           
/* 176 */           childrenList.addAll(Arrays.asList(children));
/*     */         }
/*     */       
/* 179 */       } else if (BusiStatus.EffectNever.equals(busiStateChangeVO.getBusiState().getTo())) {
/* 180 */         head.setEffect_date(null);
/* 181 */         head.setEffect_flag(null);
/* 182 */         head.setPk_effect_user(null);
/* 183 */         if (UFBoolean.TRUE.equals(head.getIs_cf())) {
/* 184 */           BillDetailVO[] children = (BillDetailVO[])busiagg.getChildrenVO();
/* 185 */           for (BillDetailVO billDetailVO : children) {
/* 186 */             billDetailVO.setCf_status(SettleEnumCollection.CommissionPayStatus.CommissionPaying.VALUE);
/*     */           }
/*     */           
/* 189 */           childrenList.addAll(Arrays.asList(children));
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 197 */       Map<String, List<SettlementBodyVO>> detailMap = (busiStateChangeVO.getDetailMap() == null) ? CmpInterfaceProxy.INSTANCE.getQueryService().queryMap(busiInfo) : busiStateChangeVO.getDetailMap();
/*     */       
/* 199 */       BillAggVO billagg = (BillAggVO)busiagg;
/*     */       
/* 201 */       BillAggVO clonebusiagg = new BillAggVO();
/*     */       
/* 203 */       SendToDapBatchVO sendToDapBatchVO = new SendToDapBatchVO();
/* 204 */       sendToDapBatchVO.setBusiagg(billagg);
/* 205 */       sendToDapBatchVO.setClonebusiagg(clonebusiagg);
/* 206 */       sendToDapBatchVO.setDetailMap(detailMap);
/* 207 */       sendToDapBatchVOList.add(sendToDapBatchVO);
/*     */       
/* 209 */       BusiStatus to = busiStateChangeVO.getBusiState().getTo();
/*     */       
/* 211 */       if (!map.containsKey(head.getPk_org())) {
/* 212 */         String cmp37 = SysInit.getParaString(head.getPk_org(), "CMP37");
/* 213 */         map.put(head.getPk_org(), cmp37);
/*     */       } 
/* 215 */       if (CmpQueryModulesUtil.isFIPEnable(head.getPk_group()) && 
/* 216 */         NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0", "03607mng-0363")
/*     */ 
/*     */         
/* 219 */         .equals(map.get(head.getPk_org()))) {
/*     */         
/* 221 */         PayBillSagasUtil payutil = new PayBillSagasUtil();
/* 222 */         if (to.equals(BusiStatus.Effet)) {
/* 223 */           payutil.frozenAndAddSaga(SagasOperEnum.VOUCHER, new AggregatedValueObject[] { busiagg });
/* 224 */           iCmpBillSendMessageToDap.sendMessageToDapBatchAdd(new SendToDapBatchVO[] { sendToDapBatchVO });
/* 225 */         } else if (to.equals(BusiStatus.EffectNever)) {
/* 226 */           payutil.frozenAndAddSaga(SagasOperEnum.UNVOUCHER, new AggregatedValueObject[] { busiagg });
/* 227 */           iCmpBillSendMessageToDap.sendMessageToDapBatchDelete(new SendToDapBatchVO[] { sendToDapBatchVO });
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 236 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "effect_date", "effect_flag", "pk_effect_user", "paystatus" });
/*     */     
/* 238 */     this.basedao.updateVOArray((SuperVO[])childrenList.toArray(new BillDetailVO[0]), new String[] { "cf_status" });
/*     */   }
/*     */ 
/*     */   
/*     */   private BillAggVO[] getBillAggVOsByBusiInfos(BusiInfo... busiInfos) throws BusinessException {
/* 243 */     List<String> pk_paybillList = ArrayUtil.makeList();
/* 244 */     for (BusiInfo busiInfo : busiInfos) {
/* 245 */       pk_paybillList.add(busiInfo.getPk_bill());
/*     */     }
/* 247 */     return CmpBillInterfaceProxy.INSTANCE.getPayBillPubQueryService()
/* 248 */       .findBillByPrimaryKey((String[])pk_paybillList.toArray(new String[0]));
/*     */   }
/*     */   
/*     */   private Map<String, BusiInfo> getBusiMap(BusiInfo... busiInfos) {
/* 252 */     Map<String, BusiInfo> busiMap = new HashMap<String, BusiInfo>();
/* 253 */     for (BusiInfo busiInfo : busiInfos) {
/* 254 */       busiMap.put(busiInfo.getPk_bill(), busiInfo);
/*     */     }
/*     */     
/* 257 */     return busiMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillCancelCommitToFts(BusiInfo... busiInfos) throws BusinessException {
/* 262 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 263 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 264 */     for (BillAggVO billAggVO : billaggs) {
/* 265 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 266 */       billVO.setPaydate(null);
/* 267 */       billVO.setPaystatus(BillEnumCollection.PayStatus.NONE.VALUE);
/* 268 */       billVO.setPayman(null);
/* 269 */       billVO.setPayway(BillEnumCollection.PayWay.NONE.VALUE);
/* 270 */       billVOList.add(billVO);
/*     */     } 
/* 272 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "paydate", "paystatus", "payman", "payway" });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void netPayExecChange(NetPayExecInfo payInfo) throws BusinessException {
/* 279 */     BillAggVO[] billAggVOs = CmpBillInterfaceProxy.INSTANCE.getPayBillPubQueryService().findBillByPrimaryKey(new String[] { payInfo.getBillid() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 291 */     List<SuperVO> parents = new ArrayList<SuperVO>();
/* 292 */     List<SuperVO> childrenList = new ArrayList<SuperVO>();
/* 293 */     Map<String, String> transerialMap = payInfo.getTranserialMap();
/* 294 */     for (BillAggVO billAggVO : billAggVOs) {
/* 295 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 296 */       billVO.setPaydate(payInfo.getOperateDate());
/* 297 */       billVO.setPayman(payInfo.getOperator());
/*     */       
/* 299 */       CMPExecStatus execStatus = (CMPExecStatus)payInfo.getExecStatusMap().get(billVO.getPrimaryKey());
/*     */       
/* 301 */       if (execStatus != null) {
/* 302 */         billVO.setPaystatus(CMPExecStatusToPayStatus.getPayStatus(execStatus));
/*     */       }
/*     */       
/* 305 */       BillDetailVO[] children = (BillDetailVO[])billAggVO.getChildrenVO();
/*     */       
/* 307 */       for (BillDetailVO billDetailVO : children) {
/*     */         
/* 309 */         CMPExecStatus cmpExecStatus = (CMPExecStatus)payInfo.getExecStatusMap().get(billDetailVO.getPrimaryKey());
/*     */         
/* 311 */         if (cmpExecStatus != null) {
/* 312 */           billDetailVO.setPaystatus(CMPExecStatusToPayStatus.getPayStatus(cmpExecStatus));
/* 313 */           billDetailVO.setPaydate(payInfo.getOperateDate());
/* 314 */           billDetailVO.setPayman(payInfo.getOperator());
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 319 */         if (null != transerialMap && transerialMap.containsKey(billDetailVO.getPrimaryKey()))
/*     */         {
/* 321 */           billDetailVO.setTranserial((String)transerialMap.get(billDetailVO.getPrimaryKey()));
/*     */         }
/*     */         
/* 324 */         if ("DS".equals(billVO.getTrade_type())) {
/* 325 */           billDetailVO.setPaystatus(billVO.getPaystatus());
/*     */         }
/*     */ 
/*     */         
/* 329 */         if (null != billVO.getPaystatus() && billVO.getPaystatus() == BillEnumCollection.PayStatus.SUCESS.VALUE && (
/* 330 */           null == billDetailVO.getPaystatus() || 1 == billDetailVO.getPaystatus().intValue())) {
/* 331 */           billDetailVO.setPaystatus(billVO.getPaystatus());
/* 332 */           billDetailVO.setPaydate(billVO.getPaydate());
/* 333 */           billDetailVO.setPayman(billVO.getPayman());
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 338 */       parents.add(billVO);
/* 339 */       childrenList.addAll(Arrays.asList(children));
/*     */     } 
/*     */     
/* 342 */     this.basedao.updateVOArray((SuperVO[])parents.toArray(new BillVO[0]), new String[] { "paydate", "paystatus", "payman" });
/* 343 */     this.basedao.updateVOArray((SuperVO[])childrenList.toArray(new BillDetailVO[0]), new String[] { "paydate", "paystatus", "payman", "transerial" });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillCancelInnertansferAndCancelEffect(BusiStateChangeVO... busiStateChangeVOs) throws BusinessException {
/* 350 */     List<BusiInfo> busiInfoList = ArrayUtil.makeList();
/* 351 */     for (BusiStateChangeVO busiStateChangeVO : busiStateChangeVOs) {
/* 352 */       busiInfoList.add(busiStateChangeVO.getBusiInfo());
/*     */     }
/* 354 */     BusiInfo[] busiInfos = (BusiInfo[])busiInfoList.toArray(new BusiInfo[0]);
/* 355 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/*     */     
/* 357 */     Map<String, AggregatedValueObject> headPkToAggMap = SettleUtils.convertAggregatedValueObjectsToHeadPkToAggMap(billaggs);
/* 358 */     for (BusiStateChangeVO busiStateChangeVO : busiStateChangeVOs) {
/* 359 */       BillAggVO billAggVO = (BillAggVO)headPkToAggMap.get(busiStateChangeVO.getBusiInfo().getPk_bill());
/* 360 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 361 */       billVO.setPaystatus(BillEnumCollection.PayStatus.ING.VALUE);
/* 362 */       busiStateChangeVO.setBusibill(billAggVO);
/*     */     } 
/* 364 */     notifyBusiDealWithSendToDap(busiStateChangeVOs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillCommitToFts(BusiInfo... busiInfos) throws BusinessException {
/* 369 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 370 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 371 */     Map<String, BusiInfo> PkBusibillToBusiInfoMap = BillUtils.convertBusiInfosToPk_busibillToBusiInfoMap(busiInfos);
/* 372 */     for (BillAggVO billAggVO : billaggs) {
/* 373 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 374 */       BusiInfo busiInfo = (BusiInfo)PkBusibillToBusiInfoMap.get(billVO.getPrimaryKey());
/* 375 */       billVO.setPaydate(busiInfo.getOperatorDate());
/* 376 */       billVO.setPaystatus(BillEnumCollection.PayStatus.ING.VALUE);
/* 377 */       billVO.setPayman(busiInfo.getOperator());
/* 378 */       billVO.setPayway(BillEnumCollection.PayWay.CENTRPAY.VALUE);
/* 379 */       billVOList.add(billVO);
/*     */     } 
/* 381 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "paydate", "paystatus", "payman", "payway" });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillFtsRefuseDeal(BusiInfo... busiInfos) throws BusinessException {
/* 387 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 388 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 389 */     for (BillAggVO billAggVO : billaggs) {
/* 390 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 391 */       billVO.setStatus(1);
/* 392 */       billVO.setIsrefused(UFBoolean.TRUE);
/* 393 */       billVOList.add(billVO);
/*     */     } 
/* 395 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "isrefused" });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillInnertansferCancelForcePay(BusiInfo... busiInfos) throws BusinessException {
/* 401 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 402 */     List<BillDetailVO> billDetailVOList = ArrayUtil.makeList();
/* 403 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 404 */     for (BillAggVO billAggVO : billaggs) {
/* 405 */       BillVO head = (BillVO)billAggVO.getParentVO();
/* 406 */       head.setStatus(1);
/* 407 */       billVOList.add(head);
/* 408 */       BillDetailVO[] bodys = (BillDetailVO[])billAggVO.getChildrenVO();
/* 409 */       for (BillDetailVO billDetailVO : bodys) {
/* 410 */         billDetailVO.setCf_man(null);
/* 411 */         billDetailVO.setCf_status(SettleEnumCollection.CommissionPayStatus.UnCommissionPay.VALUE);
/* 412 */         billDetailVO.setCf_type(null);
/* 413 */         billDetailVO.setStatus(1);
/* 414 */         billDetailVOList.add(billDetailVO);
/*     */       } 
/*     */     } 
/* 417 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "dr" });
/* 418 */     this.basedao.updateVOArray((SuperVO[])billDetailVOList.toArray(new BillDetailVO[0]), new String[] { "cf_man", "cf_status", "cf_type" });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillInnertansferForcePay(BusiInfo... busiInfos) throws BusinessException {
/* 424 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 425 */     Map<String, BusiInfo> PkBusibillToBusiInfoMap = BillUtils.convertBusiInfosToPk_busibillToBusiInfoMap(busiInfos);
/* 426 */     List<BillDetailVO> billDetailVOList = ArrayUtil.makeList();
/* 427 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 428 */     for (BillAggVO billAggVO : billaggs) {
/* 429 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 430 */       billVOList.add(billVO);
/* 431 */       BusiInfo busiInfo = (BusiInfo)PkBusibillToBusiInfoMap.get(billVO.getPrimaryKey());
/* 432 */       BillDetailVO[] bodys = (BillDetailVO[])billAggVO.getChildrenVO();
/* 433 */       for (BillDetailVO billDetailVO : bodys) {
/* 434 */         billDetailVO.setCf_man(busiInfo.getOperator());
/* 435 */         billDetailVO.setCf_status(SettleEnumCollection.CommissionPayStatus.CommissionPaySusscess.VALUE);
/* 436 */         billDetailVO.setCf_type(SettleEnumCollection.CommissionPayType.ForceCommPayALL.VALUE);
/* 437 */         billDetailVO.setStatus(1);
/* 438 */         billDetailVOList.add(billDetailVO);
/*     */       } 
/*     */     } 
/*     */     
/* 442 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "dr" });
/* 443 */     this.basedao.updateVOArray((SuperVO[])billDetailVOList.toArray(new BillDetailVO[0]), new String[] { "cf_man", "cf_status", "cf_type" });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillInnertansferRefuseCommisionPay(BusiInfo... busiInfos) throws BusinessException {
/* 449 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/* 450 */     Map<String, BusiInfo> PkBusibillToBusiInfoMap = BillUtils.convertBusiInfosToPk_busibillToBusiInfoMap(busiInfos);
/* 451 */     List<BillDetailVO> billDetailVOList = ArrayUtil.makeList();
/* 452 */     List<BillVO> billVOList = ArrayUtil.makeList();
/* 453 */     for (BillAggVO billAggVO : billaggs) {
/* 454 */       BillDetailVO[] bodys = (BillDetailVO[])billAggVO.getChildrenVO();
/* 455 */       BillVO billVO = (BillVO)billAggVO.getParentVO();
/* 456 */       BusiInfo busiInfo = (BusiInfo)PkBusibillToBusiInfoMap.get(billVO.getPrimaryKey());
/*     */       
/* 458 */       billVO.setPrimal_money(UFDouble.ZERO_DBL);
/* 459 */       billVO.setLocal_money(UFDouble.ZERO_DBL);
/* 460 */       billVO.setGroup_local(UFDouble.ZERO_DBL);
/* 461 */       billVO.setGlobal_local(UFDouble.ZERO_DBL);
/*     */       
/* 463 */       for (BillDetailVO billDetailVO : bodys) {
/* 464 */         billDetailVO.setCf_man(null);
/* 465 */         billDetailVO.setCf_status(SettleEnumCollection.CommissionPayStatus.UnCommissionPay.VALUE);
/* 466 */         billDetailVO.setCf_type(null);
/* 467 */         billDetailVO.setRefusenote(busiInfo.getBodyRefuseReason());
/*     */         
/* 469 */         billDetailVO.setPay_primal(billDetailVO.getTs_primal());
/* 470 */         billDetailVO.setPay_local(billDetailVO.getTs_local());
/* 471 */         billDetailVO.setGroup_local_pay(billDetailVO.getGroup_local_ts());
/* 472 */         billDetailVO.setGlobal_local_pay(billDetailVO.getGlobal_local_ts());
/* 473 */         billDetailVO.setStatus(1);
/*     */         
/* 475 */         billDetailVOList.add(billDetailVO);
/*     */         
/* 477 */         billVO.setPrimal_money(UFDoubleUtils.add(billDetailVO.getTs_primal(), billVO.getPrimal_money()));
/* 478 */         billVO.setLocal_money(UFDoubleUtils.add(billDetailVO.getTs_local(), billVO.getLocal_money()));
/* 479 */         billVO.setGroup_local(UFDoubleUtils.add(billDetailVO.getGroup_local_ts(), billVO.getGroup_local()));
/* 480 */         billVO.setGlobal_local(UFDoubleUtils.add(billDetailVO.getGlobal_local_ts(), billVO.getGlobal_local()));
/*     */       } 
/*     */ 
/*     */       
/* 484 */       billVO.setBill_status(Integer.valueOf(ICMPBillStatus.Tempeorary));
/* 485 */       billVOList.add(billVO);
/*     */     } 
/*     */     
/* 488 */     this.basedao.updateVOArray((SuperVO[])billDetailVOList.toArray(new SuperVO[0]), new String[] { "cf_man", "cf_type", "cf_status", "pay_primal", "pay_local", "grouplocal", "globallocal", "refusenote" });
/*     */     
/* 490 */     this.basedao.updateVOArray((SuperVO[])billVOList.toArray(new BillVO[0]), new String[] { "bill_status", "primal_money", "grouplocal", "globallocal" });
/*     */ 
/*     */ 
/*     */     
/* 494 */     for (SuperVO body : billDetailVOList) {
/*     */       
/* 496 */       BillDetailVO billDetailVO = (BillDetailVO)body;
/* 497 */       updateMoney(billDetailVO);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void updateMoney(BillDetailVO billDetailVO) throws DAOException {
/* 503 */     StringBuilder sBuilder = new StringBuilder("UPDATE cmp_paybilldetail SET grouplocal =");
/* 504 */     sBuilder.append(billDetailVO.getGroup_local_pay());
/* 505 */     sBuilder.append(" , globallocal = ").append(billDetailVO.getGlobal_local_pay());
/* 506 */     sBuilder.append(" WHERE pk_paybill_detail='").append(billDetailVO.getPrimaryKey()).append("'");
/*     */     
/* 508 */     this.basedao.executeUpdate(sBuilder.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyPayTypeBillInnertansferSuccessAndEffect(BusiStateChangeVO... busiStateChangeVOs) throws BusinessException {
/* 514 */     List<BusiInfo> busiInfoList = ArrayUtil.makeList();
/* 515 */     for (BusiStateChangeVO busiStateChangeVO : busiStateChangeVOs) {
/* 516 */       busiInfoList.add(busiStateChangeVO.getBusiInfo());
/*     */     }
/* 518 */     BusiInfo[] busiInfos = (BusiInfo[])busiInfoList.toArray(new BusiInfo[0]);
/* 519 */     BillAggVO[] billaggs = getBillAggVOsByBusiInfos(busiInfos);
/*     */     
/* 521 */     Map<String, AggregatedValueObject> headPkToAggMap = SettleUtils.convertAggregatedValueObjectsToHeadPkToAggMap(billaggs);
				BillVO billVO = null;
/* 522 */     for (BusiStateChangeVO busiStateChangeVO : busiStateChangeVOs) {
/* 523 */       BillAggVO billAggVO = (BillAggVO)headPkToAggMap.get(busiStateChangeVO.getBusiInfo().getPk_bill());
/* 524 */       billVO = (BillVO)billAggVO.getParentVO();
/* 525 */       billVO.setPaystatus(BillEnumCollection.PayStatus.SUCESS.VALUE);
/* 526 */       busiStateChangeVO.setBusibill(billAggVO);
/*     */     } 
				
				//回写nc65支付状态
//				if (!"".equals(billVO.getDef20()) && billVO.getDef20() != null  ) {
//					Map<String, Object> map = new HashMap<>();
//					map.put("pk_upbill", billVO.getPk_upbill());//来源单据主键
//					map.put("bill_type", billVO.getBill_type());//单据类型
//					map.put("def20", billVO.getDef20());//来源系统
//					map.put("pay", 2);
//					
//					String param = JSONObject.toJSONString(map);
//					JSONObject json = JSONObject.parseObject(param);
//					try {
//						//调用nc65接口 回写签字状态
//						ICMPService gett = NCLocator.getInstance().lookup(ICMPService.class);
//						String result = gett.writeBack2NC65(json);
//						JSONObject jsonsucc = JSONObject.parseObject(result);
//						if (!"200".equals(jsonsucc.get("code"))) {
//							ExceptionUtils.wrappBusinessException("回写支付失败:" + jsonsucc.getString("msg"));
//						}	
//					} catch (Exception e) {
//						Logger.error(e.getMessage());
//						throw new BusinessException("回写NC65单据失败，请检查！" + e.getMessage());
////			            ExceptionUtils.wrappBusinessException("回写NC65单据失败，请检查!"+e.getMessage());
//					}
//				}
//				
/* 528 */     notifyBusiDealWithSendToDap(busiStateChangeVOs);
/*     */   }
/*     */ }


/* Location:              D:\WORK\NCC202105_GOLD\ncc_home\modules\cmp\META-INF\lib\cmp_billmanagement.jar!/nc/impl/cmp/bill/outer/CmpBill4BusiService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */