package model;

public class XLabel {
    private int id;
    private String label;

    public XLabel() {
    }

    public XLabel(String lbl) {
        this.label = lbl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLabel(String lbl) {
        this.label = lbl;
    }

    public String getLabel() {
        return label;
    }

}
