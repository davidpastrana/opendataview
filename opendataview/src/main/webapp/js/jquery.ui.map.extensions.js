(function($){
	$.extend($.ui.gmap.prototype, {
		getCurrentPosition: function(callback, geoPositionOptions) {
			if ( navigator.geolocation ) {
				navigator.geolocation.getCurrentPosition ( 
					function(result) {
						callback(result, 'OK');
					}, 
					function(error) {
						callback(null, error);
					}, 
					geoPositionOptions 
				);	
			} else {
				callback(null, 'NOT_SUPPORTED');
			}
		},
		watchPosition: function(callback, geoPositionOptions) {
			if ( navigator.geolocation ) {
				this.set('watch', navigator.geolocation.watchPosition ( 
					function(result) {
						callback(result, "OK");
					}, 
					function(error) {
						callback(null, error);
					}, 
					geoPositionOptions 
				));	
			} else {
				callback(null, "NOT_SUPPORTED");
			}
		},
		clearWatch: function() {
			if ( navigator.geolocation ) {
				navigator.geolocation.clearWatch(this.get('watch'));
			}
		},
		autocomplete: function(panel, callback) {
			var self = this;
			$(this._unwrap(panel)).autocomplete({
				source: function( request, response ) {
					self.search({'address':request.term}, function(results, status) {
						if ( status === 'OK' ) {
							response( $.map( results, function(item) {
								return { label: item.formatted_address, value: item.formatted_address, position: item.geometry.location }
							}));
						} else if ( status === 'OVER_QUERY_LIMIT' ) {
							alert('Google said it\'s too much!');
						}
					});
				},
				minLength: 3,
				select: function(event, ui) { 
					self._call(callback, ui);
				},
				open: function() { $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" ); },
				close: function() { $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" ); }
			});
		},
		placesSearch: function(placeSearchRequest, callback) {
			this.get('services > PlacesService', new google.maps.places.PlacesService(this.get('map'))).search(placeSearchRequest, callback);
		},
		clearDirections: function() {
			var directionsRenderer = this.get('services > DirectionsRenderer');
			if (directionsRenderer) {
				directionsRenderer.setMap(null);
				directionsRenderer.setPanel(null);
			}
		},
		    pagination: function(prop,markerDetails) {
		    	var $el = $("<div id='pagination' class='pagination shadow gradient rounded clearfix'><div class='lt btn back-btn'></div><div class='lt display'></div><div class='rt btn fwd-btn'></div></div>");
		    	var self = this, i = 0, prop = prop, markerDetails=markerDetails || 'title' || 'markerDetails';
		    	self.set('p_nav', function(a, b) {
		    	    if (a){
		    		i=i+b;
		    		$el.find('.display').text(self.get('markers')[i][prop]);
		    		self.get('map').panTo(self.get('markers')[i].getPosition());
		    		self.get('markers')[i].setAnimation(google.maps.Animation.BOUNCE);
		    		setTimeout(function(){self.get('markers')[i].setAnimation(null);},750);
		    		self.openInfoWindow({'content':self.get('markers')[i][markerDetails]},
		    		self.get('markers')[i]);
		    	    }
		    	});
		    	self.get('p_nav')(true, 0);
		 	   	$el.find('.back-btn').click(function() {
		 	   	    self.get('markers')[i].setAnimation();
		 	   	    if(i == 0){i = self.get('markers').length;
		 	   	    self.get('p_nav')(i , -1, this);}else{self.get('p_nav')((i > 0), -1, this);
		 	       }});
		 	       $el.find('.fwd-btn').click(function() {
		 		   self.get('markers')[i].setAnimation();
		 		   if(i ==  self.get('markers').length -1){i = -1 ;self.get('p_nav')(i , 1, this);}
		 		   else{self.get('p_nav')((i < self.get('markers').length - 1), 1, this);}
		 	       });
		 	       self.addControl($el, google.maps.ControlPosition.BOTTOM_LEFT);	
		    }
	});
}(jQuery));