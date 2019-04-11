package com.opendataview.web.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.SpatialMode;
import org.hibernate.search.annotations.Store;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Indexed;

@Spatial(spatialMode = SpatialMode.HASH)
@Indexed
@Entity(name = "locations")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "latitude", "longitude" }))
public class LocationModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "locations_sequence", sequenceName = "locations_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_sequence")
	@Column(name = "id", updatable = false, nullable = false)
	protected Long id;

	public LocationModel(String id, String address) {
		this.id = Long.valueOf(id);
		this.address = address;
	}

	public LocationModel(String id, String address, String archive, String city, String country, String csvname,
			String currency, String date, String date_published, String date_updated, String description,
			String document, String elevation, String email, String iconmarker, String latitude, String longitude,
			String name, String number, String otherinfo, String percentage, String phone, String population,
			String postcode, String private_mode, String schedule, String source, String street, String type,
			String urlimage, String username, String website, String year) {
		this.id = Long.valueOf(id);
		this.name = name;
		this.csvName = csvname;
		this.website = website;
		this.phone = phone;
		this.email = email;
		this.description = description;
		this.latitude = BigDecimal.valueOf(Double.parseDouble(latitude)).setScale(6, RoundingMode.DOWN);
		this.longitude = BigDecimal.valueOf(Double.parseDouble(longitude)).setScale(6, RoundingMode.DOWN);
		this.iconmarker = iconmarker;
		this.date = date;
		this.date_published = date_published;
		this.date_updated = date_updated;
		this.private_mode = Boolean.valueOf(private_mode);
		this.username = username;
		this.source = source;
		this.schedule = schedule;
		this.otherInfo = otherinfo;
		this.address = address;
		this.street = street;
		this.number = number;
		this.postcode = postcode;
		this.city = city;
		this.country = country;
		this.population = population;
		this.elevation = elevation;
		this.year = year;
		this.archive = archive;
		this.currency = currency;
		this.percentage = percentage;
		this.document = document;
		this.archive = archive;
		this.urlImage = urlimage;
		this.type = type;
	}

	@Field(store = Store.YES, index = Index.YES)
	@Column(name = "name", columnDefinition = "TEXT")
	protected String name;
	@Column(columnDefinition = "TEXT")
	protected String type;
	@Column(columnDefinition = "TEXT")
	protected String address;
	@Column(columnDefinition = "TEXT")
	protected String city;
	@Column(columnDefinition = "TEXT")
	protected String postcode;
	@Column(columnDefinition = "TEXT")
	protected String country;
	@Column(columnDefinition = "TEXT")
	protected String elevation;
	@Column(columnDefinition = "TEXT")
	protected String population;

	@Field(store = Store.YES, index = Index.YES)
	@Column(precision = 10, scale = 6)
	@Type(type = "big_decimal")
	@Latitude
	protected BigDecimal latitude;

	@Field(store = Store.YES, index = Index.YES)
	@Column(precision = 10, scale = 6)
	@Type(type = "big_decimal")
	@Longitude
	protected BigDecimal longitude;
	@Column(name = "website", columnDefinition = "TEXT")
	private String website;
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	@Column(columnDefinition = "TEXT")
	private String email;
	@Column(columnDefinition = "TEXT")
	private String phone;
	@Column(columnDefinition = "TEXT")
	protected String date;
	@Column(columnDefinition = "TEXT")
	protected String date_updated;
	@Column(columnDefinition = "TEXT")
	protected String date_published;
	@Column(columnDefinition = "TEXT")
	protected String schedule;
	@Column(columnDefinition = "TEXT")
	protected String urlImage;
	@Column(columnDefinition = "TEXT")

	@Field(store = Store.YES, index = Index.YES)
	protected String csvName;
	@Column(columnDefinition = "TEXT")
	protected String source;
	@Column(columnDefinition = "TEXT")
	protected String username;

	@Field(store = Store.YES, index = Index.YES)
	@Column(columnDefinition = "TEXT")
	protected String otherInfo;
	@Column(columnDefinition = "TEXT")
	protected String year;
	@Column(columnDefinition = "TEXT")
	protected String archive;
	@Column(columnDefinition = "TEXT")
	protected String document;
	@Column(columnDefinition = "TEXT")
	protected String currency;
	@Column(columnDefinition = "TEXT")
	protected String iconmarker;
	@Column(columnDefinition = "TEXT")
	protected String percentage;
	@Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected boolean private_mode;
	@Column(columnDefinition = "TEXT")
	protected String street;
	@Column(columnDefinition = "TEXT")
	protected String number;

	public LocationModel() {
	}

	public Long getId() {
		return id;
	}

//	@Spatial(spatialMode = SpatialMode.HASH)
//	public Coordinates getLocation() {
//		return new Coordinates() {
//			@Override
//			public Double getLatitude() {
//				return latitude;
//			}
//
//			@Override
//			public Double getLongitude() {
//				return longitude;
//			}
//		};
//	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getElevation() {
		return elevation;
	}

	public void setElevation(String elevation) {
		this.elevation = elevation;
	}

	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public BigDecimal getLatitude() {
		return latitude.setScale(6, RoundingMode.UNNECESSARY);
	}

	public BigDecimal getLongitude() {
		return longitude.setScale(6, RoundingMode.UNNECESSARY);
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude.setScale(6, RoundingMode.UNNECESSARY);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude.setScale(6, RoundingMode.UNNECESSARY);
	}

	public void setLatitude(String latitude) {
		this.latitude = BigDecimal.valueOf(Double.parseDouble(latitude)).setScale(6, RoundingMode.DOWN);
	}

	public void setLongitude(String longitude) {
		this.longitude = BigDecimal.valueOf(Double.parseDouble(longitude)).setScale(6, RoundingMode.DOWN);
	}

	public void setLatitude(double latitude) {
		this.latitude = BigDecimal.valueOf(latitude).setScale(6, RoundingMode.DOWN);
	}

	public void setLongitude(double longitude) {
		this.longitude = BigDecimal.valueOf(longitude).setScale(6, RoundingMode.DOWN);
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate_updated() {
		return date_updated;
	}

	public void setDate_updated(String date_updated) {
		this.date_updated = date_updated;
	}

	public String getDate_published() {
		return date_published;
	}

	public void setDate_published(String date_published) {
		this.date_published = date_published;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	public String getCsvName() {
		return csvName;
	}

	public void setCsvName(String csvName) {
		this.csvName = csvName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIconmarker() {
		return iconmarker;
	}

	public void setIconmarker(String iconmarker) {
		this.iconmarker = iconmarker;
	}

	public boolean getPrivate_mode() {
		return private_mode;
	}

	public void setPrivate_mode(boolean private_mode) {
		this.private_mode = private_mode;
	}
}
