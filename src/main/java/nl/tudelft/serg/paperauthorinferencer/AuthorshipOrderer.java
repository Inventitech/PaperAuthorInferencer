package nl.tudelft.serg.paperauthorinferencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

public class AuthorshipOrderer {
	Map<String, Integer> authorsToFrequency = new HashMap<String, Integer>();
	Map<String, Author> authors = new HashMap<>();
	private int maxAuthors;
	private int highestScore;
	private float authorThreshold;

	public AuthorshipOrderer(int maxAuthors, float authorThreshold) {
		this.maxAuthors = maxAuthors;
		this.authorThreshold = authorThreshold;
	}

	public class Author implements Comparable<Author> {
		String name;
		int score;

		@Override
		public int compareTo(Author o) {
			return score < o.score ? -1 : (score == o.score) ? 0 : 1;
		}

		@Override
		public String toString() {
			return name + ": " + score;
		}
	}

	public void orderReferences(Set<Reference> references) {
		references.forEach(new Consumer<Reference>() {

			@Override
			public void accept(Reference r) {
				r.authors.forEach(t -> {
					if (authors.containsKey(t)) {
						Author author = authors.get(t);
						author.score += r.occurences;
						authors.put(t, author);
					} else {
						Author author = new Author();
						author.name = t;
						author.score = r.occurences;
						authors.put(t, author);
					}
				});
			}
		});
	}

	public void printTopAuthors() {
		try {
			List<Author> authorEntries = new ArrayList<>(authors.values());
			highestScore = authorEntries.stream().map(t -> t.score)
					.max(Integer::compare).get();

			authorEntries.stream()
					.filter(a -> a.score >= authorThreshold * highestScore)
					.sorted(Collections.reverseOrder()).limit(maxAuthors)
					.forEach(t -> System.out.print(t + "; "));
		} catch (NoSuchElementException e) {
			System.out.print("PDF not analyzable.");
		}
	}
}
