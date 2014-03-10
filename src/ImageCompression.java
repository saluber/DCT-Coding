
public class ImageCompression
{
	// Constants
	private static final Integer ImageWidth = 352;
	private static final Integer ImageHeight = 288;
	private static final String ProjectTitle = "CSCI 576: Assignment 2";
	private static final String OriginalImageTitle = "Original Image:";
	private static final String DecodedImageTitle = "Decoded Image:";
	private static final String[] InputArgumentNamesList = {
		"Image file path: ",
		"Quantization level: ",
		"Delivery mode: ",
		"Latency (ms): "
	};
	private static final String[] DeliveryModesList = {
		"Baseline Delivery",
		"Progressive Delivery (Spectral Selection)",
		"Progressive Delivery (Successive Bit Approximation)"
	};
	
	// Member variables
	private static DoubleImageDisplay _display;
	public static MultiFormatImage _originalImage;
	
	// Input arguments
	private static String _imageFilePath; // Path to starting rgb image file
	private static Integer _quantizationLevel; // quantization level (ranges from 0-7)
	private static Integer _deliveryMode; // 1 = baseline delivery, 2 = progressive delivery (spectral selection), 3 = progression delivery (successive bit approximation)
	private static Integer _latency; // "sleep time" (in milliseconds) between data blocks during decoding
	private static String[] _inputArgumentsList;
	
	/* Private Helper Methods */
	// Parses command line arguments into passed-by-references and returns whether argument values are valid
	private static boolean parseCommandLineInput(String[] args)
	{
		boolean isValidInput = true;
		
		// Check for expected number of input arguments
		if (args.length != 4)
		{
			System.out.println("Error: Invalid number of input arguments.");
			isValidInput = false;
		}
		else
		{
			// Parse command line input
			try
			{
				_imageFilePath = args[0];
				_quantizationLevel = Integer.parseInt(args[1]);
				if ((_quantizationLevel < 0) || (_quantizationLevel > 7))
				{
					System.out.println("Error: Invalid value specified for quantization level. Valid range is [0-7].");
					isValidInput = false;
				}
				
				_deliveryMode = Integer.parseInt(args[2]);
				if ((_deliveryMode < 1) || (_deliveryMode > 3))
				{
					System.out.println("Error: Invalid value specified for delivery mode. Valid range is [1-3].");
					isValidInput = false;
				}
				
				_latency = Integer.parseInt(args[3]);
				if (_latency < 0)
				{
					System.out.println("Error: Invalid value specified for latency. Valid range is [0," + Integer.MAX_VALUE + "]");
					isValidInput = false;
				}
			}
			catch (Exception e)
			{
				System.out.println("Error: Unexpected format of input arguments.");
				System.out.println("Exception thrown while parsing input args: " + e.toString());
				isValidInput = false;
			}
			
			// Store "output friendly" list of input strings (if valid input)
			if (isValidInput)
			{
				_inputArgumentsList = new String[4];
				_inputArgumentsList[0] = new String(InputArgumentNamesList[0] + _imageFilePath);
				_inputArgumentsList[1] = new String(InputArgumentNamesList[1] + _quantizationLevel);
				_inputArgumentsList[2] = new String(InputArgumentNamesList[2] + DeliveryModesList[_deliveryMode]);
				_inputArgumentsList[3] = new String(InputArgumentNamesList[3] + _latency);
			}
		}
		
		return isValidInput;
	}
	
	public static void main(String[] args)
	{
		if (parseCommandLineInput(args) == false)
		{
			// Invalid arguments
			System.out.println("Exiting program due to invalid input arguments.");
			return;
		}
		
		// Parse original image and update display
		_originalImage = new MultiFormatImage(_imageFilePath, ImageWidth, ImageHeight);
		if (!_originalImage.IsValidImage())
		{
			System.out.println("Exiting program due to invalid image.");
			return;
		}
		
		// Initialize program output display and show first image
		_display = new DoubleImageDisplay(ProjectTitle, OriginalImageTitle, DecodedImageTitle, _inputArgumentsList);
		_display.setFirstImage(_originalImage.GetBufferedImage());
		
	}
}
