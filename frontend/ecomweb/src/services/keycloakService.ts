import Keycloak from 'keycloak-js';

// Khởi tạo Keycloak instance
let keycloak: any = null;
let initPromise: Promise<boolean> | null = null;

const keycloakConfig = {
  url: 'http://localhost:8089',
  realm: 'ecomweb',
  clientId: 'ecomweb-frontend'
};

// Lazy initialization để tránh multiple instances
const getKeycloakInstance = () => {
  if (!keycloak) {
    keycloak = new Keycloak(keycloakConfig);
  }
  return keycloak;
};

// Khởi tạo Keycloak với các options
export const initKeycloak = async () => {
  // Nếu đã có promise đang chạy, return promise đó
  if (initPromise) {
    return initPromise;
  }

  // Tạo promise để tránh race condition
  initPromise = (async () => {
    try {
      const keycloakInstance = getKeycloakInstance();
      
      // Nếu đã authenticated rồi, return luôn
      if (keycloakInstance.authenticated) {
        console.log('Keycloak already authenticated');
        return true;
      }

      console.log('Initializing Keycloak with config:', keycloakConfig);
      
      const authenticated = await keycloakInstance.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256'
      });
      
      console.log('Keycloak initialized. Authenticated:', authenticated);
      if (authenticated) {
        console.log('Keycloak token:', keycloakInstance.token);
        console.log('Keycloak user info:', keycloakInstance.tokenParsed);
      }
      
      return authenticated;
    } catch (error) {
      console.error('Failed to initialize Keycloak:', error);
      initPromise = null; // Reset promise để có thể retry
      return false;
    }
  })();

  return initPromise;
};

// Login user - redirect đến Keycloak
export const login = () => {
  console.log('Login function called - redirecting to Keycloak...');
  console.log('Keycloak config:', keycloakConfig);
  console.log('Redirect URI:', window.location.origin);
  
  try {
    const keycloakInstance = getKeycloakInstance();
    keycloakInstance.login({
      redirectUri: window.location.origin
    });
  } catch (error) {
    console.error('Login error:', error);
  }
};

// Logout user
export const logout = () => {
  const keycloakInstance = getKeycloakInstance();
  keycloakInstance.logout({
    redirectUri: window.location.origin
  });
};

// Lấy access token
export const getToken = () => {
  const keycloakInstance = getKeycloakInstance();
  return keycloakInstance.token;
};

// Lấy thông tin user
export const getUserInfo = () => {
  const keycloakInstance = getKeycloakInstance();
  return keycloakInstance.tokenParsed;
};

// Kiểm tra authenticated
export const isAuthenticated = () => {
  const keycloakInstance = getKeycloakInstance();
  return keycloakInstance.authenticated;
};

// Refresh token khi sắp hết hạn
export const updateToken = async () => {
  try {
    const keycloakInstance = getKeycloakInstance();
    const refreshed = await keycloakInstance.updateToken(30);
    if (refreshed) {
      console.log('Token refreshed');
      return keycloakInstance.token;
    }
    return keycloakInstance.token;
  } catch (error) {
    console.error('Failed to refresh token:', error);
    return null;
  }
};

// Lấy user roles
export const getUserRoles = () => {
  const keycloakInstance = getKeycloakInstance();
  return keycloakInstance.realmAccess?.roles || [];
};

// Kiểm tra user có role không
export const hasRole = (role: string) => {
  return getUserRoles().includes(role);
};

export default getKeycloakInstance;