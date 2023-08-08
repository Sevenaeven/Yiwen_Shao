var localAuto =""

CheckBox = function (){
    if(document.getElementById('locationcheckbox').checked){
        document.getElementById('location').style.display = 'none';
        fetch("https://ipinfo.io/json?token=f8d287f84a5f3c").then(
            (response) => response.json()
        ).then(
            (jsonResponse) => localAuto = jsonResponse.loc
        )
    }else{
        document.getElementById('location').style.display = 'block';
    }
}

SearchSubmit = function() {

    var keywordcomplete = document.getElementById("keyword").reportValidity();
    var locationcomplete = false;
    if(document.getElementById('locationcheckbox').checked) {
        locationcomplete = true;
    }else{
        locationcomplete = document.getElementById("location").reportValidity();
        localAuto="";
    }

    var keyword = document.getElementById("keyword");
    var distance = document.getElementById("distance");
    var category = document.getElementById("category");
    var location = document.getElementById("location");


    if (distance.value == "") {
        distance.value = 10;
    }
    if (keywordcomplete && locationcomplete) {
        $.ajax({
            url: "/search",
            type: 'GET',
            data: {
                keyword: keyword.value,
                distance: distance.value,
                category: category.value,
                locationManu: location.value,
                localAuto: localAuto,

            },
            success: function (data) {
                if(document.getElementById('eventsearch').style.display == 'none') {
                    document.getElementById('eventsearch').style.display = 'block';
                }
                if(document.getElementById('eventdetail').style.display == 'block') {
                    document.getElementById('eventdetail').style.display = 'none';
                }
                if(document.getElementById('venuedetail').style.display == 'block') {
                    document.getElementById('venuedetail').style.display = 'none';
                }
                if(document.getElementById('showdetail').style.display == 'block') {
                    document.getElementById('showdetail').style.display = 'none';
                }
                if(document.getElementById('downarrow').style.display == 'block') {
                    document.getElementById('downarrow').style.display = 'none';
                }

                eventLength = Object.keys(data).length;
                var events = "";
                eventArray = DataToArray(data);
                if(eventLength == 0){
                    events = "<div id='norecord'><h3>No Records found</h3><div>"
                    eventhead.innerHTML=events;
                    eventbody.innerHTML="";
                }else{
                    events = "<tr><th>Date</th>";
                    events += "<th>Icon</th>";
                    events += "<th id ='eventname' data-sort='null' onclick='EventSort()'>Event</th>";
                    events += "<th id ='genre' data-sort='null' onclick='GenreSort()'>Genre</th>";
                    events += "<th id ='venue' data-sort='null' onclick='VenueSort()'>Venue</th>";
                    events += "</tr>";
                    eventhead.innerHTML=events;
                    EventsDisplay();
                }

            }
        })
    }
}

ClearSubmit = function (){
    document.getElementById("keyword").value = "";
    document.getElementById("distance").value = "";
    document.getElementById("category").value = "Default";
    document.getElementById("location").value = "";
    if(document.getElementById('locationcheckbox').checked){
        document.getElementById('locationcheckbox').checked = false;
        document.getElementById('location').style.display = 'block';
    }
    if(document.getElementById('eventsearch').style.display == 'block') {
        document.getElementById('eventsearch').style.display = 'none';
    }
    if(document.getElementById('eventdetail').style.display == 'block') {
        document.getElementById('eventdetail').style.display = 'none';
    }
    if(document.getElementById('showdetail').style.display == 'block') {
        document.getElementById('showdetail').style.display = 'none';
    }
    if(document.getElementById('downarrow').style.display == 'block') {
        document.getElementById('downarrow').style.display = 'none';
    }
    if(document.getElementById('venuedetail').style.display == 'block') {
         document.getElementById('venuedetail').style.display = 'none';
    }
}

EventSort = function(){
    var eventSortArr;
    if (document.getElementById('eventname').getAttribute('data-sort') == 'desc') {
        eventSortArr = eventArray.sort(Compare('Event',-1));
        EventsDisplay();
        document.getElementById('eventname').setAttribute('data-sort','asc');
    } else {
        eventArray = eventArray.sort(Compare('Event',1));
        EventsDisplay();
        document.getElementById('eventname').setAttribute('data-sort','desc');
    }
}

GenreSort = function(){
    var eventSortArr;
    if (document.getElementById('genre').getAttribute('data-sort') == 'desc') {
        eventSortArr = eventArray.sort(Compare('Genre',-1));
        EventsDisplay();
        document.getElementById('genre').setAttribute('data-sort','asc');
    } else {
        eventArray = eventArray.sort(Compare('Genre',1));
        EventsDisplay();
        document.getElementById('genre').setAttribute('data-sort','desc');
    }
}

VenueSort = function(){
    var eventSortArr;
    if (document.getElementById('venue').getAttribute('data-sort') == 'desc') {
        eventSortArr = eventArray.sort(Compare('Venue',-1));
        EventsDisplay();
        document.getElementById('venue').setAttribute('data-sort','asc');
    } else {
        eventArray = eventArray.sort(Compare('Venue',1));
        EventsDisplay();
        document.getElementById('venue').setAttribute('data-sort','desc');
    }
}

Compare = function (prop,type) {
    return function (obj1, obj2) {

        var val1 = obj1[prop];
        var val2 = obj2[prop];

        if (val1 < val2) {
            return 0-type;
        } else if (val1 > val2) {
            return type;
        } else {
            return 0;
        }
    }
}

DataToArray = function (data){
    var eventArr = [];

    for (var i = 0; i < eventLength; i++){
        eventArr.push(data[i]);
    }
    return eventArr;
}

EventsDisplay = function (){
    var eventDetail ="";
    var useLength = eventLength;
    for (var i = 0; i < useLength; i++) {
        var eventRow = eventArray[i];
        var eventID = eventRow['id'];
        var eventLocalDate = eventRow['LocalDate'];
        var eventLocalTime = eventRow['LocalTime'];
        var eventIcon = eventRow['Icon'];
        var eventEvent = eventRow['Event'];
        var eventGenre = eventRow['Genre'];
        var eventVenue = eventRow['Venue'];


        eventDetail +=
           "<tr>" +
            "<td>" + VisibleTime(eventLocalDate, eventLocalTime) + "</td>" +
            "<td>" + VisibleIcon(eventIcon)+ "</td>" +
            "<td>" + VisibleEvent(eventID,eventEvent)+"</td>" +
            "<td>" + VisbleGene(eventGenre) + "</td>" +
            "<td>" + VisbleGene(eventVenue) + "</td>" +
            "</tr>";
    }
    eventbody.innerHTML = eventDetail;
}


SearchDetail = function (data) {
    $.ajax({
        url: "/detail",
        type: 'GET',
        data: {
            eventID: data,
        },
        success: function (data) {
            var detailLeft = "";
            var detailRight = "";
            var artist = data['artist'];
            artist = ArtistNameAndUrl(artist);
            var artistTitile = data['artistTitle'];
            var detailSeatMap = data['detailSeatMap'];
            var detailGenre = data['detailGenre'];
            var detailLocalDate = data['detailLocalDate'];
            var detailLocalTime = data['detailLocalTime'];
            var detailPrice = data['detailPrice'];
            var detailTicketStatus = data['detailTicketStatus'];
            var detailTicketUrl = data['detailTicketUrl'];
            var detailVenue = data['detailVenue'];

            detailLeft = VisbleDate(detailLocalDate,detailLocalTime)
                + VisibleArtist(artist)
                + VisibleVenue(detailVenue)
                + VisiblePrice(detailPrice)
                + VisibleGenre(detailGenre)
                + VisibleTicketStatus(detailTicketStatus)
                + VisbleTicketUrl(detailTicketUrl);

            detailRight = VisbleMap(detailSeatMap);

            detailheadline.innerHTML = artistTitile;
            eventinfo.innerHTML = detailLeft;
            eventmap.innerHTML = detailRight;

            var status = document.getElementById('statuscolor');
            var color = status.innerText;
            switch (color) {
                case "On sale":
                    status.style.backgroundColor = "#1b9c29";
                    break;
                case "Off sale":
                    status.style.backgroundColor = "red";
                    break;
                case "Cancelled":
                    status.style.backgroundColor = "black";
                    break;
                case "Postponed":
                    status.style.backgroundColor = "orange";
                    break;
                case "Rescheduled":
                    status.style.backgroundColor = "orange";
                    break;
            }

            if(document.getElementById('eventdetail').style.display == 'none') {
                document.getElementById('eventdetail').style.display = 'block';
            }
            if(document.getElementById('showdetail').style.display == 'none') {
                document.getElementById('showdetail').style.display = 'block';
            }
            if(document.getElementById('downarrow').style.display == 'none') {
                document.getElementById('downarrow').style.display = 'block';
            }

            if(document.getElementById('venuedetail').style.display == 'block') {
                document.getElementById('venuedetail').style.display = 'none';
            }

            document.getElementById('eventdetail').scrollIntoView();

            SearchVenue = function () {
                $.ajax({
                    url: "/venue",
                    type: 'GET',
                    data: {
                        detailVenue: detailVenue,
                    },
                    success: function (data) {
                        if(Object.keys(data).length == 0){
                            if(document.getElementById('showdetail').style.display == 'block') {
                                document.getElementById('showdetail').style.display = 'none';
                            }
                            if(document.getElementById('downarrow').style.display == 'block') {
                                document.getElementById('downarrow').style.display = 'none';
                            }

                        }else{
                            var venueHead = "";
                            var venueLeft = "";
                            var venueRight = "";
                            var venueAddress = data['venueAddress'];
                            var venueCity = data['venueCity'];
                            var venueImage = data['venueImage'];
                            var venueName = data['venueName'];
                            var venuePS = data['venuePS'];
                            var venueUpcoming = data['venueUpcoming'];
                            var encodeName = data['encodeName']

                            venueHead ="<div>"+venueName+"</div>"+VisibleImage(venueImage);

                            venueLeft = VisibleAddress(venueAddress,venueCity,venuePS) + VisibleGoogle(encodeName);

                            venueRight = VisibleUpcoming(venueUpcoming);

                            venueheadline.innerHTML = venueHead;
                            venueinfo.innerHTML = venueLeft;
                            upcoming.innerHTML = venueRight;

                            if(document.getElementById('venuedetail').style.display == 'none') {
                                document.getElementById('venuedetail').style.display = 'block';
                            }
                            if(document.getElementById('showdetail').style.display == 'block') {
                                document.getElementById('showdetail').style.display = 'none';
                            }
                            if(document.getElementById('downarrow').style.display == 'block') {
                                document.getElementById('downarrow').style.display = 'none';
                            }


                            document.getElementById('venuedetail').scrollIntoView();
                        }
                    }
                })
            }
        }
    })
}



VisibleTime = function(data1,data2){
    if(data1 == "Undefined" && data2 == "Undefined"){
        return "";
    }else if(data1 != "Undefined" && data2 == "Undefined"){
        return data1;
    }else if(data1 == "Undefined" && data2 != "Undefined"){
        return data2;
    }else{
        return data1+"<br>"+data2;
    }
}

VisibleIcon = function (data){
    if(data == "Undefined"){
        return "";
    }else{
        return '<img src="' + data+ '" width="50px" height="30px"/>';
    }
}

VisibleEvent = function (eventID,eventEvent){
    if(eventEvent == "Undefined"){
        return "";
    }else{
        return "<a id='detailclick' href='javascript:void(0);' onclick=\"SearchDetail('"+eventID+"')\">" + eventEvent + "</a>"+"</td>";
    }
}

VisbleGene = function (data){
    if(data == "Undefined"){
        return "";
    }else{
        return data;
    }
}

ArtistNameAndUrl = function (data){
    var artistLength = Object.keys(data).length;
    var artist ="";
    if(data == "Undefined"){
        artist = "Undefined";
    }else{
        for (var i = 0; i < artistLength; i++) {
            var artistDetail = data[i];
            var artistName = artistDetail['artistName'];
            var artistUrl = artistDetail['artistUrl'];
            artist += "<div id='artistn'><a id='artisturl' target=\"_blank\" href="+artistUrl+">"+artistName+"</a></div>";
            if(i < artistLength-1){
                artist += "<div id='artistse'>|</div>";
            }
        }
    }
    return artist;
}

VisbleDate = function (data1,data2){
    if(data1 != "Undefined" && data2 != "Undefined"){
        return "<div id='date'>Date</div>" + "<br><div>" + data1 +" "+ data2 + "</div><br>";
    }else if(data1 == "Undefined" && data2 != "Undefined"){
        return "<div id='date'>Date</div>" + "<br><div>" + data2 + "</div><br>";
    }else if(data1 != "Undefined" && data2 == "Undefined") {
        return "<div id='date'>Date</div>" + "<br><div>" + data1 + "</div><br>";
    }else {
        return "";
    }

}
VisibleArtist = function (data){
    if(data == "Undefined"){
        return "";
    }else{
        return "<div id='team'>Artist/Team</div>" + "<br><div>" + data + "</div><br>";
    }
}

VisibleGenre = function (data){
    if(data == "Undefined"){
        return "";
    }else{
        return "<div id='genres'>Genres</div>" + "<br><div>" + data + "</div><br>";
    }
}


VisiblePrice = function(data){
    if(data == "Undefined"){
        return "";
    }else{
        return "<div id='price'>Price Ranges</div>"+"<br><div>"+data+"</div><br>";
    }
}

VisibleVenue = function (data){
    if(data == "Undefined"){
        return "";
    }else {
        return "<div id='venued'>Venue</div>" + "<br><div>" + data + "</div><br>";
    }
}

VisibleTicketStatus= function (data){
    if(data == "Undefined"){
        return "";
    }else {
        if(data == "onsale"){
            data = "On sale"
        }else if(data == "offsale"){
            data = "Off sale"
        }else if(data == "cancelled"){
            data = "Cancelled"
        }else if(data == "postponed"){
            data = "Postponed"
        }else if(data == "rescheduled") {
            data = "Rescheduled"
        }

        return "<div id='ticketstatus'>Ticket Status</div>" + "<br><div id='statuscolor'>" + data + "</div><br>";
    }
}


VisbleTicketUrl = function (data){
    if(data == "Undefined"){
        return "";
    }else {
        return "<div id='buyticket'>Buy Ticket At:</div>" + "<br><div>" + "<a target=\"_blank\" id='ticketurl' href='" + data + "'>Ticketmaster</a>" + "</div><br>";
    }
}

VisbleMap = function (data){
    if(data == "Undefined"){
        return "";
    }else {
        return "<img id='detailmap' src='" + data + "'/>";
    }
}



VisibleImage = function(data){
    if(data == "Undefined"){
        return "";
    }else{
        return "<img id='image' src='"+data+"'/>";

    }
}

VisibleAddress = function(venueAddress,venueCity,venuePS){
    if(venueAddress == "Undefined" && venueCity== "Undefined" && venuePS== "Undefined"){
        return "";
    }else{
        if(venueAddress == "Undefined"){
            venueAddress = "";
        }else{
            venueAddress = venueAddress + "<br>";
        }

        if(venueCity == "Undefined"){
            venueCity = "";
        }else{
            venueCity = venueCity + "<br>";
        }

        if(venuePS == "Undefined"){
            venuePS = "";
        }else{
            venuePS = venuePS + "<br>";
        }
        return "<div id='address'>Address: </div>" + "<div id='addressdetail'>" + venueAddress + venueCity + venuePS +"</div>";
    }
}

VisibleGoogle = function (encodeName) {
    if (encodeName == "Undefined") {
        return "";
    } else {
        return "<div id='googlemap'><a id='googleurl' target=\"_blank\" href='https://www.google.com/maps/search/?api=1&query=" + encodeName + "'>Open in Google Maps</a></div>";
    }
}

VisibleUpcoming = function (venueUpcoming) {
    if (venueUpcoming == "Undefined") {
        return "";
    } else {
        return "<div id='upcomingbox'><a id='upcomingurl' target=\"_blank\" href='" + venueUpcoming + "'> More events at this venue</a></div>";
    }
}