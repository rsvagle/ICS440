package com.abc.prime;

public class PrimeChecker {
    private final LongFifo inputFifo;
    private final LongFifo outputFifo;

    public PrimeChecker(LongFifo inputFifo, LongFifo outputFifo) {
        this.inputFifo = inputFifo;
        this.outputFifo = outputFifo;

        new Thread(new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        }, "PrimeChecker").start();
    }

    private void runWork() {
        try {
            while ( true ) {
                long candidate = inputFifo.remove();
                if ( isPrime(candidate) ) {
                    outputFifo.add(candidate);
                }
            }
        } catch ( InterruptedException x ) {
            // ignore and let the thread die
        }
    }

    private boolean isPrime(long number) {
        if ( number < 2 ) {
            return false;
        }

        long divisorLimit = 1 + (long) Math.sqrt(number);
        for ( long divisor = 2; divisor < divisorLimit; divisor++ ) {
            if ( number % divisor == 0 ) {
                return false;
            }
        }
        return true;
    }
}
