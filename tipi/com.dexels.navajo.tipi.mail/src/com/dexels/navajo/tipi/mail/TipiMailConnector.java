package com.dexels.navajo.tipi.mail;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;
import javax.mail.internet.MimeMultipart;

import com.dexels.navajo.document.Message;
import com.dexels.navajo.document.Navajo;
import com.dexels.navajo.document.NavajoException;
import com.dexels.navajo.document.NavajoFactory;
import com.dexels.navajo.document.Property;
import com.dexels.navajo.document.types.Binary;
import com.dexels.navajo.tipi.TipiBreakException;
import com.dexels.navajo.tipi.TipiComponentMethod;
import com.dexels.navajo.tipi.TipiException;
import com.dexels.navajo.tipi.connectors.TipiBaseConnector;
import com.dexels.navajo.tipi.connectors.TipiConnector;
import com.dexels.navajo.tipi.internal.TipiEvent;

public class TipiMailConnector extends TipiBaseConnector implements TipiConnector {
	
	private static final long serialVersionUID = -2794376821756313135L;
	private long disconnectTimeout = 40000;
	private String host = "";
	private String username = "";
	private String password = "";
	private Session session;
	private ConnectionListener myConnectionListener = null;
	private Store store = null;
	private FolderListener myFolderListener = null;
	private StoreListener myStoreListener = null;
	private String mailMode = "imap";
	private int messageCount;
	private int unreadMessageCount;
	private Folder fff;
//	private Date recentAfter;

	private int pageSize = 0;
	private int currentPage = 1;
	
	private transient Timer disconnectTimer = new Timer("DisconnectTimer",true);
	

	public void doTransaction(Navajo n, String service, String destination) throws TipiBreakException, TipiException {
		try {
			ensureOpenConnection();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		activity();
		if (service == null) {
			service = "InitMail";
		}
		if (service.equals("InitMail")) {
			injectNavajo(service, createMailInit());
		}
		if (service.equals("InitGetFolders")) {
			injectNavajo(service, createFolderNavajo());
		}
		if (service.equals("InitGetMessages")) {
			injectNavajo(service, createInitGetMessages());
		}

		if (service.equals("GetInboxMessages")) {
			injectNavajo(service, createMessagesNavajo());
		}
		// if (service.equals("GetMessages")) {
		// injectNavajo(service, createMessagesNavajo(n));
		// }
		// if (service.equals("InitGetDefaultMessages")) {
		// injectNavajo(service, createMessagesNavajo(n));
		// }
		if (service.equals("GetMessage")) {
			injectNavajo(service, createGetMessage(n));
		}
		if (service.equals("DeleteMessage")) {
			setMessageFlag(n, Flag.DELETED, true);
		}
		if (service.equals("SeenMessage")) {
			setMessageFlag(n, Flag.SEEN, true);
		}
		if (service.equals("RecentMessage")) {
			setMessageFlag(n, Flag.RECENT, true);
			setMessageFlag(n, Flag.SEEN, false);
		}
		if (service.equals("UnRecentMessage")) {
			setMessageFlag(n, Flag.RECENT, false);
			setMessageFlag(n, Flag.SEEN, false);
		}
		if (service.equals("SendMessage")) {
			sendMessage(n);
		}
	}

	private void activity() {
		// something happened. Reset disconnect imeout
		if(disconnectTimer==null) {
			return;
		}
		if(disconnectTimeout > 0) {
			disconnectTimer.cancel();
			// I _think_ this is not necessary
			disconnectTimer = new Timer("DisconnectTimer",true);
			disconnectTimer.schedule(new TimerTask(){

				@Override
				public void run() {
					synchronized(this) {
						System.err.println("Timing out!");
						try {
							disconnect();
							performTipiEvent("onTimeout", null, false);
						} catch (MessagingException e) {
							e.printStackTrace();
						} catch (TipiBreakException e) {
							e.printStackTrace();
						} catch (TipiException e) {
							e.printStackTrace();
						}
						if(disconnectTimer!=null) {
							disconnectTimer.cancel();
						}
					}
				}}, disconnectTimeout);
		}
	}

	private void sendMessage(Navajo n) {
	}

	public String getDefaultEntryPoint() {
		return "InitMail";
	}

	private Navajo createInitGetMessages() throws TipiException {

		try {
			Navajo n = NavajoFactory.getInstance().createNavajo();
			Message m = NavajoFactory.getInstance().createMessage(n, "Folder");
			n.addMessage(m);
			Property p = NavajoFactory.getInstance().createProperty(n, "Name", Property.STRING_PROPERTY, "", 0, "", Property.DIR_IN);
			m.addProperty(p);

			Property start = NavajoFactory.getInstance().createProperty(n, "Start", Property.INTEGER_PROPERTY, "0", 0, "",
					Property.DIR_IN);
			m.addProperty(start);
			Property end = NavajoFactory.getInstance().createProperty(n, "End", Property.INTEGER_PROPERTY, "" + messageCount, 0, "",
					Property.DIR_IN);
			m.addProperty(end);

			Property q = NavajoFactory.getInstance().createProperty(n, "MessageNumber", Property.INTEGER_PROPERTY,
					"" + messageCount, 0, "", Property.DIR_OUT);
			m.addProperty(q);
			n.addMethod(NavajoFactory.getInstance().createMethod(n, "GetMessages", null));
			return n;
		} catch (NavajoException e) {
			throw new TipiException("Can not get init messages", e);
		}

	}

	private Navajo createMailInit() {
		Navajo n = NavajoFactory.getInstance().createNavajo();
		try {
			n.addMethod(NavajoFactory.getInstance().createMethod(n, "InitGetFolders", null));
			n.addMethod(NavajoFactory.getInstance().createMethod(n, "GetInboxMessages", null));

		} catch (NavajoException e) {
			e.printStackTrace();
		}
		return n;
	}

	private Navajo createGetMessage(Navajo n) throws TipiException {
		String name = null;
		name = (String) n.getProperty("/Folder/Name").getTypedValue();

		if ("POP3".equalsIgnoreCase(mailMode)) {
			name = "INBOX";
		}
		int messageNumber = (Integer) n.getProperty("/Folder/MessageNumber").getTypedValue();
		// System.err.println("Getting message: " + name + " nr. " +
		// messageNumber);
		return createSingleMessageNavajo(name, messageNumber);
	}

	private boolean setMessageFlag(Navajo n, Flag flag, boolean value) throws TipiException {
		String name = (String) n.getProperty("/Folder/Name").getTypedValue();
		int messageNumber = (Integer) n.getProperty("/Folder/MessageNumber").getTypedValue();
		System.err.println("Attempting to delete message: " + messageNumber);

		if ("POP3".equalsIgnoreCase(mailMode)) {
			name = "INBOX";
		}
		// Folder fff;
		// if(fff==null || !fff.isOpen()) {
		// try {
		// fff = store.getFolder(name);
		// fff.open(Folder.READ_WRITE);
		// } catch (MessagingException e) {
		// throw new TipiException("Error opening mailbox: " + name, e);
		// }
		// }
		try {
			javax.mail.Message m = fff.getMessage(messageNumber);
			m.setFlag(flag, true);
			// m.getFlags().
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new TipiException("Error deleting message# " + messageNumber + " from folder: " + name, e);
		}
		return true;
	}

	private synchronized Navajo createSingleMessageNavajo(String name, int messageNumber) throws TipiException {
		try {
			ensureOpenConnection();
			javax.mail.Message m = fff.getMessage(messageNumber);
			m.setFlag(Flag.SEEN, true);
			Navajo myNavajo = NavajoFactory.getInstance().createNavajo();
			Message mm = addMessageProperties(m, myNavajo);
			myNavajo.addMessage(mm);
			Object content = m.getContent();
			if (content instanceof MimeMultipart) {
				Message parts = NavajoFactory.getInstance().createMessage(myNavajo, "Parts", Message.MSG_TYPE_ARRAY);
				mm.addMessage(parts);
				MimeMultipart r = (MimeMultipart) content;
				for (int i = 0; i < r.getCount(); i++) {
					Message part = NavajoFactory.getInstance().createMessage(myNavajo, "Parts", Message.MSG_TYPE_ARRAY_ELEMENT);
					parts.addMessage(part);
					BodyPart bp = r.getBodyPart(i);
					addProperty(part, "ContentType", bp.getContentType(), "string");
					addProperty(part, "Description", bp.getDescription(), "string");
					addProperty(part, "FileName", bp.getFileName(), "string");
					addProperty(part, "Disposition", bp.getDisposition(), "string");
					Binary b = new Binary(bp.getInputStream(), false);
					addProperty(part, "Content", b, "binary");
				}
			} else {
				Binary b = new Binary(m.getInputStream(), false);
				addProperty(mm, "Content", b, name);
				addProperty(mm, "ContentType", m.getContentType(), "string");
				addProperty(mm, "Description", m.getDescription(), "string");
				addProperty(mm, "FileName", m.getFileName(), "string");
				addProperty(mm, "Disposition", m.getDisposition(), "string");
				addProperty(mm, "Content", b, "binary");
			}
			return myNavajo;
		} catch (MessagingException e) {
			throw new TipiException("Error getting message: " + messageNumber + " from box: " + name, e);
		} catch (NavajoException e) {
			throw new TipiException("Error getting message: " + messageNumber + " from box: " + name, e);
		} catch (IOException e) {
			throw new TipiException("Error getting message: " + messageNumber + " from box: " + name, e);
		}
	}

	protected void ensureOpenConnection() throws MessagingException {
		if(store==null) {
			connect();
			return;
		}
		if(fff==null || !fff.isOpen()) {
			connect();
			return;
		}
	}

	private Navajo createMessagesNavajo() throws TipiException {
		// String name = null;
		//
		// if (n == null) {
		// name = "INBOX";
		// } else {
		// name = (String) n.getProperty("/Folder/Name").getTypedValue();
		// }
		//
		// if ("POP3".equalsIgnoreCase(mailMode)) {
		// name = "INBOX";
		// }
		Navajo myNavajo = NavajoFactory.getInstance().createNavajo();

		// if (store == null) {
		// try {
		// connect();
		// } catch (MessagingException e) {
		// throw new TipiException("WTF? " + e.getMessage(), e);
		// }
		// }
		try {
			Message m = NavajoFactory.getInstance().createMessage(myNavajo, "Page");
			myNavajo.addMessage(m);
			Property p = NavajoFactory.getInstance().createProperty(myNavajo, "CurrentPage", Property.INTEGER_PROPERTY,
					"" + currentPage, 0, "", Property.DIR_IN);
			m.addProperty(p);
			p = NavajoFactory.getInstance().createProperty(myNavajo, "PageCount", Property.INTEGER_PROPERTY, "" + getPageCount(), 0,
					"", Property.DIR_OUT);
			m.addProperty(p);
			p = NavajoFactory.getInstance().createProperty(myNavajo, "PageSize", Property.INTEGER_PROPERTY, "" + pageSize, 0, "",
					Property.DIR_OUT);
			m.addProperty(p);

		} catch (NavajoException e) {
			e.printStackTrace();
		}

		addFolderToNavajo(fff, myNavajo, "MailBox", null);
		return myNavajo;
	}

	protected void setComponentValue(String name, Object object) {
		if (name.equals("server")) {
			host = (String) object;
		}
		if (name.equals("username")) {
			username = (String) object;
		}
		if (name.equals("password")) {
			password = (String) object;
		}
		if (name.equals("mailMode")) {
			mailMode = (String) object;
		}
		if (name.equals("recentAfter")) {
//			recentAfter = (Date) object;
		}
		if (name.equals("pageSize")) {
			pageSize = (Integer) object;
		}
		if (name.equals("currentPage")) {
			setCurrentPage((Integer) object);
		}
		if (name.equals("disconnectTimeout")) {
			disconnectTimeout = (Integer)object;
		}

		super.setComponentValue(name, object);
	}

	@Override
	protected Object getComponentValue(String name) {
		if (name.equals("messageCount")) {
			return messageCount;
		}
		if (name.equals("pageCount")) {
			return getPageCount();
		}
		if (name.equals("currentPage")) {
			return currentPage;
		}
		if (name.equals("disconnectTimeout")) {
			return disconnectTimeout;
		}

		return super.getComponentValue(name);
	}

	private int getPageCount() {
		if (pageSize == 0) {
			return 1;
		} else {
//			System.err.println("# messages: " + messageCount + " pagesize: " + pageSize + " pageCount: "
//					+ ((int) (messageCount / pageSize)));
			return (int) Math.ceil((double) messageCount / pageSize);
		}
	}

	public String getConnectorId() {
		return "mail";
	}

	@Override
	protected void performComponentMethod(String name, TipiComponentMethod compMeth, TipiEvent event) throws TipiBreakException {
		if (name.equals("connect")) {
			try {
				connect();
			} catch (MessagingException e) {
				e.printStackTrace();
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("message", "Error connecting to server: " + host);
				m.put("exception", "Error connecting to server: " + e.getMessage());
				try {
					performTipiEvent("onMailError", m, false);
				} catch (TipiException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (name.equals("disconnect")) {
//			System.err.println("Disconnecting mail");
			try {
				fff.close(true);
				disconnect();
			} catch (MessagingException e) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("message", "Error disconnecting from server: " + host);
				m.put("exception", "Error disconnecting from server: " + e.getMessage());
				try {
					performTipiEvent("onMailError", m, false);
				} catch (TipiException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (name.equals("nextPage")) {
			setCurrentPage(currentPage + 1);
		}
		if (name.equals("previousPage")) {
			setCurrentPage(currentPage - 1);
		}
		if (name.equals("firstPage")) {
			setCurrentPage(1);
		}
		if (name.equals("lastPage")) {
			setCurrentPage(getPageCount());
		}

		super.performComponentMethod(name, compMeth, event);
	}

	private void setCurrentPage(int p) throws TipiBreakException {
		try {
			ensureOpenConnection();
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}

		if (p < 1 || p > getPageCount() || p == currentPage) {
			return;
		}
		currentPage = p;
		try {
			injectNavajo("GetInboxMessages", createMessagesNavajo());
		} catch (TipiException e) {
			e.printStackTrace();
		}
	}

	public synchronized void connect() throws MessagingException {
		Properties props = new Properties();
		session = Session.getDefaultInstance(props, null);
		store = session.getStore(mailMode);
		myConnectionListener = new ConnectionListener() {
			public void closed(ConnectionEvent e) {
				try {
					performTipiEvent("onConnectionClosed", null, false);
					store = null;
				} catch (TipiException e1) {
					e1.printStackTrace();
				}
			}

			public void disconnected(ConnectionEvent arg0) {
				try {
					performTipiEvent("onConnectionLost", null, false);
					store = null;
				} catch (TipiException e1) {
					e1.printStackTrace();
				}
			}

			public void opened(ConnectionEvent arg0) {
				try {
					Map<String, Object> params = new HashMap<String, Object>();
					// params.put("messageCount", messageCount);
					// params.put("unreadMessageCount", unreadMessageCount);
					performTipiEvent("onConnectionCreated", params, false);
//					System.err.println("Connection created event!");
				} catch (TipiException e1) {
					e1.printStackTrace();
				}
			}
		};
		store.addConnectionListener(myConnectionListener);
		myFolderListener = new FolderListener() {

			public void folderCreated(FolderEvent arg0) {
				System.err.println("Folder created");
			}

			public void folderDeleted(FolderEvent arg0) {
				System.err.println("Folder deleted");
			}

			public void folderRenamed(FolderEvent arg0) {
				System.err.println("Folder renamed");
			}

		};
		store.addFolderListener(myFolderListener);
		myStoreListener = new StoreListener() {
			public void notification(StoreEvent ee) {
				System.err.println("Store notification: " + ee.getMessage() + " " + ee.getMessageType());
			}
		};
		store.addStoreListener(myStoreListener);
		store.connect(host, username, password);
		if ("POP3".equalsIgnoreCase(mailMode)) {
			fff = store.getFolder("INBOX");
		} else {
			fff = store.getDefaultFolder();
		}
		fff.open(Folder.READ_WRITE);
		messageCount = fff.getMessageCount();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("messageCount", messageCount);
			params.put("unreadMessageCount", unreadMessageCount);
			performTipiEvent("onFolderOpened", params, false);
		} catch (TipiBreakException e1) {
			e1.printStackTrace();
		} catch (TipiException e1) {
			e1.printStackTrace();
		}
		try {
			injectNavajo("InitGetMessages", createInitGetMessages());
		} catch (TipiBreakException e1) {
			e1.printStackTrace();
		} catch (TipiException e1) {
			e1.printStackTrace();
		}
	}

//	private int determineRecentMessages() throws MessagingException {
//		javax.mail.Message[] m = fff.getMessages();
//		int result = 0;
//		for (javax.mail.Message message : m) {
//			Date d = message.getSentDate();
//			System.err.println("Datebefore: " + recentAfter);
//			System.err.println("sent: " + message.getSentDate());
//			System.err.println("received: " + message.getReceivedDate());
//			if (d.after(recentAfter)) {
//				result++;
//			}
//		}
//		return result;
//	}

	public synchronized void disconnect() throws MessagingException {
		if (store != null) {
			store.removeConnectionListener(myConnectionListener);
			store.removeFolderListener(myFolderListener);
			store.removeStoreListener(myStoreListener);
			// Close connection
			if(fff!=null) {
				fff.close(true);
			}
			store.close();
			store = null;
		}
	}

	public Navajo createFolderNavajo() throws TipiException {
		Navajo myNavajo;
		if (store == null) {
			try {
				connect();
			} catch (MessagingException e) {
				throw new TipiException("Error connecting: " + e.getMessage(), e);
			}
		}
		try {
			Folder g = store.getDefaultFolder();
			myNavajo = NavajoFactory.getInstance().createNavajo();
			Message folderMessage = NavajoFactory.getInstance().createMessage(myNavajo, "Folders", Message.MSG_TYPE_ARRAY);
			myNavajo.addMessage(folderMessage);
			Folder[] fff = g.list();
			for (int i = 0; i < fff.length; i++) {
				addFolderMessage(folderMessage, fff[i]);
			}
			return myNavajo;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TipiException("Error getting folders: ", e);
		}
		// recursiveShow(g);
	}

	private void addFolderMessage(Message folderMessage, Folder folder) throws NavajoException, MessagingException {
		Message element = NavajoFactory.getInstance().createMessage(folderMessage.getRootDoc(), "Folders",
				Message.MSG_TYPE_ARRAY_ELEMENT);
		folderMessage.addMessage(element);
		addFolderToMessage(element, folder);
	}

	public void testMessage() throws MessagingException, TipiBreakException, TipiException {
		host = "hermes1.dexels.com";
		username = "Secretaris-BBKY84H";
		password = "wachtwoord";
		mailMode = "pop3";
		connect();
		Navajo init = createInitGetMessages();
		doTransaction(init,"GetInboxMessages");

		init.getProperty("Folder/MessageNumber").setAnyValue(3);

		doTransaction(getNavajo(), "InitGetMessages");
				
		System.err.println("Sleeping....");		
		try {
			Thread.sleep(14000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("End of sleep...");
				
		doTransaction(init, "GetMessage");
		disconnect();
		
	}

	private void addFolderToNavajo(Folder f, Navajo myNavajo, String folderName, Navajo inputNavajo) throws TipiException {
		try {
			if (inputNavajo == null) {
				System.err.println("No inputNavajo");
			}
			Message folderMessage = NavajoFactory.getInstance().createMessage(myNavajo, folderName);
			myNavajo.addMessage(folderMessage);
			addFolderToMessage(folderMessage, f);
			javax.mail.Message message[] = f.getMessages();
			Message mailMessages = NavajoFactory.getInstance().createMessage(myNavajo, "Mail", Message.MSG_TYPE_ARRAY);
			folderMessage.addMessage(mailMessages);
			Integer start = null;
			Integer end = null;

			if (pageSize != 0) {
				start = pageSize * (currentPage - 1);
				end = (pageSize * currentPage);
			} else {
				start = 0;
				end = messageCount;
			}

			for (int i = start; i < end; i++) {
				if (i >= message.length) {
					break;
				}
				if (message[messageCount - i - 1].getFlags().contains(Flag.DELETED)) {
					System.err.println("Deleted message, deleting");
					// keep on looping until the right number of messages is present.
					end++;
					continue;
				}
				addMessages(mailMessages, message[messageCount - i - 1]);
			}
//			System.err.println("Start: " + start + " end: " + end);
			// f.close(false);
		} catch (Exception e) {
			throw new TipiException("Error getting messages from folder: " + folderName + " problem: " + e.getMessage(), e);
		}
	}

	private void addMessages(Message imapFolder, javax.mail.Message currentImapMessage) throws NavajoException {
		Navajo n = imapFolder.getRootDoc();
		try {
			Message current;
			current = addMessageProperties(currentImapMessage, n);
			imapFolder.addMessage(current);
		} catch (MessagingException e) {
			System.err.println("Bad message content. Ignoring.");
			e.printStackTrace();
		}
	}

	private Message addMessageProperties(javax.mail.Message mail, Navajo n) throws NavajoException, MessagingException {
		Message current = NavajoFactory.getInstance().createMessage(n, "Mail", Message.MSG_TYPE_ARRAY_ELEMENT);
		addProperty(current, "Subject", mail.getSubject(), Property.STRING_PROPERTY);
		addProperty(current, "ContentType", mail.getContentType(), Property.STRING_PROPERTY);
		addProperty(current, "Description", mail.getDescription(), Property.STRING_PROPERTY);
		addProperty(current, "Disposition", mail.getDisposition(), Property.STRING_PROPERTY);
		addProperty(current, "FileName", mail.getFileName(), Property.STRING_PROPERTY);
		addProperty(current, "ReceivedDate", mail.getReceivedDate(), Property.DATE_PROPERTY);
		addProperty(current, "SentDate", mail.getSentDate(), Property.DATE_PROPERTY);
		addProperty(current, "MessageNumber", mail.getMessageNumber(), Property.INTEGER_PROPERTY);

		addAddressProperty(current, "From", mail.getFrom());
		addAddressProperty(current, "ReplyTo", mail.getReplyTo());
		addAddressProperty(current, "To", mail.getRecipients(javax.mail.Message.RecipientType.TO));
		addAddressProperty(current, "Cc", mail.getRecipients(javax.mail.Message.RecipientType.CC));
		addAddressProperty(current, "Bcc", mail.getRecipients(javax.mail.Message.RecipientType.BCC));
		addProperty(current, "IsAnswered", mail.isSet(Flag.ANSWERED), Property.BOOLEAN_PROPERTY);
		addProperty(current, "IsDeleted", mail.isSet(Flag.DELETED), Property.BOOLEAN_PROPERTY);
		addProperty(current, "IsDraft", mail.isSet(Flag.DRAFT), Property.BOOLEAN_PROPERTY);
		addProperty(current, "IsFlagged", mail.isSet(Flag.FLAGGED), Property.BOOLEAN_PROPERTY);
		addProperty(current, "IsRecent", mail.isSet(Flag.RECENT), Property.BOOLEAN_PROPERTY);
		addProperty(current, "IsSeen", mail.isSet(Flag.SEEN), Property.BOOLEAN_PROPERTY);

		return current;
	}

	private void addAddressProperty(Message current, String name, Address[] addressList) throws NavajoException {
		if (addressList == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < addressList.length; i++) {
			sb.append(addressList[i].toString());
			if (i != addressList.length - 1) {
				sb.append(",");
			}
		}
		addProperty(current, name, sb.toString(), name);
	}

	private void addFolderToMessage(Message folderMessage, Folder c) throws NavajoException, MessagingException {
		addProperty(folderMessage, "FolderName", c.getFullName(), Property.STRING_PROPERTY);
		addProperty(folderMessage, "MessageCount", "" + c.getMessageCount(), Property.INTEGER_PROPERTY);
		addProperty(folderMessage, "NewMessageCount", "" + c.getNewMessageCount(), Property.INTEGER_PROPERTY);
		addProperty(folderMessage, "UnreadMessageCount", "" + unreadMessageCount, Property.INTEGER_PROPERTY);
		addProperty(folderMessage, "", c.getName(), Property.STRING_PROPERTY);
	}

	private void addProperty(Message m, String name, Object value, String type) throws NavajoException {
		addProperty(m, name, value, type, Property.DIR_OUT);
	}

	private void addProperty(Message m, String name, Object value, String type, String direction) throws NavajoException {
		Navajo n = m.getRootDoc();
		Property p = NavajoFactory.getInstance().createProperty(n, name, type, null, 0, null, Property.DIR_IN);
		p.setAnyValue(value);
		m.addProperty(p);
	}

	public static void main(String[] args) throws MessagingException, TipiBreakException, TipiException {

		TipiMailConnector ttt = new TipiMailConnector();
//		ttt.sendMail(new InternetAddress("flyaruu@gmail.com"),new InternetAddress("frank@lindyhop.nl"), "ter info", "tralallaal");
		 ttt.messageCount = 5;
		 ttt.pageSize = 20;
//		 ttt.useTimeout = true;

		 ttt.testMessage();
				
		 System.err.println("# messages: "+ttt.messageCount+" pagesize: "+ttt.pageSize+" pageCount: "
		 +(double)ttt.messageCount / ttt.pageSize +" ---- "+
		 (int)(Math.ceil((double)ttt.messageCount / ttt.pageSize)));
		 

	}


	public Set<String> getEntryPoints() {
		Set<String> s = new HashSet<String>();
		s.add("InitMail");
		return s;
	}
	


}
