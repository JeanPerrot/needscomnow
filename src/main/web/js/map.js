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
            var rendering=false
            var latlng = new google.maps.LatLng(beginLat, beginLng);
            var options = {
                zoom: beginZoom,
                center: latlng,
                mapTypeId: google.maps.MapTypeId.ROADMAP,
            };
            var map = new google.maps.Map(document.getElementById('map'), options);
            var myParser = new geoXML3.parser({map: map, zoom:false,afterParse: function(){rendering=false;show()}
});
            var show=function(){
                for (var i=0;i<myParser.docs.length;i++){
                     myParser.showDocument(myParser.docs[i])
                }
            }
            var hide = function(){
                console.log("hiding "+myParser.docs.length+" documents")
                for (var i=0;i<myParser.docs.length;i++){
                     myParser.hideDocument(myParser.docs[i])
                }
            }
            var reload = function(){
                 if (rendering){
                    return
                 }
                 hide()
                 rendering=true
                 var center=map.getCenter()
                 var lat=center.lat()
                 var lng=center.lng()
                 var span=map.getBounds().toSpan()
                 var range=Math.max(span.lat(),span.lng())
                 myParser.parse('/?lat='+lat+'&lng='+lng+'&range='+range);
            }
            google.maps.event.addListener(map, 'idle', reload);
        }
})();