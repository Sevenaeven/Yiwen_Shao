import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import{ FavoritesComponent } from './favorites/favorites.component';
import { EventtableComponent } from './eventtable/eventtable.component';

const routes: Routes = [
  { path: '', redirectTo: '/search', pathMatch: 'full' },
  { path: 'search', component: EventtableComponent },
  { path: 'favorites', component: FavoritesComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
