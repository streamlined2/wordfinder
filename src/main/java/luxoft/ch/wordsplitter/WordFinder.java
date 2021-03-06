package luxoft.ch.wordsplitter;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WordFinder implements WordSplitter {

	private final Dictionary dictionary;

	public WordFinder(File file) {
		dictionary = new Dictionary(file);
	}

	Dictionary getDictionary() {
		return dictionary;
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

	private int probableWordCount(String string) {
		return (int) (string.length() * 1.25d / dictionary.getAverageLength());
	}

	public Collection<Integer> splitWords(String string) {
		char[] text = string.toCharArray();
		List<Integer> indices = new ArrayList<>(probableWordCount(string));
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

	private static void printIndicesWords(String data, Collection<Integer> indices) {
		int startIndex = 0;
		for (int endIndex : indices) {
			System.out.printf("last index: %d, word: %s%n", endIndex, data.substring(startIndex, endIndex));
			startIndex = endIndex;
		}
	}

	private String prepareTestData(int size) {
		SecureRandom random = new SecureRandom();
		List<Integer> indices = new ArrayList<>(size);
		for (int k = 0; k < size; k++) {
			indices.add(random.nextInt(0, dictionary.getSize()));
		}
		Collections.sort(indices);

		StringBuilder builder = new StringBuilder();
		int prevIndex = 0;
		var iterator = dictionary.iterator();
		String value = iterator.next();
		for (var index : indices) {
			while (prevIndex < index && iterator.hasNext()) {
				value = iterator.next();
				prevIndex++;
			}
			builder.append(value);
		}
		return builder.toString();
	}

	private static String reconstructTestData(String data, Collection<Integer> indices) {
		StringBuilder builder = new StringBuilder();
		int startIndex = 0;
		for (int endIndex : indices) {
			builder.append(data, startIndex, endIndex);
			startIndex = endIndex;
		}
		return builder.toString();
	}

	private static final String SAMPLE_PHRASE = "abandonedaberdeenabsenceabsorptionacademyaccessibilityaccommodationaccordanceaccountabilityaccuracyaceacknowledgeacquisitionactionadaptationadditionadjustmentadmission";
	private static final int WORD_COUNT = 1_000_000;

	public static void main(String... args) {
		WordFinder wordFinder = new WordFinder(new File("dictionary.txt"));

		System.out.println("\ntest #1: splitting phrase: %s".formatted(SAMPLE_PHRASE));
		Collection<Integer> indices = wordFinder.splitWords(SAMPLE_PHRASE);
		printIndicesWords(SAMPLE_PHRASE, indices);

		System.out.println("\ntest #2");
		String testData = wordFinder.prepareTestData(WORD_COUNT);
		// System.out.println("\nsplitting phrase: %s".formatted(testData));
		long start = System.currentTimeMillis();
		Collection<Integer> indices2 = wordFinder.splitWords(testData);
		long duration = System.currentTimeMillis() - start;
		// printIndicesWords(testData, indices2);
		String checkData = reconstructTestData(testData, indices2);
		if (testData.equals(checkData)) {
			System.out.println("\ntest passed, splitting took %d msec for %d words".formatted(duration, WORD_COUNT));
		} else {
			System.out.println("\ntest failed");
		}

	}

}
