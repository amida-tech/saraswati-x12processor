package saraswati.processor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import com.imsweb.x12.*;


@EnableBinding(Processor.class)
public class ProcessorController {

    static final int TOTAL_NUM_OF_ELEMENTS = 15;

    public Segment parseSegment(String line){
        
        Segment segment = new Segment();
        String [] elementStr = line.split("*", TOTAL_NUM_OF_ELEMENTS);
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

    public Loop parseLoop(int count, File file) throws IOException{

        Loop loop = new Loop();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        //Add segments to the loop, call recursively for inner loops
        for (int i = 0; i < count; i++){
            br.readLine();
        }

        for(line = br.readLine(); line != null; line = br.readLine()){
            loop.addSegment(parseSegment(line));
            if(line.contains("HL")||line.contains("NM1")){
                count++;
                loop.addLoop(1, parseLoop(count, file));
            }
        }

        br.close();
        return loop;
    }

    public void parseEdi(File filename) throws IOException{

        BufferedReader br = new BufferedReader(new FileReader(filename));
        X12 x12 = new X12();
        int count = 0;
        int lineCount = 0;

        for(String line = br.readLine(); line != null; line = br.readLine()){    
            if(line.contains("HL") || line.contains("NM1")){
                lineCount++;
                x12.addLoop(count, parseLoop(lineCount, filename));
            } else {    
                x12.addSegment(parseSegment(line));
            }
            count++;
        }

        br.close();
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public void receive(@Payload byte[] bytes) throws Exception{
        String message = new String(bytes, StandardCharsets.UTF_8);
        File filename = new File(message);
        parseEdi(filename);
    }

    

    
}
