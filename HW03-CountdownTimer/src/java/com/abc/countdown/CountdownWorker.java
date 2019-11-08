package com.abc.countdown;

public class CountdownWorker {
    private final int totalSeconds;
    private final MinuteSecondDisplay display;

    public CountdownWorker(int totalSeconds, MinuteSecondDisplay display) {
        this.totalSeconds = totalSeconds;
        this.display = display;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };

        Thread t = new Thread(r, getClass().getName());
        t.start();

    }

    private void runWork() {

        int secondsDisplay = 10;
        int sleepTime = 1000;

        display.setSeconds(secondsDisplay);
        long startTime = System.currentTimeMillis();

        for ( int i = 0; i < 10; i++ ) {

            long targetTime = startTime + (1000*i);

            try {
                Thread.sleep(sleepTime);
            } catch ( InterruptedException x ) {
                x.printStackTrace();
            }

            sleepTime = sleepTime - (int) (System.currentTimeMillis() - targetTime - 1000);

            secondsDisplay--;
            display.setSeconds(secondsDisplay);
        }

        long finishTime = System.currentTimeMillis();
        long timeTotal = finishTime - startTime;

        System.out.println("Total time: " + timeTotal + "ms");
    }
}