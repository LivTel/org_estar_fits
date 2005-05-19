// FITSHeaderLoader.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSHeaderLoader.java,v 1.2 2005-05-19 19:07:39 cjm Exp $
package org.estar.fits;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.eso.fits.*;
import org.estar.astrometry.*;

/**
 * This class loads FITS image headers. The image data is <b>NOT</b> loaded.
 * @author Chris Mottram
 * @version $Revision: 1.2 $
 */
public class FITSHeaderLoader
{
	/**
	 * Revision control system version id.
	 */
	public final static String RCSID = "$Id: FITSHeaderLoader.java,v 1.2 2005-05-19 19:07:39 cjm Exp $";
	/**
	 * The FITS file we are getting the header from.
	 */
	FitsFile fitsFile = null;
	/**
	 * The FITS header object.
	 */
	FitsHeader header = null;

	/**
	 * Default constructor.
	 */
	public FITSHeaderLoader()
	{
		super();
	}

	/**
	 * Load FITS image.
	 * @param filename The filename to load from.
	 * @see #fitsFile
	 * @see #load(FitsFile)
	 */
	public void load(String filename) throws IOException,FITSException
	{
		try
		{
			fitsFile = new FitsFile(new RandomAccessFile(filename,"r"),true);
			load(fitsFile);
		}
		catch(FitsException e)
		{
			throw new FITSException(this.getClass().getName()+":load:"+e);
		}
	}

	/**
	 * Load FITS image.
	 * @param file The file to load from.
	 * @see #fitsFile
	 * @see #load(FitsFile)
	 */
	public void load(File file) throws IOException,FITSException
	{
		try
		{
			fitsFile = new FitsFile(new RandomAccessFile(file,"r"),true);
			load(fitsFile);
		}
		catch(FitsException e)
		{
			throw new FITSException(this.getClass().getName()+":load:"+e);
		}
	}

	/**
	 * Load FITS image.
	 * @param url The URL to load from.
	 * @see #fitsFile
	 * @see #load(DataInput)
	 */
	public void load(URL url) throws IOException,FITSException
	{
		DataInputStream dis = null;

		dis = new DataInputStream(new BufferedInputStream(url.openStream()));
		load(dis);
	}

	/**
	 * Load FITS image.
	 * @param di The DataInput to load from.
	 * @see #fitsFile
	 * @see #load(FitsFile)
	 */
	public void load(DataInput di) throws IOException,FITSException
	{
		try
		{
			fitsFile = new FitsFile(di,true);
			load(fitsFile);
		}
		catch(FitsException e)
		{
			throw new FITSException(this.getClass().getName()+":load:"+e);
		}
	}

	/**
	 * Get the value of the specified keyword as a string.
	 * Returns null if a header has not been loaded yet.
	 * @param keywordString The name of the keyword.
	 * @return The keyword's value as a string.
	 * @see #header
	 */
	public String getKeywordValueString(String keywordString)
	{
		FitsKeyword keyword = null;

		if(header == null)
			return null;
		keyword = header.getKeyword(keywordString);
		if(keyword == null)
			return null;
		return keyword.getString();
	}

	/**
	 * Get the value of the specified keyword as an int.
	 * Returns 0 if a header has not been loaded yet, or is not an int.
	 * @param keywordString The name of the keyword.
	 * @return The keyword's value as an int.
	 * @see #header
	 */
	public int getKeywordValueInt(String keywordString)
	{
		FitsKeyword keyword = null;

		if(header == null)
			return 0;
		keyword = header.getKeyword(keywordString);
		if(keyword == null)
			return 0;
		return keyword.getInt();
	}

	/**
	 * Get the value of the specified keyword as an double.
	 * Returns 0.0 if a header has not been loaded yet, or is not a double.
	 * @param keywordString The name of the keyword.
	 * @return The keyword's value as an double.
	 * @see #header
	 */
	public double getKeywordValueDouble(String keywordString)
	{
		FitsKeyword keyword = null;

		if(header == null)
			return 0.0;
		keyword = header.getKeyword(keywordString);
		if(keyword == null)
			return 0.0;
		return keyword.getReal();
	}

	/**
	 * Get the value of the specified keyword as a boolean.
	 * Returns false if a header has not been loaded yet, or is not a boolean.
	 * @param keywordString The name of the keyword.
	 * @return The keyword's value as an boolean.
	 * @see #header
	 */
	public boolean getKeywordValueBoolean(String keywordString)
	{
		FitsKeyword keyword = null;

		if(header == null)
			return false;
		keyword = header.getKeyword(keywordString);
		if(keyword == null)
			return false;
		return keyword.getBool();
	}

	/**
	 * Get the value of the specified keyword as a date.
	 * Returns null if a header has not been loaded yet, or is not a date.
	 * @param keywordString The name of the keyword.
	 * @return The keyword's value as a date.
	 * @see #header
	 */
	public Date getKeywordValueDate(String keywordString)
	{
		FitsKeyword keyword = null;

		if(header == null)
			return null;
		keyword = header.getKeyword(keywordString);
		if(keyword == null)
			return null;
		return keyword.getDate();
	}

	/**
	 * Method to print out a string representation of this node.
	 * @return The string.
	 * @see #toString(java.lang.String)
	 */
	public String toString()
	{
		return toString("");
	}

	/**
	 * Method to print out a string representation of this node, with a prefix.
	 * @param prefix A string to prefix to each line of data we print out.
	 * @return The string.
	 */
	public String toString(String prefix)
	{
		StringBuffer sb = null;
		FitsKeyword keyword = null;

		sb = new StringBuffer();
		if(header == null)
			return sb.toString();
		for (Enumeration e = header.getKeywords(); e.hasMoreElements();)
		{
			keyword = (FitsKeyword)(e.nextElement());
			sb.append(prefix+keyword.toString()+"\n");
		}
		return sb.toString();
	}

	// protected methods
	/**
	 * Method to load the data array from the specified FitsFile
	 * @param ff the Fits File to load.
	 * @exception FITSException Thrown if HDU type is not image, or number of axes are not 2.
	 */
	protected void load(FitsFile ff) throws FITSException
	{
		FitsHDUnit hdu = null;

		hdu = ff.getHDUnit(0);
		header = hdu.getHeader();
	}
}
/*
** $Log: not supported by cvs2svn $
** Revision 1.1  2005/05/17 14:49:17  cjm
** Initial revision
**
*/
