import java.awt.image.*;
import java.io.*;

// Stores an image in multiple formats
public class MultiFormatImage 
{
	private Boolean _isValidImage = true;
	private Integer _width = 352; //512;
	private Integer _height = 288; //512;
	private Integer _blockSize = 8;
	private Integer _numBlocks = 1584; //4096;
	private BufferedImage _bufferedImage;
	private Integer[][] _imagePixels;
	private Integer[][][] _imageBlocks;
	
	/* Constructors */
	public MultiFormatImage(String filePath, int width, int height)
	{
		if ((width > 0) && (height > 0) && (filePath != null))
		{
			_width = width;
			_height = height;
			_numBlocks = ((_width + _blockSize - 1)/_blockSize) * ((_height + _blockSize - 1)/_blockSize);
			readRGBFileImage(filePath);
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	public MultiFormatImage(String filePath)
	{
		if (filePath != null)
		{
			readRGBFileImage(filePath);
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	public MultiFormatImage(BufferedImage image)
	{
		if (image != null)
		{
			// Store copy of image
			_width = image.getWidth();
			_height = image.getHeight();
			_numBlocks = ((_width + _blockSize - 1)/_blockSize) * ((_height + _blockSize - 1)/_blockSize);
			_bufferedImage = new BufferedImage(_width, _height, image.getType());
			_imagePixels = new Integer[_width][_height];
			_imageBlocks = new Integer[_numBlocks][_blockSize][_blockSize];
			int block = 0;
			for (int y = 0; y < _height; y++)
			{
				block = y/_blockSize*((_width + _blockSize - 1)/_blockSize);
				for (int x = 0; x < _width; x++)
				{
					_bufferedImage.setRGB(x, y, image.getRGB(x, y));
					_imagePixels[x][y] = image.getRGB(x, y);
					_imageBlocks[(block + x/_blockSize)][x%_blockSize][y%_blockSize] = image.getRGB(x, y);
				}
			}
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	public MultiFormatImage(Integer[][] imagePixels)
	{
		if ((imagePixels != null) && (imagePixels[0] != null))
		{
			// Store copy of image
			_width = imagePixels.length;
			_height = imagePixels[0].length;
			_numBlocks = ((_width + _blockSize - 1)/_blockSize) * ((_height + _blockSize - 1)/_blockSize);
			_bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
			_imagePixels = new Integer[_width][_height];
			_imageBlocks = new Integer[_numBlocks][_blockSize][_blockSize];
			int block = 0;
			for (int y = 0; y < _height; y++)
			{
				block = y/_blockSize*((_width + _blockSize - 1)/_blockSize);
				for (int x = 0; x < _width; x++)
				{
					_bufferedImage.setRGB(x, y, imagePixels[x][y]);
					_imagePixels[x][y] = imagePixels[x][y];
					_imageBlocks[(block + x/_blockSize)][x%_blockSize][y%_blockSize] = imagePixels[x][y];
				}
			}
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	/* Public Methods */
	public Integer GetWidth()
	{
		return _width;
	}
	
	public Integer GetHeight()
	{
		return _height;
	}
	
	public BufferedImage GetBufferedImage()
	{
		return _bufferedImage;
	}
	
	public Integer[][] GetImagePixels()
	{
		return _imagePixels;
	}
	
	public Integer[][] GetImageBlock(int blockNum)
	{
		if ((blockNum < 0) || (blockNum >= _numBlocks))
		{
			return null;
		}
		else
		{
			return _imageBlocks[blockNum];
		}
	}
	
	public Boolean IsValidImage()
	{
		return _isValidImage;
	}
	
	/* Private Methods */
	private void readRGBFileImage(String filePath)
	{
		File file = null;
		InputStream is = null;
		try 
		{
			// Read file as byte array
			file = new File(filePath);
			is = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int)length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) 
			{
				offset += numRead;
			}
	
			// Parse byte array to BufferImage and image pixel array
			_bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
			_imagePixels = new Integer[_width][_height];
			_imageBlocks = new Integer[_numBlocks][_blockSize][_blockSize];
			int ind = 0;
			int block = 0;
			for (int y = 0; y < _height; y++) 
			{
				block = y/_blockSize*((_width + _blockSize - 1)/_blockSize);
				for (int x = 0; x < _width; x++)
				{
					// byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + _height * _width];
					byte b = bytes[ind + _height * _width * 2];
					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					_imagePixels[x][y] = pix;
					_bufferedImage.setRGB(x, y, pix);
					_imageBlocks[(block + x/_blockSize)][x%_blockSize][y%_blockSize] = pix;
					
					ind++;
				}
			}
			
			is.close();
		} 
		catch (FileNotFoundException e) 
		{
			_isValidImage = false;
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			_isValidImage = false;
			e.printStackTrace();
		}
	}
}
