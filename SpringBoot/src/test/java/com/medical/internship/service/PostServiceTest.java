package com.medical.internship.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.common.AccessDeniedException;
import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.PostCreateRequest;
import com.medical.internship.dto.PostResponse;
import com.medical.internship.dto.PostStatusUpdateRequest;
import com.medical.internship.dto.PostUpdateRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.entity.Application;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.Post;
import com.medical.internship.repository.ApplicationRepository;
import com.medical.internship.repository.OrganizationRepository;
import com.medical.internship.repository.PostRepository;
import com.medical.internship.service.impl.PostServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 岗位服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private OrganizationRepository organizationRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private PostServiceImpl postService;
    
    private Organization testHospital;
    private Organization testSchool;
    private Post testPost;
    private UserResponse hospitalAdminUser;
    private UserResponse studentUser;
    
    @BeforeEach
    public void setUp() {
        testHospital = Organization.builder()
                .id(1L)
                .name("测试医院")
                .type("HOSPITAL")
                .code("TEST_HOSPITAL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        testSchool = Organization.builder()
                .id(2L)
                .name("测试学校")
                .type("SCHOOL")
                .code("TEST_SCHOOL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        testPost = Post.builder()
                .id(1L)
                .hospital(testHospital)
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchools("[2]")
                .status("PUBLISHED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        OrganizationResponse hospitalResponse = OrganizationResponse.builder()
                .id(1L)
                .name("测试医院")
                .type("HOSPITAL")
                .code("TEST_HOSPITAL_001")
                .build();
        
        hospitalAdminUser = UserResponse.builder()
                .id(1L)
                .username("hospital_admin")
                .role("HOSPITAL_ADMIN")
                .organization(hospitalResponse)
                .build();
        
        studentUser = UserResponse.builder()
                .id(2L)
                .username("student")
                .role("STUDENT")
                .organization(OrganizationResponse.builder()
                        .id(2L)
                        .name("测试学校")
                        .type("SCHOOL")
                        .code("TEST_SCHOOL_001")
                        .build())
                .build();
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    @Test
    public void testCreatePostSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminUser);
        
        // 准备测试数据
        PostCreateRequest request = PostCreateRequest.builder()
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(2L))
                .build();
        
        // 模拟repository行为
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(applicationRepository.countByPostId(1L)).thenReturn(0L);
        
        // 执行测试
        PostResponse response = postService.createPost(request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("内科实习岗位", response.getTitle());
        assertEquals("DRAFT", response.getStatus());
        
        // 验证方法调用
        verify(organizationRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }
    
    @Test
    public void testCreatePostAccessDenied() {
        // 设置会话上下文 - 学生用户
        SessionContext.setCurrentUser(studentUser);
        
        // 准备测试数据
        PostCreateRequest request = PostCreateRequest.builder()
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .build();
        
        // 执行测试并验证异常
        assertThrows(AccessDeniedException.class, () -> postService.createPost(request));
    }
    
    @Test
    public void testUpdatePostSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminUser);
        
        // 准备测试数据
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("更新后的岗位名称")
                .quota(10)
                .build();
        
        // 模拟repository行为
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(applicationRepository.countByPostId(1L)).thenReturn(0L);
        
        // 执行测试
        PostResponse response = postService.updatePost(1L, request);
        
        // 验证结果
        assertNotNull(response);
        
        // 验证方法调用
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }
    
    @Test
    public void testUpdatePostAccessDenied() {
        // 设置会话上下文 - 不同医院的管理员
        OrganizationResponse otherHospital = OrganizationResponse.builder()
                .id(99L)
                .name("其他医院")
                .type("HOSPITAL")
                .code("OTHER_HOSPITAL")
                .build();
        
        UserResponse otherHospitalAdmin = UserResponse.builder()
                .id(99L)
                .username("other_admin")
                .role("HOSPITAL_ADMIN")
                .organization(otherHospital)
                .build();
        
        SessionContext.setCurrentUser(otherHospitalAdmin);
        
        // 准备测试数据
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("更新后的岗位名称")
                .build();
        
        // 模拟repository行为
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        
        // 执行测试并验证异常
        assertThrows(AccessDeniedException.class, () -> postService.updatePost(1L, request));
    }
    
    @Test
    public void testUpdatePostStatusSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminUser);
        
        // 准备测试数据
        PostStatusUpdateRequest request = PostStatusUpdateRequest.builder()
                .status("ARCHIVED")
                .build();
        
        // 模拟repository行为
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(applicationRepository.countByPostId(1L)).thenReturn(0L);
        
        // 执行测试
        PostResponse response = postService.updatePostStatus(1L, request);
        
        // 验证结果
        assertNotNull(response);
        
        // 验证方法调用
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }
    
    @Test
    public void testIsPostQuotaFull() {
        // 准备测试数据 - 已有5个已批准的申请
        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Application app = Application.builder()
                    .id((long) i)
                    .hospitalStatus("APPROVED")
                    .build();
            applications.add(app);
        }
        
        // 模拟repository行为
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(applicationRepository.findByPostId(1L)).thenReturn(applications);
        
        // 执行测试
        boolean isFull = postService.isPostQuotaFull(1L);
        
        // 验证结果
        assertTrue(isFull);
        
        // 验证方法调用
        verify(postRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).findByPostId(1L);
    }
    
    @Test
    public void testIsPostQuotaNotFull() {
        // 准备测试数据 - 只有3个已批准的申请
        List<Application> applications = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Application app = Application.builder()
                    .id((long) i)
                    .hospitalStatus("APPROVED")
                    .build();
            applications.add(app);
        }
        
        // 模拟repository行为
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(applicationRepository.findByPostId(1L)).thenReturn(applications);
        
        // 执行测试
        boolean isFull = postService.isPostQuotaFull(1L);
        
        // 验证结果
        assertFalse(isFull);
        
        // 验证方法调用
        verify(postRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).findByPostId(1L);
    }
    
    @Test
    public void testGetPostDetailSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminUser);
        
        // 模拟repository行为
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(applicationRepository.countByPostId(1L)).thenReturn(0L);
        
        // 执行测试
        PostResponse response = postService.getPostDetail(1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("内科实习岗位", response.getTitle());
        
        // 验证方法调用
        verify(postRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetPostDetailNotFound() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminUser);
        
        // 模拟repository行为
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostDetail(999L));
        
        // 验证方法调用
        verify(postRepository, times(1)).findById(999L);
    }
}
