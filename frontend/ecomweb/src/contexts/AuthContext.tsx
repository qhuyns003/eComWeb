import React, { createContext, useContext, useEffect, useState } from 'react';
import { initKeycloak, getToken, getUserInfo, login, logout, updateToken } from '../services/keycloakService';

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: any;
  token: string | null;
  login: () => void;
  logout: () => void;
  refreshToken: () => Promise<string | null>;
  getUserRoles: () => string[];
  hasRole: (role: string) => boolean;
  isSeller: boolean;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticatedState, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<any>(null);
  const [token, setToken] = useState<string | null>(null);

  const initAuth = async () => {
    try {
      setIsLoading(true);
      const authenticated = await initKeycloak();
      
      if (authenticated) {
        const userInfo = getUserInfo();
        const accessToken = getToken();
        
        setIsAuthenticated(true);
        setUser(userInfo);
        setToken(accessToken || null);
        
        // Auto refresh token
        setInterval(async () => {
          const newToken = await updateToken();
          if (newToken) {
            setToken(newToken);
          }
        }, 60000); // Check every minute
      } else {
        setIsAuthenticated(false);
        setUser(null);
        setToken(null);
      }
    } catch (error) {
      console.error('Auth initialization failed:', error);
      setIsAuthenticated(false);
      setUser(null);
      setToken(null);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogin = () => {
    console.log('AuthContext handleLogin called');
    login();
  };

  const handleLogout = () => {
    logout();
    setIsAuthenticated(false);
    setUser(null);
    setToken(null);
    
    // Clear legacy localStorage data
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  };

  const refreshTokenFunc = async (): Promise<string | null> => {
    const newToken = await updateToken();
    if (newToken) {
      setToken(newToken);
      return newToken;
    }
    return null;
  };

  // Role management functions
  const getUserRoles = (): string[] => {
    if (!user) return [];
    // Lấy roles từ Keycloak token - có thể trong realm_access hoặc resource_access
    return user.realm_access?.roles || [];
  };

  const hasRole = (role: string): boolean => {
    const roles = getUserRoles();
    return roles.includes(role) || roles.includes('ADMIN');
  };

  const isSeller = hasRole('SELLER') || hasRole('ADMIN');
  const isAdmin = hasRole('ADMIN');

  useEffect(() => {
    initAuth();
  }, []);

  const value: AuthContextType = {
    isAuthenticated: isAuthenticatedState,
    isLoading,
    user,
    token,
    login: handleLogin,
    logout: handleLogout,
    refreshToken: refreshTokenFunc,
    getUserRoles,
    hasRole,
    isSeller,
    isAdmin
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};