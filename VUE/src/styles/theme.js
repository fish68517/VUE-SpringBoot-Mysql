/**
 * Element Plus Theme Configuration
 * Defines the blue color scheme for the entire application
 */

export const themeConfig = {
  // Primary blue color scheme
  colors: {
    primary: '#1890ff',
    primaryLight: '#40a9ff',
    primaryLighter: '#69c0ff',
    primaryDark: '#0050b3',
    primaryDarker: '#003a8c',
    
    // Semantic colors
    success: '#52c41a',
    warning: '#faad14',
    error: '#f5222d',
    info: '#1890ff',
    
    // Text colors
    textPrimary: '#262626',
    textSecondary: '#595959',
    textTertiary: '#8c8c8c',
    textDisabled: '#bfbfbf',
    
    // Background colors
    bgPrimary: '#fafafa',
    bgSecondary: '#ffffff',
    bgTertiary: '#f5f5f5',
    
    // Border colors
    borderDefault: '#d9d9d9',
    borderLight: '#f0f0f0',
    borderDark: '#bfbfbf',
  },
  
  // Spacing scale
  spacing: {
    xs: '4px',
    sm: '8px',
    md: '12px',
    lg: '16px',
    xl: '24px',
    xxl: '32px',
  },
  
  // Border radius
  borderRadius: {
    sm: '2px',
    md: '4px',
    lg: '8px',
    xl: '12px',
  },
  
  // Font sizes
  fontSize: {
    xs: '12px',
    sm: '13px',
    md: '14px',
    lg: '16px',
    xl: '18px',
    xxl: '20px',
  },
  
  // Font weights
  fontWeight: {
    light: 300,
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
  },
  
  // Shadows
  shadows: {
    sm: '0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px 0 rgba(0, 0, 0, 0.02)',
    md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
    lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
  },
  
  // Transitions
  transitions: {
    fast: '0.15s ease-in-out',
    normal: '0.3s ease-in-out',
    slow: '0.5s ease-in-out',
  },
};

/**
 * Element Plus component theme overrides
 * Customizes Element Plus components to use the blue color scheme
 */
export const elementPlusTheme = {
  // Button component
  '--el-button-bg-color': themeConfig.colors.primary,
  '--el-button-border-color': themeConfig.colors.primary,
  '--el-button-text-color': '#ffffff',
  '--el-button-hover-bg-color': themeConfig.colors.primaryLight,
  '--el-button-hover-border-color': themeConfig.colors.primaryLight,
  '--el-button-active-bg-color': themeConfig.colors.primaryDark,
  '--el-button-active-border-color': themeConfig.colors.primaryDark,
  
  // Input component
  '--el-input-border-color': themeConfig.colors.borderDefault,
  '--el-input-focus-border-color': themeConfig.colors.primary,
  '--el-input-text-color': themeConfig.colors.textPrimary,
  '--el-input-placeholder-color': themeConfig.colors.textTertiary,
  
  // Select component
  '--el-select-border-color': themeConfig.colors.borderDefault,
  '--el-select-input-focus-border-color': themeConfig.colors.primary,
  
  // Menu component
  '--el-menu-bg-color': themeConfig.colors.bgSecondary,
  '--el-menu-text-color': themeConfig.colors.textPrimary,
  '--el-menu-hover-bg-color': themeConfig.colors.bgTertiary,
  '--el-menu-hover-text-color': themeConfig.colors.primary,
  '--el-menu-active-color': themeConfig.colors.primary,
  
  // Tabs component
  '--el-tabs-header-bg-color': themeConfig.colors.bgTertiary,
  '--el-tabs-active-name-color': themeConfig.colors.primary,
  '--el-tabs-active-name-border-color': themeConfig.colors.primary,
  
  // Table component
  '--el-table-header-bg-color': themeConfig.colors.bgTertiary,
  '--el-table-header-text-color': themeConfig.colors.textPrimary,
  '--el-table-row-hover-bg-color': themeConfig.colors.bgTertiary,
  '--el-table-border-color': themeConfig.colors.borderLight,
  
  // Pagination component
  '--el-pagination-button-bg-color': themeConfig.colors.bgSecondary,
  '--el-pagination-button-color': themeConfig.colors.textPrimary,
  '--el-pagination-hover-color': themeConfig.colors.primary,
  '--el-pagination-active-color': themeConfig.colors.primary,
  
  // Dialog component
  '--el-dialog-bg-color': themeConfig.colors.bgSecondary,
  '--el-dialog-title-text-color': themeConfig.colors.textPrimary,
  
  // Message component
  '--el-color-primary': themeConfig.colors.primary,
  '--el-color-success': themeConfig.colors.success,
  '--el-color-warning': themeConfig.colors.warning,
  '--el-color-error': themeConfig.colors.error,
  '--el-color-info': themeConfig.colors.info,
};

/**
 * Apply theme variables to document root
 */
export function applyTheme() {
  const root = document.documentElement;
  
  // Apply Element Plus theme variables
  Object.entries(elementPlusTheme).forEach(([key, value]) => {
    root.style.setProperty(key, value);
  });
  
  // Apply custom theme variables
  Object.entries(themeConfig.colors).forEach(([key, value]) => {
    root.style.setProperty(`--${key}`, value);
  });
  
  Object.entries(themeConfig.spacing).forEach(([key, value]) => {
    root.style.setProperty(`--spacing-${key}`, value);
  });
  
  Object.entries(themeConfig.borderRadius).forEach(([key, value]) => {
    root.style.setProperty(`--radius-${key}`, value);
  });
  
  Object.entries(themeConfig.fontSize).forEach(([key, value]) => {
    root.style.setProperty(`--font-size-${key}`, value);
  });
  
  Object.entries(themeConfig.shadows).forEach(([key, value]) => {
    root.style.setProperty(`--shadow-${key}`, value);
  });
  
  Object.entries(themeConfig.transitions).forEach(([key, value]) => {
    root.style.setProperty(`--transition-${key}`, value);
  });
}
