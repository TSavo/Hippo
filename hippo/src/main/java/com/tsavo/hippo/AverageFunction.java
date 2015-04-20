package com.tsavo.hippo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface AverageFunction {

	public BigDecimal getAverage();

	public void addSample(Sample aSample);
}
