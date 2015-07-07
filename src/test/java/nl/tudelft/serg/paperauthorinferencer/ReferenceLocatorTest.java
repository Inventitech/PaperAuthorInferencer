package nl.tudelft.serg.paperauthorinferencer;

import static org.junit.Assert.assertEquals;
import nl.tudelft.serg.paperauthorinferencer.PDFPaper;
import nl.tudelft.serg.paperauthorinferencer.ReferenceListBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

public class ReferenceLocatorTest {

	private static PDFPaper paper;

	@BeforeClass
	public static void setup() {
		paper = new PDFPaper("src/test/resources/msr2014.pdf");
	}

	@Test
	public void testFindsReferenceLine() {
		ReferenceListBuilder referenceLocator = new ReferenceListBuilder(paper);
		referenceLocator.locateReferences();
		assertEquals(54, referenceLocator.references.size());
	}
	
	@Test
	public void testCreatesNonReferenceContent() {
		ReferenceListBuilder referenceLocator = new ReferenceListBuilder(paper);
		referenceLocator.locateReferences();
		assertEquals(true, !paper.nonRefContent.isEmpty());
	}

}
