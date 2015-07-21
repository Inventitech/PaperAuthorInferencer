package nl.tudelft.serg.paperauthorinferencer;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class ReferenceOccurrenceCounterTest {

	private static PDFPaper pdf;

	@BeforeClass
	public static void setupPDFPaper() {
		pdf = new PDFPaper("src/test/resources/msr2014.pdf");
	}

	@Test
	public void testSingleOccuringReferences() {
		pdf.content = "This is a multi-reference [1], [2], [3]. See how often it occurs [1]?";
		pdf.nonRefContent = pdf.content;
		int numberOfCountedReferences = countNumberOfOccuringReferences(buildReferences());
		assertEquals(4, numberOfCountedReferences);
	}

	@Test
	public void testMultipleOccuringReferences() {
		pdf.content = "This is a multi-reference [1, 2, 3]. See how often it occurs [1]?";
		pdf.nonRefContent = pdf.content;
		int numberOfCountedReferences = countNumberOfOccuringReferences(buildReferences());
		assertEquals(4, numberOfCountedReferences);
	}
	
	@Test
	public void testRangeOccuringReferences() {
		pdf.content = "This is a multi-reference [1-3]. See how often it occurs [1]?";
		pdf.nonRefContent = pdf.content;
		int numberOfCountedReferences = countNumberOfOccuringReferences(buildReferences());
		assertEquals(4, numberOfCountedReferences);
	}

	private Set<Reference> buildReferences() {
		Set<Reference> references = new HashSet<Reference>();
		references.add(new Reference("[1]"));
		references.add(new Reference("[2]"));
		references.add(new Reference("[3]"));
		return references;
	}

	private int countNumberOfOccuringReferences(Set<Reference> references) {
		ReferenceOccurrenceCounter referenceCounter = new ReferenceOccurrenceCounter(
				pdf, references);
		referenceCounter.countReferences();
		return referenceCounter.references.stream().reduce((r, e) -> {
			r.occurences += e.occurences;
			return r;
		}).get().occurences;
	}

}
