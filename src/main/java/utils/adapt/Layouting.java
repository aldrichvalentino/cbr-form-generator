package utils.adapt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import model.LOResult;
import model.OMembers;
import model.Orders;
import model.VLMembers;

public class Layouting {
    private static final String startRow = "(";
    private static final String endRow = ")";
    private static final String startCol = "{";
    private static final String endCol = "}";
    private static final String startGroup = "[";
    private static final String endGroup = "]";

    public static <T> LOResult deleteLayouting(Set<T> deleteField, LOResult layoutResult) {
        List<VLMembers> nvlo = layoutResult.getVLMember();
        for (T field : deleteField) {
            for (int currentLayout = 0; currentLayout < nvlo.size(); currentLayout++) {
                try {
                    String layoutName = nvlo.get(currentLayout).getName();
                    // System.out.println("Ini nama layout = " + layoutName);
                    String pureName = layoutName.replace(startRow, "").replace(startCol, "")
                            .replace(startGroup, "").replace(endRow, "").replace(endCol, "")
                            .replace(endGroup, "");
                    // System.out.println("Ini yg udh dibersihin = " + pureName);
                    Method method = field.getClass().getDeclaredMethod("getName");
                    String fieldName = (String) method.invoke(field);
                    // System.out.println("Ini yg mau dihapus = " + fieldName);
                    if (pureName.equals(fieldName)) {
                        // System.out.println("Sama nih");
                        int startIndex = layoutName.indexOf(pureName);
                        int endIndex = startIndex + fieldName.length();
                        int startTokenLength = startIndex;
                        int endTokenLength = layoutName.length() - endIndex;
                        if (startTokenLength > endTokenLength) {
                            // System.out.println("Dikirim layoutnya ke next word");
                            String startToken =
                                    layoutName.substring(0, startTokenLength - endTokenLength);
                            nvlo.get(currentLayout + 1)
                                    .setName(startToken + nvlo.get(currentLayout + 1).getName());
                            // System.out.println("Jadi: " + nvlo.get(currentLayout + 1).getName());
                            break;
                        } else if (endTokenLength > startTokenLength) {
                            // System.out.println("Dikirim layoutnya ke previous word");
                            String endToken = layoutName.substring(
                                    layoutName.length() - (endTokenLength - startTokenLength),
                                    layoutName.length());
                            nvlo.get(currentLayout - 1)
                                    .setName(nvlo.get(currentLayout - 1).getName() + endToken);
                            // System.out.println("Jadi: " + nvlo.get(currentLayout - 1).getName());
                            break;
                        } else {
                            // System.out.println("Hapus elemen");
                            nvlo.remove(currentLayout);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        layoutResult.setVLMember(nvlo);
        return layoutResult;
    }

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
                String orderName = omember.getMemberName();
                String elementNameInLayout = contains(lor.getVLMember(), orderName);
                if (!elementNameInLayout.equals("")) {
                    // System.out.println("ga bikin karena " + elementNameInLayout);
                    vlm = new VLMembers(elementNameInLayout);
                } else {
                    System.out.println("bikin baru karena layout blm ada: " + orderName);
                    vlm = new VLMembers(orderName);
                }
                nvlm.add(vlm);
            }
        }
        lor.setVLMember(nvlm);
        return lor;
    }

    private static String contains(List<VLMembers> layouts, String elementName) {
        String returnValue = "";
        for (VLMembers singleLayout : layouts) {
            String layoutName = singleLayout.getName().trim();
            String pureName =
                    layoutName.replace(startRow, "").replace(startCol, "").replace(startGroup, "")
                            .replace(endRow, "").replace(endCol, "").replace(endGroup, "");
            if (pureName.equals(elementName)) {
                returnValue = layoutName;
                break;
            }
        }
        return returnValue;
    }

}
