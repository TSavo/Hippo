package com.tsavo.hippo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class WeightedMovingAverageFunction extends MovingAverageFunction {

	public WeightedMovingAverageFunction(int aWindowSize) {
		super(aWindowSize);
	}

	public BigDecimal getAverage() {
		return samples.stream().map(x -> x.getSample().multiply(x.getWeight())).reduce((x, y) -> x.add(y)).get().divide(new BigDecimal(samples.size()), 8, RoundingMode.HALF_DOWN);
	}
}
