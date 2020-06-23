package jp.co.ssd.bi.service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.co.ssd.bi.constant.UploadCommonConst;
import jp.co.ssd.bi.mapper.UploadMapper;
import jp.co.ssd.bi.model.MyException;
import jp.co.ssd.bi.util.DBUtil;

@Service
public class FileUploadService {
	private static Logger logger = LoggerFactory.getLogger(FileUploadService.class);
	
	
	@Autowired
	DBUtil dbutil;
	
	@Autowired
	UploadMapper uploadMapper;

	/**
     *　XMLファイル読み込み
     *
     * @param config XMLファイル
     * @param filetype ファイルタイプ
     * @return xmlData　XMLファイル情報
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public Map<String,List<String>> xmlLoad(String filetype,String config) throws Exception{
		SAXReader reader = new SAXReader();
		File filetmp = new File(config);
		Document document = reader.read(filetmp);
		Element root = document.getRootElement();
		
		Map<String,List<String>> xmlData = new LinkedHashMap<String,List<String>>();
		
		//テーブル名
		String tableString = null;		
		String value = null;
		String nameString = null;
		String valueString = null;
		String typeString = null;
		String sheetString = null;
		List<Element> nodes = root.elements("table");		
		for (Iterator<Element> it = nodes.iterator(); it.hasNext();) {
			//一時リスト
			List<String> tmpList = new ArrayList<String>();
			Element elm = (Element) it.next();
			Attribute tbattribute = elm.attribute("tablename");
			tableString = tbattribute.getText();
			List<Element> elmChild = elm.elements("column");
			for (Iterator<Element> childIterator = elmChild.iterator(); childIterator
					.hasNext();) {
				Element element = (Element) childIterator.next();
				Attribute attribute = element.attribute("sheet");
				Element nameElement = element.element("name");
				Element valueElement = element.element("value");
				Element typeElement = element.element("type");
				nameString = nameElement.getText();
				valueString = valueElement.getText();
				typeString = typeElement.getText();
				sheetString = attribute.getText();
				
				if(UploadCommonConst.案件管理一覧.equals(tableString)) {
					String tmpCloum[] = nameString.split(",");
					String tmpData[] = valueString.split(",");
					String tmptype[] = typeString.split(",");
					for(int row = Integer.parseInt(tmpData[0]);row <=Integer.parseInt(tmpData[1]);row++){
						for(int colum = 0;colum <= tmpCloum.length - 1;colum++) {
							value = sheetString + "," + tmpCloum[colum] + "," + tmpCloum.length + "," + tmptype[colum] + "," + row + "," + (colum+1);
							tmpList.add(value);
						}
					}					
				}else {
					value = sheetString + "," + nameString + "," + typeString + "," + valueString;
					tmpList.add(value);
				}				
			}
			if(UploadCommonConst.案件振り返り_指摘.equals(tableString) && UploadCommonConst.タイプ_案件振り返り.equals(filetype)) {
				xmlData.put(UploadCommonConst.案件振り返り_指摘, tmpList);
			}else if(UploadCommonConst.案件振り返り_欠陥.equals(tableString) && UploadCommonConst.タイプ_案件振り返り.equals(filetype)){
				xmlData.put(UploadCommonConst.案件振り返り_欠陥, tmpList);
			}else if(UploadCommonConst.案件振り返り_生産性実績.equals(tableString) && UploadCommonConst.タイプ_案件振り返り.equals(filetype)){
				xmlData.put(UploadCommonConst.案件振り返り_生産性実績, tmpList);
			}else if(UploadCommonConst.案件管理一覧.equals(tableString)  && UploadCommonConst.タイプ_案件一覧.equals(filetype)){
				xmlData.put(UploadCommonConst.案件管理一覧, tmpList);	
			}			
		}		
	return xmlData;		
	}
	
	
	
	/**
     *　excelファイル読み込み
     *
     * @param filetype ファイルタイプ
     * @param file アップロードファイル
     * @param xmlData　XML情報
     * @return excelData　エクセル情報
     * @throws Exception
     */
	@SuppressWarnings("resource")
	public Map<String,List<String>> getExcelData(String filetype,MultipartFile file,Map<String,List<String>> xmlData) throws Exception {	
		//excelファイル名取得
		String fileName = file.getOriginalFilename();  
		//excelファイル拡張子取得
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);  
		//excelData
		Map<String,List<String>> excelData = new LinkedHashMap<String,List<String>>();
		
		for(Map.Entry<String, List<String>> tmpData : xmlData.entrySet()) {
			
			String tmpKey = tmpData.getKey();
			List<String> tmpValue = tmpData.getValue();
		
			//excelファイル読み込み
			int row = 0;
			int column = 0;
			String type = "";
			List<String> UploadData = new ArrayList<String>();
			Map<String,String> tmpUploadData = new LinkedHashMap<String,String>();
			if("xlsx".equals(fileSuffix)) {
				logger.info("excel2007");
				XSSFWorkbook hssfWorkbook = new XSSFWorkbook(file.getInputStream());
				for (int i = 0; i < tmpValue.size(); i++) {
					//cell情報取得
					String valueLiString = tmpValue.get(i);
					List<String> list = new ArrayList<String>();
					StringTokenizer stringTokenizer = new StringTokenizer(
							valueLiString, ",");
					while (stringTokenizer.hasMoreTokens()) {
						list.add(stringTokenizer.nextToken());
					}
					//sheetのindex情報取得
					int sheetIndex = Integer.parseInt(list.get(0));
					XSSFSheet sheet = hssfWorkbook.getSheetAt(sheetIndex);	
					//row、column、type情報取得
					type = list.get(list.size() - 3);
					row = Integer.parseInt(list.get(list.size() - 2));
					column = Integer.parseInt(list.get(list.size() - 1));
	
					//cell内容取得
					XSSFRow rowValue = sheet.getRow(row);
					XSSFCell cellValue = rowValue.getCell((short) column);
					String cellContent = getStringCellValuexlxs(type,cellValue);
					
					if(UploadCommonConst.タイプ_案件振り返り.equals(filetype)) {
						//必須入力チェック
						UploadPkCheck(tmpKey,list.get(1),cellContent,sheetIndex,row);
						UploadData.add(list.get(1)+","+cellContent);
					}else {
						tmpUploadData.put(list.get(1),cellContent);
						if((i+1)%Integer.parseInt(list.get(2)) == 0) {
							if(UploadCommonConst.空セル.equals((tmpUploadData.get(UploadCommonConst.課名))) && 
									UploadCommonConst.空セル.equals((tmpUploadData.get(UploadCommonConst.ユニット名))) && 
									UploadCommonConst.空セル.equals((tmpUploadData.get(UploadCommonConst.案件名)))){
								break;
							}
							for(Map.Entry<String, String> m : tmpUploadData.entrySet()) {
								//必須入力チェック
								UploadPkCheck(tmpKey,m.getKey(),m.getValue(),sheetIndex,row);
								UploadData.add(m.getKey()+","+m.getValue()+","+list.get(2));
							}
							tmpUploadData = new LinkedHashMap<String,String>();
						}
					}
				}
				
			}else if("xls".equals(fileSuffix)) {			
				logger.info("excel2003");
				HSSFWorkbook hssfWorkbook = new HSSFWorkbook(file.getInputStream());
				for (int i = 0; i < tmpValue.size(); i++) {
					//cell情報取得
					String valueLiString = tmpValue.get(i);
					List<String> list = new ArrayList<String>();
					StringTokenizer stringTokenizer = new StringTokenizer(
							valueLiString, ",");
					while (stringTokenizer.hasMoreTokens()) {
						list.add(stringTokenizer.nextToken());
					}
					//sheetのindex情報取得
					int sheetIndex = Integer.parseInt(list.get(0));
					HSSFSheet sheet = hssfWorkbook.getSheetAt(sheetIndex);	
					//row、column、type情報取得
					type = list.get(list.size() - 3);
					row = Integer.parseInt(list.get(list.size() - 2));
					column = Integer.parseInt(list.get(list.size() - 1));
	
					//cell内容取得
					HSSFRow rowValue = sheet.getRow(row);
					HSSFCell cellValue = rowValue.getCell((short) column);
					String cellContent = getStringCellValue(type,cellValue);
					
					if(UploadCommonConst.タイプ_案件振り返り.equals(filetype)) {
						//必須入力チェック
						UploadPkCheck(tmpKey,list.get(1),cellContent,sheetIndex,row);
						UploadData.add(list.get(1)+","+cellContent);
					}else {
						tmpUploadData.put(list.get(1),cellContent);
						if((i+1)%Integer.parseInt(list.get(2)) == 0) {
							if(UploadCommonConst.空セル.equals((tmpUploadData.get(UploadCommonConst.課名))) && 
									UploadCommonConst.空セル.equals((tmpUploadData.get(UploadCommonConst.ユニット名))) && 
									UploadCommonConst.空セル.equals((tmpUploadData.get(UploadCommonConst.案件名)))){
								break;
							}
							for(Map.Entry<String, String> m : tmpUploadData.entrySet()) {
								//必須入力チェック
								UploadPkCheck(tmpKey,m.getKey(),m.getValue(),sheetIndex,row);
								UploadData.add(m.getKey()+","+m.getValue()+","+list.get(2));
							}
							tmpUploadData = new LinkedHashMap<String,String>();
						}
					}
				}				
			}
			if(UploadData.size() > 0){
				excelData.put(tmpKey, UploadData);
			}
		}
		return excelData;
	}
	
	
	/** 
	 *必須入力チェック
	 * 
	 * @param tmpKey テーブル名
	 * @param cloum 項目名     
	 * @param value 項目値 
	 * @param sheetIndex シート 
	 * @param row 行目          
	 */
	private void UploadPkCheck(String tmpKey,String checkCloum,String value,int sheetIndex,int row) {	
		
		if((UploadCommonConst.課名.equals(checkCloum) || UploadCommonConst.ユニット名.equals(checkCloum) || UploadCommonConst.案件名.equals(checkCloum)) && UploadCommonConst.空セル.equals(value)) {
			throw new MyException(checkCloum+"が未入力です。" + "Sheet:" + (sheetIndex + 1) + "," + "row:" + (row + 1));	
		}
		
		if(UploadCommonConst.案件振り返り_指摘.equals(tmpKey) || UploadCommonConst.案件振り返り_欠陥.equals(tmpKey) || UploadCommonConst.案件振り返り_生産性実績.equals(tmpKey)){
			if(UploadCommonConst.会社区分.equals(checkCloum) && UploadCommonConst.空セル.equals(value)) {
				throw new MyException(checkCloum+"が未入力です。" + "Sheet:" + (sheetIndex + 1) + "," + "row:" + (row + 1));	
			}	
		}		
	}
	
	/** 
	 * cell情報取得　2003
	 * 
	 * @param cell
	 * @param type            
	 * @return String cell情報
	 */
	private String getStringCellValue(String type,HSSFCell cell) {
		String strCell = "";
		if(null == cell) {
			return UploadCommonConst.空セル;
		}
		switch(type) {
		case UploadCommonConst.文字列:
			if("".equals(cell.getStringCellValue())) {
				strCell = UploadCommonConst.空セル;
			}else {
				strCell = cell.getStringCellValue();
			}
			break;
		case UploadCommonConst.日付:
			try {
				strCell = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue()).toString();
             } catch (Exception e) {
             	strCell = UploadCommonConst.空セル;
             }
			break;
		case UploadCommonConst.数字:
			try {
				strCell = String.valueOf(cell.getNumericCellValue());
             } catch (Exception e) {
             	strCell = UploadCommonConst.空セル;
             }
		}
		return strCell;
	}

	/**
	 * cell情報取得　2007
	 * 
	 * @param cell
	 * @param type 
	 * @return String cell情報
	 */
	private String getStringCellValuexlxs(String type,XSSFCell cell) {
		String strCell = "";
		if(null == cell) {
			return UploadCommonConst.空セル;
		}
		switch(type) {
		case UploadCommonConst.文字列:
			if("".equals(cell.getStringCellValue())) {
				strCell = UploadCommonConst.空セル;
			}else {
				strCell = cell.getStringCellValue();
			}
			break;
		case UploadCommonConst.日付:
			try {
//				if("yyyy/mm;@".equals(cell.getCellStyle().getDataFormatString()) || "m/d/yy".equals(cell.getCellStyle().getDataFormatString())
//				        || "yy/m/d".equals(cell.getCellStyle().getDataFormatString()) || "mm/dd/yy".equals(cell.getCellStyle().getDataFormatString())
//				        || "dd-mmm-yy".equals(cell.getCellStyle().getDataFormatString())|| "yyyy/m/d".equals(cell.getCellStyle().getDataFormatString())
//				        || "yyyy/mm".equals(cell.getCellStyle().getDataFormatString()) || "m/d;@".equals(cell.getCellStyle().getDataFormatString())
//				        || "yyyy/m/d;@".equals(cell.getCellStyle().getDataFormatString())){
					strCell = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue()).toString();
//					}
             } catch (Exception e) {
             	strCell = UploadCommonConst.空セル;
             }
			break;
		case UploadCommonConst.数字:
			try {
				strCell = String.valueOf(cell.getNumericCellValue());
             } catch (Exception e) {
             	strCell = UploadCommonConst.空セル;
             }
		}
		return strCell;
	}
	
	/**
	 * データ登録
	 * 
	 * @param filetype ファイルタイプ
	 * @param excelData エクセル情報
	 * @return String cell情報
	 * @throws SQLException 
	 */	
	public void dataUpload(String filetype,Map<String,List<String>> excelData){
		Connection myconn = dbutil.getConn();
		try{
			myconn.setAutoCommit(false);
			for(Map.Entry<String, List<String>> tmpData : excelData.entrySet()) {				
				String tmpKey = tmpData.getKey();
				List<String> tmpValue = tmpData.getValue();
				//sql文生成
				Map <String,String> sqlMap = getsql(tmpKey,tmpValue);
				//テーブルを更新
				dbutil.queryUpdate(myconn, "delete from 案件振り返り_テスト");
				dbutil.queryUpdate(myconn, "insert into 案件振り返り_テスト(課名) values(2)");	
				dbutil.queryUpdate(myconn, sqlMap.get(UploadCommonConst.DELETE));
				dbutil.queryUpdate(myconn, sqlMap.get(UploadCommonConst.INSERT));	
			}
			myconn.commit();
		}catch (Exception e){
			try {
				myconn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * sql文生成
	 *  
	 * @param tableName テーブルネーム
	 * @param upLoadData アップデータ情報
	 * @return sqlMap sql情報
	 */	
	private Map<String,String> getsql(String tableName,List<String> upLoadData){

		Map <String,String> sqlMap = new LinkedHashMap<String,String>(); 
		String insCloumSql = "";
		String insValueSql = "";
		String delSql = "";
		
		for(int i = 0;i <= upLoadData.size()-1 ; i++) {
			String dataTmp[] = upLoadData.get(i).split(",");			
			if(UploadCommonConst.案件管理一覧.equals(tableName)) {				
				if (i < Integer.parseInt(dataTmp[2])) {
					insCloumSql = insCloumSql + dataTmp[0] + ",";
				}
				if(UploadCommonConst.課名.equals(dataTmp[0]) || UploadCommonConst.ユニット名.equals(dataTmp[0]) || UploadCommonConst.案件名.equals(dataTmp[0])) {
					delSql = delSql + dataTmp[0] + "=" + "'" + dataTmp[1] + "'" + " and ";
				}
				insValueSql = insValueSql + getInsVal(dataTmp[1]) + ",";			
				if((i+1)%Integer.parseInt(dataTmp[2]) == 0) {
					delSql = delSql.substring(0, delSql.length() - 5) + ") or (";
					insValueSql = insValueSql.substring(0, insValueSql.length() - 1) + "),(";

				}
	
			}else {
				insCloumSql = insCloumSql + dataTmp[0] + ",";
				if(UploadCommonConst.課名.equals(dataTmp[0]) || UploadCommonConst.ユニット名.equals(dataTmp[0]) || UploadCommonConst.案件名.equals(dataTmp[0]) || UploadCommonConst.会社区分.equals(dataTmp[0])) {
					delSql = delSql + dataTmp[0] + "=" + "'" + dataTmp[1] + "'" + " and  ";
				}
					insValueSql = insValueSql + getInsVal(dataTmp[1]) + "  ,";
										
			}
		}
		sqlMap.put(UploadCommonConst.DELETE, "delete from " + tableName + " where " + "(" + delSql.substring(0, delSql.length() - 6) + ")");
		sqlMap.put(UploadCommonConst.INSERT, "insert into " + tableName + "(" + insCloumSql.substring(0, insCloumSql.length() - 1) + ")" + " values " + "(" + insValueSql.substring(0, insValueSql.length() - 3) + ")");
				
		return sqlMap;	
	}	
	
	
	/**
	 * insertSql文作成
	 *  
	 * @param insValueSql 編集元文字列
	 * @return String 編集後文字列
	 */	
	private String getInsVal(String insValueSql) {
		if(UploadCommonConst.空セル.endsWith(insValueSql)) {
			return UploadCommonConst.NULL;
		}else {
			if(isNumber(insValueSql)) {
				return insValueSql;
			}else {
				return "'" + insValueSql + "'";
			}
		}
	}
	
	/**
	 * 数字型判断
	 *  
	 * @param str 判断文字列
	 * @return boolean 数字：true 数字以外:false
	 */	
	private static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);

    }
}
