package com.your.namespace;

public class Main {
	public static void main(String args[]) {
		var img = new PngImage();
		img.setOpaque(null);
		img.setVersion(LibPNG.PNG_IMAGE_VERSION);

		LibPNG.pngImageBeginReadFromFile(img, "cat.png");
		img.setFormat(LibPNG.PNG_FORMAT_RGBA);
		var pixels = new byte[(int) (img.getWidth() * img.getHeight() * 4)];
		LibPNG.pngImageFinishRead(img, null, pixels, 0, null);
	}
}
