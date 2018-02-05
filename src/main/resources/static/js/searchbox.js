$( function() {
    var selectedAdmBoundaryOsmId = 0;
    var selectedCityOsmId = 0;
    var selectedStreetOsmId = 0;

    var map = new Map();

    //-------- cities ----------
    $( "#cities" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/rest/getCity.json",
                dataType: "json",
                data: {
                    q: request.term
                },
                success: function(data) {
                    response($.map(data, function(item) {
                        return {
                            id: item.cityOsmId + item.admBoundaryOsmId,
                            cityOsmId: item.cityOsmId,
                            admBoundaryOsmId: item.admBoundaryOsmId,
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
            selectedAdmBoundaryOsmId =  ui.item.admBoundaryOsmId;
            selectedCityOsmId =  ui.item.cityOsmId;

            map.drawMarker(ui.item.lat, ui.item.lon, 12);
            console.log( ui.item ?
                "Selected: " + ui.item.label + " id = " + ui.item.id + " cityOsmId = " + ui.item.cityOsmId + " admBoundaryOsmId = " + ui.item.admBoundaryOsmId :
                "Nothing selected, input was " + this.value);
        }
    });
    //---------- streets -------------------
    $( "#streets" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/rest/getStreets.json",
                dataType: "json",
                data: {
                    admBoundaryOsmId: selectedAdmBoundaryOsmId,
                    q: request.term
                },
                success: function(data) {
                    response($.map(data, function(item) {
                        return {
                            id: item.osmId,
                            //streetOsmId: item.streetOsmId,
                            //admBoundaryOsmId: item.admBoundaryOsmId,
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
            selectedStreetOsmId = ui.item.id;
            map.drawMarker(ui.item.lat, ui.item.lon, 14);
            console.log( ui.item ?
                "Selected: " + ui.item.label + " id = " + ui.item.id + " streetOsmId = " + ui.item.id :
                "Nothing selected, input was " + this.value);
        }
    });
    //---------- houses -------------------
    $( "#houses" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                url: "/rest/getHouses.json",
                dataType: "json",
                data: {
                    admBoundaryOsmId: selectedAdmBoundaryOsmId,
                    cityOsmId: selectedCityOsmId,
                    streetOsmId: selectedStreetOsmId,
                    q: request.term
                },
                success: function(data) {
                    response($.map(data, function(item) {
                        return {
                            id: item.osmId,
                            //cityOsmId: item.cityOsmId,
                            //admBoundaryOsmId: item.admBoundaryOsmId,
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
        map = L.map('mapid').setView([44.072, 44.495], 6);

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

