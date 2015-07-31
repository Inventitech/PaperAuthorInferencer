package nl.tudelft.serg.paperauthorinferencer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class PDFPaperTest {

	@Test
	public void testCanRead() {
		PDFPaper pdfReader = new PDFPaper("src/test/resources/msr2014.pdf");
		assertEquals(10, pdfReader.pages);
		assertNotNull(pdfReader.content);
		assertEquals("\n", pdfReader.lineSeparator);
	}

	@Test
	public void testIfMetadataIsCorrect() {
		PDFPaper pdfReader = new PDFPaper("src/test/resources/2013_ICSE_ICSE13_p222-hatcliff.pdf");
		HashSet<Author> expectedAuthors = new HashSet<Author>();
		expectedAuthors.add(new Author("Robby"));
		expectedAuthors.add(new Author("John Hatcliff"));
		expectedAuthors.add(new Author("Patrice Chalin"));

		List<String> expectedAuthorList = expectedAuthors.stream().map(a -> a.getCanonicalName()).sorted()
				.collect(Collectors.toList());
		List<String> actualAuthorList = pdfReader.authors.stream().map(a -> a.getCanonicalName()).sorted()
				.collect(Collectors.toList());
		assertEquals(expectedAuthorList, actualAuthorList);
	}

	@Test
	public void testIfMetadataIsCorrectWhalenPaper() {
		PDFPaper pdfReader = new PDFPaper("src/test/resources/2013_ICSE_ICSE13_p102-whalen.pdf");
		assertEquals(5, pdfReader.authors.size());
	}

}
