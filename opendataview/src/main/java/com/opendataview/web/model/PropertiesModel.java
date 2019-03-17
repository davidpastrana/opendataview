package com.opendataview.web.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "properties")
public class PropertiesModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "properties_sequence", sequenceName = "properties_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "properties_sequence")
	@Column(name = "id", updatable = false, nullable = false)
	protected Long id;
	@Column(columnDefinition = "TEXT")
	protected String mapsAPI;
	@Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected boolean executeSQLqueries;
	@Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected boolean autodetectSchema;
	@Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected boolean geonamesdebugmode;
	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	protected boolean fieldtypesdebugmode;
	@Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected boolean active_dictionary;
	@Column(columnDefinition = "TEXT")
	protected String dictionary_matches;
	@Column(columnDefinition = "TEXT")
	protected String nrowchecks;
	@Column(columnDefinition = "TEXT")
	protected String pvalue_nrowchecks;
	@Column(columnDefinition = "TEXT")
	protected String imageRegex;
	@Column(columnDefinition = "TEXT")
	protected String phoneRegex;
	@Column(columnDefinition = "TEXT")
	protected String cityRegex;
	@Column(columnDefinition = "TEXT")
	protected String archiveRegex;
	@Column(columnDefinition = "TEXT")
	protected String documentRegex;
	@Column(columnDefinition = "TEXT")
	protected String openinghoursRegex;
	@Column(columnDefinition = "TEXT")
	protected String dateRegex;
	@Column(columnDefinition = "TEXT")
	protected String yearRegex;
	@Column(columnDefinition = "TEXT")
	protected String currencyRegex;
	@Column(columnDefinition = "TEXT")
	protected String percentageRegex;
	@Column(columnDefinition = "TEXT")
	protected String postcodeRegex;
	@Column(columnDefinition = "TEXT")
	protected String nutsRegex;
	@Column(columnDefinition = "TEXT")
	protected String shapeRegex;
	@Column(columnDefinition = "TEXT")
	protected String latitudeRegex;
	@Column(columnDefinition = "TEXT")
	protected String longitudeRegex;
	@Column(columnDefinition = "TEXT")
	protected String latlngRegex;
	@Column(columnDefinition = "TEXT")
	protected String possiblenameRegex;
	@Column(columnDefinition = "TEXT")
	protected String descriptionRegex;
	@Column(columnDefinition = "TEXT")
	protected String iconmarker;
	@Column(columnDefinition = "TEXT")
	protected String countrycode;
	@Column(columnDefinition = "TEXT")
	protected String geonames_dbdriver;
	@Column(columnDefinition = "TEXT")
	protected String geonames_dburl;
	@Column(columnDefinition = "TEXT")
	protected String geonames_dbusr;
	@Column(columnDefinition = "TEXT")
	protected String geonames_dbpwd;
	@Column(columnDefinition = "TEXT")
	protected String web_dbdriver;
	@Column(columnDefinition = "TEXT")
	protected String web_dburl;
	@Column(columnDefinition = "TEXT")
	protected String web_dbusr;
	@Column(columnDefinition = "TEXT")
	protected String web_dbpwd;
	@Column(columnDefinition = "TEXT")
	protected String st1postcode;
	@Column(columnDefinition = "TEXT")
	protected String st1city;
	@Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected boolean private_mode;
	@Column(columnDefinition = "TEXT")
	protected String username;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMapsAPI() {
		return mapsAPI;
	}

	public void setMapsAPI(String mapsAPI) {
		this.mapsAPI = mapsAPI;
	}

	public boolean getExecuteSQLqueries() {
		return executeSQLqueries;
	}

	public void setExecuteSQLqueries(boolean executeSQLqueries) {
		this.executeSQLqueries = executeSQLqueries;
	}

	public boolean getAutoDetectSchema() {
		return autodetectSchema;
	}

	public void setAutoDetectSchema(boolean autodetectSchema) {
		this.autodetectSchema = autodetectSchema;
	}

	public boolean getGeonamesdebugmode() {
		return geonamesdebugmode;
	}

	public void setGeonamesdebugmode(boolean geonamesdebugmode) {
		this.geonamesdebugmode = geonamesdebugmode;
	}

	public boolean getFieldtypesdebugmode() {
		return fieldtypesdebugmode;
	}

	public void setFieldtypesdebugmode(boolean fieldtypesdebugmode) {
		this.fieldtypesdebugmode = fieldtypesdebugmode;
	}

	public boolean getActiveDictionary() {
		return active_dictionary;
	}

	public void setActiveDictionary(boolean active_dictionary) {
		this.active_dictionary = active_dictionary;
	}

	public String getDictionaryMatches() {
		return dictionary_matches;
	}

	public void setDictionaryMatches(String dictionary_matches) {
		this.dictionary_matches = dictionary_matches;
	}

	public String getNrowchecks() {
		return nrowchecks;
	}

	public void setNrowchecks(String nrowchecks) {
		this.nrowchecks = nrowchecks;
	}

	public String getPvalue_nrowchecks() {
		return pvalue_nrowchecks;
	}

	public void setPvalue_nrowchecks(String pvalue_nrowchecks) {
		this.pvalue_nrowchecks = pvalue_nrowchecks;
	}

	public String getImageRegex() {
		return imageRegex;
	}

	public void setImageRegex(String imageRegex) {
		this.imageRegex = imageRegex;
	}

	public String getPhoneRegex() {
		return phoneRegex;
	}

	public void setPhoneRegex(String phoneRegex) {
		this.phoneRegex = phoneRegex;
	}

	public String getCityRegex() {
		return cityRegex;
	}

	public void setCityRegex(String cityRegex) {
		this.cityRegex = cityRegex;
	}

	public String getArchiveRegex() {
		return archiveRegex;
	}

	public void setArchiveRegex(String archiveRegex) {
		this.archiveRegex = archiveRegex;
	}

	public String getDocumentRegex() {
		return documentRegex;
	}

	public void setDocumentRegex(String documentRegex) {
		this.documentRegex = documentRegex;
	}

	public String getOpeninghoursRegex() {
		return openinghoursRegex;
	}

	public void setOpeninghoursRegex(String openinghoursRegex) {
		this.openinghoursRegex = openinghoursRegex;
	}

	public String getDateRegex() {
		return dateRegex;
	}

	public void setDateRegex(String dateRegex) {
		this.dateRegex = dateRegex;
	}

	public String getYearRegex() {
		return yearRegex;
	}

	public void setYearRegex(String yearRegex) {
		this.yearRegex = yearRegex;
	}

	public String getCurrencyRegex() {
		return currencyRegex;
	}

	public void setCurrencyRegex(String currencyRegex) {
		this.currencyRegex = currencyRegex;
	}

	public String getPercentageRegex() {
		return percentageRegex;
	}

	public void setPercentageRegex(String percentageRegex) {
		this.percentageRegex = percentageRegex;
	}

	public String getPostcodeRegex() {
		return postcodeRegex;
	}

	public void setPostcodeRegex(String postcodeRegex) {
		this.postcodeRegex = postcodeRegex;
	}

	public String getNutsRegex() {
		return nutsRegex;
	}

	public void setNutsRegex(String nutsRegex) {
		this.nutsRegex = nutsRegex;
	}

	public String getShapeRegex() {
		return shapeRegex;
	}

	public void setShapeRegex(String shapeRegex) {
		this.shapeRegex = shapeRegex;
	}

	public String getLatitudeRegex() {
		return latitudeRegex;
	}

	public void setLatitudeRegex(String latitudeRegex) {
		this.latitudeRegex = latitudeRegex;
	}

	public String getLongitudeRegex() {
		return longitudeRegex;
	}

	public void setLongitudeRegex(String longitudeRegex) {
		this.longitudeRegex = longitudeRegex;
	}

	public String getLatlngRegex() {
		return latlngRegex;
	}

	public void setLatlngRegex(String latlngRegex) {
		this.latlngRegex = latlngRegex;
	}

	public String getPossiblenameRegex() {
		return possiblenameRegex;
	}

	public void setPossiblenameRegex(String descriptionRegex) {
		this.descriptionRegex = descriptionRegex;
	}

	public String getDescriptionRegex() {
		return descriptionRegex;
	}

	public void setDescriptioneRegex(String possiblenameRegex) {
		this.possiblenameRegex = possiblenameRegex;
	}

	public String getIconmarker() {
		return iconmarker;
	}

	public void setIconmarker(String iconmarker) {
		this.iconmarker = iconmarker;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public String getGeonames_dbdriver() {
		return geonames_dbdriver;
	}

	public void setGeonames_dbdriver(String geonames_dbdriver) {
		this.geonames_dbdriver = geonames_dbdriver;
	}

	public String getGeonames_dburl() {
		return geonames_dburl;
	}

	public void setGeonames_dburl(String geonames_dburl) {
		this.geonames_dburl = geonames_dburl;
	}

	public String getGeonames_dbusr() {
		return geonames_dbusr;
	}

	public void setGeonames_dbusr(String geonames_dbusr) {
		this.geonames_dbusr = geonames_dbusr;
	}

	public String getGeonames_dbpwd() {
		return geonames_dbpwd;
	}

	public void setGeonames_dbpwd(String geonames_dbpwd) {
		this.geonames_dbpwd = geonames_dbpwd;
	}

	public String getWeb_dbdriver() {
		return web_dbdriver;
	}

	public void setWeb_dbdriver(String web_dbdriver) {
		this.web_dbdriver = web_dbdriver;
	}

	public String getWeb_dburl() {
		return web_dburl;
	}

	public void setWeb_dburl(String web_dburl) {
		this.web_dburl = web_dburl;
	}

	public String getWeb_dbusr() {
		return web_dbusr;
	}

	public void setWeb_dbusr(String web_dbusr) {
		this.web_dbusr = web_dbusr;
	}

	public String getWeb_dbpwd() {
		return web_dbpwd;
	}

	public void setWeb_dbpwd(String web_dbpwd) {
		this.web_dbpwd = web_dbpwd;
	}

	public String getSt1postcode() {
		return st1postcode;
	}

	public void setSt1postcode(String st1postcode) {
		this.st1postcode = st1postcode;
	}

	public String getSt1city() {
		return st1city;
	}

	public void setSt1city(String st1city) {
		this.st1city = st1city;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean getPrivate_mode() {
		return private_mode;
	}

	public void setPrivate_mode(boolean private_mode) {
		this.private_mode = private_mode;
	}
}
