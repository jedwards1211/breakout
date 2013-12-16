package org.andork.jogl.util;

public class OrthoFrame
{
	/**
	 * @param x
	 *            the value to reparameterize
	 * @param a1
	 *            the start of the first range
	 * @param a2
	 *            the end of the first range
	 * @param b1
	 *            the start of the second range
	 * @param b2
	 *            the end of the second range
	 */
	public static float reparam( float x , float a1 , float a2 , float b1 , float b2 )
	{
		return b1 + ( x - a1 ) * ( b2 - b1 ) / ( a2 - a1 );
	}
	
	/**
	 * 
	 * @param srcRange
	 * @param srcIndex
	 *            {@code srcRange[srcIndex]} and {@code srcRange[srcIndex + 1]} make up the input range.
	 * @param anchor
	 *            the value that will remain at the same relative distance from the range endpoints.
	 * @param factor
	 *            the scale factor. Values less than 1 will scale down (zoom in), values greater than 1 will scale up (zoom out).
	 * @param destRange
	 * @param destIndex
	 *            {@code destRange[destIndex]} and {@code destRange[destIndex + 1]} make up the output range.
	 */
	public static void zoom( float[ ] srcRange , int srcIndex , float anchor , float factor , float[ ] destRange , int destIndex )
	{
		destRange[ destIndex ] = anchor + ( srcRange[ srcIndex ] - anchor ) * factor;
		destRange[ destIndex + 1 ] = anchor + ( srcRange[ srcIndex + 1 ] - anchor ) * factor;
	}
	
	/**
	 * @param src
	 * @param srcIndex
	 *            {@code src[srcIndex]}, {@code src[srcIndex + 1]}, {@code src[srcIndex + 2]}, and {@code src[srcIndex + 3]} make up the input left, right, top,
	 *            bottom
	 * @param anchor_x
	 *            the x value that will remain at the same relative distance from the left/right.
	 * @param anchor_y
	 *            the y value that will remain at the same relative distance from the top/bottom.
	 * @param factor
	 *            the scale factor. Values less than 1 will scale down (zoom in), values greater than 1 will scale up (zoom out).
	 * @param dest
	 * @param destIndex
	 *            {@code dest[destIndex]}, {@code dest[destIndex + 1]}, {@code dest[destIndex + 2]}, and {@code dest[destIndex + 3]} make up the output left,
	 *            right, top, bottom
	 */
	public static void zoom( float[ ] src , int srcIndex , float anchor_x , float anchor_y , float factor , float[ ] dest , int destIndex )
	{
		zoom( src , srcIndex , anchor_x , factor , dest , destIndex );
		zoom( src , srcIndex + 2 , anchor_y , factor , dest , destIndex + 2 );
	}
	
	/**
	 * @param rect
	 * @param rectIndex
	 *            {@code rect[rectIndex]}, {@code rect[rectIndex + 1]}, {@code rect[rectIndex + 2]}, and {@code rect[rectIndex + 3]} make up the left, right,
	 *            top, bottom
	 * @param anchor_x
	 *            the x value that will remain at the same relative distance from the left/right.
	 * @param anchor_y
	 *            the y value that will remain at the same relative distance from the top/bottom.
	 * @param factor
	 *            the scale factor. Values less than 1 will scale down (zoom in), values greater than 1 will scale up (zoom out).
	 */
	public static void zoom( float[ ] rect , int rectIndex , float anchor_x , float anchor_y , float factor )
	{
		zoom( rect , rectIndex , anchor_x , anchor_y , factor , rect , rectIndex );
	}
}
