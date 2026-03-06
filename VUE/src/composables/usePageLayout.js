/**
 * Composable for consistent page layout and styling
 */
export function usePageLayout() {
  /**
   * Get page container classes
   */
  const getPageContainerClass = (options = {}) => {
    const {
      fullHeight = false,
      noPadding = false,
      centered = false
    } = options

    const classes = ['page-container']
    
    if (fullHeight) classes.push('page-container--full-height')
    if (noPadding) classes.push('page-container--no-padding')
    if (centered) classes.push('page-container--centered')

    return classes.join(' ')
  }

  /**
   * Get section classes
   */
  const getSectionClass = (options = {}) => {
    const {
      bordered = false,
      padded = true,
      elevated = false
    } = options

    const classes = ['page-section']
    
    if (bordered) classes.push('page-section--bordered')
    if (padded) classes.push('page-section--padded')
    if (elevated) classes.push('page-section--elevated')

    return classes.join(' ')
  }

  /**
   * Get form container classes
   */
  const getFormContainerClass = (options = {}) => {
    const {
      inline = false,
      compact = false
    } = options

    const classes = ['form-container']
    
    if (inline) classes.push('form-container--inline')
    if (compact) classes.push('form-container--compact')

    return classes.join(' ')
  }

  /**
   * Get button group classes
   */
  const getButtonGroupClass = (options = {}) => {
    const {
      align = 'left', // left, center, right
      gap = 'md' // sm, md, lg
    } = options

    const classes = ['button-group']
    classes.push(`button-group--align-${align}`)
    classes.push(`button-group--gap-${gap}`)

    return classes.join(' ')
  }

  /**
   * Get card classes
   */
  const getCardClass = (options = {}) => {
    const {
      hoverable = true,
      shadow = 'md' // sm, md, lg
    } = options

    const classes = ['styled-card']
    
    if (hoverable) classes.push('styled-card--hoverable')
    classes.push(`styled-card--shadow-${shadow}`)

    return classes.join(' ')
  }

  return {
    getPageContainerClass,
    getSectionClass,
    getFormContainerClass,
    getButtonGroupClass,
    getCardClass
  }
}
