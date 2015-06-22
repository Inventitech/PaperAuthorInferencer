package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AuthorshipOrderer {
	Map<String, Integer> authorsToFrequency = new HashMap<String, Integer>();
	Map<String, Author> authors = new HashMap<>();
	private int max;

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
						author.papers += 1;
						authors.put(t, author);
					} else {
						Author author = new Author();
						author.name = t;
						author.papers = 1;
						authors.put(t, author);
					}
				});
			}
		});
	}

	public void printTopAuthors() {
		double authorThreshold = 0.65;
		int maxAuthors = 5;
		List<Author> authorEntries = new ArrayList<>(authors.values());
		authorEntries.forEach(t -> {
			max = Math.max(max, t.papers);
		});

		List<Author> authorGuess = authorEntries.stream().filter(a -> {
			return a.papers >= authorThreshold * max;
		}).collect(Collectors.toList());
		Collections.sort(authorGuess, Collections.reverseOrder());
		authorGuess = authorGuess.stream().limit(maxAuthors)
				.collect(Collectors.toList());
		System.out.println(authorGuess);
	}

	public static void main(String[] args) {
		System.out.println("Reading " + args[0]);

		PDFPaper paper = new PDFPaper(args[0]);
		ReferenceLocator referenceLocator = new ReferenceLocator(paper);
		referenceLocator.locateReferences();
		new AuthorshipOrderer(referenceLocator.references).printTopAuthors();
	}
}
