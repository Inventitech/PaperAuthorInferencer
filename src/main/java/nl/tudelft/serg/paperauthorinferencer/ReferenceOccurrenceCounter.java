package nl.tudelft.serg.paperauthorinferencer;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ReferenceOccurrenceCounter {

	public ReferenceOccurrenceCounter(PDFPaper paper, Set<Reference> references) {
		this.paper = paper;
		this.references = references;
	}

	public PDFPaper paper;

	public Set<Reference> references;

	public void countReferences() {
		references.forEach(r -> {
			r.occurences = StringUtils
					.countMatches(paper.content, r.identifier);
		});
	}
}
