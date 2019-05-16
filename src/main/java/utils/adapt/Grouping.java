package utils.adapt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.exception.OntologyAccessException;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.AdaptResult;
import model.GMembers;
import model.Groups;

public class Grouping {
    private static final String op = "(";
    private static final String cp = ")";

    public static <T> List<Groups> grouping(Set<T> sadd, List<Groups> sgrp, OntoBridge ob)
            throws NoApplicableSimilarityFunctionException, NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, OntologyAccessException {
        double oif;
        List<AdaptResult> ladr;
        // OntDeep onc= new OntDeep();
        // OntDeepBasic onc= new OntDeepBasic();
        // OntDetail onc= new OntDetail();

        List<Groups> nsgrp = new ArrayList<Groups>(sgrp); // new group set

        for (T fl1 : sadd) {
            Groups ngrp = new Groups(); // new group
            String fln = "";
            ladr = new ArrayList<AdaptResult>();
            for (Groups grpi : sgrp) {
                oif = 0;
                int i = 0;
                for (GMembers gm : grpi.getgMembers()) {
                    Method m = fl1.getClass().getDeclaredMethod("getName");
                    fln = (String) m.invoke(fl1);
                    // System.out.println("fl name: "+fln+" Group mem "+gm.getMemberName());
                    // oif += onc.compute (new Instance(gm.getName()), new Instance(fln));
                    if (isInAGroup(gm.getName(), fln, ob)) {
                        oif++;
                    } else {
                        oif += compute(gm.getName(), fln, ob);
                    }
                    i++;
                    // System.out.println("fl name: "+fln+" Group mem "+gm.getMemberName()+
                    // " Group Id "+grpi.getId()+" Nilai "+oif);
                }

                ladr.add(new AdaptResult(grpi, (double) oif / i));
            }

            if (!ladr.isEmpty()) {
                System.out.println("Spek yg dikelompokan " + fln);
                for (AdaptResult ar : ladr) {
                    System.out.println(
                            "Nilai group " + ar.getGroup().getId() + " adalah: " + ar.getEval());
                }
                // Out.println("Panjang ladr stlah sort "+ladr.size());
                Collections.sort(ladr);// , Collections.reverseOrder());
                // for (AdaptResult ar: ladr){
                // System.out.println("Nilai group "+ar.getEval()+
                // " group Id "+ar.getGroup().getId());
                // }
                ngrp = ladr.get(0).getGroup();
                int indexOfGroup = nsgrp.indexOf(ngrp);
                nsgrp.remove(ngrp);
                // create new group member
                GMembers ngm = new GMembers(fln);
                // get group member of corresponding group
                Set<GMembers> sgm = ngrp.getgMembers();
                // insert new member
                if (!berisiset(sgm, ngm)) {
                    sgm.add(ngm);
                }
                ngrp.setgMembers(sgm);
                nsgrp.add(indexOfGroup, ngrp);
                // break;
            } else {
                System.out.println("ladr Kosong");
            }
        }
        return nsgrp;
    }

    private static boolean isInAGroup(String st1, String st2, OntoBridge ob) {
        if (!ob.existsInstance(st1) || !ob.existsInstance(st2))
            return false;
        Iterator<String> it = ob.listPropertyValue(st1, "isInAGroupWith"); // it suksesor fln
        while (it.hasNext()) {
            String elm = ob.getShortName(it.next());
            // Out.println(fname+" has odd elemen "+" "+elm);
            if (elm.equalsIgnoreCase(st2))
                return true;
        }

        return false;
    }

    private static double compute(String i1, String i2, OntoBridge ob) {
        if (i1.equals(i2))
            return 1;
        if (!ob.existsInstance(i1))
            return 0;
        if (!ob.existsInstance(i2.toString()))
            return 0;

        Set<String> sc1 = new HashSet<String>();
        for (Iterator<String> iter = ob.listBelongingClasses(i1); iter.hasNext();)
            sc1.add(iter.next());
        sc1.remove(ob.getThingURI());

        Set<String> sc2 = new HashSet<String>();
        for (Iterator<String> iter = ob.listBelongingClasses(i2); iter.hasNext();)
            sc2.add(iter.next());
        sc2.remove(ob.getThingURI());

        double sc1size = sc1.size();
        double sc2size = sc2.size();

        sc1.retainAll(sc2);
        double intersectionsize = sc1.size();

        double res = intersectionsize / (Math.sqrt(sc1size) * Math.sqrt(sc2size));
        return res;
    }

    private static <T> boolean berisiset(Set<T> lvlm, T vlm)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
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
}
