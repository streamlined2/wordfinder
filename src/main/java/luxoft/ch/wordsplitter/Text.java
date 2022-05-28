package luxoft.ch.wordsplitter;

import java.util.ArrayList;
import java.util.List;

class Text {
	
	private final List<StringBuilder> textChunks;

	public Text(String text) {
		this.textChunks = new ArrayList<>();
		this.textChunks.add(new StringBuilder(text));
	}

}
