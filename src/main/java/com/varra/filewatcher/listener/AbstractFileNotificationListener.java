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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mt.filewatcher.listener.FileNotificationListener#onCreateFile(com
	 * .mt.filewatcher.info.FileInfo)
	 */
	public void onCreateFile(FileInfo fileInfo)
	{
		System.out.println("Created File: " + fileInfo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mt.filewatcher.listener.FileNotificationListener#onCreateDirectory
	 * (com.mt.filewatcher.info.FileInfo)
	 */
	public void onCreateDirectory(FileInfo fileInfo)
	{
		System.out.println("Created Dir: " + fileInfo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mt.filewatcher.listener.FileNotificationListener#onModifyFile(com
	 * .mt.filewatcher.info.FileInfo, com.mt.filewatcher.info.FileInfo)
	 */
	public void onModifyFile(FileInfo oldFileInfo, FileInfo newFileInfo)
	{
		System.out.println("Modified file: " + oldFileInfo + ", new: " + newFileInfo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mt.filewatcher.listener.FileNotificationListener#onModifyDirectory
	 * (com.mt.filewatcher.info.FileInfo, com.mt.filewatcher.info.FileInfo)
	 */
	public void onModifyDirectory(FileInfo oldFileInfo, FileInfo newFileInfo)
	{
		System.out.println("Modified Dir: " + oldFileInfo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mt.filewatcher.listener.FileNotificationListener#onDeleteFile(com
	 * .mt.filewatcher.info.FileInfo)
	 */
	public void onDeleteFile(FileInfo fileInfo)
	{
		System.out.println("Deleted file: " + fileInfo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mt.filewatcher.listener.FileNotificationListener#onDeleteDirectory
	 * (com.mt.filewatcher.info.FileInfo)
	 */
	public void onDeleteDirectory(FileInfo fileInfo)
	{
		System.out.println("Deleted Dir: " + fileInfo);
	}
}
