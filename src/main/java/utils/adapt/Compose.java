package utils.adapt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class Compose {
  public static <T> Set<T> compose(Set<T> oc, Set<T> specadd, Set<T> specdel)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    Set<T> speccom = new HashSet<T>();
    Set<T> speccomlup = new HashSet<T>();
    speccom.addAll(oc);
    speccomlup.addAll(speccom);

    // delete by specDel
    if (!specdel.isEmpty()) {
      for (T sdelm : specdel) {
        for (T scmelm : speccomlup) {
          Method mdel = sdelm.getClass().getDeclaredMethod("getName");
          Method mcom = scmelm.getClass().getDeclaredMethod("getName");
          String necom = (String) mcom.invoke(scmelm);
          String nedel = (String) mdel.invoke(sdelm);
          if (necom.equalsIgnoreCase(nedel)) {
            speccom.remove(scmelm);
          }
        }
      }
    }

    speccom.addAll(specadd);
    return speccom;
  }
}
