package luxoft.ch.wordsplitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

class Dictionary implements Iterable<String> {

	private static class Word implements Comparable<Word> {

		private static final Comparator<Word> BY_LENGTH_FIRSTLETTER_HASH_WORD_COMPARATOR = Comparator
				.comparing(Word::getLength).thenComparing(Word::getFirstLetter).thenComparing(Word::getHash)
				.thenComparing(Word::getWord);

		private final int length;
		private final char firstLetter;
		private final int hash;
		private final String word;

		private Word(String word) {
			this.length = word.length();
			this.firstLetter = word.charAt(0);
			this.hash = word.hashCode();
			this.word = word;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Word key) {
				return BY_LENGTH_FIRSTLETTER_HASH_WORD_COMPARATOR.compare(this, key) == 0;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public int compareTo(Word word) {
			return BY_LENGTH_FIRSTLETTER_HASH_WORD_COMPARATOR.compare(this, word);
		}

		public int getLength() {
			return length;
		}

		public char getFirstLetter() {
			return firstLetter;
		}

		public int getHash() {
			return hash;
		}

		public String getWord() {
			return word;
		}

	}

	private final SortedSet<Word> words;

	private Dictionary() {
		words = new TreeSet<>();
	}

	public void addWord(String word) {
		words.add(new Word(word));
	}

	public int getSize() {
		return words.size();
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {

			private final Iterator<Word> wordIterator = words.iterator();

			@Override
			public boolean hasNext() {
				return wordIterator.hasNext();
			}

			@Override
			public String next() {
				return wordIterator.next().word;
			}

		};
	}

	public static Dictionary create(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			var dictionary = new Dictionary();
			String line;
			while (reader.ready() && (line = reader.readLine()) != null) {
				dictionary.addWord(line.strip());
			}
			return dictionary;
		} catch (IOException e) {
			e.printStackTrace();
			throw new WordFinderException("can't read file %s".formatted(file.getName()), e);
		}
	}

	public void print(Writer writer) {
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		try {
			for (var word : this) {
				bufferedWriter.append(word);
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new WordFinderException("can't print dictionary", e);
		}
	}

	public static void main(String... args) {
		Dictionary dictionary = Dictionary.create(new File("dictionary.txt"));
		System.out.printf("Successfully loaded %d entries of dictionary:%n", dictionary.getSize());
		dictionary.print(new PrintWriter(System.out));
	}

}
