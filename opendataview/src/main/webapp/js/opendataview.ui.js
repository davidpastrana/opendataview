//function getCurrentDateTimeShort() {
//  var currentDateTime = new moment();
//  currentDateTime = currentDateTime.format("DDMMYY_HH.mm");
//  return currentDateTime.toString();
//}

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

//function displayMoreInfo(marker) {
//  var idMarker = $(marker);
//  if (idMarker.attr('id') == null) {
//    $(".markersTableContainer").one('click', 'tr', function() {
//
//      num_row = $(this).attr('id').substring(6);
//      if (($('#moreInfo' + num_row)).is(':visible')) {
//        $('#moreInfo' + num_row).animate({
//          opacity: 'hide'
//        }, 100);
//      } else {
//        if (($('#streetviewSize' + num_row)).is(':visible')) {
//          $('#streetview' + num_row).hide();
//          $('#streetviewSize' + num_row).hide();
//        }
//        $('#moreInfo' + num_row).animate({
//          opacity: 'show'
//        }, 800);
//        $('#moreInfo' + num_row).trigger('click');
//      }
//    });
//  }
//  if (idMarker.attr('id') != null) {
//    num_row = idMarker.attr('id').substring(18);
//    if (($('#moreInfo' + num_row)).is(':visible')) {
//      $('#moreInfo' + num_row).animate({
//        opacity: 'hide'
//      }, 100);
//    } else {
//      if (($('#streetviewSize' + num_row)).is(':visible')) {
//        $('#streetview' + num_row).hide();
//        $('#streetviewSize' + num_row).hide();
//      }
//      $('#moreInfo' + num_row).animate({
//        opacity: 'show'
//      }, 800);
//      $('#moreInfo' + num_row).trigger('click');
//    }
//  }
//}
//
//function displayMainMenu() {
//  if (hideMenu) {
//    $('#arrowHideMainMenu').show();
//    $('#arrowShowMainMenu').hide();
//    $('#mainMenu').animate({
//      marginRight: '-=100%',
//    }, '1000');
//    hideMenu = false;
//  } else {
//    $('#arrowHideMainMenu').hide();
//    $('#arrowShowMainMenu').show();
//    $('#arrowHideLocationsTable').hide();
//    $('#mainMenu').animate({
//      marginRight: '+=100%',
//    }, '1000');
//    hideMenu = true;
//  }
//}

//function initIds() {
//  id = 0;
//  $('.selectMarkersTable').each(function() {
//    $(this).attr('id', 'selectMarkersTable' + (id++));
//  });
//  totalMarkers = 0;
//  $('.moreInfo').each(function() {
//    $(this).attr('id', 'moreInfo' + (totalMarkers++));
//  });
//  id = 0;
//  $('.streetviewSize').each(function() {
//    $(this).attr('id', 'streetviewSize' + (id++));
//  });
//  id = 0;
//  $('.streetview').each(function() {
//    $(this).attr('id', 'streetview' + (id++));
//  });
//  id = 0;
//  $('.row').each(function() {
//    $(this).attr('id', 'marker' + (id++));
//  });
//}

//function displayWaypoints(origin, destination) {
//
//  var selectedMode = $("#mode").val();
//  var displayDirectionsOptions = {
//    'origin': origin,
//    'waypoints': waypts,
//    'destination': destination,
//    'optimizeWaypoints': true,
//    'provideRouteAlternatives': false,
//    'avoidHighways': false,
//    'avoidTolls': false,
//    'unitSystem': google.maps.UnitSystem.METRIC,
//    'travelMode': google.maps.DirectionsTravelMode[selectedMode]
//  };
//  var panel = {
//    'panel': document.getElementById('directions')
//  };
//  map
//          .gmap(
//                  'displayDirections',
//                  displayDirectionsOptions,
//                  panel,
//                  function(success, response) {
//                    console.log("Status is " + success + " with response "
//                            + response);
//                    if (success) {
//                      if (response == 'ZERO_RESULTS') {
//                        alert('Error, number of waypoints exceeded (Maximum 8 waypoints per route).');
//                        $('#directions').text(' ');
//                        findDirection = false;
//                      } else {
//                        findDirection = true;
//                      }
//                    } else {
//                      findDirection = false;
//                    }
//                  });
//
//  var directionsDisplay = new google.maps.DirectionsRenderer();
//  var directions = new google.maps.DirectionsService();
//  directions.route(displayDirectionsOptions, function(result, status) {
//    if (status === google.maps.DirectionsStatus.OK) {
//
//      directionsDisplay.setDirections(result);
//
//      // calculate total distance and duration
//      var distance = 0;
//      var time = 0;
//      var theRoute = result.routes[0];
//      for (var i = 0; i < theRoute.legs.length; i++) {
//        var theLeg = theRoute.legs[i];
//        distance += theLeg.distance.value;
//        time += theLeg.duration.value;
//      }
//      $("#distance").html(
//              "Total distance: " + Math.round(distance / 100) / 10 + " km, ");
//      $("#duration").html(
//              "total duration: " + Math.round(time / 60) + " minutes");
//    }
//  });
//}
//
//function isScrolledIntoView(elem) {
//  var docViewTop = $(window).scrollTop(), docViewBottom = docViewTop
//          + $(window).height(), elemTop = $(elem).offset().top, elemBottom = elemTop
//          + $(elem).height();
//
//  return ((elemTop + ((elemBottom - elemTop) / 2)) >= docViewTop && ((elemTop + ((elemBottom - elemTop) / 2)) <= docViewBottom));
//}
//
//function checkListSelection(element) {
//
//  if (element.offset().top < 52 || element.offset().top > 630) {
//    $('.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
//            .scrollTop(0);
//
//    var offset = element.offset().top - 70,
//
//    wait = setInterval(
//            function() {
//
//              if (!$(
//                      '.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
//                      .is(":animated")) {
//                $(
//                        '.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
//                        .scrollTop(offset);
//                clearInterval(wait);
//              }
//            }, 0);
//  }
//  return true;
//}
//
//function initDirection() {
//  $('#submitFindDirection')
//          .click(
//                  function() {
//
//                    if (recalculate) {
//                      if ($('#from').val() == '' && $('#to').val() == '') {
//                        displayWaypoints(origin, destination);
//                        //initMarkers2();
//                      }
//
//                      if (currentPosition && $('#from').val() == '') {
//                        origin = waypts[0].location;
//                        waypts.shift();
//                        totalMarkers--;
//                        displayWaypoints(origin, destination);
//                        currentPosition = false;
//                        //initMarkers2();
//                      }
//
//                      if (newEndPosition && $('#to').val() == '') {
//                        destination = waypts[waypts.length - 1].location;
//                        waypts.pop();
//                        totalMarkers--;
//                        displayWaypoints(origin, destination);
//                        newEndPosition = false;
//                        //initMarkers2();
//                      }
//
//                      if ($('#from').val() != '') {
//                        var geocoder = new google.maps.Geocoder();
//                        geocoder
//                                .geocode(
//                                        {
//                                          'address': $('#from').val()
//                                        },
//                                        function(results, status) {
//                                          if (status == google.maps.GeocoderStatus.OK) {
//                                            lat = results[0].geometry.location
//                                                    .lat();
//                                            lng = results[0].geometry.location
//                                                    .lng();
//
//                                            if (!currentPosition) {
//                                              totalMarkers++;
//                                              waypts.splice(0, 0, {
//                                                location: origin,
//                                                stopover: true
//                                              });
//                                            }
//
//                                            origin = new google.maps.LatLng(
//                                                    lat, lng);
//                                            displayWaypoints(origin,
//                                                    destination);
//                                            currentPosition = true;
//                                            //initMarkers2();
//                                          } else {
//                                            alert("Geocode was not successful for the following reason: "
//                                                    + status);
//                                          }
//                                        });
//                      } else {
//                        //console.log("no value");
//                      }
//
//                      if ($('#to').val() != '') {
//
//                        var geocoder = new google.maps.Geocoder();
//                        geocoder
//                                .geocode(
//                                        {
//                                          'address': $('#to').val()
//                                        },
//                                        function(results, status) {
//                                          if (status == google.maps.GeocoderStatus.OK) {
//                                            lat = results[0].geometry.location
//                                                    .lat();
//                                            lng = results[0].geometry.location
//                                                    .lng();
//
//                                            if (!newEndPosition) {
//                                              totalMarkers++;
//                                              waypts.push({
//                                                location: destination,
//                                                stopover: true
//                                              });
//                                            }
//
//                                            destination = new google.maps.LatLng(
//                                                    lat, lng);
//                                            displayWaypoints(origin,
//                                                    destination);
//                                            newEndPosition = true;
//                                            //initMarkers2();
//                                          } else {
//                                            alert("Geocode was not successful for the following reason: "
//                                                    + status);
//                                          }
//                                        });
//                      }
//                      recalculate = true;
//
//                    } else {
//                      var selectedMode = $("#mode").val();
//                      var displayDirectionsOptions = {
//                        'origin': $('#from').val(),
//                        'destination': $('#to').val(),
//                        'provideRouteAlternatives': false,
//                        'avoidHighways': false,
//                        'avoidTolls': false,
//                        'unitSystem': google.maps.UnitSystem.METRIC,
//                        'travelMode': google.maps.DirectionsTravelMode[selectedMode]
//                      };
//                      directionsDisplay = new google.maps.DirectionsRenderer();
//                      var panel = {
//                        'panel': document.getElementById('directions')
//                      };
//                      map
//                              .gmap(
//                                      'displayDirections',
//                                      displayDirectionsOptions,
//                                      panel,
//                                      function(success, response) {
//                                        if (success) {
//                                          if (response == 'ZERO_RESULTS') {
//                                            //alert('Error. Introduce una dirección más exacta.');
//                                            $('#directions').text(' ');
//                                            findDirection = false;
//                                          } else {
//                                            findDirection = true;
//                                          }
//                                        } else {
//                                          findDirection = false;
//                                        }
//                                        $('#directions').show();
//                                      });
//                    }
//                  });
//}

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

//function displayTable() {
//  $('#createTour').show();
//}
//
//function changeToUnselect(index) {
//  $('#unsel' + index).hide();
//}
//function changeToSelected(index) {
//  $('#sel' + index).hide();
//}
//
//function getIdMarker(id, marker) {
//
//  var idMarker = $(marker);
//  if (idMarker.attr('id') != null) {
//    $('#idInput').val(id);
//    $('#editInfoButton').trigger('click');
//  }
//  if (idMarker.attr('id') == null) {
//    $(".markersTableContainer").one('click', 'tr', function() {
//      num_row = $(this).attr('id');
//      id = $('#' + num_row).find(".id_DB").text();
//      $('#idInput').val(id);
//      $('#editInfoButton').trigger('click');
//    });
//  }
//}

//function showMarkerInfo(id) {
//
////	  var idMarker = $(marker);
////	  if (idMarker.attr('id') != null) {
////		  console.log("WERE NOT IN NULL"+idMarker.attr('id'));
////	    $('#idInput2').val(id);
////	    $('#showMarkerInfoBttn').trigger('click');
////	  }
////	  if (idMarker.attr('id') == null) {
////		  console.log("WERE  IN NULL ");
////	    //$(".markersTableContainer").one('click', 'tr', function() {
////	      num_row = $(this).attr('id');
////	      console.log("num row "+num_row);
////	      id = $('#' + num_row).find(".id_DB").text();
////	      console.log("");
////	      $('#idInput2').val(id);
////	      $('#showMarkerInfoBttn').trigger('click');
////	    //});
////	  }
//	}
//
//function markerInfo(name, item, index, id) {
//	
//    $('#idInput2').val(id);
//    $('#showMarkerInfoBttn').trigger('click');
//	  if ($('#sidebar').not('.active')) {
//		  console.log("SHOWING SIDEBAR");
//		    //$('.markersTable2').hide();
//			  //$('.markersTable2').show();
//		      $('#sidebar').addClass('active');
////		      $('#sidebar').addClass('active');
//		      $('.overlay').addClass('active'); // Only for mobilephones
////		      $('.collapse.in').toggleClass('in');
////		      $('a[aria-expanded=true]').attr('aria-expanded', 'false');
////		      //$('.overlay').addClass('active'); //only for mobile phones
////		      $('.collapse.in').toggleClass('in');
////		      $('a[aria-expanded=true]').attr('aria-expanded', 'false');
//		  }
//
//    
////	  var markerDetails = '';
////	  markerDetails += '<div class="markerInfo notranslate">';
//	  var markerDetails = name;
////	  markerDetails += '<i class="fa fa-info-circle fa-2x iconInfoMarker" onclick="displayLocationsTable();displayMoreInfo('
////          + $(item).attr('id')
////          + ')"></i>';
////	  markerDetails += '<i class="fa fa-map fa-2x iconDirectionMarker" onclick="displayLocationsTable();displayDirectionTable()"></i>';
////	  markerDetails += '<i class="fa fa-street-view fa-2x iconStreetViewMarker" onclick="!hideDirectionTable ? displayDirectionTable() : false; hideLocationTable ? displayLocationsTable() : false; displayStreetView('
////          + $(item).attr('id')
////          + ')"></i>';
////	  if (user == active_user) {
////	    markerDetails += '<i class="fa fa-pencil fa-2x iconEditMarker" onclick="getIdMarker('
////            + id
////            + ','
////            + $(item).attr('id')
////            + ')"></i>';
////	  }
////	  markerDetails += '</div>';
//	  
//	  return markerDetails;
//	}
//
//function markerInfo2(name, item, index, id) {
//	  var markerDetails = name;
//	  return markerDetails;
//}
//
//function typeIcon(result) {
//	var marker = result.icon_marker;
//	
//	icon = {
//			url: '/images/markers/svg/'+marker+'.svg',
//			scaledSize: new google.maps.Size(28, 28),
//		    };
////	var csvName = csvName.toLowerCase();	
////	if(csvName.match(/(.*party.*|.*fiesta.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_01.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	} else if(csvName.match(/(.*hospital.*|.*disease.*|.*cure.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_02.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	} else if(csvName.match(/(.*coffee.*|.*break.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_03.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	} else if(csvName.match(/(.*food.*|.*meal.*|.*comida.*|.*hosteleria.*|.*restaura.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_22.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	} else if(csvName.match(/(.*comerc.*|.*shop.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_54.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	}
////	else if(csvName.match(/(.*deporte.*|.*sports.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_12.svg',
////				scaledSize: new google.maps.Size(28, 28),
////				
////	    };
////	}
////	else if(csvName.match(/(.*industri.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_61.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	}
////	else if(csvName.match(/(.*beer.*|.*bars.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_25.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	}
////	else if(csvName.match(/(.*nature.*|.*mountain.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_07.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	}
////	else if(csvName.match(/(.*museums.*|.*monuments.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_62.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	}
////	else if(csvName.match(/(.*open.*|.*data.*|.*share.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_89.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	} else if(csvName.match(/(.*repair.*|.*auto.*)/g)) {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_08.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	} else {
////		icon = {
////				url: '/images/markers/svg/wsd_markers_88.svg',
////				scaledSize: new google.maps.Size(28, 28),
////			    };
////	}
//  return icon;
//}
//


//var getGoogleClusterInlineSvg2 = function (color) {
//    var encoded = window.btoa('<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="40" viewBox="-100 -100 200 200"> <defs><g id="a" transform="rotate(45)"> </g></defs> <g fill="orange" fill-opacity="1"> <circle style="fill:red;fill-opacity:0.5" r="58" /> <!-- <circle style="fill:purple;fill-opacity:0.2" r="54" />--> <circle r="42"/> <use xlink:href="#a"/><g transform="rotate(120)"> <use xlink:href="#a"/></g><g transform="rotate(240)"> <use xlink:href="#a"/> </g></g> </svg>');
//    return ('data:image/svg+xml;base64,' + encoded);
//};


//function markerProperties(result, item, index, zoomin) {
//
//  var name = result.name;
//  var id_marker = result.url;
//  var icon_marker = result.identifier;
//  var lat = result.latitude;
//  var lng = result.longitude;
//  
//  var val = icon_marker.split('-');
//  var svg = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="20" height="20" viewBox="-100 -100 200 200"><defs><g id="a" transform="rotate(45)"></g></defs><g fill="'+val[0]+'"><circle style="fill:'+val[1]+'" r="'+val[3]+'" /><circle r="'+val[2]+'"/><use xlink:href="#a"/><g transform="rotate(120)"><use xlink:href="#a"/></g><g transform="rotate(240)"><use xlink:href="#a"/></g></g></svg>';
//
//  var latlng = new google.maps.LatLng(lat, lng);
//  
////  console.log("INDEX IS "+index);
//  if(index == 0 || zoomin == "true") {
//	  map.gmap('get', 'map').panTo(latlng);
//	  map.gmap('get','map').setZoom(parseInt(getSearchParams("zoom")));
//	 
//  }
////	icon = {
////			url: '/images/markers/svg/wsd_markers_custom1.svg',
////			scaledSize: new google.maps.Size(40, 40),
////		    };
//  
//  
//    var icon2 = {
//           // url: ('data:image/svg+xml;base64,' + window.btoa('<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="40" viewBox="-100 -100 200 200"> <defs><g id="a" transform="rotate(45)"> </g></defs> <g fill="orange" fill-opacity="1"> <circle style="fill:red;fill-opacity:0.5" r="58" /> <!-- <circle style="fill:purple;fill-opacity:0.2" r="54" />--> <circle r="42"/> <use xlink:href="#a"/><g transform="rotate(120)"> <use xlink:href="#a"/></g><g transform="rotate(240)"> <use xlink:href="#a"/> </g></g> </svg>')), //blue
//            
////            path: "M-20,0a20,20 0 1,0 40,0a20,20 0 1,0 -40,0",
////            fillColor: '#FF0000',
////            fillOpacity: .6,
//
//            url: 'data:image/svg+xml;base64,' + window.btoa(svg), //blue
//        }
////  if (csvName == "myPosition") {
////    markerOptions1 = {
////      'zIndex': 1300,
////      'bounds': false,
////      'position': latlng,
////      'draggable': true,
////      'title': "My Position",
////      'icon': icon
////    };
////
////  } else {
//    
//    var markerOptions1 = {
//      'id': index,
//      'position': latlng,
//      //'markerDetails': markerInfo(name, item, index, id),
//      'icon': icon2,
//      'optimized': false,
//      'bounds': false
//    };
//  }

//  map.gmap('addMarker', markerOptions1, function(map, marker) {
//
//            $(item).click(function(event) {
//              //$(marker).triggerEvent('click');
////              if ($(event.target).is('.moreInfo')) {
////                $(item).unbind('click');
////              }
//              if ($(event.target).hasClass('streetview2')) {
//                $(item).unbind('click');
//              }
//            });
//          })
////          .drag(function(marker) {
////            $('#polygonCoordInput').val(marker.latLng);
////          })
//                                .mouseover(function() {map.gmap('openInfoWindow', {
//                                    'content': markerInfo2(name, item, index, id_marker)
//                                }, this);
//                                
////                                var sv = new google.maps.StreetViewService();
////                                sv.getPanoramaByLocation(
////                                                this.position,
////                                                800,
////                                                function(result, status) {
////                                                  if (status == google.maps.StreetViewStatus.OK) {
////                                                    map.gmap('displayStreetView',
////                                                            'streetview2', {
////                                                              'position': result.location.latLng,
////                                                              'pov': {
////                                                                'heading': 2,
////                                                                'pitch': 1,
////                                                                'zoom': 0
////                                                              }
////                                                            });
////                                                    state = false;
////                                                  } else {
////                                                    $('#streetview2')
////                                                            .html(
////                                                                    "<div class='errorMessage'>Sorry there is no Street View service within 800 meters from the location.</div>");
////                                                  }
////                                                });
//                              
//                                
//                                
//                                })
//                        //.mouseout(function() {document.getElementById("rank_" + data.id).style.backgroundColor="#333333";})
//          .click(
//                  function() {
//
//
//                    
//
//
////                    $(item).addClass('markerClicked');
////                    
////                    
////
////                    if (last_checked != "") {
////                      $(last_checked).removeClass('markerClicked');
////                    }
////
////                    if (!isScrolledIntoView($(item))) {
////                      checkListSelection($(item));
////                    }
////
////                    last_checked = $(item);
//
//                	  console.log("we open infowindow from google maps with index"+index);
//                      map.gmap('openInfoWindow', {
//                          'content': markerInfo(name, item, index, id_marker)
//                        }, this);
//
//
//                      callStreetView(this.position);
//
//
//                    
////	                    map.gmap('search', {
////	                        'location': this.position
////	                      }, function(results, status) {
////	                        if (status === 'OK') {
////	                          $('#to').val(results[0].formatted_address);
////	                        }
////	                      });
//                  });
//  
//}
    var mly;
    var key_image;
function callStreetView(lat,lng) {
    //var url = 'https://a.mapillary.com/v3/images?client_id=bjRlSHBOc1c0enFCWmZodWxReEx3QTpkZDE0MDMxZTdlM2I1ZmYw&closeto='+lng+','+lat+'&radius=300&per_page=1';
	console.log("mly is "+mly);
	
	if(mly === undefined) {
		mly = new Mapillary.Viewer('mly2','QjI1NnU0aG5FZFZISE56U3R5aWN4ZzplNDVjNDc0YmYwYjJmYjQ0',null,{ cover: false });
	}
    mly.moveCloseTo(lat, lng);
}
//    $.getJSON(url, function(data) {
//    	  console.log("JSON OBJ is "+ JSON.stringify(data));
//    	key_image=JSON.stringify(data.features[0].properties.key);
//    	  console.log("data keyyyy is "+ JSON.stringify(data.features[0].properties.key));
//    	    console.log("key is::"+key_image);
//            window.addEventListener("resize", function() { mly.resize(); });
//    });

    	 // your code
    	

//    mly1.moveToKey('6ZcXjb82YuNEtPNA3fqBzA').then(
//            function(node) { console.log('mly1 loaded key:', node.key); },
//            function(error) { console.error('mly1 error:', error); });
//
//    window.addEventListener('resize', function() { mly.resize(); });
	
//	sv = new google.maps.StreetViewService();
//    sv.getPanoramaByLocation(
//    		new google.maps.LatLng(lat,lng),
//                    100,
//                    function(result, status) {
//                      if (status == google.maps.StreetViewStatus.OK) {
//                        map.gmap('displayStreetView',
//                                'streetview2', {
//                                  'position': result.location.latLng,
//                                  'pov': {
//                                    'heading': 34,
//                                    'pitch': 4,
//                                    'zoom': 1
//                                  },
//                                  'panControl': false,
//                                  //'addressControl':false,
//                                  'linksControl': true,
//                                  'enableCloseButton': false,
//                                  'zoomControl': false,
//                                  'motionTrackingControl': true
//                                });
//                        state = false;
//                      } else {
//                        $('#streetview2')
//                                .html(
//                                        "<div class='errorMessage'>No Streetview available within 100m.</div>");
//                      }
//                    });


//function initMyPosition() {
//
//  map.gmap('microdata', 'https://schema.org/Property', function(result,
//          item, index) {
//	  
//	  if (index == 0) {
//      var lat = result.alternateName;
//      var lng = result.additionalType;
//      var latlng = new google.maps.LatLng(lat, lng);
//		icon = {
//				
//				//<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="-100 -100 200 200"><defs><g id="a" transform="rotate(45)"> </g></defs><g fill="'+color+'"><circle style="fill:'+color+';fill-opacity:0.5" r="48" /><circle style="fill:'+color+';fill-opacity:0.2" r="54" /><circle r="42"/><use xlink:href="#a"/><g transform="rotate(120)"><use xlink:href="#a"/></g><g transform="rotate(240)"><use xlink:href="#a"/></g></g></svg>
//				
//				
//	            path: "M-20,0a20,20 0 1,0 40,0a20,20 0 1,0 -40,0",
//	            fillColor: '#FF6F00',
//	            fillOpacity: 0.9,
//	            strokeOpacity: .3,
//	            anchor: new google.maps.Point(0,0),
//	            strokeWeight: 15,
//	            //strokeColor:red,
//	            scale: 0.3,
//	            strokeColor: '#f39c12',
//	            //strokeWidth: 5,
////				url: '/images/markers/svg/wsd_markers_custom1.svg',
////				scaledSize: new google.maps.Size(40, 40),
//			    };
//      markerOptions1 = {
//        'zIndex': 1300,
//        'bounds': false,
//        'position': latlng,
//        'draggable': true,
//        'title': "My Position",
//        'icon': icon
//      };
//	  map.gmap('get', 'map').panTo(latlng);
//	  //console.log("ZOOM TO ADD "+parseInt(getSearchParams("zoom")));
//	  map.gmap('get','map').setZoom(parseInt(getSearchParams("zoom")));
//	  
//      map.gmap('addMarker', markerOptions1, function(map, marker) {
//
//        $(item).click(function(event) {
//          $(marker).triggerEvent('click');
//          //TODO
//
//
//          return false;
//        });
//
//      }).drag(
//
//      function(marker) {
//
//        $('#polygonCoordInput').val(marker.latLng);
//
//      }).dragend(
//
//      function(marker) {
//
//        $('#polygonCoordInput').val(marker.latLng);
//        $('#savePolygonCoordinates').trigger('click');
//
//      }).click(function() {
//
//        map.gmap('openInfoWindow', {
//          'content': "Drag me Sir!<br/>Slider is in the upper left corner."
//        }, this);
//      });
//
//    }
//
//  });
//}
//var getGoogleClusterInlineSvg = function (color) {
//    var encoded = window.btoa('<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="-100 -100 200 200"><defs><g id="a" transform="rotate(45)"> </g></defs><g fill="'+color+'"><circle style="fill:'+color+';fill-opacity:0.5" r="48" /><circle style="fill:'+color+';fill-opacity:0.2" r="54" /><circle r="42"/><use xlink:href="#a"/><g transform="rotate(120)"><use xlink:href="#a"/></g><g transform="rotate(240)"><use xlink:href="#a"/></g></g></svg>');
//
//    return ('data:image/svg+xml;base64,' + encoded);
//};
//var clusterStyles = [
//    {
//        width: 40,
//        height: 40,
//        url: getGoogleClusterInlineSvg('#FF7F50'), //orange
//        textColor: 'white',
//        textSize: 12
//    },
//    {
//        width: 60,
//        height: 60,
//        url: getGoogleClusterInlineSvg('#6495ED'), //blue
//        textColor: 'white',
//        textSize: 12
//    },
//    {
//        width: 80,
//        height: 80,
//        url: getGoogleClusterInlineSvg('#CC4646'), //red
//        textColor: 'white',
//        textSize: 12
//    },
//    {
//        width: 100,
//        height: 100,
//        url: getGoogleClusterInlineSvg('#8A2BE2'), //violet
//        textColor: 'white',
//        textSize: 14
//    },
//    {
//        width: 120,
//        height: 120,
//        url: getGoogleClusterInlineSvg('#800000'), //red
//        textColor: 'white',
//        textSize: 16
//    }
//	];
//var mcOptions = {
//	    gridSize: 50,
//	    styles: clusterStyles,
//	    maxZoom: 15
//	};

//function initMarkers(zoomin) {
//
//  map.gmap('clear', 'markers');
//  map.gmap('microdata', 'https://schema.org/GeoCoordinates', function(
//          result, item, index) {
//    markerProperties(result, item, index, zoomin);
//     map.gmap({'zoom': 2}).bind('init', function(evt, map) {
//    	 
//    	  var list_size = result.subjectOf;
////    	 if(list_size > 500) {
////     $(this).gmap('set', 'MarkerClusterer', new MarkerClusterer(map,
////     $(this).gmap('get', 'markers'),mcOptions));
////     }
//     });
//  });
//}

//function animateDirectionTable() {
//  if (hideDirectionTable) {
//    if (hideMenu) {
//      diplayMainMenu();
//    }
//    $('#directionLocation').hide();
//    $('.arrowHideLocation').hide();
//    $('.markersTable').animate({
//      marginLeft: '0px',
//    }, '1000');
//    $('#arrowShowLocationsTable').hide();
//    $('.markersTableContainer').hide();
//    $('.tableNavigator').show();
//    hideLocationTable = false;
//    displayDirection();
//    hideDirectionTable = false;
//    var wait = setInterval(function() {
//      if (!$('.markersTable').is(":animated")) {
//        $('.arrowHideDirection').show();
//        clearInterval(wait);
//      }
//    }, 0);
//  } else {
//    $('.markersTable').css({
//      marginLeft: '-100%',
//    });
//    $('.markersTable > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)')
//            .width(318).css("overflow-y", "auto");
//    $('.markersTableContainer').show();
//    $('.tableNavigator').hide();
//    hideLocationTable = true;
//    hideDirectionTable = true;
//    $('.arrowHideLocation').hide();
//    $('.arrowHideDirection').hide();
//  }
//}
//
//function displayDirectionTable() {
//	
//	  if ($('#directionLocation').is(':visible')) {
//		    $('#directionLocation').hide();
//		    $('.markersTableContainer').show();
//		  } else {
//		    $('#directionLocation').show();
//		    $('.markersTableContainer').hide();
//		  }
//}
//
//function showLocationTableIfNavigatorChange() {
//	
//  if (hideLocationTable == false) {
//    $('#directionLocation').hide();
//    $('.markersTableContainer').css({
//      marginLeft: '0px'
//    });
//    $('.markersTableContainer').show();
//    $('.markersTable').css({
//      marginLeft: '0px',
//    });
//    var wait = setInterval(function() {
//      if (!$('.markersTable').is(":animated")) {
//        $('.arrowHideLocation').show();
//        $('#arrowHideLocationsTable').show();
//        clearInterval(wait);
//      }
//    }, 0);
//    $('.markersTableContainer').show();
//    $('.tableNavigator').show();
//  }
//}
//
//function animateLocationTable() {
//  if (hideLocationTable) {
//    if (hideMenu) {
//      diplayMainMenu();
//    }
//
//    $('#directionLocation').hide();
//    $('.markersTableContainer').css({
//      marginLeft: '0px'
//    });
//    $('.markersTableContainer').show();
//    $('.tableNavigator').show();
//    $('.markersTable').animate({
//      marginLeft: '0px',
//    }, '1000');
//    var wait = setInterval(function() {
//      if (!$('.markersTable').is(":animated")) {
//        $('.arrowHideLocation').show();
//        $('#arrowHideLocationsTable').show();
//        clearInterval(wait);
//      }
//    }, 0);
//    $('.markersTableContainer').show();
//    hideLocationTable = false;
//  } else {
//    $('.arrowHideLocation').hide();
//    $('.markersTable').animate({
//      marginLeft: '-=100%',
//    }, '500');
//    var wait = setInterval(function() {
//      if (!$('.markersTable').is(":animated")) {
//    	  $('.arrowShowLocation').show();
//        $('#arrowShowLocationsTable').show();
//        clearInterval(wait);
//      }
//    }, 0);
//    $('.markersTableContainer').css({
//      marginLeft: '0px',
//    });
//    $('.tableNavigator').hide();
//    hideLocationTable = true;
//  }
//}

//function buttonDisplayLocations() {
//	  if ($('#sidebar').not('.active')) {
//		    //$('.markersTable2').hide();
//			  //$('.markersTable2').show();
//		      $('#sidebar').addClass('active');
//		      // fade in the overlay
//		      $('.overlay').addClass('active');
//		      $('.collapse.in').toggleClass('in');
//		      $('a[aria-expanded=true]').attr('aria-expanded', 'false');
//		  }
//      // open sidebar
//
//}

//function displayLocationsTable() {
//	
//    $('.markersTableContainer').show();
//    $('#directionLocation').hide();
//    $('.markersTable').show();
//    
//
//}

//function initGeolocation() {
//  $('#iconDirectionGeoloc').click(
//          function() {
//            map.gmap('getCurrentPosition', function(position, status) {
//              if (status === 'OK') {
//                currentLatLng = new google.maps.LatLng(
//                        position.coords.latitude, position.coords.longitude);
//                //TODO
//                //map.panTo(marker.position);
//
//                map.gmap('search', {
//                  'location': currentLatLng
//                }, function(results, status) {
//                  if (status === 'OK') {
//                    $('#from').val(results[0].formatted_address);
//                    $('#submitFindDirection').trigger("click");
//                    $('#resetInputFrom').css('display', 'inline-block');
//                  }
//                });
//              } else {
//                alert('Unable to get current position');
//              }
//            });
//          });
//}



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

//function resetForm(id, btn) {
//	  $('#' + id).val('');
//	  $('#' + btn).hide();
//
//	}
//var map = "";
//var recalculate = false;
//var waypts = [];
//var currentLatLng = null;
//var currentPosition = false;
//var newEndPosition = false;
//var hideLocationTable = true;
//var hideMenu = false;
//var hideDirectionTable = true;
//
//var visible_direction = false
//
//var findDirection = false;
//var last_checked = null;
//var totalMarkers = 0;
//var origin = null;
//var destination = null;
//var change_color = false;
//var temp_name = null;
//var temp_lat = null;
//var temp_lng = null;
//var temp_id = null;
//var temp_rating = null;
//var temp_nrating = null;
//var temp_latlng2 = null;
////var temp_id2 = null;
//var remove_selection = false;
//var insert_selected = true;
//var route = [];
//var markerCluster = null;
var user = null;
//var active_user = null;

// Goole Maps SKIN
//var white = [{featureType: "administrative",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: 'landscape',elementType: 'all',stylers: [{ hue: '#FFFFFF' },{ saturation: -100 },{ lightness: 100 },{ visibility: 'on' }]},{featureType: "poi",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "road",elementType: "all",stylers: [{ visibility: "on" },{ lightness: -30 }]},{featureType: "transit",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "water",elementType: "all",stylers: [{ saturation: -100 },{ lightness: -100 }]},{featureType: "all",elementType: "all",stylers: [{ saturation: -100 },{ lightness: 91 }]}];
//var route = [{"featureType":"administrative","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":20}]},{"featureType":"road","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":40}]},{"featureType":"water","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-10},{"lightness":30}]},{"featureType":"landscape.man_made","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":10}]},{"featureType":"landscape.natural","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":60}]},{"featureType":"poi","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]},{"featureType":"transit","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]}]
//var silver = [ { elementType: 'geometry', stylers: [{color: '#f5f5f5'}] }, { elementType: 'labels.icon', stylers: [{visibility: 'off'}] }, { elementType: 'labels.text.fill', stylers: [{color: '#616161'}] }, { elementType: 'labels.text.stroke', stylers: [{color: '#f5f5f5'}] }, { featureType: 'administrative.land_parcel', elementType: 'labels.text.fill', stylers: [{color: '#bdbdbd'}] }, { featureType: 'poi', elementType: 'geometry', stylers: [{color: '#eeeeee'}] }, { featureType: 'poi', elementType: 'labels.text.fill', stylers: [{color: '#757575'}] }, { featureType: 'poi.park', elementType: 'geometry', stylers: [{color: '#e5e5e5'}] }, { featureType: 'poi.park', elementType: 'labels.text.fill', stylers: [{color: '#9e9e9e'}] }, { featureType: 'road', elementType: 'geometry', stylers: [{color: '#ffffff'}] }, { featureType: 'road.arterial', elementType: 'labels.text.fill', stylers: [{color: '#757575'}] }, { featureType: 'road.highway', elementType: 'geometry', stylers: [{color: '#dadada'}] }, { featureType: 'road.highway', elementType: 'labels.text.fill', stylers: [{color: '#616161'}] }, { featureType: 'road.local', elementType: 'labels.text.fill', stylers: [{color: '#9e9e9e'}] }, { featureType: 'transit.line', elementType: 'geometry', stylers: [{color: '#e5e5e5'}] }, { featureType: 'transit.station', elementType: 'geometry', stylers: [{color: '#eeeeee'}] }, { featureType: 'water', elementType: 'geometry', stylers: [{color: '#c9c9c9'}] }, { featureType: 'water', elementType: 'labels.text.fill', stylers: [{color: '#9e9e9e'}] } ];
//var night = [ {elementType: 'geometry', stylers: [{color: '#242f3e'}]}, {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]}, {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]}, { featureType: 'administrative.locality', elementType: 'labels.text.fill', stylers: [{color: '#d59563'}] }, { featureType: 'poi', elementType: 'labels.text.fill', stylers: [{color: '#d59563'}] }, { featureType: 'poi.park', elementType: 'geometry', stylers: [{color: '#263c3f'}] }, { featureType: 'poi.park', elementType: 'labels.text.fill', stylers: [{color: '#6b9a76'}] }, { featureType: 'road', elementType: 'geometry', stylers: [{color: '#38414e'}] }, { featureType: 'road', elementType: 'geometry.stroke', stylers: [{color: '#212a37'}] }, { featureType: 'road', elementType: 'labels.text.fill', stylers: [{color: '#9ca5b3'}] }, { featureType: 'road.highway', elementType: 'geometry', stylers: [{color: '#746855'}] }, { featureType: 'road.highway', elementType: 'geometry.stroke', stylers: [{color: '#1f2835'}] }, { featureType: 'road.highway', elementType: 'labels.text.fill', stylers: [{color: '#f3d19c'}] }, { featureType: 'transit', elementType: 'geometry', stylers: [{color: '#2f3948'}] }, { featureType: 'transit.station', elementType: 'labels.text.fill', stylers: [{color: '#d59563'}] }, { featureType: 'water', elementType: 'geometry', stylers: [{color: '#17263c'}] }, { featureType: 'water', elementType: 'labels.text.fill', stylers: [{color: '#515c6d'}] }, { featureType: 'water', elementType: 'labels.text.stroke', stylers: [{color: '#17263c'}] } ];
//var retro = [ {elementType: 'geometry', stylers: [{color: '#ebe3cd'}]}, {elementType: 'labels.text.fill', stylers: [{color: '#523735'}]}, {elementType: 'labels.text.stroke', stylers: [{color: '#f5f1e6'}]}, { featureType: 'administrative', elementType: 'geometry.stroke', stylers: [{color: '#c9b2a6'}] }, { featureType: 'administrative.land_parcel', elementType: 'geometry.stroke', stylers: [{color: '#dcd2be'}] }, { featureType: 'administrative.land_parcel', elementType: 'labels.text.fill', stylers: [{color: '#ae9e90'}] }, { featureType: 'landscape.natural', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'poi', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'poi', elementType: 'labels.text.fill', stylers: [{color: '#93817c'}] }, { featureType: 'poi.park', elementType: 'geometry.fill', stylers: [{color: '#a5b076'}] }, { featureType: 'poi.park', elementType: 'labels.text.fill', stylers: [{color: '#447530'}] }, { featureType: 'road', elementType: 'geometry', stylers: [{color: '#f5f1e6'}] }, { featureType: 'road.arterial', elementType: 'geometry', stylers: [{color: '#fdfcf8'}] }, { featureType: 'road.highway', elementType: 'geometry', stylers: [{color: '#f8c967'}] }, { featureType: 'road.highway', elementType: 'geometry.stroke', stylers: [{color: '#e9bc62'}] }, { featureType: 'road.highway.controlled_access', elementType: 'geometry', stylers: [{color: '#e98d58'}] }, { featureType: 'road.highway.controlled_access', elementType: 'geometry.stroke', stylers: [{color: '#db8555'}] }, { featureType: 'road.local', elementType: 'labels.text.fill', stylers: [{color: '#806b63'}] }, { featureType: 'transit.line', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'transit.line', elementType: 'labels.text.fill', stylers: [{color: '#8f7d77'}] }, { featureType: 'transit.line', elementType: 'labels.text.stroke', stylers: [{color: '#ebe3cd'}] }, { featureType: 'transit.station', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'water', elementType: 'geometry.fill', stylers: [{color: '#b9d3c2'}] }, { featureType: 'water', elementType: 'labels.text.fill', stylers: [{color: '#92998d'}] } ];
//var light = [ { "featureType": "water", "elementType": "geometry", "stylers": [ { "color": "#e9e9e9" }, { "lightness": 17 } ] }, { "featureType": "landscape", "elementType": "geometry", "stylers": [ { "color": "#f5f5f5" }, { "lightness": 20 } ] }, { "featureType": "road.highway", "elementType": "geometry.fill", "stylers": [ { "color": "#ffffff" }, { "lightness": 17 } ] }, { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [ { "color": "#ffffff" }, { "lightness": 29 }, { "weight": 0.2 } ] }, { "featureType": "road.arterial", "elementType": "geometry", "stylers": [ { "color": "#ffffff" }, { "lightness": 18 } ] }, { "featureType": "road.local", "elementType": "geometry", "stylers": [ { "color": "#ffffff" }, { "lightness": 16 } ] }, { "featureType": "poi", "elementType": "geometry", "stylers": [ { "color": "#f5f5f5" }, { "lightness": 21 } ] }, { "featureType": "poi.park", "elementType": "geometry", "stylers": [ { "color": "#dedede" }, { "lightness": 21 } ] }, { "elementType": "labels.text.stroke", "stylers": [ { "visibility": "on" }, { "color": "#ffffff" }, { "lightness": 16 } ] }, { "elementType": "labels.text.fill", "stylers": [ { "saturation": 36 }, { "color": "#333333" }, { "lightness": 40 } ] }, { "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "transit", "elementType": "geometry", "stylers": [ { "color": "#f2f2f2" }, { "lightness": 19 } ] }, { "featureType": "administrative", "elementType": "geometry.fill", "stylers": [ { "color": "#fefefe" }, { "lightness": 20 } ] }, { "featureType": "administrative", "elementType": "geometry.stroke", "stylers": [ { "color": "#fefefe" }, { "lightness": 17 }, { "weight": 1.2 } ] } ];
//var black = [ { "featureType": "all", "elementType": "labels.text.fill", "stylers": [ { "saturation": 36 }, { "color": "#000000" }, { "lightness": 40 } ] }, { "featureType": "all", "elementType": "labels.text.stroke", "stylers": [ { "visibility": "on" }, { "color": "#000000" }, { "lightness": 16 } ] }, { "featureType": "all", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "administrative", "elementType": "geometry.fill", "stylers": [ { "color": "#000000" }, { "lightness": 20 } ] }, { "featureType": "administrative", "elementType": "geometry.stroke", "stylers": [ { "color": "#000000" }, { "lightness": 17 }, { "weight": 1.2 } ] }, { "featureType": "landscape", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 20 } ] }, { "featureType": "poi", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 21 } ] }, { "featureType": "road.highway", "elementType": "geometry.fill", "stylers": [ { "color": "#000000" }, { "lightness": 17 } ] }, { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [ { "color": "#000000" }, { "lightness": 29 }, { "weight": 0.2 } ] }, { "featureType": "road.arterial", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 18 } ] }, { "featureType": "road.local", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 16 } ] }, { "featureType": "transit", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 19 } ] }, { "featureType": "water", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 17 } ] } ];
//var grayscale = [{ "featureType": "administrative", "elementType": "all", "stylers": [ { "saturation": "-100" } ] }, { "featureType": "administrative.province", "elementType": "all", "stylers": [ { "visibility": "off" } ] }, { "featureType": "landscape", "elementType": "all", "stylers": [ { "saturation": -100 }, { "lightness": 65 }, { "visibility": "on" } ] }, { "featureType": "poi", "elementType": "all", "stylers": [ { "saturation": -100 }, { "lightness": "50" }, { "visibility": "simplified" } ] }, { "featureType": "road", "elementType": "all", "stylers": [ { "saturation": "-100" } ] }, { "featureType": "road.highway", "elementType": "all", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "road.arterial", "elementType": "all", "stylers": [ { "lightness": "30" } ] }, { "featureType": "road.local", "elementType": "all", "stylers": [ { "lightness": "40" } ] }, { "featureType": "transit", "elementType": "all", "stylers": [ { "saturation": -100 }, { "visibility": "simplified" } ] }, { "featureType": "water", "elementType": "geometry", "stylers": [ { "hue": "#ffff00" }, { "lightness": -25 }, { "saturation": -97 } ] }, { "featureType": "water", "elementType": "labels", "stylers": [ { "lightness": -25 }, { "saturation": -100 } ] } ];
//var monotone = [ { "stylers": [ { "visibility": "on" }, { "saturation": -100 }, { "gamma": 0.54 } ] }, { "featureType": "road", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "water", "stylers": [ { "color": "#4d4946" } ] }, { "featureType": "poi", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "poi", "elementType": "labels.text", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "road", "elementType": "geometry.fill", "stylers": [ { "color": "#ffffff" } ] }, { "featureType": "road.local", "elementType": "labels.text", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "water", "elementType": "labels.text.fill", "stylers": [ { "color": "#ffffff" } ] }, { "featureType": "transit.line", "elementType": "geometry", "stylers": [ { "gamma": 0.48 } ] }, { "featureType": "transit.station", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "road", "elementType": "geometry.stroke", "stylers": [ { "gamma": 7.18 } ] } ];
//var nature = [ { "featureType": "landscape", "stylers": [ { "hue": "#FFA800" }, { "saturation": 0 }, { "lightness": 0 }, { "gamma": 1 } ] }, { "featureType": "road.highway", "stylers": [ { "hue": "#53FF00" }, { "saturation": -73 }, { "lightness": 40 }, { "gamma": 1 } ] }, { "featureType": "road.arterial", "stylers": [ { "hue": "#FBFF00" }, { "saturation": 0 }, { "lightness": 0 }, { "gamma": 1 } ] }, { "featureType": "road.local", "stylers": [ { "hue": "#00FFFD" }, { "saturation": 0 }, { "lightness": 30 }, { "gamma": 1 } ] }, { "featureType": "water", "stylers": [ { "hue": "#00BFFF" }, { "saturation": 6 }, { "lightness": 8 }, { "gamma": 1 } ] }, { "featureType": "poi", "stylers": [ { "hue": "#679714" }, { "saturation": 33.4 }, { "lightness": -25.4 }, { "gamma": 1 } ] } ];
//var colourful = [ { "featureType": "water", "stylers": [ { "color": "#19a0d8" } ] }, { "featureType": "administrative", "elementType": "labels.text.stroke", "stylers": [ { "color": "#ffffff" }, { "weight": 6 } ] }, { "featureType": "administrative", "elementType": "labels.text.fill", "stylers": [ { "color": "#e85113" } ] }, { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [ { "color": "#efe9e4" }, { "lightness": -40 } ] }, { "featureType": "road.arterial", "elementType": "geometry.stroke", "stylers": [ { "color": "#efe9e4" }, { "lightness": -20 } ] }, { "featureType": "road", "elementType": "labels.text.stroke", "stylers": [ { "lightness": 100 } ] }, { "featureType": "road", "elementType": "labels.text.fill", "stylers": [ { "lightness": -100 } ] }, { "featureType": "road.highway", "elementType": "labels.icon" }, { "featureType": "landscape", "elementType": "labels", "stylers": [ { "visibility": "off" } ] }, { "featureType": "landscape", "stylers": [ { "lightness": 20 }, { "color": "#efe9e4" } ] }, { "featureType": "landscape.man_made", "stylers": [ { "visibility": "off" } ] }, { "featureType": "water", "elementType": "labels.text.stroke", "stylers": [ { "lightness": 100 } ] }, { "featureType": "water", "elementType": "labels.text.fill", "stylers": [ { "lightness": -100 } ] }, { "featureType": "poi", "elementType": "labels.text.fill", "stylers": [ { "hue": "#11ff00" } ] }, { "featureType": "poi", "elementType": "labels.text.stroke", "stylers": [ { "lightness": 100 } ] }, { "featureType": "poi", "elementType": "labels.icon", "stylers": [ { "hue": "#4cff00" }, { "saturation": 58 } ] }, { "featureType": "poi", "elementType": "geometry", "stylers": [ { "visibility": "on" }, { "color": "#f0e4d3" } ] }, { "featureType": "road.highway", "elementType": "geometry.fill", "stylers": [ { "color": "#efe9e4" }, { "lightness": -25 } ] }, { "featureType": "road.arterial", "elementType": "geometry.fill", "stylers": [ { "color": "#efe9e4" }, { "lightness": -10 } ] }, { "featureType": "poi", "elementType": "labels", "stylers": [ { "visibility": "simplified" } ] } ];
//var bluewater = [ { "featureType": "administrative", "elementType": "labels.text.fill", "stylers": [ { "color": "#444444" } ] }, { "featureType": "landscape", "elementType": "all", "stylers": [ { "color": "#f2f2f2" } ] }, { "featureType": "poi", "elementType": "all", "stylers": [ { "visibility": "off" } ] }, { "featureType": "road", "elementType": "all", "stylers": [ { "saturation": -100 }, { "lightness": 45 } ] }, { "featureType": "road.highway", "elementType": "all", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "road.arterial", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "transit", "elementType": "all", "stylers": [ { "visibility": "off" } ] }, { "featureType": "water", "elementType": "all", "stylers": [ { "color": "#46bcec" }, { "visibility": "on" } ] } ];

/*
var white = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]},{featureType: "administrative",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: 'landscape',elementType: 'all',stylers: [{ hue: '#FFFFFF' },{ saturation: -100 },{ lightness: 100 },{ visibility: 'on' }]},{featureType: "road",elementType: "all",stylers: [{ visibility: "on" },{ lightness: -30 }]},{featureType: "transit",elementType: "all",stylers: [{ visibility: "off" }]},{featureType: "water",elementType: "all",stylers: [{ saturation: -100 },{ lightness: -100 }]},{featureType: "all",elementType: "all",stylers: [{ saturation: -100 },{ lightness: 91 }]}];
var route = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]},{"featureType":"administrative","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":20}]},{"featureType":"road","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-100},{"lightness":40}]},{"featureType":"water","elementType":"all","stylers":[{"visibility":"on"},{"saturation":-10},{"lightness":30}]},{"featureType":"landscape.man_made","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":10}]},{"featureType":"landscape.natural","elementType":"all","stylers":[{"visibility":"simplified"},{"saturation":-60},{"lightness":60}]},{"featureType":"transit","elementType":"all","stylers":[{"visibility":"off"},{"saturation":-100},{"lightness":60}]}]
var silver = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { elementType: 'geometry', stylers: [{color: '#f5f5f5'}] }, { elementType: 'labels.icon', stylers: [{visibility: 'off'}] }, { elementType: 'labels.text.fill', stylers: [{color: '#616161'}] }, { elementType: 'labels.text.stroke', stylers: [{color: '#f5f5f5'}] }, { featureType: 'administrative.land_parcel', elementType: 'labels.text.fill', stylers: [{color: '#bdbdbd'}] }, { featureType: 'road', elementType: 'geometry', stylers: [{color: '#ffffff'}] }, { featureType: 'road.arterial', elementType: 'labels.text.fill', stylers: [{color: '#757575'}] }, { featureType: 'road.highway', elementType: 'geometry', stylers: [{color: '#dadada'}] }, { featureType: 'road.highway', elementType: 'labels.text.fill', stylers: [{color: '#616161'}] }, { featureType: 'road.local', elementType: 'labels.text.fill', stylers: [{color: '#9e9e9e'}] }, { featureType: 'transit.line', elementType: 'geometry', stylers: [{color: '#e5e5e5'}] }, { featureType: 'transit.station', elementType: 'geometry', stylers: [{color: '#eeeeee'}] }, { featureType: 'water', elementType: 'geometry', stylers: [{color: '#c9c9c9'}] }, { featureType: 'water', elementType: 'labels.text.fill', stylers: [{color: '#9e9e9e'}] } ];
var night = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, {elementType: 'geometry', stylers: [{color: '#242f3e'}]}, {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]}, {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]}, { featureType: 'administrative.locality', elementType: 'labels.text.fill', stylers: [{color: '#d59563'}] }, { featureType: 'road', elementType: 'geometry', stylers: [{color: '#38414e'}] }, { featureType: 'road', elementType: 'geometry.stroke', stylers: [{color: '#212a37'}] }, { featureType: 'road', elementType: 'labels.text.fill', stylers: [{color: '#9ca5b3'}] }, { featureType: 'road.highway', elementType: 'geometry', stylers: [{color: '#746855'}] }, { featureType: 'road.highway', elementType: 'geometry.stroke', stylers: [{color: '#1f2835'}] }, { featureType: 'road.highway', elementType: 'labels.text.fill', stylers: [{color: '#f3d19c'}] }, { featureType: 'transit', elementType: 'geometry', stylers: [{color: '#2f3948'}] }, { featureType: 'transit.station', elementType: 'labels.text.fill', stylers: [{color: '#d59563'}] }, { featureType: 'water', elementType: 'geometry', stylers: [{color: '#17263c'}] }, { featureType: 'water', elementType: 'labels.text.fill', stylers: [{color: '#515c6d'}] }, { featureType: 'water', elementType: 'labels.text.stroke', stylers: [{color: '#17263c'}] } ];
var retro = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, {elementType: 'geometry', stylers: [{color: '#ebe3cd'}]}, {elementType: 'labels.text.fill', stylers: [{color: '#523735'}]}, {elementType: 'labels.text.stroke', stylers: [{color: '#f5f1e6'}]}, { featureType: 'administrative', elementType: 'geometry.stroke', stylers: [{color: '#c9b2a6'}] }, { featureType: 'administrative.land_parcel', elementType: 'geometry.stroke', stylers: [{color: '#dcd2be'}] }, { featureType: 'administrative.land_parcel', elementType: 'labels.text.fill', stylers: [{color: '#ae9e90'}] }, { featureType: 'landscape.natural', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'road', elementType: 'geometry', stylers: [{color: '#f5f1e6'}] }, { featureType: 'road.arterial', elementType: 'geometry', stylers: [{color: '#fdfcf8'}] }, { featureType: 'road.highway', elementType: 'geometry', stylers: [{color: '#f8c967'}] }, { featureType: 'road.highway', elementType: 'geometry.stroke', stylers: [{color: '#e9bc62'}] }, { featureType: 'road.highway.controlled_access', elementType: 'geometry', stylers: [{color: '#e98d58'}] }, { featureType: 'road.highway.controlled_access', elementType: 'geometry.stroke', stylers: [{color: '#db8555'}] }, { featureType: 'road.local', elementType: 'labels.text.fill', stylers: [{color: '#806b63'}] }, { featureType: 'transit.line', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'transit.line', elementType: 'labels.text.fill', stylers: [{color: '#8f7d77'}] }, { featureType: 'transit.line', elementType: 'labels.text.stroke', stylers: [{color: '#ebe3cd'}] }, { featureType: 'transit.station', elementType: 'geometry', stylers: [{color: '#dfd2ae'}] }, { featureType: 'water', elementType: 'geometry.fill', stylers: [{color: '#b9d3c2'}] }, { featureType: 'water', elementType: 'labels.text.fill', stylers: [{color: '#92998d'}] } ];
var light = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { "featureType": "water", "elementType": "geometry", "stylers": [ { "color": "#e9e9e9" }, { "lightness": 17 } ] }, { "featureType": "landscape", "elementType": "geometry", "stylers": [ { "color": "#f5f5f5" }, { "lightness": 20 } ] }, { "featureType": "road.highway", "elementType": "geometry.fill", "stylers": [ { "color": "#ffffff" }, { "lightness": 17 } ] }, { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [ { "color": "#ffffff" }, { "lightness": 29 }, { "weight": 0.2 } ] }, { "featureType": "road.arterial", "elementType": "geometry", "stylers": [ { "color": "#ffffff" }, { "lightness": 18 } ] }, { "featureType": "road.local", "elementType": "geometry", "stylers": [ { "color": "#ffffff" }, { "lightness": 16 } ] }, { "elementType": "labels.text.stroke", "stylers": [ { "visibility": "on" }, { "color": "#ffffff" }, { "lightness": 16 } ] }, { "elementType": "labels.text.fill", "stylers": [ { "saturation": 36 }, { "color": "#333333" }, { "lightness": 40 } ] }, { "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "transit", "elementType": "geometry", "stylers": [ { "color": "#f2f2f2" }, { "lightness": 19 } ] }, { "featureType": "administrative", "elementType": "geometry.fill", "stylers": [ { "color": "#fefefe" }, { "lightness": 20 } ] }, { "featureType": "administrative", "elementType": "geometry.stroke", "stylers": [ { "color": "#fefefe" }, { "lightness": 17 }, { "weight": 1.2 } ] } ];
var black = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { "featureType": "all", "elementType": "labels.text.fill", "stylers": [ { "saturation": 36 }, { "color": "#000000" }, { "lightness": 40 } ] }, { "featureType": "all", "elementType": "labels.text.stroke", "stylers": [ { "visibility": "on" }, { "color": "#000000" }, { "lightness": 16 } ] }, { "featureType": "all", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "administrative", "elementType": "geometry.fill", "stylers": [ { "color": "#000000" }, { "lightness": 20 } ] }, { "featureType": "administrative", "elementType": "geometry.stroke", "stylers": [ { "color": "#000000" }, { "lightness": 17 }, { "weight": 1.2 } ] }, { "featureType": "landscape", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 20 } ] }, { "featureType": "road.highway", "elementType": "geometry.fill", "stylers": [ { "color": "#000000" }, { "lightness": 17 } ] }, { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [ { "color": "#000000" }, { "lightness": 29 }, { "weight": 0.2 } ] }, { "featureType": "road.arterial", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 18 } ] }, { "featureType": "road.local", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 16 } ] }, { "featureType": "transit", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 19 } ] }, { "featureType": "water", "elementType": "geometry", "stylers": [ { "color": "#000000" }, { "lightness": 17 } ] } ];
var grayscale = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]},{ "featureType": "administrative", "elementType": "all", "stylers": [ { "saturation": "-100" } ] }, { "featureType": "administrative.province", "elementType": "all", "stylers": [ { "visibility": "off" } ] }, { "featureType": "landscape", "elementType": "all", "stylers": [ { "saturation": -100 }, { "lightness": 65 }, { "visibility": "on" } ] }, { "featureType": "road", "elementType": "all", "stylers": [ { "saturation": "-100" } ] }, { "featureType": "road.highway", "elementType": "all", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "road.arterial", "elementType": "all", "stylers": [ { "lightness": "30" } ] }, { "featureType": "road.local", "elementType": "all", "stylers": [ { "lightness": "40" } ] }, { "featureType": "transit", "elementType": "all", "stylers": [ { "saturation": -100 }, { "visibility": "simplified" } ] }, { "featureType": "water", "elementType": "geometry", "stylers": [ { "hue": "#ffff00" }, { "lightness": -25 }, { "saturation": -97 } ] }, { "featureType": "water", "elementType": "labels", "stylers": [ { "lightness": -25 }, { "saturation": -100 } ] } ];
var monotone = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { "stylers": [ { "visibility": "on" }, { "saturation": -100 }, { "gamma": 0.54 } ] }, { "featureType": "road", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "water", "stylers": [ { "color": "#4d4946" } ] }, { "featureType": "road", "elementType": "geometry.fill", "stylers": [ { "color": "#ffffff" } ] }, { "featureType": "road.local", "elementType": "labels.text", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "water", "elementType": "labels.text.fill", "stylers": [ { "color": "#ffffff" } ] }, { "featureType": "transit.line", "elementType": "geometry", "stylers": [ { "gamma": 0.48 } ] }, { "featureType": "transit.station", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "road", "elementType": "geometry.stroke", "stylers": [ { "gamma": 7.18 } ] } ];
var nature = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { "featureType": "landscape", "stylers": [ { "hue": "#FFA800" }, { "saturation": 0 }, { "lightness": 0 }, { "gamma": 1 } ] }, { "featureType": "road.highway", "stylers": [ { "hue": "#53FF00" }, { "saturation": -73 }, { "lightness": 40 }, { "gamma": 1 } ] }, { "featureType": "road.arterial", "stylers": [ { "hue": "#FBFF00" }, { "saturation": 0 }, { "lightness": 0 }, { "gamma": 1 } ] }, { "featureType": "road.local", "stylers": [ { "hue": "#00FFFD" }, { "saturation": 0 }, { "lightness": 30 }, { "gamma": 1 } ] }, { "featureType": "water", "stylers": [ { "hue": "#00BFFF" }, { "saturation": 6 }, { "lightness": 8 }, { "gamma": 1 } ] } ];
var colourful = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { "featureType": "water", "stylers": [ { "color": "#19a0d8" } ] }, { "featureType": "administrative", "elementType": "labels.text.stroke", "stylers": [ { "color": "#ffffff" }, { "weight": 6 } ] }, { "featureType": "administrative", "elementType": "labels.text.fill", "stylers": [ { "color": "#e85113" } ] }, { "featureType": "road.highway", "elementType": "geometry.stroke", "stylers": [ { "color": "#efe9e4" }, { "lightness": -40 } ] }, { "featureType": "road.arterial", "elementType": "geometry.stroke", "stylers": [ { "color": "#efe9e4" }, { "lightness": -20 } ] }, { "featureType": "road", "elementType": "labels.text.stroke", "stylers": [ { "lightness": 100 } ] }, { "featureType": "road", "elementType": "labels.text.fill", "stylers": [ { "lightness": -100 } ] }, { "featureType": "road.highway", "elementType": "labels.icon" }, { "featureType": "landscape", "elementType": "labels", "stylers": [ { "visibility": "off" } ] }, { "featureType": "landscape", "stylers": [ { "lightness": 20 }, { "color": "#efe9e4" } ] }, { "featureType": "landscape.man_made", "stylers": [ { "visibility": "off" } ] }, { "featureType": "water", "elementType": "labels.text.stroke", "stylers": [ { "lightness": 100 } ] }, { "featureType": "water", "elementType": "labels.text.fill", "stylers": [ { "lightness": -100 } ] }, { "featureType": "road.highway", "elementType": "geometry.fill", "stylers": [ { "color": "#efe9e4" }, { "lightness": -25 } ] }, { "featureType": "road.arterial", "elementType": "geometry.fill", "stylers": [ { "color": "#efe9e4" }, { "lightness": -10 } ] }];
var bluewater = [{featureType:"poi",elementType:"labels",stylers:[{ visibility:"off"}]}, { "featureType": "administrative", "elementType": "labels.text.fill", "stylers": [ { "color": "#444444" } ] }, { "featureType": "landscape", "elementType": "all", "stylers": [ { "color": "#f2f2f2" } ] }, { "featureType": "road", "elementType": "all", "stylers": [ { "saturation": -100 }, { "lightness": 45 } ] }, { "featureType": "road.highway", "elementType": "all", "stylers": [ { "visibility": "simplified" } ] }, { "featureType": "road.arterial", "elementType": "labels.icon", "stylers": [ { "visibility": "off" } ] }, { "featureType": "transit", "elementType": "all", "stylers": [ { "visibility": "off" } ] }, { "featureType": "water", "elementType": "all", "stylers": [ { "color": "#46bcec" }, { "visibility": "on" } ] } ];
var styledMapOptions = [];
var whiteMapType = new google.maps.StyledMapType(white, styledMapOptions);
var routeMapType = new google.maps.StyledMapType(route, styledMapOptions);
var silverMapType = new google.maps.StyledMapType(silver, styledMapOptions);
var nightMapType = new google.maps.StyledMapType(night, styledMapOptions);
var retroMapType = new google.maps.StyledMapType(retro, styledMapOptions);
var ultraLightMapType = new google.maps.StyledMapType(light, styledMapOptions);
var grayscaleMapType = new google.maps.StyledMapType(grayscale, styledMapOptions);
var monotoneMapType = new google.maps.StyledMapType(monotone, styledMapOptions);
var natureMapType = new google.maps.StyledMapType(nature, styledMapOptions);
var colourfulMapType = new google.maps.StyledMapType(colourful, styledMapOptions);
var blueWaterMapType = new google.maps.StyledMapType(bluewater, styledMapOptions);
*/

//function initialize() {
//  var from = document.getElementById('from');
//  $('#from').on("change keyup paste", function() {
//    new google.maps.places.Autocomplete(from, {
//      types: ['geocode']
//    });
//    $('#resetInputFrom').css('display', 'inline-block');
//    if ($(this).val() == '') {
//      $('#resetInputFrom').hide();
//    }
//  });
//  var to = document.getElementById('to');
//  $('#to').on("change keyup paste", function() {
//    new google.maps.places.Autocomplete(to, {
//      types: ['geocode']
//    });
//    $('#resetInputTo').css('display', 'inline-block');
//    if ($(this).val() == '') {
//      $('#resetInputTo').hide();
//    }
//  });
//  $('#mode').on('change', function() {
//    $('#submitFindDirection').trigger("click");
//  });
//
//  $("#polygonCoordInput").on('keypress', function(e){
//	  if(e.which == 13) {
//	  $('#savePolygonCoordinates').trigger('click');
//	  }
//	  
//
//
//
//
// });
  
  
  

  // $(':checkbox').addClass("checkbox");
//}
/*google.maps.event.addDomListener(window, 'load', initialize);*/

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






function getSearchParams(k){
	 var p={};
	 location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){p[k]=v})
	 return k?p[k]:p;
}
function showSidebar(id) {
  $('#idInput2').val(id);
  $('#showMarkerInfoBttn').trigger('click');
	if($('#sidebar').not('.active')){$('#sidebar').addClass('active');$('.overlay').addClass('active');}
}

/*function markerClicked(e) {
	 var marker = e.target;
	 var lat = marker.myData.lat;
	 var lng = marker.myData.lng;
	 //hack where we add 3 random digits at the end in order to create a new key (avoids streetview of freezing after its first load)
	 var random = Math.floor(Math.random() * (500 - 50 + 1)) + 50;
	 lat = marker.myData.lat+random;
	 lng = marker.myData.lng+random;
	 var latlng = new google.maps.LatLng(lat,lng);
	 callStreetView(latlng);
	 showSidebar(marker.myData.id);
	 $('#showMarkerInfoBttn').trigger('click');
}*/
//function getGeolocation() {
//
//
////  map.gmap('getCurrentPosition', function(position, status) {
////    if (status === 'OK') {
////      currentLatLng = new google.maps.LatLng(position.coords.latitude,
////              position.coords.longitude);
////      map.gmap('get', 'map').panTo(currentLatLng);
////      map.gmap('get', 'map').setZoom(parseInt($('#mapZoomLevel').val()));
////
////      map.gmap('search', {
////        'location': currentLatLng
////      }, function(results, status) {
////        if (status === 'OK') {
////
////          $('#polygonCoordInput').val(currentLatLng);
////          $('#savePolygonCoordinates').trigger('click');
////        }
////      });
////    } else {
////      alert('Unable to get current position');
////    }
////  });
//}
var map2 = null;
function onLocationFound(e) {
	
	if(getSearchParams("coords") === undefined) {
    //var radius = e.accuracy / 2;

    $('#polygonCoordInput').val(e.latlng.toString().replace(/\LatLng|\s+/g, ''));
    $('#savePolygonCoordinates').trigger('click');
    
	} else {
	    var iconMarker = L.icon({
	    	  draggable: true,
			  iconUrl: 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="40" height="40" viewBox="-100 -100 200 200"><defs><g id="a" transform="rotate(45)"></g></defs><g fill="#F39C123D"><circle fill="#724600" r="38px" /><circle fill="#f39c12" r="30" /><circle r="100" /><use xlink:href="#a"/><g transform="rotate(120)"><use xlink:href="#a"/></g><g transform="rotate(240)"><use xlink:href="#a"/></g></g></svg>'),
		  });
		//var myRenderer = L.canvas({ padding: 0.5 });
	    var latlng = getSearchParams('coords').split(',');
	    var lat = latlng[0].replace(/%28|\(/, '');
	    var lng = latlng[1].replace(/%29|\)/, '');
	    latlng = new L.latLng(lat,lng);

	    marker = L.marker(latlng, {icon: iconMarker}) //'content': "Drag me Sir!<br/>Slider is in the upper left corner."
	      .bindPopup('Drag your current location<br>Control the distance visibility from the top left corner')
	      .openPopup()
	      .on('mouseover', function (e) {
		            this.openPopup();
		   })
		   .on('mouseout', function (e) {
	            this.closePopup();
	        });
//	    marker.on('click', function(){
//	      confirm("You can drag the icon and adjust the desired distance");
//	    });
        marker.on("dragend",function(e){
            $('#polygonCoordInput').val(e.target.getLatLng().toString().replace(/\LatLng|\s+/g, ''));
            $('#savePolygonCoordinates').trigger('click');
        }); 
	    $(".slider_wrapper").show();
	    marker.addTo(map2).dragging.enable();
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
var map = null;
//var markerClusters = null
var editableLayers = null;

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

$(function() {
	if($('#mapid').length === 1) {
	  map2 = L.map('mapid', {
		  //preferCanvas: true,
	      center: [20.0, 5.0],
	      minZoom: 2,
	      maxZoom: 18,
	      zoom: 2,
	      zoomControl:false,
	  });
	  



	 // if($('#mapid').val() != undefined) {
		//L_PREFER_CANVAS = true;


	  //var markersList = L.featureGroup();
	  /*markerClusters = L.markerClusterGroup({
		  maxClusterRadius: 60,
		  spiderfyOnMaxZoom: true,
		  showCoverageOnHover: true,
		  zoomToBoundsOnClick: true
		 });*/
	  //map2.addLayer(markersList);
	  //markerClusters.freezeAtZoom(15);
	  /*markerClusters.enableClustering();
	  map2.addLayer(markerClusters);*/


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
//	      L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
//              attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributor',    
//              //other attributes.
//}).addTo(m);
	      
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
//		var cloudscls = L.OWM.cloudsClassic({opacity: 0.5, appId: OWM_API_KEY});
//		var raincls = L.OWM.rainClassic({opacity: 0.5, appId: OWM_API_KEY});
//		var precipitationcls = L.OWM.precipitationClassic({opacity: 0.5, appId: OWM_API_KEY});
			  
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
				  //"Show Markers": markerClusters,
				    "Current Weather (min Zoom 5)": city,
				    "Temperature": temp,
				    "Precipitation": precipitation,
				    "Rain": rain,
				    "Clouds": clouds,
				    "Wind Rose": windrose,
				    "Snowing": snow,
					  "Toner Lines":Stamen_TonerLines,
					  "Toner Labels":Stamen_TonerLabels,
					  "RoadsAndLabels":Hydda_RoadsAndLabels,
					  "Public Transport":OpenPtMap,
					  "Railway":OpenRailwayMap,

				};
		  layerControl = L.control.layers(baseLayers, overlayLayers,{collapsed: true}).addTo(map2);

			$('#icon-mylocation').on('click', function(){
				  map2.locate();//{setView: true, maxZoom: 15}
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
		  		default: wikimedia.addTo(map2);
		  		}
		    } else {
		    	//set default layer
		    	wikimedia.addTo(map2);
		    }
		 map2.on('baselayerchange', function (e) {
			    console.log(e.name);
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
		  map2.on('zoom', function() {
			  $('#mapZoomLevel').val(map2.getZoom());
			});


		  
//			 var random = Math.floor(Math.random() * (500 - 50 + 1)) + 50;
//			 lat = marker.myData.lat+random;
//			 lng = marker.myData.lng+random;
//			 var latlng = new google.maps.LatLng(lat,lng);
		  
		  if (localStorage.getItem('jsonobj') !== undefined) {
			  //if (localStorage.getItem('jsonobj') !== null && localStorage.getItem('jsonobj') != 'undefined') {
			  var markers = JSON.parse(localStorage.getItem('jsonobj'));
			  	  //fitBounds used for positioning screen with the markers
				  //map2.fitBounds([[markers[0].lat,markers[0].lng],[markers[markers.length-1].lat,markers[markers.length-1].lng]]);
			  		var markers2 = [];
				  for (var i=0; i<markers.length; ++i){
					  markers2.push([JSON.parse(markers[i].lat),JSON.parse(markers[i].lng),markers[i].name+"+"+markers[i].id+"+"+markers[i].icon]);
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
				    L.glify.points({
				        map: map2,
				        color: function(index,point){if(!colorsel){color=fromHex(point[2].split('+')[2]);}return color},
				        opacity: opacity,
				        click: function(e,point,xy) {
				          L.popup()
				              .setLatLng([point[0],point[1]])
				              .setContent(point[2].split('+')[0],callStreetView(point[0],point[1]),showSidebar(point[2].split('+')[1])) //
				              .openOn(map2);

				        },
				        onMouseOver: function (e, point, xy) {e.target.openPopup()},
				        data: markers2,
				        size: size,		        
				      });
					  if(getSearchParams("coords") != null && getSearchParams("coords").split(',').length == 2) {
						    var latlng = getSearchParams('coords').split(',');
						    var lat = latlng[0].replace(/%28|\(/, '');
						    var lng = latlng[1].replace(/%29|\)/, '');
						    //new L.latLng(lat,lng)
						    
						    
							  setTimeout(function(){
								  var zoom = getSearchParams("zoom");
								  	//map2.setZoom(zoom);

								  	map2.setZoom(zoom);
								  	setTimeout(function(){
								  	map2.panTo([lat,lng]);
								  	},500); 

									//console.log("PAN when geolocation:"+[lat,lng]+" with ZOOM "+getSearchParams("zoom"));
							  },800); 
						    


						  } else {
							  map2.fitBounds([[markers[0].lat,markers[0].lng],[markers[markers.length-1].lat,markers[markers.length-1].lng]]);
							  map2.setZoom(getSearchParams("zoom"));
							  //map2.panTo([markers[0].lat,markers[0].lng],parseInt($('#mapZoomLevel').val()),{animation: true});
							  //console.log("PAN NO geolocation:"+[markers[0].lat,markers[0].lng]);
						  }
				  
//				    var markers = JSON.parse(localStorage.getItem('jsonobj'));
//					  map2.fitBounds([[markers[0].lat,markers[0].lng],[markers[markers.length-1].lat,markers[markers.length-1].lng]]);
//				  for (var i=0; i<markers.length; ++i){
//					  var icon = markers[i].icon.split('.');
//					  var iconMarker = L.icon({
//						  iconUrl: 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="20" height="20" viewBox="-100 -100 200 200"><defs><g id="a" transform="rotate(45)"></g></defs><g fill="'+icon[0]+'"><circle style="fill:'+icon[1]+'" r="'+icon[3]+'" /><circle r="'+icon[2]+'"/><use xlink:href="#a"/><g transform="rotate(120)"><use xlink:href="#a"/></g><g transform="rotate(240)"><use xlink:href="#a"/></g></g></svg>'),
//					  });
//				     var marker = L.marker(L.latLng(markers[i].lat, markers[i].lng), {icon: iconMarker});
//				     markerClusters.addLayer(marker);
//				     //markersList.addLayer(marker);
//				     marker.bindPopup(markers[i].name)
//				     .on('mouseover', function (e) {
//				            this.openPopup();
//				        })
//				     .on('mouseout', function (e) {
//			            this.closePopup();
//			        })
//				     .on('click', markerClicked)
//				     .myData = markers[i];
//				    }
//				  }
			  }

		// Initialise the FeatureGroup to store editable layers
		  editableLayers = new L.FeatureGroup();
		  map2.addLayer(editableLayers);
		  var drawPluginOptions = {
		    position: 'bottomright',
		    draw : {
		        polyline : false,
		        circle : false,
		        rectangle: true,
		        polygon: true,
		        marker: true,
		        circlemarker: false
		    },
//		    draw: {
//		      polygon: {
//		        allowIntersection: false, // Restricts shapes to simple polygons
//		        drawError: {
//		          color: '#e1e100', // Color the shape will turn when intersects
//		          message: '<strong>Oh snap!<strong> you can\'t draw that!' // Message that will show when intersect
//		        },
////		        shapeOptions: {
////		          color: '#d8f0ff'
////		        }
//		      },
//		      // disable toolbar item by setting it to false
//		      polyline: false,
//		      circle: true, // Turns off this drawing tool
//		      rectangle: true,
//		      marker: true,
//		      },
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
			        console.log("points are:"+points);
			        
			   }
			    if(type === 'rectangle') {
			        var points = layer.getLatLngs().toString().replace(/\LatLng|\s+/g, '');
			        console.log("points rectangle are:"+points);
			   }
			    if(type === 'marker') {
			        var points = layer.getLatLng().toString().replace(/\LatLng|\s+/g, '');
			        console.log("points marker are:"+points);
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
		  /*map = $('#map_canvas');
		  map.gmap({
		    'disableDefaultUI': true
		  });
		  */

		    // Attribution panel
		    $('.leaflet-control-attribution').hide();
			$("#attribution-button").on("mouseover",function(){
				$('.leaflet-control-attribution').show();
			});
			$(".leaflet-control-attribution").on("mouseleave",function(){
				$('.leaflet-control-attribution').hide();
			});
			//List file uploads panel
			$('#hideLocation').hide();
			$("#displayLocations").on("mouseover",function(){
				$('#hideLocation').show();
			});
			$("#locationView").on("mouseleave",function(){
				$('#hideLocation').hide();
			});
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
			     //console.log("value is "+activeValue+" val2 is "+seq_order+", max is "+valMap[valMap.length - 1]+" , and min is "+1);
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
			        	 //console.log("WE CHANGED VALUE TO "+valMap[ui.value]);
			           $('#geoCoordDistance').val(valMap[ui.value]);
			           //$('#savePolygonCoordinates').trigger('click');
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
			   	});	  // URL Parameter options
//				    if (getSearchParams("fullscreen") == "false") {
//				        $('.header').show();
//				        $('#mapid').attr('style', 'top:40px!important');
//				    	$('#viewHidePanels').val('false');
//				    } else {
//				    	$('#viewHidePanels').val('true');
//				  	  $('.header').hide();
//				      $('#mapid').attr('style', 'top:0px!important');
//				    }
//				    if (getSearchParams("clustering") == "false") {
//				        markerClusters.disableClustering();
//				        $('#disableClustering').val('false');
//				    } else {
//				        markerClusters.enableClustering();
//				        $('#enableClustering').val('true');
//				    }
	/*		     
			     //Switcher to turn fullscreen ON or OFF
			 	$('#switch .toggle').click(function() {
				    if ($(this).hasClass('off')) {
				    	console.log("NOT class off 2");
					  	  $('.header').hide();
					        $('#mapid').attr('style', 'top:0px!important');
					    	$('#viewHidePanels').val('true');

				    } else {
				        console.log("has class off 2");
					  	  $('.header').show();
					        $('#mapid').attr('style', 'top:40px!important');
				    	$('#viewHidePanels').val('false');
				    }
				});
//				$('#enableClustering').on('click', function(){
//					  markerClusters.enableClustering();
//						//updateCurrentFrozenZoom();
//				});
//				$('#disableClustering').on('click', function(){
//					  markerClusters.disableClustering();
//						//updateCurrentFrozenZoom();
//				});
			 	
				  // URL Parameter options
			    if (getSearchParams("clustering") == "false") {
			        markerClusters.disableClustering();
			        $('#disableClustering').val('false');
			        $('#switch2 .toggle').removeClass("off");
			    } else {
			        markerClusters.enableClustering();
			        $('#enableClustering').val('true');
			        $('#switch2 .toggle').removeClass("on");
			    }

			     //Switcher to turn Clustering Markers ON or OFF
			 	$('#switch2 .toggle').click(function() {
				    if ($(this).hasClass('off')) {
				    	console.log("enable clustering");
				        markerClusters.disableClustering();
				        $('#disableClustering').val('false');

				    } else {
				    	markerClusters.enableClustering();
				    	$('#disableClustering').val('true');
				        console.log("disable clustering");

				    }
				});
			     //Switcher to turn Private Maps ON or OFF
			 	$('#switch3 .toggle').click(function() {
				    if ($(this).hasClass('off')) {
				    	console.log("enable private map");
				    	$('#showPrivateMap').val('true');
				    } else {
				        console.log("disable private map");
				        $('#showPrivateMap').val('false');
				    }
				});

			 	
			    if (getSearchParams("graph") == "false") {
			        $('#showGraph').val('true');
			        $('#switch4 .toggle').removeClass("off");
			    } else {
			    	$('#showGraph').val('false');
			        $('#switch4 .toggle').removeClass("on");
			    }
			    
			     //Switcher to turn Graph Duplicate Attributes ON or OFF
			 	$('#switch4 .toggle').click(function() {
				    if ($(this).hasClass('off')) {
				    	console.log("enable graph");
				    	$('#showGraph').val('true');
				    	$('#savePolygonCoordinates').trigger('click');
				    } else {
				        console.log("disable graph");
				        $('#showGraph').val('false');

				    }
				});
			 	*/
			 	
				$('#mapTypeId').change(function() {
					  $('#mapType').val(this.value);
					});
		} // END only when loading leaflet maps
	  
	  // Helpful message when cursor is over - labeled in bootstrap
	    $('[data-toggle="tooltip"]').tooltip();



		 // console.log("fullscreen11 is "+getSearchParams("fullscreen"));



//	    $('.zoomIn').on('click', function () {
//	        map2.setZoom(map2.getZoom() + 1);
//	    });
//
//	    $('.zoomOut').on('click', function () {
//	        map2.setZoom(map2.getZoom() - 1);
//	    });
	    
//	    L.DomEvent.addListener(element, 'click', function (e) {
//	        console.log('Got clicked:', e)
//	    });

	    
	    

	//  $('#sidebarCollapse').on('click', function () {
//	      $('#sidebar').addClass('active');
//	      //$('.overlay').addClass('active'); // Only for mobilephones
//	      $('.collapse.in').toggleClass('in');
//	      $('a[aria-expanded=true]').attr('aria-expanded', 'false');
	//  });
	  
	  
	 // var getmap = map.gmap("get","map");
	  
	  //}
	  //L.tileLayer('https://2.base.maps.api.here.com/maptile/2.1/maptile/newest/normal.day/{z}/{x}/{y}/256/png8?app_id=DIMWjDXvm0l2iZDhQwLw;app_code=vJk4OzJfwR2tfUXA1TIc4g', {

	  
	  
	  
	  
//      $('#idInput2').val(id);
//      $('#showMarkerInfoBttn').trigger('click');
//  	  if ($('#sidebar').not('.active')) {
//  		  console.log("SHOWING SIDEBAR");
//  		      $('#sidebar').addClass('active');
//  		      $('.overlay').addClass('active'); // Only for mobilephones
//  			  var markerDetails = name;
	  
	  
//	  L.marker([51.5, -0.09]).addTo(map2)
//	      .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')
//	      .openPopup();
  
  // google async end
	  
	  
	  /*
  map = $('#map_canvas');
 
  map.gmap({
	'fullscreenControl': false,
    'panControl': false,
    //'mapTypeId': google.maps.MapTypeId.ROADMAP,
    //'styles': grayscaleMapType,
     'zoom': 3,
     'minZoom': 3,
     //'maxZoom': 18,
    'center': new google.maps.LatLng(12.072820115129355, 17.519712325),
    'zoo

  


//  initIds();
  initMarkers(true);
  initGeolocation();
  initDirection();
  

  




    

	
	
	  
  
//  $(window).resize(redrawchart);
//  redrawchart();
  
//  console.log("fullscreen is "+getSearchParams("fullscreen"));
//  if (getSearchParams("fullscreen") == "true") {
//
//	  console.log("iss fullscreen");
//  } else {
//	  console.log("not fullscreen");
//  }
  
  
  
//  $("#textsearch-autocomplete > ul").on('click','li',function (){
//	    alert($(this).text());
//	});
  
  

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

	  var retroMapType = new google.maps.StyledMapType(retro, styledMapOptions);
	  var ultraLightMapType = new google.maps.StyledMapType(light, styledMapOptions);
	  var grayscaleMapType = new google.maps.StyledMapType(grayscale, styledMapOptions);
	  var monotoneMapType = new google.maps.StyledMapType(monotone, styledMapOptions);
	  var natureMapType = new google.maps.StyledMapType(nature, styledMapOptions);
	  var colourfulMapType = new google.maps.StyledMapType(colourful, styledMapOptions);
	  var blueWaterMapType = new google.maps.StyledMapType(bluewater, styledMapOptions);
	  
	// call zoom control
	ZoomControl(getmap);
	getmap.mapTypes.set("Snow", whiteMapType);
	getmap.mapTypes.set("Route", routeMapType);
	getmap.mapTypes.set("Silver", silverMapType);
	getmap.mapTypes.set("Night", nightMapType);
	getmap.mapTypes.set("Retro", retroMapType);
	getmap.mapTypes.set("Light", ultraLightMapType);
	getmap.mapTypes.set("Grayscale", grayscaleMapType);
	getmap.mapTypes.set("Monotone", monotoneMapType);
	getmap.mapTypes.set("Nature", natureMapType);
	getmap.mapTypes.set("Colourful", colourfulMapType);
	getmap.mapTypes.set("BlueWater", blueWaterMapType);

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
          case "grayscale":
        	  getmap.setMapTypeId("Grayscale");
              break;
          case "satellite":
       	   		getmap.setMapTypeId(google.maps.MapTypeId.SATELLITE);
             break;
          case "terrain":
        	  getmap.setMapTypeId(google.maps.MapTypeId.TERRAIN);
              break;
          case "roadmap":
        	  getmap.setMapTypeId(google.maps.MapTypeId.ROADMAP);
              break;
          case "route":
        	  getmap.setMapTypeId("Route");
              break;
          case "silver":
        	  getmap.setMapTypeId("Silver");
              break;
          case "night":
        	  getmap.setMapTypeId("Night");
              break;
          case "retro":
        	  getmap.setMapTypeId("Retro");
              break;
          case "light":
        	  getmap.setMapTypeId("Light");
              break;
          case "snow":
        	  getmap.setMapTypeId("Snow");
              break;
          case "monotone":
        	  getmap.setMapTypeId("Monotone");
              break;
          case "nature":
        	  getmap.setMapTypeId("Nature");
              break;
          case "colourful":
        	  getmap.setMapTypeId("Colourful");
              break;
          case "bluewater":
        	  getmap.setMapTypeId("BlueWater");
              break;
      }
	}

	$('#mapTypeId').change(function () {
		var self = $(this);
		TypeIdChange(self.val());
	});

	google.maps.event.addDomListener(window, 'load', initialize);
  */
  
});
