package dev.lpa;


class FirstThread extends Thread{
    @Override
    public void run() {
        for (int i = 0; i <= 10; i+=2) {
            System.out.println("FirstThread: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("FirstThread interrupted!");
                break;
            }
        }
    }
}

class SecondThread implements Runnable {

    @Override
    public void run() {

    }
}

public class Main {
    public static void main(String[] args) {
        FirstThread firstThread = new FirstThread();
        Runnable runnable = () -> {
            for (int i = 1; i < 10; i+=2) {
                System.out.println("Second Thread: " + i);
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    System.out.println("Second Thread Interrupted!");
                    break;
                }
            }
        };
        Thread secondThread = new Thread(runnable);

        firstThread.start();
        secondThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        firstThread.interrupt();
    }
}
