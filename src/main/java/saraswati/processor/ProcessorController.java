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
 
    
    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public void receive(@Payload byte[] bytes) throws Exception{
        String message = new String(bytes, StandardCharsets.UTF_8);
        File filename = new File(message);
        parseEdi(filename);
    }

    public void parseEdi(File filename) throws IOException{

        BufferedReader br = new BufferedReader(new FileReader(filename));
        X12 x12 = new X12();
        int count = 0;

        for( String line = br.readLine(); line != null; line = br.readLine()){
            
            Loop loop = new Loop();
            String str = line.substring(0, 2);
            if(str.contains("HL") || str.contains("NM1")){
                Segment segment = new Segment();
                segment.addElements(line);
                loop.addSegment(segment);
                count++;
            }
            
            Segment segment = new Segment();
            segment.addElements(line);
            x12.addSegment(segment);
            x12.addLoop(count, loop);
        }
    }
}
