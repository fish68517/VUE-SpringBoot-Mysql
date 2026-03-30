const THEME_STORAGE_KEY = 'time-management-theme';
const DEFAULT_THEME = 'light';

const normalizeTheme = (theme) => {
  return theme === 'dark' ? 'dark' : DEFAULT_THEME;
};

export const getStoredTheme = () => {
  return normalizeTheme(localStorage.getItem(THEME_STORAGE_KEY));
};

export const applyTheme = (theme) => {
  const normalizedTheme = normalizeTheme(theme);
  const root = document.documentElement;

  root.classList.toggle('dark', normalizedTheme === 'dark');
  root.setAttribute('data-theme', normalizedTheme);
  root.style.colorScheme = normalizedTheme;
  localStorage.setItem(THEME_STORAGE_KEY, normalizedTheme);

  return normalizedTheme;
};

export const initTheme = () => {
  return applyTheme(getStoredTheme());
};
