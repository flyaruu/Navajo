package com.dexels.navajo.tipi.jabber.lockimpl;

import java.util.*;

import org.jivesoftware.smack.*;
import org.jivesoftware.smackx.muc.*;

public class JabberLockImpl extends BaseLockImpl {
	private Random generator = new Random();
	private String nick = null;
	private String preferredNick = null;
	private MultiUserChat muc = null;
	private String resource = null;
	private XMPPConnection connection;
//	public JabberLockImpl(String domain, String id) {
//		super( id);
//	}

	public void connect(String server, int port, String conferenceService, String chatDomain, String preferredNick) throws XMPPException {
		ConnectionConfiguration config = new ConnectionConfiguration(server, port, chatDomain);
		connection = new XMPPConnection(config);
		connection.connect();
		connection.loginAnonymously();
		this.preferredNick = preferredNick;
		// connection.addConnectionListener(new ConnectionListener(){});

		muc = new MultiUserChat(connection, "demoroom@" + conferenceService + "." + chatDomain);
		nick = join(muc,preferredNick);
		muc.addParticipantStatusListener(new DefaultParticipantStatusListener() {

			public void joined(String participant) {
				// doesn't matter, I think
			}

			public void left(String participant) {
				if(participant.equals(resource)) {
					// my resource!
					setLockRequest(resource);
				}

			}
			
			

			public void nicknameChanged(String participant, String name) {
				if (nick.equals(participant)) {
					// me
				} else {
//					System.err.println("Nick changed: "+participant+" name: "+name);
					if(participant.equals(resource)) {
						// my resource!
						setLockRequest(resource);
					}
				}
			}

		});
	}

	private String join(MultiUserChat muc, String preferredNick) {
		try {
			String nick = null;
			if(preferredNick!=null) {
				nick = preferredNick;
			} else {
				nick = "Participant" + generator.nextInt();
			}
			muc.join(nick);
			return nick;
		} catch (XMPPException e) {
			// taken:
			return join(muc,null);
		}
	}
	private String reset(MultiUserChat muc) {
		try {
			if(preferredNick!=null) {
				nick = preferredNick;
			} else {
				nick = "Participant" + generator.nextInt();
			}
			muc.changeNickname(nick);
			return nick;
		} catch (XMPPException e) {
			// taken:
			return reset(muc);
		}
	}
	
	public boolean isLocked() {
		if(resource==null) {
			// no lock requested.
			return false;
		}
		return muc.getNickname().equals(resource);
	}

	public boolean setLockRequest(String resource) {
		try {
//			System.err.println("Changing nick from: "+muc.getNickname()+" to "+resource);
			muc.changeNickname(resource);
			String newName = muc.getNickname();
			if(!resource.equals(newName)) {
				debug("It seemed that I am king, but I am not.");
				return false;
			}
			fireLockingChanges(true, false, "locked");
			return true;
		} catch (XMPPException e) {
			debug("Changing didn't work");
			return false;
		}
	}

	public void unlock() {
		resource = null;
		reset(muc);
		fireLockingChanges(true, false, "locked");

	}

	public void disconnect() {
		muc.leave();
		connection.disconnect();
	}

public void debug(String text) {
	if(muc!=null) {
		if(muc.isJoined()) {
			try {
				muc.sendMessage(text);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

public String getPreferredNick() {
	return preferredNick;
}

}
