// FITSImage.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSImage.java,v 1.1 2003-03-03 11:39:32 cjm Exp $
package org.estar.fits;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.eso.fits.*;

/**
 * This class loads FITS image headers and data, and produces objects suitable for
 * hooking into java.awt for creating FITS images.
 * @author Chris Mottram
 * @version $Revision: 1.1 $
 */
public class FITSImage
{
	/**
	 * Revision control system version id.
	 */
	public final static String RCSID = "$Id: FITSImage.java,v 1.1 2003-03-03 11:39:32 cjm Exp $";
	/**
	 * Width of image.
	 */
	int width;
	/**
	 * Height of image.
	 */
	int height;
	/**
	 * The FITS file we are getting the image from.
	 */
	FitsFile fitsFile = null;
	/**
	 * Array of floats read from FITS image.
	 */
	float dataArray[];
	/**
	 * Minimum pixel value. Derived from dataArray but can be overridden, 'zero' point 
	 * when creating output image sources.
	 */
	float minPixelValue = 0.0f;
	/**
	 * Maximum pixel value. Derived from dataArray but can be overridden, 'white' point 
	 * when creating output image sources.
	 */
	float maxPixelValue = 0.0f;

	/**
	 * Default constructor.
	 */
	public FITSImage()
	{
		super();
	}

	/**
	 * Load FITS image.
	 * @param filename The filename to load from.
	 * @see #fitsFile
	 * @see #load(FitsFile)
	 */
	/* diddly
	public void load(String filename) throws IOException,FITSException
	{
		try
		{
			fitsFile = new FitsFile(filename,true);
			load(fitsFile);
		}
		catch(FitsException e)
		{
			throw new FITSException(this.getClass().getName()+":load:"+e);
		}
	}
	*/

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
	 * Set min and max pixels values to scale image between, based on previously
	 * loaded image data.
	 * @see #dataArray
	 * @see #load
	 * @see #minPixelValue
	 * @see #maxPixelValue
	 */
	public void setMinMaxPixelValue()
	{
		int nPixels;

		minPixelValue = 65535.0f;
		maxPixelValue = 0.0f;
		nPixels = dataArray.length;
		for(int i = 0;i < nPixels; i++)
		{
			if(dataArray[i] < minPixelValue)
				minPixelValue = dataArray[i];
			if(dataArray[i] > maxPixelValue)
				maxPixelValue = dataArray[i];
		}
	}

	/**
	 * Return the value of the lowest pixel in the dataArray.
	 * @return An input FITS data array pixel value.
	 * @see #minPixelValue
	 */
	public float getMinPixelValue()
	{
		return minPixelValue;
	}

	/**
	 * Return the value of the lowest pixel in the dataArray.
	 * @return An input FITS data array pixel value.
	 * @see #maxPixelValue
	 */
	public float getMaxPixelValue()
	{
		return maxPixelValue;
	}

	/**
	 * Create a memory image source model suitable for creating an image from.
	 * @return The memory image source.
	 * @see #minPixelValue
	 * @see #maxPixelValue
	 */
	public MemoryImageSource createImageSource()
	{
		return createImageSource(minPixelValue,maxPixelValue);
	}

	/**
	 * Create a memory image source model suitable for creating an image from.
	 * @param minValue Any dataArray pixel values less than this value are treated as black.
	 * @param maxValue Any dataArray pixel values greater than this value are treated as white.
	 * @return The memory image source.
	 * @see #width
	 * @see #height
	 * @see #dataArray
	 */
	public MemoryImageSource createImageSource(float minValue,float maxValue)
	{
		int pixels[];
		float scaleValue;
		int nvals;
		int value;

		nvals = width * height;
		scaleValue = 255.0f / (maxValue-minValue);
		pixels = new int[nvals];
		for(int i = 0;i < nvals; i++)
		{
			if(dataArray[i] < minValue)
				value = 0;
			else if(dataArray[i] > maxValue)
				value = 255;
			else
				value = (int)((dataArray[i] - minValue) * scaleValue);
			// pixels[] is RGB 8 bit, make greyscale
			pixels[i] = (value << 24) | (value << 16) | value;
		}
		return new MemoryImageSource(width, height, pixels, 0, width);
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
		sb.append(prefix+"FITSImage: \n");
		return sb.toString();
	}

	// protected methods
	/**
	 * Method to load the data array from the specified FitsFile
	 * @param ff the Fits File to load.
	 * @exception FITSException Thrown if HDU type is not image, or number of axes are not 2.
	 * @see #width
	 * @see #height
	 * @see #dataArray
	 */
	protected void load(FitsFile ff) throws FITSException
	{
		FitsHDUnit hdu = null;
		FitsHeader header = null;
		FitsMatrix data = null;
		int axes[];
		int nvals;

		hdu = ff.getHDUnit(0);
		header = hdu.getHeader();
		width = header.getKeyword("NAXIS1").getInt();
		height = header.getKeyword("NAXIS2").getInt();
		if(hdu.getData().getType() != Fits.IMAGE)
		{
			throw new FITSException(this.getClass().getName()+":load:Illegal HDU type:"+
						hdu.getData().getType());
		}
		data = (FitsMatrix)(hdu.getData());
		if(data.getNoAxes() != 2)
		{
			throw new FITSException(this.getClass().getName()+":load:Illegal number of axes:"+
						data.getNoAxes());
		}
		axes = new int[data.getNoAxes()];
		axes = data.getNaxis();
		width = axes[0];
		height = axes[1];
		nvals = data.getNoValues();
		//dataArray = new float[nvals];
		dataArray = null; // get getFloatValues to allocate buffer
		try
		{
			dataArray = data.getFloatValues(0,nvals,dataArray);
		}
		catch(FitsException e)
		{
			e.printStackTrace(System.err);
			throw new FITSException(this.getClass().getName()+":load:"+e);
		}
	}
}
/*
** $Log: not supported by cvs2svn $
*/
