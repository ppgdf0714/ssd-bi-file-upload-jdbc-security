package jp.co.ssd.bi.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadMapper {
	
	void deleteInfo(String sql);
	void insterInfo(String sql);
	int selectInfo();
}
