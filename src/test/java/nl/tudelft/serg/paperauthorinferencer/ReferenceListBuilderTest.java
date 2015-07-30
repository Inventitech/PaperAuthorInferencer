package nl.tudelft.serg.paperauthorinferencer;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ReferenceListBuilderTest {

	@Test
	public void singleSimpleAuthor() {
		String refEntry = "M. Aversano. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("M. Aversano", ref.authors.toArray()[0]);
	}
	
	@Test
	public void singleComplicatedAuthor() {
		String refEntry = "Max Di Penta. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("Max Di Penta", ref.authors.toArray()[0]);
	}

	@Test
	public void singleFullFirstAuthorName() {
		String refEntry = "Martin Aversano. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("Martin Aversano", ref.authors.toArray()[0]);
	}
	

	@Test
	public void singleFullFirstAuthorDobuleName() {
		String refEntry = "Emmerson Murphy-Hill. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("Emmerson Murphy-Hill", ref.authors.toArray()[0]);
	}

	@Test
	public void singleFullAuthorName() {
		String refEntry = "Martin Robillard Aversano. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("Martin Robillard Aversano", ref.authors.toArray()[0]);
	}

	@Test
	public void singleAbbreviatedAuthorName() {
		String refEntry = "Martin R. Aversano. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("Martin R. Aversano", ref.authors.toArray()[0]);
	}

	/**
	 * This is supposed not to work, otherwise how can we identify the end of
	 * the author list?
	 */
	public void singleNormalAbbreviatedAuthorName() {
		String refEntry = "M. R. Aversano. How clones are maintained: An empirical study. In Proc. CSMR ’07. IEEE, 2007.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		assertEquals("M. R. Aversano", ref.authors.toArray()[0]);
	}

	@Test
	public void multipleNormalAuthors() {
		String refEntry = "I. Baxter, A. Yahin, L. Moura, M. SantAnna, and L. Bier. Clone detection using abstract syntax trees. In Proc. ICSM’98. IEEE, 1998.";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		Set<String> authors = new HashSet<String>();
		authors.add("L. Moura");
		authors.add("M. SantAnna");
		authors.add("I. Baxter");
		authors.add("L. Bier");
		authors.add("A. Yahin");

		assertEquals(authors, ref.authors);
	}

	@Test
	public void commaSeparatedAuhtorlist() {
		String refEntry = "J. Anvik, L. Hiew, and G. C. Murphy, “Who should fix this bug?” in Proceedings of the 28th International Conference on Software Engineering, ser. ICSE ’06. New York, NY, USA: ACM, 2006, pp. 361–370. [Online].";
		Reference ref = new Reference("[1]");
		ReferenceListBuilder.addAuthors(refEntry, ref);
		Set<String> authors = new HashSet<String>();
		authors.add("J. Anvik");
		authors.add("L. Hiew");
		authors.add("G. C. Murphy");

		assertEquals(authors, ref.authors);
	}

}
