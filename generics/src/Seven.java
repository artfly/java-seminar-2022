import java.util.List;

public class Seven {
    // write method: boolean contains(T what, T... objs) {}

    // 1. compiler gives us a warning here:

//    private static <T> boolean contains(T what, List<T>... lists) {
//        for (List<T> list : lists) {
//            if (list.contains(what)) return true;
//        }
//        return false;
//    }

    // 2. what does this warning mean? basically, it means that we might've broke type system.
    // consider the following example:

    private static <T extends Double> void bizarre(List<T>... lists) {
        if (lists.length == 0) return;
        // first, note that vararg in java is basically an array
        // second, we can assign here without a warning since arrays are invariant
        Object[] objects = lists;
        // there will be no ArrayStoreException!
        // because List<Double> and List<Integer> are the same type in runtime: just List
        objects[0] = List.of(1);
        for (List<T> list : lists) {
            // and only here we will get ClassCastException
            double d = list.get(0);
        }
    }

    private static void testBizarre() {
        bizarre(List.of(1.0), List.of(2.0));
    }

    public static void main(String[] args) {
        testBizarre();
    }

    // 3. in order to remove a warning we need to add annotation @SafeVarargs
    // this annotation basically means "we don't do anything weird like crazy array casts in our method":

    @SafeVarargs
    private static <T> boolean contains(T what, List<T>... lists) {
        for (List<T> list : lists) {
            if (list.contains(what)) return true;
        }
        return false;
    }
}