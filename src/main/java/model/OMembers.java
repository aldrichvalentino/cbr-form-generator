package model;

public class OMembers {
  private int id;
  private String memberName;

  public OMembers() {
  }

  public String getMemberName() {
    return memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public OMembers(String mn) {
    this.memberName = mn;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
