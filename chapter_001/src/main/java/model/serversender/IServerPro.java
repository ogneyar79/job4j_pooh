package model.serversender;

public interface IServerPro {

    void startServer(int port);

    void stopServer();

    void acceptServer();

    void deleteConnection(String id);
}
