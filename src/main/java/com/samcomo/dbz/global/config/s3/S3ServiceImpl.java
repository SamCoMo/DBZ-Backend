package com.samcomo.dbz.global.config.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.samcomo.dbz.global.config.s3.exception.S3Exception;
import com.samcomo.dbz.global.exception.ErrorCode;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  public ImageUploadState upload(MultipartFile multipartFile){
    String filename = multipartFile.getOriginalFilename();

    int idx = filename.lastIndexOf(".");
    String extension = filename.substring(idx + 1);
    String contentType = "image/" + extension;

    String newFilename = createFileId(filename);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(contentType);


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

  public void delete(String fileName){
    try {
      amazonS3.deleteObject(bucket, fileName);
      log.info(" S3 객체 삭제 : {}", fileName);
    } catch (SdkClientException e) {
      throw new S3Exception(ErrorCode.AWS_SDK_ERROR);
    }
  }

  public String createFileId(String fileName){
    String random = UUID.randomUUID().toString();
    return random + fileName;
  }
}
