import { Component } from '@angular/core';
import { AppService } from '../app.service';
import { ThemePalette } from '@angular/material/core';


@Component({
  selector: 'app-eventtable',
  templateUrl: './eventtable.component.html',
  styleUrls: ['./eventtable.component.css']
})
export class EventtableComponent {

  hideTable = true;
  hideNoResult = true;
  hideDetail = true;
  eventData !: any[];
  backgroundColor: ThemePalette = 'primary';
  isFavorite = true;

  constructor(private appService: AppService) { }


  EventTable(data: any) {

    if("clear" in data){
      this.hideDetail = true;
      this.hideTable = true; 
      this.hideNoResult = true;
    }else{
      this.eventData = data;
      this.hideDetail = true;
      this.hideTable = false;

      if (this.eventData.length == 0) {
        this.hideTable = true;
        this.hideNoResult = false;
      } else {
        this.hideTable = false;
        this.hideNoResult = true;
        this.eventData.sort((a, b) => {
          if (a.LocalDate < b.LocalDate) {
            return -1;
          } else if (a.LocalDate > b.LocalDate) {
            return 1;
          } else {
            if (a.LocalTime < b.LocalTime) {
              return -1;
            } else if (a.LocalTime > b.LocalTime) {
              return 1;
            } else {
              return 0;
            }
          }
        });
      }

    }
  }

  EventDetail(data: string) {
    this.detailid = data;
    this.artistList = [];

    let eventID = {
      "id": data
    };

    this.appService.EventDetail(eventID).
      subscribe(data => {
        this.ShowDetail(data);
      })

  }

  detailName = "";
  detailDate = "";
  onlyDate=""
  detailTeam = "";
  detailVenue = "";
  detailGenre = "";
  detailPrice = "";
  detailStatus = "";
  detailLink = "";
  detailSeatMap = "";
  detailColor = "";
  detailid ="";
  artistList !: any[];

  hideDate = true;
  hideTeam = true;
  hideVenue = true;
  hideGenres = true;
  hidePrice = true;
  hideStatus = true;
  hideLink = true;
  hideMap = true;

  ShowDetail(data: any) {
    this.hideTable = true;
    this.hideDetail = false;

    this.detailName = data.detailname;

    if (data.detailLocalDate == "" && data.detailLocalTime == "") {
      this.hideDate = true;
    } else if (data.detailLocalDate == "" && data.detailLocalTime != "") {
      this.hideDate = false;
      this.detailDate = data.detailLocalTime;
    } else if (data.detailLocalDate != "" && data.detailLocalTime == "") {
      this.hideDate = false;
      this.detailDate = data.detailLocalDate;
      this.onlyDate = data.detailLocalDate;
    } else {
      this.hideDate = false;
      this.detailDate = data.detailLocalDate + " " + data.detailLocalTime;
      this.onlyDate = data.detailLocalDate;
    }

    if (data.artistTitle == "") {
      this.hideTeam = true;
      this.TeamDetail([""]);
    } else {
      this.detailTeam = data.artistTitle;
      this.hideTeam = false;

      if (data.artistList != "") {
        var artists = data.artistList;
        var list = [];
        for (var i = 0; i < artists.length; i++) {
          var artistName = artists[i].artistName;
          var musicRelated = artists[i].musicRelated;
          if (musicRelated == "Music") {
            list.push(artistName);
          } else {
            list.push("");
          }
        }

        this.TeamDetail(list);

      }
    }

    if (data.detailVenue == "") {
      this.hideVenue = true;
    } else {
      this.detailVenue = data.detailVenue;
      this.hideVenue = false;
      this.SendVenue(data.detailVenue);
    }

    if (data.detailGenre == "") {
      this.hideGenres = true;
    } else {
      this.detailGenre = data.detailGenre;
      this.hideGenres = false;
    }

    if (data.detailPrice == "") {
      this.hidePrice = true;
    } else {
      this.detailPrice = data.detailPrice;
      this.hidePrice = false;
    }

    if (data.detailTicketStatus == "") {
      this.hideStatus = true;
    } else {
      this.detailStatus = data.detailTicketStatus;
      this.hideStatus = false;
      if (this.detailStatus == "onsale") {
        this.detailStatus = "On sale";
        this.detailColor = "bg-success";
      } else if (this.detailStatus == "offsale") {
        this.detailStatus = "Off sale";
        this.detailColor = "bg-danger";
      } else if (this.detailStatus == "cancelled") {
        this.detailStatus = "Cancelled";
        this.detailColor = "bg-dark";
      } else if (this.detailStatus == "postponed") {
        this.detailStatus = "Postponed";
        this.detailColor = "bg-warning";
      } else if (this.detailStatus == "rescheduled") {
        this.detailStatus = "Rescheduled";
        this.detailColor = "bg-warning";
      }
    }

    if (data.detailTicketUrl == "") {
      this.hideLink = true;
    } else {
      this.detailLink = data.detailTicketUrl;
      this.hideLink = false;
    }

    if (data.detailSeatMap == "") {
      this.hideMap = true;
    } else {
      this.detailSeatMap = data.detailSeatMap;
      this.hideMap = false;
    }

    this.favoriteList = JSON.parse(localStorage.getItem('favoriteList') || '[]');
    var isExist = this.favoriteList.some(item => item.id === this.detailid);
      if(isExist){
        this.iconColor = "red";
      }else{
        this.iconColor = "white";
      }
    
    
  }


  ClickBack() {
    this.hideDetail = true;
    this.hideTable = false;

  }

  iconColor !: string;
  favoriteList !:any[];
  
  ClickFavorite() {

    if (this.iconColor == 'white') {

      this.iconColor = 'red';
      alert("Event Added to Favorites!");

      let favoriteDetail = {
        "id": this.detailid,
        "date": this.onlyDate,
        "event": this.detailName,
        "category": this.detailGenre,
        "venue": this.detailVenue
      }

      this.favoriteList = JSON.parse(localStorage.getItem('favoriteList') || '[]');
      var isDuplicate = this.favoriteList.some(item => item.id === this.detailid);
      if(!isDuplicate){
        this.favoriteList.push(favoriteDetail);
        localStorage.setItem('favoriteList', JSON.stringify(this.favoriteList));
      }


    } else {
      this.iconColor = 'white';
      alert("Removed from Favorites!");
      localStorage.setItem(this.detailid,this.iconColor);

      this.favoriteList = JSON.parse(localStorage.getItem('favoriteList') || '[]');
      var isExist = this.favoriteList.some(item => item.id === this.detailid);
      if(isExist){
        this.favoriteList = this.favoriteList.filter((item) => item.id !== this.detailid);
        localStorage.setItem('favoriteList', JSON.stringify(this.favoriteList));
      }
      
    }
  }

  TeamDetail(data: any) {
    this.artistList = [];
    var artistNum = 0;
    var artistlength = data.length;
    for (var i = 0; i < data.length; i++) {
      artistNum = i;

      let teamsdetail = {
        "team": data[i]
      };

      this.appService.TeamDetail(teamsdetail).
        subscribe(response => {
          this.FilterTeam(response, teamsdetail, artistNum, artistlength);
        })
    }
  }

  hideArtistDetail = true;
  hideNoArtistDetail = true;
  artistLength !: number;
  hideAlbum1 =true;
  hideAlbum2 =true;
  hideAlbum3 =true;
  hideNoAlbum = true;

  FilterTeam(data: any, teamsdetail: any, artistNum: number, artistlength: number) {
    var artistDetail = {};
    if (data.id != "undefined") {

      if (data.name.toLowerCase() == teamsdetail.team.toLowerCase()) {
        var name = data.name;
        var followers = data.followers.total;
        var popularity = data.popularity;
        var spotifyLink = data.external_urls.spotify;
        var album = data.album;
        var image = data.images[0].url;
        var album1 ="";
        var album2 ="";
        var album3 ="";
        if(album.length >= 3){
          album1 = album[0].images[0].url;
          album2 = album[1].images[0].url;
          album3 = album[2].images[0].url;
          this.hideAlbum1 =false;
          this.hideAlbum2 =false;
          this.hideAlbum3 =false;
          this.hideNoAlbum = true;
        }else if(album.length == 2){
          album1 = album[0].images[0].url;
          album2 = album[1].images[0].url; 
          this.hideAlbum1 =false;
          this.hideAlbum2 =false;
          this.hideAlbum3 =true;
          this.hideNoAlbum = true;        
        }else if(album.length == 1){
          album1 = album[0].images[0].url;
          this.hideAlbum1 =false;
          this.hideAlbum2 =true;
          this.hideAlbum3 =true;
          this.hideNoAlbum = true;
        }else{
          this.hideAlbum1 =true;
          this.hideAlbum2 =true;
          this.hideAlbum3 =true;
          this.hideNoAlbum =false;
        }
        

        artistDetail = {
          "name": name,
          "followers": followers,
          "popularity": popularity,
          "spotifyLink": spotifyLink,
          "image": image,
          "album1": album1,
          "album2": album2,
          "album3": album3
        };
        this.artistList.push(artistDetail);
      }
    }


    if (artistNum + 1 == artistlength) {

      if (this.artistList.length == 0) {
        this.hideArtistDetail = true;
        this.hideNoArtistDetail = false;
      } else {
        this.artistLength = this.artistList.length;
        this.hideArtistDetail = false;
        this.hideNoArtistDetail = true;
      }
    }
  }

  SendVenue(data: string) {

    let venueName = {
      "venue": data
    }

    this.appService.SendVenue(venueName).
      subscribe(response => {
        this.VenueDetail(response);
      })
  }

  venueName !: string;
  venueAddress !: string;
  venuePhone !: string;
  openHoure !: string;
  childRule !: string;
  generalRule !: string;
  venuelat = 0.00;
  venuelong = 0.00;

  hideVenueAddress = true;
  hideVenuePhone = true;
  hideOpenHoure = true;
  hideChildRule = true;
  hideGeneralRule = true;

  VenueDetail(data: any) {
    this.venueName = data.venueName;
    this.venueAddress = data.address;
    this.venuePhone = data.phone;
    this.openHoure = data.openHoure;
    this.childRule = data.childRule;
    this.generalRule = data.generalRule;
    this.venuelat = parseFloat(data.latitude);
    this.venuelong = parseFloat(data.longitude);

    if (this.venueAddress == "") {
      this.hideVenueAddress = true;
    } else {
      this.hideVenueAddress = false;
    }

    if (this.venuePhone == "") {
      this.hideVenuePhone = true;
    } else {
      this.hideVenuePhone = false;
    }

    if (this.openHoure == "") {
      this.hideOpenHoure = true;
    } else {
      this.hideOpenHoure = false;
    }

    if (this.childRule == "") {
      this.hideChildRule = true;
    } else {
      this.hideChildRule = false;
    }

    if (this.generalRule == "") {
      this.hideGeneralRule = true;
    } else {
      this.hideGeneralRule = false;
    }

  }

  showOpenHour = false;

  controlOpenHour() {
    this.showOpenHour = !this.showOpenHour;
  }

  showGeneral = false;
  controlGeneral() {
    this.showGeneral = !this.showGeneral;
  }

  showChild = false;
  controlChild() {
    this.showChild = !this.showChild;
  }

  showMapModal = false;
  center !: google.maps.LatLngLiteral;
  zoom = 15;

  ClickMap() {
    this.showMapModal = true;
    this.center = { lat: this.venuelat, lng: this.venuelong };

  }

  CloseMap() {
    this.showMapModal = false;
  }

}
