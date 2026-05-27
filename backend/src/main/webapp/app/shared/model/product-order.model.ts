import dayjs from 'dayjs';
import { IProduct } from 'app/shared/model/product.model';
import { IShoppingCart } from 'app/shared/model/shopping-cart.model';

export interface IProductOrder {
  id?: number;
  placedDate?: dayjs.Dayjs;
  quantity?: number;
  product?: IProduct | null;
  cart?: IShoppingCart | null;
}

export const defaultValue: Readonly<IProductOrder> = {};
