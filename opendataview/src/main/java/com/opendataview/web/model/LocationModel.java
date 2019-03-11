package com.opendataview.web.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "locations")
@Table(name = "locations")
public class LocationModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "locations_sequence", sequenceName = "locations_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_sequence")
	@Column(name = "id", updatable = false, nullable = false)
	protected Long id;
	@Column(columnDefinition = "TEXT")
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
	@Column(columnDefinition = "FLOAT", precision = 10, scale = 6)
	protected Float latitude;
	@Column(columnDefinition = "FLOAT", precision = 10, scale = 6)
	protected Float longitude;
	@Column(columnDefinition = "TEXT")
	private String website;
	@Column(columnDefinition = "TEXT")
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
	protected String csvName;
	@Column(columnDefinition = "TEXT")
	protected String source;
	@Column(columnDefinition = "TEXT")
	protected String username;
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
	@Column(columnDefinition = "TEXT")
	protected String private_mode;
//	protected double rating = 0;
//	private int nrating = 0;

	@Column(columnDefinition = "TEXT")
	protected String street;
	@Column(columnDefinition = "TEXT")
	protected String number;

	public Long getId() {
		return id;
	}

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

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
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

//	public double getRating() {
//		return rating;
//	}
//
//	public void setRating(double rating) {
//		this.rating = rating;
//	}
//
//	public int getNrating() {
//		return nrating;
//	}
//
//	public void setNrating(int nrating) {
//		this.nrating = nrating;
//	}

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

	public String getPrivate_mode() {
		return private_mode;
	}

	public void setPrivate_mode(String private_mode) {
		this.private_mode = private_mode;
	}

}
