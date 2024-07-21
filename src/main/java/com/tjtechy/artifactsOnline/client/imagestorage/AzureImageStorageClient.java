package com.tjtechy.artifactsOnline.client.imagestorage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobStorageException;
import com.tjtechy.artifactsOnline.system.exception.CustomBlobStorageException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureImageStorageClient implements ImageStorageClient {
    private final BlobServiceClient blobServiceClient;

    public AzureImageStorageClient(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
  }

  @Override
    public String uploadImage(String containerName,
                              String originalImageName,
                              InputStream data,
                              long length) throws IOException {
      try{
        //get the BlobContainerClient object to interact with the container
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        //create a unique name for the uploaded image file with UUID
        String newImageName = UUID.randomUUID().toString() + originalImageName.substring(originalImageName.indexOf("."));
        //get the client to interact with the specified blob
        BlobClient blobClient = blobContainerClient.getBlobClient(newImageName);
        //this updates/uploads image
        blobClient.upload(data, length, true);
        return blobClient.getBlobUrl();
      } catch (BlobStorageException exception){
        throw new CustomBlobStorageException("Failed to upload image to Azure Blob Storage", exception);
      }
  }
}


/*
* The BlobClient will automatically retrieve the account name
* and key from the yml file
* and connect to the azure portal
* BECAUSE WE HAVE THEM IN THE ENVIRONMENT VARIABLE OF THE
* INTELLIJ
* */

  /* OR
  use this type to inject the BlobServiceClient
    private final BlobServiceClient _blobServiceClient;
  public AzureImageStorageClient(BlobServiceClient blobServiceClient) {
    _blobServiceClient = blobServiceClient;
  }*/