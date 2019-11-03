package model;

public class HLMembers {
  private int id;
  private int vlFormId;
  private String memberName;

  public HLMembers() {
  }

  public HLMembers(String mn) {
    this.memberName = mn;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setFormId(int gid) {
    this.vlFormId = gid;
  }

  public int getFormIdId() {
    return vlFormId;
  }

  public void setMemberName(String mn) {
    this.memberName = mn;
  }

  public String getMemberName() {
    return memberName;
  }

}
