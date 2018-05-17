package imgdo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import util.ImgTextCut;
import util.ImgUtil;
import util.LogisticModel;

public class ImageOCR {
	private ImgUtil iu=null;
	private LogisticModel lm=null;
	private ImgTextCut itc=null;
	private Map<String,ImgData> datas=null;
	private static final String FONTS="F:\\tmImg\\test";
	
	public ImageOCR() throws IOException{
		iu=new ImgUtil();
		lm=new LogisticModel();
		itc=new ImgTextCut();
		//st0:-6.981061236123684  st1:2.233293540390179  st2:3.1899210507408884  st3:-3.9201795047703465  st4:2.233293540390179  st5:7.944151841420101  st6:-0.6043532255244712
		//st0:  st1:2.560251276719983  st2:1.412412361547835  st3:1.412412361547835  st4:7.83477796402232
		double[] sts={-6.981061236123684,2.233293540390179,3.1899210507408884,-3.9201795047703465,2.233293540390179,7.944151841420101,-0.6043532255244712};
		//double[] sts={-7.31182648573737,1.3013220942716823,3.548516570740642,-3.9444620179313317,1.3013220942716823,8.876484111091447,0.7893895024512816};
		

		lm.setSts(sts);
		datas=initImgs(FONTS);
	}

	
	private Map<String,ImgData> initImgs(String uri) throws IOException{
		long d1=new Date().getTime();
		Map<String,ImgData> map=new HashMap<String,ImgData>();
		File file=new File(uri);
		File[] files=file.listFiles();
		for(File f:files){
			ImgData imgD=iu.formatLibImg(f);
			//ImageIO.write(imgD.img, "jpg", new File("F:\\tmImg\\doc\\beanTest\\lib\\"+f.getName()));
			map.put(f.getName(), imgD);
		}
		long d2=new Date().getTime();
		System.out.println("inti:"+(d2-d1));
		return map;
	}
	public boolean ocrIsChar(double[][] img,char c){
		ImgData data=datas.get(c+".jpg");
		if(data!=null){
			double[] nubs=iu.allMatch2(data.data, iu.formatData(img));
			double r=lm.function(nubs);
			if(r>0.5){
				return true;
			}
		}
		return false;
	}
	public Map<String,Object> ocr(double[][] img){
		long d1=new Date().getTime();
		Map<String,Object> re=new HashMap<String,Object>();
		String ree="";
		double ratio=0.0;
		Set<String> set=datas.keySet();
		for(String key:set){
			double[] nubs=iu.allMatch2(datas.get(key).data, iu.formatData(img));
			double r=lm.function(nubs);
			if(ratio<r){
				ratio=r;
				ree=key;
			}
		}
		long d2=new Date().getTime();
		re.put("key",formatFileName(ree));
		re.put("useTime",d2-d1);
		re.put("ratio",ratio);
		return re;
	}
	public Map<String,Object> ocr(String uri) throws IOException{
		long d1=new Date().getTime();
		Map<String,Object> re=new HashMap<String,Object>();
		String ree="";
		double ratio=0.0;
		ImgData img=iu.formatLibImg(new File(uri));
		Set<String> set=datas.keySet();
		for(String key:set){
			double[] nubs=iu.allMatch(datas.get(key), img);
			double r=lm.function(nubs);
			if(ratio<r){
				ratio=r;
				ree=key;
			}
		}
		long d2=new Date().getTime();
		re.put("key",formatFileName(ree));
		re.put("useTime",d2-d1);
		re.put("ratio",ratio);
		return re;
	}
	
	public Map<String,Object> ocrAll(String uri) throws IOException{
		long d1=new Date().getTime();
		List<ImgData> lists=itc.cutToArr(uri);
		Map<String,Object> re=new HashMap<String,Object>();
		List<String> ree=new ArrayList<String>();
		Set<String> set=datas.keySet();
		double ratio=0.0;
		for(ImgData img : lists){
			StringBuffer sb=new StringBuffer();
			while(img.data!=null){
				img=iu.formatImg(img);
				String reee="";
				for(String key:set){
					double[] nubs=iu.allMatch(datas.get(key), img);
					double r=lm.function(nubs);
					if(ratio<r){
						ratio=r;
						reee=key;
					}
				}
				sb.append(formatFileName(reee));
				System.out.println(reee+"   "+ratio);
				img.re=formatFileName(reee);
				img.rate=ratio;
				ratio=0.0;
				img=img.nextImgData;
			}
			/*for(BufferedImage data:list){
				ImgData img=iu.formatImg(data);
				String reee="";
				for(String key:set){
					double[] nubs=iu.allMatch(datas.get(key), img);
					double r=lm.function(nubs);
					if(ratio<r){
						ratio=r;
						reee=key;
					}
				}
				sb.append(formatFileName(reee));
				System.out.println(reee+"   "+ratio);
				ratio=0.0;
			}*/
			ree.add(sb.toString());
		}

		long d2=new Date().getTime();
		re.put("re",ree);
		re.put("useTime",d2-d1);
		re.put("ratio",ratio);
		re.put("imgDs", lists);
		return re;
	}
	
	public Map<String,Object> ocr(File ff) throws IOException{
		long d1=new Date().getTime();
		Map<String,Object> re=new HashMap<String,Object>();
		String ree="";
		double ratio=0.0;
		ImgData img=iu.formatLibImg(ff);
		Set<String> set=datas.keySet();
		for(String key:set){
			double[] nubs=iu.allMatch(datas.get(key), img);
			double r=lm.function(nubs);
			if(ratio<r){
				ratio=r;
				ree=key;
			}
		}
		long d2=new Date().getTime();
		re.put("key",formatFileName(ree));
		re.put("useTime",d2-d1);
		re.put("ratio",ratio);
		return re;
	}
	
	private String formatFileName(String data) {
		data=data.substring(0, data.lastIndexOf("."));
		if(data!=null && "&".equals(data.substring(0, 1))){
			if("&lt".equals(data)){
				return "<";
			}else if("&gt".equals(data)){
				return ">";
			}else if("&x".equals(data)){
				return "/";
			}else if("&fx".equals(data)){
				return "\\";
			}else if("&quot".equals(data)){
				return "\"";
			}else if("&all".equals(data)){
				return "*";
			}else if("&laquo".equals(data)){
				return "?";
			}else if("&brvbar".equals(data)){
				return "|";
			}else if("&mh".equals(data)){
				return ":";
			}else if("&d".equals(data)){
				return ".";
			}
		}
		return data.substring(0, 1);
	}
}
