/*
 * FileWatcher - FileWatcher.java, Oct 5, 2012 1:00:19 PM
 * 
 * Copyright 2012 varra Ltd, Inc. All rights reserved.
 * varra proprietary/confidential. Use is subject to license terms.
 */
package com.varra.filewatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.varra.filewatcher.info.FileInfo;
import com.varra.filewatcher.listener.AbstractFileNotificationListener;
import com.varra.filewatcher.listener.FileNotificationListener;
import com.varra.log.Logger;
import com.varra.log.MyLogLevel;
import com.varra.util.EnhancedTimerTask;
import com.varra.util.FIFOQueue;
import com.varra.util.GlobalThread;
import com.varra.util.StringUtils;

/**
 * 
 * The entry point to start the {@link FileWatcher}, it uses singleton pattern.<br>
 * It tries to be realtime maximum, notifies as and when the things happen.Just
 * set the lowest interval value(default is 2 secs) possible to be more
 * realtime to scan your filesystem for changes.<br>
 * <br>You have to register for notifications of the files you are interested in,
 * uses asynchronous notifications. <br>
 * <br>
 * <b>Note: </b>As It uses asynchronous notifications do not use heavy code
 * snippet in {@link FileNotificationListener} implementation, it may cause slow
 * down to your application speed.
 * 
 * @see {@link AbstractFileNotificationListener} or
 *      {@link FileNotificationListener} for notification types and information
 *      that will be emitted.
 * 
 * @author Rajakrishna V. Reddy
 * @version 1.0
 * 
 */
public class FileWatcher
{
	
	/** The file watcher. */
	private static FileWatcher fileWatcher;
	
	/** The abstract file watcher. */
	private static AbstractFileWatcher abstractFileWatcher;
	
	/** The listeners. */
	private transient final Map<String, FileNotificationListener> listeners;
	
	/** The interval. */
	private int interval;
	
	/** The is running. */
	private volatile boolean isRunning;
	
	/**
	 * Instantiates a new file watcher.
	 * 
	 */
	private FileWatcher()
	{
		this.listeners = new LinkedHashMap<String, FileNotificationListener>();
		abstractFileWatcher = new AbstractFileWatcher();
		
		setInterval(2000);
	}
	
	/**
	 * Sets the interval.
	 * 
	 * <br>
	 * <br>
	 * <b>Note: </b> There will be no impact if the {@link FileWatcher} is
	 * already started.
	 * 
	 * @param interval
	 *            the new interval
	 */
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
	
	/**
	 * Gets the interval.
	 * 
	 * @return the interval
	 */
	public int getInterval()
	{
		return interval;
	}
	
	/**
	 * Gets the file watcher.
	 * 
	 * @return the file watcher
	 */
	public static synchronized FileWatcher getFileWatcher()
	{
		if (fileWatcher == null)
		{
			fileWatcher = new FileWatcher();
		}
		return fileWatcher;
	}
	
	/**
	 * Registers file notification listener and starts notifying to the given
	 * listener.
	 * 
	 * @param listener
	 *            the listener
	 * @param directory
	 *            the dir
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public synchronized void registerFileNotificationListener(FileNotificationListener listener, String directory)
			throws FileNotFoundException
	{
		abstractFileWatcher.registerFileNotificationListener(listener, directory);
	}
	
	/**
	 * UnRegisters the file notification listener and will not notify further
	 * anymore.
	 * 
	 * @param listener
	 *            the listener
	 * @param dir
	 *            the dir
	 */
	public synchronized void unRegisterFileNotificationListener(FileNotificationListener listener, String dir)
	{
		abstractFileWatcher.unRegisterFileNotificationListener(listener, dir);
	}
	
	/**
	 * Starts the {@link FileWatcher} which monitors the files and directories
	 * you have registered for notifications.
	 * 
	 * @see {@link #stop()} and {@link #shutdown()} for stopping the
	 *      {@link FileWatcher}.
	 */
	public synchronized void start()
	{
		setRunning(true);
		abstractFileWatcher.start();
	}
	
	/**
	 * Sets the running.
	 * 
	 * @param isRunning
	 *            the isRunning to set
	 */
	private void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}
	
	/**
	 * Checks if the {@link TrapReceiverImpl} is running.
	 * 
	 * @return the isRunning
	 */
	public boolean isRunning()
	{
		return isRunning;
	}
	
	/**
	 * Forces the thread to stop executing and unregisters all the listeners and
	 * stops the monitoring and notifying.
	 * 
	 * <br>
	 * <b>Note: </b>You can start the {@link FileWatcher} again using
	 * {@link #start()} method, but you have to register again for
	 * notifications.
	 */
	public synchronized void stop()
	{
		setRunning(false);
		abstractFileWatcher.stop();
	}
	
	/**
	 * Shutdowns the {@link FileWatcher} and unregisters all the listeners and
	 * stops the monitoring and notifying. <br>
	 * <br>
	 * <b>Note: </b>You can not start the {@link FileWatcher} again, that is
	 * stopped using {@link #shutdown()}, instead use {@link #stop()}.
	 */
	public void shutdown()
	{
		setRunning(false);
		abstractFileWatcher.shutdown();
	}
	
	/**
	 * 
	 * The Class AbstractFileWatcher which actually implements the
	 * {@link FileWatcher} logic.
	 * 
	 * @author Rajakrishna V. Reddy
	 * @version 1.0
	 * 
	 */
	class AbstractFileWatcher extends EnhancedTimerTask
	{
		
		/** The Constant DIRECTORIES_AND_FILES. */
		protected static final boolean DIRECTORIES_AND_FILES = true;
		
		/** The dirs monitored. */
		private final transient FIFOQueue<FileInfo> dirsMonitored;
		
		/** The files. */
		private final transient FIFOQueue<FileInfo> filesMonitored;
		
		/** The modified files. */
		private final transient FIFOQueue<FileInfo> modifiedFiles;
		
		/** The created files. */
		private final transient FIFOQueue<FileInfo> createdFiles;
		
		/** The deleted files. */
		private final transient FIFOQueue<FileInfo> deletedFiles;
		
		/** The temp files. */
		private final transient FIFOQueue<FileInfo> tempFiles;
		
		/** The all filter. */
		private final FilenameFilter allFilter;
		
		private final GlobalThread thread;
		
		/**
		 * Instantiates a new file watcher.
		 * 
		 */
		private AbstractFileWatcher()
		{
			super(FileWatcher.class.getSimpleName());
			
			this.dirsMonitored = new FIFOQueue<FileInfo>();
			this.filesMonitored = new FIFOQueue<FileInfo>();
			this.createdFiles = new FIFOQueue<FileInfo>();
			this.deletedFiles = new FIFOQueue<FileInfo>();
			this.modifiedFiles = new FIFOQueue<FileInfo>();
			this.tempFiles = new FIFOQueue<FileInfo>();
			this.thread = GlobalThread.getGlobalThread(1);
			thread.start();
			
			allFilter = new FilenameFilter()
			{
				
				public boolean accept(File dir, String name)
				{
					return DIRECTORIES_AND_FILES;
				}
			};
		}
		
		/**
		 * Causes this thread to begin execution;.
		 */
		private void start()
		{
			setPeriodic(true);
			setDaemon(true);
			setPeriod(getInterval());
			
			thread.onTimerTask(this);
		}
		
		/**
		 * Forces the thread to stop executing.
		 */
		private synchronized void stop()
		{
			/** Cancels the present . */
			cancel();
			
			/** Clears all the containers to free up the memory. */
			dirsMonitored.clear();
			filesMonitored.clear();
			modifiedFiles.clear();
			createdFiles.clear();
			deletedFiles.clear();
			tempFiles.clear();
		}
		
		/**
		 * Shutdowns the {@link FileWatcher} and unregisters all the listeners
		 * and stops the monitoring and notifying.
		 */
		private void shutdown()
		{
			this.stop();
			
			/** Stops the actual background thread. */
			thread.shutdown();
		}
		
		/**
		 * Registers file notification listener and starts notifying to the
		 * given listener.
		 * 
		 * @param listener
		 *            the listener
		 * @param directory
		 *            the dir
		 * @throws FileNotFoundException
		 *             the file not found exception
		 */
		public synchronized void registerFileNotificationListener(FileNotificationListener listener, String directory)
				throws FileNotFoundException
		{
			if (StringUtils.isNotBlank(directory))
			{
				final FileInfo fileInfo = new FileInfo(directory);
				boolean subDirectory = Boolean.FALSE;
				listeners.put(directory, listener);
				for (FileInfo info : dirsMonitored)
				{
					if (info.getAbsolutePath().equals(directory))
					{
						// Already directory exists, and under monitoring.!
						subDirectory = Boolean.TRUE;
					}
					else if (directory.startsWith(info.getAbsolutePath()))
					{
						// Directory is a sub directory of the exists, and under
						// monitoring.!
						subDirectory = Boolean.TRUE;
					}
					else if (info.getAbsolutePath().startsWith(directory))
					{
						// The directory is a parent or grandpa of exists, so
						// deleting the subs.
						dirsMonitored.remove(info);
					}
				}
				if (!subDirectory)
				{
					dirsMonitored.push(fileInfo);
				}
			}
		}
		
		/**
		 * UnRegisters the file notification listener and will not notify
		 * further anymore.
		 * 
		 * @param listener
		 *            the listener
		 * @param dir
		 *            the dir
		 */
		public synchronized void unRegisterFileNotificationListener(FileNotificationListener listener, String dir)
		{
			if (StringUtils.isNotBlank(dir) && listener != null)
			{
				listeners.remove(dir);
			}
		}
		
		/**
		 * Update all the files based on the status.
		 */
		protected void update()
		{
			for (FileInfo oldFileInfo : modifiedFiles)
			{
				updateOnModify(oldFileInfo, filesMonitored.get(filesMonitored.indexOf(oldFileInfo)));
			}
			for (FileInfo fileInfo : deletedFiles)
			{
				updateOnDelete(fileInfo);
			}
			for (FileInfo fileInfo : createdFiles)
			{
				updateOnCreate(fileInfo);
			}
		}
		
		/**
		 * Update on create.
		 * 
		 * @param fileInfo
		 *            the file info
		 */
		private void updateOnCreate(FileInfo fileInfo)
		{
			for (Entry<String, FileNotificationListener> listener : listeners.entrySet())
			{
				synchronized (listeners)
				{
					/** To restrict the parent file notifications to child registrar */
					if (fileInfo.getAbsolutePath().contains(listener.getKey()))
					{
						if (fileInfo.isDirectory())
						{
							listener.getValue().onCreateDirectory(fileInfo);
						}
						else
						{
							listener.getValue().onCreateFile(fileInfo);
						}
					}
				}
			}
		}
		
		/**
		 * Update on delete.
		 * 
		 * @param fileInfo
		 *            the file info
		 */
		private void updateOnDelete(FileInfo fileInfo)
		{
			for (Entry<String, FileNotificationListener> listener : listeners.entrySet())
			{
				synchronized (listeners)
				{
					/** To restrict the parent file notifications to child registrar */
					if (fileInfo.getAbsolutePath().contains(listener.getKey()))
					{
						if (fileInfo.isDirectory())
						{
							listener.getValue().onDeleteDirectory(fileInfo);
						}
						else
						{
							listener.getValue().onDeleteFile(fileInfo);
						}
					}
				}
			}
		}
		
		/**
		 * Update on modify.
		 * 
		 * @param oldFileInfo
		 *            the old file info
		 * @param newFileInfo
		 *            the new file info
		 */
		private void updateOnModify(FileInfo oldFileInfo, FileInfo newFileInfo)
		{
			for (Entry<String, FileNotificationListener> listener : listeners.entrySet())
			{
				synchronized (listeners)
				{
					/** To restrict the parent file notifications to child registrar */   
					if (oldFileInfo.getAbsolutePath().contains(listener.getKey()))
					{
						if (oldFileInfo.isDirectory())
						{
							listener.getValue().onModifyDirectory(oldFileInfo, newFileInfo);
						}
						else
						{
							listener.getValue().onModifyFile(oldFileInfo, newFileInfo);
						}
					}
				}
			}
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.varra.util.EnhancedTimerTask#run()
		 */
		@Override
		public void compute()
		{
			for (FileInfo fileInfo : dirsMonitored)
			{
				loadFiles(new File(fileInfo.getAbsolutePath()));
			}
			deletedFiles.addAll(filesMonitored);
			filesMonitored.clear();
			filesMonitored.addAll(tempFiles);
			// filesMonitored.addAll(modifiedFiles);
			filesMonitored.addAll(createdFiles);
			
			update();
			
			/*
			 * System.out.println("deleted: " + deletedFiles);
			 * System.out.println("created: " + createdFiles);
			 * System.out.println("modified: " + modifiedFiles);
			 */
			deletedFiles.clear();
			createdFiles.clear();
			modifiedFiles.clear();
			tempFiles.clear();
		}
		
		/**
		 * Scan and load files from the given parent directory.
		 * 
		 * @param directory
		 *            the directory
		 */
		protected void loadFiles(File directory)
		{
			if (directory.isDirectory())
			{
				final File[] files = directory.listFiles(allFilter);
				for (final File file : files)
				{
					loadFiles(file);
				}
				
				/** To add directories to the notified list. */
				addToMonitoredFiles(directory.getAbsolutePath());
			}
			else
			{
				/** To add leaf files to the notified list. */
				addToMonitoredFiles(directory.getAbsolutePath());
			}
		}
		
		/**
		 * Adds the to monitored files.
		 * 
		 * @param absolutePath
		 *            the absolute path
		 */
		private void addToMonitoredFiles(String absolutePath)
		{
			try
			{
				final FileInfo newFileInfo = new FileInfo(absolutePath);
				if (!newFileInfo.isBackup())
				{
					final int index = filesMonitored.indexOf(newFileInfo);
					if (index != -1)
					{
						final FileInfo existingFileInfo = filesMonitored.get(index);
						if (existingFileInfo.lastModified() == newFileInfo.lastModified())
						{
							tempFiles.push(existingFileInfo);
						}
						else
						{
							modifiedFiles.push(existingFileInfo);
							tempFiles.push(newFileInfo);
						}
						filesMonitored.remove(existingFileInfo);
					}
					else
					{
						createdFiles.push(newFileInfo);
					}
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void badSwap(Integer var1, int var2)
	{
		//System.out.println(var1+", "+var2);
		var1 = 100;
		
	 // System.out.println(var1+", "+var2);
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static void main(String[] args) throws FileNotFoundException, InterruptedException
	{
		Logger.getLogger(FileWatcher.class).setLevel(MyLogLevel.INFO);
		final FileWatcher watcher = FileWatcher.getFileWatcher();
		watcher.setInterval(5000);
		watcher.registerFileNotificationListener(new AbstractFileNotificationListener(), "/home/krishna/Download/SpringMVCForm");
		watcher.start();
		
		int var1 = 5;
		int var2 = 10;
		
		System.out.println(var1+", "+var2);
		badSwap(var1, var2);
		System.out.println(var1+", "+var2);
	}
}
