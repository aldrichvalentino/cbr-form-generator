package model;

public class InputFields {
  private int id;
  private String Name;

  public InputFields() {
  }

  public InputFields(String name) {
    this.Name = name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setName(String name) {
    this.Name = name;
  }

  public String getName() {
    return Name;
  }
}
