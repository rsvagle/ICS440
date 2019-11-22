package com.abc.handoff;

import com.abc.pp.stringhandoff.*;
import com.programix.thread.*;


/*
 * Ryan Vagle
 * ICS 440 Project
 * 2-Thread String Handoff
 */
public class StringHandoffImpl implements StringHandoff {

    private String messageBeingPassed;
    private boolean passerIn;
    private boolean receiverIn;
    private boolean messageSet;
    private boolean shutdown;

    public StringHandoffImpl() {
        this.messageBeingPassed = null;
        this.passerIn = false;
        this.receiverIn = false;
        this.messageSet = false;
        this.shutdown = false;

    }

    @Override
    public synchronized void pass(String msg, long msTimeout)
            throws InterruptedException,
                   TimedOutException,
                   ShutdownException,
                   IllegalStateException {

        // Check if shutdown immediately and after every return from a wait()
        if(shutdown) {
            throw new ShutdownException();
        }

        // Check immediately if there's already a passer in here
        if(passerIn) {
            throw new IllegalStateException("There is already a thread passing a message");
        }

        // We're good to go in
        // We need to wrap the rest in a try/finally to make sure we reset
        // the "in here" variable in case we get kicked out because of an exception
        try {

        // Declare myself in an notify everyone
        passerIn = true;
        getLockObject().notifyAll();

        // Timeout conditions
        if(!receiverIn) {
            if (msTimeout == 0L) {
                // wait without ever timing out
                while (!receiverIn){
                    getLockObject().wait();
                    if(shutdown) {
                        throw new ShutdownException();
                    }
                }
            } else {

            long msEndTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;
            while (msRemaining > 0L) {
                getLockObject().wait(msRemaining);
                if(shutdown) {
                    throw new ShutdownException();
                }
                if (receiverIn) break; // success
                msRemaining = msEndTime - System.currentTimeMillis();
            }
                if(!receiverIn) {
                    throw new TimedOutException(); // timed out;
                }
            }
        }

        // Assign the message and notify all
        messageBeingPassed = msg;
        messageSet = true;
        getLockObject().notifyAll();

        // While the message is still there, wait
        while(messageSet) {
            getLockObject().wait();
        }

        } finally {
            // No matter how you leave, reset the "in" variable
            passerIn = false;
        }
    }

    @Override
    public synchronized void pass(String msg)
            throws InterruptedException,
                   ShutdownException,
                   IllegalStateException {

        pass(msg, 0);
    }

    @Override
    public synchronized String receive(long msTimeout)
            throws InterruptedException,
                   TimedOutException,
                   ShutdownException,
                   IllegalStateException {

        // Check if shutdown immediately and after every return from a wait()
        if(shutdown) {
            throw new ShutdownException();
        }


        // Check immediately to see if a receiver is already in here
        if(receiverIn) {
            throw new IllegalStateException("There is already a thread receiving a message");
        }

        // We're good to go in
        // We need to wrap the rest in a try/finally to make sure we reset
        // the "in here" variable in case we get kicked out because of an exception
        try {

        // Declare myself in and notify everyone
        receiverIn = true;
        getLockObject().notifyAll();

        // Timeout conditions
        if(!passerIn) {
            if (msTimeout == 0L) {
                // wait without ever timing out
                while (!passerIn){
                    getLockObject().wait();
                    if(shutdown) {
                        throw new ShutdownException();
                    }
                }
            } else {

            long msEndTime = System.currentTimeMillis() + msTimeout;
            long msRemaining = msTimeout;
            while (msRemaining > 0L) {
                getLockObject().wait(msRemaining);
                if(shutdown) {
                    throw new ShutdownException();
                }
                if (passerIn) break; // success
                msRemaining = msEndTime - System.currentTimeMillis();
            }
                if(!passerIn) {
                    throw new TimedOutException(); // timed out;
                }
            }
        }

        // If a message hasn't been stuffed in, wait
        while(!messageSet) {
            getLockObject().wait();
        };

        // There is a message! Grab it, pull down the flag, and notify you did
        String msg = messageBeingPassed;
        messageSet = false;
        getLockObject().notifyAll();

        return msg;

        } finally {
            // Leaving. Do this no matter what!
            receiverIn = false;
        }
    }

    @Override
    public synchronized String receive()
            throws InterruptedException,
                   ShutdownException,
                   IllegalStateException {

        return receive(0);
    }

    @Override
    public synchronized void shutdown() {
        shutdown = true;
        getLockObject().notifyAll();
        // Everyone who's waiting should be alerted of a shutdown
    }

    @Override
    public Object getLockObject() {
        return this;
    }
}