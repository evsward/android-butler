package com.evsward.butler.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Build;

public class ImageUtil {
	private static final String TAG = "ImageUtil";

	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			LogUtil.se(TAG, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	protected static int getSizeOfBitmap(Bitmap data) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			return data.getRowBytes() * data.getHeight();
		} else {
			return data.getByteCount();
		}
	}

	public static byte[] compressBitmap(Bitmap bitmap, float size) {
		if (bitmap == null || getSizeOfBitmap(bitmap) <= size) {
			return null;// 如果图片本身的大小已经小于这个大小了，就没必要进行压缩
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 如果签名是png的话，则不管quality是多少，都不会进行质量的压缩
		int quality = 100;
		while (baos.toByteArray().length / 1024f > size) {
			quality = quality - 4;// 每次都减少4
			baos.reset();// 重置baos即清空baos
			if (quality <= 0) {
				break;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		}
		return baos.toByteArray();
	}
}
