package nl.tudelft.serg.paperauthorinferencer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Author implements Comparable<Author> {

	public String firstName;
	public String firstNameAbreviation;

	public String middleName;
	public String middleNameAbreviation;

	public String lastName;

	public String fullName;

	double referenceEntriesRatio = 0;
	double occurenceRatio = 0;
	double firstOccurrenceRatio = 1;
	int eldestRefDelta = 0;
	int newestRefDelta = Integer.MAX_VALUE;

	public Author(String fullName) {
		this.fullName = fullName;

		String potentialFirstName = matchAndFindNamePart("^([^ ]*).*?$");
		if (!StringUtils.isEmpty(potentialFirstName)) {
			if (!isAbbreviated(potentialFirstName)) {
				firstName = potentialFirstName;
			}
			firstNameAbreviation = abbreviate(potentialFirstName);
		}
		String potentialMiddleName = matchAndFindNamePart("^.*? (.*) .*?$");
		if (!StringUtils.isEmpty(potentialMiddleName)) {
			if (!isAbbreviated(potentialMiddleName)) {
				middleName = potentialMiddleName;
			}
			middleNameAbreviation = abbreviate(potentialMiddleName);
		}

		lastName = matchAndFindNamePart(".* ([^ ]*)$");
	}

	private boolean isAbbreviated(String namePart) {
		if (namePart.length() < 3) {
			if (namePart.endsWith(".")) {
				return true;
			}
		}
		return false;
	}

	private String abbreviate(String name) {
		return name.substring(0, 1) + ".";
	}

	public String getCanonicalName() {
		if (!StringUtils.isEmpty(firstNameAbreviation) && !StringUtils.isEmpty(lastName)) {
			return firstNameAbreviation + " " + lastName;
		}
		return fullName;
	}

	private String matchAndFindNamePart(String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(fullName);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	@Override
	public boolean equals(Object otherObject) {
		// Algorithm principal: the more information we have, the more precise
		// the result of equals is.
		if (!(otherObject instanceof Author)) {
			return false;
		}

		Author otherAuthor = (Author) otherObject;

		boolean firstNameMatch = false;
		boolean middleNameMatch = false;
		boolean lastNameMatch = false;

		boolean isTheSameAuthor = getCanonicalName().equals(otherAuthor.getCanonicalName());
		if (firstName != null && otherAuthor.firstName != null) {
			firstNameMatch = firstName.equals(otherAuthor.firstName);
			isTheSameAuthor &= firstNameMatch;
		} else if (firstNameAbreviation != null && otherAuthor.firstNameAbreviation != null) {
			firstNameMatch = firstNameAbreviation.equals(otherAuthor.firstNameAbreviation);
			isTheSameAuthor &= firstNameMatch;
		}

		if (middleName != null && otherAuthor.middleName != null) {
			middleNameMatch = middleName.equals(otherAuthor.middleName);
			isTheSameAuthor &= middleNameMatch;
		} else if (middleNameAbreviation != null && otherAuthor.middleNameAbreviation != null) {
			middleNameMatch = middleNameAbreviation.equals(otherAuthor.middleNameAbreviation);
			isTheSameAuthor &= middleNameMatch;
		}

		if (lastName != null && otherAuthor.lastName != null) {
			lastNameMatch = lastName.equals(otherAuthor.lastName);
			isTheSameAuthor &= lastNameMatch;
		}

		if (firstNameMatch && lastNameMatch && !middleNameMatch) {
			return true;
		}

		return isTheSameAuthor;
	}

	@Override
	public int compareTo(Author o) {
		return referenceEntriesRatio < o.referenceEntriesRatio ? -1
				: (referenceEntriesRatio == o.referenceEntriesRatio) ? 0 : 1;
	}

	@Override
	public String toString() {
		return getCanonicalName();
	}
}