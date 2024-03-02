package com.samcomo.dbz.global.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.samcomo.dbz.global.s3.exception.S3Exception;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.report.exception.ReportException;
import com.samcomo.dbz.report.model.entity.ReportImage;
import java.io.IOException;
import java.util.ArrayList;
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

  public ImageUploadState upload(MultipartFile multipartFile, ImageType imageType){

    String filename = multipartFile.getOriginalFilename();

    String newFilename = createFileId(filename, imageType);

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

  public List<ReportImage> uploadAll(List<MultipartFile> multipartFileList){
    List<ReportImage> imageList = new ArrayList<>();

    for (MultipartFile image : multipartFileList) {
      ImageUploadState imageUploadState = upload(image, ImageType.REPORT);

      // 이미지 업로드 실패
      if(!imageUploadState.isSuccess()){
        //지금까지 저장된 이미지 삭제
        deleteUploadedImages(imageList);

        throw new ReportException(ErrorCode.IMAGE_UPLOAD_FAIL);
      }

      String imageUrl = imageUploadState.getImageUrl();
      imageList.add(ReportImage.builder()
          .imageUrl(imageUrl)
          .build());
    }

    return imageList;
  }

  public void delete(String fileName){
    try {
      amazonS3.deleteObject(bucket, fileName);
      log.info(" S3 객체 삭제 : {}", fileName);
    } catch (SdkClientException e) {
      throw new S3Exception(ErrorCode.AWS_SDK_ERROR);
    }
  }

  public String createFileId(String fileName, ImageType imageType){
    String random = UUID.randomUUID().toString();
    return imageType.getName() + "-" + random + fileName;
  }

  private String getContentType(String filename) {
    int idx = filename.lastIndexOf(".");
    String extension = filename.substring(idx + 1);
    return "image/" + extension;
  }

  private void deleteUploadedImages(List<ReportImage> imageList){
    for (ReportImage reportImage : imageList){
      String imageUrl = reportImage.getImageUrl();
      int idx = imageUrl.lastIndexOf("/");
      String fileName = imageUrl.substring(idx + 1);
      delete(fileName);
    }
  }
}
