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
// FITSException.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSException.java,v 1.5 2007-01-30 18:34:32 cjm Exp $
package org.estar.fits;

import java.io.*;

/**
 * This class extends Exception. 
 * @author Chris Mottram
 * @version $Revision: 1.5 $
 */
public class FITSException extends Exception
{
	/**
	 * Revision Control System id string, showing the version of the Class
	 */
	public final static String RCSID = new String("$Id: FITSException.java,v 1.5 2007-01-30 18:34:32 cjm Exp $");
	/**
	 * An exception that caused this exception to be generated.
	 */
	private Exception exception = null;

	/**
	 * Constructor for the exception.
	 * @param errorString The error string.
	 */
	public FITSException(String errorString)
	{
		super(errorString);
	}

	/**
	 * Constructor for the exception.
	 * @param errorString The error string.
	 * @param e An exception that caused this exception to be generated.
	 * @see #exception
	 */
	public FITSException(String errorString,Exception e)
	{
		super(errorString);
		exception = e;
	}

	/**
	 * Retrieve method to return exception that generated this exception.
	 * @return An exception, or null.
	 * @see #exception
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 * Overridden toString method, that calls super toString and adds exception's toString to it.
	 * @see #exception
	 */
	public String toString()
	{
		StringBuffer sb = null;

		sb = new StringBuffer();
		sb.append(super.toString());
		if(exception != null)
			sb.append(exception.toString());
		return sb.toString();
	}

	/**
	 * Overridden printStackTrace, that prints the creating exceptions stack if it is non-null.
	 * NB Logger.dumpStack uses this method.
	 * @param s The writer to write to.
	 */
	public void printStackTrace(PrintWriter s)
	{
		super.printStackTrace(s);
		if(exception != null)
			exception.printStackTrace(s);
	}

	/**
	 * Overridden printStackTrace, that prints the creating exceptions stack if it is non-null.
	 * NB printStackTrace() uses this method.
	 * @param s The stream to write to.
	 */
	public void printStackTrace(PrintStream s)
	{
		super.printStackTrace(s);
		if(exception != null)
			exception.printStackTrace(s);
	}
}

//
// $Log: not supported by cvs2svn $
// Revision 1.4  2005/06/07 13:47:15  cjm
// Added printStackTrace(PrintStream).
// This is used when calling printStackTrace().
//
// Revision 1.3  2005/06/07 13:24:05  cjm
// Fixed comments.
//
// Revision 1.2  2005/06/07 13:19:14  cjm
// Changed method signature of overwritten printStackTrace.
//
// Revision 1.1  2003/03/03 11:39:32  cjm
// Initial revision
//
// Revision 1.1  2003/02/24 13:19:56  cjm
// Initial revision
//
//
