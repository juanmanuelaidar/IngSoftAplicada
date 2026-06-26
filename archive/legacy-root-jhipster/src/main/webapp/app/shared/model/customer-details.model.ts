import { IUser } from 'app/shared/model/user.model';

export interface ICustomerDetails {
  id?: number;
  phone?: string | null;
  address?: string;
  user?: IUser | null;
}

export const defaultValue: Readonly<ICustomerDetails> = {};
