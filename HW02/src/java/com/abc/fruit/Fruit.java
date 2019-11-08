package com.abc.fruit;

public class Fruit{
    private String name;

    public Fruit(String name) {
        this.name = name;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private void runWork(){
        for ( int i = 0; i < 20; i++ ) {
            try {
                Thread.sleep((long) (Math.random()*1000));
            } catch ( InterruptedException x ) {
                x.printStackTrace();
            }
            System.out.println(name);
        }
    }
}
