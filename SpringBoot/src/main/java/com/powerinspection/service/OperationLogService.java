package com.powerinspection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerinspection.entity.OperationLog;
import com.powerinspection.entity.User;
import com.powerinspection.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;
    private final ObjectMapper objectMapper;

    public void save(String module, String action, boolean success, String message, Object details, User user) {
        OperationLog log = new OperationLog();
        log.setModule(module);
        log.setAction(action);
        log.setSuccess(success);
        log.setMessage(message);
        log.setDetails(toJson(details));
        if (user != null) {
            log.setUsername(user.getUsername());
            log.setRealName(user.getRealName());
            log.setRole(user.getRole().name());
        }

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            log.setRequestMethod(request.getMethod());
            log.setRequestPath(request.getRequestURI());
            log.setIp(request.getRemoteAddr());
        }
        operationLogRepository.save(log);
    }

    public Object sanitizeArgs(Object[] args) {
        if (args == null) {
            return null;
        }
        return Arrays.stream(args)
                .map(arg -> arg == null ? null : arg.getClass().getSimpleName())
                .toList();
    }

    private String toJson(Object details) {
        if (details == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(details);
            return json.length() > 1800 ? json.substring(0, 1800) : json;
        } catch (JsonProcessingException ex) {
            return String.valueOf(details);
        }
    }
}
