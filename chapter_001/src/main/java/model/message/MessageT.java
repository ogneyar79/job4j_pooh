package model.message;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect
public class MessageT {
    public MessageT() {

    }

    @JsonIgnore
    String type;
    String topic;
    String text;

}
