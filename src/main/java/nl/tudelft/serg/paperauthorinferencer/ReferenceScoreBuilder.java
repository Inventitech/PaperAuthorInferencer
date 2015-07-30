package nl.tudelft.serg.paperauthorinferencer;

import java.util.Set;

public class ReferenceScoreBuilder {

	Set<Reference> references;

	public ReferenceScoreBuilder(Set<Reference> references) {
		this.references = references;
	}

	public void normalize() {
		int total = references.stream().mapToInt(r -> r.occurences).sum();

		references.stream().forEach(r -> {
			r.occurenceRatio = (double) r.occurences / total;
			r.referenceEntriesRatio = (double) 1 / references.size();
		});
	}
}
