package utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.smu.tspell.wordnet.WordNetDatabase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.exception.OntologyAccessException;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;

import gate.util.Out;
import model.ControlButtons;
import model.FormDescription;
import model.FormSolution;
import model.GMembers;
import model.Groups;
import model.HLMembers;
import model.InputFields;
import model.LOResult;
import model.Orders;
import model.OutputFields;
import model.VLMembers;
import model.XLabel;
import utils.adapt.Compose;
import utils.adapt.Grouping;
import utils.adapt.Layouting;
import utils.adapt.Ordering;

public class Adaptation {

    OntoBridge ob;
    WordNetDatabase database;
    String op = "(";
    String cp = ")";
    HashMap<String, String> inrowelm;

    public Adaptation(WordNetDatabase wNetDatabase, OntoBridge ontoBridge) {
        database = wNetDatabase;
        ob = ontoBridge;
    }

    public CBRCase adapt(CBRQuery query, CBRCase icase) throws NoApplicableSimilarityFunctionException,
            NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, OntologyAccessException, InstantiationException {

        // ArrayList<CBRCase> ncase = new ArrayList<CBRCase>();
        // ncase.addAll(_case);

        CBRCase adaptedCase = new CBRCase();

        // ncase.remove(icase);
        FormDescription fdq = (FormDescription) query.getDescription();
        FormDescription fdc = (FormDescription) icase.getDescription();
        FormDescription fdn = new FormDescription(); // fdn = new form desc

        // set new form name
        fdn.setId((int) icase.getID());
        Out.println("Mulai adaptasi ... no kasus " + fdn.getId());
        Out.println("Spek kasus " + fdc.toString());
        // set new form name
        String fname = fdc.getFormName();
        // String pfname = fname.replaceAll("\\d", "");
        fdn.setFormName(fname);

        Set<InputFields> infldq = fdq.getInputFields();
        Set<InputFields> infldc = fdc.getInputFields();
        Set<OutputFields> outfldq = fdq.getOutputFields();
        Set<OutputFields> outfldc = fdc.getOutputFields();
        Set<ControlButtons> cq = fdq.getControlButtons();
        Set<ControlButtons> cc = fdc.getControlButtons();

        // build spec add
        Out.println("Mo build spec add");
        Set<InputFields> specaddi = buildSpecAdd(fname, infldq, infldc);
        Set<OutputFields> specaddo = buildSpecAdd(fname, outfldq, outfldc);
        Set<ControlButtons> specaddc = buildSpecAdd(fname, cq, cc);

        Out.print("\nSpec IF yg ditambahkan " + specaddi.size() + " buah");
        for (InputFields ifl : specaddi) {
            Out.print(ifl.getName() + " ");
        }
        Out.print("\nSpec OF yg ditambahkan ");
        for (OutputFields ofl : specaddo) {
            Out.print(ofl.getName() + " ");
        }
        Out.print("\nSpec CB yg ditambahkan ");
        for (ControlButtons cb : specaddc) {
            Out.print(cb.getName() + " ");
        }

        FormSolution fsc = (FormSolution) icase.getSolution();
        List<VLMembers> nvlo = fsc.getvlMember();
        inrowelm = new HashMap<String, String>();
        buildInRowElement(nvlo);
        /*
         * for (Map.Entry<String, String> en: inrowelm.entrySet()){
         * Out.println("Elemen "+en.getKey()+" Status "+en.getValue()); } Out.println();
         * for(VLMembers vlm:nvlo) {
         * Out.println(vlm.getName()+" status "+inrowelm.get(vlm.getName(). replace(op,
         * "").replace(cp, ""))); }
         */
        //////
        // build spec del
        Out.println("\nMo build spec del IF");
        Set<InputFields> specdeli = buildSpecDel(fname, infldc, infldq);
        // Out.println("Selesai build spec del IF");

        // Out.println("Mo build spec del OF");
        Set<OutputFields> specdelo = buildSpecDel(fname, outfldc, outfldq);
        // Out.println("Mo build spec del CB");
        Set<ControlButtons> specdelc = buildSpecDel(fname, cc, cq);
        // end build spec del

        Out.print("\nSpec IF yg dihapus ");
        for (InputFields ifl : specdeli) {
            Out.print(ifl.getName() + " ");
        }
        Out.print("\nSpec OF yg dihapus ");
        for (OutputFields ofl : specdelo) {
            Out.print(ofl.getName() + " ");
        }
        Out.print("\nSpec CB yg dihapus ");
        for (ControlButtons cb : specdelc) {
            Out.print(cb.getName() + " ");
        }

        /////////
        // compose input field
        // Out.println("\nMo compose IF");
        fdn.setInputFields(Compose.compose(infldc, specaddi, specdeli));

        // compose output field
        // Out.println("Mo compose OF");
        fdn.setOutputFields(Compose.compose(outfldc, specaddo, specdelo));

        // compose control button
        // Out.println("Mo compose CB");
        fdn.setControlButtons(Compose.compose(cc, specaddc, specdelc));

        Out.println("\nHasil komposisi " + fdn.toString());

        // Add Label for new entry
        Map<String, XLabel> lbl = fsc.getlabel();
        for (InputFields specaddi1 : specaddi)
            lbl.put(specaddi1.getName(), new XLabel(makeLabel(specaddi1.getName())));
        for (OutputFields specaddo1 : specaddo)
            lbl.put(specaddo1.getName(), new XLabel(makeLabel(specaddo1.getName())));
        for (ControlButtons specaddc1 : specaddc)
            lbl.put(specaddc1.getName(), new XLabel(makeLabel(specaddc1.getName())));

        // delete label del
        // for (InputFields ifl : specdeli) {
        // if (lbl.containsKey(ifl.getName())) {
        // lbl.remove(ifl.getName());
        // }
        // }

        // for (OutputFields ofl : specdelo) {
        // if (lbl.containsKey(ofl.getName())) {
        // lbl.remove(ofl.getName());
        // }
        // }

        // for (ControlButtons cb : specdelc) {
        // if (lbl.containsKey(cb.getName())) {
        // lbl.remove(cb.getName());
        // }
        // }

        //////////////////
        // update group
        // ambil himp group
        List<Groups> nsgrp = fsc.getGroup();
        Out.println("Mulai grouping ...");
        nsgrp = Grouping.grouping(specaddi, nsgrp, ob);
        nsgrp = Grouping.grouping(specaddo, nsgrp, ob);
        nsgrp = Grouping.grouping(specaddc, nsgrp, ob);

        Out.println(" Group hasil adaptasi: ");
        for (Groups grp : nsgrp) {
            Out.println("Grp id " + grp.getId());
            for (GMembers gmm : grp.getgMembers())
                Out.println(" Member " + gmm.getName());
        }

        // set group
        fsc.setGroup(nsgrp);
        Out.println("Selesai setting group");

        // ordering; getOrder();
        List<Orders> nsord = fsc.getOrder();
        // remove empty ordering
        List<Orders> lorder = new ArrayList<Orders>();
        lorder.addAll(nsord);
        for (Orders ord : lorder) {
            if (ord.getoMembers().isEmpty()) {
                nsord.remove(ord);
            }
        }

        Out.println("Periksa Id group dan order");
        for (Groups grp : nsgrp) {
            Out.println("Id group " + grp.getId());
        }
        for (Orders ord : nsord) {
            Out.println("Id order " + ord.getId());
        }

        Out.println("Mo ordering .. Id kasus " + icase.getID());
        nsord = Ordering.ordering(specaddi, nsgrp, nsord, ob);
        nsord = Ordering.ordering(specaddo, nsgrp, nsord, ob);
        nsord = Ordering.ordering(specaddc, nsgrp, nsord, ob);
        // set ordering
        fsc.setOrder(nsord);
        Out.println("Selesai setting order");

        // layouting
        List<HLMembers> nhlo = fsc.gethlMember();
        LOResult nlor = new LOResult(nvlo, nhlo); // nlor= new layout result

        Out.println("mo layout IF");
        nlor = Layouting.setLayouting(specaddi, nlor, nsord);
        Out.println("mo layout OF");
        nlor = Layouting.setLayouting(specaddo, nlor, nsord);
        Out.println("mo layout CB");
        nlor = Layouting.setLayouting(specaddc, nlor, nsord);
        // set layout
        fsc.setvlMember(nlor.getVLMember());
        fsc.sethlMember(nlor.getHLMember());
        Out.println("Selesai setting layout\n");
        /////////

        fsc.setId((int) icase.getID());

        // put new solution
        adaptedCase.setSolution(fsc);
        adaptedCase.setDescription(fdn);
        // ncase.add(icase);

        return adaptedCase;
    }

    public String makeLabel(String st) { // capitalize a first letter of each word
        StringBuffer res = new StringBuffer();
        String[] strArr = st.split("_");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }
        return res.toString().trim();
    }

    // membuat spec yang ditambahkan - specadd
    // sprcadd = spec di kueri dikurangi spec di kasus, ditambah spec wajib,
    // dikurangi yng aneh
    private <T> Set<T> buildSpecAdd(String fname, Set<T> oq, Set<T> oc) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<T> specadd = new HashSet<T>(); // tdk ada oq
        Set<T> specaddloop = new HashSet<T>(); // tdk ada oq
        // tambahkan spek di kueri
        specadd.addAll(oq); // di combine pake oc
        specaddloop.addAll(specadd);

        // kurangi dengan spek di kasus
        for (T cm : oc) {
            for (T qm : oq) {
                Method mq = qm.getClass().getDeclaredMethod("getName");
                Method mc = cm.getClass().getDeclaredMethod("getName");
                String nmq = (String) mq.invoke(qm); // metode mq diinvoke dengan param qm
                String nmc = (String) mc.invoke(cm);
                // Out.println("Build Specadd Q "+nmq+" C "+nmc);
                if (nmq.equalsIgnoreCase(nmc) || isWeaker(nmq, nmc)) {
                    // Out.println("Sama");
                    specadd.remove(qm);
                    // Out.println("Stlh remove");
                }
            }
        }

        // tambahkan elemen wajib
        for (T cm : oc) {
            Method mc = cm.getClass().getDeclaredMethod("getName");
            String nmc = (String) mc.invoke(cm);
            if (isRequired(fname, nmc) && (!berisiset(oc, cm))) {
                // if (isRequired(fname, nmc) ){
                Out.println("Tambah elm wajib " + nmc + " yg di kasus ke specadd");
                specadd.add(cm);
            }
        }
        // kurangi dengan elemen terlarang
        Set<String> odds = getOddElements(fname);
        for (String odmem : odds) {
            for (T qm : specaddloop) {
                Method mq = qm.getClass().getDeclaredMethod("getName");
                String nmq = (String) mq.invoke(qm); // metode mq diinvoke dengan param qm
                if (nmq.trim().equalsIgnoreCase(odmem)) {
                    specadd.remove(qm);
                }
            }
        }
        return specadd;
    }

    // untuk menghapus elmen dari kasus
    private <T> Set<T> deleteSpecDel(Set<T> del, Set<T> cqc) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        Set<T> cqca = new HashSet<T>();
        cqca.addAll(cqc);

        for (T itd : del) {
            // Out.println("Loop del kelas "+itd.getClass());
            for (T ifk : cqca) {
                Method mq = ifk.getClass().getDeclaredMethod("getName");
                Method mc = itd.getClass().getDeclaredMethod("getName");
                String nmq = (String) mq.invoke(ifk); // metode mq diinvoke dengan param qm
                String nmc = (String) mc.invoke(itd);
                if (nmq.equals(nmc)) {
                    cqc.remove(ifk);
                    // System.out.println("Spec del: "+nmq+ " sdh dihapus. ukuran: "+cqc.size());
                }
            }
        }
        return cqc;
    }

    private boolean isWeaker(String wk, String stg) {
        if (!ob.existsInstance(wk) || !ob.existsInstance(stg))
            return false;
        // Out.println("isRequired "+fname+" or "+fldname+" exist");
        Iterator<String> it = ob.listPropertyValue(stg, "isPreferredOver"); // it suksesor fln
        while (it.hasNext()) {
            String elm = ob.getShortName(it.next());
            // Out.println(elm+" Required by "+" "+fname);
            if (elm.equalsIgnoreCase(wk))
                return true;
        }

        return false;
    }

    // membuat spec yang dihapus - specdel
    // specdel: spek di kasus, dikurangi spek di kueri, ditambah spek terlarang
    private <T> Set<T> buildSpecDel(String fname, Set<T> oc, Set<T> oq) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<T> specdel = new HashSet<T>();

        // tambahkan spek kasus
        specdel.addAll(oc);

        // kurangi dengan spec kueri
        for (T delel : oc) {
            for (T qelm : oq) {
                Method metq = qelm.getClass().getDeclaredMethod("getName");
                Method metd = delel.getClass().getDeclaredMethod("getName");
                String nmq = (String) metq.invoke(qelm); // metode mq diinvoke dengan param qm
                String nmd = (String) metd.invoke(delel);
                // Out.println("Build Specdel Qry "+nmq+" Cs "+nmd);
                // if (nmq.equalsIgnoreCase(nmd)) {
                if (nmq.equalsIgnoreCase(nmd)) {
                    // isInRow menyebabkan elemen di dlm row tidak dihapus
                    // Out.println("Sama, hrs dihapus ");
                    specdel.remove(delel); // remove yang tdk boleh dihapus
                    // Out.println("Stlh remove");
                }
                if (isInRow(nmd)) {
                    // isInRow menyebabkan elemen di dlm row tidak dihapus
                    // Out.println("In row, hrs dihapus ");
                    specdel.remove(delel); // remove yang tdk boleh dihapus
                    // Out.println("Stlh remove");
                }
                /*
                 * if (isOdd(fname, nmd)) { specdel.add(qelm); }
                 */ }
        }

        return specdel;
    }

    private boolean isInRow(String nm) {
        if (inrowelm.containsKey(nm))
            return true;
        return false;
    }

    // memeriksa apakah mandatory atau tdk
    private boolean isRequired(String fname, String fldname) { // fldnmae: fld di kasus
        if (!ob.existsInstance(fldname) || !ob.existsInstance(fname))
            return false;
        // Out.println("isRequired "+fname+" or "+fldname+" exist");
        Iterator<String> it = ob.listPropertyValue(fname, "hasMandatoryElements"); // it suksesor fln
        while (it.hasNext()) {
            String elm = ob.getShortName(it.next());
            // Out.println(elm+" Required by "+" "+fname);
            if (elm.equalsIgnoreCase(fldname))
                return true;
        }
        return false;
    }

    // Meriksa apakah elemen ganjil?
    private Set<String> getOddElements(String fname) { // fldnmae: fld di kasus
        Set<String> setOdd = new HashSet<String>();
        if (!ob.existsInstance(fname))
            return setOdd;
        Iterator<String> it = ob.listPropertyValue(fname, "hasOddElements"); // it suksesor fln
        while (it.hasNext()) {
            String elm = ob.getShortName(it.next());
            // Out.println(fname+" has odd elemen "+" "+elm);
            setOdd.add(elm);
        }
        return setOdd;
    }

    private <T> boolean berisiset(Set<T> lvlm, T vlm) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (T lmem : lvlm) {
            Method m = lmem.getClass().getDeclaredMethod("getName");
            String fln = (String) m.invoke(lmem); // mengambil nama elemen
            Method me = vlm.getClass().getDeclaredMethod("getName");
            String flne = (String) me.invoke(vlm); // mengambil nama elemen

            if (fln.startsWith(op))
                fln = fln.replace(op, "");
            if (fln.endsWith(cp))
                fln = fln.replace(cp, "");
            if (fln.equals(flne))
                return true;
        }
        return false;
    }

    private void buildInRowElement(List<VLMembers> lvlm) {
        boolean inrow = false;
        // Out.println("Build yng in row ");
        for (VLMembers vlm : lvlm) {
            String namamem = vlm.getName();
            // Out.println("loop. Member: "+namamem.trim());
            if (namamem.startsWith(op)) {
                inrow = true;
                inrowelm.put(namamem.replace(op, ""), "start");
            } else if (namamem.endsWith(cp)) {
                inrowelm.put(namamem.replace(cp, ""), "end");
                inrow = false;
            } else if (inrow)
                inrowelm.put(namamem, "in");
        }
        // Out.println("akhir Build in row ");
    }
}
