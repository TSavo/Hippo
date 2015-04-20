package com.tsavo.hippo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class MovingAverageFunction implements AverageFunction {

	public List<Sample> samples = new ArrayList<>();
	public int windowSize;

	public MovingAverageFunction(int aWindowSize) {
		windowSize = aWindowSize;
	}

	public void addSample(Sample aSample) {
		samples.add(aSample);
		while (samples.size() > windowSize) {
			samples.remove(0);
		}
	}

	public BigDecimal getAverage() {
		return samples.stream().map(x -> x.getSample()).reduce((x, y) -> x.add(y)).get().divide(new BigDecimal(samples.size()), 8, RoundingMode.HALF_DOWN);
	}
}
