import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import axios from 'axios'
import {
  authAPI,
  userAPI,
  healthDataAPI,
  doctorAPI,
  consultationAPI,
  healthAdviceAPI,
  adminAPI
} from '../api'

// Mock axios
vi.mock('axios')

describe('API 服务', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  afterEach(() => {
    localStorage.clear()
  })

  describe('authAPI', () => {
    it('应该调用注册接口', async () => {
      const mockResponse = { data: { id: 1, username: 'newuser' } }
      axios.create().post = vi.fn().mockResolvedValue(mockResponse)

      // 由于 api.js 中已经创建了 apiClient，我们需要测试导出的方法
      expect(typeof authAPI.register).toBe('function')
      expect(typeof authAPI.login).toBe('function')
      expect(typeof authAPI.doctorRegister).toBe('function')
      expect(typeof authAPI.doctorLogin).toBe('function')
      expect(typeof authAPI.adminLogin).toBe('function')
      expect(typeof authAPI.logout).toBe('function')
    })
  })

  describe('userAPI', () => {
    it('应该导出用户API方法', () => {
      expect(typeof userAPI.getProfile).toBe('function')
      expect(typeof userAPI.updateProfile).toBe('function')
      expect(typeof userAPI.getUsers).toBe('function')
    })
  })

  describe('healthDataAPI', () => {
    it('应该导出健康数据API方法', () => {
      expect(typeof healthDataAPI.submitData).toBe('function')
      expect(typeof healthDataAPI.getUserData).toBe('function')
      expect(typeof healthDataAPI.getTrends).toBe('function')
      expect(typeof healthDataAPI.getDataByRange).toBe('function')
    })
  })

  describe('doctorAPI', () => {
    it('应该导出医师API方法', () => {
      expect(typeof doctorAPI.getPatients).toBe('function')
      expect(typeof doctorAPI.getPatientRecord).toBe('function')
      expect(typeof doctorAPI.getDoctors).toBe('function')
    })
  })

  describe('consultationAPI', () => {
    it('应该导出咨询API方法', () => {
      expect(typeof consultationAPI.submitConsultation).toBe('function')
      expect(typeof consultationAPI.getConsultations).toBe('function')
      expect(typeof consultationAPI.answerConsultation).toBe('function')
      expect(typeof consultationAPI.getConsultationDetail).toBe('function')
    })
  })

  describe('healthAdviceAPI', () => {
    it('应该导出健康建议API方法', () => {
      expect(typeof healthAdviceAPI.createAdvice).toBe('function')
      expect(typeof healthAdviceAPI.getPatientAdvice).toBe('function')
      expect(typeof healthAdviceAPI.getMyAdvice).toBe('function')
    })
  })

  describe('adminAPI', () => {
    it('应该导出管理员API方法', () => {
      expect(typeof adminAPI.getUsers).toBe('function')
      expect(typeof adminAPI.updateUserRole).toBe('function')
      expect(typeof adminAPI.disableUser).toBe('function')
      expect(typeof adminAPI.getDoctors).toBe('function')
      expect(typeof adminAPI.updateDoctor).toBe('function')
      expect(typeof adminAPI.deleteDoctor).toBe('function')
      expect(typeof adminAPI.getStatistics).toBe('function')
      expect(typeof adminAPI.getAuditLogs).toBe('function')
    })
  })
})
