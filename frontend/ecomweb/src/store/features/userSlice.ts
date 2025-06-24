import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { getMyInfo, logout as logoutApi } from '../../api/api';
import type { RootState } from '../store';

interface Role {
  name: string;
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
      return null;
    } catch (error: any) {
      // Bỏ qua lỗi khi logout, vẫn xóa token
      localStorage.removeItem('token');
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
  extraReducers: (builder) => { //extraReducers giống như một "bộ lắng nghe" các action bất đồng bộ.
    builder
      // fetchUserInfo
      .addCase(fetchUserInfo.pending, (state) => {  //Dựa vào trạng thái (pending/fulfilled/rejected), nó sẽ cập nhật state một cách tương ứng.
        state.loading = true;  // dang goi api
        state.error = null;
      })
      .addCase(fetchUserInfo.fulfilled, (state, action) => {
        state.loading = false;     // goi thanh cong
        state.user = action.payload;
        state.error = null;
      })
      .addCase(fetchUserInfo.rejected, (state, action) => {
        state.loading = false;    // goi that bai
        state.user = null;
        state.error = action.payload as string;
      })
      // logoutUser
      .addCase(logoutUser.fulfilled, (state) => {
        state.user = null;
        state.error = null;
      });
  },
});

export const { setUser, clearUser, setLoading, setError } = userSlice.actions;

// Selectors
export const selectUser = (state: RootState) => state.user.user;
export const selectUserLoading = (state: RootState) => state.user.loading;
export const selectUserError = (state: RootState) => state.user.error;
export const selectIsSeller = (state: RootState) => 
  state.user.user?.roles?.some(role => role.name === 'SELLER') || false;
export const selectIsAuthenticated = (state: RootState) => !!state.user.user;

export default userSlice.reducer; 