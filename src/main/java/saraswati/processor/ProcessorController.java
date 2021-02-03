package saraswati.processor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

//import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.StreamListener;
import com.imsweb.x12.*;


//@EnableBinding(Processor.class)
public class ProcessorController {

    public Segment parseSegment(String line){
        
        Segment segment = new Segment();
        String [] elementStr = line.split("\\*");
        int count = 0;

        //Add individual elements and subelements to segment
        segment.setId(elementStr[count]);
        for(String eleStr : elementStr){
            count++;
            String id = "0"+ String.valueOf(count);  
            Element element = new Element(id, eleStr);
            segment.addElement(element);
        }        

        return segment;
    }

    public void parseLoop(Loop loop, List<String> data, int start, int end){

        for(int i = start; i <= end; i++){
            loop.addSegment(parseSegment(data.get(i)));
        }

    }

    public void parseEdi(List<String> lines){

        X12 x12 = new X12();
        Loop loop_header = new Loop();
        Loop loop_provider = new Loop();
        Loop loop_subscriber = new Loop();
        Loop loop_client = new Loop();
        Loop loop_claim_info = new Loop();

        parseLoop(loop_header, lines, 0, 6);
        parseLoop(loop_provider, lines, 7, 17);
        parseLoop(loop_subscriber, lines, 18, 27);
        parseLoop(loop_client, lines, 28, 38);
        parseLoop(loop_claim_info, lines, 39, lines.size());

        x12.addLoop(1000, loop_header);
        x12.addLoop(2000, loop_provider);
        x12.addLoop(1000, loop_subscriber);
        x12.addLoop(1000, loop_client);
        x12.addLoop(1000, loop_claim_info);

    }

    //@StreamListener(Processor.INPUT)
    //@SendTo(Processor.OUTPUT)
    public String receive(@Payload byte[] bytes){
        String message = new String(bytes, StandardCharsets.UTF_8);
        List<String> lines = Arrays.asList(message.split("~"));
        parseEdi(lines);
        return null;
    }
   
}
