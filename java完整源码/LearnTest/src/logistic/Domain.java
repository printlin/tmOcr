package logistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Domain {
	private double[] sts=null;//参数Θ
	private double a=0.1;//学习速率
	private List<Map<String,Object>> list=null;
	public Domain(){
		this.list=new ArrayList<Map<String,Object>>();
		formatData();
	}
	public static void main(String[] args) {
		new Domain().go();
	}
	private void formatData() {//数据初始化  {x0,x1,...xn,y}x0默认1，因为他总是x的0次方
		
		/*double[][] data={{1,0.8,0.95,1},
						{1,0.4,0.2,0},
						{1,0.2,0.1,0},
						{1,0.95,0.75,1},
						{1,0.1,0.75,0},
						{1,0.8,0.75,1},
						{1,0.96,0.77,1},
						{1,0.6,0.89,1},
						{1,0.7,0.89,1},
						{1,0.66,0.96,1},
						{1,0.85,0.89,1},
						{1,0.2,0.1,0},
						{1,0.6,0.1,0},
						{1,0.88,0.2,0},
						{1,0.22,0.45,0},
						{1,0.32,0.12,0},
						{1,0.1,0.01,0},
						{1,0.89,0.89,1},
						{1,0.5,0.5,0}};*/
		double[][] data={{1,0.95,1},
				{1,0.2,0},
				{1,0.1,0},
				{1,0.75,1},
				{1,0.75,0},
				{1,0.75,1},
				{1,0.77,1},
				{1,0.89,1},
				{1,0.89,1},
				{1,0.96,1},
				{1,0.89,1},
				{1,0.1,0},
				{1,0.1,0},
				{1,0.2,0},
				{1,0.45,0},
				{1,0.12,0},
				{1,0.01,0},
				{1,0.89,1},
				{1,0.5,0}};

		sts=new double[data[0].length-1];//设置参数的个数：x个数减1
		for(int i=0;i<data.length;i++){//遍历每一排
			double[] ff=new double[data[i].length-1];
			for(int j=0;j<data[i].length-1;j++){
				ff[j]=data[i][j];
			}
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("x", ff);
			map.put("y",data[i][data[i].length-1]);
			list.add(map);
		}
	}
	public double function(double[] xs){//函数模型
		double re=0f;
		for(int i=0;i<xs.length;i++){
			re+=xs[i]*sts[i];
		}
		return 1/(Math.pow(Math.E, -re)+1);//logistic函数
	}
	public void update(){//模型参数的更新
		double[] stss=new double[sts.length];//新的模型参数，此处单独用数组来装而不是直接对该参数赋值，是为了不影响下一个参数的学习，保证每个参数对应的都是同一个函数
		int len=list.size();
		for(int i=0;i<stss.length;i++){//遍历每一个参数
			double sum=0f;
			for(Map<String,Object> map:list){
				double[] xs=(double[])map.get("x");
				double y=(double)map.get("y");
				sum+=(function(xs)-y)*xs[i];
			}
			//System.out.println("js---:"+a*(1.0f/len)*sum);
			stss[i]=sts[i]-a*(1.0f/len)*sum;//更新该参数
		}
		sts=stss;//统一更新参数
	}
	public double dj(){//代价函数
		double sum=0f;
		for(Map<String,Object> map:list){
			double[] xs=(double[])map.get("x");
			double y=(double)map.get("y");
			sum+=y*Math.log(function(xs))+(1-y)*Math.log(1-function(xs));
		}
		return -(1.0f/list.size())*sum;
	}
	public void go(){
		int sum=0;//参数迭代次数
		while(true){
			double oldDj=dj();//迭代前损失
			sum++;
			if(sum>=10000){//迭代次数不超过10000
				break;
			}
			update();//迭代
			double newDj=dj();//迭代后损失
			if(Math.abs(newDj-oldDj)<0.00001){//如果损失小于0.0001则认为已经拟合
				break;
			}
		}
		for(int j=0;j<sts.length;j++){
			System.out.print("st"+j+":"+sts[j]+"  ");
		}
		System.out.println("dj:"+dj()+"   sum:"+sum);
		/*for(int i=0;i<5;i++){
			update();
			for(int j=0;j<sts.length;j++){
				System.out.print("st"+j+":"+sts[j]+"  ");
			}
			System.out.println("dj:"+dj());
		}*/
		/*for(int j=0;j<sts.length;j++){
			System.out.print("st"+j+":"+sts[j]+"  ");
		}
		System.out.println("dj:"+dj());*/
		double[] fff={1,0.99};
		System.out.println(function(fff));
	}
}
