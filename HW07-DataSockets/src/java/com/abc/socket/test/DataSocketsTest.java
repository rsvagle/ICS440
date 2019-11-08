package com.abc.socket.test;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import com.abc.log.*;
import com.abc.socket.*;
import com.programix.gui.*;
import com.programix.testing.*;

public class DataSocketsTest extends AbstractRegression {
    private static final String HOST = "localhost";
    private static final int PORT = 4500;

    private final LogPane testLogPane;
    private final LogPane serverLogPane;
    private final LogPane clientALogPane;
    private final LogPane clientBLogPane;
    private final RequestResponseMapping requestResponseMapping;

    public DataSocketsTest() {
        requestResponseMapping = RequestResponseMapping.getInstance();

        testLogPane = new LogPane("Regression Output");
        serverLogPane = new LogPane("SocketServer");
        clientALogPane = new LogPane("SocketClient-A");
        clientBLogPane = new LogPane("SocketClient-B");
        buildUI();

        setOutput(testLogPane);
    }

    private void buildUI() {
        JPanel mainP = new JPanel(new GridLayout(2, 0, 0, 0));
        mainP.add(testLogPane);
        mainP.add(serverLogPane);
        mainP.add(clientALogPane);
        mainP.add(clientBLogPane);

        JFrame f = new JFrame("DataSocketsTest");
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setContentPane(mainP);
        GuiTools.setSizeToMax(f);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    @SuppressWarnings("unused")
    protected void runBundles() throws Exception {
        new ServerLauncher();

        Thread.sleep(1000);
        SocketClient clientA = new ClientLauncher(clientALogPane).getClient();

        outln("Testing clientA...");
        runSimpleClientTest(clientA);

        SocketClient clientB = new ClientLauncher(clientBLogPane).getClient();

        outln("Testing clientB...");
        runSimpleClientTest(clientB);

        // Be sure that we can go back to A and then back to B
        outln("Confirming that after starting clientB, clientA still works...");
        runSimpleClientTest(clientA);
        outln("Confirming that clientB still works...");
        runSimpleClientTest(clientB);

        outln("Disconnecting clientA...");
        clientA.disconnect();

        outln("Confirming that after disconnecting clientA, " +
                "clientB still works...");
        runSimpleClientTest(clientB);

        outln("Disconnecting clientB...");
        clientB.disconnect();
    }

    private void runSimpleClientTest(SocketClient client) throws IOException {
        for ( int i = 0; i < 5; i++ ) {
            String req = requestResponseMapping.getRandomRequest();
            String expectedRes = requestResponseMapping.getResponse(req);
            String actualRes = client.chat(req);
            outln("Sent '" + req + "'", actualRes, expectedRes);
        }
    }

    public static void main(String[] args) {
        AbstractRegression ar = new DataSocketsTest();
        ar.runTests();
    }

    private class ServerLauncher implements Runnable {
        public ServerLauncher() {
            outln("Attempting to startup SocketServer...");
            new Thread(this, "SocketServer-Launcher").start();
        }

        @SuppressWarnings("unused")
        @Override
        public void run() {
            try {
                new SocketServer(PORT, serverLogPane);
            } catch ( IOException x ) {
                serverLogPane.out(x);
            }
        }
    } // class ServerLauncher

    private class ClientLauncher implements Runnable {
        private Log log;
        private SocketClient client;

        public ClientLauncher(Log log) {
            this.log = log;
            outln("Attempting to startup SocketClient...");
            new Thread(this, "SocketClient-Launcher").start();
        }

        @Override
        public void run() {
            try {
                setClient(new SocketClient(HOST, PORT, log));
            } catch ( IOException x ) {
                log.out(x);
            }
        }

        private synchronized void setClient(SocketClient client) {
            this.client = client;
            notifyAll();
        }

        public synchronized SocketClient getClient()
                throws InterruptedException {

            while ( client == null ) {
                wait();
            }

            return client;
        }
    } // class ClientLauncher
}
