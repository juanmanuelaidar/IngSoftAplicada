import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { ApiService, ProductOrder, ShoppingCart } from '../../services/api.service';

const CARTS_CACHE_KEY = 'store-mobile.shopping-carts';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.page.html',
  styleUrls: ['./shopping-cart.page.scss'],
})
export class ShoppingCartPage implements OnInit {
  carts: ShoppingCart[] = [];
  orders: ProductOrder[] = [];
  loading = false;
  offline = false;
  errorMessage = '';

  constructor(
    private readonly api: ApiService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.api.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadCarts();
  }

  onRefresh(event: Event): void {
    this.loadCarts(event.target as HTMLIonRefresherElement);
  }

  loadCarts(refresher?: HTMLIonRefresherElement): void {
    this.loading = true;
    this.errorMessage = '';

    this.api
      .getShoppingCarts()
      .pipe(
        finalize(() => {
          this.loading = false;
          refresher?.complete();
        }),
      )
      .subscribe({
        next: carts => {
          this.carts = carts;
          this.offline = false;
          localStorage.setItem(CARTS_CACHE_KEY, JSON.stringify(carts));
          this.loadOrders();
        },
        error: () => {
          const cached = localStorage.getItem(CARTS_CACHE_KEY);
          if (cached) {
            this.carts = JSON.parse(cached) as ShoppingCart[];
            this.offline = true;
            return;
          }

          this.carts = [];
          this.errorMessage = 'No se pudieron cargar los carritos.';
        },
      });
  }

  loadOrders(): void {
    this.api.getProductOrders().subscribe({
      next: orders => {
        const activeCartId = this.api.getActiveCartId();
        this.orders = activeCartId ? orders.filter(order => order.cart?.id === activeCartId) : orders;
      },
      error: () => {
        this.orders = [];
      },
    });
  }

  get activeCartId(): number | null {
    return this.api.getActiveCartId();
  }

  createCart(): void {
    this.loading = true;
    this.errorMessage = '';

    this.api
      .createShoppingCart({
        createdDate: new Date().toISOString(),
        status: 'OPEN',
      })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: cart => {
          this.api.setActiveCartId(cart.id);
          this.carts = [cart, ...this.carts];
          localStorage.setItem(CARTS_CACHE_KEY, JSON.stringify(this.carts));
          this.orders = [];
        },
        error: () => {
          this.errorMessage = 'No se pudo crear el carrito.';
        },
      });
  }

  closeActiveCart(): void {
    this.api.clearActiveCartId();
    this.orders = [];
  }

  trackByCartId(_: number, cart: ShoppingCart): number {
    return cart.id;
  }

  trackByOrderId(_: number, order: ProductOrder): number {
    return order.id;
  }

  logout(): void {
    this.api.logout();
    this.router.navigate(['/login']);
  }
}
