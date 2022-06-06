package luxoft.ch.wordsplitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Dictionary implements Iterable<String> {

	private static class Word implements Comparable<Word> {

		private static final Comparator<Word> WORD_COMPARATOR = (Word a1, Word a2) -> Arrays.compare(a1.value, 0,
				a1.length, a2.value, 0, a2.length);

		private int hash;
		private char[] value;
		private int length;

		private Word(int size) {
			value = new char[size];
		}

		private Word(String string) {
			value = string.toCharArray();
			length = value.length;
			hash = hashCode(value, 0, value.length);
		}

		private static int hashCode(char[] a, int startIndex, int endIndex) {
			int result = 1;
			for (int k = startIndex; k < endIndex; k++) {
				result = (result << 5) - result + a[k];
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Word key) {
				return WORD_COMPARATOR.compare(this, key) == 0;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public int compareTo(Word word) {
			return WORD_COMPARATOR.compare(this, word);
		}

	}

	private final Set<Word> words;
	private final Word searchKey;
	private int maxLength;
	private long totalLength;

	public Dictionary(File file) {
		words = new HashSet<>();
		load(file);
		searchKey = new Word(getMaxLength());
	}

	private Word getSearchKey(char[] key, int startIndex, int endIndex) {
		searchKey.length = endIndex - startIndex;
		System.arraycopy(key, startIndex, searchKey.value, 0, searchKey.length);
		searchKey.hash = Word.hashCode(searchKey.value, 0, searchKey.length);
		return searchKey;
	}

	public void addWord(String word) {
		maxLength = Math.max(maxLength, word.length());
		totalLength += word.length();
		words.add(new Word(word));
	}

	public int getMaxLength() {
		return maxLength;
	}

	public int getAverageLength() {
		return (int) totalLength / words.size();
	}

	public int getSize() {
		return words.size();
	}

	public boolean seek(char[] key) {
		return seek(key, 0, key.length);
	}

	public boolean seek(char[] key, int startIndex, int endIndex) {
		return words.contains(getSearchKey(key, startIndex, endIndex));
	}

	private void load(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while (reader.ready() && (line = reader.readLine()) != null) {
				addWord(line.strip());
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new WordFinderException("can't read file %s".formatted(file.getName()), e);
		}
	}

	public void print(Writer writer) {
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		try {
			for (var word : words) {
				bufferedWriter.append(String.valueOf(word));
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new WordFinderException("can't print dictionary", e);
		}
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {

			private Iterator<Word> iterator = words.iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public String next() {
				var word = iterator.next();
				return new String(word.value, 0, word.length);
			}

		};
	}

	public static void main(String... args) {
		Dictionary dictionary = new Dictionary(new File("dictionary.txt"));

		System.out.printf("Successfully loaded %d entries of dictionary%n", dictionary.getSize());
		// dictionary.print(new PrintWriter(System.out));

		System.out.printf("%nmaximum length of word is %d%n", dictionary.getMaxLength());

		System.out.printf("%nLooking for word %s: %b", "space", dictionary.seek("space".toCharArray()));
		System.out.printf("%nLooking for word %s: %b", "ditch", dictionary.seek("ditch".toCharArray()));
		System.out.printf("%nLooking for word %s: %b", "back", dictionary.seek("back".toCharArray()));
		System.out.printf("%nLooking for word %s: %b", "123", dictionary.seek("123".toCharArray()));

	}

}
