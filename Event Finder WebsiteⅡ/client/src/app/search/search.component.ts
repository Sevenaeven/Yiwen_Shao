import { Component, EventEmitter, Output } from '@angular/core';
import { AppService } from '../app.service';
import { FormControl } from '@angular/forms';
import { debounceTime, tap, switchMap, finalize, distinctUntilChanged, filter } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})

export class SearchComponent{
  
  keyword : any = "";
  distance !: number;
  category !: string;
  location !: string;
  locationcheckbox !: boolean;
  locationStatus!:boolean;

  searchEventCtrl = new FormControl();
  filteredEvent: any;
  isLoading = false;


  @Output() dataEvent = new EventEmitter<any>();

  constructor(private appService: AppService,private http: HttpClient){ 
    this.category = 'Default';
    this.distance = 10;
   };

  Checkbox(){

    this.locationStatus = this.locationcheckbox;
    this.location = "";

  } 

  Search() {

    let locValue;

    if(this.locationcheckbox == true){

      this.appService.CheckboxLocation().
      subscribe(data =>{
        locValue = data.loc;
        this.Senddata(locValue);
      })

    }else{

      let output= this.location.split('');
      for(var i=0 ; i< output.length ;i++){
        if(output[i]==" " || output[i]== ","){
          output[i] = "+";
        }else if(output[i]=="."){
          output[i] = "";
        }
      }

      let geolocation = output.join("");

      let latitude;
      let longitude;
      this.appService.GeocodeLocation(geolocation).
        subscribe(data =>{
          if(data.results[0] == undefined){
            locValue = 'undefined';
            this.Senddata(locValue);
          }else{
            latitude = data.results[0].geometry.location.lat;
            longitude = data.results[0].geometry.location.lng;
            locValue = latitude+","+longitude;
            this.Senddata(locValue);
          }
        })
    }
    
  }

  Senddata(locValue:string){

    let searchData ={
      "keyword" : this.keyword,
      "distance" : this.distance,
      "category" : this.category,
      "location": locValue
    };
    
    if(locValue == 'undefined'){
      this.DatatoTable([]);
    }else{
      this.appService.SearchEvents(searchData).
      subscribe(data => {
        this.DatatoTable(data);
      })
    }

  }

  DatatoTable(data:any){
    this.dataEvent.emit(data);    
  }


  Clear(){

    this.keyword = "";
    this.distance = 10;
    this.category = "Default";
    this.location = "";
    if(this.locationcheckbox){
      this.locationStatus = !this.locationcheckbox;
    }
    this.locationcheckbox = false;

    this.DatatoTable({clear:true});

  }

  onSelected() {
    this.keyword = this.keyword.name;
  }

  autoSearch() {
    this.searchEventCtrl.valueChanges
      .pipe(
        filter(res => {
          return res !== null
        }),
        distinctUntilChanged(),
        debounceTime(1000),
        tap(() => {
          this.filteredEvent = [];
          this.isLoading = true;
        }),
        switchMap(value => 
          this.appService.AutoSearch({ search: value })
        )
      )
      .subscribe((data: any) => {
        this.isLoading = false;
        this.filteredEvent = data;
      });
  }

}
