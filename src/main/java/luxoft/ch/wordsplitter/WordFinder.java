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

	public int findFirstWord(int startIndex, char[] text) {
		int length = Math.min(dictionary.getMaxLength(), text.length - startIndex);
		while (length > 0) {
			int endIndex = startIndex + length;
			boolean found = dictionary.seek(text, startIndex, endIndex);
			if (found) {
				return endIndex;
			}
			length--;
		}
		return NOT_FOUND;
	}

	public Collection<Integer> splitWords(String text) {
		List<Integer> indices = new ArrayList<>();
		// TODO Auto-generated method stub

		return indices;
	}

	private static final String SAMPLE_PHRASE = "abandonedgreenhousewithgrownplanttrunksandrottendebris";

	public static void main(String... args) {
		WordFinder wordFinder = new WordFinder(new File("dictionary.txt"));
//		Collection<Integer> indices = wordFinder.splitWords(SAMPLE_PHRASE);
//		int startIndex = 0;
//		for (int endIndex : indices) {
//			System.out.printf("index %d, word %s%n", SAMPLE_PHRASE.substring(startIndex, endIndex));
//			startIndex = endIndex;
//		}
		int index = wordFinder.findFirstWord(0, "abandoned".toCharArray());
		if (index != NOT_FOUND) {
			System.out.println("found word index %d".formatted(index));
		} else {
			System.out.println("nothing found");
		}

	}

}
