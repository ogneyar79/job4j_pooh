package model.server;

import model.connection.Conection;

import java.util.HashMap;
import java.util.Map;

public class ModelGuiServer {

    private Map<String, Conection> subscribersConect = new HashMap();

    public Map<String, Conection> getSubscribersConect() {
        return subscribersConect;
    }

    public void addSub(String id, Conection conection) {
        subscribersConect.put(id, conection);
    }

    public void removeConectionSub(String id) {
        subscribersConect.remove(id);
    }
}
