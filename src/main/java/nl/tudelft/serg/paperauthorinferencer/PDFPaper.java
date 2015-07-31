package nl.tudelft.serg.paperauthorinferencer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFPaper {

	public int pages;

	public String content;

	public String nonRefContent;

	public String lineSeparator;

	public int year;

	public Set<Author> authors = new HashSet<Author>();

	public PDFPaper(String filename) {
		extractYear(filename);
		PDDocument pdfDocument = null;
		try {
			pdfDocument = PDDocument.load(filename, true);
			Set<String> authorNames = new HashSet<String>();
			ReferenceListBuilder.extractAuthors(pdfDocument.getDocumentInformation().getAuthor()+".", authorNames);
			authorNames.forEach(a -> authors.add(new Author(a)));
			PDFTextStripper textStripper = new PDFTextStripper("UTF-8");
			if (!pdfDocument.isEncrypted()) {
				pages = pdfDocument.getNumberOfPages();
				content = textStripper.getText(pdfDocument);
				fixGermanUmlauts();
				lineSeparator = textStripper.getLineSeparator();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				pdfDocument.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void extractYear(String filename) {
		File file = new File(filename);
		int year;
		try {
			year = Integer.valueOf(file.getName().substring(0, 4));
			if (Utils.isReasonablyPossilbeYear(year)) {
				this.year = year;
			}
		} catch (NumberFormatException e) {
			System.err.println("No year in PDF supplied! Using current year as year of publication!");
			this.year = Utils.currentYear;
		}
	}

	private void fixGermanUmlauts() {
		Map<String, String> replaceGlyphons = new HashMap<String, String>();
		replaceGlyphons.put("A¨", "Ä");
		replaceGlyphons.put("O¨", "Ö");
		replaceGlyphons.put("U¨", "Ü");
		replaceGlyphons.put("a¨", "ä");
		replaceGlyphons.put("u¨", "ü");
		replaceGlyphons.put("o¨", "ö");

		replaceGlyphons.entrySet().forEach(e -> replace(e.getKey(), e.getValue()));
	}

	private void replace(String string, String replace) {
		content = content.replaceAll(string, replace);
	}
}
