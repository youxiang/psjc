package njscky.psjc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {
	
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/DCIM/Camera/PSJC/";
	/**
	 * 根据路径加载bitmap  将缩放后的bitmap返回去
	 *
	 * @param path 路径
	 * @param w    宽
	 * @param h    长
	 * @return
	 */
	public static  Bitmap convertToBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 设置为ture只获取图片大小
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 返回为空
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			float scaleWidth = 0.f, scaleHeight = 0.f;
			if (width > w || height > h) {//如果宽度大于 传入的宽度  或者 高度大于 传入的高度大于
				// 缩放
				scaleWidth = ((float) width) / w;
				scaleHeight = ((float) height) / h;
			}
			opts.inJustDecodeBounds = false;
			//缩放后的高度和宽度取最大值
			float scale = Math.max(scaleWidth, scaleHeight);
			opts.inSampleSize = (int) scale;//此处是最后的宽高值
//            //WeakReference 弱引用
//            WeakReference<Bitmap> weak = new WeakReference<Bitmap>actory.decodeFile(path, opts));
//            Bitmap bMapRotate = Bitmap.createBitmap(weak.get(BitmapF(), 0, 0, weak.get().getWidth(), weak.get().getHeight(), null, true);
			Bitmap bMapRotate = BitmapFactory.decodeFile(path, opts);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bMapRotate.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			ByteArrayInputStream bais=new ByteArrayInputStream(baos.toByteArray());
			Bitmap bitmap = BitmapFactory.decodeStream(bais);
			System.out.println("质量 压缩---------------width-" + bMapRotate.getWidth() + "---height--" + bMapRotate.getHeight()+"------------------size---"+bMapRotate.getRowBytes());
			if (bMapRotate != null) {
				return bMapRotate;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String saveBitmap(Bitmap bm, String picName) {
		try {
			if (!isFileExist(picName)) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName);

			if (f.exists()) {
				f.delete();

			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 70, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SDPATH + picName;
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

}
