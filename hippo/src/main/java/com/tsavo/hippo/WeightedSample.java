package com.tsavo.hippo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS)
public class WeightedSample implements Sample {

	BigDecimal sample;

	BigDecimal weight;

	public WeightedSample() {
	}

	public WeightedSample(@JsonProperty("sample") BigDecimal aSample, @JsonProperty("weight") BigDecimal aWeight) {
		sample = aSample;
		weight = aWeight;
	}

	public BigDecimal getSample() {
		return sample;
	}

	public void setSample(BigDecimal sample) {
		this.sample = sample;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

}
