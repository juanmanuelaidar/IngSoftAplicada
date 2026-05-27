import { IProductCategory } from 'app/shared/model/product-category.model';

export interface IProduct {
  id?: number;
  name?: string;
  price?: number;
  stock?: number;
  category?: IProductCategory | null;
}

export const defaultValue: Readonly<IProduct> = {};
