package nl.tudelft.serg.paperauthorinferencer;

import java.util.HashSet;
import java.util.Set;

public class Reference {
	public Reference(String identifier) {
		this.identifier = identifier;
	}

	public String identifier;
	public Set<String> authors = new HashSet<String>();
	public int occurences = 0;
	public int year = Utils.currentYear;
	public double occurrenceRatio = 0;
	public double referenceEntriesRatio = 0;
	public double firstOccurrenceRatio = 1;

	public void updateFirstOccurrenceRatio(double firstOccurrenceRatio) {
		this.firstOccurrenceRatio = (this.firstOccurrenceRatio < firstOccurrenceRatio ? this.firstOccurrenceRatio
				: firstOccurrenceRatio);
	}

	@Override
	public String toString() {
		return identifier + ": " + authors.stream().reduce("", (a, b) -> a + b + ", ") + " " + year + "\n";
	}
}