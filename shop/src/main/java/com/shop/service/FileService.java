package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{

        //UUID(Universally Unique Identifier) : 서로 다른 개체들을 구별하기 위해서 이름을 부여할 때 사용한다
        UUID uuid = UUID.randomUUID();

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // 바이트단위의 출력을 내보내는 클래스이다. -> 생성자로 파일이 저장될 위치와 파일의 이름을 넘긴다.
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);

        // fileData를 파일 출력 스트림에 입력
        fos.write(fileData);
        fos.close();
        //업로드된 파일의 이름 반환
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);

        if(deleteFile.exists()){ //해당 파일이 존재하면 삭제
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else{
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
