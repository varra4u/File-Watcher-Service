/*
 * FileWatcher - FileNotificationListener.java, Oct 6, 2012 3:28:30 PM
 * 
 * Copyright 2012 varra Ltd, Inc. All rights reserved.
 * varra proprietary/confidential. Use is subject to license terms.
 */
package com.varra.filewatcher.listener;

import com.varra.filewatcher.info.FileInfo;

import static com.varra.filewatcher.listener.WatchEventType.*;

/**
 * The Interface that emits the File Notifications to the registered users.
 * 
 * @author Rajakrishna V. Reddy
 * 
 *         TODO description go here.
 */
public interface FileNotificationListener
{

	void onWatchEvent(WatchEventType type, FileInfo fileInfo);

	/**
	 * On create file.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	default void onCreateFile(FileInfo fileInfo){
		this.onWatchEvent(CREATE, fileInfo);
	}

	/**
	 * On create directory.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	default void onCreateDirectory(FileInfo fileInfo) {this.onWatchEvent(CREATE, fileInfo);};
	
	/**
	 * On modify file.
	 * 
	 * @param oldFileInfo
	 *            the old file info
	 * @param newFileInfo
	 *            the new file info
	 */
	default void onModifyFile(FileInfo oldFileInfo, FileInfo newFileInfo) {this.onWatchEvent(MODIFY, newFileInfo);};
	
	/**
	 * On modify directory.
	 * 
	 * @param oldFileInfo
	 *            the old file info
	 * @param newFileInfo
	 *            the new file info
	 */
	default void onModifyDirectory(FileInfo oldFileInfo, FileInfo newFileInfo) {this.onWatchEvent(MODIFY, newFileInfo);};
	
	/**
	 * On delete file.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	default void onDeleteFile(FileInfo fileInfo) {this.onWatchEvent(DELETE, fileInfo);};
	
	/**
	 * On delete directory.
	 * 
	 * @param fileInfo
	 *            the file info
	 */
	default void onDeleteDirectory(FileInfo fileInfo) {this.onWatchEvent(DELETE, fileInfo);};
}
