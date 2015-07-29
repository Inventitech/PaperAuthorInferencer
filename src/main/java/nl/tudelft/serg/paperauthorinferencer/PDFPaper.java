package nl.tudelft.serg.paperauthorinferencer;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFPaper {

	public PDFPaper(String filename) {
		PDDocument pdDocument = null;
		extractYear(filename);
		try {
			pdDocument = PDDocument.load(filename, true);
			// TODO (MMB) continue here.
			pdDocument.getDocumentInformation().getAuthor();
			PDFTextStripper textStripper = new PDFTextStripper();

			if (!pdDocument.isEncrypted()) {
				pages = pdDocument.getNumberOfPages();
				content = textStripper.getText(pdDocument);
				lineSeparator = textStripper.getLineSeparator();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				pdDocument.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void extractYear(String file) {
		int year;
		try {
			year = Integer.valueOf(file.substring(0, 4));
			if (Utils.isReasonablyPossilbeYear(year)) {
				this.year = year;
			}
		} catch (NumberFormatException e) {
		}
	}

	public int pages;

	public String content;

	public String nonRefContent;

	public String lineSeparator;

	public int year;

}
