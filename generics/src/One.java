public class One {
    // Box - wrapper for any object
    static class Box<T> {
        private final T thing;

        Box(T thing) {
            this.thing = thing;
        }

        public T getThing() {
            return thing;
        }
    }

    public static void main(String[] args) {
        Box<String> strBox = new Box<>("foo");
        Box<Integer> intBox = new Box<>(42);
        int i = intBox.getThing();
//        String s = getStrFromBox(intBox);
    }

    public static String getStrFromBox(Box<String> box) {
        return box.getThing();
    }
}
