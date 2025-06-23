import { configureStore } from '@reduxjs/toolkit';
// import userReducer from './features/userSlice'; // Ví dụ

export const store = configureStore({
  reducer: {
    // user: userReducer, // Ví dụ
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch; 