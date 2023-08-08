const cors = require('cors');
const bodyParser = require('body-parser');
const axios = require('axios');
const { map } = Array.prototype;
const ngeohash = require('ngeohash');
const SpotifyWebApi = require('spotify-web-api-node');

global.spotifyApi = new SpotifyWebApi({
  clientId: 'be4be9704ce9488f85d7d8bccfce2bd1',
  clientSecret: '8881465af16b4f54a61bfa52c3e052ef'
});

AccessTocken = function(){

  global.spotifyApi.clientCredentialsGrant().
  then(function(result) {
    var accessToken= result.body.access_token; 
    global.spotifyApi.setAccessToken(accessToken);
  }).catch((e) => console.log(e));
  
}

AccessTocken();

var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

const { copyFileSync } = require('fs');


var app = express();
app.use(cors());
app.use(bodyParser.json());


app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use((req, res, next) => {
  res.setHeader('Cache-Control', 'no-cache, no-store, must-revalidate');
  next();
});

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.get('/search', (req, res) => {
  
  var keyword = req.query.keyword;
  var distance = req.query.distance;
  var category = req.query.category;
  var location = req.query.location.split(",");
  var lat = location[0];
  var lng = location[1];
  var locationGeohash = ngeohash.encode(lat,lng);

  var searchResult=[];
/*
  var categoryNum;
  if (category == "Music"){
    categoryNum = "KZFzniwnSyZfZ7v7nJ";
  }else if(category == "Sports"){
    categoryNum = "KZFzniwnSyZfZ7v7nE";
  }else if(category == "Arts & Theatre"){
    categoryNum = "KZFzniwnSyZfZ7v7na";
  }else if(category == "Film"){
    categoryNum = "KZFzniwnSyZfZ7v7nn";
  }else if(category == "Miscellaneous"){
    categoryNum = "KZFzniwnSyZfZ7v7n1";
  }else if(category == "Default"){
    categoryNum = "";
  }
*/

//change for hw9
  var categoryNum;
  if (category == "Music"){
    categoryNum = "KZFzniwnSyZfZ7v7nJ";
  }else if(category == "Sports"){
    categoryNum = "KZFzniwnSyZfZ7v7nE";
  }else if(category == "Arts & Theatre"){
    categoryNum = "KZFzniwnSyZfZ7v7na";
  }else if(category == "Film"){
    categoryNum = "KZFzniwnSyZfZ7v7nn";
  }else if(category == "Miscellaneous"){
    categoryNum = "KZFzniwnSyZfZ7v7n1";
  }else if(category == "All"){
    categoryNum = "";
  }
//

  axios.get('https://app.ticketmaster.com/discovery/v2/events.json?keyword=' + keyword + '&radius=' + distance + '&unit=miles&segmentId=' + categoryNum + '&geoPoint=' + locationGeohash + '&apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist')
  .then(response => {
    var response_json = response.data;
    if('_embedded' in response_json){
      var events = response_json['_embedded'].events;
      searchResult = FindAllEvent(events);
      res.json(searchResult);
    }else{
      res.json(searchResult);
    }
  })
  .catch(error => {
    console.log(error);
  });
});

app.get('/detail', (req, res) => {
  var eventID = req.query.id;

  axios.get('https://app.ticketmaster.com/discovery/v2/events/' + eventID + '.json?apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist')
  .then(response => {
    var eventDetail_json = response.data;
    var detailResult = FindAllDetail(eventDetail_json,eventID);
    res.json(detailResult);
  })
  .catch(error => {
    console.log(error);
  });

});

app.get('/team', (req, res) => {
  AccessTocken();

  var teams = req.query.team;

  if(teams == ""){
    res.json({id:"undefined"});
  }else{
    global.spotifyApi.searchArtists(teams)
    .then(data => {
      var oneArtist = data.body.artists.items[0];
      var artistId = oneArtist.id;
      global.spotifyApi.getArtistAlbums(artistId,{limit:3})
        .then(response =>{
          oneArtist['album'] = response.body.items;
          res.json(oneArtist); 
        })
        .catch(error => {
          console.error(error);
        })      
    })
    .catch(error => {
      console.error(error);
    });
    
  }
});

app.get('/venueName', (req, res) => {

  var venue = req.query.venue;

  axios.get("https://app.ticketmaster.com/discovery/v2/venues.json?keyword="+venue+"&apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist")
  .then(response => {
    var venue_json = response.data;
    res.json(FindVenueDetail(venue_json));
  })
  .catch(error => {
    console.log(error);
  });
});

app.get('/autoSearch', (req,res) => {

  var value = req.query.search;
  axios.get("https://app.ticketmaster.com/discovery/v2/suggest?apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist&keyword="+value)
  .then(response => {
    if("_embedded" in response.data){
      res.json(response.data._embedded.attractions);
    }
  })
  .catch(error => {
    console.log(error);
  });

});


FindVenueDetail = function(venue_json){
  
  var venueDetail = venue_json._embedded.venues[0];
  var venueAll = {};

  var venueName = venueDetail.name;
  venueAll['venueName'] =venueName;

  var venueAddress;
  if('address' in venueDetail){
    if('line1' in venueDetail.address){
      venueAddress = ChangeNullData(venueDetail.address.line1);
    }else{
      venueAddress="";
    }
  }else{
    venueAddress="";
  }

  if('location' in venueDetail){
    venueAll['longitude'] = venueDetail.location.longitude;
    venueAll['latitude'] = venueDetail.location.latitude;
  }else{
    venueAll['longitude'] = "";
    venueAll['latitude'] = "";
  }

  var city; 
  if('city' in venueDetail){
    if('name' in venueDetail.city){
      city = ChangeNullData(venueDetail.city.name);
    }else{
      city = "";
    }
  }else{
    city = "";
  }

  var state; 
  if('state' in venueDetail){
    if('name' in venueDetail.state){
      state = ChangeNullData(venueDetail.state.name);
    }else{
      state = "";
    }
  }else{
    state = "";
  }

  var cityName;
  if(state == "" && city == ""){
    cityName = "";
  }else if(state == "" || city == ""){
    cityName = city + state;
  }else{
    cityName = city +", " +state;
  }
/*
  var address;
  if(cityName == "" || venueAddress == ""){
    address = venueAddress + cityName;
  }else{
    address = venueAddress + ", " + cityName;
  }

  venueAll['address'] = address;
*/
  //change for hw9
  venueAll['venueAddress'] = venueAddress;
  venueAll['cityName'] = cityName;

  var phone;
  var openHoure;
  if('boxOfficeInfo' in venueDetail){
    var officeInfo = venueDetail.boxOfficeInfo;
    phone = officeInfo.phoneNumberDetail;
    openHoure = officeInfo.openHoursDetail;
  }else{
    phone = "";
    openHoure = "";
  }

  venueAll['phone'] = phone;
  venueAll['openHoure'] = openHoure;

  var childRule;
  var generalRule;
  if("generalInfo" in venueDetail){
    var generalInfo = venueDetail.generalInfo;
    if("childRule" in generalInfo){
      childRule = generalInfo.childRule;
    }else{
      childRule = "";
    }

    if("generalRule" in generalInfo){
      generalRule = generalInfo.generalRule;
    }else{
      generalRule = "";
    }
    
  }else{
    childRule = "";
    generalRule = "";
  }

  venueAll['childRule'] = childRule;
  venueAll['generalRule'] = generalRule;

  return venueAll;


}


FindAllDetail = function(eventDetail_json,eventID){
  var detailAll = {};

  detailAll['id'] = eventID;

  var detailName = eventDetail_json.name;
  detailAll['detailname'] = detailName;

  if('dates' in eventDetail_json){

    if('start' in eventDetail_json.dates){

      if('localDate' in eventDetail_json.dates.start){
        var detailLocalDate = eventDetail_json.dates.start.localDate;
        detailLocalDate = ChangeNullData(detailLocalDate);
        detailAll['detailLocalDate'] = detailLocalDate;
      }else{
        detailAll['detailLocalDate'] = "";
      }

      if('localTime' in eventDetail_json.dates.start){
        var detaillocalTime = eventDetail_json.dates.start.localTime;
        detaillocalTime = ChangeNullData(detaillocalTime);
        detailAll['detailLocalTime'] = detaillocalTime;
      }else{
        detailAll['detailLocalTime'] = "";
      }

    }else{
      detailAll['detailLocalDate'] = "";
      detailAll['detailLocalTime'] = "";
    }

    if('status' in eventDetail_json.dates){

      if('code' in eventDetail_json.dates.status){
        var detailTicketStatus = eventDetail_json.dates.status.code;
        detailTicketStatus = ChangeNullData(detailTicketStatus);
        detailAll['detailTicketStatus'] = detailTicketStatus;
      }else{
        detailAll['detailTicketStatus'] = "";
      }

    }else{
      detailAll['detailTicketStatus'] = "";
    }

  }else{
    detailAll['detailLocalDate'] = "";
    detailAll['detailLocalTime'] = "";
    detailAll['detailTicketStatus'] = "";
  }

  if('attractions' in eventDetail_json._embedded){
    var detailArtists = eventDetail_json._embedded.attractions;
    var artistTitle = "";
    var artistList = [];
    for(var i=0 ; i<detailArtists.length ; i++){
      var artistJson = {};
      var artist = detailArtists[i];
      artistTitle = artistTitle + artist.name + " | ";
      var musicRelated = detailArtists[i].classifications[0].segment.name;
      artistJson['artistName'] = artist.name;
      artistJson['musicRelated'] = musicRelated;
      artistList.push(artistJson);
    }

    artistTitle = artistTitle.trim();
    artistTitle = artistTitle.replace(/^\||\|$/g, "");
    artistTitle = artistTitle.trim();
    detailAll['artistTitle'] = artistTitle;
    detailAll['artistList'] = artistList;

  }else{
    detailAll['artistTitle'] = "";
    detailAll['artistList'] = "";
  }

  if('venues' in eventDetail_json._embedded){
    var detailVenue = eventDetail_json._embedded.venues[0].name;
    detailAll['detailVenue'] = detailVenue;    
  }else{
    detailAll['detailVenue'] = "";
  }

  var detailGenre = "";
  if('classifications' in eventDetail_json){
    var detailClass = eventDetail_json.classifications[0];
    if('subGenre' in detailClass){
      var subGenre = detailClass.subGenre.name;
      detailGenre += CheckType(subGenre);
    }
    if('genre' in detailClass){
      var genre = detailClass.genre.name;
      detailGenre += CheckType(genre)
    }
    if('segment' in detailClass){
      var segment = detailClass.segment.name;
      detailGenre += CheckType(segment);
    }
    if('subType' in detailClass){
      var subType = detailClass.subType.name;
      detailGenre += CheckType(subType);
    }
    if('type' in detailClass){
      var type = detailClass.type.name;
      detailGenre += CheckType(type);
    }
  }

  if(detailGenre != ""){
    detailGenre = detailGenre.trim();
    detailGenre = detailGenre.replace(/^\||\|$/g, "");
    detailGenre = detailGenre.trim();
    detailAll['detailGenre'] = detailGenre;
  }else{
    detailAll['detailGenre'] = "";
  }

  var detailPrice = "";
  if('priceRanges' in eventDetail_json){
    var price = eventDetail_json.priceRanges[0];
    var detailPriceMin;
    var detailPriceMax;
    var currency;

    if('min' in price){
      detailPriceMin = price.min;
      detailPriceMin = CheckPrice(detailPriceMin);
    }else{
      detailPriceMin = ""
    }
    if('max' in price){
      detailPriceMax = price.max;
      detailPriceMax = CheckPrice(detailPriceMax);
    }else{
      detailPriceMax = ""
    }

    if('currency' in price){
      currency = price.currency;
      detailAll['currency'] = currency;
    }else{
      detailAll['currency'] = "";
    }

    if(detailPriceMin == "" && detailPriceMax == ""){
      detailAll['detailPrice'] = "";
    }else if(detailPriceMin == "" && detailPriceMax != ""){
      detailPriceMin = detailPriceMax;
      detailPrice = detailPriceMin + "-" + detailPriceMax;
      detailAll['detailPrice'] = detailPrice;
    }else if(detailPriceMax == "" && detailPriceMin != ""){
      detailPriceMax = detailPriceMin;
      detailPrice = detailPriceMin + "-" + detailPriceMax;
      detailAll['detailPrice'] = detailPrice;
    }else{
      detailPrice = detailPriceMin + "-" + detailPriceMax;
      detailAll['detailPrice'] = detailPrice;
    }
  }else{
    detailAll['detailPrice'] = "";
    detailAll['currency'] = "";
  }

  if('url' in eventDetail_json){
    var detailTicketUrl = eventDetail_json.url;
    detailAll['detailTicketUrl'] = detailTicketUrl;
  }else{
    detailAll['detailTicketUrl'] = "";
  }

  if('seatmap' in eventDetail_json){
    var detailSeatMap = eventDetail_json.seatmap.staticUrl;
    detailAll['detailSeatMap'] = detailSeatMap;
  }else{
    detailAll['detailSeatMap'] = "";
  }

  return detailAll;

}

CheckPrice = function(data){
  if(data == "Undefined" || data == null || data == "undefined"){
    return "";
  }else{
    return data;
  }

}

CheckType = function(data){
  if(data == "" || data == "Undefined" || data == null || data == "undefined"){
    return "";
  }else{
    return data + " | ";
  }
}

FindAllEvent = function(events){
  var eventAll = [];
  var length = events.length;
  var eachEvent;

  if(length > 20){
    length = 20;
  }

  for(var i=0 ; i<length ; i++){
    eachEvent = events[i];

    var localDate;
    var localTime;
    if('dates' in eachEvent){
      if('start' in eachEvent.dates){

        var eventStart = eachEvent.dates.start;
        if('localDate' in eventStart){
          localDate = ChangeNullData(eventStart.localDate);
        }else{
          localDate = "";
        }

        if('localTime' in eventStart){
          localTime = ChangeNullData(eventStart.localTime);
        }else{
          localTime = "";
        }
      }else{
        localDate = "";
        localTime = "";
      }
    }else{
      localDate = "";
      localTime = "";
    }

    var eventIcon;
    if('images' in eachEvent){
      var eventIconAll = eachEvent.images;
      eventIcon = ChangeNullData(eventIconAll[0].url);
    }else{
      eventIcon = "";
    }

    var eventName;
    if('name' in eachEvent){
      eventName = ChangeNullData(eachEvent.name);
    }else{
      eventName = "";
    }

    var eventGenre;
    if('classifications' in eachEvent){
      var eventClass = eachEvent.classifications;
      var eventGenre1 = eventClass[0];
      if('segment' in eventGenre1){
        var eventGenre2 = eventGenre1.segment;
        if('name' in eventGenre2){
          eventGenre = ChangeNullData(eventGenre2.name);
        }else{
          eventGenre = "";
        }
      }else{
        eventGenre = "";
      }
    }else{
      eventGenre = "";
    }

    var eventVenueName;
    if('venues' in eachEvent['_embedded']){
      var eventVenue = eachEvent['_embedded'].venues;
      eventVenueName = ChangeNullData(eventVenue[0].name);
    }else{
      eventVenueName = "";
    }

    var eventID;
    if('id' in eachEvent){
      eventID = ChangeNullData(eachEvent.id);
    }else{
      eventID = "";
    }

    if(localDate == "" && localTime == "" && eventIcon == "" && eventName == "" && eventGenre == "" && eventVenueName == ""){
      continue;
    }else{
      var eventInfo = {};
      eventInfo['LocalDate'] = localDate;
      eventInfo['LocalTime'] = localTime;
      eventInfo['Icon']= eventIcon;
      eventInfo['Event']= eventName;
      eventInfo['Genre']= eventGenre;
      eventInfo['Venue']= eventVenueName;
      eventInfo['id']= eventID;
      eventAll[i] = eventInfo;
    }
  }

  return eventAll;
}

ChangeNullData = function(data){
  if(data == null || data == "Undefined" || data == "undefined"){
    return "";
  }else{
    return data.trim();
  }
}


app.get('/', (req, res) => {
  res.send('Hello, world!')
})

//app.use(express.static('client'));
module.exports = app;
