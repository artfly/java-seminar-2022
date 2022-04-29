import java.util.Comparator;
import java.util.Objects;

public class Six {
    // implement compareTo for these classes:
    // Product(int price), Camera(String brand) extends Product, Phone extends Product

    // 1. create this classes

//    private static abstract class Product {
//        protected final int price;
//
//        protected Product(int price) {
//            this.price = price;
//        }
//    }
//
//    private static class Camera extends Product {
//        private final String brand;
//
//        private Camera(int price, String brand) {
//            super(price);
//            this.brand = brand;
//        }
//    }
//
//    private static class Phone extends Product {
//        protected Phone(int price) {
//            super(price);
//        }
//    }

    // 2. now inherit from Comparable<Product>

//    private static abstract class Product implements Comparable<Product> {
//        protected final int price;
//
//        protected Product(int price) {
//            this.price = price;
//        }
//
//        @Override
//        public int compareTo(Product o) {
//            // we do not use price - o.price because of possible underflow
//            return Integer.compare(price, o.price);
//        }
//    }
//
//    private static class Camera extends Product {
//
//        private final String brand;
//
//        private Camera(int price, String brand) {
//            super(price);
//            this.brand = brand;
//        }
//
//        @Override
//        public int compareTo(Product o) {
//            int cmp = super.compareTo(o);
//            if (cmp != 0) return cmp;
//            // see https://openjdk.java.net/jeps/394
//            if (o instanceof Camera c) {
//                return Objects.compare(brand, c.brand, Comparator.nullsFirst(String::compareTo));
//            }
//            // wtf? we have to compare cameras with other types of products
//            // probably it would be better to expect only Camera as input parameter in 'compareTo'
//            return -1;
//        }
//    }

    // 3. rewrite code so that camera would have method compareTo(Camera) inside
    // it is possible when 'Product implements Comparable<Camera>'. But then logic for phone would be broken:


//    private static abstract class Product implements Comparable<Camera> {
//        protected final int price;
//
//        protected Product(int price) {
//            this.price = price;
//        }
//
//        @Override
//        public int compareTo(Product o) {
//            return Integer.compare(price, o.price);
//        }
//    }
//
//    private static class Camera extends Product {
//        // some code
//    }
//
//    private static class Phone extends Product {
//
//        // some code
//
//        @Override
//        public int compareTo(Camera o) {
//            // now we cannot even compare two phones!
//            return 0;
//        }
//    }

    // 4. ok, seems that we need to tell somehow:
    // "This class can be comparable only with itself."
    // also please note that we do not know anything about Phone or Camera in Product (they may be a part of user code)
    // probably we need some kind of generic <T extends Product> ....

//    private static abstract class Product<T extends Product> implements Comparable<T> {
//
//        protected final int price;
//
//        protected Product(int price) {
//            this.price = price;
//        }
//
//        @Override
//        public int compareTo(Product o) {
//            return Integer.compare(price, o.price);
//        }
//    }
//
//    private static class Camera extends Product<Camera> {
//
//        private final String brand;
//
//        protected Camera(int price, String brand) {
//            super(price);
//            this.brand = brand;
//        }
//
//        @Override
//        public int compareTo(Camera o) {
//            int cmp = super.compareTo(o);
//            if (cmp != 0) return cmp;
//            return Objects.compare(brand, o.brand, Comparator.nullsFirst(String::compareTo));
//        }
//    }

    // 5. nice, we almost there!
    // the only thing is left is that we have to specify generic type in Product signature
    // Product<T extends Product<HERE> >
    // what can we put there? we know that for Cameras base class Product has Camera as generic argument
    // so, if we're replacing T with Camera: Product<Camera extends Product<Camera> > .
    // Seems that we need to put T in signature.

    private static abstract class Product<T extends Product<T>> implements Comparable<T> {

        protected final int price;

        protected Product(int price) {
            this.price = price;
        }

        @Override
        public int compareTo(T o) {
            return Integer.compare(price, o.price);
        }
    }

    private static class Camera extends Product<Camera> {

        private final String brand;

        protected Camera(int price, String brand) {
            super(price);
            this.brand = brand;
        }

        @Override
        public int compareTo(Camera o) {
            int cmp = super.compareTo(o);
            if (cmp != 0) return cmp;
            return Objects.compare(brand, o.brand, Comparator.nullsFirst(String::compareTo));
        }
    }

    private static class Phone extends Product<Phone> {

        protected Phone(int price) {
            super(price);
        }
    }

    public static void main(String[] args) {
        Phone phone = new Phone(42);
        Camera camera = new Camera(1337, "dunno");
//        phone.compareTo(camera);
        phone.compareTo(phone);
        camera.compareTo(camera);
        Product<Camera> cameraProduct = camera;
//        cameraProduct.compareTo(phone);
    }

}
