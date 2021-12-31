package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.obm.ebankconfirmpay.plugin.bpplugin.EbankconfirmpayPluginPoint;
import nc.bs.obm.pub.rule.CommitPayback2Nc65Rule;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.obm.confirmpay.IEbankconfirmpayMaintain;
import nc.vo.obm.ebankconfirmpay.AggConfirmPayHVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class N_36CC_SAVE extends AbstractPfAction<AggConfirmPayHVO> {
	@SuppressWarnings("unchecked")
	protected CompareAroundProcesser<AggConfirmPayHVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggConfirmPayHVO> processor = new CompareAroundProcesser<AggConfirmPayHVO>(
				EbankconfirmpayPluginPoint.SEND_APPROVE);
		
		IRule<AggConfirmPayHVO> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		
		rule = new CommitPayback2Nc65Rule();
		processor.addAfterRule(rule);
		return processor;
	}

	protected AggConfirmPayHVO[] processBP(Object userObj, AggConfirmPayHVO[] clientFullVOs,
			AggConfirmPayHVO[] originBills) {
		IEbankconfirmpayMaintain operator = (IEbankconfirmpayMaintain) NCLocator.getInstance()
				.lookup(IEbankconfirmpayMaintain.class);
		AggConfirmPayHVO[] bills = null;

		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException var7) {
			ExceptionUtils.wrappBusinessException(var7.getMessage());
		}

		
		
		return bills;
	}
}