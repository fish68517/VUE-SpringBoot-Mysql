package com.naxi.service;

import com.naxi.entity.Question;
import com.naxi.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    /**
     * 发布提问
     * 需求: 14.1, 14.2
     */
    public Question publishQuestion(Long userId, Long patternId, String title, String content) {
        Question question = new Question();
        question.setUserId(userId);
        question.setPatternId(patternId);
        question.setTitle(title);
        question.setContent(content);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        return questionRepository.save(question);
    }

    /**
     * 获取纹样提问列表（支持分页）
     * 需求: 14.1, 14.2
     */
    public Page<Question> getPatternQuestions(Long patternId, Pageable pageable) {
        return questionRepository.findByPatternId(patternId, pageable);
    }

    /**
     * 删除提问（管理员）
     * 需求: 14.1, 14.2
     */
    public boolean deleteQuestion(Long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            questionRepository.deleteById(questionId);
            return true;
        }
        return false;
    }

    /**
     * 获取提问详情
     */
    public Question getQuestionById(Long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.orElse(null);
    }
}
