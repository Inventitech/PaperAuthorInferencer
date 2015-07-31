package nl.tudelft.serg.paperauthorinferencer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ReferenceListBuilder {
	private PDFPaper pdfPaper;
	public Set<Reference> references = new HashSet<Reference>();

	private final static String period = ".";
	private final static String comma = ", ";
	private final static String and = "and ";
	private final static String commaAnd = comma + and;

	private final static Pattern REF_PATTERN = Pattern.compile("^(\\[\\d+\\]).*");

	private final static String AUTHOR_LENIENT_REG_EX = "^((([^.:,]*? ){0,3}[^.:,]{2,}?)(" + comma + "|" + commaAnd
			+ "| " + and + Pattern.quote(period) + "))+";
	private final static Pattern AUTHOR_LENIENT_PATTERN = Pattern.compile(AUTHOR_LENIENT_REG_EX);

	private final static String AUTHOR_REG_EX = "^(((\\p{Lu}\\. )|(\\p{L}+[\\p{L}ǎ´'`’-]*?\\p{Ll} )){1,3}\\p{Lu}[\\p{L}ǎ´'`’-]*\\p{Ll}).*";
	private final static Pattern AUTHOR_PATTERN = Pattern.compile(AUTHOR_REG_EX);

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
		String[] individualLines = pdfPaper.content.split(pdfPaper.lineSeparator);
		StringBuilder nonRefContentBuilder = new StringBuilder();

		String curReference = "";

		ReferenceFindingState state = ReferenceFindingState.NONE;
		for (String line : individualLines) {
			line = line.trim();
			Matcher matcher = REF_PATTERN.matcher(line);
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
					Reference reference = createReferenceEntry(curReference);
					if (reference != null) {
						references.add(reference);
					}
					curReference = line;
					break;
				}

				curReference += " " + line;
				break;
			case NONE:
			default:
				if (line.matches("(?i)^(\\d. )*(references|literature)$")) {
					state = ReferenceFindingState.NEW_REF_TAG;
				} else {
					nonRefContentBuilder.append(line).append(pdfPaper.lineSeparator);
				}
			}
		}

		pdfPaper.nonRefContent = nonRefContentBuilder.toString();
	}

	private Reference createReferenceEntry(String referenceEntry) {
		Matcher matcher = REF_PATTERN.matcher(referenceEntry);
		if (matcher.matches()) {
			String extractedReference = matcher.group(1);
			Reference reference = new Reference(extractedReference);
			referenceEntry = referenceEntry.replaceFirst(Pattern.quote(extractedReference), "");

			addAuthors(referenceEntry, reference);
			addYear(referenceEntry, reference);
			return reference;
		}
		return null;
	}

	static void addAuthors(String referenceEntry, Reference reference) {
		extractAuthors(referenceEntry).forEach(a -> reference.authors.add(a));
	}

	private static Set<String> extractAuthors(String referenceEntry) {
		Set<String> authors = new HashSet<>();
		extractAuthors(referenceEntry, authors);
		return authors;
	}

	static void extractAuthors(String referenceEntry, Set<String> authors) {
		extractAuthors(referenceEntry, authors, 0);
	}

	/** Recursively adds authors until there are no more authors left. */
	static void extractAuthors(String referenceEntry, Set<String> authors, int recursionDepth) {
		if (recursionDepth > 15) {
			// we only support a maximum of 15 authors per reference entry.
			return;
		}
		if (StringUtils.isEmpty(referenceEntry) || StringUtils.isBlank(referenceEntry)) {
			return;
		}

		referenceEntry = referenceEntry.trim();
		// evil fix against double -
		referenceEntry = referenceEntry.replace("--", "  ");

		boolean lastEntry = false;
		if (referenceEntry.startsWith(period))
			return;
		else if (referenceEntry.startsWith(commaAnd)) {
			referenceEntry = referenceEntry.replaceFirst(commaAnd, "");
			lastEntry = true;
		} else if (referenceEntry.startsWith(and)) {
			referenceEntry = referenceEntry.replaceFirst(and, "");
			lastEntry = true;
		} else if (referenceEntry.startsWith(comma)) {
			referenceEntry = referenceEntry.replaceFirst(comma, "");
		}

		Matcher lenientMatcher = AUTHOR_LENIENT_PATTERN.matcher(referenceEntry);
		Matcher authorMatcher = AUTHOR_PATTERN.matcher(referenceEntry);
		boolean authorMatcherFound = authorMatcher.find();
		boolean lenientMatcherFound = lenientMatcher.find();
		String author;
		if (authorMatcherFound) {
			author = authorMatcher.group(1);
		} else if (!authorMatcherFound && lenientMatcherFound) {
			author = lenientMatcher.group(2);
		} else {
			return;
		}
		authors.add(author);

		referenceEntry = referenceEntry.replaceFirst(Pattern.quote(author), "");
		if (!lastEntry) {
			extractAuthors(referenceEntry, authors, recursionDepth++);
		}
	}

	private void addYear(String referenceEntry, Reference reference) {
		List<Integer> foundPossibleYears = new ArrayList<Integer>();

		String authorRegEx = "\\D(\\d{4,4})\\D";
		Pattern pattern = Pattern.compile(authorRegEx);
		Matcher matcher = pattern.matcher(referenceEntry);
		while (matcher.find()) {
			int year = Integer.valueOf(matcher.group(1));
			if (Utils.isReasonablyPossilbeYear(year)) {
				foundPossibleYears.add(year);
			}
		}

		if (!foundPossibleYears.isEmpty()) {
			reference.year = foundPossibleYears.get(foundPossibleYears.size() - 1);
		}
	}
}
