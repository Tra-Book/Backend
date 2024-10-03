package Trabook.PlanManager.service.file;

import Trabook.PlanManager.repository.plan.PlanRepository;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final PlanRepository planRepository;
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.project-id")
    private String projectId;

    private final Storage storage;
    //private final String defaultImagePath = "planPhoto-thumbnail.png";

    public void uploadPlanImage(MultipartFile image,long planId) throws IOException {
       // Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        String ext = image.getContentType();
        log.info("content-type : {}",ext);
        String fileName = "planPhoto/"+Long.toString(planId);
        System.out.println("fileName:"+fileName);
        BlobInfo blobInfo = storage.create(BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(ext)
                        .setCacheControl("no-cache")
                .build(),
                image.getInputStream());

    }
    public String uploadDefaultImage(Long planId) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        //Path path = Paths.get(defaultImagePath);
        String fileName = "planPhoto/"+Long.toString(planId);
        try(InputStream inputStream = classLoader.getResourceAsStream("planPhoto-thumbnail.png")){
            if(inputStream == null){
                throw new IOException("File not found in resources: planPhoto-thumbnail.png");

            }
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType("image/png")
                    .build();
            storage.create(blobInfo, inputStream);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }
}
