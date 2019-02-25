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
        List<OMembers> nlom; // nlom=Orders.getoMembers/set

        // cari di group mana
        for (T fli : sadd) { // fli=elemen yang ditambahkan
            // System.out.println("loop 1: fld");
            Method m = fli.getClass().getDeclaredMethod("getName");
            String fln = (String) m.invoke(fli); // mengambil nama elemen
            // System.out.println("Dlm ordering - Specadd: "+fln);

            // find group
            int grpId = findGroup(fln, grps);
            System.out.println("Selesai cari grp. Id " + grpId);
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
            System.out.println("Setelah ambil order ");
            if (ordfIdx > -1)
                sord.remove(ordf);
            boolean finish = false; // finish isert
            int size = nlom.size();

            for (int i = 0; i < size; i++) { // loop 2
                System.out.println("loop 2: ord mem");
                if (isPredecessor(fln, nlom.get(i).getMemberName(), ob)) {
                    // System.out.println("pred ?");
                    OMembers omb = new OMembers();
                    omb.setMemberName(fln);
                    nlom.add(i, omb);
                    finish = true; // finish insert
                    System.out.println("sini 2");
                    break;
                }
            }
            if (!finish) { // if it hasnt been inserted
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
            // Out.println("cari group. id "+grp.getId());
            Set<GMembers> sgm = grp.getgMembers();
            for (GMembers gm : sgm) {
                // Out.println("cari grp mem. id "+gm.getId());
                if (st.equalsIgnoreCase(gm.getName())) {
                    // Out.println("st==mem: "+gm.getMemberName());
                    return grp.getId();
                }
            }
        }

        return 0;
    }

    private static boolean isPredecessor(String pred, String suc, OntoBridge ob) {
        if (!ob.existsInstance(pred))
            return false;
        // Out.println("Instn pred "+pred+" exist");
        Iterator<String> it = ob.listPropertyValue(pred, "isPredecessorOf"); // it suksesor fln
        while (it.hasNext()) {
            String elm = ob.getShortName(it.next());
            // Out.println(pred+" Pred of "+" "+elm);
            if (elm.equalsIgnoreCase(suc))
                return true;
        }
        return false;
    }

}
