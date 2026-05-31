import { Component, OnInit } from '@angular/core';
import { ApiService, Product } from '../../services/api.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.page.html',
})
export class ProductsPage implements OnInit {
  products: Product[] = [];
  loading = false;
  offline = false;
  errorMessage = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.offline = false;
    this.errorMessage = '';

    this.api.getProducts().subscribe({
      next: products => {
        this.products = products;
        localStorage.setItem('cached-products', JSON.stringify(products));
        this.loading = false;
      },
      error: () => {
        const cached = localStorage.getItem('cached-products');
        this.products = cached ? JSON.parse(cached) : [];
        this.offline = true;
        this.errorMessage = this.products.length
          ? 'Mostrando productos guardados porque la API no está disponible.'
          : 'No hay productos disponibles offline. Conectate una vez para cargar el catálogo.';
        this.loading = false;
      },
    });
  }
}
