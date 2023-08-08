import { Component} from '@angular/core';
import { AppService } from '../app.service';

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent {

  favoriteData !: any;
  favoriteList !: any[]; 
  removefavorite !: any;
  hideTable = true;
  hideNoResult = true;

  constructor(private appService: AppService){ }

  ngOnInit() {
    this.favoriteList = JSON.parse(localStorage.getItem('favoriteList') || '[]');

    if(this.favoriteList.length != 0){
      this.hideNoResult = true;
      this.hideTable = false;
    }else{
      this.hideNoResult = false;
      this.hideTable = true;
    }
  }

  RemoveEvent(id:string){
    alert("Removed from Favorites!");
    this.favoriteList = this.favoriteList.filter((item) => item.id !== id);
    localStorage.setItem('favoriteList', JSON.stringify(this.favoriteList));

    if(this.favoriteList.length != 0){
      this.hideNoResult = true;
      this.hideTable = false;
    }else{
      this.hideNoResult = false;
      this.hideTable = true;
    }
  }

  


}
