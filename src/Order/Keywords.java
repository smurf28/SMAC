package Order;

import java.util.HashSet;
import java.util.Set;
/*
 * The class is set the keywords which will use in the system
 * */
public class Keywords {
	private static final String []words={"let","save","log","reset","setprecision","saved","loged","load","precision"};
	Set<String> keywords ;
	public Keywords(){
		keywords = new HashSet<String>();
		for(int i=0;i<words.length;i++){
			keywords.add(words[i]);
		}
	}
	public Set<String> getKeywords(){
		return keywords;
	}
}
