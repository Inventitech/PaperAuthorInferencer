package nl.tudelft.serg.paperauthorinferencer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ReferenceOccurrenceCounter {

	public ReferenceOccurrenceCounter(PDFPaper paper, Set<Reference> references) {
		this.paper = paper;
		this.references = references;
	}

	public PDFPaper paper;

	public Set<Reference> references;

	public void countReferences() {
		countSimpleReferences();
		countMultipleReferences();
	}

	private void countMultipleReferences() {
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(paper.nonRefContent);
		while (matcher.find()) {
			String group = matcher.group(1);
			if (group.contains(", ")) {
				List<String> subRefs = Arrays.asList(group.split(", "));

				addOccurrences(subRefs);
			} else if (group.contains("-")) {
				List<String> subRefs = new ArrayList <String> (Arrays
						.asList(group.split("(-|–)")));

				if (subRefs.size() != 2) {
					continue;
				}
				int from = Integer.valueOf(subRefs.get(0));
				int to = Integer.valueOf(subRefs.get(1));
				for (int i = from + 1; i < to; i++) {
					subRefs.add(String.valueOf(i));
				}

				addOccurrences(subRefs);
			}
		}
	}

	private void addOccurrences(List<String> subRefs) {
		references.forEach(r -> {
			String strippedIdentifier = stripIdentifier(r);
			if (subRefs.contains(strippedIdentifier)) {
				r.occurences += 1;
			}
		});
	}

	private String stripIdentifier(Reference r) {
		String strippedIdentifier = r.identifier.replace("[", "");
		strippedIdentifier = strippedIdentifier.replace("]", "");
		return strippedIdentifier;

	}

	private void countSimpleReferences() {
		references.forEach(r -> {
			r.occurences = StringUtils
					.countMatches(paper.content, r.identifier);
		});
	}
}
