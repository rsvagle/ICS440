package com.abc.socket;

import java.io.*;
import java.net.*;

import com.abc.log.*;

public class SocketClient {
    private final Log log;
    private final RequestResponseMapping requestResponseMapping;
    private final Socket socket;
    private  DataOutputStream out;
    private DataInputStream in;

    public SocketClient(String host, int port, Log log) throws IOException {
        this.log = log;
        this.requestResponseMapping = RequestResponseMapping.getInstance();

        log.outln("Attempting to connect to " + host + ":" + port);

        // Connect the socket and initialize I/O streams
        socket = new Socket(host, port);
        log.outln("Connected to " + host + ":" + port);

        // Start the data input/output streams
        try {
            out = new DataOutputStream(
                new BufferedOutputStream(socket.getOutputStream()));

            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));

            } catch ( IOException x ) {
                x.printStackTrace();
            }
    }

    public String chat(String request) throws IOException {
        // The request is sent to the server and
        // the response is returned.

        out.writeUTF(request);
        out.flush();
        log.outln("Sent " + request);

        String response = in.readUTF();
        log.outln("Recieved " + response);

        return response;
    }

    public void disconnect() throws IOException {
        // Send the goodbye stuff and then close the connection
        chat(requestResponseMapping.getDisconnectRequest());
        out.flush();
        out.close();
        socket.close();
    }
}
