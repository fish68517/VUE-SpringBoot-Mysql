package com.health.service;

import com.health.entity.Consultation;
import com.health.entity.Doctor;
import com.health.entity.HealthAdvice;
import com.health.entity.HealthData;
import com.health.entity.User;
import com.health.repository.ConsultationRepository;
import com.health.repository.DoctorRepository;
import com.health.repository.HealthAdviceRepository;
import com.health.repository.HealthDataRepository;
import com.health.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 医师服务类
 * 处理医师相关的业务逻辑
 */
@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private HealthAdviceRepository healthAdviceRepository;

    @Autowired
    private HealthDataRepository healthDataRepository;

    /**
     * 获取医师的患者列表
     * 通过咨询记录和健康建议找到医师关联的患者
     *
     * @param doctorId 医师ID
     * @return 患者列表
     */
    public List<Map<String, Object>> getDoctorPatients(Long doctorId) {
        // 验证医师是否存在
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("医师不存在"));

        // 获取医师通过咨询关联的患者ID集合
        List<Consultation> consultations = consultationRepository.findByDoctorId(doctorId);
        Set<Long> patientIds = consultations.stream()
                .map(Consultation::getUserId)
                .collect(Collectors.toSet());

        // 获取医师通过健康建议关联的患者ID集合
        List<HealthAdvice> healthAdvices = healthAdviceRepository.findByDoctorId(doctorId);
        patientIds.addAll(healthAdvices.stream()
                .map(HealthAdvice::getUserId)
                .collect(Collectors.toSet()));

        // 构建患者信息列表
        List<Map<String, Object>> patientList = new ArrayList<>();
        for (Long patientId : patientIds) {
            User patient = userRepository.findById(patientId).orElse(null);
            if (patient != null) {
                Map<String, Object> patientInfo = new HashMap<>();
                patientInfo.put("id", patient.getId());
                patientInfo.put("username", patient.getUsername());
                patientInfo.put("name", patient.getName());
                patientInfo.put("age", patient.getAge());
                patientInfo.put("gender", patient.getGender());
                patientInfo.put("phone", patient.getPhone());
                patientInfo.put("email", patient.getEmail());
                patientList.add(patientInfo);
            }
        }

        return patientList;
    }

    /**
     * 获取患者的完整档案
     * 包含患者个人信息、健康数据、咨询记录和健康建议
     *
     * @param doctorId 医师ID
     * @param patientId 患者ID
     * @return 患者档案信息
     */
    public Map<String, Object> getPatientRecord(Long doctorId, Long patientId) {
        // 验证医师是否存在
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("医师不存在"));

        // 验证患者是否存在
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("患者不存在"));

        // 检查医师是否有权限访问该患者的档案
        // 医师只能访问通过咨询或健康建议关联的患者
        List<Consultation> consultations = consultationRepository.findByDoctorId(doctorId);
        List<HealthAdvice> healthAdvices = healthAdviceRepository.findByDoctorId(doctorId);

        boolean hasAccess = consultations.stream().anyMatch(c -> c.getUserId().equals(patientId)) ||
                healthAdvices.stream().anyMatch(h -> h.getUserId().equals(patientId));

        if (!hasAccess) {
            throw new IllegalArgumentException("您没有权限访问该患者的档案");
        }

        // 构建患者档案
        Map<String, Object> patientRecord = new HashMap<>();

        // 个人信息
        Map<String, Object> personalInfo = new HashMap<>();
        personalInfo.put("id", patient.getId());
        personalInfo.put("username", patient.getUsername());
        personalInfo.put("name", patient.getName());
        personalInfo.put("age", patient.getAge());
        personalInfo.put("gender", patient.getGender());
        personalInfo.put("phone", patient.getPhone());
        personalInfo.put("email", patient.getEmail());
        personalInfo.put("createdAt", patient.getCreatedAt());
        patientRecord.put("personalInfo", personalInfo);

        // 健康数据
        List<HealthData> healthDataList = healthDataRepository.findByUserId(patientId);
        List<Map<String, Object>> healthDataInfo = new ArrayList<>();
        for (HealthData data : healthDataList) {
            Map<String, Object> dataInfo = new HashMap<>();
            dataInfo.put("id", data.getId());
            dataInfo.put("height", data.getHeight());
            dataInfo.put("weight", data.getWeight());
            dataInfo.put("bloodPressure", data.getBloodPressure());
            dataInfo.put("heartRate", data.getHeartRate());
            dataInfo.put("dietRecord", data.getDietRecord());
            dataInfo.put("exerciseRecord", data.getExerciseRecord());
            dataInfo.put("dataType", data.getDataType());
            dataInfo.put("recordedAt", data.getRecordedAt());
            dataInfo.put("createdAt", data.getCreatedAt());
            healthDataInfo.add(dataInfo);
        }
        patientRecord.put("healthData", healthDataInfo);

        // 咨询记录
        List<Consultation> patientConsultations = consultationRepository.findByUserId(patientId);
        List<Map<String, Object>> consultationInfo = new ArrayList<>();
        for (Consultation consultation : patientConsultations) {
            Map<String, Object> consInfo = new HashMap<>();
            consInfo.put("id", consultation.getId());
            consInfo.put("question", consultation.getQuestion());
            consInfo.put("answer", consultation.getAnswer());
            consInfo.put("status", consultation.getStatus());
            consInfo.put("createdAt", consultation.getCreatedAt());
            consInfo.put("answeredAt", consultation.getAnsweredAt());
            consultationInfo.add(consInfo);
        }
        patientRecord.put("consultations", consultationInfo);

        // 健康建议
        List<HealthAdvice> patientAdvices = healthAdviceRepository.findByUserId(patientId);
        List<Map<String, Object>> adviceInfo = new ArrayList<>();
        for (HealthAdvice advice : patientAdvices) {
            Map<String, Object> advInfo = new HashMap<>();
            advInfo.put("id", advice.getId());
            advInfo.put("adviceContent", advice.getAdviceContent());
            advInfo.put("recommendation", advice.getRecommendation());
            advInfo.put("createdAt", advice.getCreatedAt());
            advInfo.put("updatedAt", advice.getUpdatedAt());
            adviceInfo.add(advInfo);
        }
        patientRecord.put("healthAdvice", adviceInfo);

        return patientRecord;
    }

    /**
     * 获取所有医师列表
     * 仅管理员可以调用此方法
     *
     * @return 医师列表
     */
    public List<Map<String, Object>> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Map<String, Object>> doctorList = new ArrayList<>();

        for (Doctor doctor : doctors) {
            User user = userRepository.findById(doctor.getUserId()).orElse(null);
            if (user != null) {
                Map<String, Object> doctorInfo = new HashMap<>();
                doctorInfo.put("id", doctor.getId());
                doctorInfo.put("userId", doctor.getUserId());
                doctorInfo.put("username", user.getUsername());
                doctorInfo.put("name", user.getName());
                doctorInfo.put("licenseNumber", doctor.getLicenseNumber());
                doctorInfo.put("specialization", doctor.getSpecialization());
                doctorInfo.put("hospital", doctor.getHospital());
                doctorInfo.put("verified", doctor.getVerified());
                doctorInfo.put("createdAt", doctor.getCreatedAt());
                doctorList.add(doctorInfo);
            }
        }

        return doctorList;
    }
}
