package model;

public class VLMembers {
    private int id;
    private String Name;

    public VLMembers() {
    }

    public VLMembers(String mn) {
        this.Name = mn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String mn) {
        this.Name = mn;
    }

    public String getName() {
        return Name;
    }
}
