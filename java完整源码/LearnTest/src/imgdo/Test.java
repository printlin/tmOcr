package imgdo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ImgFindRange;
import util.ImgUtil;
import util.LogisticModel;
import util.OCRCorrect;

public class Test {
	
	private String trueFolder="F:\\textImg\\trueFolder";
	private String falseFolder="F:\\textImg\\falseFolder";
	private ImgUtil iu=new ImgUtil();
	private LogisticModel lm=new LogisticModel();
	private Map<String,ImgData> datas=null;
	//st0:-9.434002324729786  st1:2.560251276719983  st2:1.412412361547835  st3:1.412412361547835  st4:7.83477796402232
	/*public Test() throws IOException{
		double[] sts={-7.050356633609126,3.0690114876533747,2.1189790628370955,2.1189790628370955,7.782309434478946,-0.7455159879748935,0.8711694138398449,-4.018696274554741};
		lm.setSts(sts);
		datas=initImgs();
	}*/
	
	/*
	 * 下面这个方法只对一个字进行了学习。将同样的字作为正样本，其他字为负样本，进行分类学习
	 * 如果衍生到所有字：
	 * 将所有字放在一起，文件名为该字。学习时判断文件名是否一致，一致则结果设置为1.其他为0
	 * 当然集合中一致的字是相对少的，这样会导致负样本过多，可以随机取固定比例的负样本进行学习。
	 * *//*
	public void learn() throws IOException{
		ImgUtil iu=new ImgUtil();
		File trueFile=new File(trueFolder);
		File[] trueFiles=trueFile.listFiles();
		File falseFile=new File(falseFolder);
		File[] falseFiles=falseFile.listFiles();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(int i=0,len=trueFiles.length;i<len;i++){//将正样本进行两两比较
			for(int j=i+1;j<len;j++){
				//System.out.println("i"+trueFiles[i].getName()+"  j"+trueFiles[j].getName());
				ImgData data=iu.formatLibImg(trueFiles[i]),img=iu.formatLibImg(trueFiles[j]);
				double[] re=iu.allMatch(data, img);
				Map<String,Object> map=new HashMap<String,Object>();
				//System.out.println(re[1]+"   "+re[2]+"  "+re[3]+"  "+re[4]+"  "+re[5]+"  "+re[6]+"  "+re[7]);
				map.put("x", re);
				map.put("y",1.0);//结果为相似
				list.add(map);
			}
		}
		for(int i=0,len=trueFiles.length;i<len;i++){//将正样本与每一个负样本进行比较
			for(int j=0,jLen=falseFiles.length;j<jLen;j++){
				ImgData data=iu.formatLibImg(trueFiles[i]),img=iu.formatLibImg(falseFiles[j]);
				double[] re=iu.allMatch(data, img);
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("x", re);
				map.put("y",0.0);//结果为不相似
				list.add(map);
			}
		}
		LogisticModel lm=new LogisticModel(list);
		lm.go();
	}
	
	public void learnAll() throws IOException{
		ImgUtil iu=new ImgUtil();
		File file=new File("F:\\tmImg\\all");
		File[] files=file.listFiles();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(int i=0,len=files.length;i<len;i++){//将正样本进行两两比较
			for(int j=i+1;j<len;j++){
				//System.out.println("i"+trueFiles[i].getName()+"  j"+trueFiles[j].getName());
				ImgData data=iu.formatImg(files[i]),img=iu.formatImg(files[j]);
				double[] re=iu.allMatch(data, img);
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("x", re);
				String fileNameI=files[i].getName();
				String fileNameJ=files[j].getName();
				String nameI=fileNameI.substring(0,1);
				String nameJ=fileNameJ.substring(0,1);
				if(nameI.equals(nameJ)){
					map.put("y",1.0);//结果为相似
				}else{
					map.put("y",0.0);//结果不为相似
				}
				list.add(map);
			}
		}
		LogisticModel lm=new LogisticModel(list);
		lm.go();
	}
	public Map<String,ImgData> initImgs() throws IOException{
		long d1=new Date().getTime();
		Map<String,ImgData> map=new HashMap<String,ImgData>();
		File file=new File("F:\\tmImg\\test");
		File[] files=file.listFiles();
		for(File f:files){
			map.put(f.getName().substring(0,1), iu.formatImg(f));
		}
		long d2=new Date().getTime();
		System.out.println("inti:"+(d2-d1));
		return map;
	}
	public String ocr(String uri) throws IOException{
		long d1=new Date().getTime();
		String re="";
		double ratio=0.0;
		ImgData img=iu.formatImg(uri);
		Set<String> set=datas.keySet();
		for(String key:set){
			double[] nubs=iu.allMatch(datas.get(key), img);
			double r=lm.function(nubs);
			if(ratio<r){
				ratio=r;
				re=key;
			}
		}
		long d2=new Date().getTime();
		System.out.println("ocr:"+(d2-d1));
		return re;
	}
	*/
	
	public static void main(String[] args) throws IOException {
		
		/*
		 * 13662
		 * 12836 第一次
		 * 6890 第二次
		 * 5372 第三次
		 * 2094 第四次
		 * */
		ImageOCR i=new ImageOCR();
		ImgUtil iu=new ImgUtil();
		/*Map<String, Object> m=i.ocrAll("F:\\tmImg\\jpgs\\33.jpg");
		List<String> ree=(List<String>) m.get("re");
		for(String s:ree){
			System.out.println(s);
		}
		System.out.println(m.get("useTime"));
		long b=new Date().getTime();
		List<ImgData> lists=(List<ImgData>) m.get("imgDs");
		lists=new OCRCorrect(new ImgFindRange(),i,new ImgUtil()).correct(lists);
		int count=0;
		String r="";
		for(ImgData img : lists){
			while(!img.re.equals("")){
				r+=img.re;
				System.out.println(img.re+"   "+img.rate+"   "+img.nextSpace);
				//ImageIO.write(iu.arrToImg(img.data), "jpg", new File("F:\\tmImg\\doc\\test\\"+count+img.re+".jpg"));
				img=img.nextImgData;
				count++;
			}
			r+="\n";
			System.out.println("\n");
		}
		long e=new Date().getTime();
		System.out.println(r);
		System.out.println(b-e);*/
		
		for(int a=1;a<=46;a++){
			Map<String, Object> m=i.ocrAll("F:\\tmImg\\jpgs\\"+a+".jpg");
			List<String> mapRe=(List<String>) m.get("re");
			String fRe="";
			for(String s:mapRe){
				fRe+=s+"\n";
			}
			fRe+="useTime:"+m.get("useTime")+"\n\n优化：\n";
			long b=new Date().getTime();
			List<ImgData> lists=(List<ImgData>) m.get("imgDs");
			lists=new OCRCorrect(new ImgFindRange(),i,new ImgUtil()).correct(lists);
			for(ImgData img : lists){
				while(!img.re.equals("")){
					fRe+=img.re;
					//System.out.println(img.re+"   "+img.rate+"   "+img.nextSpace);
					//ImageIO.write(iu.arrToImg(img.data), "jpg", new File("F:\\tmImg\\doc\\test\\"+count+img.re+".jpg"));
					img=img.nextImgData;
				}
				fRe+="\n";
				//System.out.println("\n");
			}
			long e=new Date().getTime();
			fRe+="useTime:"+(e-b);
			TextToFile("F:\\tmImg\\jpgs\\"+a+".txt",fRe);
		}
		/*long d1=new Date().getTime();
		String r=new Test().ocr("F:\\tmImg\\all\\水270.jpg");
		System.out.println(r);
		long d2=new Date().getTime();
		System.out.println("useTime:"+(d2-d1));*/
		//new Test().learn();
		/*ImgUtil iu=new ImgUtil();
		String uri1="F:\\tmImg\\all\\零191.jpg";
		String uri2="F:\\tmImg\\test\\零.jpg";
		double[][] data=iu.formatImg(uri1),img=iu.formatImg(uri2);
		double[] re={1,0,0,0,0,0,0,0};
		re[1]=iu.shadowMatch(data,img);
		/*re[2]=iu.pixelMatch(data,img);
		re[3]=iu.blockMatch(data,img,ImgUtil.COUNT_PIX);
		re[4]=iu.blockMatch(data,img,ImgUtil.COUNT_SHADOW);
		re[5]=iu.blockMatch(data,img,ImgUtil.COUNT_FOCUS);
		re[6]=iu.blockMatch(data,img,ImgUtil.COUNT_DEFAULT);
		re[7]=iu.focusMatch(data,img);
		//-7.050356633609126  st1:3.0690114876533747  st2:2.1189790628370955  st3:2.1189790628370955  st4:7.782309434478946  st5:-0.7455159879748935  st6:0.8711694138398449  st7:-4.018696274554741
		//st0:-7.044970656774771  st1:3.0662936299831816  st2:2.1172289538273685  st3:2.1172289538273685  st4:7.775312630622242  st5:-0.7447390914346788  st6:0.8709672338008659  st7:-4.015922220620581  dj:0.07522232895826027   sum:5172
		//all  st0:-3.9614116370883994  st1:0.6479407551390237  st2:1.0897315599192503  st3:1.0897315599192503  st4:2.132322021156969  st5:0.2610681148638905  st6:0.9255061138010606  st7:-3.24872871793641  dj:0.045739092175042594   sum:4163
		LogisticModel lm=new LogisticModel();
		//double[] sts={-7.044970656774771,3.0662936299831816,2.1172289538273685,2.1172289538273685,7.775312630622242,-0.7447390914346788,0.8709672338008659,-4.015922220620581};
		double[] sts={-7.050356633609126,3.0690114876533747,2.1189790628370955,2.1189790628370955,7.782309434478946,-0.7455159879748935,0.8711694138398449,-4.018696274554741};
		//double[] sts={-3.9614116370883994,0.6479407551390237,1.0897315599192503,1.0897315599192503,2.132322021156969,0.2610681148638905,0.9255061138010606,-3.24872871793641};
		lm.setSts(sts);
		System.out.println(lm.function(re));
		/*ImgUtil iu=new ImgUtil();
		String uri1="F:\\test\\cut\\0-1.jpg";
		String uri2="F:\\test\\cut\\1-1.jpg";
		double[][] data=iu.formatImg(uri1),img=iu.formatImg(uri2);
		double re=iu.shadowMatch(data,img);
		double re1=iu.pixelMatch(data,img);
		double re2=iu.blockMatch(data,img,ImgUtil.COUNT_PIX);
		double re3=iu.blockMatch(data,img,ImgUtil.COUNT_SHADOW);
		double re4=iu.blockMatch(data,img,ImgUtil.COUNT_FOCUS);
		double re5=iu.blockMatch(data,img,ImgUtil.COUNT_DEFAULT);
		double re6=iu.focusMatch(data,img);
		System.out.println(re+"   "+re1+"  "+re2+"  "+re3+"  "+re4+"  "+re5+"  "+re6);
*/
		
		//0.8125   0.9375  0.9375  0.9375  0.9375 -4
		//0.6041666666666667   0.84375  0.84375  0.7734375  0.6875  -8
		//0.8979166666666667   0.8671875  0.8671875  0.9010416666666666  0.9627976190476191  -16
		//0.83546875   0.8662109375  0.8662109375  0.8193545386904761  0.7883183392694262  -32
		//0.8181501079492404   0.864990234375  0.864990234375  0.7936229623241342  0.7200556100603465  -64
		
		//0.5104166666666667   0.75  0.75  0.75  0.75  -4
		//0.453125   0.671875  0.671875  0.6015625  0.5 -8
		//0.5310081845238095   0.6875  0.6875  0.6555989583333334  0.7373511904761905   -16
		//0.6031664472680098   0.7080078125  0.7080078125  0.5777994791666666  0.6716569707466447  -32
		//0.5848044416633447   0.703369140625  0.703369140625  0.5741653420364359  0.6849640114452964  -64
	}
	public static void TextToFile(final String strFilename, final String strBuffer)  
	  {  
	    try  
	    {      
	      // 创建文件对象  
	      File fileText = new File(strFilename);  
	      // 向文件写入对象写入信息  
	      FileWriter fileWriter = new FileWriter(fileText);  
	  
	      // 写文件        
	      fileWriter.write(strBuffer);  
	      // 关闭  
	      fileWriter.close();  
	    }  
	    catch (IOException e)  
	    {  
	      //  
	      e.printStackTrace();  
	    }  
	  }  
}
