package luxoft.ch.wordsplitter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WordFinder implements WordSplitter {

	private final Dictionary dictionary;

	public WordFinder(File file) {
		dictionary = new Dictionary(file);
	}

	private static final int NOT_FOUND = -1;

	private int findWord(char[] text, int startIndex) {
		int length = Math.min(dictionary.getMaxLength(), text.length - startIndex);
		while (length > 0) {
			int endIndex = startIndex + length;
			if (dictionary.seek(text, startIndex, endIndex)) {
				return endIndex;
			}
			length--;
		}
		return NOT_FOUND;
	}

	public Collection<Integer> splitWords(String string) {
		char[] text = string.toCharArray();
		List<Integer> indices = new ArrayList<>();
		int startIndex = 0;
		while (startIndex < text.length) {
			int lastIndex = findWord(text, startIndex);
			if (lastIndex == NOT_FOUND) {
				startIndex++;
			} else {
				indices.add(lastIndex);
				startIndex = lastIndex;
			}
		}
		return indices;
	}

	private static final String SAMPLE_PHRASE = "diedandalmostdilapidatedbutnonethelessstillprettyabandonedgreenhousewithgrownplanttrunksandrottendebris";

	public static void main(String... args) {
		WordFinder wordFinder = new WordFinder(new File("dictionary.txt"));
		Collection<Integer> indices = wordFinder.splitWords(SAMPLE_PHRASE);
		int startIndex = 0;
		for (int endIndex : indices) {
			System.out.printf("last index %d, word %s%n", endIndex, SAMPLE_PHRASE.substring(startIndex, endIndex));
			startIndex = endIndex;
		}

	}

}
