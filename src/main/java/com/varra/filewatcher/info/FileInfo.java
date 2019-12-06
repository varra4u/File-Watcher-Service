/*
 * FileWatcher - FileInfo.java, Oct 6, 2012 3:27:35 PM
 * 
 * Copyright 2012 varra Ltd, Inc. All rights reserved.
 * varra proprietary/confidential. Use is subject to license terms.
 */
package com.varra.filewatcher.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

/**
 * Contains the very important and most useful File details, created so with
 * very less details for memory saving. There may be thousands of these objects in realtime.
 * 
 * @author Rajakrishna V. Reddy
 * 
 *         TODO description go here.
 */
public class FileInfo implements Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4550137992012021896L;
	
	/**
	 * This abstract pathname's normalized pathname string. A normalized
	 * pathname string uses the default name-separator character and does not
	 * contain any duplicate or redundant separators.
	 * 
	 * @serial
	 */
	private final String name;
	
	/** The parent. */
	private final String parent;
	
	/** The owner. */
	private String owner;
	
	/** The last modified. */
	private final long lastModified;
	
	/** The directory. */
	private final boolean directory;
	
	/** The hidden. */
	private final boolean hidden;
	
	/** The can execute. */
	private final boolean canExecute;
	
	/** The can read. */
	private final boolean canRead;
	
	/** The can write. */
	private final boolean canWrite;
	
	/** The short name. */
	private final String shortName;
	
	/** The size. */
	private final long size;
	
	/** The backup. */
	private final boolean backup;
	
	/**
	 * Instantiates a new file info.
	 * 
	 * @param name
	 *            the name
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public FileInfo(String name) throws FileNotFoundException
	{
		super();
		
		final File file = new File(name);
		if (!file.exists())
		{
			throw new FileNotFoundException("Provided file is an invalid location.");
		}
		this.name = name;
		this.canExecute = file.canExecute();
		this.canRead = file.canRead();
		this.canWrite = file.canWrite();
		this.lastModified = file.lastModified();
		this.directory = file.isDirectory();
		this.hidden = file.isHidden();
		this.parent = file.getParent();
		this.shortName = file.getName();
		this.size = file.length();
		this.backup = name.endsWith("~") || name.toUpperCase().endsWith("BAK");
	}
	
	/**
	 * Returns the name of the file or directory denoted by this abstract
	 * pathname. This is just the last name in the pathname's name sequence. If
	 * the pathname's name sequence is empty, then the empty string is returned.
	 * 
	 * @return The name of the file or directory denoted by this abstract
	 *         pathname, or the empty string if this pathname's name sequence is
	 *         empty
	 */
	public String getName()
	{
		return shortName;
	}
	
	/**
	 * Returns the absolute pathname string of this abstract pathname.
	 * 
	 * <p>
	 * If this abstract pathname is already absolute, then the pathname string
	 * is simply returned as if by the <code>{@link #getPath}</code> method. If
	 * this abstract pathname is the empty abstract pathname then the pathname
	 * string of the current user directory, which is named by the system
	 * property <code>user.dir</code>, is returned. Otherwise this pathname is
	 * resolved in a system-dependent way. On UNIX systems, a relative pathname
	 * is made absolute by resolving it against the current user directory. On
	 * Microsoft Windows systems, a relative pathname is made absolute by
	 * resolving it against the current directory of the drive named by the
	 * pathname, if any; if not, it is resolved against the current user
	 * directory.
	 * 
	 * @return The absolute pathname string denoting the same file or directory
	 *         as this abstract pathname
	 * @see java.io.File#isAbsolute()
	 */
	public String getAbsolutePath()
	{
		return name;
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public String getOwner()
	{
		return owner;
	}
	
	/**
	 * Sets the owner.
	 * 
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner)
	{
		this.owner = owner;
	}
	
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public String getParent()
	{
		return parent;
	}
	
	/**
	 * Gets the last modified.
	 * 
	 * @return the lastModified
	 */
	public long lastModified()
	{
		return lastModified;
	}
	
	/**
	 * Checks if is the directory.
	 * 
	 * @return the directory
	 */
	public boolean isDirectory()
	{
		return directory;
	}
	
	/**
	 * Checks if is the file.
	 * 
	 * @return the file.
	 */
	public boolean isFile()
	{
		return !directory;
	}
	
	/**
	 * Checks if is the hidden.
	 * 
	 * @return the hidden
	 */
	public boolean isHidden()
	{
		return hidden;
	}
	
	/**
	 * Checks if is the can execute.
	 * 
	 * @return the canExecute
	 */
	public boolean isCanExecute()
	{
		return canExecute;
	}
	
	/**
	 * Checks if is the can read.
	 * 
	 * @return the canRead
	 */
	public boolean isCanRead()
	{
		return canRead;
	}
	
	/**
	 * Checks if is the can write.
	 * 
	 * @return the canWrite
	 */
	public boolean isCanWrite()
	{
		return canWrite;
	}
	
	/**
	 * Gets the short name.
	 * 
	 * @return the shortName
	 */
	public String getShortName()
	{
		return shortName;
	}
	
	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public long getSize()
	{
		return size;
	}
	
	/**
	 * Checks if is the backup.
	 * 
	 * @return the backup
	 */
	public boolean isBackup()
	{
		return backup;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FileInfo other = (FileInfo) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("FileInfo [name=");
		builder.append(name);
		builder.append(", parent=");
		builder.append(parent);
		builder.append(", owner=");
		builder.append(owner);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append(", directory=");
		builder.append(directory);
		builder.append(", hidden=");
		builder.append(hidden);
		builder.append(", canExecute=");
		builder.append(canExecute);
		builder.append(", canRead=");
		builder.append(canRead);
		builder.append(", canWrite=");
		builder.append(canWrite);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", size=");
		builder.append(size);
		builder.append(", backup=");
		builder.append(backup);
		builder.append("]");
		return builder.toString();
	}
}
