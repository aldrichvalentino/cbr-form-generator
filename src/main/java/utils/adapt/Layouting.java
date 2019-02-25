package utils.adapt;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import model.LOResult;
import model.OMembers;
import model.Orders;
import model.VLMembers;

public class Layouting {
    private static final String op = "(";
    private static final String cp = ")";

    public static <T> LOResult setLayouting(Set<T> sadd, LOResult lor, List<Orders> lsord)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException { // Okt 16

        if (sadd.isEmpty())
            return lor; // sadd adalah spec yang ditambahkan
        System.out.println("Masuk layouting ...");
        List<VLMembers> nvlm = new ArrayList<VLMembers>(); // nvlm: new vl
        VLMembers vlm;

        for (Orders ord : lsord) {
            for (OMembers omember : ord.getoMembers()) {
                String namaordmem = omember.getMemberName();
                // System.out.println("Nama order member "+namaordmem);
                if (berisi(lor.getVLMember(), namaordmem).equals("start")) {
                    // System.out.println("Nama ord mem start "+namaordmem);
                    vlm = new VLMembers(op.concat(namaordmem));
                } else if (berisi(lor.getVLMember(), namaordmem).equals("end")) {
                    // System.out.println("Nama ord mem end "+namaordmem);
                    vlm = new VLMembers(namaordmem.concat(cp));
                } else {
                    // System.out.println("Nama ord mem "+namaordmem);
                    vlm = new VLMembers(namaordmem);
                }
                nvlm.add(vlm);
            }
        }
        lor.setVLMember(nvlm);
        return lor;
    }

    private static String berisi(List<VLMembers> lvlm, String vlm) {
        String ret = "tidak";
        for (VLMembers lmem : lvlm) {
            String namalmem = lmem.getName().trim();
            if (namalmem.startsWith(op))
                if (namalmem.replace(op, "").equals(vlm))
                    ret = "start";
            if (namalmem.endsWith(cp))
                if (namalmem.replace(cp, "").equals(vlm))
                    ret = "end";
            if (namalmem.equals(vlm))
                ret = "in";
        }
        return ret;
    }

}
