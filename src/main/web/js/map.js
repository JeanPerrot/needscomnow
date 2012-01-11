(function() {
        window.onload = function(){
        var latlng = new google.maps.LatLng(45.31, -122.40);
        var options = {
            zoom: 2,
            center: latlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById('map'), options);
        var myParser = new geoXML3.parser({map: map, zoom:false});
        var clean = function(){
            for (var i=0;i<myParser.docs.length;i++){
                 myParser.hideDocument(myParser.docs[i])
            }
        }
        var reload = function(){
                                     clean()
                                     var center=map.getCenter()
                                     var lat=center.lat()
                                     var lng=center.lng()
                                     var span=map.getBounds().toSpan()
                                     var range=Math.max(span.lat(),span.lng())
                                     myParser.parse('/?lat='+lat+'&lng='+lng+'&range='+range);
        }
        google.maps.event.addListener(map, 'idle', reload);
        google.maps.event.addListener(map, 'zoom_changed', clean);
        google.maps.event.addListener(map, 'dragstart', clean);
        }
        })();