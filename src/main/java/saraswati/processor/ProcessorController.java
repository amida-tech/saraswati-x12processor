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

    public Loop parseLoop(List<String> lines){
        
        //Find line to start new loop
        //Add segments/loops to the loop
        int index = 0;
        Loop outerLoop = new Loop();

        for(int i = 0,j = 0,k = 0; i < lines.size(); i++, j++, k++){
            if(j % 5 == 0 && lines.get(i).contains("HL") || lines.get(i).contains("NM1") || lines.get(i).contains("N1")){
                Loop loop = new Loop();      
                while(j < lines.size()){
                    loop.addSegment(parseSegment(lines.get(j)));   
                }
                if(k % 10 == 0 && lines.get(i).contains("HL") || lines.get(i).contains("NM1") || lines.get(i).contains("N1")){
                    Loop newLoop = new Loop();
                    while(k < lines.size()){
                        newLoop.addSegment(parseSegment(lines.get(k)));
                    }
                    loop.addLoop(index, newLoop);
                }
            outerLoop.addLoop(index, loop);
            } 
        
        }

        return outerLoop;
    }

    public void parseEdi(List<String> lines){

        X12 x12 = new X12();
        int index = 0;

        for(int i = 0; i < lines.size(); i++) {
            if(lines.get(i).contains("HL") || lines.get(i).contains("NM1") || lines.get(i).contains("N1")){
                x12.addLoop(index, parseLoop(lines));
                index++;
            } else {    
                x12.addSegment(parseSegment(lines.get(i)));
            }

        }

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
