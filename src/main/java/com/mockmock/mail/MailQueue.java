package com.mockmock.mail;

import com.mockmock.Settings;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Getter
public class MailQueue  {

    // internal list is declared as an ArrayList rather than as an interface type,
    // since we want to use the internal trimToSize() method when the list is truncated
    private final ArrayList<MockMail> mailQueue = new ArrayList<>();

    @Setter
    private Settings settings;

    /** Returns the number of messages in the queue. **/
    public int size() {
        return mailQueue.size();
    }

    /**
     * Adds a mail message to the queue.
     * This is implemented as an insertion-sort, so messages are always stored in order of the time they are received.
     * If {@link Settings#setMaxMailQueueSize(int)} is set, the queue is also trimmed automatically.
     *
     * @param mail the mail message to add to the queue.
     */
    public synchronized void add(MockMail mail) {
        // insert the new message into the list at the right index to keep the list sorted
        synchronized (mailQueue) {
            int insertIndex = Collections.binarySearch(mailQueue, mail);
            if (insertIndex < 0) {
                insertIndex = -insertIndex - 1;
            }
            mailQueue.add(insertIndex, mail);
            trimQueue();
        }
    }

    public int findById(UUID id) {
        for (int implIndex = 0; implIndex < mailQueue.size(); implIndex++) {
            MockMail mockMail = mailQueue.get(implIndex);
            if (mockMail.getId().equals(id)) {
                return implIndex + 1;
            }
        }
        return 0;
    }

    /**
     * Returns the MockMail that belongs to the given ID
     * @param id The id of the mail that needs to be retrieved
     * @return Returns the MockMail when found or null otherwise
     */
    public MockMail getById(UUID id) {
        int apiIndex = findById(id);
        int implIndex = getImplIndex(apiIndex);
        return mailQueue.get(implIndex);
    }

    public MockMail getByIndex(int index) {
        try {
            int implIndex = getImplIndex(index);
            return mailQueue.get(implIndex);
        } catch (IndexOutOfBoundsException x) {
            return null;
        }
    }

    /**
     * Removes all mail in the queue
     */
    public void emptyQueue() {
        mailQueue.clear();
        mailQueue.trimToSize();
    }

    public boolean deleteByIndex(int index) {
        try {
            int implIndex = getImplIndex(index);
            mailQueue.remove(implIndex);
            return true;
        } catch (IndexOutOfBoundsException x) {
            return false;
        }
    }

    /**
     * Given a 1-indexed or -1-indexed API index, returns the index in the underlying 0-indexed list implementation.
     * For example, for a mail queue with 10 entries:
     * <ul>
     *   <li>{@code mailQueue.getImplIndex(1)} returns 0</li>
     *   <li>{@code mailQueue.getImplIndex(10)} returns 9</li>
     *   <li>{@code mailQueue.getImplIndex(-1)} returns 9</li>
     *   <li>{@code mailQueue.getImplIndex(-4)} returns 6</li>
     *   <li>{@code mailQueue.getImplIndex(0)} throws an exception</li>
     *   <li>{@code mailQueue.getImplIndex(11)} throws an exception</li>
     *   <li>{@code mailQueue.getImplIndex(-11)} throws an exception</li>
     * </ul>
     *
     * @param apiIndex an index into the mail queue,
     *     where 1,2,3... represent the earliest messages received,
     *     and -1,-2,-3... represent the latest messages received.
     * @return the corresponding 0-based index into the underlying list implementation.
     * @throws IndexOutOfBoundsException if {@code apiIndex} is 0,
     *     or if {@code |apiIndex|} is greater than the number of messages in the queue.
     */
    private int getImplIndex(int apiIndex) throws IndexOutOfBoundsException {
        if (apiIndex > 0 && apiIndex <= mailQueue.size()) {
            return apiIndex - 1;
        } else if (apiIndex < 0 && -apiIndex <= mailQueue.size()) {
            return mailQueue.size() + apiIndex;
        }
        throw new IndexOutOfBoundsException("invalid mail queue index: " + apiIndex);
    }

    /** Trims the mail queue so that the number of messages does not exceed the maximum setting. **/
    private void trimQueue() {
        int maxMailQueueSize = (settings != null ? settings.getMaxMailQueueSize() : 0);
        if (maxMailQueueSize <= 0) {
            return;
        }

        int numberOfMessagesToRemove = mailQueue.size() - maxMailQueueSize;
        if (numberOfMessagesToRemove > 0) {
            mailQueue.subList(0, numberOfMessagesToRemove).clear();
            mailQueue.trimToSize();
        }
    }

}
