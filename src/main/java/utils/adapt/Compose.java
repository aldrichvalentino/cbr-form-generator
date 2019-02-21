package utils.adapt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class Compose {
    public static <T> Set<T> compose(Set<T> oc, Set<T> specadd, Set<T> specdel) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<T> speccom = new HashSet<T>(); // ada oq tdk ada di builtSpecAdd
        Set<T> speccomlup = new HashSet<T>(); // ada oq tdk ada di builtSpecAdd
        speccom.addAll(oc); // pake oc di builtSpecAdd pake oq
        speccomlup.addAll(speccom);

        // Utk memenuhi rekuiremen pak dwi, ada pengurangan dgn specdel
        // menurut sy tidak benar karena percuma ada spek kasus kalau harus selalu
        // dikurangi specdel.
        // versi sy, specdl hanya spec aneh yng sudah di kurangkan dalam specadd

        System.out.println("Mo dikurangi specdel");
        // dikurangi dgn specdel
        if (!specdel.isEmpty()) {
            System.out.println("Specdel tdk kosong ");
            for (T sdelm : specdel) {
                for (T scmelm : speccomlup) {
                    Method mdel = sdelm.getClass().getDeclaredMethod("getName");
                    Method mcom = scmelm.getClass().getDeclaredMethod("getName");
                    String necom = (String) mcom.invoke(scmelm); // metode mq diinvoke dengan param qm
                    String nedel = (String) mdel.invoke(sdelm);
                    if (necom.equalsIgnoreCase(nedel)) {
                        System.out.println("speccom Sama dgn nedel " + nedel);
                        speccom.remove(scmelm);
                        System.out.println("Stlh remove speccom");
                    }
                }
            }
        }

        System.out.println("Ukuran specadd " + specadd.size());
        speccom.addAll(specadd);

        // Out.println("Ukuran speccom "+speccom.size());
        return speccom;
    }

}