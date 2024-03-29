// LoginPage.tsx
import LoginForm from './LoginForm';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';

const LoginPage: React.FC = () => {
  const LOGIN_URL = `${import.meta.env.VITE_APP_BASE_URL}/login`;
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/'; 

  const handleLogin = async (email: string, password: string) => {
    try {
      const response = await axios.post(LOGIN_URL, {
        email,
        password
      });
      console.log('Login successful:', response.data);
      const { accessToken } = response.data;
      console.log("Access token is: " + accessToken)
      localStorage.setItem('accessToken', accessToken);
      dispatch({ 
        type: 'LOGIN_SUCCESS', 
        payload: { accessToken  }  
      });      
      navigate(from, { replace: true });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error('Login error:', error.response?.data || error.message);
      } else {
        console.error('An unknown error occurred:', error);
      }
      
    }
  };

  return (
    <div>
      <LoginForm onLogin={handleLogin} />
    </div>
  );
};

export default LoginPage;
