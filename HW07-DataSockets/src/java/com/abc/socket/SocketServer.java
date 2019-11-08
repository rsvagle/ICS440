package com.abc.socket;

import java.io.*;
import java.net.*;

import com.abc.log.*;

public class SocketServer {
    private final Log log;
    private ServerSocket serverSocket;
    private Socket socket;

    public SocketServer(int port, Log log) throws IOException {
        this.log = log;

        log.outln("Attempting to begin accepting connections on port " + port);

        // Setup a ServerSocket
        serverSocket = new ServerSocket(port);

        // Spawn an internal thread to wait for connections
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
        // Accept socket connections for clients...
        int connectionNumber = 0;

        while(true) {
            log.outln("Waiting for connections");

            socket = new Socket();
            try {
                socket = serverSocket.accept();
                log.outln("Accepted connection #" + connectionNumber++);
                new Conversation(socket);
            } catch ( IOException x ) {
                x.printStackTrace();
            }
        }
    }
}

class Conversation {
    private final RequestResponseMapping requestResponseMapping;
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public Conversation(Socket socket) {
        this.socket = socket;
        this.requestResponseMapping = RequestResponseMapping.getInstance();

        try {
            out = new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream()));

            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

            } catch ( IOException x ) {
                x.printStackTrace();
            }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runWork();
            }
        };

        Thread t = new Thread(r, getClass().getName());
        t.start();
    }


    protected void runWork() {
            try {
                while(true) {
                    String request = in.readUTF();
                    if(request.equals(requestResponseMapping.getDisconnectRequest())) {
                        String response = requestResponseMapping.getDisconnectResponse();
                        out.writeUTF(response);
                        out.flush();
                        out.close();
                        in.close();
                        break;
                    } else {
                    String response = requestResponseMapping.getResponse(request);
                    out.writeUTF(response);
                    out.flush();
                    }
                }
            } catch ( IOException x ) {
                x.printStackTrace();
            }
    }
}

