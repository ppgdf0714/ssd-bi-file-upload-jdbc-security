package jp.co.ssd.bi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jp.co.ssd.bi.service.FileUploadService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BiFileUploadApplicationTests {
//https://github.com/ppgdf0714/ssd-bi-file-upload.git

	
	@Autowired
	FileUploadService fileUploadService;
	
	@Value("${jp.co.sdd.bi.xmlname}")
	private String config;

    @Test
    public void selectAll() throws Exception {
//        List<AkfrStk> userList=akfrStkMapper.getAkfrStkInfo();
//        for (int i=0;i<userList.size();i++){
//            System.out.println(userList.get(i));
//        }
//    	AkfrStk akfrStk = new AkfrStk();
//    	Map<String,String> keyValues=new HashMap<>();
//    	keyValues.put("課名", "一課");
//    	keyValues.put("ユニット名", "ユニット１");
//    	keyValues.put("案件名", "案件一");
//    	keyValues.put("件数", "1");
//    	
//    	BeanUtils.populate(akfrStk,keyValues);
//    	
//    	System.out.println(akfrStk.get課名()+akfrStk.getユニット名()+akfrStk.get案件名()+akfrStk.get件数());
    	
//    	String A = "select 課名,ユニット名,案件名 from 案件振り返り_指摘";
//    	List<AkfrStk> userList=akfrStkMapper.getAkfrStkInfo1(A);
//    	
//    	for (int i=0;i<userList.size();i++){
//         System.out.println(userList.get(i));
//      }
    	
//    	String B = "insert into 案件振り返り_指摘(課名, ユニット名, 案件名, 会社区分) VALUES ('課テスト','ユニットテスト','案件テスト','会社テスト')";
//    	akfrStkMapper.addAkfrStkInfo(B);
//    	
//    	String cC
//    	= "insert into 案件振り返り_指摘(課名, ユニット名, 案件名, 会社区分) VALUES ('課テスト1','ユニットテスト1','案件テスト1','会社テスト1')";
//    	akfrStkMapper.addAkfrStkInfo(B);
    	
    	// XMLファイル読み込み	
    	//Map<String,List<String>> contentMap = fileUploadService.xmlLoad(config);
    	// excelファイル読み込み
    	//String cellContentString = fileUploadService.getExcelData(file, contentMap.get(UploadCommonConst.案件振り返り_指摘));	    
    	
    	
//    	List<Double> cost = Arrays.asList(10.0, 20.0,30.0);
//        double allCost = cost.stream().map(x -> x+x*0.05).reduce((sum,x) -> sum + x).get();
//        System.out.println(allCost);
    	System.out.println(isNumber("abc"));
    	
    }
    
    
    public static boolean isNumber(String str){

        String reg = "^[0-9]+(.[0-9]+)?$";

        return str.matches(reg);

    }
    
    /**
     * 把一个字符串转换成bean对象
     * @param str
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
    	return (T)str;

    }
}
