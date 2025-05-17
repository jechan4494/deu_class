package model.user;

import java.io.Serializable;

public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String id;
  private String password;
  private String name;
  private String department;
  private String role;

  public User(String id, String password, String name, String department, String role) {
    this.id = id;
    this.password = password;
    this.name = name;
    this.department = department;
    this.role = role;
  }

  // Getter 메서드 추가
  public String getId() { return id; }
  public String getPassword() { return password; }
  public String getName() { return name; }
  public String getDepartment() { return department; }
  public String getRole() { return role; }
}
