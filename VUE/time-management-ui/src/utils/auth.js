const AUTH_STORAGE_KEY = 'time-management-auth-user';

const normalizeUser = (payload) => {
  if (!payload || typeof payload !== 'object') {
    return null;
  }

  return {
    campusUserId: payload.campusUserId ?? null,
    campusNickname: payload.campusNickname ?? '',
    campusEmailAddr: payload.campusEmailAddr ?? '',
    campusSchoolId: payload.campusSchoolId ?? '',
    campusUserType: payload.campusUserType ?? '',
    campusAvatarUrl: payload.campusAvatarUrl ?? '',
    campusStatusFlag: payload.campusStatusFlag ?? null,
  };
};

export const getStoredUser = () => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    return normalizeUser(JSON.parse(raw));
  } catch (error) {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }
};

export const setStoredUser = (payload) => {
  const user = normalizeUser(payload);
  if (!user) {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }

  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
  return user;
};

export const clearStoredUser = () => {
  localStorage.removeItem(AUTH_STORAGE_KEY);
};

export const getStoredUserType = () => getStoredUser()?.campusUserType || '';

export const isAdminLoggedIn = () => getStoredUserType() === 'admin';
