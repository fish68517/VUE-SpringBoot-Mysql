package com.sharkfitness.config;

import com.sharkfitness.entity.*;
import com.sharkfitness.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Data initialization component that creates default admin account and sample data
 * for testing and demonstration purposes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final DynamicRepository dynamicRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Check if data already exists
        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        try {
            // Create default admin account
            createDefaultAdmin();
            
            // Create sample coach profiles
            User coach1 = createCoach("coach_mike", "Mike Johnson", "Strength training specialist with 10 years experience");
            User coach2 = createCoach("coach_sarah", "Sarah Williams", "Yoga and flexibility expert, certified instructor");
            User coach3 = createCoach("coach_david", "David Chen", "Cardio and endurance training professional");
            
            // Create sample fitness resources
            createSampleResources(coach1, coach2, coach3);
            
            // Create sample community posts
            createSamplePosts(coach1, coach2);
            
            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during data initialization", e);
        }
    }

    private void createDefaultAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setRole("admin");
        admin.setGender("male");
        admin.setIntro("System administrator");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(admin);
        log.info("Created default admin account: username=admin, password=admin123");
    }

    private User createCoach(String username, String displayName, String intro) {
        User coach = new User();
        coach.setUsername(username);
        coach.setPassword("coach123");
        coach.setRole("coach");
        coach.setGender("male");
        coach.setIntro(intro);
        coach.setCreatedAt(LocalDateTime.now());
        coach.setUpdatedAt(LocalDateTime.now());
        
        User savedCoach = userRepository.save(coach);
        log.info("Created coach account: username={}", username);
        return savedCoach;
    }

    private void createSampleResources(User coach1, User coach2, User coach3) {
        // Video resource
        FitnessResource video1 = new FitnessResource();
        video1.setTitle("Beginner's Guide to Weight Training");
        video1.setDescription("Learn the fundamentals of weight training with proper form and technique. Perfect for beginners starting their fitness journey.");
        video1.setContentType("video");
        video1.setFileUrl("/uploads/videos/sample-weight-training.mp4");
        video1.setCreator(coach1);
        video1.setViewCount(0);
        video1.setCreatedAt(LocalDateTime.now());
        video1.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(video1);

        // Article resource
        FitnessResource article1 = new FitnessResource();
        article1.setTitle("10 Essential Yoga Poses for Flexibility");
        article1.setDescription("Discover the most effective yoga poses to improve your flexibility and reduce stress.");
        article1.setContentType("article");
        article1.setContent("Yoga is an ancient practice that combines physical postures, breathing techniques, and meditation. " +
                "Here are 10 essential poses that will help you build flexibility:\n\n" +
                "1. Downward-Facing Dog (Adho Mukha Svanasana)\n" +
                "2. Child's Pose (Balasana)\n" +
                "3. Warrior I (Virabhadrasana I)\n" +
                "4. Warrior II (Virabhadrasana II)\n" +
                "5. Triangle Pose (Trikonasana)\n" +
                "6. Tree Pose (Vrksasana)\n" +
                "7. Bridge Pose (Setu Bandha Sarvangasana)\n" +
                "8. Cobra Pose (Bhujangasana)\n" +
                "9. Seated Forward Bend (Paschimottanasana)\n" +
                "10. Corpse Pose (Savasana)\n\n" +
                "Practice these poses regularly for best results.");
        article1.setCreator(coach2);
        article1.setViewCount(0);
        article1.setCreatedAt(LocalDateTime.now());
        article1.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(article1);

        // Document resource
        FitnessResource document1 = new FitnessResource();
        document1.setTitle("Complete Nutrition Guide for Athletes");
        document1.setDescription("Comprehensive guide covering macronutrients, meal timing, and supplementation for optimal athletic performance.");
        document1.setContentType("document");
        document1.setFileUrl("/uploads/documents/nutrition-guide.pdf");
        document1.setCreator(coach3);
        document1.setViewCount(0);
        document1.setCreatedAt(LocalDateTime.now());
        document1.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(document1);

        // Video resource 2
        FitnessResource video2 = new FitnessResource();
        video2.setTitle("High-Intensity Interval Training (HIIT) Workout");
        video2.setDescription("30-minute HIIT workout to burn calories and improve cardiovascular fitness. No equipment needed!");
        video2.setContentType("video");
        video2.setFileUrl("/uploads/videos/sample-hiit-workout.mp4");
        video2.setCreator(coach3);
        video2.setViewCount(0);
        video2.setCreatedAt(LocalDateTime.now());
        video2.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(video2);

        // Article resource 2
        FitnessResource article2 = new FitnessResource();
        article2.setTitle("Understanding Rest and Recovery");
        article2.setDescription("Why rest days are crucial for muscle growth and injury prevention.");
        article2.setContentType("article");
        article2.setContent("Rest and recovery are often overlooked aspects of fitness, but they are just as important as your workouts.\n\n" +
                "Why Rest Matters:\n" +
                "- Muscle repair and growth occur during rest periods\n" +
                "- Prevents overtraining and burnout\n" +
                "- Reduces risk of injury\n" +
                "- Improves performance in subsequent workouts\n" +
                "- Supports immune system function\n\n" +
                "How to Optimize Recovery:\n" +
                "1. Get 7-9 hours of quality sleep each night\n" +
                "2. Stay hydrated throughout the day\n" +
                "3. Consume adequate protein for muscle repair\n" +
                "4. Include active recovery activities like walking or light stretching\n" +
                "5. Listen to your body and take extra rest when needed\n\n" +
                "Remember: Progress happens during recovery, not during the workout itself!");
        article2.setCreator(coach1);
        article2.setViewCount(0);
        article2.setCreatedAt(LocalDateTime.now());
        article2.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(article2);

        log.info("Created 5 sample fitness resources");
    }

    private void createSamplePosts(User coach1, User coach2) {
        // Post 1
        Dynamic post1 = new Dynamic();
        post1.setContent("Just finished an amazing morning workout! Remember, consistency is key to achieving your fitness goals. " +
                "Start your day with some exercise and you'll feel energized all day long! 💪 #FitnessMotivation #MorningWorkout");
        post1.setUser(coach1);
        post1.setLikeCount(0);
        post1.setCommentCount(0);
        post1.setStatus("approved");
        post1.setCreatedAt(LocalDateTime.now().minusDays(2));
        post1.setUpdatedAt(LocalDateTime.now().minusDays(2));
        dynamicRepository.save(post1);

        // Post 2
        Dynamic post2 = new Dynamic();
        post2.setContent("Yoga tip of the day: Focus on your breathing! Proper breathing technique can enhance your practice " +
                "and help you achieve deeper stretches. Inhale deeply through your nose, exhale slowly through your mouth. 🧘‍♀️ #YogaTips #Mindfulness");
        post2.setUser(coach2);
        post2.setLikeCount(0);
        post2.setCommentCount(0);
        post2.setStatus("approved");
        post2.setCreatedAt(LocalDateTime.now().minusDays(1));
        post2.setUpdatedAt(LocalDateTime.now().minusDays(1));
        dynamicRepository.save(post2);

        // Post 3
        Dynamic post3 = new Dynamic();
        post3.setContent("Nutrition reminder: Don't forget to fuel your body properly! Eating a balanced meal with protein, " +
                "carbs, and healthy fats within 2 hours after your workout helps with recovery and muscle growth. 🥗 #NutritionTips #PostWorkout");
        post3.setUser(coach1);
        post3.setLikeCount(0);
        post3.setCommentCount(0);
        post3.setStatus("approved");
        post3.setCreatedAt(LocalDateTime.now().minusHours(12));
        post3.setUpdatedAt(LocalDateTime.now().minusHours(12));
        dynamicRepository.save(post3);

        log.info("Created 3 sample community posts");
    }
}
