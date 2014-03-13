import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

// Stores an image in multiple formats
public class RGBBlockImage 
{
	// Image constants
	private Integer _width, _height, _blockSize, _numHorizontalBlocks, _numVerticalBlocks;
	private Boolean _isValidImage = true;
	// Image containers
	private BufferedImage _bufferedImage;
	private ArrayList<RGBBlock> _imageBlocks; 
	
	/* Constructors */
	// Initializes empty (all black pixel) BufferedImage and ImageBlocks
	public RGBBlockImage(int width, int height, int blockSize)
	{
		if ((width > 0) && (height > 0) && (blockSize > 0))
		{
			_width = width;
			_height = height;
			_blockSize = blockSize;
			_numHorizontalBlocks = (_width + _blockSize - 1)/_blockSize;
			_numVerticalBlocks = (_height + _blockSize - 1)/_blockSize;
			
			_bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
			_imageBlocks = new ArrayList<RGBBlock>(_numHorizontalBlocks * _numVerticalBlocks);
			for (int i = 0; i < (_numHorizontalBlocks * _numVerticalBlocks); i++)
			{
				_imageBlocks.add(new RGBBlock());
			}
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	// Initializes BufferedImage and ImageBlocks from rgb image file
	public RGBBlockImage(String filePath, int width, int height, int blockSize)
	{
		if ((filePath != null) && (width > 0) && (height > 0) && (blockSize > 0))
		{
			_width = width;
			_height = height;
			_blockSize = blockSize;
			_numHorizontalBlocks = (_width + _blockSize - 1)/_blockSize;
			_numVerticalBlocks = (_height + _blockSize - 1)/_blockSize;
			
			_bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
			_imageBlocks = new ArrayList<RGBBlock>(_numHorizontalBlocks * _numVerticalBlocks);
			for (int i = 0; i < (_numHorizontalBlocks * _numVerticalBlocks); i++)
			{
				_imageBlocks.add(new RGBBlock());
			}
			
			readRGBFileImage(filePath);
		}
		else
		{
			_isValidImage = false;
		}
	}
	
	// Copy Constructor (Initializes BufferedImage and ImageBlocks from rgbBlockImage copy)
	public RGBBlockImage(RGBBlockImage rgbBlockImage)
	{
		_width = rgbBlockImage.getWidth();
		_height = rgbBlockImage.getHeight();
		_blockSize = rgbBlockImage.getBlockSize();
		_numHorizontalBlocks = (_width + _blockSize - 1)/_blockSize;
		_numVerticalBlocks = (_height + _blockSize - 1)/_blockSize;
		
		// Create empty buffered image and block image
		_bufferedImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
		_imageBlocks = new ArrayList<RGBBlock>(_numHorizontalBlocks * _numVerticalBlocks);
		for (int i = 0; i < (_numHorizontalBlocks * _numVerticalBlocks); i++)
		{
			_imageBlocks.add(new RGBBlock());
		}
		
		// Copy image from rgbBlockImage
		ArrayList<RGBBlock> rgbBlockImageBlocks = rgbBlockImage.getImageBlocks();
		for (int i = 0; i < rgbBlockImageBlocks.size(); i++)
		{
			this.setImageBlock(i, rgbBlockImageBlocks.get(i));
		}
	}
	
	/* Public Methods */
	public int getWidth()
	{
		return _width;
	}
	
	public int getHeight()
	{
		return _height;
	}
	
	public int getBlockSize()
	{
		return _blockSize;
	}
	
	public Boolean isValidImage()
	{
		return _isValidImage;
	}
	
	public BufferedImage getBufferedImage()
	{
		return _bufferedImage;
	}
	
	public ArrayList<RGBBlock> getImageBlocks()
	{
		return _imageBlocks;
	}
	
	public void setImageBlocks(ArrayList<RGBBlock> imageBlocks)
	{
		if ((imageBlocks != null) && (imageBlocks.size() == _imageBlocks.size()))
		{
			for (int i = 0; i < imageBlocks.size(); i++)
			{
				this.setImageBlock(i, imageBlocks.get(i));
			}
		}
	}
	
	public RGBBlock getImageBlock(int blockNum)
	{
		if ((blockNum < 0) || (blockNum >= _imageBlocks.size()))
		{
			return null;
		}
		
		return _imageBlocks.get(blockNum);
	}
	
	public void setImageBlock(int blockNum, RGBBlock block)
	{
		if ((blockNum < 0) || (blockNum >= _imageBlocks.size()))
		{
			return;
		}
		
		// Set pixels in image block
		_imageBlocks.set(blockNum, new RGBBlock(block));
		
		// Set pixels in Buffered Image
		int startRow = (blockNum/_numHorizontalBlocks)*_blockSize;
		int startCol = (blockNum%_numHorizontalBlocks)*_blockSize;
		for (int r = startRow; r < DCTCoder.BLOCK_SIZE; r++)
		{
			for (int c = startCol; c < DCTCoder.BLOCK_SIZE; c++)
			{
				_bufferedImage.setRGB(r, c, block.getRGB(r, c));
			}
		}
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
					Byte r = bytes[ind];
					Byte g = bytes[ind + _height * _width];
					Byte b = bytes[ind + _height * _width * 2];
					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					_bufferedImage.setRGB(x, y, pix);
					
					_imageBlocks.get(block + x/_blockSize).redBlock().set(x%_blockSize, y%_blockSize, (r & 0xFF));
					_imageBlocks.get(block + x/_blockSize).greenBlock().set(x%_blockSize, y%_blockSize, (g & 0xFF));
					_imageBlocks.get(block + x/_blockSize).blueBlock().set(x%_blockSize, y%_blockSize, (b & 0xFF));
					
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
