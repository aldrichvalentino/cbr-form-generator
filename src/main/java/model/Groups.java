package model;

import java.util.Set;

public class Groups {
  private int id;
  private Set<GMembers> gMembers;

  public Groups() {
  }

  public Groups(Set<GMembers> gm) {
    this.gMembers = gm;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public void setgMembers(Set<GMembers> gm) {
    this.gMembers = gm;
  }

  public Set<GMembers> getgMembers() {
    return this.gMembers;
  }

}
