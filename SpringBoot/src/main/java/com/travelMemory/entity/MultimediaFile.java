package com.travelMemory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "multimedia_files", indexes = {
    @Index(name = "idx_travel_record_id", columnList = "travel_record_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultimediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 修改重点 START ---
    // 必须显式添加 name = "travel_record_id"
    @Column(name = "travel_record_id", nullable = false)
    private Long travelRecordId;
    // --- 修改重点 END ---


    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false, length = 50)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}
