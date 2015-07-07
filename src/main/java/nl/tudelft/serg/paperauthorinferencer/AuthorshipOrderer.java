package nl.tudelft.serg.paperauthorinferencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AuthorshipOrderer {
	Map<String, Integer> authorsToFrequency = new HashMap<String, Integer>();
	Map<String, Author> authors = new HashMap<>();
	private int maxAuthors;
	private int authorThreshold;
	
	public AuthorshipOrderer(int maxAuthors, int authorThreshold) {
		this.maxAuthors = maxAuthors;
		this.authorThreshold = authorThreshold;
	}

	public class Author implements Comparable<Author> {
		String name;
		int papers;

		@Override
		public int compareTo(Author o) {
			return papers < o.papers ? -1 : (papers == o.papers) ? 0 : 1;
		}

		@Override
		public String toString() {
			return name + ": " + papers;
		}
	}

	public AuthorshipOrderer(Set<Reference> references) {
		references.forEach(new Consumer<Reference>() {

			@Override
			public void accept(Reference r) {
				r.authors.forEach(t -> {
					if (authors.containsKey(t)) {
						Author author = authors.get(t);
						author.papers += r.occurences;
						authors.put(t, author);
					} else {
						Author author = new Author();
						author.name = t;
						author.papers = r.occurences;
						authors.put(t, author);
					}
				});
			}
		});
	}

	public void printTopAuthors() {
		List<Author> authorEntries = new ArrayList<>(authors.values());
		authorEntries.forEach(t -> {
			maxAuthors = Math.max(maxAuthors, t.papers);
		});

		List<Author> authorGuess = authorEntries.stream().filter(a -> {
			return a.papers >= authorThreshold * maxAuthors;
		}).collect(Collectors.toList());
		Collections.sort(authorGuess, Collections.reverseOrder());
		authorGuess = authorGuess.stream().limit(maxAuthors)
				.collect(Collectors.toList());
		System.out.println(authorGuess);
	}
}
