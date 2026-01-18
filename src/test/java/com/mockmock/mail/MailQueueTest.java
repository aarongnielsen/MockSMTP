package com.mockmock.mail;

import com.mockmock.Settings;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MailQueueTest  {

	@Test
	public void emptyQueue_valid() {
		MockMail mail = new MockMail();
		MailQueue mailQueue = new MailQueue();
		mailQueue.setSettings(new Settings());
		mailQueue.add(mail);
		Assertions.assertFalse(mailQueue.getMailQueue().isEmpty());

		mailQueue.emptyQueue();
		Assertions.assertTrue(mailQueue.getMailQueue().isEmpty());
	}

    @Test
    public void add_valid() {
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(new Settings());
        mailQueue.emptyQueue();

        MockMail mail = new MockMail();
        mailQueue.add(mail);

        Assertions.assertEquals(1, mailQueue.getMailQueue().size());
    }

    @Test
    public void add_validForMultipleAdditions() {
        Settings settings = new Settings();
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(settings);
        mailQueue.emptyQueue();

        for (int i = 0; i < settings.getMaxMailQueueSize(); i++) {
            MockMail mail = new MockMail();
            mailQueue.add(mail);
        }

        Assertions.assertEquals(mailQueue.getMailQueue().size(), settings.getMaxMailQueueSize());

        for (int i = 0; i < 10; i++) {
            MockMail mail = new MockMail();
            mailQueue.add(mail);
        }

        Assertions.assertEquals(mailQueue.getMailQueue().size(), settings.getMaxMailQueueSize());
    }

    @Test
    public void getById_valid() {
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(new Settings());
        mailQueue.emptyQueue();

        UUID id = UUID.randomUUID();
        MockMail mail = new MockMail();
        mail.setId(id);
        mail.setSubject("Test subject");
        mailQueue.add(mail);

        UUID id2 = UUID.randomUUID();
        MockMail mail2 = new MockMail();
        mail2.setId(id2);
        mail2.setSubject("Test subject 2");
        mailQueue.add(mail2);

        Assertions.assertEquals(mail, mailQueue.getById(id));
        Assertions.assertEquals(mail2, mailQueue.getById(id2));
    }

    @Test
    public void findById_valid() {
        List<MockMail> mockMailMessages = IntStream.rangeClosed(0, 3)
                .mapToObj(i -> {
                    String iAsString = String.valueOf(i);
                    String uuidString = StringUtils.repeat(iAsString, 8) + "-"
                            + StringUtils.repeat(iAsString, 4) + "-"
                            + StringUtils.repeat(iAsString, 4) + "-"
                            + StringUtils.repeat(iAsString, 4) + "-"
                            + StringUtils.repeat(iAsString, 12);
                    MockMail mockMail = new MockMail();
                    mockMail.setId(UUID.fromString(uuidString));
                    mockMail.setReceivedTime(2000000000L + i);
                    return mockMail;
                })
                .collect(Collectors.toList());

        MailQueue mailQueue = new MailQueue();
        for (int i = 1; i < mockMailMessages.size(); i++) {
            mailQueue.add(mockMailMessages.get(i));
        }

        Assertions.assertEquals(1, mailQueue.findById(mockMailMessages.get(1).getId()));
        Assertions.assertEquals(2, mailQueue.findById(mockMailMessages.get(2).getId()));
        Assertions.assertEquals(3, mailQueue.findById(mockMailMessages.get(3).getId()));
        Assertions.assertEquals(0, mailQueue.findById(mockMailMessages.get(0).getId()));  // not found
    }

    @Test
    public void deleteByIndex_notFound() {
        MailQueue mailQueue = new MailQueue();
        IntStream.rangeClosed(1, 3).forEach((i) -> mailQueue.add(new MockMail()));
        Assertions.assertFalse(mailQueue.deleteByIndex(4));
        Assertions.assertFalse(mailQueue.deleteByIndex(0));
    }


}
