package com.dexels.navajo.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.dexels.navajo.server.Access;
import com.dexels.navajo.server.GenericThread;
import com.dexels.navajo.server.jmx.JMXHelper;
import com.dexels.navajo.util.AuditLog;

/**
 * <p>Title: Navajo Product Project</p>
 * <p>Description: This is the official source for the Navajo server</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Dexels BV</p>
 * @author Arjen Schoneveld
 * @version $Id$
 *
 * This (singleton) object is used to store instances of (active) asynchronous mappable objects.
 * An instance is de-activated if the store() or kill() methods is called or if a certain time-out is reached.
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

@SuppressWarnings("unchecked")
public final class AsyncStore extends GenericThread implements AsyncStoreMXBean {

  private static final String VERSION = "$Id$";
	
  private static volatile AsyncStore instance = null;
  
  public Map objectStore = null;
  public Map accessStore = null;
  private float timeout;
  private static int threadWait = 2000;
  private static final String id = "Navajo AsyncStore";
  
  private static Object semaphore = new Object();
  
  public AsyncStore() {
		super(id);
  }
  
  /**
   * Get the singleton AsyncStore object instance.
   *
   * @return
   */
  public final static AsyncStore getInstance() {
    return instance;
  }

  /**
   * Get the singleton AsyncStore object instance given an async inactive timeout.
   *
   * @param timeout
   * @return
   */
  public final static AsyncStore getInstance(float timeout) {
	
	  /** Make sure new instance determination is thread safe */
	  synchronized ( semaphore ) {
		  if (instance == null) {
			  instance = new AsyncStore();
			  JMXHelper.registerMXBean(instance, JMXHelper.NAVAJO_DOMAIN, id);
			  instance.timeout = timeout;
			  instance.objectStore = Collections.synchronizedMap(new HashMap());
			  instance.accessStore = Collections.synchronizedMap(new HashMap());
			  instance.setSleepTime(threadWait);
			  instance.startThread(instance);
		  }
	  }
    return instance;
  }

  /**
   * Start the main AsyncStore loop.
   */
  public final void worker() {
	  
	  synchronized (instance) {
		  Set s = new HashSet(objectStore.keySet());
		  Iterator iter = s.iterator();
		  while (iter.hasNext()) {
			  String ref = (String) iter.next();
			  AsyncMappable a = (AsyncMappable) objectStore.get(ref);
			  long now = System.currentTimeMillis();
			  if ( (now - a.getLastAccess()) > timeout ) {
				  a.kill();
				  objectStore.remove(ref);
				  accessStore.remove(ref);
				  a = null;
			  }
		  }
	  }
  }

  /**
   * Add an asynchronous mappable object and it's service access object.
   *
   * @param o
   * @param a
   * @return the unique reference to the async map
   */
  public final String addInstance(AsyncMappable o, Access a) {
    String ref = o.hashCode() + "";
    objectStore.put(ref + "", o);
    accessStore.put(ref + "", a);
    // Register Async mappable as MXBean.
    // JMXHelper.registerMXBean(o, JMXHelper.ASYNC_DOMAIN, o.getClassName() + "-" + ref);
    return ref;
  }

  /**
       * Get the access object of a async map's service given it's unique reference.
   *
   * @param ref
   * @return
   */
  public final Access getAccessObject(String ref) {
    Object o = accessStore.get(ref);
    if (o == null) {
      return null;
    }
    else {
      return (Access) o;
    }
  }

  /**
   * Get the async mappable object given it's unique reference.
   *
   * @param ref
   * @return
   */
  public final AsyncMappable getInstance(String ref) {
    Object o = objectStore.get(ref);
    if (o == null) {
      return null;
    }
    else {
      // Always  set lastaccess timestamp when retrieving asyncmappable object.
      ((AsyncMappable) o).setLastAccess();
      return (AsyncMappable) o;
    }
  }

  /**
   * Remove as async map ansd it's service access object given it's unique reference.
   *
   * @param ref
   */
  public final synchronized void removeInstance(String ref) {
//	  System.err.println("About to remove async instance: " + ref);
	  Object o = objectStore.get(ref);

	  if (o == null) {
		  return;
	  }
	  else {
		  objectStore.remove(ref);
		  if (accessStore.containsKey(ref)) {
			  accessStore.remove(ref);
		  }
		  o = null;
	  }
  }
  
  public String getVERSION() {
	  return VERSION;
  }
  
  public int getStoreSize() {
	  return objectStore.size();
  }
  
  private static final synchronized void resetInstance() {
	  instance = null;
  }
  
  public void terminate() {
	  // Removed all async mappable instances.
	  for (Iterator iter = objectStore.values().iterator(); iter.hasNext();) {
		  AsyncMappable element = (AsyncMappable) iter.next();
		  try {
			  element.setKill(true);
		  } catch (Throwable e) {
			  e.printStackTrace(System.err);
		  }
	  }
	  objectStore.clear();
	  // Remove all Access objects.
	  accessStore.clear();
	  try {
		  JMXHelper.deregisterMXBean(JMXHelper.NAVAJO_DOMAIN, id);
	  } catch (Throwable e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  resetInstance();
	  AuditLog.log(AuditLog.AUDIT_MESSAGE_ASYNC_RUNNER, "Killed");
  }

}