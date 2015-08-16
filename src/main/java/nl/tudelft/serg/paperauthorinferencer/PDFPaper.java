package nl.tudelft.serg.paperauthorinferencer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFPaper {

	public int pages;

	public String content;

	public String nonRefContent;

	public String lineSeparator;

	public int year;

	public Set<Author> authors = new HashSet<Author>();

	public Set<String> unmatchedEMails = new HashSet<String>();

	public String filename;

	public String title;

	public PDFPaper(String filename) {
		this.filename = filename;
		extractYear(filename);
		PDDocument pdfDocument = null;
		try {
			pdfDocument = PDDocument.load(filename, true);
			title = pdfDocument.getDocumentInformation().getTitle().replaceAll("<.*?>", "");

			Set<String> authorNames = new HashSet<String>();
			ReferenceListBuilder.extractAuthors(makeASCIILike(pdfDocument.getDocumentInformation().getAuthor() + "."),
					authorNames);
			authorNames.forEach(a -> authors.add(new Author(a)));
			PDFTextStripper textStripper = new PDFTextStripper("UTF-8");
			if (!pdfDocument.isEncrypted()) {
				pages = pdfDocument.getNumberOfPages();
				content = textStripper.getText(pdfDocument);
				fixGermanUmlauts();
				lineSeparator = textStripper.getLineSeparator();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				pdfDocument.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void extractYear(String filename) {
		File file = new File(filename);
		int year;
		try {
			year = Integer.valueOf(file.getName().substring(0, 4));
			if (Utils.isReasonablyPossilbeYear(year)) {
				this.year = year;
			}
		} catch (NumberFormatException e) {
			System.err.println("No year in PDF supplied! Using current year as year of publication!");
			this.year = Utils.currentYear;
		}
	}

	public static String makeASCIILike(String string) {
		return string.replaceAll("[^\\p{L}ǎ´'`’,. -]", "");
	}

	private void fixGermanUmlauts() {
		Map<String, String> replaceGlyphons = new HashMap<String, String>();
		replaceGlyphons.put("A¨", "Ä");
		replaceGlyphons.put("O¨", "Ö");
		replaceGlyphons.put("U¨", "Ü");
		replaceGlyphons.put("a¨", "ä");
		replaceGlyphons.put("u¨", "ü");
		replaceGlyphons.put("o¨", "ö");

		replaceGlyphons.entrySet().forEach(e -> replace(e.getKey(), e.getValue()));
	}

	private void replace(String string, String replace) {
		content = content.replaceAll(string, replace);
	}

	public void extractEMailAddresses() {
		Set<String> emailAddresses = new HashSet<String>();
		emailAddresses.addAll(extractPlainEMailAddresses());
		emailAddresses.addAll(extractEnclosedEMailAddresses());
		emailAddresses.forEach(e -> {
			try {
				Author matchedAuthor = authors.stream().filter(a -> e.toLowerCase().contains(a.lastName.toLowerCase()))
						.findFirst().get();
				matchedAuthor.eMail = e;
			} catch (Exception exception) {
				unmatchedEMails.add(e);
			}
		});

	}

	private Set<String> extractPlainEMailAddresses() {
		Set<String> emailAddresses = new HashSet<String>();
		Pattern pattern = Pattern.compile("[^ }\\]]+@[^ ]{3,}");
		Matcher matcher = pattern.matcher(nonRefContent);
		while (matcher.find()) {
			String potentialEMail = matcher.group();
			validateAndAddEmailAddress(emailAddresses, potentialEMail);
		}
		return emailAddresses;
	}

	private void validateAndAddEmailAddress(Set<String> emailAddresses, String potentialEMail) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		if (emailValidator.isValid(potentialEMail)) {
			emailAddresses.add(potentialEMail);
		}
	}

	private Set<String> extractEnclosedEMailAddresses() {
		Set<String> emailAddresses = new HashSet<String>();
		Pattern pattern = Pattern.compile("[{\\[]([^@]+?)[\\]}]@([^ ]{3,})");
		Matcher matcher = pattern.matcher(nonRefContent);
		while (matcher.find()) {
			String mailGroup = matcher.group(1);
			String domainAndTLD = matcher.group(2);

			List<String> splitFirstParts = Arrays.asList(mailGroup.split("[;,|/]( )?"));
			splitFirstParts.forEach(e -> {
				validateAndAddEmailAddress(emailAddresses, e.trim() + "@" + domainAndTLD);
			});
		}
		return emailAddresses;
	}
}
