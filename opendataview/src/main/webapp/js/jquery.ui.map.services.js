!function(e){e.extend(e.ui.gmap.prototype,{displayDirections:function(e,t,i){var s=this,r=this.get("services > DirectionsService",new google.maps.DirectionsService),o=this.get("services > DirectionsRenderer",new google.maps.DirectionsRenderer({suppressMarkers:!0}));t&&o.setOptions(t),r.route(e,function(e,t){"OK"===t?(o.setDirections(e),o.setMap(s.get("map"))):o.setMap(null),i(e,t)})},displayStreetView:function(e,t){this.get("map").setStreetView(this.get("services > StreetViewPanorama",new google.maps.StreetViewPanorama(this._unwrap(e),t)))},search:function(e,t){this.get("services > Geocoder",new google.maps.Geocoder).geocode(e,t)}})}(jQuery);