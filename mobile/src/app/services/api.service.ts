import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

import { environment } from '../../environments/environment';

export interface Product {
  id: number;
  name: string;
  price: number;
  stock: number;
  category?: {
    id: number;
    name: string;
  } | null;
}

export interface ProductCategory {
  id: number;
  name: string;
  description?: string | null;
}

export interface ShoppingCart {
  id: number;
  createdDate: string;
  status: string;
  customer?: {
    id: number;
  } | null;
}

export interface ProductOrder {
  id: number;
  placedDate: string;
  quantity: number;
  product?: Product | null;
  cart?: ShoppingCart | null;
}

export interface CreateProductRequest {
  name: string;
  price: number;
  stock: number;
}

export interface CreateShoppingCartRequest {
  createdDate: string;
  status: string;
}

export interface CreateProductOrderRequest {
  placedDate: string;
  quantity: number;
  product: {
    id: number;
  };
  cart: {
    id: number;
  };
}

interface AuthResponse {
  id_token: string;
}

const AUTH_TOKEN_KEY = 'store-mobile.auth-token';
const ACTIVE_CART_ID_KEY = 'store-mobile.active-cart-id';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = environment.apiBaseUrl;
  private token?: string | null = localStorage.getItem(AUTH_TOKEN_KEY);

  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string): Observable<string> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/authenticate`, { username, password }).pipe(
      map(response => response.id_token),
      tap(token => {
        this.token = token;
        localStorage.setItem(AUTH_TOKEN_KEY, token);
      }),
    );
  }

  logout(): void {
    this.token = undefined;
    localStorage.removeItem(AUTH_TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return Boolean(this.token || localStorage.getItem(AUTH_TOKEN_KEY));
  }

  getProducts(): Observable<Product[]> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.get<Product[]>(`${this.baseUrl}/products`, { headers })));
  }

  createProduct(product: CreateProductRequest): Observable<Product> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.post<Product>(`${this.baseUrl}/products`, product, { headers })));
  }

  getCategories(): Observable<ProductCategory[]> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.get<ProductCategory[]>(`${this.baseUrl}/product-categories`, { headers })));
  }

  getShoppingCarts(): Observable<ShoppingCart[]> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.get<ShoppingCart[]>(`${this.baseUrl}/shopping-carts`, { headers })));
  }

  createShoppingCart(cart: CreateShoppingCartRequest): Observable<ShoppingCart> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.post<ShoppingCart>(`${this.baseUrl}/shopping-carts`, cart, { headers })));
  }

  getProductOrders(): Observable<ProductOrder[]> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.get<ProductOrder[]>(`${this.baseUrl}/product-orders`, { headers })));
  }

  createProductOrder(order: CreateProductOrderRequest): Observable<ProductOrder> {
    return this.withAuthHeaders().pipe(switchMap(headers => this.http.post<ProductOrder>(`${this.baseUrl}/product-orders`, order, { headers })));
  }

  getActiveCartId(): number | null {
    const value = localStorage.getItem(ACTIVE_CART_ID_KEY);
    return value ? Number(value) : null;
  }

  setActiveCartId(cartId: number): void {
    localStorage.setItem(ACTIVE_CART_ID_KEY, String(cartId));
  }

  clearActiveCartId(): void {
    localStorage.removeItem(ACTIVE_CART_ID_KEY);
  }

  private withAuthHeaders(): Observable<HttpHeaders> {
    return this.getToken().pipe(map(token => new HttpHeaders({ Authorization: `Bearer ${token}` })));
  }

  private getToken(): Observable<string> {
    if (this.token) {
      return of(this.token);
    }

    const savedToken = localStorage.getItem(AUTH_TOKEN_KEY);
    if (savedToken) {
      this.token = savedToken;
      return of(savedToken);
    }

    return throwError(() => new Error('User is not authenticated'));
  }
}
