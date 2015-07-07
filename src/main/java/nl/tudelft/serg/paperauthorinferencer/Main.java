package nl.tudelft.serg.paperauthorinferencer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {
		String pdfDocument = "";
		Options options = new Options();
		Option numberOfAuthors = new Option("n", "number-of-authors", true,
				"The maximum number of authors that will be guessed.");
		options.addOption(numberOfAuthors);
		Option thresholdOfAuthors = new Option("t", "author-threshold", true,
				"Percentage of score of first author that are still suggested as co-authors.");
		options.addOption(thresholdOfAuthors);
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.getArgList().isEmpty()) {
				System.out
						.println("Please supply a PDF document to analyze as an argument.");
				System.exit(1);
			}
			pdfDocument = line.getArgList().get(0);
			
			if(line.hasOption(numberOfAuthors.getArgName())) {
				
			}
			if(line.hasOption(thresholdOfAuthors.getArgName())) {
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("Reading " + pdfDocument);

		PDFPaper paper = new PDFPaper(pdfDocument);
		ReferenceListBuilder referenceLocator = new ReferenceListBuilder(paper);
		referenceLocator.locateReferences();
		ReferenceOccurrenceCounter occurenceCounter = new ReferenceOccurrenceCounter(
				paper, referenceLocator.references);
		occurenceCounter.countReferences();

		new AuthorshipOrderer(occurenceCounter.references).printTopAuthors();
	}
}
