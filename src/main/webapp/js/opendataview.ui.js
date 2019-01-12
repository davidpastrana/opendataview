function getCurrentDateTimeShort() {
  var currentDateTime = new moment();
  currentDateTime = currentDateTime.format("DDMMYY_HH.mm");
  return currentDateTime.toString();
}

function exportGoogleMap() {

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
}

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

                    if (display == 'true' || recalculate) {
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
                        console.log("no value");
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
                                            alert('Error. Introduce una dirección más exacta.');
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

function displayStreetView(marker) {
  var idMarker = $(marker);
  if (idMarker.attr('id') == null) {
    $(".markersTableContainer").one('click', 'tr', function() {
      num_row = $(this).attr('id').substring(6);
      if (($('#streetviewSize' + num_row)).is(':visible')) {
        $('#streetview' + num_row).animate({
          opacity: 'hide'
        }, 100);
        $('#streetviewSize' + num_row).slideUp("slow");
      } else {
        if (($('#moreInfo' + num_row)).is(':visible')) {
          $('#moreInfo' + num_row).hide();
          $('#streetviewSize' + num_row).show();
          $('#streetview' + num_row).animate({
            opacity: 'show'
          }, 1000);
          $('#streetview' + num_row).trigger('click');
        } else {
          $('#streetviewSize' + num_row).slideDown("slow");
          $('#streetview' + num_row).animate({
            opacity: 'show'
          }, 1000);
          $('#streetview' + num_row).trigger('click');
        }
      }
    });
  }
  if (idMarker.attr('id') != null) {
    num_row = idMarker.attr('id').substring(18);
    if (($('#streetviewSize' + num_row)).is(':visible')) {
      $('#streetview' + num_row).animate({
        opacity: 'hide'
      }, 100);
      $('#streetviewSize' + num_row).slideUp("slow");
    } else {
      if (($('#moreInfo' + num_row)).is(':visible')) {
        $('#moreInfo' + num_row).hide();
        $('#streetviewSize' + num_row).show();
        $('#streetview' + num_row).animate({
          opacity: 'show'
        }, 1000);
        $('#streetview' + num_row).trigger('click');
      } else {
        $('#streetviewSize' + num_row).slideDown("slow");
        $('#streetview' + num_row).animate({
          opacity: 'show'
        }, 1000);
        $('#streetview' + num_row).trigger('click');
      }
    }
  }
}

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

//function markerInfo(name, item, index, id) {
//  var markerDetails = '';
//  markerDetails += '<div class="markerInfo notranslate">';
//  markerDetails += '<h5>' + name + '</h5>';
//  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayMoreInfo('
//          + $(item).attr('id')
//          + ');"><img src="/images/info-icon.png" class="iconInfoMarker" title="More information" alt="More Info" /></a>';
//  markerDetails += '<a onclick="hideDirectionTable ? displayDirectionTable() : displayDirectionTable()"><img src="/images/direction-icon.png" class="iconDirectionMarker" title="How to get here?" alt="Direction" /></a>';
//  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
//          + $(item).attr('id')
//          + ');"><img src="/images/streetview-icon.png" class="iconStreetViewMarker" title="Street View" alt="Street View" /></a>';
//  markerDetails += '<a id="unsel'
//          + index
//          + '" onclick="change_color=true;insert_selected=true;initMarkers2();displayTable();changeToUnselect('
//          + index
//          + ')"><img src="/images/marked-icon.png" class="iconRouteMarker" title="Insert into Tour?" alt="Add marker" /></a>';
//  if (user == "admin") {
//    markerDetails += '<a onclick="getIdMarker('
//            + id
//            + ','
//            + $(item).attr('id')
//            + ');"><img src="/images/edit-icon.png" class="iconEditMarker" title="Edit informaion" alt="Edit Info" /></a>';
//  }
//  markerDetails += '</div>';
//  return markerDetails;
//}

function markerInfo(name, item, index, id) {
	  var markerDetails = '';
	  markerDetails += '<div class="markerInfo notranslate">';
	  markerDetails += '<h5>' + name + '</h5>';
	  markerDetails += '<i class="fa fa-info-circle fa-2x iconInfoMarker" onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayMoreInfo('
          + $(item).attr('id')
          + ')"></i>';
	  markerDetails += '<i class="fa fa-map fa-2x iconDirectionMarker" onclick="hideDirectionTable ? displayDirectionTable() : displayDirectionTable()"></i>';
	  markerDetails += '<i class="fa fa-street-view fa-2x iconStreetViewMarker" onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
          + $(item).attr('id')
          + ')"></i>';
//	  markerDetails += '<a id="unsel'
//	          + index
//	          + '" onclick="change_color=true;insert_selected=true;displayTable();changeToUnselect('
//	          + index
//	          + ')"><i class="fa fa-check-circle fa-2x iconRouteMarker"></i>';
	  if (user == "admin") {
	    markerDetails += '<i class="fa fa-pencil fa-2x iconEditMarker" onclick="getIdMarker('
            + id
            + ','
            + $(item).attr('id')
            + ')"></i>';
	  }
	  markerDetails += '</div>';
	  return markerDetails;
	}



//function markerInfoTour(name, item) {
//  var markerDetails = '';
//  markerDetails += '<div class="markerInfo notranslate">';
//  markerDetails += '<h5 class="">' + name + '</h5>';
//  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayMoreInfo('
//          + $(item).attr('id')
//          + ');"><img src="/images/info-icon.png" class="iconInfoMarker" title="More information" alt="More Info" /></a>';
//  markerDetails += '<a onclick="hideDirectionTable ? displayDirectionTable() : displayDirectionTable()"><i class="fa fa-directions fa-4x iconDirectionMarker"></i>';
//  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
//          + $(item).attr('id')
//          + ');changeToUnselect('
//          + item
//          + ');"><i class="fa fa-street-view fa-4x iconStreetViewMarker"></i>';
//  markerDetails += '</div>';
//  return markerDetails;
//}
//
//
//function markerInfoSelected(name, item, index) {
//	  var markerDetails = '';
//	  markerDetails += '<div class="markerInfo notranslate">';
//	  markerDetails += '<h5>' + name + '</h5>';
//	  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayMoreInfo('
//	          + $(item).attr('id')
//	          + ');"><i class="fa fa-info-circle fa-4x iconInfoMarker"></i>';
//	  markerDetails += '<a onclick="hideDirectionTable ? displayDirectionTable() : displayDirectionTable()"><i class="fa fa-directions fa-4x iconDirectionMarker"></i>';
//	  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
//	          + $(item).attr('id')
//	          + ');"><i class="fa fa-street-view fa-4x iconStreetViewMarker"></i>';
//	  markerDetails += '<a id="sel'
//	          + index
//	          + '" onclick="remove_selection=true;initMarkers2();changeToSelected('
//	          + index
//	          + ')"><i class="fa fa-minus-circle fa-4x iconRemoveRouteMarker"></i>';
//	  markerDetails += '</div>';
//	  return markerDetails;
//	}

//function markerInfoSelected(name, item, index) {
//  var markerDetails = '';
//  markerDetails += '<div class="markerInfo notranslate">';
//  markerDetails += '<h5>' + name + '</h5>';
//  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayMoreInfo('
//          + $(item).attr('id')
//          + ');"><img src="/images/info-icon.png" class="iconInfoMarker" title="More information" alt="More Info" /></a>';
//  markerDetails += '<a onclick="hideDirectionTable ? displayDirectionTable() : displayDirectionTable()"><img src="/images/direction-icon.png" class="iconDirectionMarker" title="How to arrive?" alt="Direction" /></a>';
//  markerDetails += '<a onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
//          + $(item).attr('id')
//          + ');"><img src="/images/streetview-icon.png" class="iconStreetViewMarker" title="Street View" alt="Street View" /></a>';
//  markerDetails += '<a id="sel'
//          + index
//          + '" onclick="remove_selection=true;initMarkers2();changeToSelected('
//          + index
//          + ')"><img src="/images/clear-icon.png" class="iconRemoveRouteMarker" title="Delete from route?" alt="Remove marker" /></a>';
//  markerDetails += '</div>';
//  return markerDetails;
//}

function typeIcon(csvName) {
	
	var csvName = csvName.toLowerCase();
	//console.log("the csvname issss "+csvName);
	
	if(csvName.match(/(.*party.*|.*fiesta.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_01.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	} else if(csvName.match(/(.*hospital.*|.*disease.*|.*cure.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_02.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	} else if(csvName.match(/(.*coffee.*|.*break.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_03.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	} else if(csvName.match(/(.*food.*|.*meal.*|.*comida.*|.*hosteleria.*|.*restaura.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_22.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	} else if(csvName.match(/(.*comerc.*|.*shop.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_54.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	}
	else if(csvName.match(/(.*deporte.*|.*sports.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_12.svg',
				scaledSize: new google.maps.Size(28, 28),
				
	    };
	}
	else if(csvName.match(/(.*industri.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_61.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	}
	else if(csvName.match(/(.*beer.*|.*bars.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_25.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	}
	else if(csvName.match(/(.*nature.*|.*mountain.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_07.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	}
	else if(csvName.match(/(.*museums.*|.*monuments.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_62.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	}
	else if(csvName.match(/(.*open.*|.*data.*|.*share.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_89.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	} else if(csvName.match(/(.*repair.*|.*auto.*)/g)) {
		icon = {
				url: '/images/markers/svg/wsd_markers_08.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	} else {
		icon = {
				url: '/images/markers/svg/wsd_markers_88.svg',
				scaledSize: new google.maps.Size(28, 28),
			    };
	}
//  switch (csvName) {
//  case '[ Default ]':
//    icon = {
//      'url': '/images/markers/icon-gmap__.png'
//    };
//    break;
//  case match(/.*open.*/g)://'Attractions':
//    icon = {
//      'url': '/images/markers/category/icon-attractions.png'
//    };
//    break;
//  case csvName.match(/Open/)://'Top places':
//    icon = {
//      'url': '/images/markers/category/icon-top_places.png'
//    };
//    break;
//  case 'Camping':
//    icon = {
//      'url': '/images/markers/category/icon-camping.png'
//    };
//    break;
//  case 'Christmas markets':
//    icon = {
//      'url': '/images/markers/category/icon-christmas-markets.png'
//    };
//    break;
//  case 'City bike':
//    icon = {
//      'url': '/images/markers/category/icon-city-bike.png'
//    };
//    break;
//  case 'City walks':
//    icon = {
//      'url': '/images/markers/category/icon-city-walk.png'
//    };
//    break;
//  case 'Donau leisure venues':
//    icon = {
//      'url': '/images/markers/category/icon-donau.png'
//    };
//    break;
//  case 'Natural monuments':
//    icon = {
//      'url': '/images/markers/category/icon-natural-monuments.png'
//    };
//    break;
//  case 'Parks':
//    icon = {
//      'url': '/images/markers/category/icon-parks.png'
//    };
//    break;
//  case 'Swimming places':
//    icon = {
//      'url': '/images/markers/category/icon-swimming.png'
//    };
//    break;
//  case 'Markets':
//    icon = {
//      'url': '/images/markers/category/icon-vienna_markets.png'
//    };
//    break;
//  case 'Police':
//    icon = {
//      'url': '/images/markers/category/icon-police.png'
//    };
//    break;
//  case 'myPosition':
//    icon = {
//      'url': '/images/markers/icon-gmap-start.png'
//    };
//    break;
//  default:
//    icon = {
//      'url': '/images/markers/icon-gmap___.png'
//    };
//  }
  return icon;
}

function markerProperties(result, item, index) {

  var name = result.name;
  var csvName = result.alternateName;
  user = result.user_name;

  var id = result.id;
  var lat = result.geo[0].latitude;
  var lng = result.geo[0].longitude;
  var latlng = new google.maps.LatLng(lat, lng);
//  var rating = result.aggregateRating[0].ratingValue;
//  var nrating = result.aggregateRating[0].reviewCount;

  if (csvName == "myPosition") {
    markerOptions1 = {
      'zIndex': 1300,
      'bounds': false,
      'position': latlng,
      'draggable': true,
      'title': "My Position",
      'icon': new google.maps.MarkerImage('/images/markers/icon-gmap-start.png')
    };

  } else {
    var markerOptions1 = {
      'id': index,
      'zIndex': 800,
      'position': latlng,
      'title': "Test",
      'markerDetails': markerInfo(name, item, index, id),
      'icon': typeIcon(csvName),
      'optimized': false,
      'bounds': false
    };
  }

  map
          .gmap('addMarker', markerOptions1, function(map, marker) {

            $(item).click(function(event) {
              $(marker).triggerEvent('click');
              map.panTo(marker.position);
              if ($(event.target).is('.moreInfo')) {
                $(item).unbind('click');
              }
              if ($(event.target).hasClass('streetview')) {
                $(item).unbind('click');
              }
              return false;
            });

          })
          .drag(

          function(marker) {

            console.log("THE LAT IS " + marker.latLng.lat());
            $('#geoCoordInput').val(marker.latLng);
            // document.getElementById('lat').value = event.latLng.lat();
            // document.getElementById('lng').value = event.latLng.lng();

          })
          .dragend(

          function(marker) {

            console.log("ZZTHE LAT IS " + marker.latLng.lat());
            $('#geoCoordInput').val(marker.latLng);
            
            $('#saveGeoCoordinates').trigger('click');
            // document.getElementById('lat').value = event.latLng.lat();
            // document.getElementById('lng').value = event.latLng.lng();

          })
          .click(
                  function() {

                    map.gmap('openInfoWindow', {
                      'content': markerInfo(name, item, index, id)
                    }, this);

                    $(item).addClass('markerClicked');

                    if (last_checked != "") {
                      $(last_checked).removeClass('markerClicked');
                    }

                    if (!isScrolledIntoView($(item))) {
                      checkListSelection($(item));
                    }

                    last_checked = $(item);

                    map.gmap('search', {
                      'location': this.position
                    }, function(results, status) {
                      if (status === 'OK') {
                        $('#to').val(results[0].formatted_address);
                      }
                    });
                    temp_name = name;
                    temp_id = id;
                    temp_lat = lat;
                    temp_lng = lng;
//                    temp_rating = rating;
//                    temp_nrating = nrating;


                    var sv = new google.maps.StreetViewService();
                    sv
                            .getPanoramaByLocation(
                                    this.position,
                                    100,
                                    function(result, status) {
                                      if (status == google.maps.StreetViewStatus.OK) {
                                        var svPos = result.location.latLng;
                                        map.gmap('displayStreetView',
                                                'streetview' + index, {
                                                  'position': svPos,
                                                  'pov': {
                                                    'heading': 1,
                                                    'pitch': 1,
                                                    'zoom': 0
                                                  }
                                                });
                                        state = false;
                                      } else {
                                        $('#streetview' + index)
                                                .html(
                                                        "<div class='errorMessage'>Sorry there is no Street View service within 100 meters from the location.</div>");
                                      }
                                    });
                  });
}

//function markerProperties2(result, item, index) {
//  var name = result.name;
//  var csvName = result.csvName;
//
//  var id = result.id;
//  var lat = result.geo[0].latitude;
//  var lng = result.geo[0].longitude;
//  var latlng = new google.maps.LatLng(lat, lng);
//
//  console.log('DESPUES');
//  var icon2 = {
//    'url': '/images/markers/icon-selection.png'
//  };
//  var markerOptions = {
//    'id': index,
//    'zIndex': 800,
//    'position': latlng,
//    'title': name,
//    'markerDetails': markerInfoTour(name, item),
//    'icon': typeIcon(csvName),
//    'optimized': false,
//    'bounds': false
//  };
//
//  if (remove_selection) {
//
//    route.splice(temp_id, 1);
//    $.each(route, function(j, val2) {
//
//      if ($.trim(val2.name) == $.trim(temp_name)) {
//
//        var coord = $('#coordInput').val();
//
//        if (coord.match(new RegExp(temp_id, 'i'))) {
//
//          var str_match = coord.split(';');
//          var str2 = "";
//          for (i = 0; i < str_match.length - 1; i++) {
//            var str_match2 = str_match[i].split(',');
//            console.log("coord id " + str_match2[0]);
//            console.log("id clicked " + temp_id2);
//            if (str_match2[0] == temp_id2) {
//
//            } else {
//              str2 += str_match2[0] + "," + str_match2[1] + "," + str_match2[2]
//                      + "," + str_match2[3] + "," + str_match2[4] + ";";
//            }
//
//          }
//          $('#coordInput').val(str2);
//
//        }
//
//        var markers = map.gmap('get', 'markers');
//        var cont = 0;
//        $(markers).each(function(index, elem) {
//
//          if ($.trim(elem.title) == $.trim(temp_name)) {
//            if (cont >= 1) {
//              elem.setVisible(false);
//            }
//            cont++;
//          }
//        });
//
//        // we delete selected marker
//        route.splice(j, 1);
//      }
//    });
//
//    remove_selection = false;
//    change_color = true;
//    insert_selected = false;
//  }
//
//  display = localStorage['displayOverlays'];
//
//  // when the route is marked
//  if (display == 'true' || recalculate) {
//
//    var lat1 = $(
//            '#moreInfo0 > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2) > div:nth-child(1) > span:nth-child(1)')
//            .text();
//    var lng1 = $(
//            '#moreInfo0 > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2) > div:nth-child(1) > span:nth-child(2)')
//            .text();
//    var lat2 = $(
//            '#moreInfo'
//                    + (totalMarkers - 1)
//                    + ' > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2) > div:nth-child(1) > span:nth-child(1)')
//            .text();
//    var lng2 = $(
//            '#moreInfo'
//                    + (totalMarkers - 1)
//                    + ' > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2) > div:nth-child(1) > span:nth-child(2)')
//            .text();
//
//    if (!currentPosition && !newEndPosition) {
//      origin = new google.maps.LatLng(lat1, lng1);
//      destination = new google.maps.LatLng(lat2, lng2);
//    }
//
//    if (!currentPosition && !newEndPosition) {
//      if (index != 0 && index != (totalMarkers - 1) && !recalculate) {
//        waypts.push({
//          location: latlng,
//          stopover: true
//        });
//      }
//
//      if (!recalculate) {
//        displayWaypoints(origin, destination);
//      }
//    }
//
//    map.gmap('addMarker', markerOptions, function(map, marker) {
//    });
//
//    var icons = {
//      start: new google.maps.MarkerImage('/images/markers/icon-gmap-start.png'),
//      waypoint: new google.maps.MarkerImage(
//              '/images/markers/icon-gmap-waypoint.png'),
//      end: new google.maps.MarkerImage('/images/markers/icon-gmap-end.png')
//    };
//
//    var markerOptions3 = null;
//
//    // if marker is start point
//    if (index == 0 || currentPosition) {
//      markerOptions3 = {
//        'zIndex': 1300,
//        'bounds': false,
//        'position': origin,
//        'title': name,
//        'markerDetails': markerInfoTour(name, item),
//        'icon': icons.start
//      };
//    }
//
//    // if marker is way point
//    if (index > 0 && index < (totalMarkers - 1)) {
//      if (currentPosition) {
//        $.each(waypts, function(index, val) {
//          markerOptions3 = {
//            'zIndex': 1300,
//            'bounds': false,
//            'position': val.location,
//            'title': name,
//            'markerDetails': markerInfoTour(name, item),
//            'icon': icons.waypoint
//          };
//        });
//      } else {
//        markerOptions3 = {
//          'zIndex': 1300,
//          'bounds': false,
//          'position': latlng,
//          'title': name,
//          'markerDetails': markerInfoTour(name, item),
//          'icon': icons.waypoint
//        };
//      }
//    }
//
//    // if marker is end point
//    if (index == (totalMarkers - 1) || newEndPosition || currentPosition) {
//      markerOptions3 = {
//        'zIndex': 1300,
//        'bounds': false,
//        'position': destination,
//        'title': name,
//        'markerDetails': markerInfoTour(name, item),
//        'icon': icons.end
//      };
//    }
//
//    map
//            .gmap('addMarker', markerOptions3, function(map, marker) {
//
//              $(item).click(function(event) {
//                $(marker).triggerEvent('click');
//                map.panTo(marker.position);
//                if ($(event.target).is('a')) {
//                  $(item).unbind('click');
//                }
//                if ($(event.target).hasClass('streetview')) {
//                  $(item).unbind('click');
//                }
//                return false;
//              });
//
//            })
//            .click(
//                    function() {
//
//                      map.gmap('openInfoWindow', {
//                        'content': markerInfoTour(name, item)
//                      }, this);
//
//                      $(item).addClass('markerClicked');
//
//                      if (last_checked != "") {
//                        $(last_checked).removeClass('markerClicked');
//                      }
//
//                      if (!isScrolledIntoView($(item))) {
//                        checkListSelection($(item));
//                      }
//
//                      last_checked = $(item);
//
//                      map.gmap('search', {
//                        'location': this.position
//                      }, function(results, status) {
//                        if (status === 'OK') {
//                          $('#to').val(results[0].formatted_address);
//                        }
//                      });
//                      temp_id = id;
//                      temp_lat = lat;
//                      temp_lng = lng;
//
//                      var sv = new google.maps.StreetViewService();
//                      sv
//                              .getPanoramaByLocation(
//                                      this.position,
//                                      100,
//                                      function(result, status) {
//                                        if (status == google.maps.StreetViewStatus.OK) {
//                                          var svPos = result.location.latLng;
//                                          map.gmap('displayStreetView',
//                                                  'streetview' + index, {
//                                                    'position': svPos,
//                                                    'pov': {
//                                                      'heading': 1,
//                                                      'pitch': 1,
//                                                      'zoom': 0
//                                                    }
//                                                  });
//                                          state = false;
//                                        } else {
//                                          $('#streetview' + index)
//                                                  .html(
//                                                          "<div class='errorMessage'>Sorry there is no Street View service within 100 meters from the location.</div>");
//                                        }
//                                      });
//                    });
//  }
//
//  if (change_color) {
//
//    if (insert_selected) {
//      route.push({
//        location: new google.maps.LatLng(temp_lat, temp_lng),
//        name: temp_name,
//        marker_id: temp_id
//      });
////      $('#coordInput').val(
////              $('#coordInput').val() + temp_id + ',' + temp_rating + ','
////                      + temp_nrating + ',' + temp_lat + ',' + temp_lng + ';');
//      insert_selected = false;
//    }
//
//    $.each(route, function(i, val) {
//
//      markerOptions2 = {
//        'zIndex': 1000,
//        'bounds': false,
//        'position': val.location,
//        'title': val.name,
//        'markerDetails': markerInfo(name, val.marker_id, index, id),
//        'icon': icon2
//      };
//
//      map.gmap('addMarker', markerOptions2, function(map, marker) {
//
//        $(item).click(function(event) {
//          $(marker).triggerEvent('click');
//          map.panTo(marker.position);
//          return false;
//        });
//
//      }).click(function() {
//
//        map.gmap('openInfoWindow', {
//          'content': markerInfoSelected(val.name, val.marker_id, index)
//        }, this);
//
//        $(item).addClass('markerClicked');
//
//        if (last_checked != "") {
//          $(last_checked).removeClass('markerClicked');
//        }
//
//        if (!isScrolledIntoView($(item))) {
//          checkListSelection($(item));
//        }
//
//        last_checked = $(item);
//
//        map.gmap('search', {
//          'location': this.position
//        }, function(results, status) {
//          if (status === 'OK') {
//            $('#to').val(results[0].formatted_address);
//          }
//        });
//
//        temp_name = val.name;
//        temp_latlng2 = this.location;
//        temp_id2 = val.marker_id;
//        remove_selection = true;
//      });
//    });
//    change_color = false;
//  }
//}

function initMyPosition() {

  map.gmap('microdata', 'https://schema.org/GeoCoordinates', function(result,
          item, index) {
    var lat = result.geo[0].latitude;
    var lng = result.geo[0].longitude;
    console.log('EOOOOOOOOOOO IS: ' + lat);

    if (lat != 0 && lng != 0) {

      var name = result.name;
      var csvName = result.csvName;
      var id = result.id;
      var lat = result.geo[0].latitude;
      var lng = result.geo[0].longitude;
      var latlng = new google.maps.LatLng(lat, lng);

      markerOptions1 = {
        'zIndex': 1300,
        //'animation': google.maps.Animation.DROP,
        'bounds': false,
        'position': latlng,
        'draggable': true,
        'title': "My Position",
        'icon': new google.maps.MarkerImage(
                '/images/markers/icon-gmap-start.png')
      };
      map.gmap('addMarker', markerOptions1, function(map, marker) {

        $(item).click(function(event) {
          $(marker).triggerEvent('click');
          map.panTo(marker.position);

          return false;
        });

      }).drag(

      function(marker) {

        $('#geoCoordInput').val(marker.latLng);

      }).dragend(

      function(marker) {

        $('#geoCoordInput').val(marker.latLng);
        $('#saveGeoCoordinates').trigger('click');

      }).click(function() {

        map.gmap('openInfoWindow', {
          'content': "My position<br/>drag icon and set Km range"
        }, this);
      });

    }

  });
}
function initMarkers() {

  map.gmap('clear', 'markers');
//  initGeolocation();
//  initDirection();
  map.gmap('microdata', 'https://schema.org/TouristAttraction', function(
          result, item, index) {
    //display = localStorage['displayOverlays'];
    markerProperties(result, item, index);
//    // when the route is marked
//    if (display == 'true' || recalculate) {
//      markerProperties2(result, item, index);
//      $('#iconSavePDF').show();
//    } else {
//      markerProperties(result, item, index);
//      // map.gmap({'zoom': 2}).bind('init', function(evt, map) {
//      // $(this).gmap('set', 'MarkerClusterer', new MarkerClusterer(map,
//      // $(this).gmap('get', 'markers')));
//      // });
//    }
  });
}

//function initMarkers2() {
//  console.log('ANTES.......');
//  console.log('DESPUES DE LIMPIAR');
//  map.gmap('microdata', 'https://schema.org/TouristAttraction', function(
//          result, item, index) {
//    console.log('secondeEEEEEE');
//    markerProperties2(result, item, index);
//  });
//}

function displayDirection() {
  if ($('#directionLocation').is(':visible')) {
    $('#directionLocation').hide();
    $('.row').show();
  } else {
    $('#directionLocation').show();
    $('.row').hide();
  }
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
  $(".markersTableContainer").one('click', 'tr', function() {
    num_row = $(this).attr('id').substring(6);
    $('#selectMarkersTable' + num_row).trigger('click');
  });
  if (!hideLocationTable && hideDirectionTable) {
    $('#arrowShowLocationsTable').show();
    $('.markersTable').animate({
      marginLeft: '-100%',
    }, '1000');
    $('.markersTableContainer').animate({
      marginLeft: '-100%',
    }, '1000');
    hideLocationTable = true;
    hideDirectionTable = true;
    animateDirectionTable();
  } else {
    animateDirectionTable();
  }
}

function showLocationTableIfNavigatorChange() {
	
	console.log("HIDE LOCATION TABLE IS "+hideLocationTable);
	
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
    $('.row').show();
    $('.tableNavigator').show();
    //hideLocationTable = false;
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
    $('.row').show();
    hideLocationTable = false;
  } else {
    $('.arrowHideLocation').hide();
    $('.markersTable').animate({
      marginLeft: '-=100%',
    }, '500');
    var wait = setInterval(function() {
      if (!$('.markersTable').is(":animated")) {
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

function displayLocationsTable() {

  if (!hideDirectionTable && !hideLocationTable) {
    hideDirectionTable = true;
    hideLocationTable = true;
    $('.markersTable').animate({
      marginLeft: '-100%',
    }, '1000');
    $('.markersTableContainer').animate({
      marginLeft: '-100%',
    }, '1000');
    setTimeout(function() {
      animateLocationTable();
    }, 500);

    $('.arrowHideDirection').hide();
    $('.tableNavigator').hide();
  } else {
    animateLocationTable();
  }
}

function initGeolocation() {
  $('.submitFindPosition').click(
          function() {
            map.gmap('getCurrentPosition', function(position, status) {
              if (status === 'OK') {
                currentLatLng = new google.maps.LatLng(
                        position.coords.latitude, position.coords.longitude);
                map.gmap('get', 'map').panTo(currentLatLng);

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
    console.log("estado... " + status);
    if (status === 'OK') {
      currentLatLng = new google.maps.LatLng(position.coords.latitude,
              position.coords.longitude);
      console.log("MYO POSITION ISSS " + currentLatLng);
      map.gmap('get', 'map').panTo(currentLatLng);
      map.gmap('get', 'map').setZoom(10);

      console.log("YOUR CURRENT LATLOG IS " + currentLatLng);
      
      
      $(".slider_wrapper").show();

      map.gmap('search', {
        'location': currentLatLng
      }, function(results, status) {
        if (status === 'OK') {

          $('#geoCoordInput').val(currentLatLng);
          $('#saveGeoCoordinates').trigger('click');
          // myPositionOptions = {
          // 'zIndex': 1300,
          // 'bounds': false,
          // 'position': currentLatLng,
          // 'draggable': true,
          // 'title': "My Position",
          // 'icon': new google.maps.MarkerImage(
          // '/images/markers/icon-gmap-start.png')
          // };
          //
          // map.gmap('addMarker', myPositionOptions, function(map, marker) {
          //
          // }).click(function() {
          //
          // map.gmap('openInfoWindow', {
          // 'content': "My position"
          // }, this);
          // });

          // setTimeout(function() {
          // var current_name = $('#geoCoordInput').val();
          // console.log("value is.... " + current_name);
          // $('#saveGeoCoordinates').trigger('click');
          // }, 5000);

        }
      });
    } else {
      alert('Unable to get current position');
    }
  });

}

function resetForm(id, img) {
  $('#' + id).val('');
  $('#' + img).hide();
  wait = setInterval(function() {
    if ($.trim($('#' + id).val('')) != '') {
      $('#submitFindDirection').trigger("click");
      clearInterval(wait);
    }
  }, 0);
}

//function hideTable() {
//console.log("Table is hidden? "+hideLocationTable);
//		$('#arrowHideLocationsTable').hide();
//		$('.tableNavigator').hide();
//		$('.showHideNavigator').hide();
//		hideLocationTable = true;
//
//}

var map = ""
var recalculate = false;
var waypts = [];
var currentLatLng = null;
var currentPosition = false;
var newEndPosition = false;
var hideLocationTable = true;
var hideMenu = false;
var hideDirectionTable = true;
var findDirection = false;
var last_checked = null;
var totalMarkers = 0;
var origin = null;
var destination = null;
var change_color = false;
var temp_name = null;
var temp_lat = null;
var temp_lng = null;
var temp_id = null;
//var temp_rating = null;
//var temp_nrating = null;
var temp_latlng2 = null;
//var temp_id2 = null;
var remove_selection = false;
var insert_selected = true;
var route = [];
var _markerCluster = null;
var user = null;



//var map;
//var marker;
//var lat = 41.616302;
//var lng = -8.432792;
//var ico = new google.maps.MarkerImage("https://res.cloudinary.com/durky4ga0/image/upload/v1438339086/marker_plnomd.png");
//var markerList = $('.markers-list');

/*Draw Overlay*/
//var overlay = new google.maps.OverlayView();
//overlay.draw = function() {};

// Goole Maps SKIN
var styleGrey = [{featureType: "administrative",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: 'landscape',elementType: 'all',stylers: [{ hue: '#FFFFFF' },{ saturation: -100 },{ lightness: 100 },{ visibility: 'on' }]},{featureType: "poi",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "road",elementType: "all",stylers: [{ visibility: "on" },{ lightness: -30 }]},{featureType: "transit",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "water",elementType: "all",stylers: [{ saturation: -100 },{ lightness: -100 }]},{featureType: "all",elementType: "all",stylers: [{ saturation: -100 },{ lightness: 91 }]}];
// Ref: https://snazzymaps.com/
var routeXL = [{"featureType":"administrative","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":20}]},{"featureType":"road","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":40}]},{"featureType":"water","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-10},{"lightness":30}]},{"featureType":"landscape.man_made","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":10}]},{"featureType":"landscape.natural","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":60}]},{"featureType":"poi","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]},{"featureType":"transit","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]}]

var styledMapOptions = { name: "MYTHEME" }
var greyMapType = new google.maps.StyledMapType( styleGrey, styledMapOptions );

var styledSnazzymaps = { name: "RouteXL" }
var snazzymapsMapType = new google.maps.StyledMapType( routeXL, styledSnazzymaps );

// function googleTranslateElementInit() {
// new google.translate.TranslateElement({
// pageLanguage : 'auto',
// includedLanguages : 'de,en,es,ru,sv,fr,pl,ca,nl,tr',
// layout : google.translate.TranslateElement.InlineLayout.SIMPLE,
// multilanguagePage : true,
// gaTrack : true,
// gaId : 'UA-51302397-1'
// }, 'google_translate_element');
// }

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

  $("#tourView input:checkbox").on('click', function() {
    var $box = $(this);
    if ($box.is(":checked")) {
      var group = "input:checkbox[name='" + $box.attr("name") + "']";
      $(group).prop("checked", false);
      $box.prop("checked", true);
      checked = $box;
    } else {
      $box.prop("checked", false);
    }
  });
  $("#polygonCoordInput").on('keypress', function(e){
	  if(e.which == 13) {
	  $('#savePolygonCoordinates').trigger('click');
	  }
 })
  
  
  

  // $(':checkbox').addClass("checkbox");
}
google.maps.event.addDomListener(window, 'load', initialize);

$(function() {
	

//	if($('#markersTableContainer').is(':visible')){
//		$('#arrowHideLocationsTable').show();
//		$('.tableNavigator').show();
//		$('.showHideNavigator').show();
//	} else {
//		$('#arrowHideLocationsTable').hide();
//		$('.tableNavigator').hide();
//		$('.showHideNavigator').hide();
//	}
	
	
  var po = document.createElement('script');
  po.type = 'text/javascript';
  po.async = true;
  po.src = 'https://apis.google.com/js/plusone.js';
  var s = document.getElementsByTagName('script')[0];
  s.parentNode.insertBefore(po, s);
  // google async end
  map = $('#map_canvas');

  map.gmap({
	'fullscreenControl': false,
    'panControl': false,
    'mapTypeId': google.maps.MapTypeId.ROADMAP,
     'zoom': 3,
     'minZoom': 3,
     //'maxZoom': 18,
    //'center': new google.maps.LatLng(48.194240, 16.374256),
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
    'keyboardShortcuts': true,
    'disableDoubleClickZoom': false,
    'disableDefaultUI': true
  });
  var getmap = map.gmap("get", "map");
	// set overlay map
	//overlay.setMap(getmap);

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
	  
	  var polyOption2 = {
			  zIndex: 1300,
			   animation: google.maps.Animation.DROP,
			    icon: new google.maps.MarkerImage('/images/markers/icon-gmap-start.png'),
			    fillColor: 'rgba(255,255,255,.87)',
			    fillOpacity: 0.4,
			    strokeWeight: 1,
			    clickable: true,
			    editable: true,
			    draggable: true,
		        flat: true,
		        raiseOnDrag: true
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
	      markerOptions: polyOption2
	    });
	    
  google.maps.event.addListenerOnce(getmap, 'tilesloaded', function(){

	// call zoom control
	ZoomControl(getmap);
	getmap.mapTypes.set("MYTHEME", greyMapType);
	getmap.mapTypes.set("RouteXL", snazzymapsMapType);
//	// call add marker
//	addMarker($("#map_canvas").gmap("get", "map"));

	// call gmap skin

  
	drawingManager.setMap(getmap);
	
	
	
	
	

		  // Clear the current selection when the drawing mode is changed, or when the
		  // map is clicked.
		  google.maps.event.addListener(drawingManager, 'drawingmode_changed', clearSelection);
		  google.maps.event.addListener(map, 'click', clearSelection);
		  google.maps.event.addDomListener(document.getElementById('delete-button'), 'click', deleteSelectedShape);
		  google.maps.event.addDomListener(document.getElementById('delete-all-button'), 'click', deleteAllShape);

		  buildColorPalette();
	   
	    
	    google.maps.event.addListener(drawingManager, 'overlaycomplete', function(e) {
	        all_overlays.push(e);
	        
	      //	console.log("the coordinatesss are: "+e.overlay.getPath().getArray());

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
	              console.log("the rect coordinates first are: "+bounds);
	              $('#polygonCoordInput').val('');
	             // var coord = coordinates.toString().slice(1,-1);
	              var NE = bounds.getNorthEast();
	              var SW = bounds.getSouthWest();
	              var NW = new google.maps.LatLng(NE.lat(),SW.lng());
	              var SE = new google.maps.LatLng(SW.lat(),NE.lng());
	              $('#polygonCoordInput').val(NE+","+SE+","+SW+","+NW);
	              $('#savePolygonCoordinates').trigger('click');
	              drawingManager.setDrawingMode(null);
	              //hideTable();
	          }
	          if (e.type == google.maps.drawing.OverlayType.POLYGON) {
	              	console.log("the poly coordinates second are: "+newShape.getPath().getArray());

	                  $('#polygonCoordInput').val('');
	                  $('#polygonCoordInput').val(newShape.getPath().getArray());
	                  $('#savePolygonCoordinates').trigger('click');
	          }

	        } else {
	            $('#polygonCoordInput').val('');
	            $('#polygonCoordInput').val(e.overlay.getPosition());
	            $(".slider_wrapper").show();
	            $('#savePolygonCoordinates').trigger('click');
	            drawingManager.setDrawingMode(null);
	        }


	      });
	    


	    


	  $(window).resize(function() {
	    map.gmap('refresh');
	  });

	  initIds();
	  initMarkers();
	  initGeolocation();
	  initDirection();
	  //localStorage['displayOverlays'] = 'false';

	//  $('#createTour, #initDisplayTours').submit(function() {
//	    if ($.trim($("#nameRoute").val()) === "") {
//	      alert('Please add a Tour name');
//	      return false;
//	    } else if ($.trim($("#coordInput").val()) === "") {
//	      alert('Please add at least two markers in your Tour');
//	      return false;
//	    } else {
//	      localStorage['displayOverlays'] = 'true';
//	    }
	//  });
	//  $(
//	          '#initDisplayTours,#filterTours,.showTours,.hideRoutes input, #hideSuggested input')
//	          .click(function() {
//	            localStorage['displayOverlays'] = 'true'; // save in cache
//	            $('#iconSavePDF').show();
//	          });
	
	
	
	
	
	
	
	
	

	
  });
  
  
  
  
//  google.maps.event.addListener(drawingManager, 'circlecomplete', function (circle) {
//  google.maps.event.addListener(polygon, "radius_changed", function(event){
//      var coordinates = circle.getCenter() + ", " + circle.getRadius();
//      console.log("circle coord are: "+coordinates);
//      $('#polygonCoordInput').val('');
//      $('#polygonCoordInput').val(coordinates);
//      $('#savePolygonCoordinates').trigger('click');
//      drawingManager.setDrawingMode(null);
//      hideTable();
//  });
//  
//  deleteAllShape();
//  var coordinates = circle.getCenter() + ", " + circle.getRadius();
//  console.log("circle coord are: "+coordinates);
//  $('#polygonCoordInput').val('');
//  $('#polygonCoordInput').val(coordinates);
//  //$('#savePolygonCoordinates').trigger('click');
//  drawingManager.setDrawingMode(null);
//});

google.maps.event.addListener(drawingManager, 'polygoncomplete', function (polygon) {
  google.maps.event.addListener(polygon, "mouseup", function(e){

	  	console.log("the poly coordinates second are: "+polygon.getPath().getArray());
		
	      $('#polygonCoordInput').val('');
	      $('#polygonCoordInput').val(polygon.getPath().getArray());
	      $('#savePolygonCoordinates').trigger('click');
  });

});

google.maps.event.addListener(drawingManager, 'markercomplete', function (marker) {
  google.maps.event.addListener(marker, "mouseup", function(e){
	  		polygon.setMap(null)
	        $('#polygonCoordInput').val('');
	        $('#polygonCoordInput').val(marker.getPosition());
	        $(".slider_wrapper").show();
	        $('#savePolygonCoordinates').trigger('click');
	        drawingManager.setDrawingMode(null);
	        //hideTable();
  });

});

//google.maps.event.addListener(drawingManager, 'polylinecomplete', function (polyline) {
//  var coordinates = (polyline.getPath().getArray());
//  console.log("polyline coord are: "+coordinates);
//  $('#polygonCoordInput').val('');
//  $('#polygonCoordInput').val(coordinates);
//  $('#savePolygonCoordinates').trigger('click');
//  drawingManager.setDrawingMode(null);
//});

google.maps.event.addListener(drawingManager, 'rectanglecomplete', function (rectangle) {
  google.maps.event.addListener(rectangle, "bounds_changed", function(e){
     

	        var bounds = rectangle.getBounds();
	       	console.log("the rect coordinates second are: "+bounds);
	        $('#polygonCoordInput').val('');
	        var NE = bounds.getNorthEast();
	        var SW = bounds.getSouthWest();
	        var NW = new google.maps.LatLng(NE.lat(),SW.lng());
	        var SE = new google.maps.LatLng(SW.lat(),NE.lng());
	        $('#polygonCoordInput').val(NE+","+SE+","+SW+","+NW);
	        $('#savePolygonCoordinates').trigger('click');
	        drawingManager.setDrawingMode(null);
	        //hideTable();
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
	}

	function deleteAllShape() {
	  for (var i = 0; i < all_overlays.length; i++) {
	    all_overlays[i].overlay.setMap(null);
	  }
	  all_overlays = [];
      $('#polygonCoordInput').val("");
      $('#deletePolygonCoordinates').trigger('click');
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

  
  
  
  
  
  
  
  
  
  
  
  
  
	

	
	
//	function initialize () {
//		var mapCanvas = document.getElementById('map');
//		var mapOptions = {
//			zoom: 13,
//			center: {
//				lat: lat, 
//  			lng: lng
//			},
//			mapTypeControl: false,
//			zoomControl: false,
//			panControl: false,
//	        scaleControl: false,
//	        streetViewControl: false,
//	        scrollwheel: false
//		}
//
//		map = new google.maps.Map( mapCanvas, mapOptions );
//
//
//	}

//	// Add marker and listeners
//	function addMarker ( map ) {
//		marker = new google.maps.Marker({
//          map: map,
//          draggable: false,
//          icon: ico,
//          position: new google.maps.LatLng( lat, lng )
//      });
//
//      mouseOverHandler = function () {
//      	showMarker(marker);
//      }
//      mouseClickHandler = function () {
//      	clickMarker(lat, lng);
//      }
//
//      google.maps.event.addListener( marker, 'click', mouseClickHandler );
//      google.maps.event.addListener( marker, 'mouseover', mouseOverHandler );
//	}

//	// Marker over animation
//	function showMarker ( marker ) {
//		var name = 'Blleb';
//      var projection = overlay.getProjection();
//      var pixel = projection.fromLatLngToContainerPixel( marker.getPosition() );
//      var x = pixel.x-58;
//      var y = pixel.y-84;
//
//      markerList.find('li').css({"left": x, "top": y});
//     	markerList.find('p').html( name );
//      markerList.fadeIn();
//	}

//	// Marker click function
//	function clickMarker ( lat, lng ) {
//		var url = 'https://maps.google.com/maps?q='+lat+','+lng+'&z=18';
//		window.open(url, '_blank');
//	}

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

	// Map set center
	$( '#center' ).on( 'click', function () {
		getmap.setZoom( 13 );
		getmap.setCenter(new google.maps.LatLng( lat, lng ) );
		getmap.setMapTypeId( google.maps.MapTypeId.ROADMAP );
		 $( '#mapTypeId' ).val( "1" ).trigger('click');
	});

	// Change Map TypeId
	function TypeIdChange ( option ) {
		switch (option) {
          case "1":
        	  getmap.setMapTypeId( google.maps.MapTypeId.ROADMAP );
              break;
          case "2":
        	  getmap.setMapTypeId( google.maps.MapTypeId.TERRAIN );
              break;
           case "3":
        	   getmap.setMapTypeId( google.maps.MapTypeId.SATELLITE );
              break;
          case "4":
        	  getmap.setMapTypeId( "MYTHEME" );
              break;
          case "5":
        	  getmap.setMapTypeId( "RouteXL" );
              break;
          default:
        	  getmap.setMapTypeId( google.maps.MapTypeId.ROADMAP );
      }
	}

	$( '#mapTypeId' ).change( function () {
		var self = $(this);
		TypeIdChange( self.val() );
	});

	google.maps.event.addDomListener( window, 'load', initialize );
  
  
});
