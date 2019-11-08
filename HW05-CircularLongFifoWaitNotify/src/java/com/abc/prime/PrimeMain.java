package com.abc.prime;

public class PrimeMain {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        LongFifo candidateFifo = new CircularArrayLongFifo(50000);
        LongFifo primeFifo = new CircularArrayLongFifo(100);

        System.out.println("loading up candidate fifo...");
        new CandidateGenerator(candidateFifo);
        while (!candidateFifo.isFull()) {
            try {
                Thread.sleep(200);
            } catch ( InterruptedException x ) {
                x.printStackTrace();
            }
        }
        System.out.println("DONE loading up candidate fifo...");

        new PrimeChecker(candidateFifo, primeFifo);
        new PrimeChecker(candidateFifo, primeFifo);
        new PrimeChecker(candidateFifo, primeFifo);

        new PrimePrinter(primeFifo);
    }
}
