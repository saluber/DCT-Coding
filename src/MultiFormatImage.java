import java.awt.image.*;
import java.io.*;

// Stores an image in multiple formats
public class MultiFormatImage 
{
	// Image constants
	private Integer _width, _height, _blockSize, _numHorizontalBlocks, _numVerticalBlocks;
	// Class variables
	private Boolean _isValidImage = true;
	private BufferedImage _bufferedImage;
	private Integer[][][] _imageBlocks;
	private Integer[][][][] _imageChannelBlocks;
	
	/* Constructors */
	public MultiFormatImage(String filePath, int width, int height, int blockSize)
	{
		if ((filePath != null) && (width > 0) && (height > 0) && (blockSize > 0))
		{
			_width = width;
			_height = height;
			_blockSize = blockSize;
			_numHorizontalBlocks = (_width + _blockSize - 1)/_blockSize;
			_numVerticalBlocks = (_height + _blockSize - 1)/_blockSize;
			
			_bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
			_imageBlocks = new Integer[_numHorizontalBlocks * _numVerticalBlocks][_blockSize][_blockSize];
			readRGBFileImage(filePath);
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	/* Public Methods */
	public Boolean IsValidImage()
	{
		return _isValidImage;
	}
	
	public BufferedImage GetBufferedImage()
	{
		return _bufferedImage;
	}
	
	public Integer[][] GetImageBlock(int blockNum)
	{
		if ((blockNum < 0) || (blockNum >= (_numHorizontalBlocks * _numVerticalBlocks)))
		{
			return null;
		}
		
		return _imageBlocks[blockNum];
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
			int ind = 0;
			int block = 0;
			for (int y = 0; y < _height; y++) 
			{
				block = y/_blockSize*_numHorizontalBlocks;
				for (int x = 0; x < _width; x++)
				{
					// byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + _height * _width];
					byte b = bytes[ind + _height * _width * 2];
					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
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
