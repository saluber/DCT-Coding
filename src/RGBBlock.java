import april.Matrix;

public class RGBBlock 
{	
	private Matrix _redBlock, _greenBlock, _blueBlock;
	
	public RGBBlock()
	{
		_redBlock = new Matrix(DCTCoder.BLOCK_SIZE, DCTCoder.BLOCK_SIZE);
		_greenBlock = new Matrix(DCTCoder.BLOCK_SIZE, DCTCoder.BLOCK_SIZE);
		_blueBlock = new Matrix(DCTCoder.BLOCK_SIZE, DCTCoder.BLOCK_SIZE);
	}
	
	public RGBBlock(RGBBlock block)
	{
		_redBlock = block.redBlock().copy();
		_greenBlock = block.greenBlock().copy();
		_blueBlock = block.blueBlock().copy();
	}
	
	public Matrix redBlock()
	{
		return _redBlock;
	}
	
	public void setRedBlock(Matrix m)
	{
		_redBlock = m;
	}
	
	public Matrix greenBlock()
	{
		return _greenBlock;
	}
	
	public void setGreenBlock(Matrix m)
	{
		_greenBlock = m;
	}
	
	public Matrix blueBlock()
	{
		return _blueBlock;
	}
	
	public void setBlueBlock(Matrix m)
	{
		_blueBlock = m;
	}
	
	public int getRGB(int row, int col)
	{
		int r = (int)_redBlock.get(row, col);
		int g = (int)_greenBlock.get(row, col);
		int b = (int)_blueBlock.get(row, col);
		
		if (r > 255)
		{
			r = 255;
		}
		else if (r < 0)
		{
			r = 0;
		}
		
		if (g > 255)
		{
			g = 255;
		}
		else if (g < 0)
		{
			g = 0;
		}
		
		if (b > 255)
		{
			b = 255;
		}
		else if (b < 0)
		{
			b = 0;
		}
		
		int pix =  0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
		
		return pix;
	}
}
