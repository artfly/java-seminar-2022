import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Four {
    // write util method max(Collection<E>)
    public static<E extends Comparable<E>> E max(Collection<E> collection) {
        Iterator<E> iterator = collection.iterator();
        E max = null;
        while (iterator.hasNext()) {
            E next = iterator.next();
            if (max == null || next.compareTo(max) > 0) max = next;
        }
        return max;
    }

    public static void main(String[] args) {
        List<Integer> integers = List.of(1, 3, 2);
        System.out.println(max(integers));
    }
}
