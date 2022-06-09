package luxoft.ch.wordsplitter;

import java.io.File;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class WordFinderTest {

	@Test
	void testShorterWordBetter() {
		String text = "additionalgorithm";
		test(text);
	}

	public void test(String text) {

		WordFinder wordFinder = new WordFinder(new File("dictionary.txt"));

		Dictionary dictionry = wordFinder.getDictionary();

		Collection<Integer> spaces = wordFinder.splitWords(text);

		int start = 0;

		for (int end : spaces) {

			String s = text.substring(start, end);

			start = end;

			System.out.print(s);

			if (dictionry.seek(s.toCharArray())) {

				System.out.println(" +");

			} else {

				System.out.println();

			}

		}

	}

}
