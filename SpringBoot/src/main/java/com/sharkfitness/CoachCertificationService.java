package com.sharkfitness;

import com.sharkfitness.entity.CoachCertification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CoachCertificationService {

    @Autowired
    private CoachCertificationMapper certMapper;

    public List<CoachCertification> getMyCertifications(Long coachId) {
        return certMapper.findByCoachId(coachId);
    }

    public void addCertification(CoachCertification cert) {
        // 默认状态为 0 (待审核)
        cert.setStatus(0);
        certMapper.insert(cert);
    }

    public void updateCertification(CoachCertification cert) {
        certMapper.update(cert);
    }

    public void deleteCertification(Long id, Long coachId) {
        certMapper.delete(id, coachId);
    }
}