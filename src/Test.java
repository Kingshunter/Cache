import java.util.ArrayList;
import java.util.List;

import cache.Cache;
import cache.DataOperation;
import cache.LocalAutoCacheRefreshManager;



public class Test {
    public static void main(String[] args) throws InterruptedException {
        Cache.init();
        
        DataOperation dataOperation = new DataOperation() {
			
			public Object operate() {
				List<String> list = new ArrayList<String>();
				list.add("2232");
				list.add("sfsf");
				list.add("3432424");
				return list;
			}
		};
        LocalAutoCacheRefreshManager local =
        		new LocalAutoCacheRefreshManager("22", "3", dataOperation);
        
        System.out.println(local.operateCache());
        
        Cache.set("11", "aa", "20s");
        System.out.println(Cache.get("11"));
    }
    
    
}
class Task implements Runnable {

    public void run() {
        boolean flag = Cache.add("11", "bbb", "1mn");
        System.out.println(flag);
    }
    
}