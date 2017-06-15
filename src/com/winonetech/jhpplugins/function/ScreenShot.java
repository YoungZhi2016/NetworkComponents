package com.winonetech.jhpplugins.function;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.winonetech.jhpplugins.utils.Configs;
import com.winonetech.jhpplugins.utils.Logs;

/**
 * 
 * @author ywkj ScreenShot
 *
 */
public final class ScreenShot {

	private Dimension aDimension;

	private static class ShotHolder {
		private static ScreenShot aScreenshot = new ScreenShot();
	}

	private ScreenShot() {
		aDimension = Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static ScreenShot getInstance() {
		return ShotHolder.aScreenshot;
	}

	/**
	 * default directory config
	 * 
	 * default replace true
	 */
	public void snapShot(String fileName, String suffix) throws AWTException, IOException {
		snapShot(Configs.getScreenShotPath(), fileName, suffix, true);
	}

	/**
	 * default directory config
	 */
	public void snapShot(String fileName, String suffix, boolean replace) throws AWTException, IOException {
		snapShot(Configs.getScreenShotPath(), fileName, suffix, replace);
	}

	/**
	 * default replace true
	 */
	public void snapShot(String directory, String fileName, String suffix) throws AWTException, IOException {
		snapShot(directory, fileName, suffix, true);
	}

	/**
	 * 截屏
	 * 
	 * @param directory
	 *            file save directory
	 * @param fileName
	 * 
	 * @param suffix
	 *            not contain .
	 * @param replace
	 *            if the file exists to be replaced
	 */
	public void snapShot(String directory, String fileName, String suffix, boolean replace)
			throws AWTException, IOException {
		String name = directory + "/" + fileName + "." + suffix;// FilePathName
		File aFile = new File(name);

		if (aFile.exists() && !replace) {
			// if the file exists
			Logs.info("this file exist " + name);
			return;
		}

		if (!aFile.getParentFile().exists() || !aFile.getParentFile().isDirectory()) {
			aFile.mkdirs();
		}

		BufferedImage aImage = (new Robot())
				.createScreenCapture(new Rectangle(0, 0, (int) aDimension.getWidth(), (int) aDimension.getHeight()));
		ImageIO.write(aImage, suffix, aFile);
		Logs.info("save image finished! " + name);
	}
}
