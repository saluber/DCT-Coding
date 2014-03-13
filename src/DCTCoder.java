import april.Matrix;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DCTCoder 
{
	public static final int BLOCK_SIZE = 8;
	public static final double[] C = {(1.0/Math.sqrt(2.0)), 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
	public static final Matrix _dctMatrix = DCTCoder.computeDCTMatrix();
	public static final Matrix _dctMatrixT = _dctMatrix.transpose();
	private double _qFactor;
	private int _deliveryMode;
	private int _latency;
	private DoubleImageDisplay _decodeDisplay;

	public static Matrix computeDCTMatrix()
	{
		double[][] dctMatrixArray = new double[8][8];
		for (int c = 0; c < BLOCK_SIZE; c++)
		{
			dctMatrixArray[0][c] = 0.5*C[0];
		}
		
		for (int r = 1; r < BLOCK_SIZE; r++)
		{
			for (int c = 0; c < BLOCK_SIZE; c++)
			{
				dctMatrixArray[r][c] = 0.5*Math.cos((2.0*c+1.0)*(r*Math.PI)/(2.0*BLOCK_SIZE));
			}
		}
		
		return new Matrix(dctMatrixArray);
	}
	
	public DCTCoder(int qFactor, int deliveryMode, int latency, DoubleImageDisplay display)
	{
		// Set quantization factor
		_qFactor = Math.pow(2.0, (double)qFactor);
		_deliveryMode = deliveryMode;
		_latency = latency;
		_decodeDisplay = display;
		
		// Pre-compute DCTMatrix values
		//_dctMatrix.print();
		// Compute DCTMatrixTranspose matrix
		//_dctMatrixT.print();
	}
	
	// Perform DCT on image blocks
	public void dctImage(ArrayList<RGBBlock> imageBlocks)
	{
		for (int i = 0; i < imageBlocks.size(); i++)
		{
			RGBBlock block = imageBlocks.get(i);
			
			block.setRedBlock(this.dctBlock(block.redBlock()));
			block.setGreenBlock(this.dctBlock(block.greenBlock()));
			block.setBlueBlock(this.dctBlock(block.blueBlock()));
		}
	}
	
	public Matrix dctBlock(Matrix block)
	{
		return (_dctMatrix.times(block)).times(_dctMatrixT);
	}
	
	// Quantize all image blocks
	public void quantizeImage(ArrayList<RGBBlock> imageBlocks)
	{
		for (int i = 0; i < imageBlocks.size(); i++)
		{
			this.quantizeBlock(imageBlocks.get(i).redBlock());
			this.quantizeBlock(imageBlocks.get(i).greenBlock());
			this.quantizeBlock(imageBlocks.get(i).blueBlock());
		}
	}
	
	// Quantize and round block
	public void quantizeBlock(Matrix block)
	{
		for (int r = 0; r < BLOCK_SIZE; r++)
		{
			for (int c = 0; c < BLOCK_SIZE; c++)
			{
				block.set(r, c, Math.round(block.get(r, c)/_qFactor));
			}
		}
	}
	
	// De-quantize all image blocks
	public void dequantizeImage(ArrayList<RGBBlock> imageBlocks)
	{
		for (int i = 0; i < imageBlocks.size(); i++)
		{			
			this.dequantizeBlock(imageBlocks.get(i).redBlock());
			this.dequantizeBlock(imageBlocks.get(i).greenBlock());
			this.dequantizeBlock(imageBlocks.get(i).blueBlock());
		}
	}
	
	// De-quantize block
	public void dequantizeBlock(Matrix block)
	{
		block.timesEquals(_qFactor);
	}
	
	// Perform IDCT on image blocks and output result to _decodeDisplay
	public void idctImage(RGBBlockImage image)
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
		
		System.out.println("Done!");
	}
	
	public Matrix idctBlock(Matrix block)
	{
		return (_dctMatrixT.times(block)).times(_dctMatrix);
	}
	
	// Decode image for simulated baseline delivery mode
	public void baselineDecode(RGBBlockImage image)
	{
		// Create new empty image to store decoded image result
		RGBBlockImage decodeImage = new RGBBlockImage(image.getWidth(), image.getHeight(), image.getBlockSize());
		// Get de-quantized encoded image blocks
		ArrayList<RGBBlock> imageBlocks = image.getImageBlocks();
		for (int i = 0; i < imageBlocks.size(); i++)
		{
			// Get RGBBlock to decode and RGBBlock to store result in
			RGBBlock encodeBlock = imageBlocks.get(i);
			RGBBlock decodeBlock = decodeImage.getImageBlock(i);
			
			// Decode rgb block matrices
			decodeBlock.setRedBlock(this.idctBlock(encodeBlock.redBlock()));
			decodeBlock.setGreenBlock(this.idctBlock(encodeBlock.greenBlock()));
			decodeBlock.setBlueBlock(this.idctBlock(encodeBlock.blueBlock()));

			// Save decode block result to decodedImage (also updates decodedImage.BufferedImage)
			decodeImage.setImageBlock(i, decodeBlock);
			
			// Display updated decoded image result
			_decodeDisplay.setSecondImage(decodeImage.getBufferedImage());
			
			// Sleep for latency time
			if (_latency != 0)
			{
				try {
					TimeUnit.MILLISECONDS.sleep(_latency);
				} catch (InterruptedException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	// Decode image for simulated progressive (spectral selection) delivery mode
	public void progressiveSSDecode(RGBBlockImage encodedImage)
	{
		// TODO
	}
	
	// Decode image for simulated progressive (successive bit approximation) delivery mode
	public void progressiveSBADecode(RGBBlockImage encodedImage)
	{
		// TODO
	}
	
	public static void main(String[] args)
	{
		DCTCoder _dctCoder = new DCTCoder(2, 1, 100, null);
		
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
		
		DCTCoder._dctMatrix.print();
		
		Matrix A = new Matrix(a);
		System.out.println("Printing original block:");
		A.print();
		
		//_dctCoder.encodeBlock(A);
		A = _dctCoder.dctBlock(A);
		System.out.println("\nPrinting encoded block:");
		A.print();
		
		_dctCoder.quantizeBlock(A);
		System.out.println("\nPrinting quantized block:");
		A.print();
		
		_dctCoder.dequantizeBlock(A);
		System.out.println("\nPrinting dequantized block:");
		A.print();
		
		A = _dctCoder.idctBlock(A);
		System.out.println("\nPrinting deencoded block:");
		A.print();
	}
}
