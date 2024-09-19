package Trabook.PlanManager.service.file;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("{spring.cloud.gcp.storage.project-id")
    private String projectId;
/*
    public void uploadPlanImage(MultipartFile image,long planId) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        String ext = image.getContentType();
        String fileName = Long.toString(planId);

        BlobInfo blobInfo =
    }*/
}
