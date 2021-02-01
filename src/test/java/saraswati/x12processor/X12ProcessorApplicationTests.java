package saraswati.x12processor;

import org.junit.jupiter.api.Test;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
class X12ProcessorApplicationTests {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	//private SinkController sink;
	private static File emptyFile = new File("testSample.txt");
	

	//test if you can write to a file
	@BeforeAll
	public static void writeTestFile(){
		
		try (Writer writer = new FileWriter(emptyFile)) {
			writer.write("Patient data");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Test name of file
	@Test
	public void testFileName (){		
		boolean exists = emptyFile.exists();
		System.out.println(exists);
		
	}

	//Test contents of the file
	@Test
	public void testContentsOfFile(){
		String message = "FHIR Doc: Patient Name: John Doe, etc";
		byte [] testData = message.getBytes();

		try{
			File testFile = testFolder.newFolder("testFHIR.txt");
			sink.messenger(testData);
			assertEquals("FHIR Doc: Patient Name: John Doe, etc", testFile);
		} catch(Exception e){
			System.out.println(e);
		}
	}

	//Test directory name
	@Test
	public void testDirectoryName(){
		try{
			Path tempDirectory =Files.createTempDirectory("test-sinkDirectory");
			assertTrue(Files.exists(tempDirectory));
		} catch(IOException e){
			e.printStackTrace();
		}
	}


	//remove all test files
	@AfterAll
	public static void removeTestFile() throws IOException {
		if(emptyFile.isFile()){
			emptyFile.delete();
		}
	}

}
