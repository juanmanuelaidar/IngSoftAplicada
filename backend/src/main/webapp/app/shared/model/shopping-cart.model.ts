import dayjs from 'dayjs';
import { ICustomerDetails } from 'app/shared/model/customer-details.model';

export interface IShoppingCart {
  id?: number;
  createdDate?: dayjs.Dayjs;
  status?: string;
  customer?: ICustomerDetails | null;
}

export const defaultValue: Readonly<IShoppingCart> = {};
