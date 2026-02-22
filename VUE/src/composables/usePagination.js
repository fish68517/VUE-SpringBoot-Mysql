import { ref } from 'vue'
import { PAGINATION } from '@/constants'

/**
 * 分页管理的组合式函数
 */
export function usePagination(initialPageSize = PAGINATION.DEFAULT_PAGE_SIZE) {
  const currentPage = ref(1)
  const pageSize = ref(initialPageSize)
  const total = ref(0)

  /**
   * 重置分页
   */
  function resetPagination() {
    currentPage.value = 1
    pageSize.value = initialPageSize
    total.value = 0
  }

  /**
   * 设置总数
   * @param {number} count - 总数
   */
  function setTotal(count) {
    total.value = count
  }

  /**
   * 处理页码变化
   * @param {number} page - 新页码
   */
  function handlePageChange(page) {
    currentPage.value = page
  }

  /**
   * 处理每页数量变化
   * @param {number} size - 新的每页数量
   */
  function handlePageSizeChange(size) {
    pageSize.value = size
    currentPage.value = 1
  }

  /**
   * 获取分页参数
   * @returns {object} 分页参数
   */
  function getPaginationParams() {
    return {
      page: currentPage.value,
      pageSize: pageSize.value,
    }
  }

  return {
    currentPage,
    pageSize,
    total,
    resetPagination,
    setTotal,
    handlePageChange,
    handlePageSizeChange,
    getPaginationParams,
  }
}
