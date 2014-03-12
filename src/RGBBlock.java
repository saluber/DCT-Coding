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
		
		int pix = ((0 << 24) + (r << 16) + (g << 8) + b);
		return pix;
	}
}
