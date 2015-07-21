package nl.tudelft.serg.paperauthorinferencer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class ReferenceListBuilder {
	private PDFPaper pdfPaper;
	public Set<Reference> references = new HashSet<Reference>();

	private static String period = ".";
	private static String comma = ", ";
	private static String and = "and ";
	private static String commaAnd = comma + and;

	private static List<String> splitters = Arrays.asList(commaAnd, comma, and)
			.stream().map(a -> Pattern.quote(a)).collect(Collectors.toList());

	public ReferenceListBuilder(PDFPaper pdfPaper) {
		this.pdfPaper = pdfPaper;
	}

	enum ReferenceFindingState {
		NONE, NEW_REF_TAG, REF_TAG_FOUND
	}

	/**
	 * Tries to auto-locate the references from a given paper, by trying to
	 * establish where the Reference section begins and from then on searching
	 * for entries embedded in [] braces.
	 */
	public void locateReferences() {
		String[] individualLines = pdfPaper.content
				.split(pdfPaper.lineSeparator);
		StringBuilder nonRefContentBuilder = new StringBuilder();

		String curReference = "";

		ReferenceFindingState state = ReferenceFindingState.NONE;
		Pattern pattern = compileReferencePattern();
		for (String line : individualLines) {
			line = line.trim();
			Matcher matcher = pattern.matcher(line);
			boolean readReference = matcher.matches();
			switch (state) {
			case NEW_REF_TAG:
				if (!readReference) {
					break;
				}

				curReference += line;
				state = ReferenceFindingState.REF_TAG_FOUND;
				break;

			case REF_TAG_FOUND:
				if (readReference) {
					references.add(createReferenceEntry(curReference));
					curReference = line;
					break;
				}

				curReference += line;
				break;
			case NONE:
			default:
				if (line.matches("(?i)^(\\d. )*(references|literature)$")) {
					state = ReferenceFindingState.NEW_REF_TAG;
				} else {
					nonRefContentBuilder.append(line).append(
							pdfPaper.lineSeparator);
				}
			}
		}

		pdfPaper.nonRefContent = nonRefContentBuilder.toString();
	}

	private static Pattern compileReferencePattern() {
		return Pattern.compile("^(\\[\\d+\\]).*");
	}

	public static Reference createReferenceEntry(String referenceEntry) {
		Matcher matcher = compileReferencePattern().matcher(referenceEntry);
		matcher.matches();
		String extractedReference = matcher.group(1);
		Reference reference = new Reference(extractedReference);
		referenceEntry = referenceEntry.replaceFirst(
				Pattern.quote(extractedReference), "");

		addAuthors(referenceEntry, 0, reference);
		return reference;
	}

	static void addAuthors(String referenceEntry, Reference reference) {
		String authors = extractAuthors(referenceEntry);
		addAuthors(authors, 0, reference);
	}

	private static String extractAuthors(String referenceEntry) {
		referenceEntry = referenceEntry.trim();

		Pattern pattern = Pattern.compile("(.*?[a-z])\\.");
		Matcher matcher = pattern.matcher(referenceEntry);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";

	}

	/** Recursively adds authors until there are no more authors left. */
	static void addAuthors(String authors, int splitterPos, Reference reference) {
		authors = authors.trim();
		String splitter = splitters.get(splitterPos);

		Pattern pattern = Pattern.compile(splitter);
		Matcher matcher = pattern.matcher(authors);

		if (matcher.find()) {
			String[] groups = authors.split(splitter);
			for (String group : groups) {
				addAuthors(group, splitterPos, reference);
			}
			return;
		}
	
		if (splitterPos + 1 < splitters.size()) {
			addAuthors(authors, splitterPos + 1, reference);
		} else {
			addSingleAuthor(authors, reference);
		}
	}

	private static void addSingleAuthor(String author, Reference reference) {
		reference.authors.add(StringUtils.removeEnd(author.trim(), period));
	}
}
