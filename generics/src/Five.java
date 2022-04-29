import java.util.ArrayList;
import java.util.List;

public class Five {
    // write append for two lists
    // PECS: producer - extends, consumer - super
    static <V> void append(List<? extends V> src, List<? super V> dst) {
        if (src == null || dst == null) return;
        dst.addAll(src);
    }

    static class Parent {}
    static class Child extends Parent {}

    public static void main(String[] args) {
        List<Child> children = new ArrayList<>();
        List<Parent> parents = new ArrayList<>();

//        children.addAll(parents);
        parents.addAll(children);

        append(children, parents);
//        append(parents, children);


        List<? extends Object> anything = new ArrayList<Number>();
        anything.add(null);

        List<? extends Number> numbers = new ArrayList<>();
        Number n = 42;
        numbers.add(null);

        List<? super Number> numberParents = new ArrayList<>();
        numberParents.add(42);
//        numberParents.add("");
//         numberParents.add(new Object());

        List<Number> nList = new ArrayList<>();
        nList.add((Number) 42);
//        nList.add(new Number());
        List<Object> oList = new ArrayList<>();
        oList.add((Object) 42);
        oList.add((Object) "str");

        String[] strings = new String[20];
        Object[] objects = strings;
        objects[0] = 42;

        // Object -> Collection<String> -> List<String>
        List<String> ss = new ArrayList<>();
        // Object -> Collection<Object> -> List<Object>
        List<Object> os = new ArrayList<>();
//        os = ss;

        List<? extends String> wildStrings = new ArrayList<>();
        List<? extends Object> wildObjects = new ArrayList<>();
         wildObjects = wildStrings;

         // heap pollution
    }
}
