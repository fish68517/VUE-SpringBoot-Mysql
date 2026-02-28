package com.health.util;

import com.health.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证工具类单元测试
 * 测试ValidationUtil中的各种验证方法
 */
public class ValidationUtilTest {

    @Test
    public void testValidateUsernameSuccess() {
        // 测试有效的用户名
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("user123"));
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("test_user"));
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("abc"));
    }

    @Test
    public void testValidateUsernameFailure() {
        // 测试无效的用户名
        assertThrows(BusinessException.class, () -> ValidationUtil.validateUsername(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateUsername(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateUsername("ab")); // 太短
        assertThrows(BusinessException.class, () -> ValidationUtil.validateUsername("user@123")); // 包含特殊字符
    }

    @Test
    public void testValidatePasswordSuccess() {
        // 测试有效的密码
        assertDoesNotThrow(() -> ValidationUtil.validatePassword("password123"));
        assertDoesNotThrow(() -> ValidationUtil.validatePassword("123456"));
    }

    @Test
    public void testValidatePasswordFailure() {
        // 测试无效的密码
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePassword(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePassword(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePassword("12345")); // 太短
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePassword("a".repeat(51))); // 太长
    }

    @Test
    public void testValidateEmailSuccess() {
        // 测试有效的邮箱
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("user@example.com"));
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("test.user@domain.co.uk"));
    }

    @Test
    public void testValidateEmailFailure() {
        // 测试无效的邮箱
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail("invalid-email"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateEmail("user@"));
    }

    @Test
    public void testValidatePhoneSuccess() {
        // 测试有效的电话号码
        assertDoesNotThrow(() -> ValidationUtil.validatePhone("13800138000"));
        assertDoesNotThrow(() -> ValidationUtil.validatePhone("15900000000"));
    }

    @Test
    public void testValidatePhoneFailure() {
        // 测试无效的电话号码
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhone(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhone(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhone("12345678901")); // 不是1开头
        assertThrows(BusinessException.class, () -> ValidationUtil.validatePhone("1380013800")); // 位数不对
    }

    @Test
    public void testValidateAgeSuccess() {
        // 测试有效的年龄
        assertDoesNotThrow(() -> ValidationUtil.validateAge(0));
        assertDoesNotThrow(() -> ValidationUtil.validateAge(25));
        assertDoesNotThrow(() -> ValidationUtil.validateAge(150));
    }

    @Test
    public void testValidateAgeFailure() {
        // 测试无效的年龄
        assertThrows(BusinessException.class, () -> ValidationUtil.validateAge(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateAge(-1));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateAge(151));
    }

    @Test
    public void testValidateHeightSuccess() {
        // 测试有效的身高
        assertDoesNotThrow(() -> ValidationUtil.validateHeight(50.0));
        assertDoesNotThrow(() -> ValidationUtil.validateHeight(170.5));
        assertDoesNotThrow(() -> ValidationUtil.validateHeight(250.0));
    }

    @Test
    public void testValidateHeightFailure() {
        // 测试无效的身高
        assertThrows(BusinessException.class, () -> ValidationUtil.validateHeight(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateHeight(49.9));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateHeight(250.1));
    }

    @Test
    public void testValidateWeightSuccess() {
        // 测试有效的体重
        assertDoesNotThrow(() -> ValidationUtil.validateWeight(10.0));
        assertDoesNotThrow(() -> ValidationUtil.validateWeight(70.5));
        assertDoesNotThrow(() -> ValidationUtil.validateWeight(300.0));
    }

    @Test
    public void testValidateWeightFailure() {
        // 测试无效的体重
        assertThrows(BusinessException.class, () -> ValidationUtil.validateWeight(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateWeight(9.9));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateWeight(300.1));
    }

    @Test
    public void testValidateHeartRateSuccess() {
        // 测试有效的心率
        assertDoesNotThrow(() -> ValidationUtil.validateHeartRate(30));
        assertDoesNotThrow(() -> ValidationUtil.validateHeartRate(72));
        assertDoesNotThrow(() -> ValidationUtil.validateHeartRate(200));
    }

    @Test
    public void testValidateHeartRateFailure() {
        // 测试无效的心率
        assertThrows(BusinessException.class, () -> ValidationUtil.validateHeartRate(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateHeartRate(29));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateHeartRate(201));
    }

    @Test
    public void testValidateBloodPressureSuccess() {
        // 测试有效的血压
        assertDoesNotThrow(() -> ValidationUtil.validateBloodPressure("120/80"));
        assertDoesNotThrow(() -> ValidationUtil.validateBloodPressure("140/90"));
    }

    @Test
    public void testValidateBloodPressureFailure() {
        // 测试无效的血压
        assertThrows(BusinessException.class, () -> ValidationUtil.validateBloodPressure(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateBloodPressure(""));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateBloodPressure("120")); // 格式错误
        assertThrows(BusinessException.class, () -> ValidationUtil.validateBloodPressure("50/80")); // 收缩压过低
        assertThrows(BusinessException.class, () -> ValidationUtil.validateBloodPressure("120/150")); // 舒张压过高
    }

    @Test
    public void testValidateNotEmpty() {
        // 测试字符串不为空
        assertDoesNotThrow(() -> ValidationUtil.validateNotEmpty("test", "字段"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateNotEmpty(null, "字段"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateNotEmpty("", "字段"));
    }

    @Test
    public void testValidateNotNull() {
        // 测试对象不为空
        assertDoesNotThrow(() -> ValidationUtil.validateNotNull("test", "字段"));
        assertDoesNotThrow(() -> ValidationUtil.validateNotNull(123, "字段"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateNotNull(null, "字段"));
    }

    @Test
    public void testValidateStringLength() {
        // 测试字符串长度
        assertDoesNotThrow(() -> ValidationUtil.validateStringLength("test", 1, 10, "字段"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateStringLength("test", 5, 10, "字段")); // 太短
        assertThrows(BusinessException.class, () -> ValidationUtil.validateStringLength("test", 1, 3, "字段")); // 太长
    }

    @Test
    public void testValidateLicenseNumber() {
        // 测试执业证号
        assertDoesNotThrow(() -> ValidationUtil.validateLicenseNumber("1234567890"));
        assertDoesNotThrow(() -> ValidationUtil.validateLicenseNumber("12345678901234567890"));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateLicenseNumber(null));
        assertThrows(BusinessException.class, () -> ValidationUtil.validateLicenseNumber("123456789")); // 太短
    }
}
