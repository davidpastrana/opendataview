function callStreetView(lat,lng) {
    //var url = 'https://a.mapillary.com/v3/images?client_id=bjRlSHBOc1c0enFCWmZodWxReEx3QTpkZDE0MDMxZTdlM2I1ZmYw&closeto='+lng+','+lat+'&radius=300&per_page=1';	
	if(mly === undefined)
		mly = new Mapillary.Viewer('mly2','LUxmM1dLdzhHNVRXTDhyVkJyOUZyZzpkMGJhNGY4NWMzMjQ0MjJi',null,{ cover: false });
    mly.moveCloseTo(lat, lng);
}
function getSearchParams(k){
	 var p={};
	 location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){p[k]=v})
	 return k?p[k]:p;
}
function showSidebar(id) {
  $('#idInput2').val(id);
  if($('#active_user').text().split(": ")[1] === $('#userPublished2').text()) {$('.iconEditInfo').show()} else {$('.iconEditInfo').hide()}
  if($('#sidebar').not('.active')){$('#sidebar').addClass('active');$('.overlay').addClass('active');}
  $('#showMarkerInfoBttn').trigger('click');
}
function customMarker() {
	return 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="40" height="40" viewBox="-100 -100 200 200"><defs><g id="a" transform="rotate(65)"></g></defs><g fill="#cc464600"><circle fill="#333" r="50px" /><circle fill="#ef3737" r="39" /><circle r="100" /><use xlink:href="#a"/><g transform="rotate(120)"><use xlink:href="#a"/></g><g transform="rotate(240)"><use xlink:href="#a"/></g></g></svg>');
}
function onLocationFound(e) {
	if(getSearchParams("coords") === undefined) {
    $('#polygonCoordInput').val(e.latlng.toString().replace(/\LatLng|\s+/g, ''));
    $('#savePolygonCoordinates').trigger('click');
	} else {
	    var iconMarker = L.icon({
	    	  draggable: true,
			  iconUrl: customMarker()
		  });
	    var latlng = getSearchParams('coords').split(',');
	    var lat = latlng[0].replace(/%28|\(/, '');
	    var lng = latlng[1].replace(/%29|\)/, '');
	    latlng = new L.latLng(lat,lng);
	    if(getSearchParams('coords').split(',').length == 2) {
		    marker = L.marker(latlng, {icon: iconMarker})
		      .bindPopup('Drag your current location<br>Control the distance visibility from the top left corner',{'maxWidth': '400', 'width': '200', 'className' : 'current-location-popup'})
		      .openPopup()
		      .on('mouseover', function (e) {this.openPopup();})
			  .on('mouseout', function (e) {this.closePopup();});
	        marker.on("dragend",function(e){
	            $('#polygonCoordInput').val(e.target.getLatLng().toString().replace(/\LatLng|\s+/g, ''));
	            $('#savePolygonCoordinates').trigger('click');
	        }); 
		    $(".slider_wrapper").show();
		    marker.addTo(map2).dragging.enable();
	    }
	}
}
function onLocationError(e) {
    alert(e.message);
}
function myWindroseMarker(data) {
	var content = '<canvas id="id_' + data.id + '" width="50" height="50"></canvas>';
	var icon = L.divIcon({html: content, iconSize: [50,50], className: 'owm-div-windrose'});
	return L.marker([data.coord.Lat, data.coord.Lon], {icon: icon, clickable: false});
}
function myWindroseDrawCanvas(data, owm) {

	var canvas = document.getElementById('id_' + data.id);
	canvas.title = data.name;
	var angle = 0;
	var speed = 0;
	var gust = 0;
	if (typeof data.wind != 'undefined') {
		if (typeof data.wind.speed != 'undefined') {
			canvas.title += ', ' + data.wind.speed + ' m/s';
			canvas.title += ', ' + owm._windMsToBft(data.wind.speed) + ' BFT';
			speed = data.wind.speed;
		}
		if (typeof data.wind.deg != 'undefined') {
			//canvas.title += ', ' + data.wind.deg + '°';
			canvas.title += ', ' + owm._directions[(data.wind.deg/22.5).toFixed(0)];
			angle = data.wind.deg;
		}
		if (typeof data.wind.gust != 'undefined') {
			gust = data.wind.gust;
		}
	}
	if (canvas.getContext && speed > 0) {
		var red = 0;
		var green = 0;
		if (speed <= 10) {
			green = 10*speed+155;
			red = 255*speed/10.0;
		} else {
			red = 255;
			green = 255-(255*(Math.min(speed, 21)-10)/11.0);
		}
		var ctx = canvas.getContext('2d');
		ctx.translate(25, 25);
		ctx.rotate(angle*Math.PI/180);
		ctx.fillStyle = 'rgb(' + Math.floor(red) + ',' + Math.floor(green) + ',' + 0 + ')';
		ctx.beginPath();
		ctx.moveTo(-15, -25);
		ctx.lineTo(0, -10);
		ctx.lineTo(15, -25);
		ctx.lineTo(0, 25);
		ctx.fill();

		// draw inner arrow for gust
		if (gust > 0 && gust != speed) {
			if (gust <= 10) {
				green = 10*gust+155;
				red = 255*gust/10.0;
			} else {
				red = 255;
				green = 255-(255*(Math.min(gust, 21)-10)/11.0);
			}
			canvas.title += ', gust ' + data.wind.gust + ' m/s';
			canvas.title += ', ' + owm._windMsToBft(data.wind.gust) + ' BFT';
			ctx.fillStyle = 'rgb(' + Math.floor(red) + ',' + Math.floor(green) + ',' + 0 + ')';
			ctx.beginPath();
			ctx.moveTo(-15, -25);
			ctx.lineTo(0, -10);
			//ctx.lineTo(15, -25);
			ctx.lineTo(0, 25);
			ctx.fill();
		}
	} else {
		canvas.innerHTML = '<div>'
				+ (typeof data.wind != 'undefined' && typeof data.wind.deg != 'undefined' ? data.wind.deg + '°' : '')
				+ '</div>';
	}
}
function windroseAdded(e) {
	for (var i in this._markers) {
		var m = this._markers[i];
		var cv = document.getElementById('id_' + m.options.owmId);
		for (var j in this._cache._cachedData.list) {
			var station = this._cache._cachedData.list[j];
			if (station.id == m.options.owmId) {
				myWindroseDrawCanvas(station, this);
			}
		}
	}
}
function editMarkerInfo() {
	$('#idInput').val($('#location_id').text());
	$('#editInfoButton').trigger('click');
}
function fromHex(hex) {
    if (hex.length < 6) return null;
    hex = hex.toLowerCase();

    if (hex[0] === '#') {
      hex = hex.substring(1, hex.length);
    }
    var r = parseInt(hex[0] + hex[1], 16),
      g = parseInt(hex[2] + hex[3], 16),
      b = parseInt(hex[4] + hex[5], 16);

    return {r: r / 255, g: g / 255, b: b / 255};
  };
  function displayPosition(position) {
	  //map2.locate({setView: true, maxZoom: 16});
	    var iconMarker = L.icon({draggable: true,iconUrl: customMarker()});
	  	var lat = position.coords.latitude;
	  	var lng = position.coords.longitude;
	    latlng = new L.latLng(lat,lng);
	    
		if(getSearchParams("coords") === undefined) {
		    $('#polygonCoordInput').val("\("+lat+","+lng+"\)");
		    $('#savePolygonCoordinates').trigger('click');
		} 
	    
//		    marker = L.marker(latlng, {icon: iconMarker})
//		      .bindPopup('Drag your current location<br>Control the distance visibility from the top left corner',{'maxWidth': '400', 'width': '200', 'className' : 'current-location-popup'})
//		      .openPopup()
//		      .on('mouseover', function (e) {this.openPopup();})
//			  .on('mouseout', function (e) {this.closePopup();});
//	        marker.on("dragend",function(e){
//	            $('#polygonCoordInput').val(e.target.getLatLng().toString().replace(/\LatLng|\s+/g, ''));
//	            $('#savePolygonCoordinates').trigger('click');
//	        }); 
//		    $(".slider_wrapper").show();
//		    marker.addTo(map2).dragging.enable();
	  console.log("Latitude: " + lat + ", Longitude: " + lng);
	}
  function displayError(error) {
	  var errors = { 
	    1: 'Permission denied',
	    2: 'Position unavailable',
	    3: 'Request timeout'
	  };
	  alert("Error: " + errors[error.code]);
	}
var mly;
  var key_image;
  var map2;
  var editableLayers;
  var jsonObj;
  var point_markers;
  
$(function() {
	
	if($('#mapid').length === 1) {
	  map2 = L.map('mapid', {
	      center: [20.0, 5.0],
	      minZoom: 2,
	      maxZoom: 18,
	      zoom: 2,
	      zoomControl:false,
	  });
	  /*navigator.geolocation ? map2.locate({setView: true, maxZoom: 16}) : alert("Geolocation is not supported by this browser.");*/
	  
	  if (navigator.geolocation)  {
		  var timeoutVal = 10 * 1000 * 1000;
		  navigator.geolocation.getCurrentPosition(
		    displayPosition, 
		    displayError,
		    { enableHighAccuracy: true, timeout: timeoutVal, maximumAge: 0 }
		  );
		} else alert("Geolocation is not supported by this browser");
	  
		  var satellite = L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {id: 'mapid', attribution: 'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ'}),
	      topography = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}', {id: 'mapid', attribution: 'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ'}),
	      esri = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {id: 'mapid', attribution: 'Tiles &copy; Esri &mdash; Esri, DeLorme, NAVTEQ'}),
	      mapbox = L.tileLayer(
	      			'https://api.mapbox.com/styles/v1/mapbox/streets-v10/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoiZHBhc3RyYW5hIiwiYSI6ImNqc3RjYml6ZjB4c2U0NHM5bng1OWR3ZWoifQ.aTc9xcGHEcMWaa0aRIaVMg', {
	      			    tileSize: 512,
	      			    zoomOffset: -1,
	      			    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://www.mapbox.com/tos/">Mapbox</a>'
	      			});
	      light = L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {id: 'mapid', attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'}),
	      dark = L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {id: 'mapid', attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'}),
	      openstreetmap = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {id: 'mapid', attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'}),
	      wikimedia   = L.tileLayer('https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}{r}.png', {id: 'mapid', attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://wikimediafoundation.org/wiki/Maps_Terms_of_Use">Wikimedia</a>'}),
	      cycle   = L.tileLayer('https://tile.thunderforest.com/cycle/{z}/{x}/{y}.png?apikey=977ed3be832a4b4ebab0039809d88978', {id: 'mapid', attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="http://thunderforest.com/">Thunderforest</a>'}),
	  	  terrain = L.tileLayer('https://1.aerial.maps.cit.api.here.com' +
	    	        '/maptile/2.1/maptile/newest/terrain.day/{z}/{x}/{y}/256/png' +
	    	        '?app_id=DIMWjDXvm0l2iZDhQwLw&app_code=vJk4OzJfwR2tfUXA1TIc4g', {id: 'mapid', attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://legal.here.com/en-gb/terms">HERE</a>'}),
	    transport = L.tileLayer('https://tile.thunderforest.com/transport/{z}/{x}/{y}.png?apikey=977ed3be832a4b4ebab0039809d88978', {
	      		attribution: 'Map data: &copy; <a href="http://thunderforest.com/">Thunderforest</a> contributors'
	      	});
	  	var OpenPtMap = L.tileLayer('http://openptmap.org/tiles/{z}/{x}/{y}.png', {
	  		maxZoom: 19,
	  		attribution: 'Map data: &copy; <a href="http://www.openptmap.org">OpenPtMap</a> contributors'
	  	});
	  	var OpenRailwayMap = L.tileLayer('https://{s}.tiles.openrailwaymap.org/standard/{z}/{x}/{y}.png', {
	  		maxZoom: 19,
	  		attribution: 'Map data: &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors | Map style: &copy; <a href="https://www.OpenRailwayMap.org">OpenRailwayMap</a> (<a href="https://creativecommons.org/licenses/by-sa/3.0/">CC-BY-SA</a>)'
	  	});
	  	var Hydda_RoadsAndLabels = L.tileLayer('https://{s}.tile.openstreetmap.se/hydda/roads_and_labels/{z}/{x}/{y}.png', {
	  		attribution: 'Tiles courtesy of <a href="http://openstreetmap.se/" target="_blank">OpenStreetMap Sweden</a> &mdash; Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
	  	});
	  	var Stamen_TonerHybrid = L.tileLayer('https://stamen-tiles-{s}.a.ssl.fastly.net/toner-hybrid/{z}/{x}/{y}{r}.{ext}', {
	  		attribution: 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
	  		subdomains: 'abcd',
	  		ext: 'png'
	  	});
	  	var Stamen_TonerLines = L.tileLayer('https://stamen-tiles-{s}.a.ssl.fastly.net/toner-lines/{z}/{x}/{y}{r}.{ext}', {
	  		attribution: 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
	  		subdomains: 'abcd',
	  		ext: 'png'
	  	});
	  	var Stamen_TonerLabels = L.tileLayer('https://stamen-tiles-{s}.a.ssl.fastly.net/toner-labels/{z}/{x}/{y}{r}.{ext}', {
	  		attribution: 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
	  		subdomains: 'abcd',
	  		ext: 'png'
	  	});
	  	var OWM_API_KEY = 'c55388b07ce1b54b66c09b9fe9d49566';
	  	var clouds = L.OWM.clouds({opacity: 0.8, legendImagePath: '/images/weather/NT2.png', appId: OWM_API_KEY});
		var precipitation = L.OWM.precipitation( {opacity: 0.5, appId: OWM_API_KEY} );
		var rain = L.OWM.rain({opacity: 0.5, appId: OWM_API_KEY});
		var snow = L.OWM.snow({opacity: 0.5, appId: OWM_API_KEY});
		var pressure = L.OWM.pressure({opacity: 0.4, appId: OWM_API_KEY});
		var pressurecntr = L.OWM.pressureContour({opacity: 0.5, appId: OWM_API_KEY});
		var temp = L.OWM.temperature({opacity: 0.5, appId: OWM_API_KEY});
		var wind = L.OWM.wind({opacity: 0.5, appId: OWM_API_KEY});
		var city = L.OWM.current({intervall: 15, imageLoadingUrl: '/images/gif/loading.gif', lang: 'en', minZoom: 5,
			appId: OWM_API_KEY});
		var windrose = L.OWM.current({intervall: 15, imageLoadingUrl: '/images/gif/loading.gif', lang: 'en', minZoom: 4,
			appId: OWM_API_KEY, markerFunction: myWindroseMarker, popup: false, clusterSize: 50,
				imageLoadingBgUrl: 'https://openweathermap.org/img/w0/iwind.png' });
		windrose.on('owmlayeradd', windroseAdded, windrose); // Add an event listener to get informed when windrose layer is ready
		  var baseLayers = {
				    "Light": light,
				    "Dark": dark,
				    "OpenStreetMap": openstreetmap,
				    "Wikimedia":wikimedia,
				    "Mapbox": mapbox,
				    "Esri":esri,
				    "Satellite": satellite,
				    "Topography":topography,
				    "Terrain": terrain,
				    "Cycle":cycle,
					"Transport":transport,
				};
		  var overlayLayers = {
				  "Public Transport":OpenPtMap,
				  "Railway":OpenRailwayMap,
				  "Toner Lines":Stamen_TonerLines,
				  "Toner Labels":Stamen_TonerLabels,
				  "RoadsAndLabels":Hydda_RoadsAndLabels,
				  "Current Weather (min Zoom 5)": city,
				  "Temperature": temp,
				  "Precipitation": precipitation,
				  "Rain": rain,
				  "Clouds": clouds,
				  "Wind Rose": windrose,
				  "Snowing": snow,
				};
		  layerControl = L.control.layers(baseLayers, overlayLayers,{collapsed:true, position: 'bottomleft'}).addTo(map2);
		  //layerControl.setPosition('topright');
			$('#icon-mylocation').on('click', function(){
				  map2.locate();
			});
			map2.on('locationfound', onLocationFound);
			map2.on('locationerror', onLocationError);
		  	if(getSearchParams("map") != undefined) {	 
		  		switch(getSearchParams("map")) {
		  		case 'light': light.addTo(map2); break
		  		case 'dark': dark.addTo(map2); break
		  		case 'openstreetmap': openstreetmap.addTo(map2); break
		  		case 'wikimedia': wikimedia.addTo(map2); break
		  		case 'mapbox': mapbox.addTo(map2); break
		  		case 'esri': esri.addTo(map2); break
		  		case 'satellite': satellite.addTo(map2); break
		  		case 'topography': topography.addTo(map2); break
		  		case 'satellite': satellite.addTo(map2); break
		  		case 'topography': topography.addTo(map2); break
		  		case 'terrain': terrain.addTo(map2); break
		  		case 'cycle': cycle.addTo(map2); break
		  		case 'transport': transport.addTo(map2); break
		  		default: light.addTo(map2);
		  		}
		    } else {
		    	light.addTo(map2);
		    }
		 map2.on('baselayerchange', function (e) {
			    //console.log(e.name);
			    $('#mapType').val(e.name.toLowerCase());
			});
		    L.DomEvent.on(L.DomUtil.get('zoomIn'), 'click', function () {
		        map2.setZoom(map2.getZoom() + 1);
		    });

		    L.DomEvent.on(L.DomUtil.get('zoomOut'), 'click', function () {
		        map2.setZoom(map2.getZoom() - 1);
		    });
//		  if(getSearchParams("zoom") != undefined) {
//			  map2.setZoom(getSearchParams("zoom"));
//		  }
		    //$('#mapZoomLevel').val(map2.getZoom());
		  map2.on('zoom', function() {
			  $('#mapZoomLevel').val(map2.getZoom());
			});
		  //console.log("jsonobj is "+jsonObj);
		  if (jsonObj !== undefined) {
			  var markers = JSON.parse(jsonObj);
			  	  //fitBounds used for positioning screen with the markers
				  //map2.fitBounds([[markers[0].lat,markers[0].lng],[markers[markers.length-1].lat,markers[markers.length-1].lng]]);
			  		var markers2 = [];
				  for (var i=0; i<markers.length; ++i){
					  markers2.push([JSON.parse(markers[i].lat),JSON.parse(markers[i].lng),markers[i].name+"#"+markers[i].id+"#"+markers[i].icon]);
				  }
				  var color;
				  var size=12;
				  var opacity=0.9;
				  var colorsel = false;
				  if(getSearchParams("color")!=null) {
					  color=fromHex("#"+getSearchParams('color'));
					  colorsel = true;
				  }
				  if(getSearchParams("size")!=null) {
					  size=getSearchParams("size");
				  }
				  if(getSearchParams("opacity")!=null) {
					  opacity=getSearchParams("opacity");
				  }
				     point_markers = L.glify.points({
					        map: map2,
					        color: function(index,point){if(!colorsel){color=fromHex('#'+point[2].split('#')[2])};return color},
					        opacity: opacity,
					        click: function(e,point,xy) {
					        L.popup().setLatLng([point[0],point[1]]).setContent(point[2].split('#')[0],callStreetView(point[0],point[1]),showSidebar(point[2].split('#')[1])).openOn(map2);
					        },
					        data: markers2,
					        size: size,   
					      });
					  if(getSearchParams("coords") != null && getSearchParams("coords").split(',').length == 2) {
						    var latlng = getSearchParams('coords').split(',');
						    var lat = latlng[0].replace(/%28|\(/, '');
						    var lng = latlng[1].replace(/%29|\)/, '');
							  setTimeout(function(){
								  var zoom = getSearchParams("zoom");
								  	map2.setZoom(zoom);
								  	setTimeout(function(){
								  	map2.panTo([lat,lng]);
								  	},500); 
							  },600); 
						  } else {
							  map2.fitBounds([[markers[0].lat,markers[0].lng],[markers[markers.length-1].lat,markers[markers.length-1].lng]]);
							  map2.setZoom(getSearchParams("zoom"));
						  }
			  }
		  
		  
		  
		// Initialise the FeatureGroup to store editable layers
		  editableLayers = new L.FeatureGroup();
		  map2.addLayer(editableLayers);
		  var drawPluginOptions = {
		    position: 'bottomright',
		    draw : {
		        marker: {
		            icon: new customMarker()
		          },
	            polygon: {
	                allowIntersection: false,
	                drawError: {
	                  color: '#46945C', 
	                  message: '<strong>Oh snap!<strong> you can\'t draw that!'
	                },
	                shapeOptions: {
	                	fillColor:'#1c9099',
	                	color:'white',
	                	weight:3
	                }
	              },
	              rectangle: {
	                  shapeOptions: {
		                	fillColor:'#1c9099',
		                	color:'white',
		                	weight:3
	                  }
	                },
		        polyline : false,
		        circle : false,
		        rectangle: true,
		        polygon: true,
		        marker: true,
		        circlemarker: false
		    },
		    edit: {
		      featureGroup: editableLayers,
		      remove: true
		    }
		  };
		  var drawControl = new L.Control.Draw(drawPluginOptions);
		  map2.addControl(drawControl);
		  map2.on('draw:created', function (e) {
			    var type = e.layerType,
			        layer = e.layer;

			    if(type === 'polygon') {
			        var points = layer.getLatLngs().toString().replace(/\LatLng|\s+/g, '');
			   }
			    if(type === 'rectangle') {
			        var points = layer.getLatLngs().toString().replace(/\LatLng|\s+/g, '');
			   }
			    if(type === 'marker') {
			        var points = layer.getLatLng().toString().replace(/\LatLng|\s+/g, '');
			   }
//			    if(type === 'circle') {
//			        var centerpt = layer.getLatLng();
//			        var center = [centerpt.lng,centerpt.lat]; 
//			        var radius = layer.getRadius();
//			        points = "cent("+center+"),rad("+radius+")";
//			        console.log("center circle is:"+center+" and radius:"+radius);
//			   }
			    $('#polygonCoordInput').val(points);
			    $('#savePolygonCoordinates').trigger('click');
			});
		  map2.on('draw:edited', function (e) {
			      var layers = e.layers;
		          var countOfEditedLayers = 0;
		          var points = null;
		            layers.eachLayer(function(layer) {
		            	latlngs = layer.getLatLngs();
				        points = layer.getLatLngs().toString().replace(/\LatLng|\s+/g, '');
		                countOfEditedLayers++;
		            });
				    $('#polygonCoordInput').val(points);
				    $('#savePolygonCoordinates').trigger('click');
		            //console.log("Number of Layers: " + countOfEditedLayers + " layers");
			});
		  map2.on('draw:created', function(e) {
		    var type = e.layerType,
		      layer = e.layer;

		    if (type === 'marker') {
		      layer.bindPopup('A popup!');
		    }

		    editableLayers.addLayer(layer);
		  });
		  
		  //L.control.attribution({position: 'bottomright'}).addTo(map2);
		    // Attribution panel
		    $('.leaflet-control-attribution').hide();
			$("#attribution-button").on("mouseover",function(){
				$('.leaflet-control-attribution').show();
			});
			$(".leaflet-control-attribution").on("mouseleave",function(){
				$('.leaflet-control-attribution').hide();
			});
			//List file uploads panel
			
			if($(window).width() <= 1024){
				$('#hideLocation').hide();
				$("#displayLocations").on("mouseover",function(){
					$('#hideLocation').show();
				});
				$("#locationView").on("mouseleave",function(){
					$('#hideLocation').hide();
				});
				$('html').click(function() {
					$('#hideLocation').hide();
				});
			} else {
				$('#hideLocation').show();
			}

			//Sidebar Mobile left menu with black layout
			  $('#dismiss, .overlay').on('click', function () {
			      $('#sidebar').removeClass('active');
			      $('.overlay').removeClass('active'); // Only for mobilephones
			  });
			  //About Page slider - time passing in seconds
			  $('.carousel').carousel({
				  interval: 6000
				})
				//Slider to measure the distance from a location
			     var valMap = [0.2,0.5,1,5,10,20,50];
			     var activeValue = $('#geoCoordDistance').val();
			     var seq_order = null;
			     switch(activeValue) {
			     case '0.2':
			    	 seq_order = 0;
			       break;
			     case '0.5':
			    	 seq_order = 1;
			       break;
			     case '1':
			    	 seq_order = 2;
			       break;
			     case '5':
			    	 seq_order = 3;
			       break;
			     case '10':
			    	 seq_order = 4;
			       break;
			     case '20':
			    	 seq_order = 5;
			       break;
			     case '50':
			    	 seq_order = 6;
			       break;
			   }
			     var handle = $("#custom-handle");
			     $(".slider").slider({
			         create: function() {
			             handle.text(activeValue);
			           },
			           range: "min",
			           max: valMap.length - 1,
			       value: seq_order,
			       change: function (event, ui) {
			         if (event.originalEvent) {
			           $('#geoCoordDistance').val(valMap[ui.value]);
			   	    var latlng = getSearchParams('coords').split(',');
				    var lat = latlng[0].replace(/%28|\(/, '');
				    var lng = latlng[1].replace(/%29|\)/, '');
				    latlng = new L.latLng(lat,lng);
			            $('#polygonCoordInput').val('('+lat+','+lng+')');
			            $('#savePolygonCoordinates').trigger('click');
			           }
			        }
			     }).slider("pips", {
			       rest: "label",
			    	   suffix: 'Km',
			    	   labels: valMap
			   	});
				$('#mapTypeId').change(function() {
					  $('#mapType').val(this.value);
					});
		} // END only when loading leaflet maps
	  // Helpful message when cursor is over - labeled in bootstrap
	    $('[data-toggle="tooltip"]').tooltip();

});
