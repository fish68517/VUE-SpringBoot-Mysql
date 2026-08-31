import request from '@/utils/request'
import type { PageQuery, PageResult, ResponseResult } from '@/types/api'
import type { ResourceRecord } from '@/types/system'

const prefix = '/api/web/v1'

export const getResourcePage = (module: string, resource: string, params: PageQuery) =>
  request.get(`${prefix}/${module}/${resource}/page`, { params }) as unknown as Promise<
    ResponseResult<PageResult<ResourceRecord>>
  >

export const createResource = (module: string, resource: string, data: ResourceRecord) =>
  request.post(`${prefix}/${module}/${resource}`, data) as unknown as Promise<
    ResponseResult<number>
  >

export const updateResource = (module: string, resource: string, data: ResourceRecord) =>
  request.post(`${prefix}/${module}/${resource}/update`, data) as unknown as Promise<
    ResponseResult<null>
  >

export const deleteResource = (module: string, resource: string, id: number) =>
  request.post(`${prefix}/${module}/${resource}/delete`, { id }) as unknown as Promise<
    ResponseResult<null>
  >

export const changeResourceStatus = (
  module: string,
  resource: string,
  id: number,
  status: number,
) =>
  request.post(`${prefix}/${module}/${resource}/changeStatus`, { id, status }) as unknown as Promise<
    ResponseResult<null>
  >
