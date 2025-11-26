import { ref, computed } from 'vue'

/**
 * Composable for managing pagination
 * @param {number} initialPageSize - Initial page size (default: 10)
 * @returns {object} - Pagination state and methods
 */
export const usePagination = (initialPageSize = 10) => {
  const currentPage = ref(1)
  const pageSize = ref(initialPageSize)
  const total = ref(0)

  /**
   * Calculate total pages
   */
  const totalPages = computed(() => {
    return Math.ceil(total.value / pageSize.value)
  })

  /**
   * Check if there is a previous page
   */
  const hasPreviousPage = computed(() => {
    return currentPage.value > 1
  })

  /**
   * Check if there is a next page
   */
  const hasNextPage = computed(() => {
    return currentPage.value < totalPages.value
  })

  /**
   * Get the starting index for the current page (0-based)
   */
  const startIndex = computed(() => {
    return (currentPage.value - 1) * pageSize.value
  })

  /**
   * Get the ending index for the current page (0-based)
   */
  const endIndex = computed(() => {
    return Math.min(currentPage.value * pageSize.value, total.value)
  })

  /**
   * Go to the next page
   */
  const nextPage = () => {
    if (hasNextPage.value) {
      currentPage.value++
    }
  }

  /**
   * Go to the previous page
   */
  const previousPage = () => {
    if (hasPreviousPage.value) {
      currentPage.value--
    }
  }

  /**
   * Go to a specific page
   * @param {number} page - Page number
   */
  const goToPage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
      currentPage.value = page
    }
  }

  /**
   * Reset pagination to first page
   */
  const reset = () => {
    currentPage.value = 1
  }

  /**
   * Set the total number of items
   * @param {number} count - Total count
   */
  const setTotal = (count) => {
    total.value = count
  }

  /**
   * Set the page size
   * @param {number} size - Page size
   */
  const setPageSize = (size) => {
    pageSize.value = size
    reset()
  }

  return {
    currentPage,
    pageSize,
    total,
    totalPages,
    hasPreviousPage,
    hasNextPage,
    startIndex,
    endIndex,
    nextPage,
    previousPage,
    goToPage,
    reset,
    setTotal,
    setPageSize
  }
}
