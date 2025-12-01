package com.mockmock.mail;

import com.mockmock.Settings;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class MailQueueTest
{
	@Test
	public void testEmptyQueue()
	{
		MockMail mail = new MockMail();
		MailQueue mailQueue = new MailQueue();
		mailQueue.setSettings(new Settings());
		mailQueue.add(mail);
		Assert.assertFalse(mailQueue.getMailQueue().isEmpty());

		mailQueue.emptyQueue();
		Assert.assertTrue(mailQueue.getMailQueue().isEmpty());
	}

	@Test
	public void testDeleteMailFromQueue()
	{
		MockMail mail = new MockMail();
		mail.setId(UUID.fromString("00000000-0000-0000-0000-000000001337"));
		MailQueue mailQueue = new MailQueue();
		mailQueue.setSettings(new Settings());
		mailQueue.add(mail);
		Assert.assertFalse(mailQueue.getMailQueue().isEmpty());

		mailQueue.deleteById(mail.getId());
		Assert.assertTrue(mailQueue.getMailQueue().isEmpty());
	}

    @Test
    public void testAdd()
    {
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(new Settings());
        mailQueue.emptyQueue();

        MockMail mail = new MockMail();
        mailQueue.add(mail);

        Assert.assertEquals(1, mailQueue.getMailQueue().size());
    }

    @Test
    public void testMultipleAdd()
    {
        Settings settings = new Settings();
        MailQueue mailQueue = new MailQueue();
        mailQueue.setSettings(settings);
        mailQueue.emptyQueue();

        for (int i = 0; i < settings.getMaxMailQueueSize(); i++)
        {
            MockMail mail = new MockMail();
            mailQueue.add(mail);
        }

        Assert.assertEquals(mailQueue.getMailQueue().size(), settings.getMaxMailQueueSize());

        for (int i = 0; i < 10; i++)
        {
            MockMail mail = new MockMail();
            mailQueue.add(mail);
        }

        Assert.assertEquals(mailQueue.getMailQueue().size(), settings.getMaxMailQueueSize());
    }

    @Test
    public void testGetById()
    {
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

        Assert.assertEquals(mail, mailQueue.getById(id));
        Assert.assertEquals(mail2, mailQueue.getById(id2));
    }
}
