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
	public double occurenceRatio = 0;
	double referenceEntriesRatio = 0;

	@Override
	public String toString() {
		return identifier + ": "
				+ authors.stream().reduce("", (a, b) -> a + b + ", ") + " " + year + "\n";
	}
}