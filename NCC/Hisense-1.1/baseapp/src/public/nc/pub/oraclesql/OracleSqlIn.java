package nc.pub.oraclesql;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class OracleSqlIn {

	/**
	 * @param ids in����еļ��϶���
	 * @param count in����г��ֵ���������
	 * @param field in����Ӧ�����ݿ��ѯ�ֶ�
	 * @return ���� field in (...) or field in (...) �ַ���
	 */
	public static String getInSqlByPks(String[] pks, int len, String field) {
		StringBuffer sql = new StringBuffer("");
		if (len <= 0 || len>1000) {
			len = 1000;
		}
		if (pks.length > len) {
        	int count = pks.length/len;
        	for (int i = 0; i <= count; i++) {
        		String[] tempPks = subArrayFree(pks, len*i, len*(i+1));
        		if (tempPks!=null && tempPks.length>0) {
        			if (i > 0) {
        				sql.append(" or ");
        			}
        			sql.append(field+arrayToSqlCond(tempPks, true));
        		}
			}
        } else {
        	sql.append(field+arrayToSqlCond(pks, true));
        }
		return sql.toString();
	}
	/**
	 * @description ��ȡ����
	 * @param arr   Ҫ��ȡ������
	 * @param begin ��ʼ��ȡ�±�
	 * @param end   ������ȡ�±�
	 * @return
	 */
    public static String[] subArrayFree(String[] arr, int begin, int end) {
        if (arr == null)
            return null;
        if (arr.length == 0 || (arr.length - 1) < begin)
            return null;
        if (end > arr.length)
        	end = arr.length;
        ArrayList<String> list = new ArrayList<String>();
        for (int i = begin; i < end; i++){
            list.add(arr[i]);
        }
        return list.toArray(new String[0]);
    }
    /**
     * ����ת��Ϊsql��ѯ����
     * @param arr ��������
     * @param isIn  �Ƿ��ǰ�����in�����߲�������not in��
     * @return
     */
    public static String arrayToSqlCond(String[] arr, boolean isIn) {
        StringBuffer cond = new StringBuffer("");
        if (arr != null) {
            int len = arr.length;
            if (len == 1) {
                if (isIn) {
                	cond.append(" = '");
                } else {
                	cond.append(" != '");
                }
                cond.append(arr[0]).append("'");
            } else if (len > 1) {
                if (isIn) {
                	cond.append(" in ('");
                } else {
                	cond.append(" not in ('");
                }
                for (int i = 0; i < len; i++) {
                	cond.append(arr[i]);
                    if (i < len - 1)
                    	cond.append("','");
                }
                cond.append("')");
            }
        }
        return cond.toString();
    }
	/**
	 * @param ids in����еļ��϶���
	 * @param count in����г��ֵ���������
	 * @param field in����Ӧ�����ݿ��ѯ�ֶ�
	 * @return ���� field in (...) or field in (...) �ַ���
	 */
	public static String getOracleSQLIn(List<String> ids, int count, String field) {
	    count = Math.min(count, 1000);
	    int len = ids.size();
	    int size = len % count;
	    if (size == 0) {
	        size = len / count;
	    } else {
	        size = (len / count) + 1;
	    }
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < size; i++) {
	        int fromIndex = i * count;
	        int toIndex = Math.min(fromIndex + count, len);
	        //System.out.println(ids.subList(fromIndex, toIndex));
	        String productId = StringUtils.defaultIfEmpty(StringUtils.join(ids.subList(fromIndex, toIndex), "','"), "");
	        if (i != 0) {
	            builder.append(" or ");
	        }
	        builder.append(field).append(" in ('").append(productId).append("')");
	    }
	    
	    return StringUtils.defaultIfEmpty(builder.toString(), field + " in ('')");
	}
}
