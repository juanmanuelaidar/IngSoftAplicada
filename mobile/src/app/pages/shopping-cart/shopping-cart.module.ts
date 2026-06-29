import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';

import { ShoppingCartPageRoutingModule } from './shopping-cart-routing.module';
import { ShoppingCartPage } from './shopping-cart.page';

@NgModule({
  imports: [CommonModule, IonicModule, ShoppingCartPageRoutingModule],
  declarations: [ShoppingCartPage],
})
export class ShoppingCartPageModule {}
