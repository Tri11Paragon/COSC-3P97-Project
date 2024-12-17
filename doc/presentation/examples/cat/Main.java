import com.sun.jna.*;

public class Main {

	@Structure.FieldOrder({"opaque", "version", "width", "height", "format", "flags", "colormapEntries", "warningOrError", "message"})
	public static class PNGImage extends Structure {
		public Pointer opaque;
		public int version, width, height, format, flags, colormapEntries, warningOrError;
		public byte[] message = new byte[32];
	}

	public static interface LibPNG extends Library {
		LibPNG Instance = (LibPNG) Native.load("png", LibPNG.class);
		int png_image_begin_read_from_file(PNGImage img, String file);
		int png_image_finish_read(PNGImage img, Pointer bg, byte[] data, int rowStride, Pointer colormap);
	}

	public static void main(String args[]) {
		var img = new PNGImage();
		img.opaque = null;
		img.version = 1;

		LibPNG.Instance.png_image_begin_read_from_file(img, "cat.png");
		img.format = 3; // PNG_FORMAT_RGBA
		var pixels = new byte[img.width * img.width * 4];
		LibPNG.Instance.png_image_finish_read(img, null, pixels, 0, null);

		for (int i = 0; i < pixels.length; i+=4) {
			System.out.println(String.format("%02x%02x%02x%02x", pixels[i], pixels[i+1], pixels[i+2], pixels[i+3]));
		}
	}
}
