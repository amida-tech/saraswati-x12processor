package saraswati.processor;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import com.imsweb.x12.*;


@EnableBinding(Processor.class)
public class ProcessorController {
 
    
    public Segment parseSegment(String line){
        
        Segment segment = new Segment();
        String [] elementStr = line.split("*", 10);
        int count = 0;

        //Add individual elements and subelements to segment
        for(String eleStr : elementStr){
            Element element = new Element(elementStr[count], elementStr[count+1]);
            segment.addElement(element);
            count++;
        }        

        return segment;
    }

    public Loop parseLoop(String current, File currentFile) throws IOException{

        Loop loop = new Loop();
        BufferedReader br = new BufferedReader(new FileReader(currentFile));

        //Add segments to the loop, call recursively for inner loops
        for(String line = current; current != null; current = br.readLine()){
            loop.addSegment(parseSegment(line));
            if(line.contains("HL")||line.contains("NM1")){
                parseLoop(current, currentFile);
            }
        }

        return loop;
    }

    public void parseEdi(File filename) throws IOException{

        BufferedReader br = new BufferedReader(new FileReader(filename));
        X12 x12 = new X12();
        int count = 0;

        for( String line = br.readLine(); line != null; line = br.readLine()){    
            if(line.contains("HL") || line.contains("NM1")){
                x12.addLoop(count, parseLoop(line, filename));
                count++;
            } else {    
                x12.addSegment(parseSegment(line));
                count++;
            }
        }

    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public void receive(@Payload byte[] bytes) throws Exception{
        String message = new String(bytes, StandardCharsets.UTF_8);
        File filename = new File(message);
        parseEdi(filename);
    }

    

    
}
