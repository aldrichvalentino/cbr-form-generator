package utils.adapt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import model.GMembers;
import model.Groups;
import model.OMembers;
import model.Orders;

public class Ordering {
  public static <T> List<Orders> ordering(Set<T> sadd, List<Groups> grps, List<Orders> sord,
      OntoBridge ob) throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {

    if (sadd.isEmpty())
      return sord;
    List<OMembers> nlom;

    // search for group
    for (T fli : sadd) {
      Method m = fli.getClass().getDeclaredMethod("getName");
      String fln = (String) m.invoke(fli);

      // find group
      int grpId = findGroup(fln, grps);
      System.out.println("Group id: " + grpId);
      // find corresponding order
      Orders ordf = new Orders();
      for (Orders ordi : sord) {
        System.out.println("grp id" + ordi.getId());
        if (ordi.getId() == grpId) {
          ordf = ordi;
          System.out.println("Id ord " + ordf.getId());
          break;
        }
      }

      // save ordf id
      int ordfIdx = sord.indexOf(ordf);
      System.out.println("idx ordf " + ordfIdx);
      // get order member of found order
      nlom = ordf.getoMembers();
      if (ordfIdx > -1)
        sord.remove(ordf);
      boolean finish = false; // finish insert
      int size = nlom.size();

      for (int i = 0; i < size; i++) {
        if (isPredecessor(fln, nlom.get(i).getMemberName(), ob)) {
          // System.out.println("pred ?");
          OMembers omb = new OMembers();
          omb.setMemberName(fln);
          nlom.add(i, omb);
          finish = true; // finish insert
          break;
        }
      }

      if (!finish) {
        System.out.println("not finish");
        OMembers omb = new OMembers();
        omb.setMemberName(fln);
        nlom.add(omb);
      }

      ordf.setoMembers(nlom);
      sord.add(ordfIdx, ordf);
    }
    return sord;
  }

  private static int findGroup(String st, List<Groups> grps) {
    for (Groups grp : grps) {
      Set<GMembers> sgm = grp.getgMembers();
      for (GMembers gm : sgm) {
        if (st.equalsIgnoreCase(gm.getName())) {
          return grp.getId();
        }
      }
    }

    return 0;
  }

  private static boolean isPredecessor(String pred, String suc, OntoBridge ob) {
    if (!ob.existsInstance(pred))
      return false;
    Iterator<String> it = ob.listPropertyValue(pred, "isPredecessorOf");
    while (it.hasNext()) {
      String elm = ob.getShortName(it.next());
      if (elm.equalsIgnoreCase(suc))
        return true;
    }
    return false;
  }
}
