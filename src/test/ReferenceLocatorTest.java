package test;

import main.PDFPaper;
import main.ReferenceLocator;

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
		ReferenceLocator referenceLocator = new ReferenceLocator(paper);
		referenceLocator.locateReferences();
	}

}
