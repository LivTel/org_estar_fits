/*   
    Copyright 2006, Astrophysics Research Institute, Liverpool John Moores University.

    This file is part of org.estar.fits.

    org.estar.fits is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    org.estar.fits is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with org.estar.fits; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
// FITSHeaderLoader.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSHeaderLoader.java,v 1.5 2007-01-30 18:34:33 cjm Exp $
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
 * @version $Revision$
 */
public class FITSHeaderLoader
{
	/**
	 * Revision control system version id.
	 */
	public final static String RCSID = "$Id$";
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
			fitsFile = new FitsFile(new RandomAccessFile(filename,"r"),false);
			load(fitsFile);
		}
		catch(FitsException e)
		{
			throw new FITSException(this.getClass().getName()+":load:"+e,e);
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
			throw new FITSException(this.getClass().getName()+":load:"+e,e);
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
			throw new FITSException(this.getClass().getName()+":load:"+e,e);
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

	/**
	 * Test main method.
	 * @param args The command line arguments.
	 */
	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.err.println("java org.estar.fits.FITSHeaderLoader <fits filename>");
			System.exit(1);
		}
		FITSHeaderLoader fhl = null;
		fhl = new FITSHeaderLoader();
		try
		{
			fhl.load(args[0]);
		}
		catch(Exception e)
		{
			System.err.println("FITSHeaderLoader failed:"+e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println(fhl.toString());
		System.exit(0);
	}
}
/*
** $Log: not supported by cvs2svn $
** Revision 1.4  2005/06/07 15:21:31  cjm
** Added new main test program.
**
** Revision 1.3  2005/06/03 10:29:50  cjm
** Load exception handling now passes in FitsException.
**
** Revision 1.2  2005/05/19 19:07:39  cjm
** Added load(File).
**
** Revision 1.1  2005/05/17 14:49:17  cjm
** Initial revision
**
*/
