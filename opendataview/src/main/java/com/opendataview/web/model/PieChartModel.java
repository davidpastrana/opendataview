package com.opendataview.web.model;

import java.io.Serializable;

public class PieChartModel implements Serializable, Comparable<PieChartModel> {

	private static final long serialVersionUID = 1L;

	private String attribute;
	private Integer repetitions;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Integer getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(Integer repetitions) {
		this.repetitions = repetitions;
	}

	@Override
	public int compareTo(PieChartModel o) {
		return this.getRepetitions().compareTo(o.getRepetitions());
	}

}
