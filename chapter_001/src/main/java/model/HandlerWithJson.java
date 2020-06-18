package model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class HandlerWithJson {
    public MessageB parseJson(String json) {
        String tipeKey = null;
        String keyValue = null;
        String textValue = null;

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = getIeratorJsonNode(json);
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            if (field.getKey().equals("topic") || field.getKey().equals("queue")) {
                tipeKey = field.getKey();
                keyValue = field.getValue().textValue();
            } else {
                textValue = field.getValue().textValue();
            }
        }
        return new MessageB(tipeKey, keyValue, textValue);
    }

    public Iterator<Map.Entry<String, JsonNode>> getIeratorJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootNode.fields();
    }

    public static void main(String... args) {
        HandlerWithJson handler = new HandlerWithJson();
        String json = " {\"id\" : 12345, \"value\" : \"123\", \"person\" : \"1\"}";
        handler.parseJson(json);
    }
}
