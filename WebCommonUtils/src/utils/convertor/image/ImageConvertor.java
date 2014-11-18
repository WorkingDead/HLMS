package utils.convertor.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageConvertor {
	
	public static BufferedImage createImageThumbnail(File orimage, int thumbWidth, int thumbHeight) throws IOException
	{
		BufferedImage image = javax.imageio.ImageIO.read(orimage);
		
		double thumbRatio = (double)thumbWidth / (double)thumbHeight;
		int imageWidth    = image.getWidth(null);
		int imageHeight   = image.getHeight(null);
		double imageRatio = (double)imageWidth / (double)imageHeight;
		if (thumbRatio < imageRatio) 
		{
			thumbHeight = (int)(thumbWidth / imageRatio);
		} 
		else 
		{
			thumbWidth = (int)(thumbHeight * imageRatio);
		}

		if(imageWidth < thumbWidth && imageHeight < thumbHeight)
		{
			thumbWidth = imageWidth;
			thumbHeight = imageHeight;
		}
		else if(imageWidth < thumbWidth)
			thumbWidth = imageWidth;
		else if(imageHeight < thumbHeight)
			thumbHeight = imageHeight;
		
		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setBackground(Color.WHITE);
		graphics2D.setPaint(Color.WHITE); 
		graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
		return thumbImage;
	}

	public static byte[] getImageBytes(BufferedImage image, String imageFormat) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, imageFormat, baos);
		return baos.toByteArray();
	}
}
