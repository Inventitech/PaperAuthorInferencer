package main;

import java.util.HashSet;
import java.util.Set;

public class Reference {
	public Reference(String identifier) {
		this.identifier = identifier;
	}

	public String identifier;
	public Set<String> authors = new HashSet<String>();
	public int referreed;

	@Override
	public String toString() {
		return identifier + ": " + authors.stream().reduce("", (a, b) -> a + b+ ", " )
				+ "\n";
	}
}