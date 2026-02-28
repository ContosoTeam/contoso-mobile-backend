package com.contoso.mobile.controller;

import com.contoso.mobile.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
// VULNERABILITY: No CSRF protection, CORS allows all
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // VULNERABILITY: SQL Injection via string concatenation
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        // VULNERABILITY: SQL Injection
        String sql = "SELECT * FROM users WHERE username LIKE '%" + query + "%' OR email LIKE '%" + query + "%'";
        Query nativeQuery = entityManager.createNativeQuery(sql, User.class);
        List<User> users = nativeQuery.getResultList();

        // VULNERABILITY: Logging sensitive search queries
        logger.info("User search: " + query + " found " + users.size() + " results");

        return ResponseEntity.ok(users);
    }

    // VULNERABILITY: Insecure deserialization
    @PostMapping("/users/import")
    public ResponseEntity<?> importUsers(@RequestBody byte[] data) {
        try {
            // VULNERABILITY: Deserializing untrusted data
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();

            return ResponseEntity.ok(Map.of("status", "imported", "type", obj.getClass().getName()));
        } catch (Exception e) {
            // VULNERABILITY: Exposing stack trace
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage(), "stackTrace", Arrays.toString(e.getStackTrace())));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        // VULNERABILITY: SQL Injection
        String sql = "SELECT * FROM users WHERE id = " + id;
        Query query = entityManager.createNativeQuery(sql, User.class);
        
        try {
            User user = (User) query.getSingleResult();
            // VULNERABILITY: Returning password hash in response
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");

        // VULNERABILITY: No password strength validation
        // VULNERABILITY: SQL Injection in INSERT
        String sql = String.format(
            "INSERT INTO users (username, password, email) VALUES ('%s', '%s', '%s')",
            username, password, email  // VULNERABILITY: Storing plaintext password
        );

        entityManager.createNativeQuery(sql).executeUpdate();

        // VULNERABILITY: Logging plaintext password
        logger.info("User registered: " + username + " with password: " + password);

        return ResponseEntity.ok(Map.of("status", "registered"));
    }

    // VULNERABILITY: Exposes all internal configuration
    @GetMapping("/debug/config")
    public ResponseEntity<?> getConfig() {
        return ResponseEntity.ok(Map.of(
            "jwt_secret", jwtSecret,
            "db_password", dbPassword,
            "java_version", System.getProperty("java.version"),
            "os", System.getProperty("os.name"),
            "env", System.getenv()  // VULNERABILITY: Exposing all env vars
        ));
    }

    // VULNERABILITY: Server-Side Request Forgery (SSRF)
    @GetMapping("/proxy")
    public ResponseEntity<?> proxy(@RequestParam String url) {
        try {
            // VULNERABILITY: SSRF - fetching arbitrary URLs
            java.net.URL targetUrl = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) targetUrl.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // VULNERABILITY: File read via path traversal
    @GetMapping("/files")
    public ResponseEntity<?> readFile(@RequestParam String path) {
        try {
            // VULNERABILITY: Path traversal - no validation
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)));
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("File not found");
        }
    }
}
