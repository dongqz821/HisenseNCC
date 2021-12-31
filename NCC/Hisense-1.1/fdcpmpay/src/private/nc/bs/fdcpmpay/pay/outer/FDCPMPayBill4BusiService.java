package nc.bs.fdcpmpay.pay.outer;

import java.util.List;
import java.util.Map;

import nc.itf.cmp.busi.ISettleNotifyBusiService;
import nc.itf.cmp.busi.ISettleNotifyPayTypeBusiBillService;
import nc.vo.cmp.BusiInfo;
import nc.vo.cmp.BusiStateTrans;
import nc.vo.cmp.CMPExecStatus;
import nc.vo.cmp.NetPayExecInfo;
import nc.vo.cmp.ReturnBill4BusiVO;
import nc.vo.cmp.ReturnBillRetDetail;
import nc.vo.cmp.fts.MoneyDetail;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.cmp.settlement.batch.BusiStateChangeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class FDCPMPayBill4BusiService implements ISettleNotifyBusiService,
	ISettleNotifyPayTypeBusiBillService {

	@Override
	public void execStatuesChange(BusiInfo busiInfo, CMPExecStatus statues)
			throws BusinessException {
		// TODO Auto-generated method stub
	}

	@Override
	public void coerceDelete(BusiInfo busiInfo) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void billChange(BusiInfo busiInfo, Map<String, MoneyDetail> value)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkCancelSign(BusiInfo info) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkCancelEffect(BusiInfo info) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void billStateChange(BusiInfo busiInfo, BusiStateTrans trans)
			throws BusinessException {
		// TODO Auto-generated method stub
	}

	@Override
	public void effectStateChange(BusiInfo busiInfo, BusiStateTrans trans)
			throws BusinessException {
		// TODO Auto-generated method stub
	}

	@Override
	public List<ReturnBillRetDetail> processReturnBill(
			ReturnBill4BusiVO bill4BusiVO) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getBillVO(BusiInfo info) throws BusinessException {
		return null;
	}

	@Override
	public void netPayExecChange(NetPayExecInfo payInfo)
			throws BusinessException {
	
	       CMPExecStatus execStatus = (CMPExecStatus)payInfo.getExecStatusMap().get(payInfo.getBillid());
	       if(execStatus.getStatus()==3){
	    	  //�ɱ�֧���ɹ���д
	    	  
	       }else{
	    	   //ExceptionUtils.wrappBusinessException("billStateChange：CMPExecStatus.PayFinish.equals(execStatus)");
	       }
	    
	   
	}

	@Override
	public void setoffRed(NetPayExecInfo payInfo,
			Map<String, SettlementBodyVO[]> value) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAutoFillEbankInfo(String pk_org, String pk_billtype,
			String pk_group) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 现金结算�?结算或�?取消结算操作，回写项目付款单的自定义�?
	 */
	@Override
	public void notify4HandSettle(List<String> idList, boolean isOpp,
			UFDate operateDate, String operator) throws BusinessException {
	
	}

	@Override
	public List<SettlementBodyVO> autoUsed(List<SettlementBodyVO> bodyList)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SettlementBodyVO> autoBX(List<SettlementBodyVO> bodyList)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeBackInnerStatus(boolean isTransfer,
			SettlementAggVO... aggVOs) throws BusinessException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isAutoSettle(String pk_group, String pk_tradetype,
			SettlementAggVO... settlementAggVOs) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyPayTypeBillInnertansferRefuseCommisionPay(
			BusiInfo... busiInfos) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillFtsRefuseDeal(BusiInfo... busiInfos)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillInnertansferCancelForcePay(
			BusiInfo... busiInfos) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillInnertansferForcePay(BusiInfo... busiInfos)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillCancelCommitToFts(BusiInfo... busiInfos)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillCommitToFts(BusiInfo... busiInfos)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillInnertansferSuccessAndEffect(
			BusiStateChangeVO... busiStateChangeVOs) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPayTypeBillCancelInnertansferAndCancelEffect(
			BusiStateChangeVO... busiStateChangeVOs) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyBusiDealWithSendToDap(
			BusiStateChangeVO... busiStateChangeVO) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}
