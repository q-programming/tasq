package com.qprogramming.tasq.support.web;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.support.web.MessageHelper;

@RunWith(MockitoJUnitRunner.class)
public class MessageHelperTest {

	private static final String MESSAGE = "Message";

	@Mock
	RedirectAttributes ra;

	@Mock
	Model model;

	@Test
	public void addSuccessMessageTest() {
		MessageHelper.addSuccessAttribute(ra, MESSAGE);
		verify(ra, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.SUCCESS,
								new Object[] {}));
	}

	@Test
	public void addErrorMessageTest() {
		MessageHelper.addErrorAttribute(ra, MESSAGE);
		verify(ra, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void addWarrningMessageTest() {
		MessageHelper.addWarningAttribute(ra, MESSAGE);
		verify(ra, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.WARNING,
								new Object[] {}));
	}

	@Test
	public void addInfoMessageTest() {
		MessageHelper.addInfoAttribute(ra, MESSAGE);
		verify(ra, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.INFO, new Object[] {}));
	}

	@Test
	public void addMessagesWithModelTest() {
		MessageHelper.addSuccessAttribute(model, MESSAGE);
		MessageHelper.addErrorAttribute(model, MESSAGE);
		MessageHelper.addWarningAttribute(model, MESSAGE);
		MessageHelper.addInfoAttribute(model, MESSAGE);
		verify(model, times(4)).addAttribute(anyString(), any(Message.class));
	}

	@Test
	public void createMessageTest() {
		Message message = new Message(MESSAGE, Message.Type.SUCCESS);
		Message message2 = new Message(MESSAGE, Message.Type.SUCCESS,
				new Object[] {});
		Assert.assertEquals(Message.Type.SUCCESS, message.getType());
		Assert.assertEquals(MESSAGE, message.getMessage());
		Assert.assertNotNull(message2.getArgs());
	}

}
