import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class AppService {

  constructor(private http: HttpClient) { }

  AutoSearch(data:any){

    return this.http.get(`https://csci571-hw8-382106.wl.r.appspot.com/autoSearch`, {params:data});
  }

  CheckboxLocation(){
    interface ipinfoResponse {
      loc:string;
    }

    return this.http.get<ipinfoResponse>(`https://ipinfo.io/json?token=f8d287f84a5f3c`);
  }

  GeocodeLocation(locationPlus:string){
    interface geocodeResponse {
      results: {
        0:{
          geometry:{
            location:{
              lat:number;
              lng:number;
            }
          }
        }
      };
    }
    
    return this.http.get<geocodeResponse>(`https://maps.googleapis.com/maps/api/geocode/json?address="+${locationPlus}+"&key=AIzaSyDqkDt5Z3gLGpSnsx35ZnBQx3wM1tte6eM`);
  }
 

  SearchEvents(data:any){
    
    return this.http.get(`https://csci571-hw8-382106.wl.r.appspot.com/search`, {params:data});
  }

  EventDetail(data:any){

    return this.http.get(`https://csci571-hw8-382106.wl.r.appspot.com/detail`, {params:data});
  }

  TeamDetail(data:any){
    return this.http.get(`https://csci571-hw8-382106.wl.r.appspot.com/team`, {params:data});
  }

  SendVenue(data:any){
    return this.http.get(`https://csci571-hw8-382106.wl.r.appspot.com/venueName`, {params:data});
  }

}
