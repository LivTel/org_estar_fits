// FITSException.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSException.java,v 1.2 2005-06-07 13:19:14 cjm Exp $
package org.estar.fits;

import java.io.*;

/**
 * This class extends Exception. 
 * @author Chris Mottram
 * @version $Revision: 1.2 $
 */
public class FITSException extends Exception
{
	/**
	 * Revision Control System id string, showing the version of the Class
	 */
	public final static String RCSID = new String("$Id: FITSException.java,v 1.2 2005-06-07 13:19:14 cjm Exp $");
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
	 * @param exception An exception that caused this exception to be generated.
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
	 * Overridden toString method, that calls super toString and adds errorString to it.
	 * @see #errorString
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
}

//
// $Log: not supported by cvs2svn $
// Revision 1.1  2003/03/03 11:39:32  cjm
// Initial revision
//
// Revision 1.1  2003/02/24 13:19:56  cjm
// Initial revision
//
//
