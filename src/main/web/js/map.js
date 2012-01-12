(function() {

        function get(name,def){
           if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
                return decodeURIComponent(name[1]);
           return def
        }

        var beginLat=get('lat',0)
        var beginLng=get('lng',0)
        var beginZoom=beginLat==0?2:11

        window.onload = function(){
            var latlng = new google.maps.LatLng(beginLat, beginLng);
            var options = {
                zoom: beginZoom,
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