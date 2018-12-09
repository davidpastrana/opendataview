package com.opendataview.web.heuristicsearch;

import java.io.IOException;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;

import com.vividsolutions.jts.geom.MultiPolygon;

public class ReadGISShapes extends MainClass {

	private static final long serialVersionUID = 1L;

public ReadGISShapes(PageParameters parameters) throws IOException {
		super(parameters);
		// TODO Auto-generated constructor stub
	}

public static double[] getNutsLatLng(String nutsCode) throws IOException, CQLException {

    org.opengis.filter.Filter filter = ECQL.toFilter("NUTS_ID = '" + nutsCode + "'");

    ShapefileDataStore dataStore = new ShapefileDataStore(MainClass.class.getResource(shapes_file));

    dataStore.setIndexed(true);
    String[] typeNames = dataStore.getTypeNames();

    String typeName = typeNames[0];

    ContentFeatureCollection collection = dataStore.getFeatureSource(typeName).getFeatures(filter);

    SimpleFeatureIterator results = collection.features();

    // log.info("Nuts code: " + nutsCode + ", File name:" + name + "\n");
    MultiPolygon mp = null;

    try {
      if (results.hasNext()) {
        mp = (MultiPolygon) results.next().getAttribute("the_geom");
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      results.close();
      dataStore.dispose();
    }

    if (mp != null) {
      com.vividsolutions.jts.geom.Point p = mp.getCentroid();
      // log.info("latitude " + p.getX());
      // log.info("longitude " + p.getY());
      return new double[] {p.getY(), p.getX()};
    }

    return null;
  }

}
