/*
 * FileWatcher - FileWatcher.java, Oct 5, 2012 1:00:19 PM
 * 
 * Copyright 2012 varra Ltd, Inc. All rights reserved.
 * varra proprietary/confidential. Use is subject to license terms.
 */
package com.varra.filewatcher;

import com.varra.filewatcher.info.FileInfo;
import com.varra.filewatcher.listener.AbstractFileNotificationListener;
import com.varra.filewatcher.listener.FileNotificationListener;
import com.varra.util.EnhancedTimerTask;
import com.varra.util.FIFOQueue;
import com.varra.util.GlobalThread;
import com.varra.util.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

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
 * @author Rajakrishna V. Reddy
 * @version 1.0
 * 
 */
@Builder
@AllArgsConstructor
public class FileWatcher
{
	
	/** The file watcher. */
	private static FileWatcher fileWatcher;
	
	/** The abstract file watcher. */
	@Getter(AccessLevel.NONE)
	private final AbstractFileWatcher abstractFileWatcher = new AbstractFileWatcher();
	
	/** The listeners. */
	@Getter(AccessLevel.NONE)
	private transient final Map<String, FileNotificationListener> listeners = new LinkedHashMap<>();
	
	/** The interval in milli seconds. */
	private long interval = 2000;

	private boolean initialScanNotificationRequired;
	
	/**
	 * Instantiates a new file watcher.
	 * 
	 */
	private FileWatcher()
	{
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
	public synchronized FileWatcher registerListener(FileNotificationListener listener, String directory)
			throws FileNotFoundException
	{
		abstractFileWatcher.registerFileNotificationListener(listener, directory);
		return this;
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
	public synchronized void unRegisterListener(FileNotificationListener listener, String dir)
	{
		abstractFileWatcher.unRegisterFileNotificationListener(listener, dir);
	}
	
	/**
	 * Starts the {@link FileWatcher} which monitors the files and directories
	 * you have registered for notifications.
	 */
	public synchronized FileWatcher start()
	{
		abstractFileWatcher.start();
		return this;
	}
	
	/**
	 * Checks if the {@link FileWatcher} is running.
	 * 
	 * @return the isRunning
	 */
	public boolean isRunning()
	{
		return this.abstractFileWatcher.isCanceled();
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
	public synchronized FileWatcher stop()
	{
		abstractFileWatcher.stop();
		return this;
	}
	
	/**
	 * Shutdowns the {@link FileWatcher} and unregisters all the listeners and
	 * stops the monitoring and notifying. <br>
	 * <br>
	 * <b>Note: </b>You can not start the {@link FileWatcher} again, that is
	 * stopped using shutdown, instead use {@link #stop()}.
	 */
	public void shutdown()
	{
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
		
		private GlobalThread thread;

		private boolean isFirstScan = true;

		/**
		 * Instantiates a new file watcher.
		 * 
		 */
		private AbstractFileWatcher()
		{
			super(FileWatcher.class.getSimpleName());
			
			this.dirsMonitored = new FIFOQueue<>();
			this.filesMonitored = new FIFOQueue<>();
			this.createdFiles = new FIFOQueue<>();
			this.deletedFiles = new FIFOQueue<>();
			this.modifiedFiles = new FIFOQueue<>();
			this.tempFiles = new FIFOQueue<>();
			
			allFilter = (dir, name) -> DIRECTORIES_AND_FILES;
		}
		
		/**
		 * Causes this thread to begin execution;.
		 */
		private void start()
		{
			this.thread = GlobalThread.getGlobalThread(1);
			thread.start();

			setPeriodic(true);
			setDaemon(true);
			setPeriod(interval);
			
			thread.onTimerTask(this);
		}
		
		/**
		 * Forces the thread to stop executing.
		 */
		private synchronized void stop()
		{
			/* Cancels the present . */
			cancel();

			/* Clears all the containers to free up the memory. */
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

			/* Stops the actual background thread. */
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
					// To restrict the parent file notifications to child registrar
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
					// To restrict the parent file notifications to child registrar
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
					// To restrict the parent file notifications to child registrar
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

			if (initialScanNotificationRequired || !isFirstScan) {
				update();
			}
			isFirstScan = false;

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
			// To add directories to the notified list.
			if (directory.isDirectory())
			{
				final File[] files = directory.listFiles(allFilter);
				if (nonNull(files))
				{
					for (final File file : files)
					{
						loadFiles(file);
					}
				}

			}
			// To add leaf files to the notified list.
			addToMonitoredFiles(directory.getAbsolutePath());
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
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		final FileWatcher watcher = FileWatcher.builder().interval(5000).build()
				.registerListener(new AbstractFileNotificationListener(), "\\varra\\code\\own\\files-to-be-monitored")
				.start();

		TimeUnit.SECONDS.sleep(100);
		watcher.shutdown();
	}
}
