import java.util.Arrays;

public class Two {
    // compare two boxes using 'int compareTo(Box)'. Then rewrite with Comparable

    interface Comparable {
        int compareTo(Object other);
    }

    static class Box implements Comparable {
        private final Object thing;

        Box(Object thing) {
            this.thing = thing;
        }

        public Object getThing() {
            return thing;
        }

        static Box create(Object thing) {
            return new Box(thing);
        }

        public int compareTo(Box other) {
            return ((Comparable)thing).compareTo(other.thing);
        }

        @Override
        public int compareTo(Object other) {
            return compareTo((Box) other);
        }
    }


    static class NonComparable {}

    public static void main(String[] args) {
//        new Box<NonComparable>(new NonComparable());
        Box sBox = Box.create("foo");
        Box iBox = Box.create(42);
        String s = (String) sBox.getThing();
        // 3 methods -> 4 methods
        Arrays.stream(sBox.getClass().getDeclaredMethods()).forEach(System.out::println);
//        sBox.compareTo(iBox);
    }


}
