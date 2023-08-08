from flask import Flask,request,jsonify
import requests
import json
from geolib import geohash


app = Flask(__name__)



@app.route("/")
def home():
    return app.send_static_file("event.html")

@app.route("/search")
def EventSearch():
    keyword = str(request.args.get('keyword'))
    distance = str(request.args.get('distance'))
    category = str(request.args.get('category'))
    locationManu = str(request.args.get('locationManu'))
    localAuto = str(request.args.get('localAuto'))
    locationGeohash =""
    if localAuto != "":
        locationGeohash = GeoLocationAuto(localAuto)
        keyword = keyword.replace(' ', '+')
        keyword = keyword.replace(',', '+')
        keyword = keyword.replace('.', '')

        categoryNum = ""
        if category == "Music":
            categoryNum = "KZFzniwnSyZfZ7v7nJ"
        elif category == "Sports":
            categoryNum = "KZFzniwnSyZfZ7v7nE"
        elif category == "Arts & Theatre":
            categoryNum = "KZFzniwnSyZfZ7v7na"
        elif category == "Film":
            categoryNum = "KZFzniwnSyZfZ7v7nn"
        elif category == "Miscellaneous":
            categoryNum = "KZFzniwnSyZfZ7v7n1"
        elif category == "Default":
            categoryNum = ""

        eventSearch = requests.get(
            "https://app.ticketmaster.com/discovery/v2/events.json?keyword=" + keyword + "&radius=" + distance + "&unit=miles&segmentId=" + categoryNum + "&geoPoint=" + locationGeohash + "&apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist")
        eventSearch_json = eventSearch.json()
        events = dict()
        if '_embedded' in eventSearch_json.keys():
            events = eventSearch_json['_embedded'].get('events')
            eventAll = FindAllEvents(events)
            eventAll = jsonify(eventAll)
            return eventAll
        else:
            return jsonify(events)
    else:
        locationGeohash = GeoLocationManu(locationManu)
        if locationGeohash == "Undefined":
            return jsonify(dict())
        else:
            keyword = keyword.replace(' ', '+')
            keyword = keyword.replace(',', '+')
            keyword = keyword.replace('.', '')

            categoryNum = ""
            if category == "Music":
                categoryNum = "KZFzniwnSyZfZ7v7nJ"
            elif category == "Sports":
                categoryNum = "KZFzniwnSyZfZ7v7nE"
            elif category == "Arts & Theatre":
                categoryNum = "KZFzniwnSyZfZ7v7na"
            elif category == "Film":
                categoryNum = "KZFzniwnSyZfZ7v7nn"
            elif category == "Miscellaneous":
                categoryNum = "KZFzniwnSyZfZ7v7n1"
            elif category == "Default":
                categoryNum = ""

            eventSearch = requests.get(
            "https://app.ticketmaster.com/discovery/v2/events.json?keyword="+keyword+"&radius="+distance+"&unit=miles&segmentId="+categoryNum+"&geoPoint="+locationGeohash+"&apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist")
            eventSearch_json = eventSearch.json()
            events = dict()
            if '_embedded' in eventSearch_json.keys():
                events = eventSearch_json['_embedded'].get('events')
                eventAll = FindAllEvents(events)
                eventAll = jsonify(eventAll)
                return eventAll
            else:
                return jsonify(events)


def FindAllEvents(events):
    eventAll = dict()
    a = 0
    for event in events:
        if a==20 :
           break
        if 'dates' in  event.keys():
            if 'start' in event.get('dates').keys():
                eventStart = event.get('dates').get('start')
                if 'localDate' in eventStart.keys():
                    localDate = ChangeNullData(eventStart.get('localDate'))
                else:
                    localDate = "Undefined"

                if 'localTime' in eventStart.keys():
                    localTime = ChangeNullData(eventStart.get('localTime'))
                else:
                    localTime = "Undefined"
            else:
                localDate = "Undefined"
                localTime = "Undefined"
        else:
            localDate = "Undefined"
            localTime = "Undefined"

        if 'images' in event.keys():
            eventIconAll = event.get('images')
            eventIcon = ChangeNullData(eventIconAll[0]['url'])
        else:
            eventIcon = "Undefined"

        if 'name' in event.keys():
            eventName = ChangeNullData(event.get('name'))
        else:
            eventName = "Undefined"

        if 'classifications' in event.keys():
            eventClass = event.get('classifications')
            eventGenre1 = eventClass[0]
            if 'segment' in eventGenre1.keys():
                eventGenre2 = eventGenre1.get('segment')
                if 'name' in eventGenre2.keys():
                    eventGenre = ChangeNullData(eventGenre2.get('name'))
                else:
                    eventGenre = "Undefined"
            else:
                eventGenre = "Undefined"
        else:
            eventGenre = "Undefined"

        if 'venues' in event.get('_embedded').keys():
            eventVenue = event.get('_embedded').get('venues')
            eventVenueName = ChangeNullData(eventVenue[0].get('name'))
        else:
            eventVenueName = "Undefined"

        if 'id' in event.keys():
            eventID = ChangeNullData(event.get('id'))
        else:
            eventID = "Undefined"

        if localDate == "Undefined" and localTime == "Undefined" and eventIcon == "Undefined" and eventName == "Undefined" and eventGenre == "Undefined" and eventVenueName == "Undefined":
             continue
        else:
            eventInfo = dict()
            eventInfo['LocalDate'] = localDate
            eventInfo['LocalTime'] = localTime
            eventInfo['Icon']= eventIcon
            eventInfo['Event']= eventName
            eventInfo['Genre']= eventGenre
            eventInfo['Venue']= eventVenueName
            eventInfo['id']= eventID
            eventAll[a] = eventInfo
            a=a+1

    return eventAll


def GeoLocationManu(location):
    locationPlus = location.replace(' ','+')
    locationPlus = locationPlus.replace(',','+')
    locationPlus = locationPlus.replace('.','')
    locationInfo = requests.get("https://maps.googleapis.com/maps/api/geocode/json?address="+locationPlus+"&key=AIzaSyDqkDt5Z3gLGpSnsx35ZnBQx3wM1tte6eM")
    locationInfo_json = locationInfo.json()
    locationResult = locationInfo_json['results']
    if locationResult == []:
        locationGeohash = "Undefined"
    else:
        locationGeo= locationResult[0].get('geometry')
        latitude = locationGeo.get('location').get('lat')
        longitude = locationGeo.get('location').get('lng')
        locationGeohash = geohash.encode(latitude,longitude,9)

    return locationGeohash

def GeoLocationAuto(location):
    location = location.split(',')
    latitude = location[0]
    longitude = location[1]
    locationGeohash = geohash.encode(latitude, longitude, 9)
    return locationGeohash

def CheckType(data):
    if data == None or data == "Undefined" or data == "null":
        return ""
    else:
        return data + "|"

def CheckPrice(data):
    if data == None or data == "Undefined" or data == "null":
        return ""
    else:
        return data

def ChangeNullData(data):
    if data == None or data == "Undefined" or data == "null":
        return "Undefined"
    else:
        return data

@app.route("/detail")
def EventDeatil():
    eventID = str(request.args.get('eventID'))
    eventDetail = requests.get(
        "https://app.ticketmaster.com/discovery/v2/events/" + eventID + "?apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist")
    eventDetail_json = eventDetail.json()

    detailAll = dict()

    if 'dates' in eventDetail_json.keys():
        if 'start' in eventDetail_json.get('dates').keys():
            if 'localDate' in eventDetail_json.get('dates').get('start').keys():
                detailLocalDate = eventDetail_json.get('dates').get('start').get('localDate')
                detailLocalDate = ChangeNullData(detailLocalDate)
                detailAll['detailLocalDate'] = detailLocalDate
            else:
                detailAll['detailLocalDate'] = "Undefined"

            if 'localTime' in eventDetail_json.get('dates').get('start').keys():
                detailLocalTime = eventDetail_json.get('dates').get('start').get('localTime')
                detailLocalTime = ChangeNullData(detailLocalTime)
                detailAll['detailLocalTime'] = detailLocalTime
            else:
                detailAll['detailLocalTime'] = "Undefined"
        else:
            detailAll['detailLocalDate'] = "Undefined"
            detailAll['detailLocalTime'] = "Undefined"

        if 'status' in eventDetail_json.get('dates').keys():
            if 'code' in eventDetail_json.get('dates').get('status').keys():
                detailTicketStatus = eventDetail_json.get('dates').get('status').get('code')
                detailTicketStatus = ChangeNullData(detailTicketStatus)
                detailAll['detailTicketStatus'] = detailTicketStatus
            else:
                detailAll['detailTicketStatus'] = "Undefined"
        else:
            detailAll['detailTicketStatus'] = "Undefined"
    else:
        detailAll['detailLocalDate'] = "Undefined"
        detailAll['detailLocalTime'] = "Undefined"
        detailAll['detailTicketStatus'] = "Undefined"

    if 'attractions' in eventDetail_json.get('_embedded').keys():
        detailArtists = eventDetail_json.get('_embedded').get('attractions')
        artistTitle = ""
        for artist in detailArtists:
            artistTitle += artist.get('name')
            artistTitle += "|"
        artistTitle = artistTitle.strip("|")
        detailAll['artistTitle'] = artistTitle

        artists = dict()
        order = 0
        for artist in detailArtists:
            artistPair = dict()
            artistName = artist.get('name')
            if 'url' in artist.keys():
                artistUrl = artist.get('url')
            else:
                artistUrl = "Undefined"
            artistPair['artistName'] = artistName
            artistPair['artistUrl'] = artistUrl
            artists[order] = artistPair
            order += 1
        detailAll['artist'] = artists
    else:
        detailAll['artist'] = "Undefined"
        detailAll['artistTitle'] = eventDetail_json.get('name')

    if 'venues' in eventDetail_json.get('_embedded').keys():
        detailVenue = eventDetail_json.get('_embedded').get('venues')[0].get('name')
        detailAll['detailVenue'] = detailVenue
    else:
        detailAll['detailVenue'] = "Undefined"

    detailGenre = ""
    if 'classifications' in eventDetail_json.keys():
        detailClass = eventDetail_json.get('classifications')[0]
        if 'subGenre' in detailClass.keys():
            subGenre = detailClass.get('subGenre').get('name')
            detailGenre += CheckType(subGenre)
        if 'genre' in detailClass.keys():
            genre = detailClass.get('genre').get('name')
            detailGenre += CheckType(genre)
        if 'segment' in detailClass.keys():
            segment = detailClass.get('segment').get('name')
            detailGenre += CheckType(segment)
        if 'subType' in detailClass.keys():
            subType = detailClass.get('subType').get('name')
            detailGenre += CheckType(subType)
        if 'type' in detailClass.keys():
            type = detailClass.get('type').get('name')
            detailGenre += CheckType(type)
    if detailGenre != "":
        detailGenre = detailGenre.strip("|")
        detailAll['detailGenre'] = detailGenre
    else:
        detailAll['detailGenre'] = "Undefined"

    detailPrice = ""
    if 'priceRanges' in eventDetail_json.keys():
        price = eventDetail_json.get('priceRanges')[0]
        if 'min' in price.keys():
            detailPriceMin = price.get('min')
            detailPriceMin = CheckPrice(detailPriceMin)
        else:
            detailPriceMin = ""
        if 'max' in price.keys():
            detailPriceMax = price.get('max')
            detailPriceMax = CheckPrice(detailPriceMax)
        else:
            detailPriceMax = ""

        if 'currency' in price.keys():
            detailPriceCurr = price.get('currency')
            detailPriceCurr = CheckPrice(detailPriceCurr)
        else:
            detailPriceCurr = ""

        if detailPriceMin == "" and detailPriceMax == "":
            detailAll['detailPrice'] = "Undefined"
        elif detailPriceMin == "" and detailPriceMax != "":
            detailPriceMin = detailPriceMax
            detailPrice = str(detailPriceMin) + "-" + str(detailPriceMax)
            if detailPriceCurr == "":
                detailPrice += ""
                detailAll['detailPrice'] = detailPrice
            else:
                detailPrice += " " + detailPriceCurr
                detailAll['detailPrice'] = detailPrice
        elif detailPriceMax == "" and detailPriceMin != "":
            detailPriceMax = detailPriceMin
            detailPrice = str(detailPriceMin) + "-" + str(detailPriceMax)
            if detailPriceCurr == "":
                detailPrice += ""
                detailAll['detailPrice'] = detailPrice
            else:
                detailPrice += " " + detailPriceCurr
                detailAll['detailPrice'] = detailPrice
        else:
            detailPrice = str(detailPriceMin) + "-" + str(detailPriceMax)
            if detailPriceCurr == "":
                detailPrice += ""
                detailAll['detailPrice'] = detailPrice
            else:
                detailPrice += " " + detailPriceCurr
                detailAll['detailPrice'] = detailPrice

    else:
        detailAll['detailPrice'] = "Undefined"

    if 'url' in eventDetail_json.keys():
        detailTicketUrl = eventDetail_json.get('url')
        detailAll['detailTicketUrl'] = detailTicketUrl
    else:
        detailAll['detailTicketUrl'] = "Undefined"

    if 'seatmap' in eventDetail_json.keys():
        detailSeatMap = eventDetail_json.get('seatmap').get('staticUrl')
        detailAll['detailSeatMap'] = detailSeatMap
    else:
        detailAll['detailSeatMap'] = "Undefined"

    detailAll = jsonify(detailAll)

    return detailAll

@app.route("/venue")
def VenueSearch():
    detailVenue = str(request.args.get('detailVenue'))
    venueInfo = requests.get(
    "https://app.ticketmaster.com/discovery/v2/venues.json?keyword="+detailVenue+"&apikey=OTQp2LiCm1kLOSaLAQVhyyfHnetWqist")
    venueInfo_json = venueInfo.json()
    if '_embedded' in venueInfo_json.keys():
        venueDetail = venueInfo_json.get('_embedded').get('venues')[0]
        venueAll = dict()

        venueName = venueDetail.get('name')
        venueAll['venueName'] =venueName

        if 'address' in venueDetail.keys():
            if 'line1' in venueDetail.get('address'):
                venueAddress = ChangeNullData(venueDetail.get('address').get('line1'))
                venueAll['venueAddress'] = venueAddress
            else:
                venueAll['venueAddress'] = "Undefined"
        else:
            venueAll['venueAddress'] = "Undefined"

        if 'city' in venueDetail.keys():
            if 'name' in venueDetail.get('city'):
                city = ChangeNullData(venueDetail.get('city').get('name'))
            else:
                city = "Undefined"
        else:
            city = "Undefined"

        if 'state' in venueDetail.keys():
            if 'state' in venueDetail.get('state'):
                stateCode = ChangeNullData(venueDetail.get('state').get('stateCode'))
            else:
                stateCode = "Undefined"
        else:
            stateCode = "Undefined"

        if city == "Undefined" and stateCode == "Undefined":
            venueAll['venueCity'] = "Undefined"
        elif city != "Undefined" and stateCode == "Undefined":
            venueCity = city
            venueAll['venueCity'] = venueCity
        elif city == "Undefined" and stateCode != "Undefined":
            venueCity = stateCode
            venueAll['venueCity'] = venueCity
        else:
            venueCity = city+","+stateCode
            venueAll['venueCity'] = venueCity

        if 'postalCode' in venueDetail.keys():
            venuePS = venueDetail.get('postalCode')
            venueAll['venuePS'] = venuePS
        else:
            venueAll['venuePS'] = "Undefined"

        if 'url' in venueDetail.keys():
            venueUpcoming = venueDetail.get('url')
            venueAll['venueUpcoming'] = venueUpcoming
        else:
            venueAll['venueUpcoming'] = "Undefined"

        if 'images' in venueDetail.keys():
            venueImage = venueDetail.get('images')[0].get('url')
            venueAll['venueImage'] = venueImage
        else:
            venueAll['venueImage'] = "Undefined"

        encodeName = venueName.replace(' ','+')
        encodeName = encodeName.replace(',','+')
        encodeName = encodeName.replace('.','')
        venueAll['encodeName'] = encodeName

        venueAll = jsonify(venueAll)

        return venueAll
    else:
        return jsonify(dict())


if __name__ == "__main__":
    app.run()
