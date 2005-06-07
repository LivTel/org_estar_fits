// FITSHeaderParser.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSHeaderParser.java,v 1.3 2005-06-07 13:44:06 cjm Exp $
package org.estar.fits;

import java.util.*;
import org.eso.fits.*;

/**
 * This class parses FITS headers. It expects a string of the form returned in an RTML document, where each
 * keyword is on a separate line, but is not padded to the full 80 character width.
 * @author Chris Mottram
 * @version $Revision: 1.3 $
 */
public class FITSHeaderParser
{
	/**
	 * Revision control system version id.
	 */
	public final static String RCSID = "$Id: FITSHeaderParser.java,v 1.3 2005-06-07 13:44:06 cjm Exp $";
	/**
	 * The FITS header instance.
	 */
	protected FitsHeader fitsHeader = null;

	/**
	 * Default constructor.
	 */
	public FITSHeaderParser()
	{
		super();
	}

	/**
	 * Method to parse the header. 
	 * <ul>
	 * <li>The string is tokenised by newline.
	 * <li>Each line is padded with spaces to 80 bytes.
	 * <li>A new instance of FitsKeyword is created with the padded string.
	 * <li>The results are added to fitsHeader.
	 * </ul>
	 * @param fitsHeaderString A string containing a FITS header.
	 * @exception FITSException Thrown if a FITS card cannot be parsed.
	 * @see #fitsHeader
	 */
	public void parse(String fitsHeaderString) throws FITSException
	{
		FitsKeyword fitsKeyword = null;
		StringTokenizer st = null;
		String fitsCardString = null;
		StringBuffer sb = null;
		boolean done = false;

		fitsHeader = new FitsHeader();
		done = false;
		st = new StringTokenizer(fitsHeaderString,"\n");
		while(st.hasMoreTokens())
		{
			// get next vard
			fitsCardString = st.nextToken();
			// pad card to 80 bytes
			sb = new StringBuffer(fitsCardString);
			while(sb.length() < 80)
				sb.append(" ");
			fitsCardString = sb.toString();
			// don't parse the END keyword - this throws the exception:
			// org.eso.fits.FitsException: END card
			if(fitsCardString.startsWith("END ") == false)
			{
				// create fits keyword
				try
				{
					fitsKeyword = new FitsKeyword(fitsCardString);
				}
				catch(FitsException e)
				{
					throw new FITSException(this.getClass().getName()+":parse:Failed to parse "+
								fitsCardString,e);
				}
				// add keyword to header list
				fitsHeader.addKeyword(fitsKeyword);
			}// end if not END
		}
	}

	/**
	 * Get the number of keywords in the header.
	 * @return The number of keywords.
	 * @see #fitsHeader
	 */
	public int getKeywordCount()
	{
		return fitsHeader.getNoKeywords();
	}

	/**
	 * Get an enumeration of keywords in the header.
	 * @return An enumeration keywords.
	 * @see #fitsHeader
	 */
	public Enumeration getKeywords()
	{
		return fitsHeader.getKeywords();
	}

	/**
	 * Get the value of the specified keyword.
	 * @param keyword A string representing the keyword name.
	 * @return An object representing the keyword's value. Can be of class: Boolean,String,Date,Integer,Double,
	 *         or null if no value.
	 * @see #fitsHeader
	 */
	public Object getKeywordValue(String keyword)
	{
		FitsKeyword fitsKeyword = null;
		String s = null;
		Boolean bool = null;
		Integer intObject = null;
		Double doubleObject = null;
		boolean b;
		int i;
		double d;

		fitsKeyword = fitsHeader.getKeyword(keyword);
		switch(fitsKeyword.getType())
		{
		case FitsKeyword.BOOLEAN:
			b = fitsKeyword.getBool();
			bool = new Boolean(b);
			return bool;
		case FitsKeyword.COMMENT:
			return fitsKeyword.getString();
		case FitsKeyword.DATE:
			return fitsKeyword.getDate();
		case FitsKeyword.INTEGER:
			i = fitsKeyword.getInt();
			intObject = new Integer(i);
			return intObject;
		case FitsKeyword.NONE:
			return null;
		case FitsKeyword.REAL:
			d = fitsKeyword.getReal();
			doubleObject = new Double(d);
			return doubleObject;
		case FitsKeyword.STRING:
			return fitsKeyword.getString();
		default:
			return null;
		}
	}

	/**
	 * Get the boolean value of the specified keyword.
	 * @param keyword A string representing the keyword name.
	 * @return The boolean value.
	 * @exception NullPointerException Thrown if the keyword does not exist in the header.
	 * @see #fitsHeader
	 */
	public boolean getKeywordValueBoolean(String keyword) throws NullPointerException
	{
		FitsKeyword fitsKeyword = null;
		boolean b;

		fitsKeyword = fitsHeader.getKeyword(keyword);
		if(fitsKeyword == null)
		{
			throw new NullPointerException(this.getClass().getName()+
						       ":getKeywordValueBoolean:No keyword found for:"+keyword+".");
		}
		b = fitsKeyword.getBool();
		return b;
	}

	/**
	 * Get the date value of the specified keyword.
	 * @param keyword A string representing the keyword name.
	 * @return The date value.
	 * @exception NullPointerException Thrown if the keyword does not exist in the header.
	 * @see #fitsHeader
	 */
	public Date getKeywordValueDate(String keyword) throws NullPointerException
	{
		FitsKeyword fitsKeyword = null;
		Date d = null;

		fitsKeyword = fitsHeader.getKeyword(keyword);
		if(fitsKeyword == null)
		{
			throw new NullPointerException(this.getClass().getName()+
						       ":getKeywordValueDate:No keyword found for:"+keyword+".");
		}
		d = fitsKeyword.getDate();
		return d;
	}

	/**
	 * Get the integer value of the specified keyword.
	 * @param keyword A string representing the keyword name.
	 * @return The integer value.
	 * @exception NullPointerException Thrown if the keyword does not exist in the header.
	 * @see #fitsHeader
	 */
	public int getKeywordValueInteger(String keyword) throws NullPointerException
	{
		FitsKeyword fitsKeyword = null;
		int i;

		fitsKeyword = fitsHeader.getKeyword(keyword);
		if(fitsKeyword == null)
		{
			throw new NullPointerException(this.getClass().getName()+
						       ":getKeywordValueInteger:No keyword found for:"+keyword+".");
		}
		i = fitsKeyword.getInt();
		return i;
	}

	/**
	 * Get the double value of the specified keyword.
	 * @param keyword A string representing the keyword name.
	 * @return The double value.
	 * @exception NullPointerException Thrown if the keyword does not exist in the header.
	 * @see #fitsHeader
	 */
	public double getKeywordValueDouble(String keyword) throws NullPointerException
	{
		FitsKeyword fitsKeyword = null;
		double d;

		fitsKeyword = fitsHeader.getKeyword(keyword);
		if(fitsKeyword == null)
		{
			throw new NullPointerException(this.getClass().getName()+
						       ":getKeywordValueDouble:No keyword found for:"+keyword+".");
		}
		d = fitsKeyword.getReal();
		return d;
	}

	/**
	 * Get the String value of the specified keyword.
	 * @param keyword A string representing the keyword name.
	 * @return The string value.
	 * @exception NullPointerException Thrown if the keyword does not exist in the header.
	 * @see #fitsHeader
	 */
	public String getKeywordValueString(String keyword) throws NullPointerException
	{
		FitsKeyword fitsKeyword = null;
		String s;

		fitsKeyword = fitsHeader.getKeyword(keyword);
		if(fitsKeyword == null)
		{
			throw new NullPointerException(this.getClass().getName()+
						       ":getKeywordValueString:No keyword found for:"+keyword+".");
		}
		s = fitsKeyword.getString();
		return s;
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
		
		sb = new StringBuffer();
		sb.append(prefix+"FITSHeader: \n");

		return sb.toString();
	}
}
/*
** $Log: not supported by cvs2svn $
** Revision 1.2  2003/05/19 15:09:11  cjm
** Lots of changes.
**
** Revision 1.1  2003/03/03 11:39:32  cjm
** Initial revision
**
*/
