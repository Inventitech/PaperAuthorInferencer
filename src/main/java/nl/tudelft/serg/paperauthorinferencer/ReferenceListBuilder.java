package nl.tudelft.serg.paperauthorinferencer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceListBuilder {
	private PDFPaper pdfPaper;
	public Set<Reference> references = new HashSet<Reference>();

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

	private Pattern compileReferencePattern() {
		return Pattern.compile("^(\\[\\d+\\]).*");
	}

	private Reference createReferenceEntry(String referenceEntry) {
		Matcher matcher = compileReferencePattern().matcher(referenceEntry);
		matcher.matches();
		String extractedReference = matcher.group(1);
		Reference reference = new Reference(extractedReference);
		referenceEntry = referenceEntry.replaceFirst(
				Pattern.quote(extractedReference), "");

		addAuthors(referenceEntry, reference);
		addYear(referenceEntry, reference);
		return reference;
	}

	/** Recursively adds authors until there are no more authors left. */
	static void addAuthors(String referenceEntry, Reference reference) {
		String period = ".";
		String comma = ", ";
		String and = "and ";
		String commaAnd = comma + and;

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

		String authorRegEx = "^(((\\p{Lu}\\. )|([\\p{L}]+\\p{Ll} )){1,2}\\p{Lu}[\\p{L}’-]{1,}\\p{Ll}).*";
		Pattern pattern = Pattern.compile(authorRegEx);
		Matcher matcher = pattern.matcher(referenceEntry);
		if (!matcher.find()) {
			return;
		}
		String author = matcher.group(1);
		reference.authors.add(author);

		referenceEntry = referenceEntry.replaceFirst(author, "");
		if (!lastEntry) {
			addAuthors(referenceEntry, reference);
		}
	}

	private void addYear(String referenceEntry, Reference reference) {
		int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
		List<Integer> foundPossibleYears = new ArrayList<Integer>();

		String authorRegEx = "\\D(\\d{4,4})\\D";
		Pattern pattern = Pattern.compile(authorRegEx);
		Matcher matcher = pattern.matcher(referenceEntry);
		while (matcher.find()) {
			int year = Integer.valueOf(matcher.group(1));
			if (year > 1800 && year <= nextYear) {
				foundPossibleYears.add(year);
			}
		}

		if (foundPossibleYears.isEmpty()) {
			System.out.println("No year: " + referenceEntry);
			return;
		}

		reference.year = foundPossibleYears.get(foundPossibleYears.size() - 1);
		System.out.println("Year " + reference.year + " : " + referenceEntry);
	}
}
