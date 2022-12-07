package com.sparta.doblock.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.doblock.exception.DoBlockExceptions;
import com.sparta.doblock.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 s3Client;
    private static final int TARGET_HEIGHT = 200;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredential = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredential))
                .build();
    }

    public String uploadFeedImage(MultipartFile multipartFile) {

        String fileFormat = Objects.requireNonNull(multipartFile.getContentType()).substring(multipartFile.getContentType().lastIndexOf("/") + 1).toLowerCase();

        validateImageFormat(fileFormat);

        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        String imageUrl;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket + "/post/image", fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = (s3Client.getUrl(bucket + "/post/image", fileName).toString());

        } catch (IOException e) {
            throw new DoBlockExceptions(ErrorCodes.UPLOAD_IMAGE_FAILED);
        }

        return imageUrl;
    }

    public String uploadProfileImage(MultipartFile multipartFile) throws IOException {

        String fileFormat = Objects.requireNonNull(multipartFile.getContentType()).substring(multipartFile.getContentType().lastIndexOf("/") + 1).toLowerCase();

        validateImageFormat(fileFormat);

        String fileName = createFileName(multipartFile.getOriginalFilename());

        BufferedImage croppedImage = resizeImage(multipartFile);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        ByteArrayInputStream byteArrayInputStream = convertImage(croppedImage, multipartFile.getContentType(), fileFormat, objectMetadata);

        s3Client.putObject(new PutObjectRequest(bucket + "/post/image", fileName, byteArrayInputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucket + "/post/image", fileName).toString();
    }

    public void delete(String key) {

        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, key);
            this.s3Client.deleteObject(deleteObjectRequest);

        } catch (SdkClientException e) {
            throw new DoBlockExceptions(ErrorCodes.UPLOAD_IMAGE_FAILED);
        }
    }

    public String createFileName(String filename) {
        return UUID.randomUUID().toString().concat(getFileExtension(filename));
    }

    public String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    public void validateImageFormat(String fileFormat) {

        String[] fileValidate = {"jpg", "jpeg", "png"};

        if (!Arrays.asList(fileValidate).contains(fileFormat)) {
            throw new DoBlockExceptions(ErrorCodes.NOT_VALID_IMAGE);
        }
    }

    public BufferedImage resizeImage(MultipartFile multipartFile) throws IOException {

        BufferedImage sourceImage = ImageIO.read(multipartFile.getInputStream());

        if (sourceImage.getHeight() <= TARGET_HEIGHT) {
            return sourceImage;
        }

        double sourceImageRatio = (double) sourceImage.getWidth() / sourceImage.getHeight();

        int newWidth = (int) (TARGET_HEIGHT * sourceImageRatio);

        return Scalr.resize(sourceImage, newWidth, TARGET_HEIGHT);
    }

    public ByteArrayInputStream convertImage(BufferedImage croppedImage, String contentType, String fileFormat, ObjectMetadata objectMetadata) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(croppedImage, fileFormat, byteArrayOutputStream);

        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(byteArrayOutputStream.size());

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
