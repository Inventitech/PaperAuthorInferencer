package nl.tudelft.serg.paperauthorinferencer;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	/** Default values, overridden if specified */
	private static boolean printPDFPath = false;

	/** Disable logging */
	static {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}

	public static void main(String[] args) {
		String pdfDocument = "";

		pdfDocument = analyzeCommandLineOptions(args, pdfDocument);
		if (printPDFPath) {
			System.out.println(pdfDocument);
		}

		File file = new File(pdfDocument);

		PDFPaper paper = new PDFPaper(pdfDocument);

		ReferenceListBuilder referenceLocator = new ReferenceListBuilder(paper);
		referenceLocator.locateReferences();
		ReferenceOccurrenceCounter occurenceCounter = new ReferenceOccurrenceCounter(paper,
				referenceLocator.references);
		occurenceCounter.countReferences();

		ReferenceScoreBuilder referenceScoreBuilder = new ReferenceScoreBuilder(paper, occurenceCounter.references);
		referenceScoreBuilder.normalize();

		AuthorshipOrderer orderer = new AuthorshipOrderer(paper);
		orderer.buildAuthorList(occurenceCounter.references);

		orderer.printAuthors();
		if (printPDFPath) {
			System.out.println();
		}
	}

	private static String analyzeCommandLineOptions(String[] args, String pdfDocument) {
		Options options = new Options();
		Option printPDFPathOption = new Option("f", "print-file-name", false,
				"Prints the file that is currently analyzed.");
		options.addOption(printPDFPathOption);

		try {
			CommandLine line = new DefaultParser().parse(options, args);
			if (line.getArgList().isEmpty()) {
				System.out.println("Please supply the path to a PDF document as an argument.");
				System.exit(1);
			}
			pdfDocument = line.getArgList().get(0);

			printPDFPath = line.hasOption(printPDFPathOption.getOpt());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pdfDocument;
	}
}
