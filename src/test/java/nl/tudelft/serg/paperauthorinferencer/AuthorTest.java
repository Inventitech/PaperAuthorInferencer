package nl.tudelft.serg.paperauthorinferencer;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthorTest {

	@Test
	public void testStandardName() {
		Author author = new Author("Moritz Beller");
		assertEquals("Moritz", author.firstName);
		assertEquals("Beller", author.lastName);
	}

	@Test
	public void testComplicatedName() {
		Author author = new Author("Arie van Deursen");
		assertEquals("Arie", author.firstName);
		assertEquals("van", author.middleName);
		assertEquals("Deursen", author.lastName);
	}

	@Test
	public void testVeryCompilcatedName() {
		Author author = new Author("Mats P. E. Heimdahl");
		assertEquals("Mats", author.firstName);
		assertEquals("P. E.", author.middleName);
		assertEquals("Heimdahl", author.lastName);
	}

	@Test
	public void testOnlyFirstName() {
		Author author = new Author("Lucia");
		assertEquals("Lucia", author.firstName);
	}

	@Test
	public void testSameAuthor() {
		Author author = new Author("Moritz Beller");
		assertEquals(true, author.equals(author));
	}

	@Test
	public void testAbbreviationAuthor() {
		Author authorLong = new Author("Moritz Beller");
		Author author = new Author("M. Beller");
		assertEquals(true, author.equals(authorLong));
		assertEquals(true, authorLong.equals(author));
	}

	@Test
	public void testAbbreviationInprecisionAuthor() {
		Author authorLong = new Author("Moritz M. Beller");
		Author author = new Author("M. Beller");
		assertEquals(true, author.equals(authorLong));
		assertEquals(true, authorLong.equals(author));
	}

	@Test
	public void testNotTheSameGuy() {
		Author authorLong = new Author("Moritz Beller");
		Author author = new Author("N. Beller");
		assertEquals(false, author.equals(authorLong));
		assertEquals(false, authorLong.equals(author));
	}

}
