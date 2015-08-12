package nl.tudelft.serg.paperauthorinferencer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	/** Default values, overridden if specified */
	private static boolean printPDFPath = false;
	private static boolean extractEmails;

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

		PDFPaper paper = new PDFPaper(pdfDocument);
		ReferenceListBuilder referenceLocator = new ReferenceListBuilder(paper);
		referenceLocator.locateReferences();

		if (extractEmails) {
			paper.extractEMailAddresses();
			paper.authors.forEach(a -> System.out
					.println(paper.year + "," + paper.title + "," + a.firstName + " " + a.lastName + "," + a.eMail));
			paper.unmatchedEMails.forEach(a -> System.out.println(paper.year + "," + paper.title + "," + "," + a));
		} else {
			ReferenceOccurrenceCounter occurenceCounter = new ReferenceOccurrenceCounter(paper,
					referenceLocator.references);
			occurenceCounter.countReferences();

			ReferenceScoreBuilder referenceScoreBuilder = new ReferenceScoreBuilder(occurenceCounter.references);
			referenceScoreBuilder.normalize();

			AuthorshipOrderer orderer = new AuthorshipOrderer(paper);
			orderer.buildAuthorList(occurenceCounter.references);

			orderer.printAuthors();
			if (printPDFPath) {
				System.out.println();
			}
		}
	}

	private static String analyzeCommandLineOptions(String[] args, String pdfDocument) {
		Options options = new Options();
		Option printPDFPathOption = new Option("f", "print-file-name", false,
				"Prints the file that is currently analyzed.");
		options.addOption(printPDFPathOption);

		Option extractEMailsOption = new Option("e", "extract-email-addresses", false, "Extracts the email addresses.");
		options.addOption(extractEMailsOption);

		try {
			CommandLine line = new DefaultParser().parse(options, args);
			if (line.getArgList().isEmpty()) {
				System.out.println("Please supply the path to a PDF document as an argument.");
				System.exit(1);
			}
			pdfDocument = line.getArgList().get(0);

			printPDFPath = line.hasOption(printPDFPathOption.getOpt());
			extractEmails = line.hasOption(extractEMailsOption.getOpt());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pdfDocument;
	}
}
