package model.serversender;

import model.connection.Connection;

import java.util.HashMap;
import java.util.Map;

public class ModelGuiServer {

    private Map<String, Connection> connectionMap = new HashMap();

    public Map<String, Connection> getConnectionMap() {
        return connectionMap;
    }

    public void addSub(String id, Connection connection) {
        connectionMap.put(id, connection);
    }

    public void removeConectionSub(String id) {
        connectionMap.remove(id);
    }
}
