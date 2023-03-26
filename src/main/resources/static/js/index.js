$( function() {
    var selectedCityOsmId = 0;
    var selectedStreetId = 0;

    var map = new Map();

    //-------- cities ----------
    $( "#cities" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/rest/cities",
                dataType: "json",
                data: {
                    q: request.term
                },
                success: function(data) {
                    response($.map(data, function(item) {
                        return {
                            id: item.osmId,
                            lat: item.lat,
                            lon: item.lon,
                            label: item.name,
                            value: item.name
                        }
                    }))
                }
            });
        },
        minlength: 2,
        select: function( event, ui ) {
            selectedCityOsmId =  ui.item.id;

            map.drawMarker(ui.item.lat, ui.item.lon, 12);
            console.log( ui.item ?
                "Selected: " + ui.item.label + " cityOsmId = " + ui.item.id :
                "Nothing selected, input was " + this.value);
        }
    });
    //---------- streets -------------------
    $( "#streets" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/rest/streets",
                dataType: "json",
                data: {
                    cityId: selectedCityOsmId,
                    q: request.term
                },
                success: function(data) {
                    response($.map(data, function(item) {
                        return {
                            id: item.id,
                            lat: item.lat,
                            lon: item.lon,
                            label: item.name,
                            value: item.name
                        }
                    }))
                }
            });
        },
        minlength: 2,
        select: function( event, ui ) {
            selectedStreetId = ui.item.id;
            map.drawMarker(ui.item.lat, ui.item.lon, 14);
            console.log( ui.item ?
                "Selected: " + ui.item.label + " id = " + ui.item.id + " streetId = " + ui.item.id :
                "Nothing selected, input was " + this.value);
        }
    });
    //---------- houses -------------------
    $( "#houses" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/rest/houses",
                dataType: "json",
                data: {
                    cityOsmId: selectedCityOsmId,
                    streetId: selectedStreetId,
                    q: request.term
                },
                success: function(data) {
                    response($.map(data, function(item) {
                        return {
                            id: item.osmId,
                            lat: item.lat,
                            lon: item.lon,
                            label: item.houseNumber,
                            value: item.houseNumber
                        }
                    }))
                }
            });
        },
        select: function( event, ui ) {
            map.drawMarker(ui.item.lat, ui.item.lon, 17);
            console.log( ui.item ?
                "Selected: " + ui.item.label + " id = " + ui.item.id + " cityOsmId = " + ui.item.cityOsmId + " admBoundaryOsmId = " + ui.item.admBoundaryOsmId :
                "Nothing selected, input was " + this.value);
        }
    });

    //---------- map -----------------
    map.init();

} );

function Map() {
    var markerLayer;
    var map;

    this.init = function() {
        map = L.map('mapid').setView([52.4606, -1.9006], 8);

        L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
            maxZoom: 18,
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                'Imagery © <a href="http://mapbox.com">Mapbox</a>',
            id: 'mapbox.streets'
        }).addTo(map);


    }

    this.drawMarker = function(lat, lon, zoom) {
        if ( !! markerLayer) {
            markerLayer.remove();
        }
        markerLayer = L.marker([lat, lon]);
        markerLayer.addTo(map)
        map.setView([lat, lon], zoom);
    }
}

