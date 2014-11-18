package module.ale.handler;

public abstract class CaptureButtonBaseHandler
{
	boolean enable = false;

	public void startCapture()
	{
		enable = true;
	}
	public void stopCapture()
	{
		enable = false;
	}
//	public boolean isEnable()
	public boolean isCapturing()
	{
		return enable;
	}
}
