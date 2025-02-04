/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.dexels.navajo.tipi.ant.projectbuilder;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.tools.ant.BuildException;


/**
 * Ant task that implements the <code>/deploy</code> command, supported by
 * the Tomcat manager application.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */
public class DeployTask extends AbstractCatalinaTask {


    // ------------------------------------------------------------- Properties


    /**
     * URL of the context configuration file for this application, if any.
     */
    protected String config = null;

    public String getConfig() {
        return (this.config);
    }

    public void setConfig(String config) {
        this.config = config;
    }


    /**
     * URL of the server local web application archive (WAR) file 
     * to be deployed.
     */
    protected String localWar = null;

    public String getLocalWar() {
        return (this.localWar);
    }

    public void setLocalWar(String localWar) {
        this.localWar = localWar;
    }


    /**
     * The context path of the web application we are managing.
     */
    protected String application = null;

    public String getApplication() {
        return (this.application);
    }

    public void setApplication(String application) {
        this.application = application;
    }


    /**
     * Tag to associate with this to be deployed webapp.
     */
    protected String tag = null;

    public String getTag() {
        return (this.tag);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    /**
     * Update existing webapps.
     */
    protected boolean update = false;

    public boolean getUpdate() {
        return (this.update);
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }


    /**
     * URL of the web application archive (WAR) file to be deployed.
     */
    protected String zip = null;

    public String getZip() {
        return (this.zip);
    }

    public void setZip(String zip) {
        this.zip = zip;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Execute the requested operation.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {

        super.execute();
        if (getApplication() == null) {
            throw new BuildException
                ("Must specify 'application' attribute");
        }
        if ((zip == null) && (localWar == null) && (config == null) && (tag == null)) {
            throw new BuildException
                ("Must specify either 'war', 'localWar', 'config', or 'tag' attribute");
        }

        // Building an input stream on the WAR to upload, if any
        BufferedInputStream stream = null;
        String contentType = null;
        int contentLength = -1;
        if (zip != null) {
            if (zip.startsWith("file:")) {
                try {
                    URL url = new URL(zip);
                    URLConnection conn = url.openConnection();
                    contentLength = conn.getContentLength();
                    stream = new BufferedInputStream
                        (conn.getInputStream(), 1024);
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            } else {
                try {
                    stream = new BufferedInputStream
                        (new FileInputStream(zip), 1024);
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            }
            contentType = "application/octet-stream";
        }

        // Building URL
        StringBuffer sb = new StringBuffer("?cmd=uploaddirect&app=");
        try {
            sb.append(URLEncoder.encode(this.application, getCharset()));
            if ((zip == null) && (config != null)) {
                sb.append("&config=");
                sb.append(URLEncoder.encode(config, getCharset()));
            }
            if ((zip == null) && (localWar != null)) {
                sb.append("&war=");
                sb.append(URLEncoder.encode(localWar, getCharset()));
            }
            if (update) {
                sb.append("&update=true");
            }
            if (tag != null) {
                sb.append("&tag=");
                sb.append(URLEncoder.encode(tag, getCharset()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new BuildException("Invalid 'charset' attribute: " + getCharset());
        }

        execute(sb.toString(), stream, contentType, contentLength);

    }

    public static void main(String[] args) {
   	 DeployTask d = new DeployTask();
   	 d.setUsername("ad");
   	 d.setPassword("pw");
   	 d.setApplication("kablam");
//   	 TipiAdminServlet?cmd=upload&amp;
   	 d.setZip("/Users/frank/Documents/Spiritus/TipiAntBuild/deploy.zip");
   	 d.setUrl("http://localhost:8080/TipiServer/TipiAdminServlet");
   	 d.execute();
    }

}
