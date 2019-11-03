package model;

public class GMembers {
  private int id;
  private String name;

  public GMembers() {
  }

  public GMembers(String mn) {
    this.name = mn;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setName(String mn) {
    this.name = mn;
  }

  public String getName() {
    return name;
  }
}
