package model;

import gate.util.Out;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class RevisedModel {
    private String formName;
    private String inputFields;
    private String outputFields;
    private String controlButtons;
    private String labels;
    private String groups;
    private String orders;
    private String vLayouts;
    private String hLayouts;

    public <T> HashSet<T> addDesc(Class<T> c, String elm)
            throws ReflectiveOperationException, IllegalAccessException {
        Out.println("add desc ");
        HashSet<T> eset = new HashSet<T>();
        String[] st = elm.split("\\s");
        for (String st1 : st) {
            Constructor<T> ctor = c.getConstructor(String.class);
            ctor.setAccessible(true);
            T if1 = ctor.newInstance(st1);
            eset.add(if1);
        }
        return eset;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getInputFields() {
        return inputFields;
    }

    public void setInputFields(String inputFields) {
        this.inputFields = inputFields;
    }

    public String getOutputFields() {
        return outputFields;
    }

    public void setOutputFields(String outputFields) {
        this.outputFields = outputFields;
    }

    public String getControlButtons() {
        return controlButtons;
    }

    public void setControlButtons(String controlButtons) {
        this.controlButtons = controlButtons;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getvLayouts() {
        return vLayouts;
    }

    public void setvLayouts(String vLayouts) {
        this.vLayouts = vLayouts;
    }

    public String gethLayouts() {
        return hLayouts;
    }

    public void sethLayouts(String hLayouts) {
        this.hLayouts = hLayouts;
    }

    public HashMap<String, XLabel> addLabel(String elm) {
        Out.println("add label ");
        HashMap<String, XLabel> eset = new HashMap<String, XLabel>();
        StringTokenizer st = new StringTokenizer(elm, "\n", false);
        while (st.hasMoreTokens()) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), ":", false);
            st2.nextToken();
            StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "[]", false);
            eset.put(st3.nextToken().trim(), new XLabel(st3.nextToken().trim()));
        }
        return eset;
    }

    public ArrayList<Groups> addGroup(String elm) {
        Out.println("add group ");
        ArrayList<Groups> eset = new ArrayList<Groups>();
        StringTokenizer st = new StringTokenizer(elm, "\n", false);
        while (st.hasMoreTokens()) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), ":,.", false);
            HashSet<GMembers> hmem = new HashSet<GMembers>();
            st2.nextToken();
            while (st2.hasMoreTokens()) {
                String[] str = st2.nextToken().trim().split("\\s+");
                for (String stri : str) {
                    Out.println("elm grp " + stri);
                    hmem.add(new GMembers(stri.trim()));
                }
            }
            eset.add(new Groups(hmem));
        }
        return eset;
    }

    public ArrayList<Orders> addOrder(String elm) {
        Out.println("add order ");
        ArrayList<Orders> eset = new ArrayList<Orders>();
        StringTokenizer st = new StringTokenizer(elm, "\n", false);
        while (st.hasMoreTokens()) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), ":,.", false);
            ArrayList<OMembers> ord = new ArrayList<OMembers>();
            st2.nextToken();
            while (st2.hasMoreTokens()) {
                String[] str = st2.nextToken().trim().split("\\s+");
                for (String stri : str)
                    ord.add(new OMembers(stri.trim()));
            }
            eset.add(new Orders(ord));
        }
        return eset;
    }

    public ArrayList<VLMembers> addVL(String elm) {
        Out.println("add VL ");
        ArrayList<VLMembers> vl = new ArrayList<VLMembers>();
        StringTokenizer st = new StringTokenizer(elm, ",.", false);
        while (st.hasMoreTokens()) {
            String[] str = st.nextToken().split("\\s+");
            for (String stri : str)
                vl.add(new VLMembers(stri.trim()));
        }
        return vl;
    }

    public ArrayList<HLMembers> addHL(String elm) {
        Out.println("add HL ");
        ArrayList<HLMembers> hl = new ArrayList<HLMembers>();
        StringTokenizer st = new StringTokenizer(elm, ",.", false);
        while (st.hasMoreTokens()) {
            String[] str = st.nextToken().split("\\s+");
            for (String stri : str)
                hl.add(new HLMembers(stri.trim()));
        }
        return hl;
    }

}
