package com.opendataview.web.api;

import java.util.ArrayList;
import java.util.List;

import com.opendataview.web.model.LocationModel;

public class LocationStaticData {
	public static List<LocationModel> loc;

	public void init() {
		loc.clear();
		loc = new ArrayList<LocationModel>();
	}

	public void addLocToList(LocationModel l) {
		loc.add(l);
	}

	public List<LocationModel> getLoc() {
		return loc;
	}

}
