function getCurrentDateTimeShort() {
  var currentDateTime = new moment();
  currentDateTime = currentDateTime.format("DDMMYY_HH.mm");
  return currentDateTime.toString();
}

/*function exportGoogleMap() {

  $('#loading').show();
  $('.adp-substep').attr('style', 'display: none !important');
  $('.moreInfo').attr('style', 'display: table !important');
  $('#map_wrapper')
          .attr('style',
                  'top: 0px !important; border: 1px solid #CC4646; border-radius: 1px;');

  $('#directionsToPDF').show();
  $('#directionsToPDF2').show();
  var displayDirectionsOptions = {
    'origin': origin,
    'destination': destination,
    'waypoints': waypts,
    'optimizeWaypoints': true,
    'provideRouteAlternatives': false,
    'avoidHighways': false,
    'avoidTolls': false,
    'unitSystem': google.maps.UnitSystem.METRIC,
    'travelMode': google.maps.DirectionsTravelMode[$("#mode").val()]
  };
  var panel = {
    'panel': document.getElementById('directions')
  };
  var ok = false;

  $('#directionsToPDF').html('');
  for (var i = 0; i < totalMarkers; i++) {
    $('#directionsToPDF').append(
            $('#selectMarkersTable' + i + '').html() + "<br><br>");

  }

  map.gmap('displayDirections', displayDirectionsOptions, panel, function(
          success, response) {
    if (success) {
      $('#directionsToPDF2').html('');
      $('#directionsToPDF2').html($('#directions').html());
      ok = true;
    }
    if (ok) {
      html2canvas($('#map_wrapper'), {
        useCORS: true,
        onrendered: function(canvas) {
          var img = canvas.toDataURL("image/jpeg").slice(
                  'data:image/jpeg;base64,'.length);
          img = atob(img);

          html2canvas($('#directionsToPDF'), {
            useCORS: true,
            onrendered: function(canvas) {
              var img2 = canvas.toDataURL("image/jpeg").slice(
                      'data:image/jpeg;base64,'.length);
              img2 = atob(img2);

              html2canvas($('#directionsToPDF2'), {
                useCORS: true,
                onrendered: function(canvas) {
                  var img3 = canvas.toDataURL("image/jpeg").slice(
                          'data:image/jpeg;base64,'.length);
                  img3 = atob(img3);

                  doc = new jsPDF('landscape');
                  doc.setProperties({
                    title: 'My CVienna Tour',
                    subject: 'My saved tour',
                    keywords: 'cvienna, tour, routes, wien',
                    author: 'www.cvienna.com',
                    creator: 'www.cvienna.com'
                  });
                  doc.setFontSize(30);
                  doc.setTextColor(100);
                  doc.setTextColor(204, 70, 70);
                  doc.text(110, 25, "My CVienna Tour");
                  doc.setTextColor(0, 0, 70);
                  doc.setFontSize(10);
                  console.log("window width= " + $(window).width()
                          + ", window height= " + $(window).height());
                  var width = $(window).width();
                  var height = $(window).height();
                  if (width <= 640) {
                    doc.addImage(img, 'JPEG', 90, 40, 100, 150);
                    doc.addPage();
                    doc.text(110, 25, "Information");
                    doc.addImage(img2, 'JPEG', 90, 5, 100, 150);
                    doc.addPage();
                    doc.text(110, 25, "Direction");
                    doc.addImage(img3, 'JPEG', 90, 5, 100, 180);
                  }
                  if (width > 640 && width <= 800) {
                    doc.addImage(img, 'JPEG', 55, 40, 187, 150);
                    doc.addPage();
                    doc.text(110, 25, "Information");
                    doc.addImage(img2, 'JPEG', 55, 5, 187, 150);
                    doc.addPage();
                    doc.text(110, 25, "Direction");
                    doc.addImage(img3, 'JPEG', 55, 5, 187, 150);
                  }
                  if (width > 800 && width <= 1024) {
                    doc.addImage(img, 'JPEG', 35, 40, 230, 150);
                    doc.addPage();
                    doc.addImage(img2, 'JPEG', 35, 5, 220, 150);
                    doc.addPage();
                    doc.addImage(img3, 'JPEG', 35, 5, 210, 150);
                  }
                  if (width > 1024 && width <= 1400) {
                    doc.addImage(img, 'JPEG', 5, 40, 287, 100);
                    doc.addPage();
                    doc.addImage(img2, 'JPEG', 5, 5, 287, 100);
                    doc.addPage();
                    doc.addImage(img3, 'JPEG', 5, 5, 287, 100);
                  }
                  if (width > 1400) {
                    doc.addImage(img, 'JPEG', 5, 40, 287, 90);
                    doc.addPage();
                    doc.addImage(img2, 'JPEG', 5, 5, 287, 90);
                    doc.addPage();
                    doc.addImage(img3, 'JPEG', 5, 5, 287, 90);
                  }

                  // 3 lines to comment to edit PDF
                  $('#directionsToPDF').hide();
                  $('#directionsToPDF2').hide();
                  $('#loading').hide();

                  doc.output('save', 'CVienna_' + getCurrentDateTimeShort()
                          + '_Report.pdf');
                  $('.adp-substep').attr('style', 'display: table !important');
                  $('.moreInfo').attr('style', 'display: none !important');
                  $('#map_wrapper').attr('style',
                          'top: 55px !important; border: none !important;');
                }
              });
            }
          });
        }
      });
    }
  });
}*/

function displayMoreInfo(marker) {
  var idMarker = $(marker);
  if (idMarker.attr('id') == null) {
    $(".markersTableContainer").one('click', 'tr', function() {

      num_row = $(this).attr('id').substring(6);
      if (($('#moreInfo' + num_row)).is(':visible')) {
        $('#moreInfo' + num_row).animate({
          opacity: 'hide'
        }, 100);
      } else {
        if (($('#streetviewSize' + num_row)).is(':visible')) {
          $('#streetview' + num_row).hide();
          $('#streetviewSize' + num_row).hide();
        }
        $('#moreInfo' + num_row).animate({
          opacity: 'show'
        }, 800);
        $('#moreInfo' + num_row).trigger('click');
      }
    });
  }
  if (idMarker.attr('id') != null) {
    num_row = idMarker.attr('id').substring(18);
    if (($('#moreInfo' + num_row)).is(':visible')) {
      $('#moreInfo' + num_row).animate({
        opacity: 'hide'
      }, 100);
    } else {
      if (($('#streetviewSize' + num_row)).is(':visible')) {
        $('#streetview' + num_row).hide();
        $('#streetviewSize' + num_row).hide();
      }
      $('#moreInfo' + num_row).animate({
        opacity: 'show'
      }, 800);
      $('#moreInfo' + num_row).trigger('click');
    }
  }
}

function displayMainMenu() {
  if (hideMenu) {
    $('#arrowHideMainMenu').show();
    $('#arrowShowMainMenu').hide();
    $('#mainMenu').animate({
      marginRight: '-=100%',
    }, '1000');
    hideMenu = false;
  } else {
    $('#arrowHideMainMenu').hide();
    $('#arrowShowMainMenu').show();
    $('#arrowHideLocationsTable').hide();
    $('#mainMenu').animate({
      marginRight: '+=100%',
    }, '1000');
    hideMenu = true;
  }
}

function initIds() {
  id = 0;
  $('.selectMarkersTable').each(function() {
    $(this).attr('id', 'selectMarkersTable' + (id++));
  });
  totalMarkers = 0;
  $('.moreInfo').each(function() {
    $(this).attr('id', 'moreInfo' + (totalMarkers++));
  });
  id = 0;
  $('.streetviewSize').each(function() {
    $(this).attr('id', 'streetviewSize' + (id++));
  });
  id = 0;
  $('.streetview').each(function() {
    $(this).attr('id', 'streetview' + (id++));
  });
  id = 0;
  $('.row').each(function() {
    $(this).attr('id', 'marker' + (id++));
  });
}

function displayWaypoints(origin, destination) {

  var selectedMode = $("#mode").val();
  var displayDirectionsOptions = {
    'origin': origin,
    'waypoints': waypts,
    'destination': destination,
    'optimizeWaypoints': true,
    'provideRouteAlternatives': false,
    'avoidHighways': false,
    'avoidTolls': false,
    'unitSystem': google.maps.UnitSystem.METRIC,
    'travelMode': google.maps.DirectionsTravelMode[selectedMode]
  };
  var panel = {
    'panel': document.getElementById('directions')
  };
  map
          .gmap(
                  'displayDirections',
                  displayDirectionsOptions,
                  panel,
                  function(success, response) {
                    console.log("Status is " + success + " with response "
                            + response);
                    if (success) {
                      if (response == 'ZERO_RESULTS') {
                        alert('Error, number of waypoints exceeded (Maximum 8 waypoints per route).');
                        $('#directions').text(' ');
                        findDirection = false;
                      } else {
                        findDirection = true;
                      }
                    } else {
                      findDirection = false;
                    }
                  });

  var directionsDisplay = new google.maps.DirectionsRenderer();
  var directions = new google.maps.DirectionsService();
  directions.route(displayDirectionsOptions, function(result, status) {
    if (status === google.maps.DirectionsStatus.OK) {

      directionsDisplay.setDirections(result);

      // calculate total distance and duration
      var distance = 0;
      var time = 0;
      var theRoute = result.routes[0];
      for (var i = 0; i < theRoute.legs.length; i++) {
        var theLeg = theRoute.legs[i];
        distance += theLeg.distance.value;
        time += theLeg.duration.value;
      }
      $("#distance").html(
              "Total distance: " + Math.round(distance / 100) / 10 + " km, ");
      $("#duration").html(
              "total duration: " + Math.round(time / 60) + " minutes");
    }
  });
}

function isScrolledIntoView(elem) {
  var docViewTop = $(window).scrollTop(), docViewBottom = docViewTop
          + $(window).height(), elemTop = $(elem).offset().top, elemBottom = elemTop
          + $(elem).height();

  return ((elemTop + ((elemBottom - elemTop) / 2)) >= docViewTop && ((elemTop + ((elemBottom - elemTop) / 2)) <= docViewBottom));
}

function checkListSelection(element) {

  if (element.offset().top < 52 || element.offset().top > 630) {
    $('.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
            .scrollTop(0);

    var offset = element.offset().top - 70,

    wait = setInterval(
            function() {

              if (!$(
                      '.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
                      .is(":animated")) {
                $(
                        '.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
                        .scrollTop(offset);
                clearInterval(wait);
              }
            }, 0);
  }
  return true;
}

function initDirection() {
  $('#submitFindDirection')
          .click(
                  function() {

                    if (recalculate) {
                      if ($('#from').val() == '' && $('#to').val() == '') {
                        displayWaypoints(origin, destination);
                        //initMarkers2();
                      }

                      if (currentPosition && $('#from').val() == '') {
                        origin = waypts[0].location;
                        waypts.shift();
                        totalMarkers--;
                        displayWaypoints(origin, destination);
                        currentPosition = false;
                        //initMarkers2();
                      }

                      if (newEndPosition && $('#to').val() == '') {
                        destination = waypts[waypts.length - 1].location;
                        waypts.pop();
                        totalMarkers--;
                        displayWaypoints(origin, destination);
                        newEndPosition = false;
                        //initMarkers2();
                      }

                      if ($('#from').val() != '') {
                        var geocoder = new google.maps.Geocoder();
                        geocoder
                                .geocode(
                                        {
                                          'address': $('#from').val()
                                        },
                                        function(results, status) {
                                          if (status == google.maps.GeocoderStatus.OK) {
                                            lat = results[0].geometry.location
                                                    .lat();
                                            lng = results[0].geometry.location
                                                    .lng();

                                            if (!currentPosition) {
                                              totalMarkers++;
                                              waypts.splice(0, 0, {
                                                location: origin,
                                                stopover: true
                                              });
                                            }

                                            origin = new google.maps.LatLng(
                                                    lat, lng);
                                            displayWaypoints(origin,
                                                    destination);
                                            currentPosition = true;
                                            //initMarkers2();
                                          } else {
                                            alert("Geocode was not successful for the following reason: "
                                                    + status);
                                          }
                                        });
                      } else {
                        //console.log("no value");
                      }

                      if ($('#to').val() != '') {

                        var geocoder = new google.maps.Geocoder();
                        geocoder
                                .geocode(
                                        {
                                          'address': $('#to').val()
                                        },
                                        function(results, status) {
                                          if (status == google.maps.GeocoderStatus.OK) {
                                            lat = results[0].geometry.location
                                                    .lat();
                                            lng = results[0].geometry.location
                                                    .lng();

                                            if (!newEndPosition) {
                                              totalMarkers++;
                                              waypts.push({
                                                location: destination,
                                                stopover: true
                                              });
                                            }

                                            destination = new google.maps.LatLng(
                                                    lat, lng);
                                            displayWaypoints(origin,
                                                    destination);
                                            newEndPosition = true;
                                            //initMarkers2();
                                          } else {
                                            alert("Geocode was not successful for the following reason: "
                                                    + status);
                                          }
                                        });
                      }
                      recalculate = true;

                    } else {
                      var selectedMode = $("#mode").val();
                      var displayDirectionsOptions = {
                        'origin': $('#from').val(),
                        'destination': $('#to').val(),
                        'provideRouteAlternatives': false,
                        'avoidHighways': false,
                        'avoidTolls': false,
                        'unitSystem': google.maps.UnitSystem.METRIC,
                        'travelMode': google.maps.DirectionsTravelMode[selectedMode]
                      };
                      directionsDisplay = new google.maps.DirectionsRenderer();
                      var panel = {
                        'panel': document.getElementById('directions')
                      };
                      map
                              .gmap(
                                      'displayDirections',
                                      displayDirectionsOptions,
                                      panel,
                                      function(success, response) {
                                        if (success) {
                                          if (response == 'ZERO_RESULTS') {
                                            //alert('Error. Introduce una dirección más exacta.');
                                            $('#directions').text(' ');
                                            findDirection = false;
                                          } else {
                                            findDirection = true;
                                          }
                                        } else {
                                          findDirection = false;
                                        }
                                        $('#directions').show();
                                      });
                    }
                  });
}

//function displayStreetView(marker) {
//
//  var idMarker = $(marker);
//  if (idMarker.attr('id') == null) {
//    $(".markersTableContainer").one('click', 'tr', function() {
//      num_row = $(this).attr('id').substring(6);
//      if (($('#streetviewSize' + num_row)).is(':visible')) {
//        $('#streetview' + num_row).animate({
//          opacity: 'hide'
//        }, 100);
//        $('#streetviewSize' + num_row).slideUp("slow");
//      } else {
//        if (($('#moreInfo' + num_row)).is(':visible')) {
//          $('#moreInfo' + num_row).hide();
//          $('#streetviewSize' + num_row).show();
//          $('#streetview' + num_row).animate({
//            opacity: 'show'
//          }, 1000);
//          $('#streetview' + num_row).trigger('click');
//        } else {
//          $('#streetviewSize' + num_row).slideDown("slow");
//          $('#streetview' + num_row).animate({
//            opacity: 'show'
//          }, 1000);
//          $('#streetview' + num_row).trigger('click');
//        }
//      }
//    });
//  }
//  if (idMarker.attr('id') != null) {
//    num_row = idMarker.attr('id').substring(18);
//    if (($('#streetviewSize' + num_row)).is(':visible')) {
//      $('#streetview' + num_row).animate({
//        opacity: 'hide'
//      }, 100);
//      $('#streetviewSize' + num_row).slideUp("slow");
//    } else {
//      if (($('#moreInfo' + num_row)).is(':visible')) {
//        $('#moreInfo' + num_row).hide();
//        $('#streetviewSize' + num_row).show();
//        $('#streetview' + num_row).animate({
//          opacity: 'show'
//        }, 1000);
//        $('#streetview' + num_row).trigger('click');
//      } else {
//        $('#streetviewSize' + num_row).slideDown("slow");
//        $('#streetview' + num_row).animate({
//          opacity: 'show'
//        }, 1000);
//        $('#streetview' + num_row).trigger('click');
//      }
//    }
//  }
//}

function displayTable() {
  $('#createTour').show();
}

function changeToUnselect(index) {
  $('#unsel' + index).hide();
}
function changeToSelected(index) {
  $('#sel' + index).hide();
}

function getIdMarker(id, marker) {

  var idMarker = $(marker);
  if (idMarker.attr('id') != null) {
    $('#idInput').val(id);
    $('#editInfoButton').trigger('click');
  }
  if (idMarker.attr('id') == null) {
    $(".markersTableContainer").one('click', 'tr', function() {
      num_row = $(this).attr('id');
      id = $('#' + num_row).find(".id_DB").text();
      $('#idInput').val(id);
      $('#editInfoButton').trigger('click');
    });
  }
}

function showMarkerInfo(id) {
    $('#idInput2').val(id);
    $('#showMarkerInfoBttn').trigger('click');
//	  var idMarker = $(marker);
//	  if (idMarker.attr('id') != null) {
//		  console.log("WERE NOT IN NULL"+idMarker.attr('id'));
//	    $('#idInput2').val(id);
//	    $('#showMarkerInfoBttn').trigger('click');
//	  }
//	  if (idMarker.attr('id') == null) {
//		  console.log("WERE  IN NULL ");
//	    //$(".markersTableContainer").one('click', 'tr', function() {
//	      num_row = $(this).attr('id');
//	      console.log("num row "+num_row);
//	      id = $('#' + num_row).find(".id_DB").text();
//	      console.log("");
//	      $('#idInput2').val(id);
//	      $('#showMarkerInfoBttn').trigger('click');
//	    //});
//	  }
	}

function markerInfo(name, item, index, id) {
	
    showMarkerInfo(id);
//    displayStreetView($(item).attr('id'));
    console.log("Marker id from DB: "+id +" , Marker id from Table: "+$(item).attr('id'));
    
	  var markerDetails = '';
	  markerDetails += '<div class="markerInfo notranslate">';
	  markerDetails += '<h5>' + name + '</h5>';
	  markerDetails += '<i class="fa fa-info-circle fa-2x iconInfoMarker" onclick="displayLocationsTable();displayMoreInfo('
          + $(item).attr('id')
          + ')"></i>';
	  markerDetails += '<i class="fa fa-map fa-2x iconDirectionMarker" onclick="displayLocationsTable();displayDirectionTable()"></i>';
	  markerDetails += '<i class="fa fa-street-view fa-2x iconStreetViewMarker" onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
          + $(item).attr('id')
          + ')"></i>';
	  if (user == active_user) {
	    markerDetails += '<i class="fa fa-pencil fa-2x iconEditMarker" onclick="getIdMarker('
            + id
            + ','
            + $(item).attr('id')
            + ')"></i>';
	  }
	  markerDetails += '</div>';
	  
	  return markerDetails;
	}

function typeIcon(result) {
	var marker = result.icon_marker;
	
	icon = {
			url: '/images/markers/svg/'+marker+'.svg',
			scaledSize: new google.maps.Size(28, 28),
		    };
//	var csvName = csvName.toLowerCase();	
//	if(csvName.match(/(.*party.*|.*fiesta.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_01.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	} else if(csvName.match(/(.*hospital.*|.*disease.*|.*cure.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_02.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	} else if(csvName.match(/(.*coffee.*|.*break.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_03.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	} else if(csvName.match(/(.*food.*|.*meal.*|.*comida.*|.*hosteleria.*|.*restaura.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_22.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	} else if(csvName.match(/(.*comerc.*|.*shop.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_54.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	}
//	else if(csvName.match(/(.*deporte.*|.*sports.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_12.svg',
//				scaledSize: new google.maps.Size(28, 28),
//				
//	    };
//	}
//	else if(csvName.match(/(.*industri.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_61.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	}
//	else if(csvName.match(/(.*beer.*|.*bars.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_25.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	}
//	else if(csvName.match(/(.*nature.*|.*mountain.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_07.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	}
//	else if(csvName.match(/(.*museums.*|.*monuments.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_62.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	}
//	else if(csvName.match(/(.*open.*|.*data.*|.*share.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_89.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	} else if(csvName.match(/(.*repair.*|.*auto.*)/g)) {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_08.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	} else {
//		icon = {
//				url: '/images/markers/svg/wsd_markers_88.svg',
//				scaledSize: new google.maps.Size(28, 28),
//			    };
//	}
  return icon;
}

function getSearchParams(k){
	 var p={};
	 location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){p[k]=v})
	 return k?p[k]:p;
	}

function markerProperties(result, item, index, zoomin) {

  var name = result.name;
  var id_marker = result.identifier;
  var icon_marker = result.url;
  var lat = result.Place[0].geo[0].latitude;
  var lng = result.Place[0].geo[0].longitude;
  var latlng = new google.maps.LatLng(lat, lng);
  if(index == 0) {
	  map.gmap('get', 'map').panTo(latlng);
	  map.gmap('get','map').setZoom(parseInt(getSearchParams("zoom")));
	 
  }
	icon = {
			url: '/images/markers/svg/wsd_markers_custom1.svg',
			scaledSize: new google.maps.Size(40, 40),
		    };
  
    var icon2 = {
            
            path: "M-20,0a20,20 0 1,0 40,0a20,20 0 1,0 -40,0",
            fillColor: '#FF0000',
            fillOpacity: .6,
            anchor: new google.maps.Point(0,0),
            strokeWeight: 0,
            scale: 0.2
        }
//  if (csvName == "myPosition") {
//    markerOptions1 = {
//      'zIndex': 1300,
//      'bounds': false,
//      'position': latlng,
//      'draggable': true,
//      'title': "My Position",
//      'icon': icon
//    };
//
//  } else {
    var markerOptions1 = {
      'id': index,
      'position': latlng,
      //'markerDetails': markerInfo(name, item, index, id),
      'icon': icon2,
      'optimized': true,
      'bounds': false
    };
//  }

  map.gmap('addMarker', markerOptions1, function(map, marker) {

            $(item).click(function(event) {
              $(marker).triggerEvent('click');
//              if ($(event.target).is('.moreInfo')) {
//                $(item).unbind('click');
//              }
//              if ($(event.target).hasClass('streetview')) {
//                $(item).unbind('click');
//              }
            });
          })
//          .drag(function(marker) {
//            $('#polygonCoordInput').val(marker.latLng);
//          })
          .click(
                  function() {

                    map.gmap('openInfoWindow', {
                      'content': markerInfo(name, item, index, id_marker)
                    }, this);
                    


//                    $(item).addClass('markerClicked');
//                    
//                    
//
//                    if (last_checked != "") {
//                      $(last_checked).removeClass('markerClicked');
//                    }
//
//                    if (!isScrolledIntoView($(item))) {
//                      checkListSelection($(item));
//                    }
//
//                    last_checked = $(item);

                    map.gmap('search', {
                      'location': this.position
                    }, function(results, status) {
                      if (status === 'OK') {
                        $('#to').val(results[0].formatted_address);
                      }
                    });


                    var sv = new google.maps.StreetViewService();
                    sv.getPanoramaByLocation(
                                    this.position,
                                    800,
                                    function(result, status) {
                                        console.log("INDEX IS "+index);
                                        console.log("LOCATION IS "+result.location.latLng);
                                      if (status == google.maps.StreetViewStatus.OK) {
                                        var svPos = result.location.latLng;
                                        map.gmap('displayStreetView',
                                                'streetview2', {
                                                  'position': svPos,
                                                  'pov': {
                                                    'heading': 2,
                                                    'pitch': 1,
                                                    'zoom': 2
                                                  }
                                                });
                                        state = false;

                                        //$('#streetview2').trigger('click');
                                      } else {
                                        $('#streetview2')
                                                .html(
                                                        "<div class='errorMessage'>Sorry there is no Street View service within 800 meters from the location.</div>");
                                      }
                                    });
                  });
  
}



function initMyPosition() {

  map.gmap('microdata', 'https://schema.org/Place', function(result,
          item, index) {
	  
	  if (index == 0) {
      var lat = result.geo[0].latitude;
      var lng = result.geo[0].longitude;
      var latlng = new google.maps.LatLng(lat, lng);
		icon = {
				url: '/images/markers/svg/wsd_markers_custom1.svg',
				scaledSize: new google.maps.Size(40, 40),
			    };
      markerOptions1 = {
        'zIndex': 1300,
        'bounds': false,
        'position': latlng,
        'draggable': true,
        'title': "My Position",
        'icon': icon
      };
	  map.gmap('get', 'map').panTo(latlng);
	  console.log("ZOOM TO ADD "+parseInt(getSearchParams("zoom")));
	  map.gmap('get','map').setZoom(parseInt(getSearchParams("zoom")));
	  
      map.gmap('addMarker', markerOptions1, function(map, marker) {

        $(item).click(function(event) {
          $(marker).triggerEvent('click');
          //TODO


          return false;
        });

      }).drag(

      function(marker) {

        $('#polygonCoordInput').val(marker.latLng);

      }).dragend(

      function(marker) {

        $('#polygonCoordInput').val(marker.latLng);
        $('#savePolygonCoordinates').trigger('click');

      }).click(function() {

        map.gmap('openInfoWindow', {
          'content': "Drag me Sir!<br/>Distance slider is in the upper left corner.."
        }, this);
      });

    }

  });
}
function initMarkers(zoomin) {
  map.gmap('clear', 'markers');
  map.gmap('microdata', 'https://schema.org/Place', function(
          result, item, index) {
    markerProperties(result, item, index, zoomin);
  });
}

function animateDirectionTable() {
  if (hideDirectionTable) {
    if (hideMenu) {
      diplayMainMenu();
    }
    $('#directionLocation').hide();
    $('.arrowHideLocation').hide();
    $('.markersTable').animate({
      marginLeft: '0px',
    }, '1000');
    $('#arrowShowLocationsTable').hide();
    $('.markersTableContainer').hide();
    $('.tableNavigator').show();
    hideLocationTable = false;
    displayDirection();
    hideDirectionTable = false;
    var wait = setInterval(function() {
      if (!$('.markersTable').is(":animated")) {
        $('.arrowHideDirection').show();
        clearInterval(wait);
      }
    }, 0);
  } else {
    $('.markersTable').css({
      marginLeft: '-100%',
    });
    $('.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
            .width(318).css("overflow-y", "auto");
    $('.markersTableContainer').show();
    $('.tableNavigator').hide();
    hideLocationTable = true;
    hideDirectionTable = true;
    $('.arrowHideLocation').hide();
    $('.arrowHideDirection').hide();
  }
}

function displayDirectionTable() {
	
	  if ($('#directionLocation').is(':visible')) {
		    $('#directionLocation').hide();
		    $('.markersTableContainer').show();
		  } else {
		    $('#directionLocation').show();
		    $('.markersTableContainer').hide();
		  }
}

function showLocationTableIfNavigatorChange() {
	
  if (hideLocationTable == false) {
    $('#directionLocation').hide();
    $('.markersTableContainer').css({
      marginLeft: '0px'
    });
    $('.markersTableContainer').show();
    $('.markersTable').css({
      marginLeft: '0px',
    });
    var wait = setInterval(function() {
      if (!$('.markersTable').is(":animated")) {
        $('.arrowHideLocation').show();
        $('#arrowHideLocationsTable').show();
        clearInterval(wait);
      }
    }, 0);
    $('.markersTableContainer').show();
    $('.tableNavigator').show();
  }
}

function animateLocationTable() {
  if (hideLocationTable) {
    if (hideMenu) {
      diplayMainMenu();
    }

    $('#directionLocation').hide();
    $('.markersTableContainer').css({
      marginLeft: '0px'
    });
    $('.markersTableContainer').show();
    $('.tableNavigator').show();
    $('.markersTable').animate({
      marginLeft: '0px',
    }, '1000');
    var wait = setInterval(function() {
      if (!$('.markersTable').is(":animated")) {
        $('.arrowHideLocation').show();
        $('#arrowHideLocationsTable').show();
        clearInterval(wait);
      }
    }, 0);
    $('.markersTableContainer').show();
    hideLocationTable = false;
  } else {
    $('.arrowHideLocation').hide();
    $('.markersTable').animate({
      marginLeft: '-=100%',
    }, '500');
    var wait = setInterval(function() {
      if (!$('.markersTable').is(":animated")) {
    	  $('.arrowShowLocation').show();
        $('#arrowShowLocationsTable').show();
        clearInterval(wait);
      }
    }, 0);
    $('.markersTableContainer').css({
      marginLeft: '0px',
    });
    $('.tableNavigator').hide();
    hideLocationTable = true;
  }
}

function buttonDisplayLocations() {
	  if ($('.markersTable').is(':visible')) {
		    $('.markersTable').hide();
		  } else {
			  displayLocationsTable();
		  }
}

function displayLocationsTable() {
	
    $('.markersTableContainer').show();
    $('#directionLocation').hide();
    $('.markersTable').show();
}

function initGeolocation() {
  $('#iconDirectionGeoloc').click(
          function() {
            map.gmap('getCurrentPosition', function(position, status) {
              if (status === 'OK') {
                currentLatLng = new google.maps.LatLng(
                        position.coords.latitude, position.coords.longitude);
                //TODO
                //map.panTo(marker.position);

                map.gmap('search', {
                  'location': currentLatLng
                }, function(results, status) {
                  if (status === 'OK') {
                    $('#from').val(results[0].formatted_address);
                    $('#submitFindDirection').trigger("click");
                    $('#resetInputFrom').css('display', 'inline-block');
                  }
                });
              } else {
                alert('Unable to get current position');
              }
            });
          });
}

function getGeolocation() {
  map.gmap('getCurrentPosition', function(position, status) {
    if (status === 'OK') {
      currentLatLng = new google.maps.LatLng(position.coords.latitude,
              position.coords.longitude);
      map.gmap('get', 'map').panTo(currentLatLng);
      map.gmap('get', 'map').setZoom(10);

      map.gmap('search', {
        'location': currentLatLng
      }, function(results, status) {
        if (status === 'OK') {

          $('#polygonCoordInput').val(currentLatLng);
          $('#savePolygonCoordinates').trigger('click');
        }
      });
    } else {
      alert('Unable to get current position');
    }
  });
  //$(".slider_wrapper").show();
}

//function resetForm(id, img) {
//  $('#' + id).val('');
//  $('#' + img).hide();
//  wait = setInterval(function() {
//    if ($.trim($('#' + id).val('')) != '') {
//      $('#submitFindDirection').trigger("click");
//      clearInterval(wait);
//    }
//  }, 0);
//}

function resetForm(id, btn) {
	  $('#' + id).val('');
	  $('#' + btn).hide();

	}
var map = "";
var recalculate = false;
var waypts = [];
var currentLatLng = null;
var currentPosition = false;
var newEndPosition = false;
var hideLocationTable = true;
var hideMenu = false;
var hideDirectionTable = true;

var visible_direction = false

var findDirection = false;
var last_checked = null;
var totalMarkers = 0;
var origin = null;
var destination = null;
var change_color = false;
//var temp_name = null;
//var temp_lat = null;
//var temp_lng = null;
//var temp_id = null;
//var temp_rating = null;
//var temp_nrating = null;
var temp_latlng2 = null;
//var temp_id2 = null;
var remove_selection = false;
var insert_selected = true;
var route = [];
var _markerCluster = null;
var user = null;
var active_user = null;

// Goole Maps SKIN
var styleGrey = [{featureType: "administrative",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: 'landscape',elementType: 'all',stylers: [{ hue: '#FFFFFF' },{ saturation: -100 },{ lightness: 100 },{ visibility: 'on' }]},{featureType: "poi",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "road",elementType: "all",stylers: [{ visibility: "on" },{ lightness: -30 }]},{featureType: "transit",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "water",elementType: "all",stylers: [{ saturation: -100 },{ lightness: -100 }]},{featureType: "all",elementType: "all",stylers: [{ saturation: -100 },{ lightness: 91 }]}];
// Ref: https://snazzymaps.com/
var routeXL = [{"featureType":"administrative","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":20}]},{"featureType":"road","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":40}]},{"featureType":"water","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-10},{"lightness":30}]},{"featureType":"landscape.man_made","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":10}]},{"featureType":"landscape.natural","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":60}]},{"featureType":"poi","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]},{"featureType":"transit","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]}]

var styledMapOptions = { name: "MYTHEME" }
var greyMapType = new google.maps.StyledMapType( styleGrey, styledMapOptions );

var styledSnazzymaps = { name: "RouteXL" }
var snazzymapsMapType = new google.maps.StyledMapType( routeXL, styledSnazzymaps );

function initialize() {
  var from = document.getElementById('from');
  $('#from').on("change keyup paste", function() {
    new google.maps.places.Autocomplete(from, {
      types: ['geocode']
    });
    $('#resetInputFrom').css('display', 'inline-block');
    if ($(this).val() == '') {
      $('#resetInputFrom').hide();
    }
  });
  var to = document.getElementById('to');
  $('#to').on("change keyup paste", function() {
    new google.maps.places.Autocomplete(to, {
      types: ['geocode']
    });
    $('#resetInputTo').css('display', 'inline-block');
    if ($(this).val() == '') {
      $('#resetInputTo').hide();
    }
  });
  $('#mode').on('change', function() {
    $('#submitFindDirection').trigger("click");
  });

  $("#polygonCoordInput").on('keypress', function(e){
	  if(e.which == 13) {
	  $('#savePolygonCoordinates').trigger('click');
	  }
	  

 });
  
  
  

  // $(':checkbox').addClass("checkbox");
}
google.maps.event.addDomListener(window, 'load', initialize);

//function redrawchart(){
//    var chart = $('#container').highcharts();
//    
//    console.log('redraw');
//    var w = $('#container').closest(".wrapper").width()
//    // setsize will trigger the graph redraw 
//    chart.setSize(       
//        w,w,false
//    );
// }


$(document).ready(function(){
	

});

function setupFunc() {
//hideBusysign();
$('.fileinput-upload-button').on('click', function()
          {
	 
            showBusysign();
          }
     );
/*document.getElementsByTagName('fileinput-upload-button')[0].onclick = clickFunc;
    Wicket.Ajax.registerPreCallHandler(showBusysign);
    Wicket.Ajax.registerPostCallHandler(hideBusysign);
    Wicket.Ajax.registerFailureHandler(hideBusysign);*/
}
function hideBusysign() {
document.getElementById('bysy_indicator').style.display ='none';
}
function showBusysign() {
document.getElementById('bysy_indicator').style.display ='inline';
}

$(function() {
	
  var po = document.createElement('script');
  po.type = 'text/javascript';
  po.async = true;
  po.src = 'https://apis.google.com/js/plusone.js';
  var s = document.getElementsByTagName('script')[0];
  s.parentNode.insertBefore(po, s);
  
  var noPoi = [
	  {
	      featureType: "poi",
	      stylers: [
	        { visibility: "off" }
	      ]   
	    }
	  ];
  
  // google async end
  map = $('#map_canvas');
 
  map.gmap({
	'fullscreenControl': false,
    'panControl': false,
    'mapTypeId': google.maps.MapTypeId.ROADMAP,
     'zoom': 3,
     'minZoom': 3,
     //'maxZoom': 18,
    'center': new google.maps.LatLng(12.072820115129355, 17.519712325),
    'zoomControl': false,
    'zoomControlOptions': {
      position: google.maps.ControlPosition.RIGHT_BOTTOM
    },
    'scaleControl': false,
    'mapTypeControl': false,
    'mapTypeControlOptions': {
      position: google.maps.ControlPosition.LEFT_BOTTOM
    },
    'streetViewControl': false,
    'overviewMapControl': false,
    'draggable': true,
    'scrollwheel': true,
    'keyboardShortcuts': false,
    'disableDoubleClickZoom': false,
    'disableDefaultUI': true,
    'styles': noPoi
  });
  initIds();
  initMarkers(true);
  initGeolocation();
  initDirection();
  var getmap = map.gmap("get","map");


	 if($("#active_dictionary option:selected").text() == "true") {
		 	$(".dictionary_matches").show();
		 } else {
		 	$(".dictionary_matches").hide();
		 }
		 if($("#schema_autodetection option:selected").text() == "true") {
		 	$(".hide_schema_auto").show();
		 } else {
		 	$(".hide_schema_auto").hide();
		 }
		 setupFunc();
	$("#active_dictionary select").on("change",function(){
		 if($("#active_dictionary option:selected").text() == "true") {
		 	$(".dictionary_matches").show();
		 } else {
		 	$(".dictionary_matches").hide();
		 }
	});
	$("#schema_autodetection select").on("change",function(){
		 if($("#schema_autodetection option:selected").text() == "true") {
		 	$(".hide_schema_auto").show();
		 } else {
		 	$(".hide_schema_auto").hide();
		 }
	});
	  console.log("fullscreen11 is "+getSearchParams("fullscreen"));

    if (getSearchParams("fullscreen") == "false" || getSearchParams("fullscreen") == undefined) {
        console.log("has class off 1");
        $('.header').show();
        $('#map_wrapper').attr('style', 'top:41px!important');
    	$('#viewHidePanels').val('false');
    } else {
    	console.log("NOT class off 1");
    	$('#viewHidePanels').val('true');
  	  $('.header').hide();
	  $('#map_wrapper').attr('style', 'top:0px!important');
    }
	$('.toggle').click(function() {
	    if ($(this).hasClass('off')) {
	    	console.log("NOT class off 2");
		  	  $('.header').hide();
			  $('#map_wrapper').attr('style', 'top:0px!important');
		    	$('#viewHidePanels').val('true');

	    } else {
	        console.log("has class off 2");
		  	  $('.header').show();
		  	$('#map_wrapper').attr('style', 'top:41px!important');
	    	$('#viewHidePanels').val('false');
	    }
	});

	
	
	  $('[data-toggle="tooltip"]').tooltip(); 
  
//  $(window).resize(redrawchart);
//  redrawchart();
  
  console.log("fullscreen is "+getSearchParams("fullscreen"));
  if (getSearchParams("fullscreen") == "true") {

	  console.log("iss fullscreen");
  } else {
	  console.log("not fullscreen");
  }
  
  
  
  $("#textsearch-autocomplete > ul").on('click','li',function (){
	    alert($(this).text());
	});
  
  
  //slider time passing in seconds
  $('.carousel').carousel({
	  interval: 6000
	})
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
  
     console.log("value is "+activeValue+" val2 is "+seq_order+", max is "+valMap[valMap.length - 1]+" , and min is "+1);
     var handle = $( "#custom-handle" );
     $(".slider").slider({
         create: function() {
             handle.text(activeValue);
           },
           range: "min",
           max: valMap.length - 1,
       value: seq_order,
       change: function (event, ui) {
         if (event.originalEvent) {
        	 console.log("WE CHANGED VALUE TO "+valMap[ui.value]);
           $('#geoCoordDistance').val(valMap[ui.value]);
           $('#savePolygonCoordinates').trigger('click');
           }
        }
     }).slider("pips", {
       rest: "label",
    	   suffix: 'Km',
    	   labels: valMap
   	});
     //$("#slider").slider('value',activeValue);


	var all_overlays = [];
	var selectedShape;
	var colors = ['#1E90FF', '#FF1493', '#32CD32', '#FF8C00', '#4B0082'];
	var selectedColor;
	var colorButtons = {};
	var polyOption = {
			  zIndex: 1300,
			    fillColor: 'rgba(255,255,255,.87)',
			    clickable: true,
			    editable: true,
			    strokeWeight: 0,
			    fillOpacity: 0.45,
			    draggable: false,
		        flat: true,
			};

	  
	  var drawingManager = new google.maps.drawing.DrawingManager({
	      //drawingMode: google.maps.drawing.OverlayType.MARKER,
		  drawingMode: null,
	      drawingControl: true,
	      drawingControlOptions: {
	        position: google.maps.ControlPosition.TOP_CENTER,
	        drawingModes: [
	            google.maps.drawing.OverlayType.MARKER,
	            google.maps.drawing.OverlayType.RECTANGLE,
	            google.maps.drawing.OverlayType.POLYGON
	            //google.maps.drawing.OverlayType.CIRCLE,
	            //google.maps.drawing.OverlayType.POLYLINE,
	          ]
	      },
	      circleOptions: polyOption,
	      rectangleOptions: polyOption,
	      polygonOptions: polyOption,
	      polylineOptions: polyOption,
//	      markerOptions: polyOption2
	    });
	   
	    google.maps.event.addListener(drawingManager, 'polygoncomplete', function (polygon) {
	    	  google.maps.event.addListener(polygon, "mouseup", function(e){
	    		      $('#polygonCoordInput').val('');
	    		      $('#polygonCoordInput').val(polygon.getPath().getArray());
	    		      $('#savePolygonCoordinates').trigger('click');
	    	  });

	    	});


	    	google.maps.event.addListener(drawingManager, 'markercomplete', function (marker) {
		    	  if (marker && marker.setMap) {
			    	    marker.setMap(null);
			    	  }
	    	  google.maps.event.addListener(marker, "mouseup", function(e){
	    		        $('#polygonCoordInput').val('');
	    		        $('#polygonCoordInput').val(marker.getPosition());
	    		        //$(".slider_wrapper").show();
	    		        $('#savePolygonCoordinates').trigger('click');
	    		        drawingManager.setDrawingMode(null);
	    	  });

	    	});

	    	google.maps.event.addListener(drawingManager, 'rectanglecomplete', function (rectangle) {
	    	  google.maps.event.addListener(rectangle, "bounds_changed", function(e){
	    		        var bounds = rectangle.getBounds();
	    		        $('#polygonCoordInput').val('');
	    		        var NE = bounds.getNorthEast();
	    		        var SW = bounds.getSouthWest();
	    		        var NW = new google.maps.LatLng(NE.lat(),SW.lng());
	    		        var SE = new google.maps.LatLng(SW.lat(),NE.lng());
	    		        $('#polygonCoordInput').val(NE+","+SE+","+SW+","+NW);
	    		        $('#savePolygonCoordinates').trigger('click');
	    		        drawingManager.setDrawingMode(null);
	    	  });

	    	});
	    
	    
	    
	    
  google.maps.event.addListenerOnce(getmap, 'tilesloaded', function(){
	  //getmap.setMapTypeId("RouteXL");

	  getmap.addListener('zoom_changed', function() {
		  $('#mapZoomLevel').val(map.gmap("get","map").getZoom());
		});

	// call zoom control
	ZoomControl(getmap);
	getmap.mapTypes.set("MYTHEME", greyMapType);
	getmap.mapTypes.set("RouteXL", snazzymapsMapType);

	drawingManager.setMap(getmap);
		  google.maps.event.addListener(drawingManager, 'drawingmode_changed', clearSelection);
		  google.maps.event.addListener(map, 'click', clearSelection);
		  //google.maps.event.addDomListener(document.getElementById('delete-button'), 'click', deleteSelectedShape);
		  google.maps.event.addDomListener(document.getElementById('delete-all-button'), 'click', deleteAllShape);

		  buildColorPalette();
	   
	    
	    google.maps.event.addListener(drawingManager, 'overlaycomplete', function(e) {
	        all_overlays.push(e);
	        

	        if (e.type != google.maps.drawing.OverlayType.MARKER) {
	          // Switch back to non-drawing mode after drawing a shape.
	          drawingManager.setDrawingMode(null);

	          // Add an event listener that selects the newly-drawn shape when the user
	          // mouses down on it.
	          var newShape = e.overlay;
	          newShape.type = e.type;
	          google.maps.event.addListener(newShape, 'click', function() {
	            setSelection(newShape);
	          });
	          setSelection(newShape);
	          
	          if (e.type == google.maps.drawing.OverlayType.RECTANGLE) {
	              var bounds = newShape.getBounds();
	              $('#polygonCoordInput').val('');
	              var NE = bounds.getNorthEast();
	              var SW = bounds.getSouthWest();
	              var NW = new google.maps.LatLng(NE.lat(),SW.lng());
	              var SE = new google.maps.LatLng(SW.lat(),NE.lng());
	              $('#polygonCoordInput').val(NE+","+SE+","+SW+","+NW);
	              $('#savePolygonCoordinates').trigger('click');
	              drawingManager.setDrawingMode(null);
	          }
	          if (e.type == google.maps.drawing.OverlayType.POLYGON) {
	                  $('#polygonCoordInput').val('');
	                  $('#polygonCoordInput').val(newShape.getPath().getArray());
	                  $('#savePolygonCoordinates').trigger('click');
	          }
	        } else {
	            $('#polygonCoordInput').val('');
	            $('#polygonCoordInput').val(e.overlay.getPosition());
	            //$(".slider_wrapper").show();
	            $('#savePolygonCoordinates').trigger('click');
	            drawingManager.setDrawingMode(null);
	        }
	      });

	  $(window).resize(function() {
	    map.gmap('refresh');
	  });
	
  });

	function clearSelection() {
	  if (selectedShape) {
	    selectedShape.setEditable(false);
	    selectedShape = null;
	  }
	}

	function setSelection(shape) {
	  clearSelection();
	  selectedShape = shape;
	  shape.setEditable(true);
	  selectColor(shape.get('fillColor') || shape.get('strokeColor'));
	}

	function deleteSelectedShape() {
	  if (selectedShape) {
		selectedShape.setMap(null);
        if (selectedShape.type == google.maps.drawing.OverlayType.RECTANGLE)  {
        	console.log("rect seria "+selectedShape.getBounds());
            var bounds = selectedShape.getBounds();
            console.log("the rect coordinates first are: "+bounds);
            $('#polygonCoordInput').val('');
            var NE = bounds.getNorthEast();
            var SW = bounds.getSouthWest();
            var NW = new google.maps.LatLng(NE.lat(),SW.lng());
            var SE = new google.maps.LatLng(SW.lat(),NE.lng());
            $('#polygonCoordInput').val(NE+","+SE+","+SW+","+NW);
            $('#deletePolygonCoordinates').trigger('click');

        }
        if (selectedShape.type == google.maps.drawing.OverlayType.POLYGON)  {
        	console.log("poly seria "+selectedShape.getPath().getArray());

                $('#polygonCoordInput').val('');
                $('#polygonCoordInput').val(selectedShape.getPath().getArray());
                $('#deletePolygonCoordinates').trigger('click');
        }
	  }
	  showLocationTableIfNavigatorChange();
	}

	function deleteAllShape() {
	  for (var i = 0; i < all_overlays.length; i++) {
	    all_overlays[i].overlay.setMap(null);
	  }
	  all_overlays = [];
      $('#polygonCoordInput').val("");
      $('#deletePolygonCoordinates').trigger('click');
      showLocationTableIfNavigatorChange();
	}

	function selectColor(color) {
	  selectedColor = color;
	  for (var i = 0; i < colors.length; ++i) {
	    var currColor = colors[i];
	    colorButtons[currColor].style.border = currColor == color ? '2px solid #789' : '2px solid #fff';
	  }

	  // Retrieves the current options from the drawing manager and replaces the
	  // stroke or fill color as appropriate.
	  var polylineOptions = drawingManager.get('polylineOptions');
	  polylineOptions.strokeColor = color;
	  drawingManager.set('polylineOptions', polylineOptions);

	  var rectangleOptions = drawingManager.get('rectangleOptions');
	  rectangleOptions.fillColor = color;
	  drawingManager.set('rectangleOptions', rectangleOptions);

	  var circleOptions = drawingManager.get('circleOptions');
	  circleOptions.fillColor = color;
	  drawingManager.set('circleOptions', circleOptions);

	  var polygonOptions = drawingManager.get('polygonOptions');
	  polygonOptions.fillColor = color;
	  drawingManager.set('polygonOptions', polygonOptions);
	}

	function setSelectedShapeColor(color) {
	  if (selectedShape) {
	    if (selectedShape.type == google.maps.drawing.OverlayType.POLYLINE) {
	      selectedShape.set('strokeColor', color);
	    } else {
	      selectedShape.set('fillColor', color);
	    }
	  }
	}

	function makeColorButton(color) {
	  var button = document.createElement('span');
	  button.className = 'color-button';
	  button.style.backgroundColor = color;
	  google.maps.event.addDomListener(button, 'click', function() {
	    selectColor(color);
	    setSelectedShapeColor(color);
	  });

	  return button;
	}

	function buildColorPalette() {
	  var colorPalette = document.getElementById('color-palette');
	  for (var i = 0; i < colors.length; ++i) {
	    var currColor = colors[i];
	    var colorButton = makeColorButton(currColor);
	    colorPalette.appendChild(colorButton);
	    colorButtons[currColor] = colorButton;
	  }
	  selectColor(colors[0]);
	}

  if (user == "admin") {
    $('.iconEditInfo').show();
    $('.editInfo').show();
  }

	// Zoom control function
	function ZoomControl ( map ) {
		var zoomIn = document.getElementById('zoomIn');
		var zoomOut = document.getElementById('zoomOut');

		google.maps.event.addDomListener(zoomOut, 'click', function() {
			var currentZoomLevel = map.getZoom();
			if(currentZoomLevel != 0){
				map.setZoom(currentZoomLevel - 1);}     
		});

		google.maps.event.addDomListener(zoomIn, 'click', function() {
		var currentZoomLevel = map.getZoom();
			if(currentZoomLevel != 21){
				map.setZoom(currentZoomLevel + 1);}
		});
	}

	// Change Map TypeId
	function TypeIdChange(option) {
		switch(option) {
          case "1":
        	  getmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
              break;
          case "2":
       	   		getmap.setMapTypeId(google.maps.MapTypeId.SATELLITE);
             break;
          case "3":
        	  getmap.setMapTypeId(google.maps.MapTypeId.TERRAIN);
              break;
          case "4":
        	  getmap.setMapTypeId("RouteXL");
              break;
          case "5":
        	  getmap.setMapTypeId("MYTHEME");
              break;

          default:
        	  getmap.setMapTypeId( google.maps.MapTypeId.ROADMAP );
      }
	}

	$('#mapTypeId').change(function () {
		var self = $(this);
		TypeIdChange(self.val());
	});

	google.maps.event.addDomListener(window, 'load', initialize);
  
  
});
