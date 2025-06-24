import HomePage from "./component/homepage/HomePage";
import { Routes, Route } from 'react-router-dom';
import LoginForm from './component/login/LoginForm';
import { Provider } from 'react-redux';
import { store } from './store/store';
import AppInitializer from './component/AppInitializer';
import RegisterForm from './component/register/RegisterForm';
import ProductDetail from './component/product/ProductDetail';

function App() {
  return (
    <Provider store={store}>
      <AppInitializer>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/product/:productId" element={<ProductDetail />} />
        </Routes>
      </AppInitializer>
    </Provider>
  );
}

export default App;
