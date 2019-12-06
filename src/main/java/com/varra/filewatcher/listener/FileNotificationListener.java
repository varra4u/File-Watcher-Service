/*
 * FileWatcher - FileNotificationListener.java, Oct 6, 2012 3:28:30 PM
 * 
 * Copyright 2012 varra Ltd, Inc. All rights reserved.
 * varra proprietary/confidential. Use is subject to license terms.
 */
package com.varra.filewatcher.listener;

import com.varra.filewatcher.info.FileInfo;

/**
 * The Interface that emits the File Notifications to the registered users.
 * 
 * @author Rajakrishna V. Reddy
 * 
 *         TODO description go here.
 */
public interface FileNotificationListener
{
	
	/**
	 * On create file.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	void onCreateFile(FileInfo fileInfo);
	
	/**
	 * On create directory.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	void onCreateDirectory(FileInfo fileInfo);
	
	/**
	 * On modify file.
	 * 
	 * @param oldFileInfo
	 *            the old file info
	 * @param newFileInfo
	 *            the new file info
	 */
	void onModifyFile(FileInfo oldFileInfo, FileInfo newFileInfo);
	
	/**
	 * On modify directory.
	 * 
	 * @param oldFileInfo
	 *            the old file info
	 * @param newFileInfo
	 *            the new file info
	 */
	void onModifyDirectory(FileInfo oldFileInfo, FileInfo newFileInfo);
	
	/**
	 * On delete file.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	void onDeleteFile(FileInfo fileInfo);
	
	/**
	 * On delete directory.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	void onDeleteDirectory(FileInfo fileInfo);
}
