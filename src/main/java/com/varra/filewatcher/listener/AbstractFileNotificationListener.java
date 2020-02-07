/*
 * FileWatcher - AbstractFileNotificationListener.java, Oct 6, 2012 3:28:35 PM
 * 
 * Copyright 2012 varra Ltd, Inc. All rights reserved.
 * varra proprietary/confidential. Use is subject to license terms.
 */
package com.varra.filewatcher.listener;

import com.varra.classification.InterfaceAudience;
import com.varra.classification.InterfaceStability;
import com.varra.filewatcher.info.FileInfo;

/**
 * An abstract file notification listener which consoles the notifications
 * blindly. Has to be extended to handle the notifications properly.
 * 
 * @author Rajakrishna V. Reddy
 * @version 1.0
 * 
 */
@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AbstractFileNotificationListener implements FileNotificationListener
{
	/**
	 * Instantiates a new abstract file notification listener.
	 */
	public AbstractFileNotificationListener()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onWatchEvent(WatchEventType type, FileInfo fileInfo) {
		System.out.println("On watch event, type: "+type+", File: " + fileInfo);
	}
}
