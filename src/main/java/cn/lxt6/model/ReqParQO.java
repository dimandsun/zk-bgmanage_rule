package cn.lxt6.model;


import cn.lxt6.config.enums.ClientSourceEnum;
import cn.lxt6.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 对请求参数的封装 RequestParameters
 * @since 2020.04.09
 * @author chenzy
 */
public class ReqParQO{
	/**
	 * 由参数拼接的明文，app/net/java发起的接口，参数名为code，小程序则为wxcode
	 */
	@JsonAlias("wxcode")
	@JsonProperty("code")
	private String data;
	/**
	 * 动态密钥，
	 */
	private String sid;
	/**
	 * 验证信息：
	 * sign=MD5Secret(sid+data)
	 */
	private String sign;
	
	private String serial;

	private String message;
	
	private String token;

	private ClientSourceEnum requestType;
	/**
	 * 返回json字符串
	 * @return
	 */
	@Override
	public String toString() {
		return JsonUtil.model2Str(this);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


	public ClientSourceEnum getRequestType() {
		return requestType;
	}

	public void setRequestType(ClientSourceEnum requestType) {
		this.requestType = requestType;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
}
