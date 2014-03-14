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
		double[][] dctMatrixArray = new double[BLOCK_SIZE][BLOCK_SIZE];
		for (int y = 0; y < BLOCK_SIZE; y++)
		{
			dctMatrixArray[0][y] = 0.5*C[0];
		}
		
		for (int x = 1; x < BLOCK_SIZE; x++)
		{
			for (int y = 0; y < BLOCK_SIZE; y++)
			{
				dctMatrixArray[x][y] = 0.5*Math.cos((2.0*y+1.0)*(x*Math.PI)/(2.0*BLOCK_SIZE));
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
		for (int y = 0; y < BLOCK_SIZE; y++)
		{
			for (int x = 0; x < BLOCK_SIZE; x++)
			{
				block.set(x, y, Math.round(block.get(x, y)/_qFactor));
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
	}
	
	public Matrix idctBlock(Matrix block)
	{
		return (_dctMatrixT.times(block)).times(_dctMatrix);
	}
	
	public Matrix idctBlock(Matrix block, Matrix dctTransform, Matrix dctTransformTrans)
	{
		return (dctTransform.times(block)).times(dctTransformTrans);
	}
	
	// Decode image for simulated baseline delivery mode
	public void baselineDecode(RGBBlockImage image)
	{
		// Create new empty image to store decoded image result
		RGBBlockImage decodeImage = new RGBBlockImage(image.getWidth(), image.getHeight(), image.getBlockSize());
		// Get de-quantized encoded image blocks
		ArrayList<RGBBlock> imageBlocks = image.getImageBlocks();
		// Decode one block each iteration
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
			
			// Sleep for latency time between decoding iterations
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
	public void progressiveSSDecode(RGBBlockImage image)
	{
		// Create new empty image to store decoded image result
		RGBBlockImage decodeImage = new RGBBlockImage(image.getWidth(), image.getHeight(), image.getBlockSize());
		// Get de-quantized encoded image blocks
		ArrayList<RGBBlock> imageBlocks = image.getImageBlocks();
		// Decode image with increasing number of block coefficients used
		for (int nCoeffs = 1; nCoeffs <= (BLOCK_SIZE * BLOCK_SIZE); nCoeffs++)
		{
			// Decode each image block using nSigBits number of significant bits of each coefficient
			for (int i = 0; i < imageBlocks.size(); i++)
			{
				// Get and copy RGBBlock to decode (all bits)
				RGBBlock encodeBlock = imageBlocks.get(i);
				// Get RGBBlock to store decode result in
				RGBBlock decodeBlock = decodeImage.getImageBlock(i);
				
				// Apply "coeff mask" to each block get first nCoeff coefficients (set the rest to 0), then decode
				decodeBlock.setRedBlock(this.idctBlock(this.coeffmaskBlock(encodeBlock.redBlock(), nCoeffs)));
				decodeBlock.setGreenBlock(this.idctBlock(this.coeffmaskBlock(encodeBlock.greenBlock(), nCoeffs)));
				decodeBlock.setBlueBlock(this.idctBlock(this.coeffmaskBlock(encodeBlock.blueBlock(), nCoeffs)));

				// Save decode block result to decodedImage (also updates decodedImage.BufferedImage)
				decodeImage.setImageBlock(i, decodeBlock);
			}
			
			// Display updated decoded image result
			_decodeDisplay.setSecondImage(decodeImage.getBufferedImage());
			
			// Sleep for latency time between decoding iterations
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
	
	// Creates and returns a new matrix containing numCoeffs coeff values from input matrix (all other coeffs sets to 0)
	private Matrix coeffmaskBlock(Matrix m, int numCoeffs)
	{
		// Check for valid input args
		if ((m == null) || (numCoeffs < 0) || (numCoeffs > (BLOCK_SIZE * BLOCK_SIZE)))
		{
			return null;
		}
		
		double[][] maskedBlock = new double[BLOCK_SIZE][BLOCK_SIZE];
		
		// Iterate through input matrix until numCoeff coefficients have been copied into maskedBlock
		for (int x = 0; x < BLOCK_SIZE; x++)
		{
			for (int y = 0; y < BLOCK_SIZE; y++)
			{
				if (numCoeffs == 0)
				{
					break;
				}
				
				maskedBlock[x][y] = m.get(x, y);
				numCoeffs--;
			}
		}
		
		return new Matrix(maskedBlock);
	}
	
	// Decode image for simulated progressive (successive bit approximation) delivery mode
	public void progressiveSBADecode(RGBBlockImage image)
	{
		// Create new empty image to store decoded image result
		RGBBlockImage decodeImage = new RGBBlockImage(image.getWidth(), image.getHeight(), image.getBlockSize());
		// Get de-quantized encoded image blocks
		ArrayList<RGBBlock> imageBlocks = image.getImageBlocks();
		// Decode image with increasing number of significant bits (starts at 2 because 1st bit is sign)
		for (int nSigBits = 1; nSigBits <= 32; nSigBits++)
		{
			// Decode each image block using nSigBits number of significant bits of each coefficient
			for (int i = 0; i < imageBlocks.size(); i++)
			{
				// Get and copy RGBBlock to decode (all bits)
				RGBBlock encodeBlock = imageBlocks.get(i);
				// Get RGBBlock to store decode result in
				RGBBlock decodeBlock = decodeImage.getImageBlock(i);
				
				// Apply bitmask to each block get first nSigBits significant bits, then decode
				decodeBlock.setRedBlock(this.idctBlock(this.bitmaskBlock(encodeBlock.redBlock(), nSigBits)));
				decodeBlock.setGreenBlock(this.idctBlock(this.bitmaskBlock(encodeBlock.greenBlock(), nSigBits)));
				decodeBlock.setBlueBlock(this.idctBlock(this.bitmaskBlock(encodeBlock.blueBlock(), nSigBits)));

				// Save decode block result to decodedImage (also updates decodedImage.BufferedImage)
				decodeImage.setImageBlock(i, decodeBlock);
			}
			
			// Display updated decoded image result
			_decodeDisplay.setSecondImage(decodeImage.getBufferedImage());
			
			// Sleep for latency time between decoding iterations
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
	
	private int getBitSign(int n)
	{
		// shift int over by 24 bits (3 bytes) to get sign bit
		int signBit = (n >> 24) & 1;
		if (signBit == 1)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
	
	// Creates a new matrix containing numBits significant bits of each element in input matrix
	private Matrix bitmaskBlock(Matrix m, int numBits)
	{
		double[][] maskedBlock = new double[BLOCK_SIZE][BLOCK_SIZE];
		int bitsToShift = 32 - numBits;
		for (int y = 0; y < BLOCK_SIZE; y++)
		{
			for (int x = 0; x < BLOCK_SIZE; x++)
			{
				int coeff = (int)m.get(x, y);
				int sign = this.getBitSign(coeff);
				coeff = Math.abs(coeff); // get absolute value of coefficient to avoid 2's compliment conversion
				// Bit_sign * numBits-1 bits of coefficient
				maskedBlock[x][y] = (double)(sign * (coeff >> bitsToShift));
			}
		}
		
		return new Matrix(maskedBlock);
	}
	
	// Test program to validate dct/idct logic
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
