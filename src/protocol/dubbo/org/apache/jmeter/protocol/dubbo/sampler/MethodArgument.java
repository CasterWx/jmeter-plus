package org.apache.jmeter.protocol.dubbo.sampler;


import org.apache.jmeter.protocol.dubbo.util.JsonUtils;
import org.apache.jmeter.protocol.dubbo.util.StringUtils;

import java.io.Serializable;


/**
 * MethodArgument
 */
public class MethodArgument implements Serializable {

	private static final long serialVersionUID = -2567457932227227262L;
	private String paramType;
	private String paramValue;

	public MethodArgument(String paramType, String paramValue) {
		setParamType(paramType);
		setParamValue(paramValue);
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = (paramType == null ? null : StringUtils.trimAllWhitespace(paramType));
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = (paramValue == null ? null : StringUtils.trimWhitespace(paramValue));
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paramType == null) ? 0 : paramType.hashCode());
		result = prime * result + ((paramValue == null) ? 0 : paramValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodArgument other = (MethodArgument) obj;
		if (paramType == null) {
			if (other.paramType != null)
				return false;
		} else if (!paramType.equals(other.paramType))
			return false;
		if (paramValue == null) {
			if (other.paramValue != null)
				return false;
		} else if (!paramValue.equals(other.paramValue))
			return false;
		return true;
	}

}
