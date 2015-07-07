package nl.tudelft.serg.paperauthorinferencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import nl.tudelft.serg.paperauthorinferencer.PDFPaper;

import org.junit.Test;

public class PDFPaperTest {

	@Test
	public void testCanRead() {
		PDFPaper pdfReader = new PDFPaper(
				"src/test/resources/msr2014.pdf");
		assertEquals(10, pdfReader.pages);
		assertNotNull(pdfReader.content);
		assertEquals("\n", pdfReader.lineSeparator);
	}

}
