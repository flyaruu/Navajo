/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.dexels.navajo.tipi.swing.svg;

/**
 * This interface represents a listener to the SVGDocumentLoaderEvent events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SvgDocumentListener {

    /**
     * Called when the loading of a document was started.
     */
	public void onDocumentLoadingStarted();

    /**
     * Called when the loading of a document was completed.
     */
	public void onDocumentLoadingFinished();

    /**
     * Called when the loading of a document was cancelled.
     */
	public void onDocumentLoadingCancelled();

    /**
     * Called when the loading of a document has failed.
     */
	public void onDocumentLoadingFailed();
}
