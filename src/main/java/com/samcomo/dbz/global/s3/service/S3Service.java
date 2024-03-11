package com.samcomo.dbz.global.s3.service;

import static com.samcomo.dbz.global.s3.constants.ImageFileType.isValidImageFileType;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.constants.ImageUploadState;
import com.samcomo.dbz.global.s3.exception.S3Exception;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.entity.ReportImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  private final int maxImageSize = 5 * 1024 * 1024; // 5MB

  public ImageUploadState uploadMultipartFileByStream(MultipartFile multipartFile, ImageCategory imageCategory){

    String filename = multipartFile.getOriginalFilename();

    String newFilename = createFileName(filename, imageCategory);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(getContentType(filename));
    metadata.setContentLength(multipartFile.getSize());

    try {
      amazonS3.putObject(
          new PutObjectRequest(bucket, newFilename, multipartFile.getInputStream(), metadata)
              .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      return ImageUploadState.builder()
          .success(false)
          .build();
    }

    return ImageUploadState.builder()
        .success(true)
        .imageUrl(amazonS3.getUrl(bucket, newFilename).toString())
        .build();
  }

  public List<ReportImage> uploadReportImageList(List<MultipartFile> multipartFileList){
    // public List<String> uploadImageList(List<MultipartFile> multipartFileList, ImageCategory imageCategory){
    List<ReportImage> imageList = new ArrayList<>();

    for (MultipartFile image : multipartFileList) {
      ImageUploadState imageUploadState = uploadMultipartFileByStream(image, ImageCategory.REPORT);

      // 이미지 업로드 실패
      if(!imageUploadState.isSuccess()){
        //지금까지 저장된 이미지 삭제
        deleteUploadedReportImageList(imageList);

        throw new ReportException(ErrorCode.IMAGE_UPLOAD_FAIL);
      }

      String imageUrl = imageUploadState.getImageUrl();
      imageList.add(ReportImage.builder()
          .imageUrl(imageUrl)
          .build());
    }

    return imageList;
  }

  public void deleteFile(String fileName){
    try {
      amazonS3.deleteObject(bucket, fileName);
      log.info(" S3 객체 삭제 : {}", fileName);
    } catch (SdkClientException e) {
      throw new S3Exception(ErrorCode.AWS_SDK_ERROR);
    }
  }

  public String createFileName(String fileName, ImageCategory imageCategory){
    String random = UUID.randomUUID().toString();
    return imageCategory.getName() + "-" + random + fileName;
  }

  private String getContentType(String filename) {
    int idx = filename.lastIndexOf(".");
    String extension = filename.substring(idx + 1);
    return "image/" + extension;
  }

  private void deleteUploadedReportImageList(List<ReportImage> imageList){
    for (ReportImage reportImage : imageList){
      String imageUrl = reportImage.getImageUrl();
      int idx = imageUrl.lastIndexOf("/");
      String fileName = imageUrl.substring(idx + 1);
      deleteFile(fileName);
    }
  }

  // Base64 이미지 업로드
  public ImageUploadState uploadBase64ByStream(String base64ImageData, ImageCategory imageCategory) {
    // base64ImageData 형식 : "data:image/png;base64,iVBORw0KGgasdfQWhjfel"

    // MIME 타입 <-> 데이터 분리
    String[] base64Components = base64ImageData.split(",");

    // Base64 형식 검증
    if(base64Components.length != 2){
      throw new S3Exception(ErrorCode.INVALID_BASE64_DATA);
    }
    String base64Data = base64Components[0];

    // 이미지 형식 검증
    String imageFileType = base64Data.substring(base64Data.indexOf('/') + 1, base64Data.indexOf(';'));
    if(!isValidImageFileType(imageFileType)){
      throw new S3Exception(ErrorCode.INVALID_IMAGE_FILE_TYPE);
    }

    // 이미지 이름 설정
    String fileName = createFileName("", imageCategory);

    // 이미지 데이터 바이트 변환
    String base64Image = base64Components[1];
    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
    InputStream inputStream = new ByteArrayInputStream(imageBytes);

    // 이미지 크기 검증
    if(imageBytes.length > maxImageSize){
      throw new S3Exception(ErrorCode.IMAGE_FILE_SIZE_EXCEEDED);
    }

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(imageBytes.length);
    metadata.setContentType("image/"+imageFileType);

    // 이미지 업로드
    try {
      amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
          .withCannedAcl(CannedAccessControlList.PublicRead));

      String imageUrl = amazonS3.getUrl(bucket, fileName).toString();
      return new ImageUploadState(true, imageUrl);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("uploadBase64 : 이미지 업로드중 요류가 발생하였습니다.");
      return new ImageUploadState(false, null);
    }
  }
}
