package model.broker;

import model.HandlerWithJson;
import model.message.MessageB;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class BrockerServer implements Closeable {

    private final ServerSocket server;
    private final BrokerMessage broker;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private Socket socket;
    private boolean destributeActive = true;

    public void stopDestribute() {
        this.destributeActive = false;
    }

    public BrockerServer(ServerSocket server, BrokerMessage broker) {
        this.server = server;
        this.broker = broker;
        try {
            this.socket = getActiveSocet();
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getActiveSocet() {
        try {
            this.socket = this.server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.socket;
    }

    public void writeLine(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void excute(HandlerWithJson handler) {
        new Thread(() -> work(broker, handler));
    }

    public void excuteDestrebute() {
        new Thread(() -> workDestrebute());
    }

    public void workDestrebute() {
        while (destributeActive) {
            broker.distribute();
            broker.searchNewMessage();
        }
    }

    public void work(BrokerMessage broker, HandlerWithJson handler) {
        while (!this.server.isClosed()) {
            getActiveSocet();

            String request = this.readLine();
            System.out.println("Request : " + request);
            if (request != null) {
                MessageB message = handler.parseJson(request);
                broker.insertFirst(message);
                String response = " We got it";
                this.writeLine(response);
                System.out.println("Response : " + response);
            }
        }
    }
}



