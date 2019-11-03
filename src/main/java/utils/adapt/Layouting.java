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
          String pureName =
              layoutName.replace(startRow, "").replace(startCol, "").replace(startGroup, "")
                  .replace(endRow, "").replace(endCol, "").replace(endGroup, "");
          Method method = field.getClass().getDeclaredMethod("getName");
          String fieldName = (String) method.invoke(field);

          if (pureName.equals(fieldName)) {
            int startIndex = layoutName.indexOf(pureName);
            int endIndex = startIndex + fieldName.length();
            int startTokenLength = startIndex;
            int endTokenLength = layoutName.length() - endIndex;
            if (startTokenLength > endTokenLength) {
              String startToken = layoutName.substring(0, startTokenLength - endTokenLength);
              nvlo.get(currentLayout + 1)
                  .setName(startToken + nvlo.get(currentLayout + 1).getName());
            } else if (endTokenLength > startTokenLength) {
              String endToken = layoutName.substring(
                  layoutName.length() - (endTokenLength - startTokenLength), layoutName.length());
              nvlo.get(currentLayout - 1).setName(nvlo.get(currentLayout - 1).getName() + endToken);
            }
            nvlo.remove(currentLayout);
            break;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    layoutResult.setVLMember(nvlo);
    return layoutResult;
  }

  public static <T> LOResult setLayouting(Set<T> sadd, LOResult layoutResult, List<Orders> orders)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, InstantiationException {

    if (sadd.isEmpty())
      return layoutResult;
    System.out.println("Start layouting ...");
    List<VLMembers> nvlm = new ArrayList<VLMembers>();
    VLMembers vlm;

    for (Orders ord : orders) {
      for (OMembers omember : ord.getoMembers()) {
        String orderName = omember.getMemberName();
        String elementNameInLayout = contains(layoutResult.getVLMember(), orderName);
        if (!elementNameInLayout.equals("")) {
          vlm = new VLMembers(elementNameInLayout);
        } else {
          System.out.println("Create new in layout: " + orderName);
          vlm = new VLMembers(orderName);
        }
        nvlm.add(vlm);
      }
    }
    fixedGroupsInLayout(nvlm, orders);
    layoutResult.setVLMember(nvlm);
    return layoutResult;
  }

  private static void fixedGroupsInLayout(List<VLMembers> layouts, List<Orders> orders) {
    int startIndex = 0;
    for (Orders order : orders) {
      List<OMembers> orderMembers = order.getoMembers();
      int groupSize = orderMembers.size();
      // check for open group tag [
      if (!layouts.get(startIndex).getName().contains(startGroup)) {
        // opening tag must be in preceeding elements
        for (int nextElement = startIndex + 1; nextElement < layouts.size(); nextElement++) {
          String elementName = layouts.get(nextElement).getName();
          int indexOfStartGroup = elementName.indexOf(startGroup);
          if (indexOfStartGroup >= 0) {
            String remove = elementName.substring(0, indexOfStartGroup + 1);
            elementName = elementName.replace(remove, "");
            layouts.get(nextElement).setName(elementName);
            layouts.get(startIndex).setName(remove + layouts.get(startIndex).getName());
            break;
          }
        }
      }

      int lastGroupIndex = startIndex + groupSize - 1;
      // check for close group tag ]
      if (!layouts.get(lastGroupIndex).getName().contains(endGroup)) {
        // closing tag must be in previous elements
        for (int prevElement = lastGroupIndex - 1; prevElement >= 0; prevElement--) {
          String elementName = layouts.get(prevElement).getName();
          int indexOfEndGroup = elementName.indexOf(endGroup);
          if (indexOfEndGroup >= 0) {
            String remove = elementName.substring(indexOfEndGroup, elementName.length());
            elementName = elementName.replace(remove, "");
            layouts.get(prevElement).setName(elementName);
            layouts.get(lastGroupIndex).setName(layouts.get(lastGroupIndex).getName() + remove);
            break;
          }
        }
      }
      startIndex += groupSize;
    }
  }

  private static String contains(List<VLMembers> layouts, String elementName) {
    String returnValue = "";
    for (VLMembers singleLayout : layouts) {
      String layoutName = singleLayout.getName().trim();
      String pureName = layoutName.replace(startRow, "").replace(startCol, "")
          .replace(startGroup, "").replace(endRow, "").replace(endCol, "").replace(endGroup, "");
      if (pureName.equals(elementName)) {
        returnValue = layoutName;
        break;
      }
    }
    return returnValue;
  }
}
