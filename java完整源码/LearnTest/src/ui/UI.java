package ui;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import imgdo.ImageOCR;

public class UI {
	private JFrame jf;
	private JLabel img,text,t1,t2,zx,ys;
	private JButton btn,init;

	private ImageOCR ocr=null;
	public UI(){
		jf=new JFrame("文字识别");
		jf.setSize(50, 50);
		jf.setLayout(null);
		t1=new JLabel("图片：");
		t1.setBounds(10, 1, 100, 30);
		t2=new JLabel("识别结果：");
		t2.setBounds(130, 1, 100, 30);
		img=new JLabel();
		img.setBounds(10, 50, 30, 30);
		text=new JLabel();
		text.setFont(new Font("微软雅黑",Font.PLAIN,25));
		text.setBounds(130, 50, 30, 30);
		zx=new JLabel("置信度：");
		zx.setBounds(10, 90, 200, 30);
		ys=new JLabel("用时：");
		ys.setBounds(10, 110, 200, 30);
		init=new JButton("初始化");
		init.setBounds(5, 140, 80, 40);
		btn=new JButton("识别");
		btn.setBounds(130, 140, 80, 40);
		jf.add(t1);
		jf.add(t2);
		jf.add(img);
		jf.add(text);
		jf.add(btn);
		jf.add(init);
		jf.add(zx);
		jf.add(ys);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		jf.setSize(230,230);
		jf.setLocation((screen_width - jf.getWidth()) / 2,
				(screen_height - jf.getHeight()) / 2);
		jf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
		jf.setVisible(true);
		init();
	}
	private void init(){
		init.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ocr=new ImageOCR();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 JFileChooser jfc=new JFileChooser("F:\\tmImg\\all");  
			     jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
			     jfc.showDialog(new JLabel(), "选择欲识别图片");  
			     File file=jfc.getSelectedFile();
			     if(file!=null){
			    	img.setIcon(new ImageIcon(file.getAbsolutePath()));
				    try {
				    	Map<String,Object> map=ocr.ocr(file);
						text.setText((String)map.get("key"));
						zx.setText("置信："+(double)map.get("ratio"));
						ys.setText("用时："+(long)map.get("useTime")+"ms");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			     }
			     
			}
		});
	}
	public static void main(String[] args) {
		new UI();
	}
}
