package dev.lpa;

//public class Singleton {
//    private static Singleton instance;
//
//    private Singleton() {
//        if (instance != null) {
//            throw new RuntimeException("Use getInstance() to create!");
//        }
//    }
//
//    public static Singleton getInstance() {
//        if (instance == null) instance = new Singleton();
//        return instance;
//    }
//
//    public void showMessage() {
//        System.out.println("Hello from singleton!");
//    }
//}

// Thread Safe version
public class Singleton {
    private Singleton() {
        if (SingletonHelper.INSTANCE != null) {
            throw new RuntimeException("Use getInstance() to create");
        }
    }

    private static class SingletonHelper {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void showMessage() {
        System.out.println("Hello from singleton!");
    }
}
