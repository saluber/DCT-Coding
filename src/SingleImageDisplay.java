import java.awt.image.*;
import javax.swing.*;

public class SingleImageDisplay
{
	public static final int HORIZONTAL_PADDING = 50;
	public static final int VERTICAL_PADDING = 50;
	public static final int TEXT_LABEL_HEIGHT = 25;
	public static final int IMAGE_WIDTH = 512;
	public static final int IMAGE_HEIGHT = 512;
	public static final int WINDOW_WIDTH = 662;
	public static final int WINDOW_HEIGHT = 762;
	
	private String _containerTitle = "Single Image Display";
	private String _imageTitle = "Image";
	private JFrame _frame;
	private JPanel _basePanel;
	private JLabel _imageLabel;
	
	public SingleImageDisplay(String containerTitle, String imageTitle, String[] labels)
	{
		if (containerTitle != null)
		{
			_containerTitle = containerTitle;
		}
		if (imageTitle != null)
		{
			_imageTitle = imageTitle;
		}
		
		initDisplay(labels);
	}
	
	public void setImage(BufferedImage image)
	{
		// Create image label
		JLabel nextImageLabel = new JLabel(new ImageIcon(image));
		nextImageLabel.setLocation(SingleImageDisplay.HORIZONTAL_PADDING, SingleImageDisplay.VERTICAL_PADDING);
		nextImageLabel.setSize(image.getWidth(), image.getHeight());
		nextImageLabel.setHorizontalAlignment(JLabel.LEFT);
		
		// Swap new image label with existing image label
		_basePanel.add(nextImageLabel);
		if (_imageLabel != null)
		{
			_basePanel.remove(_imageLabel);
		}
		
		// Store reference to new image label
		_imageLabel = nextImageLabel;
		
		// "Refresh" view
		_frame.setContentPane(_basePanel);
		_frame.setVisible(true);
	}
	
	private void initDisplay(String[] labels)
	{
		// Create JFrame
		_frame = new JFrame(_containerTitle);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(SingleImageDisplay.WINDOW_WIDTH, SingleImageDisplay.WINDOW_HEIGHT);
		
		// Create JPanel
		_basePanel = new JPanel();
		_basePanel.setLayout(null);
		_basePanel.setOpaque(true);
		// Draw image title
		JLabel imageTitleLabel = createTextLabel(
				_imageTitle,
				SingleImageDisplay.HORIZONTAL_PADDING,
				SingleImageDisplay.VERTICAL_PADDING/2);
		_basePanel.add(imageTitleLabel);
		// Draw additional text labels (if any)
		if (labels != null)
		{
			int y_position = SingleImageDisplay.VERTICAL_PADDING*3/2 + SingleImageDisplay.IMAGE_HEIGHT;
			for (int i = 0; i < labels.length; i++)
			{
				JLabel textLabel = createTextLabel(
						labels[i],
						SingleImageDisplay.HORIZONTAL_PADDING,
						y_position);
				_basePanel.add(textLabel);
				y_position += (SingleImageDisplay.VERTICAL_PADDING/2);
			}
		}
		
		// Attach JPanel to JFrame
		_frame.setContentPane(_basePanel);
		_frame.setVisible(true);
	}
	
	private JLabel createTextLabel(String text, int x_position, int y_position)
	{
		JLabel textLabel = new JLabel(text);
		textLabel.setLayout(null);
		textLabel.setLocation(
				x_position,
				y_position);
		textLabel.setSize(
				SingleImageDisplay.IMAGE_WIDTH,
				SingleImageDisplay.TEXT_LABEL_HEIGHT);
		textLabel.setHorizontalTextPosition(JLabel.CENTER);
		
		return textLabel;
	}
}
