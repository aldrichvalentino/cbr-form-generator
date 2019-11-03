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

  public static <T> List<Groups> grouping(Set<T> specAdd, List<Groups> groups, OntoBridge ob)
      throws NoApplicableSimilarityFunctionException, NoSuchMethodException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      OntologyAccessException {

    double oif;
    List<AdaptResult> ladr;
    List<Groups> nsgrp = new ArrayList<Groups>(groups); // new group set

    for (T fl1 : specAdd) {
      Method m = fl1.getClass().getDeclaredMethod("getName");
      String fln = (String) m.invoke(fl1);
      Groups ngrp = new Groups(); // new group
      ladr = new ArrayList<AdaptResult>();
      for (Groups grpi : groups) {
        oif = 0;
        int i = 0;
        for (GMembers gm : grpi.getgMembers()) {
          if (isInAGroup(gm.getName(), fln, ob)) {
            oif++;
          } else {
            oif += compute(gm.getName(), fln, ob);
          }
          i++;
        }

        ladr.add(new AdaptResult(grpi, (double) oif / i));
      }

      if (!ladr.isEmpty()) {
        System.out.println("Specs to group " + fln);
        for (AdaptResult ar : ladr) {
          System.out.println("Score " + ar.getGroup().getId() + " is: " + ar.getEval());
        }

        Collections.sort(ladr);
        ngrp = ladr.get(0).getGroup();
        int indexOfGroup = nsgrp.indexOf(ngrp);
        nsgrp.remove(ngrp);

        // create new group member
        GMembers ngm = new GMembers(fln);
        // get group member of corresponding group
        Set<GMembers> sgm = ngrp.getgMembers();
        // insert new member
        if (!containsLayout(sgm, ngm)) {
          sgm.add(ngm);
        }
        ngrp.setgMembers(sgm);
        nsgrp.add(indexOfGroup, ngrp);
      }
    }
    return nsgrp;
  }

  private static boolean isInAGroup(String st1, String st2, OntoBridge ob) {
    if (!ob.existsInstance(st1) || !ob.existsInstance(st2))
      return false;
    Iterator<String> it = ob.listPropertyValue(st1, "isInAGroupWith");
    while (it.hasNext()) {
      String elm = ob.getShortName(it.next());
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
    System.out.println("Similarity of " + i1 + " and " + i2);
    System.out.println(intersectionsize);
    System.out.println(sc1size);
    System.out.println(sc2size);

    double res = intersectionsize / (Math.sqrt(sc1size) * Math.sqrt(sc2size));
    return res;
  }

  private static <T> boolean containsLayout(Set<T> lvlm, T vlm)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    for (T lmem : lvlm) {
      Method m = lmem.getClass().getDeclaredMethod("getName");
      String fln = (String) m.invoke(lmem);
      Method me = vlm.getClass().getDeclaredMethod("getName");
      String flne = (String) me.invoke(vlm);

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
