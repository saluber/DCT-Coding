import april.Matrix;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DCTCoder 
{
	public static final int BLOCK_SIZE = 8;
	private static final double[] C = {(1/Math.sqrt(2)), 1, 1, 1, 1, 1, 1, 1};
	private static Matrix _dctMatrix;
	private static Matrix _dctMatrixT;
	private static double _qFactor;
	private static int _deliveryMode;
	private static int _latency;
	private static DoubleImageDisplay _decodeDisplay;
	
	public DCTCoder(int qFactor, int deliveryMode, int latency, DoubleImageDisplay display)
	{
		// Precompute DCTMatrix
		Matrix dctMatrix = new Matrix(BLOCK_SIZE, BLOCK_SIZE);
		_dctMatrix = dctMatrix.copy();
		
		for (int c = 0; c < BLOCK_SIZE; c++)
		{
			_dctMatrix.set(0, c, 1/2*C[0]);
		}
		
		for (int r = 1; r < BLOCK_SIZE; r++)
		{
			for (int c = 0; c < BLOCK_SIZE; c++)
			{
				_dctMatrix.set(r, c, 1/2*C[r]*Math.cos((2*c+1)*(r*Math.PI)/(2*BLOCK_SIZE)));
				System.out.println(Math.cos((2*c+1)*(r*Math.PI)/(2*BLOCK_SIZE)));
			}
		}
		
		// Compute transpose matrix
		_dctMatrixT = _dctMatrix.transpose();
		
		// Set quantization factor
		_qFactor = Math.pow(2.0, qFactor);
		_deliveryMode = deliveryMode;
		_latency = latency;
		_decodeDisplay = display;
	}
	
	public Matrix getDCTMatrix()
	{
		return _dctMatrix.copy();
	}
	
	public void encodeImage(ArrayList<RGBBlock> imageBlocks)
	{
		for (int i = 0; i < imageBlocks.size(); i++)
		{
			RGBBlock block = imageBlocks.get(i);
			block.setRedBlock(this.encodeBlock(imageBlocks.get(i).redBlock()));
			block.setGreenBlock(this.encodeBlock(imageBlocks.get(i).greenBlock()));
			block.setBlueBlock(this.encodeBlock(imageBlocks.get(i).blueBlock()));
		}
	}
	
	public Matrix encodeBlock(Matrix block)
	{
		return (_dctMatrix.times(block)).times(_dctMatrixT);
	}
	
	public void quantizeImage(ArrayList<RGBBlock> imageBlocks)
	{
		for (int i = 0; i < imageBlocks.size(); i++)
		{
			this.quantizeBlock(imageBlocks.get(i).redBlock());
			this.quantizeBlock(imageBlocks.get(i).greenBlock());
			this.quantizeBlock(imageBlocks.get(i).blueBlock());
		}
	}
	
	public void quantizeBlock(Matrix block)
	{
		// Quantize and round
		for (int r = 0; r < BLOCK_SIZE; r++)
		{
			for (int c = 0; c < BLOCK_SIZE; c++)
			{
				block.set(r, c, Math.round(block.get(r, c)/_qFactor));
			}
		}
	}
	
	public void dequantizeImage(RGBBlockImage image)
	{
		for (int i = 0; i < image.getImageBlocks().size(); i++)
		{
			RGBBlock block = image.getImageBlocks().get(i);
			block.redBlock().timesEquals(_qFactor);
			block.greenBlock().timesEquals(_qFactor);
			block.blueBlock().timesEquals(_qFactor);
			
			image.setImageBlock(i, block);
		}
	}
	
	public void dequantizeBlock(Matrix block)
	{
		block.timesEquals(_qFactor);
	}
	
	public void decodeImage(RGBBlockImage image)
	{
		if (_deliveryMode == 1)
		{
			this.baselineDecode(image);
		}
		else if (_deliveryMode == 2)
		{
			this.progressiveSSDecode(image);
		}
		else if (_deliveryMode == 3)
		{
			this.progressiveSBADecode(image);
		}
	}
	
	// Decode image for simulated baseline delivery mode
	public void baselineDecode(RGBBlockImage image)
	{
		for (int i = 0; i < image.getImageBlocks().size(); i++)
		{
			// Decode one block
			RGBBlock block = image.getImageBlocks().get(i);
			this.decodeBlock(block.redBlock());
			this.decodeBlock(block.greenBlock());
			this.decodeBlock(block.blueBlock());
			
			// Save decoded block to output buffered image
			image.setImageBlock(i, block);
			
			// Display updated decoded image result
			_decodeDisplay.setSecondImage(image.getBufferedImage());
			
			// Sleep for latency time
			try {
				TimeUnit.MILLISECONDS.sleep(_latency);
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Decode image for simulated progressive (spectral selection) delivery mode
	public void progressiveSSDecode(RGBBlockImage image)
	{
		_decodeDisplay.setSecondImage(image.getBufferedImage());
	}
	
	// Decode image for simulated progressive (successive bit approximation) delivery mode
	public void progressiveSBADecode(RGBBlockImage image)
	{
		_decodeDisplay.setSecondImage(image.getBufferedImage());
	}
	
	public void decodeBlock(Matrix block)
	{
		block = (_dctMatrixT.times(block)).times(_dctMatrix);
	}
	
	public static void main(String[] args)
	{
		DCTCoder _dctCoder = new DCTCoder(2, 1, 100, null);
		System.out.println("Printing DCTMatrix:");
		_dctCoder.getDCTMatrix().print();
		
		double[][] a =
			{{51, 52, 51, 50, 50, 52, 50, 52},
			 {51, 52, 51, 51, 50, 52, 52, 51},
			 {50, 50, 51, 52, 52, 51, 51, 51},
			 {51, 50, 50, 50, 52, 50, 50, 51},
			 {51, 50, 50, 51, 50, 50, 51, 50},
			 {50, 51, 52, 52, 51, 50, 50, 50},
			 {51, 52, 51, 50, 52, 50, 52, 50},
			 {50, 51, 52, 52, 50, 51, 52, 51}
			};
		
		Matrix A = new Matrix(a);
		System.out.println("Printing original block:");
		A.print();
		
		//_dctCoder.encodeBlock(A);
		A = _dctCoder.encodeBlock(A);
		System.out.println("\nPrinting encoded block:");
		A.print();
		
		_dctCoder.quantizeBlock(A);
		System.out.println("\nPrinting quantized block:");
		A.print();
		
		_dctCoder.dequantizeBlock(A);
		System.out.println("\nPrinting dequantized block:");
		A.print();
		
		_dctCoder.decodeBlock(A);
		System.out.println("\nPrinting deencoded block:");
		A.print();
	}
}
