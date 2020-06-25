package model.message;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect
public class MessageQ {

       @JsonIgnore
       String type = "queue";

       String queue = "weather";
       String text = "temperature +18 C";
}
