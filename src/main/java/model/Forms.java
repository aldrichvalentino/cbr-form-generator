package model;

import java.util.Set;

public class Forms {
    private int id;
    private Set<FormDescription> formDes;
    private Set<FormSolution> formSol;

    public Forms() {
    }

    public Forms(Set<FormDescription> formDes, Set<FormSolution> formSol) {
        this.formDes = formDes;
        this.formSol = formSol;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setFormDes(Set<FormDescription> formDes) {
        this.formDes = formDes;
    }

    public Set<FormDescription> getFormDes() {
        return this.formDes;
    }

    public void setFormSol(Set<FormSolution> formSol) {
        this.formSol = formSol;
    }

    public Set<FormSolution> getFormSol() {
        return this.formSol;
    }
}
