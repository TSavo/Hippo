package com.tsavo.hippo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class ExponentialWeightedMovingAverageFunction extends WeightedMovingAverageFunction {

	BigDecimal weight = new BigDecimal(0.8f);

	public ExponentialWeightedMovingAverageFunction(int aWindowSize){
		super(aWindowSize);
	}

	public ExponentialWeightedMovingAverageFunction(int aWindowSize, float aWeight) {
		super(aWindowSize);
		weight = new BigDecimal(1f - (2.0f / (aWeight + 1.0f)));
	}

	@Override
	public void addSample(Sample aSample) {
		samples.forEach(x -> x.setSample(x.getSample().multiply(weight)));
		super.addSample(aSample);
	}
}
