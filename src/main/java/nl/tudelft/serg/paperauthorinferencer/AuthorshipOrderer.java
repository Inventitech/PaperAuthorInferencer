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
	PDFPaper paper;
	
	public AuthorshipOrderer(PDFPaper paper) {
		this.paper = paper;
	}

	public class Author implements Comparable<Author> {
		String name;
		double referenceEntries = 0;
		double occurenceRatio = 0;
		int eldestRefDelta;
		int newestRef;
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

	public void buildAuthorList(Set<Reference> references) {
		references.forEach(new Consumer<Reference>() {

			@Override
			public void accept(Reference r) {
				r.authors.forEach(t -> {
					Author author;
					if (authors.containsKey(t)) {
						author = authors.get(t);
					} else {
						author = new Author();
					}
					updateAuthorEntry(r, t, author);
					authors.put(t, author);
				});
			}

			private void updateAuthorEntry(Reference r, String t, Author author) {
				author.name = t;
				author.occurenceRatio += r.occurenceRatio;
				author.referenceEntries += 1;
				int yearDiff = paper.year - r.year;
				if (yearDiff < author.eldestRefDelta) {
					author.eldestRefDelta = yearDiff;
				}
				if (yearDiff > author.newestRef) {
					author.newestRef = yearDiff;
				}
			}
		});
	}
	
	public void printAuthors() {
		try {
			List<Author> authorEntries = new ArrayList<>(authors.values());

			authorEntries.stream().sorted(Collections.reverseOrder()).forEach(t -> {
				System.out.println(t.name + ", ");
			});
		} catch (NoSuchElementException e) {
			System.out.print("PDF not analyzable.");
		}
	}
}
