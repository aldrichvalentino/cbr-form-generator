package model;

public class OMembers {
    private int id;
    private String memberName;

    public OMembers() {
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

    public void setmemberName(String mn) {
        this.memberName = mn;
    }

    public String getmemberName() {
        return memberName;
    }
}
