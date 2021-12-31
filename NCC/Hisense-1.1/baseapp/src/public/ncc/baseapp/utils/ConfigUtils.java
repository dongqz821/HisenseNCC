package ncc.baseapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class ConfigUtils {
	
	public String getValueFromProperties(String field) 
    {
      InputStream in = null;
      String value = "";
      try
      {
        Properties prop = new Properties();
        in = ConfigUtils.class.getResourceAsStream("/ncc/baseapp/utils/config.properties");
        prop.load(in);
        value = prop.getProperty(field, "");
      }
      catch (Exception e)
      {
        Logger.error(e.getMessage());
        if (in != null) {
          try
          {
            in.close();
          }
          catch (IOException ex)
          {
            Logger.error(ex.getMessage());
          }
        }
      }
      finally
      {
        if (in != null) {
          try
          {
            in.close();
          }
          catch (IOException e)
          {
            Logger.error(e.getMessage());
          }
        }
      }
      return value;
    }
	
	public static boolean getDefDoc(String defdoccode){
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		boolean bpmenable = false;
		try {
			String memo = "";
			StringBuffer sql = new StringBuffer("select b.enablestate from bd_defdoclist a")
				.append(" inner join bd_defdoc b on a.pk_defdoclist = b.pk_defdoclist")
				.append(" where a.code = 'UAP002' and b.code = '" + defdoccode + "'");
			Object[] objs = (Object[]) query.executeQuery(sql.toString(), new ArrayProcessor());
			if(objs!=null && objs.length>0){
				if(objs[0]!=null){
					memo = objs[0].toString();
				}
			}
			bpmenable = "2".equals(memo)?true:false;
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException("查询用户自定义档案中接口配置参数是否启用发生异常："+e.getMessage());
		}
		return bpmenable;
		
	}
}