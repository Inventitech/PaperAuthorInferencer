package nl.tudelft.serg.paperauthorinferencer;

import java.util.function.Function;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	/** Default values, overriden if specified */
	private static int maxAuthors = 4;
	private static float authorThreshold = 0.60f;
	private static boolean printPDFPath = false;

	/** Disable logging */
	static {
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
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
		ReferenceOccurrenceCounter occurenceCounter = new ReferenceOccurrenceCounter(
				paper, referenceLocator.references);
		occurenceCounter.countReferences();

		AuthorshipOrderer orderer = new AuthorshipOrderer(maxAuthors,
				authorThreshold);

		orderer.orderReferences(occurenceCounter.references);
		
		orderer.printTopAuthors();
		if (printPDFPath) {
			System.out.println();
		}
	}

	private static String analyzeCommandLineOptions(String[] args,
			String pdfDocument) {
		Options options = new Options();
		Option numberOfAuthorsOption = new Option("n", "number-of-authors",
				true, "The maximum number of authors that will be guessed.");
		options.addOption(numberOfAuthorsOption);
		Option thresholdOfAuthorsOption = new Option("t", "author-threshold",
				true,
				"Percentage of score of first author that are still suggested as co-authors.");
		options.addOption(thresholdOfAuthorsOption);
		Option printPDFPathOption = new Option("f", "print-file-name", false,
				"Prints the file that is currently analyzed.");
		options.addOption(printPDFPathOption);

		try {
			CommandLine line = new DefaultParser().parse(options, args);
			if (line.getArgList().isEmpty()) {
				System.out
						.println("Please supply the path to a PDF document as an argument.");
				System.exit(1);
			}
			pdfDocument = line.getArgList().get(0);

			maxAuthors = (int) setCmdOptionIfAvailable(numberOfAuthorsOption,
					maxAuthors, line, Integer::valueOf);
			authorThreshold = (float) setCmdOptionIfAvailable(
					thresholdOfAuthorsOption, authorThreshold, line,
					Float::valueOf);
			printPDFPath = line.hasOption(printPDFPathOption.getOpt());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pdfDocument;
	}

	private static Number setCmdOptionIfAvailable(Option option,
			Number defaultValue, CommandLine line,
			Function<String, Number> function) {
		String optionName = option.getOpt();
		if (line.hasOption(optionName)) {
			String numberOfAuthorsValue = line.getOptionValue(optionName,
					String.valueOf(defaultValue));
			try {
				return function.apply(numberOfAuthorsValue);
			} catch (NumberFormatException e) {
				// leave at default
			}
		}
		return defaultValue;
	}
}
