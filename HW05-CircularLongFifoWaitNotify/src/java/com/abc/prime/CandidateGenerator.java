package com.abc.prime;

public class CandidateGenerator {
    private final LongFifo outputFifo;

    public CandidateGenerator(LongFifo outputFifo) {
        this.outputFifo = outputFifo;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };

        Thread t = new Thread(r, "CandidateGenerator");
        t.start();
    }

    private void runWork() {
        try {
            outputFifo.add(2);
            long number = 3;
            while ( true ) {
                outputFifo.add(number);
                number += 2;
            }
        } catch ( InterruptedException x ) {
            // ignore and let the thread die
        }
    }
}
