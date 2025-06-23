import HomePage from "./component/homepage/HomePage";
import { Routes, Route } from 'react-router-dom';
import LoginForm from './component/login/LoginForm';

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginForm />} />
    </Routes>
  );
}

export default App;
