import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import type { PayloadAction, Draft } from '@reduxjs/toolkit';
import { getMyInfo, logout as logoutApi } from '../../api/api';
import type { RootState } from '../store';

interface Role {
  id: string;
  description: string;
  permissions: any[]; // Hoặc định nghĩa cụ thể hơn
}

interface User {
  id: string;
  username: string;
  fullName: string;
  dob: string;
  email: string;
  phone: string;
  roles: Role[]; // Sửa kiểu dữ liệu ở đây
}

interface UserState {
  user: User | null;
  loading: boolean;
  error: string | null;
}

const initialState: UserState = {
  user: null,
  loading: false,
  error: null,
};

// Async thunk để lấy thông tin user
export const fetchUserInfo = createAsyncThunk(
  'user/fetchUserInfo',
  async (_, { rejectWithValue }) => {
    try {
      const response = await getMyInfo(localStorage.getItem('token') || '');
      return response.data.result;
    } catch (error: any) {
      return rejectWithValue(error?.response?.data?.message || 'Failed to fetch user info');
    }
  }
);

// Async thunk để logout
export const logoutUser = createAsyncThunk(
  'user/logoutUser',
  async (_, { rejectWithValue }) => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        await logoutApi(token);
      }
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      return null;
    } catch (error: any) {
      // Bỏ qua lỗi khi logout, vẫn xóa token
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      return null;
    }
  }
);

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
      state.error = null;
    },
    clearUser: (state) => {
      state.user = null;
      state.error = null;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchUserInfo
      .addCase(fetchUserInfo.pending, (state: Draft<UserState>) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserInfo.fulfilled, (state: Draft<UserState>, action) => {
        state.loading = false;
        state.user = action.payload;
        state.error = null;
      })
      .addCase(fetchUserInfo.rejected, (state: Draft<UserState>, action) => {
        state.loading = false;
        state.user = null;
        state.error = action.payload as string;
      })
      // logoutUser
      .addCase(logoutUser.fulfilled, (state: Draft<UserState>) => {
        state.user = null;
        state.error = null;
      });
  },
});

export const { setUser, clearUser, setLoading, setError } = userSlice.actions;

// Selectors

// Khi dùng redux-persist, state.user có thể là PersistPartial<UserState>
export const selectUser = (state: RootState) => (state.user as UserState).user;
export const selectUserLoading = (state: RootState) => (state.user as UserState).loading;
export const selectUserError = (state: RootState) => (state.user as UserState).error;
export const selectIsSeller = (state: RootState) =>
  (state.user as UserState).user?.roles?.some((role: Role) => role.id === 'SELLER') || false;
export const selectIsAuthenticated = (state: RootState) => !!(state.user as UserState).user;

export default userSlice.reducer; 