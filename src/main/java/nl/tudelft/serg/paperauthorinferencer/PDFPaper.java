package nl.tudelft.serg.paperauthorinferencer;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFPaper {

	public PDFPaper(String filename) {
		PDDocument pdfFile = null;
		try {
			pdfFile = PDDocument.load(filename, true);
			PDFTextStripper textStripper = new PDFTextStripper();

			if (!pdfFile.isEncrypted()) {
				pages = pdfFile.getNumberOfPages();
				content = textStripper.getText(pdfFile);
				lineSeparator = textStripper.getLineSeparator();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				pdfFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public int pages;

	public String content;

	public String nonRefContent;

	public String lineSeparator;

}
