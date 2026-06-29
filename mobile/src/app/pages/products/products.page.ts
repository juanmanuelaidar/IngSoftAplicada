import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize, of, switchMap, tap } from 'rxjs';

import { ApiService, Product } from '../../services/api.service';

const PRODUCTS_CACHE_KEY = 'store-mobile.products';

@Component({
  selector: 'app-products',
  templateUrl: './products.page.html',
  styleUrls: ['./products.page.scss'],
})
export class ProductsPage implements OnInit {
  products: Product[] = [];
  loading = false;
  saving = false;
  addingProductId?: number;
  offline = false;
  errorMessage = '';
  createErrorMessage = '';
  cartMessage = '';
  cartErrorMessage = '';
  successMessage = '';
  newProduct = {
    name: '',
    price: 0,
    stock: 0,
  };

  constructor(
    private readonly api: ApiService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.api.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadProducts();
  }

  onRefresh(event: Event): void {
    this.loadProducts(event.target as HTMLIonRefresherElement);
  }

  loadProducts(refresher?: HTMLIonRefresherElement): void {
    this.loading = true;
    this.errorMessage = '';

    this.api
      .getProducts()
      .pipe(
        finalize(() => {
          this.loading = false;
          refresher?.complete();
        }),
      )
      .subscribe({
        next: products => {
          this.products = products;
          this.offline = false;
          localStorage.setItem(PRODUCTS_CACHE_KEY, JSON.stringify(products));
        },
        error: () => {
          const cached = localStorage.getItem(PRODUCTS_CACHE_KEY);
          if (cached) {
            this.products = JSON.parse(cached) as Product[];
            this.offline = true;
            return;
          }

          this.products = [];
          this.errorMessage = 'No se pudieron cargar los productos.';
        },
      });
  }

  createProduct(): void {
    if (!this.newProduct.name.trim() || this.newProduct.price <= 0 || this.newProduct.stock < 0) {
      this.createErrorMessage = 'Completa nombre, precio mayor a cero y stock valido.';
      return;
    }

    this.saving = true;
    this.createErrorMessage = '';
    this.successMessage = '';

    this.api
      .createProduct({
        name: this.newProduct.name.trim(),
        price: Number(this.newProduct.price),
        stock: Number(this.newProduct.stock),
      })
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: product => {
          this.products = [product, ...this.products];
          localStorage.setItem(PRODUCTS_CACHE_KEY, JSON.stringify(this.products));
          this.newProduct = { name: '', price: 0, stock: 0 };
          this.offline = false;
          this.successMessage = 'Producto creado correctamente.';
        },
        error: () => {
          this.createErrorMessage = 'No se pudo crear el producto.';
        },
      });
  }

  addToCart(product: Product): void {
    this.addingProductId = product.id;
    this.cartMessage = '';
    this.cartErrorMessage = '';

    this.ensureActiveCart()
      .pipe(
        switchMap(cartId =>
          this.api.createProductOrder({
            placedDate: new Date().toISOString(),
            quantity: 1,
            product: { id: product.id },
            cart: { id: cartId },
          }),
        ),
        finalize(() => (this.addingProductId = undefined)),
      )
      .subscribe({
        next: () => {
          this.cartMessage = `${product.name} agregado al carrito.`;
        },
        error: () => {
          this.cartErrorMessage = 'No se pudo agregar el producto al carrito.';
        },
      });
  }

  private ensureActiveCart() {
    const cartId = this.api.getActiveCartId();
    if (cartId) {
      return of(cartId);
    }

    return this.api
      .createShoppingCart({
        createdDate: new Date().toISOString(),
        status: 'OPEN',
      })
      .pipe(
        tap(cart => this.api.setActiveCartId(cart.id)),
        switchMap(cart => of(cart.id)),
      );
  }

  trackByProductId(_: number, product: Product): number {
    return product.id;
  }

  logout(): void {
    this.api.logout();
    this.router.navigate(['/login']);
  }
}
