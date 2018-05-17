package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FolderFormat {
	/**
	 * 文件名格式化
	 * text.txt中保存图片中的文字，一个文字对应一张图片
	 * 遍历文件，修改文件名
	 */
	public void go() throws IOException {
		char[] text=new char[2048];
		InputStreamReader in=new InputStreamReader(new FileInputStream("F:\\tmImg\\yb\\2\\text.txt"),"gbk");
		in.read(text);
		for(int i=0;i<=312;i++){
			File f=new File("F:\\tmImg\\yb\\2\\"+i+".jpg");
			File ff=new File("F:\\tmImg\\yb\\2\\"+text[i]+i+".jpg");
			f.renameTo(ff);
		}
		in.close();
	}
	public static void main(String[] args) throws IOException {
		new FolderFormat().go();
	}
}
