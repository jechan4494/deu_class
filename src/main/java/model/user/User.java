package model.user;

import model.room.RoomReservation;

public class User {
  private String id;
  private String password;
  private String name;
  private String department;
  private String role; // PROFESSOR, STUDENT, TA

  public User(String id, String password, String name, String department, String role) {
    this.id = id;
    this.password = password;
    this.name = name;
    this.department = department;
    this.role = role;
  }

    public User() {
    }

    public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDepartment() { return department; }
  public void setDepartment(String department) { this.department = department; }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
}
