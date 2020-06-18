package model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HandlerWithJsonTest {

    HandlerWithJson handler = new HandlerWithJson();
    String json = " {\"topic\" : \"weather\", \"text\" : \"temperature +18 C\"}";
    MessageB message;
    MessageB testMessage = new MessageB("topic", "weather", "temperature +18 C");

    @org.junit.Test
    public void parseJson() throws Exception {
        message = handler.parseJson(json);
        System.out.println(message.getType());
        System.out.println(message.getKeyValue());
        System.out.println(message.getTextValue());
        assertThat(testMessage.equals(message), is(true));


    }

}