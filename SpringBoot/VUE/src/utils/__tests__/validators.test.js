import { describe, it, expect } from 'vitest'
import {
  validateEmail,
  validatePhone,
  validateUsername,
  validatePassword,
  validateHeight,
  validateWeight,
  validateHeartRate,
  validateAge,
  validateDateFormat,
  validateRequired
} from '../validators'

describe('表单验证工具函数', () => {
  describe('validateEmail', () => {
    it('应该验证有效的邮箱格式', () => {
      expect(validateEmail('user@example.com')).toBe(true)
      expect(validateEmail('test.email@domain.co.uk')).toBe(true)
    })

    it('应该拒绝无效的邮箱格式', () => {
      expect(validateEmail('invalid.email')).toBe(false)
      expect(validateEmail('user@')).toBe(false)
      expect(validateEmail('@example.com')).toBe(false)
    })
  })

  describe('validatePhone', () => {
    it('应该验证有效的手机号码', () => {
      expect(validatePhone('13800138000')).toBe(true)
      expect(validatePhone('15612345678')).toBe(true)
    })

    it('应该拒绝无效的手机号码', () => {
      expect(validatePhone('12345678901')).toBe(false)
      expect(validatePhone('1380013800')).toBe(false)
      expect(validatePhone('abc12345678')).toBe(false)
    })
  })

  describe('validateUsername', () => {
    it('应该验证有效的用户名', () => {
      expect(validateUsername('user123')).toBe(true)
      expect(validateUsername('test_user')).toBe(true)
      expect(validateUsername('abc')).toBe(true)
    })

    it('应该拒绝无效的用户名', () => {
      expect(validateUsername('ab')).toBe(false) // 太短
      expect(validateUsername('user@123')).toBe(false) // 包含特殊字符
      expect(validateUsername('a'.repeat(21))).toBe(false) // 太长
    })
  })

  describe('validatePassword', () => {
    it('应该验证有效的密码', () => {
      expect(validatePassword('password123')).toBe(true)
      expect(validatePassword('123456')).toBe(true)
    })

    it('应该拒绝无效的密码', () => {
      expect(validatePassword('12345')).toBe(false) // 太短
      expect(validatePassword('')).toBe(false)
      expect(validatePassword(null)).toBe(false)
    })
  })

  describe('validateHeight', () => {
    it('应该验证有效的身高范围', () => {
      expect(validateHeight('170')).toBe(true)
      expect(validateHeight('100')).toBe(true)
      expect(validateHeight('250')).toBe(true)
    })

    it('应该拒绝超出范围的身高', () => {
      expect(validateHeight('99')).toBe(false)
      expect(validateHeight('251')).toBe(false)
      expect(validateHeight('50')).toBe(false)
    })
  })

  describe('validateWeight', () => {
    it('应该验证有效的体重范围', () => {
      expect(validateWeight('70')).toBe(true)
      expect(validateWeight('20')).toBe(true)
      expect(validateWeight('200')).toBe(true)
    })

    it('应该拒绝超出范围的体重', () => {
      expect(validateWeight('19')).toBe(false)
      expect(validateWeight('201')).toBe(false)
      expect(validateWeight('10')).toBe(false)
    })
  })

  describe('validateHeartRate', () => {
    it('应该验证有效的心率范围', () => {
      expect(validateHeartRate('72')).toBe(true)
      expect(validateHeartRate('30')).toBe(true)
      expect(validateHeartRate('200')).toBe(true)
    })

    it('应该拒绝超出范围的心率', () => {
      expect(validateHeartRate('29')).toBe(false)
      expect(validateHeartRate('201')).toBe(false)
      expect(validateHeartRate('10')).toBe(false)
    })
  })

  describe('validateAge', () => {
    it('应该验证有效的年龄范围', () => {
      expect(validateAge('25')).toBe(true)
      expect(validateAge('1')).toBe(true)
      expect(validateAge('150')).toBe(true)
    })

    it('应该拒绝超出范围的年龄', () => {
      expect(validateAge('0')).toBe(false)
      expect(validateAge('151')).toBe(false)
      expect(validateAge('-1')).toBe(false)
    })
  })

  describe('validateDateFormat', () => {
    it('应该验证有效的日期格式', () => {
      expect(validateDateFormat('2024-01-15')).toBe(true)
      expect(validateDateFormat('2023-12-31')).toBe(true)
    })

    it('应该拒绝无效的日期格式', () => {
      expect(validateDateFormat('2024/01/15')).toBe(false)
      expect(validateDateFormat('01-15-2024')).toBe(false)
      expect(validateDateFormat('2024-1-15')).toBe(false)
    })
  })

  describe('validateRequired', () => {
    it('应该验证必填字段', () => {
      expect(validateRequired('value')).toBe(true)
      expect(validateRequired('0')).toBe(true)
      expect(validateRequired(false)).toBe(true)
    })

    it('应该拒绝空值', () => {
      expect(validateRequired('')).toBe(false)
      expect(validateRequired(null)).toBe(false)
      expect(validateRequired(undefined)).toBe(false)
    })
  })
})
