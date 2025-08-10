import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

export interface OrderShopGroup {
  shop: any;
  products: any[];
}

export interface OrderState {
  orderShopGroups: OrderShopGroup[];
}

const initialState: OrderState = {
  orderShopGroups: [],
};

const orderSlice = createSlice({
  name: 'order',
  initialState,
  reducers: {
    setOrderShopGroups(state, action: PayloadAction<OrderShopGroup[]>) {
      state.orderShopGroups = action.payload;
    },
    clearOrder(state) {
      state.orderShopGroups = [];
    },
  },
});

export const { setOrderShopGroups, clearOrder } = orderSlice.actions;
export default orderSlice.reducer; 