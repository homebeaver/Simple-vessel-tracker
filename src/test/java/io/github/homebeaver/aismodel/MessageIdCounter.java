package io.github.homebeaver.aismodel;

import java.util.HashMap;

/**
 * counts different MessageIds
 */
@SuppressWarnings("serial")
public class MessageIdCounter extends HashMap<Integer, Integer> {

	public MessageIdCounter() {
		super(28); // there are 28 different MessageIds (including UNKNOWNMESSAGE)
	}

	// ctor with MessageId
	public MessageIdCounter(Integer mId) {
		this();
		count(mId);
	}

	// ctor with another MessageIdCounter to copy
	public MessageIdCounter(MessageIdCounter cntr) {
		this();
		merge(cntr);
	}

	/**
	 * Counts the MessageId mId
	 * @param mId if the mapping value for mId (the counter) does not exists it is created and set to 1,
	 * otherwise the counter is incremented
	 * @return the MessageIdCounter instance
	 */
	public MessageIdCounter count(Integer mId) {
		// Interger::sum is the remappingBiFunction for the counter applying: newValue = sum(oldValue, 1);
		super.merge(mId, 1, Integer::sum);
		return this;
	}

	/**
	 * Merge a MessageIdCounter cntr to this. Acts like add of vectors
	 * @param cntr MessageIdCounter to add
	 * @return the MessageIdCounter instance after the merge
	 */
	public MessageIdCounter merge(MessageIdCounter cntr) {
		cntr.forEach((mId, v) -> {
			super.merge(mId, v, Integer::sum);
		});
		return this;
	}
}
