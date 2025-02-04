package com.dexels.navajo.adapter;

import com.dexels.navajo.adapter.mailmap.AttachmentMapInterface;
import com.dexels.navajo.document.types.Binary;

public class DummyAttachmentMap implements AttachmentMapInterface {

	private String attachFile;
	
	public String getAttachFile() {
		return this.attachFile;
	}

	public Binary getAttachFileContent() {
		return null;
	}

	public String getAttachFileName() {
		return null;
	}

	public String getEncoding() {
		return null;
	}

	public void setAttachContentHeader(String s) {
		System.err.println("in setAttachContentHeader(" + s + ")");
	}

	public void setAttachFile(String attachFile) {
		System.err.println("in setAttachFile(" + attachFile + ")");
		this.attachFile = attachFile;
	}

	public void setAttachFileContent(Binary attachFileContent) {
		System.err.println("in setAttachFileContent(" + attachFileContent + ")");
	}

	public void setAttachFileName(String attachFileName) {
		System.err.println("in setAttachFileName(" + attachFileName + ")");
	}

	public void setEncoding(String s) {
		System.err.println("in setEncoding(" + s + ")");
	}

}
