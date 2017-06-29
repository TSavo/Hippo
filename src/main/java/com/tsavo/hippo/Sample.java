package com.tsavo.hippo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS)
public interface Sample {

	public BigDecimal getSample();

	public BigDecimal getWeight();

	public void setSample(BigDecimal multiply);
	public void setWeight(BigDecimal multiply);
}
