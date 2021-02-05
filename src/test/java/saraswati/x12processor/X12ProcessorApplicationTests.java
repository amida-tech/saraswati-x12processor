package saraswati.x12processor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.imsweb.x12.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import saraswati.processor.ProcessorController;


@SpringBootTest
class X12ProcessorApplicationTests {

	ProcessorController processor;

	//Test contents of the file
	@Test
	public void readBytes(){
		String data = "ST*837*987654*005010X223~BHT*0019*00*0123*19960918*0932*CH~NM1*41*2*JONES HOSPITAL*****46*12345~\r\n"+
		"PER*IC*JANE DOE*TE*9005555555~NM1*40*2*MEDICARE*****46*00120~HL*1**20*1~PRV*BI*PXC*203BA0200N~\r\n"+
		"NM1*85*2*JONES HOSPITAL*****XX*9876540809~N3*225 MAIN STREET BARKLEY BUILDING~N4*CENTERVILLE*PA*17111~\r\n"+
		"REF*EI*567891234~PER*IC*CONNIE*TE*3055551234~HL*2*1*22*0~SBR*P*18*******MB~NM1*IL*1*DOE*JOHN*T***MI*030005074A\r\n"+
		"~N3*125 CITY AVENUE~N4*CENTERVILLE*PA*17111~DMG*D8*19261111*M~NM1*PR*2*MEDICARE B*****PI*00435~REF*G2*330127~\r\n"+
		"CLM*756048Q*89.93**14:A:1*A*Y*Y~DTP*434*RD8*19960911~CL1*3**01~HI*BK:3669~HI*BF:4019*BF:79431~\r\n"+
		"HI*BH:A1:D8:19261111*BH:A2:D8:19911101*\r\n" + 
		"BH:B1:D8:19261111*BH:B2:D8:19870101~HI*BE:A2:::15.31~HI*BG:09~NM1*71*1*JONES*JOHN*J~REF*1G*B99937~\r\n"+
		"SBR*S*01*351630*STATE TEACHERS*****CI~OI***Y***Y~NM1*IL*1*DOE*JANE*S***MI*222004433~N3*125 CITY AVENUE~\r\n"+
		"N4*CENTERVILLE*PA*17111~NM1*PR*2*STATE TEACHERS*****PI*1135~LX*1~SV2*0305*HC:85025*13.39*UN*1~\r\n"+
		"DTP*472*D8*19960911~LX*2~SV2*0730*HC:93005*76.54*UN*3~DTP*472*D8*19960911~SE*42*987654~";
		byte[] bytes = data.getBytes();
		String message = new String(bytes, StandardCharsets.UTF_8);
		assertEquals(message, data);
	}

	//Test directory name
	@Test
	public void partitionData(){
		String data = "INS*Y*18*030*XN*A*E**FT~REF*0F*152239999~REF*1L*Blue~DTP*336*D8*20070101~NM1*IL*1*BLUTH*LUCILLE****34*152239999~";
		List<String> dataList = Arrays.asList("INS*Y*18*030*XN*A*E**FT","REF*0F*152239999","REF*1L*Blue",
		"DTP*336*D8*20070101","NM1*IL*1*BLUTH*LUCILLE****34*152239999");
		List<String> lines = Arrays.asList(data.split("~"));
		assertLinesMatch(lines, dataList, data);
	}

	@Test
	public void divide(){
		String segStr = "DTP*336*D8*20070101";
		String [] eleArray = {"DTP", "336", "D8", "20070101"};
		String [] elements = segStr.split("\\*");
		assertArrayEquals(segStr, eleArray, elements);
	}
}
