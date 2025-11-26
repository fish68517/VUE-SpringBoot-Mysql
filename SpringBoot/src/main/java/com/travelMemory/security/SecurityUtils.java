package com.travelMemory.security;

import org.apache.commons.lang3.StringUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Security utility class for input validation and sanitization.
 * Provides methods to prevent XSS, SQL injection, and other security vulnerabilities.
 */
public class SecurityUtils {

    // HTML policy for XSS prevention - allows safe HTML tags
    private static final PolicyFactory HTML_POLICY = new HtmlPolicyBuilder()
            .allowElements("p", "br", "strong", "em", "u", "h1", "h2", "h3", "h4", "h5", "h6",
                    "ul", "ol", "li", "blockquote", "a", "img")
            .allowAttributes("href").onElements("a")
            .allowAttributes("src", "alt", "title").onElements("img")
            .allowAttributes("class").globally()
            .toFactory();

    /**
     * Sanitize HTML content to prevent XSS attacks.
     * Removes potentially dangerous HTML tags and attributes.
     *
     * @param input the input string to sanitize
     * @return sanitized HTML string
     */
    public static String sanitizeHtml(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return HTML_POLICY.sanitize(input);
    }

    /**
     * Sanitize plain text input to prevent XSS attacks.
     * Escapes HTML special characters.
     *
     * @param input the input string to sanitize
     * @return sanitized plain text string
     */
    public static String sanitizeText(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Validate and sanitize email address.
     * Prevents email injection attacks.
     *
     * @param email the email address to validate
     * @return sanitized email address
     * @throws IllegalArgumentException if email format is invalid
     */
    public static String sanitizeEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String sanitized = email.trim().toLowerCase();

        // Check for newline characters (email injection prevention)
        if (sanitized.contains("\n") || sanitized.contains("\r")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Basic email format validation
        if (!sanitized.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        return sanitized;
    }

    /**
     * Validate and sanitize username.
     * Prevents injection attacks through username field.
     *
     * @param username the username to validate
     * @return sanitized username
     * @throws IllegalArgumentException if username format is invalid
     */
    public static String sanitizeUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        String sanitized = username.trim();

        // Check length
        if (sanitized.length() < 3 || sanitized.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }

        // Allow only alphanumeric, underscore, and hyphen
        if (!sanitized.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, underscores, and hyphens");
        }

        return sanitized;
    }

    /**
     * Validate and sanitize search query.
     * Prevents SQL injection and XSS through search parameters.
     *
     * @param query the search query to validate
     * @return sanitized search query
     * @throws IllegalArgumentException if query is invalid
     */
    public static String sanitizeSearchQuery(String query) {
        if (StringUtils.isBlank(query)) {
            return "";
        }

        String sanitized = query.trim();

        // Limit length to prevent DoS attacks
        if (sanitized.length() > 200) {
            throw new IllegalArgumentException("Search query is too long (max 200 characters)");
        }

        // Remove potentially dangerous characters
        sanitized = sanitized.replaceAll("[;'\"\\\\]", "");

        return sanitized;
    }

    /**
     * Validate file name to prevent directory traversal attacks.
     *
     * @param fileName the file name to validate
     * @return sanitized file name
     * @throws IllegalArgumentException if file name is invalid
     */
    public static String sanitizeFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        String sanitized = fileName.trim();

        // Prevent directory traversal
        if (sanitized.contains("..") || sanitized.contains("/") || sanitized.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        // Limit length
        if (sanitized.length() > 255) {
            throw new IllegalArgumentException("File name is too long");
        }

        return sanitized;
    }

    /**
     * Validate numeric ID to prevent injection attacks.
     *
     * @param id the ID to validate
     * @return validated ID
     * @throws IllegalArgumentException if ID is invalid
     */
    public static Long validateNumericId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return id;
    }

    /**
     * Check if a string contains potential SQL injection patterns.
     *
     * @param input the input string to check
     * @return true if potential SQL injection is detected
     */
    public static boolean containsSqlInjectionPattern(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }

        String lowerInput = input.toLowerCase();
        String[] sqlKeywords = {"union", "select", "insert", "update", "delete", "drop", "create", "alter", "exec", "execute"};

        for (String keyword : sqlKeywords) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}
