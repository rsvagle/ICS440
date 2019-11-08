package com.abc.prime;

public class PrimePrinter {
    private final LongFifo inputFifo;

    public PrimePrinter(LongFifo inputFifo) {
        this.inputFifo = inputFifo;

        new Thread(new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        }, "PrimePrinter").start();
    }

    private void runWork() {
        // take a prime off the "known to be prime" FIFO and print it out...
        // ... if there aren't any, sleep a bit, then try again....
        try {
            NanoTimer timer = NanoTimer.createStarted();
            int maxCountPerLine = 10;
            int primeCount = 0;
            int currCount = 0;
            while ( true ) {
                long primeNumber = inputFifo.remove();
                primeCount++;
                System.out.printf("%7d  ", primeNumber);
                currCount++;
                if (currCount == maxCountPerLine) {
                    System.out.println();
                    currCount = 0;
                }
                if (primeNumber > 100000L) {
                    if ( currCount > 0 ) {
                        System.out.println();
                    }
                    System.out.printf(
                        "Found %,d primes in %.5f seconds with " +
                        "the last one %,d%n",
                        primeCount, timer.getElapsedSeconds(), primeNumber);
                    return;
                }
            }
        } catch ( InterruptedException x ) {
            // ignore and let the thread die
        }
    }
}
