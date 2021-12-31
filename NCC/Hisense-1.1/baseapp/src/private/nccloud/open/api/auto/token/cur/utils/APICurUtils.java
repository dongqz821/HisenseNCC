package nccloud.open.api.auto.token.cur.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import nccloud.open.api.auto.token.itf.IAPIUtils;

public class APICurUtils implements IAPIUtils {
	private String grant_type = "client";
	private String apiUrl;
	private String secret_level = "L0";
	private String pubKey;
	private String baseUrl;
	private String biz_center;
	private String client_id;
	private String client_secret;
	private String user_name;
	private String pwd;

	public String getApiUrl() {
		return this.apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getTokenByClient() throws Exception {
		Map<String, String> paramMap = new HashMap();
		paramMap.put("grant_type", "client_credentials");
		paramMap.put("client_id", this.client_id);
		paramMap.put("client_secret",
				URLEncoder.encode(Encryption.pubEncrypt(this.pubKey, this.client_secret), "UTF-8"));
		paramMap.put("biz_center", this.biz_center);
		String sign = SHA256Util.getSHA256(this.client_id + this.client_secret + this.pubKey, this.pubKey);
		paramMap.put("signature", sign);
		String url = this.baseUrl + "nccloud/opm/accesstoken";
		String mediaType = "application/x-www-form-urlencoded";
		String token = this.doPost(url, paramMap, mediaType, (Map) null, "");
		return token;
	}

	private String getTokenByRefreshToken(String refresh_token) throws Exception {
		Map<String, String> paramMap = new HashMap();
		paramMap.put("grant_type", "refresh_token");
		paramMap.put("client_id", this.client_id);
		paramMap.put("client_secret",
				URLEncoder.encode(Encryption.pubEncrypt(this.pubKey, this.client_secret), "UTF-8"));
		String sign = SHA256Util.getSHA256(this.client_id + this.client_secret + refresh_token + this.pubKey,
				this.pubKey);
		paramMap.put("signature", sign);
		paramMap.put("refresh_token", refresh_token);
		paramMap.put("biz_center", this.biz_center);
		String url = this.baseUrl + "nccloud/opm/accesstoken";
		String mediaType = "application/x-www-form-urlencoded";
		String token = this.doPost(url, paramMap, mediaType, (Map) null, "");
		return token;
	}

	public String getToken() throws Exception {
		String token = null;
		if ("password".equals(this.grant_type)) {
			token = this.getTokenByPWD();
		} else {
			if (!"client".equals(this.grant_type)) {
				throw new Exception("token?????????");
			}

			token = this.getTokenByClient();
		}

		return token;
	}

	private String getTokenByPWD() throws Exception {
		Map<String, String> paramMap = new HashMap();
		paramMap.put("grant_type", "password");
		paramMap.put("client_id", this.client_id);
		paramMap.put("client_secret",
				URLEncoder.encode(Encryption.pubEncrypt(this.pubKey, this.client_secret), "UTF-8"));
		paramMap.put("username", this.user_name);
		paramMap.put("password", URLEncoder.encode(Encryption.pubEncrypt(this.pubKey, this.pwd), "UTF-8"));
		paramMap.put("biz_center", this.biz_center);
		String sign = SHA256Util
				.getSHA256(this.client_id + this.client_secret + this.user_name + this.pwd + this.pubKey, this.pubKey);
		paramMap.put("signature", sign);
		String url = this.baseUrl + "nccloud/opm/accesstoken";
		String mediaType = "application/x-www-form-urlencoded";
		String token = this.doPost(url, paramMap, mediaType, (Map) null, "");
		return token;
	}

	private String dealResponseBody(String source, String security_key, String level) throws Exception {
		String result = null;
		if (level != null && !"".equals(level.trim()) && !"L0".equals(level)) {
			if ("L1".equals(level)) {
				result = Decryption.symDecrypt(security_key, source);
			} else if ("L2".equals(level)) {
				result = CompressUtil.gzipDecompress(source);
			} else if ("L3".equals(level)) {
				result = CompressUtil.gzipDecompress(Decryption.symDecrypt(security_key, source));
			} else {
				if (!"L4".equals(level)) {
					throw new Exception("??งน???????");
				}

				result = Decryption.symDecrypt(security_key, CompressUtil.gzipDecompress(source));
			}
		} else {
			result = source;
		}

		return result;
	}

	private String dealRequestBody(String source, String security_key, String level) throws Exception {
		String result = null;
		if (level != null && !"".equals(level.trim()) && !"L0".equals(level)) {
			if ("L1".equals(level)) {
				result = Encryption.symEncrypt(security_key, source);
			} else if ("L2".equals(level)) {
				result = CompressUtil.gzipCompress(source);
			} else if ("L3".equals(level)) {
				result = Encryption.symEncrypt(security_key, CompressUtil.gzipCompress(source));
			} else {
				if (!"L4".equals(level)) {
					throw new Exception("??งน???????");
				}

				result = CompressUtil.gzipCompress(Encryption.symEncrypt(security_key, source));
			}
		} else {
			result = source;
		}

		return result;
	}

	private String doPost(String baseUrl, Map<String, String> paramMap, String mediaType, Map<String, String> headers,
			String json) throws Exception {
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		OutputStream out = null;
		BufferedReader bufferedReader = null;
		String result = null;

		try {
			StringBuffer sb = new StringBuffer();
			sb.append(baseUrl);
			String line;
			if (paramMap != null) {
				sb.append("?");
				Iterator var13 = paramMap.entrySet().iterator();

				while (var13.hasNext()) {
					Entry<String, String> entry = (Entry) var13.next();
					String key = (String) entry.getKey();
					line = (String) entry.getValue();
					sb.append(key + "=" + line).append("&");
				}

				baseUrl = sb.toString().substring(0, sb.toString().length() - 1);
			}

			URL urlObj = new URL(baseUrl);
			urlConnection = (HttpURLConnection) urlObj.openConnection();
			urlConnection.setConnectTimeout(50000);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.addRequestProperty("content-type", mediaType);
			if (headers != null) {
				Iterator var35 = headers.keySet().iterator();

				while (var35.hasNext()) {
					String key = (String) var35.next();
					urlConnection.addRequestProperty(key, (String) headers.get(key));
				}
			}

			out = urlConnection.getOutputStream();
			out.write(json.getBytes("UTF-8"));
			out.flush();
			int resCode = urlConnection.getResponseCode();
			if (resCode != 200 && resCode != 201 && resCode != 202) {
				in = urlConnection.getErrorStream();
			} else {
				in = urlConnection.getInputStream();
			}

			bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuffer temp = new StringBuffer();

			for (line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
				temp.append(line).append("\r\n");
			}

			String ecod = urlConnection.getContentEncoding();
			if (ecod == null) {
				ecod = Charset.forName("UTF-8").name();
			}

			result = new String(temp.toString().getBytes("UTF-8"), ecod);
			return result;
		} catch (Exception var30) {
			throw var30;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException var29) {
					throw var29;
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException var28) {
					throw var28;
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException var27) {
					throw var27;
				}
			}

			urlConnection.disconnect();
		}
	}

	public String getGrant_type() {
		return this.grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

	public String getAPIRetrun(String token, String json) throws Exception {
		APIReturnEntity tokenJson = (APIReturnEntity) JSON.parseObject(token, APIReturnEntity.class);
		if (tokenJson != null && tokenJson.getData() != null && tokenJson.getData() instanceof JSONObject) {
			String access_token = ((JSONObject) tokenJson.getData()).getString("access_token");
			String security_key = ((JSONObject) tokenJson.getData()).getString("security_key");
			String url = this.baseUrl + this.apiUrl;
			HashMap headermap = new HashMap();
			headermap.put("access_token", access_token);
			headermap.put("client_id", this.client_id);
			StringBuffer sb = new StringBuffer();
			sb.append(this.client_id);
			if (json != null) {
				sb.append(json);
			}

			sb.append(this.pubKey);
			String sign = SHA256Util.getSHA256(sb.toString(), this.pubKey);
			headermap.put("signature", sign);
			headermap.put("repeat_check", "Y");
			headermap.put("ucg_flag", "y");
			String mediaType = "application/json;charset=utf-8";
			String requestBody = this.dealRequestBody(json, security_key, this.secret_level);
			String result = this.doPost(url, (Map) null, mediaType, headermap, requestBody);
			String responseBody = this.dealResponseBody(result, security_key, this.secret_level);
			return responseBody;
		} else {
			throw new Exception("???token???:" + token);
		}
	}

	public void init(String ip, String port, String biz_center, String client_id, String client_secret, String pubKey,
			String user_name, String pwd) {
		this.baseUrl = "https://" + ip + ":" + port + "/";
		this.biz_center = biz_center;
		this.client_id = client_id;
		this.client_secret = client_secret;
		this.pubKey = pubKey;
		this.user_name = user_name;
		this.pwd = pwd;
	}

	public static void main(String[] args) {
		APICurUtils util = new APICurUtils();

		try {
			String token = util.getToken();
			System.out.println(token);
			util.setApiUrl("nccloud/api/riaorg/orgmanage/org/queryOrgByCode");
			String json = "{\"code\": [\"01\", \"T2001\"]}";
			util.getAPIRetrun(token, json);
		} catch (Exception var4) {
			var4.printStackTrace();
		}

	}
}