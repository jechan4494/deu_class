package model;

public class User {
    private String id;
    private String password;
    private String name;
    private UserRole role; // STUDENT, PROFESSOR, ADMIN
    
    public enum UserRole {
        STUDENT, PROFESSOR, ADMIN
    }
    
    // 생성자, getter, setter 생략
}