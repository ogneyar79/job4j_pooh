package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.message.MessageB;
import model.message.MessageT;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

public class HandlerWithJson {

    public MessageT parseJsonVersion(String json){
        MessageT objectMessage = new MessageT();
        StringReader reader = new StringReader(json);

        ObjectMapper mapper = new ObjectMapper();
        try {
             objectMessage = mapper.readValue(reader, MessageT.class);
            System.out.println(objectMessage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objectMessage;
    }

    public String convertJsonVersion(MessageT message){
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(writer, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return writer.toString();
    }


    public String konvertJson(MessageB message) {
        String result = "";
                ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        if (message.getType().equals("queue")) {
            objectNode.put("queue", message.getKeyValue());
            objectNode.put("text", message.getTextValue());
        }
        if (message.getType().equals("topic")) {
            objectNode.put("topic", message.getKeyValue());
            objectNode.put("text", message.getTextValue());
        }
        try {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

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

    private Iterator<Map.Entry<String, JsonNode>> getIeratorJsonNode(String json) {
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
