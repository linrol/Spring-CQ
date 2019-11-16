package xin.lz1998.cq.plugin.forward.enums;

public enum DsErrorEnum {

	DATA_ERROR("-1","数据获取失败"),
	IP_LIMIT("401","IP没有添加白名单"),
	ACCESS_LIMIT("429","访问次数超限"),
	PARAMS_ERROR("403","关键参数丢失"),
	CALL_TYPE_ERROR("405","请求方式不对"),;
	
	private String code;
    private String value;
    
    DsErrorEnum(String code,String value) {
        this.code = code;
        this.value = value;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static DsErrorEnum getByCode(String code) {
		for(DsErrorEnum error:DsErrorEnum.values()) {
			if(error.code.equals(code)) {
				return error;
			}
		}
		return null;
	}
}
