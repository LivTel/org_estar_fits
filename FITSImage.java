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
// FITSImage.java
// $Header: /space/home/eng/cjm/cvs/org_estar_fits/FITSImage.java,v 1.4 2007-01-30 18:34:35 cjm Exp $
package org.estar.fits;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.eso.fits.*;
import org.estar.astrometry.*;

/**
 * This class loads FITS image headers and data, and produces objects suitable for
 * hooking into java.awt for creating FITS images.
 * A MemoryImageSource can be returned. There are various ulility routine for pixel <-> RA/Dec conversion
 * (assuming linear plate scaling), and access routines to various fits header data.
 * @author Chris Mottram
 * @version $Revision: 1.4 $
 */
public class FITSImage
{
	/**
	 * Revision control system version id.
	 */
	public final static String RCSID = "$Id: FITSImage.java,v 1.4 2007-01-30 18:34:35 cjm Exp $";
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
	 * Field centre RA. From FCRA keyword.
	 */
	RA fcRA = null;
	/**
	 * Field centre Dec. From FCDEC keyword.
	 */
	Dec fcDec = null;
	/**
	 * Plate scale - arc-sec/pixel. From XPS keyword.
	 */
	double xPlateScale = 0.0;
	/**
	 * Plate scale - arc-sec/pixel. From YPS keyword.
	 */
	double yPlateScale = 0.0;
	/**
	 * The name of the object on this frame. From OBJECT keyword.
	 */
	String objectName = null;
	/**
	 * The date the data was taken.
	 */
	Date dateObs = null;

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

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public RA getFCRA()
	{
		return fcRA;
	}

	public Dec getFCDec()
	{
		return fcDec;
	}

	public double getXPlateScale()
	{
		return xPlateScale;
	}

	public double getYPlateScale()
	{
		return yPlateScale;
	}

	/**
	 * Gets position on sky, given x and y pixel coords.
	 * @param x X pos.
	 * @param y Y pos.
	 * @return An instance of CelestialObject is returned. Only the RA and Dec fields are set.
	 *       null can be returned.
	 */
	public CelestialObject getPosition(int x,int y)
	{
		CelestialObject co = null;
		RA newRA = null;
		Dec newDec = null;
		double xpixoff,ypixoff;
		double xas,yas;

		if(x < 0)
			return null;
		if(y < 0)
			return null;
		if(x > width)
			return null;
		if(y > height)
			return null;
		if(fcRA == null)
			return null;
		if(fcDec == null)
			return null;
		xpixoff = (double)(width/2)-x;
		ypixoff = (double)(height/2)-y;
		xas = fcRA.toArcSeconds();
		yas = fcDec.toArcSeconds();
		xas += xpixoff*xPlateScale;
		yas += ypixoff*yPlateScale;
		co = new CelestialObject();
		newRA = new RA();
		newDec = new Dec();
		newRA.fromArcSeconds(xas);
		newDec.fromArcSeconds(yas);
		co.setRA(newRA);
		co.setDec(newDec);
		return co;
	}

	/**
	 * Gets position on image, given sky coords. Assumes linear fit from FCRA/FCDEC,atm.
	 * @param ra X pos.
	 * @param dec Y pos.
	 * @return An instance of Point is returned. 
	 */
	public Point getPosition(RA ra,Dec dec)
	{
		Point p = null;
		double cx,cy,raoff,decoff;
		double xas,yas,cxas,cyas;

		if(ra == null)
			return null;
		if(dec == null)
			return null;
		if(fcRA == null)
			return null;
		if(fcDec == null)
			return null;
		cx = (double)(width/2);
		cy = (double)(height/2);
		xas = ra.toArcSeconds();
		yas = dec.toArcSeconds();
		cxas = fcRA.toArcSeconds();
		cyas = fcDec.toArcSeconds();
		raoff = cxas-xas;
		decoff = cyas-yas;
		p = new Point();
		p.x = (int)((raoff/xPlateScale)+cx);
		p.y = (int)((decoff/yPlateScale)+cy);
		return p;
	}

	/**
	 * Gets the original data array value at the specified  x and y location.
	 * @param x The x position on the displayed image.
	 * @param y The y position on the displayed image. Note this is the height minus
	 *          the y position in the array, as the display image is inverted in y.
	 * @return The value in the data array, or 0.0 if the x and y values are out of range.
	 */
	public double getValue(int x,int y)
	{
		double value;
		int dataArrayIndex,nvals;

		dataArrayIndex = ((height-(y+1))*width)+x;
		nvals = width * height;
		if((dataArrayIndex < 0)||(dataArrayIndex>=nvals))
			return 0.0;
		return dataArray[dataArrayIndex];
	}

	/**
	 * Method to get the radius of the field from it's centre, in arc-seconds.
	 * This is done by computing the field size in each axis, and calculating the hypoteneuse
	 * to get the radius to the corner.
	 * @return The radius from the centre of the field to one corner, in arc-seconds.
	 * @see #getFieldSizeX
	 * @see #getFieldSizeY
	 */
	public double getFieldRadius()
	{
		double fsx,fsy;
		double radius;

		fsx = getFieldSizeX();
		fsy = getFieldSizeY();
		// calculate hypoteneuse to corner of field.
		radius = Math.sqrt((fsx*fsx)+(fsy*fsy));
		return radius;
	}

	/**
	 * Method to get the field size (X) using width,xPlateScale.
	 * @return The field size in X, in arc-seconds.
	 * @see #width
	 * @see #xPlateScale
	 */
	public double getFieldSizeX()
	{
		return ((double)width)*xPlateScale;
	}

	/**
	 * Method to get the field size (Y) using height,yPlateScale.
	 * @return The field size in Y, in arc-seconds.
	 * @see #height
	 * @see #yPlateScale
	 */
	public double getFieldSizeY()
	{
		return ((double)height)*yPlateScale;
	}

	/**
	 * Method to get the object name, as stored in the "OBJECT" keyword in the  FITS header.
	 * @return The object name, or null if one was not found.
	 * @see #objectName
	 */
	public String getObjectName()
	{
		return objectName;
	}

	/**
	 * Method to get the time of observation, as stored in the "DATE-OBS" keyword in the  FITS header.
	 * @return The time of observation, or null if one was not found.
	 * @see #dateObs
	 */
	public Date getDateObs()
	{
		return dateObs;
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
	 * We flip the input data array in Y, to get the output with North at the top.
	 * A greyscale non-transparent image source is returned.
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
		int value,dataArrayIndex,pixelsIndex;

		nvals = width * height;
		scaleValue = 255.0f / (maxValue-minValue);
		pixels = new int[nvals];
		for(int y=0;y < height; y++)
		{
			for(int x = 0;x < width; x++)
			{
				dataArrayIndex = (y*width)+x;
				pixelsIndex = ((height-(y+1))*width)+x;// pixels list flipped in y
				if(dataArray[dataArrayIndex] < minValue)
					value = 0;
				else if(dataArray[dataArrayIndex] > maxValue)
					value = 255;
				else
					value = (int)((dataArray[dataArrayIndex] - minValue) * scaleValue);
				// pixels[] is RGB 8 bit, make greyscale
				// 0xAARRGGBB (AA = Alpha transarency)
				pixels[pixelsIndex] = (255 << 24) | (value << 16) | (value << 8) | value;
			}
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
	 * @see #objectName
	 * @see #fcRA
	 * @see #fcDec
	 * @see #width
	 * @see #xPlateScale
	 * @see #height
	 * @see #yPlateScale
	 */
	public String toString(String prefix)
	{
		StringBuffer sb = null;
		
		sb = new StringBuffer();
		sb.append(prefix+objectName+" "+fcRA+" "+fcDec+" X:"+width+" * "+xPlateScale+
			  " Y:"+height+" * "+yPlateScale+" "+dateObs);
		return sb.toString();
	}

	// protected methods
	/**
	 * Method to load the data array from the specified FitsFile
	 * @param ff the Fits File to load.
	 * @exception FITSException Thrown if HDU type is not image, or number of axes are not 2.
	 * @see #objectName
	 * @see #width
	 * @see #height
	 * @see #dataArray
	 * @see #fcRA
	 * @see #fcDec
	 * @see #xPlateScale
	 * @see #yPlateScale
	 */
	protected void load(FitsFile ff) throws FITSException
	{
		FitsHDUnit hdu = null;
		FitsHeader header = null;
		FitsMatrix data = null;
		FitsKeyword keyword = null;
		String s = null;
		int axes[];
		int nvals;

		hdu = ff.getHDUnit(0);
		header = hdu.getHeader();
		width = header.getKeyword("NAXIS1").getInt();
		height = header.getKeyword("NAXIS2").getInt();
	        keyword = header.getKeyword("FCRA");
		if(keyword != null)
		{
			s = keyword.getString();
			if(s != null)
			{
				fcRA = new RA();
				fcRA.parseSpace(s);
			}
		}
		keyword = header.getKeyword("FCDEC");
		if(keyword != null)
		{
			s = keyword.getString();
			if(s != null)
			{
				fcDec = new Dec();
				fcDec.parseSpace(s);
			}
		}
		keyword = header.getKeyword("XPS");
		if(keyword != null)
			xPlateScale = keyword.getReal();
		keyword = header.getKeyword("YPS");
		if(keyword != null)
			yPlateScale = keyword.getReal();
		keyword = header.getKeyword("OBJECT");
		if(keyword != null)
			objectName = keyword.getString();
		keyword = header.getKeyword("DATE-OBS");
		if(keyword != null)
			dateObs = keyword.getDate();
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
** Revision 1.3  2003/07/23 18:07:56  cjm
** Added protection against null keywords that might not exist.
**
** Revision 1.2  2003/05/19 15:09:11  cjm
** First working version.
**
** Revision 1.1  2003/03/03 11:39:32  cjm
** Initial revision
**
*/
