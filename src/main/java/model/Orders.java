package model;

import java.util.List;

public class Orders {
  private int id;
  private List<OMembers> oMembers;

  public Orders() {
  }

  public Orders(List<OMembers> gm) {
    this.oMembers = gm;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public void setoMembers(List<OMembers> gm) {
    this.oMembers = gm;
  }

  public List<OMembers> getoMembers() {
    return this.oMembers;
  }
}
