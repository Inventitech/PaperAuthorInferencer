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

	public void buildAuthorList(Set<Reference> references) {
		references.forEach(new Consumer<Reference>() {

			@Override
			public void accept(Reference r) {
				r.authors.forEach(t -> {
					Author author;
					if (authors.containsKey(t)) {
						author = authors.get(t);
					} else {
						author = new Author(t);
					}
					updateAuthorEntry(r, t, author);
					authors.put(t, author);
				});
			}

			private void updateAuthorEntry(Reference r, String t, Author author) {
				author.occurenceRatio += r.occurenceRatio;
				author.referenceEntriesRatio += r.referenceEntriesRatio;
				int yearDiff = paper.year - r.year;
				if (yearDiff < author.eldestRefDelta) {
					author.eldestRefDelta = yearDiff;
				}
				if (yearDiff > author.newestRefDelta) {
					author.newestRefDelta = yearDiff;
				}
			}
		});
	}

	public void printAuthors() {
		try {
			List<Author> authorEntries = new ArrayList<>(authors.values());
			// String realAuthors = paper.authors.stream().reduce("", (a, b) ->
			// b += ";" + a);

			authorEntries.stream().sorted(Collections.reverseOrder()).forEach(a -> {
				System.out.println(a.fullName + ", " + a.occurenceRatio + ", " + a.referenceEntriesRatio + ", "
						+ a.eldestRefDelta + ", " + a.newestRefDelta + ", " + isRealAuthor(a));
			});
		} catch (NoSuchElementException e) {
			System.out.print("PDF not analyzable.");
		}
	}

	private boolean isRealAuthor(Author author) {
		boolean isCorrectAuthor = false;
		isCorrectAuthor = paper.authors.contains(author.fullName);
		return isCorrectAuthor;
	}
}
