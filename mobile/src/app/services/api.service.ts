import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Product {
  id?: number;
  name: string;
  price: number;
  stock: number;
  category?: { id: number; name: string };
}

export interface ProductCategory {
  id?: number;
  name: string;
  description?: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = this.resolveApiBaseUrl();

  constructor(private http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/products`);
  }

  getCategories(): Observable<ProductCategory[]> {
    return this.http.get<ProductCategory[]>(`${this.baseUrl}/product-categories`);
  }

  private resolveApiBaseUrl(): string {
    const configuredUrl = localStorage.getItem('api-base-url');
    if (configuredUrl) {
      return configuredUrl.replace(/\/$/, '');
    }

    return 'http://localhost:8080/api';
  }
}
