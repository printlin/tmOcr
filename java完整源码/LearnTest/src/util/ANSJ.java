/**
 * 
 */
package util;

import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class ANSJ {
	/**
	 * @author Administrator
	 * @Description
	 */
	@Test
	public void test() {
		String str = "企业注册号:91120222671480180P企业名称:缤致时装销售际己宰有跟公司";
		Result r=ToAnalysis.parse(str);
		List<Term> t=r.getTerms();
		for(Term tt:t){
			System.out.println(tt.getRealName()+"   "+tt.getNatureStr());
		}
		System.out.println();
	}
	
	 
}
