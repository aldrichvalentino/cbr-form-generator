package model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
// import java.util.Set;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;

/**
 * Bean that stores the solution of the case
 *
 * @author Uung Ungkawa
 * @version 1.0
 */
public class FormSolution implements CaseComponent {

    int id;
    Map<String, XLabel> xLabel;
    List<Groups> group;
    List<Orders> order;
    List<VLMembers> vlMember;
    List<HLMembers> hlMember;

    public String ltoString() {
        String s = "";
        int i = 0;
        if (!xLabel.isEmpty())
            for (String name : xLabel.keySet()) {
                String key = name.toString();
                String value = xLabel.get(name).getLabel();
                if (i > 0)
                    s = s.concat("\n");
                s = s.concat("Comp Name: " + key + " [" + value + "]");
                i++;
            }
        return s.trim();
    }

    public String gtoString() {
        String s = "";
        if (!group.isEmpty()) {
            for (int j = 0; j < group.size(); j++) {
                Groups grp = group.get(j);
                int id = grp.getId();
                if (j > 0)
                    s = s.concat("\n");
                s = s.concat("Group " + id + ":");
                for (GMembers gm1 : grp.getgMembers()) {
                    s = s.concat(" " + gm1.getName());
                }
            }
        }
        return s.trim();
    }

    public String otoString() {
        String s = "";
        if (!order.isEmpty()) {
            for (int j = 0; j < order.size(); j++) {
                Orders ord = order.get(j);
                int ido = ord.getId();
                if (j > 0)
                    s = s.concat("\n");
                s = s.concat("Order " + ido + ":");
                for (OMembers om : ord.getoMembers()) {
                    s = s.concat(" " + om.getMemberName());
                }
            }
        }
        return s.trim();
    }

    public String vltoString() {
        String s = " ";
        if (!vlMember.isEmpty())
            for (int j = 0; j < vlMember.size(); j++)
                s = s.concat(vlMember.get(j).getName()).concat(" ");
        return s.trim();
    }

    public String hltoString() {
        String s = " ";
        if (!hlMember.isEmpty())
            for (int j = 0; j < hlMember.size(); j++)
                s = s.concat(hlMember.get(j).getMemberName()).concat(" ");
        return s.trim();
    }

    public String toString() {
        String s = "Solution:";
        // Label
        if (!xLabel.isEmpty())
            for (String name : xLabel.keySet()) {
                String key = name.toString();
                String value = xLabel.get(name).getLabel();
                s = s.concat("\nField Name: " + key + "\tLabel: " + value);
            }
        s = s.concat(" ");
        // Group
        if (!group.isEmpty()) {
            Iterator<Groups> hgp = group.iterator();
            while (hgp.hasNext()) {
                Groups grp = hgp.next();
                int id = grp.getId();
                for (Iterator<GMembers> gm1 = grp.getgMembers().iterator(); gm1.hasNext();) {
                    String mem = gm1.next().getName();
                    s = s.concat("\nGroup " + id + " member " + mem);
                }
            }
        }
        s = s.concat(" ");
        // Order
        if (!order.isEmpty())
            for (int j = 0; j < order.size(); j++) {
                Orders ord = order.get(j);
                int ido = ord.getId();
                if (ido > 1)
                    s = s.concat("\n");
                s = s.concat("Order no " + ido + ": ");
                for (int i = 0; i < ord.getoMembers().size(); i++) {
                    s = s.concat(" " + ord.getoMembers().get(i).getMemberName());
                }
            }
        s = s.concat("\n");
        // Layout
        if (!vlMember.isEmpty())
            for (int j = 0; j < vlMember.size(); j++)
                s = s.concat(vlMember.get(j).getName()).concat(" ");
        s = s.concat("\n");
        if (!hlMember.isEmpty())
            for (int j = 0; j < hlMember.size(); j++)
                s = s.concat(hlMember.get(j).getMemberName()).concat(" ");

        return s.trim();
    }

    public Attribute getIdAttribute() {

        return new Attribute("id", this.getClass());
    }

    /**
     * @return Returns the group.
     */
    public Map<String, XLabel> getlabel() {
        return xLabel;
    }

    /**
     * @param group The group to set.
     */
    public void setlabel(Map<String, XLabel> xLabel) {
        this.xLabel = xLabel;
    }

    /**
     * @return Returns the group.
     */
    public List<Groups> getGroup() {
        return group;
    }

    /**
     * @param group The group to set.
     */
    public void setGroup(List<Groups> group) {
        this.group = group;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the order.
     */
    public List<Orders> getOrder() {
        return order;
    }

    /**
     * @param order The order to set.
     */
    public void setOrder(List<Orders> order) {
        this.order = order;
    }

    /**
     * @return Returns the order.
     */
    public List<VLMembers> getvlMember() {
        return vlMember;
    }

    /**
     * @param order The order to set.
     */
    public void setvlMember(List<VLMembers> vl) {
        this.vlMember = vl;
    }

    /**
     * @return Returns the order.
     */
    public List<HLMembers> gethlMember() {
        return hlMember;
    }

    /**
     * @param order The order to set.
     */
    public void sethlMember(List<HLMembers> vl) {
        this.hlMember = vl;
    }

}
