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

	public static void main(String[] args) {
		String pdfDocument = "";

		Options options = new Options();
		Option numberOfAuthors = new Option("n", "number-of-authors", true,
				"The maximum number of authors that will be guessed.");
		options.addOption(numberOfAuthors);
		Option thresholdOfAuthors = new Option("t", "author-threshold", true,
				"Percentage of score of first author that are still suggested as co-authors.");
		options.addOption(thresholdOfAuthors);

		try {
			CommandLine line = new DefaultParser().parse(options, args);
			if (line.getArgList().isEmpty()) {
				System.out
						.println("Please supply the path to a PDF document as an argument.");
				System.exit(1);
			}
			pdfDocument = line.getArgList().get(0);

			maxAuthors = (int) setCmdOptionIfAvailable(numberOfAuthors,
					maxAuthors, line, Integer::valueOf);
			authorThreshold = (float) setCmdOptionIfAvailable(
					thresholdOfAuthors, authorThreshold, line, Float::valueOf);
		} catch (ParseException e) {
			e.printStackTrace();
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
