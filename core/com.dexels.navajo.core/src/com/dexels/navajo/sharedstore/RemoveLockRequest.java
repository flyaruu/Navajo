/**
 * Title:        Navajo<p>
 * Description:  This file is part of the Navajo Service Oriented Application Framework<p>
 * Copyright:    Copyright 2002-2008 (c) Dexels BV<p>
 * Company:      Dexels<p>
 * @author Arjen Schoneveld
 * @version $Id$
 *
 * DISCLAIMER
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL DEXELS BV OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package com.dexels.navajo.sharedstore;

import com.dexels.navajo.server.DispatcherFactory;
import com.dexels.navajo.server.enterprise.tribe.Answer;
import com.dexels.navajo.server.enterprise.tribe.Request;

public class RemoveLockRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4793070731846134259L;
	
	public String parent;
	public String name;
	public String owner;
	int lockType;
	
	public RemoveLockRequest(String parent, String name) {
		this.parent = parent;
		this.name = name;
		this.owner = DispatcherFactory.getInstance().getNavajoConfig().getInstanceName();
		this.blocking = false;
	}
	
	@Override
	public Answer getAnswer() {
		
		SharedStoreInterface ssi = SharedStoreFactory.getInstance();
		SharedStoreLock ssl = ssi.getLock(parent, name, owner);
		//System.err.println("IN RemoveLockRequest(), getAnswer().....: " + ssl);
		if ( ssl != null ) {
			ssi.release(ssl);
		}
		return new LockAnswer(this, ssl);
		
	}

}
