package com.skywaet.securefiletransfer.chaincode;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.skywaet.securefiletransfer.common.minio.MinioProperties;
import com.skywaet.securefiletransfer.common.model.*;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

@Contract(
        name = "fileTransfer",
        info = @Info(
                title = "Secure file transfer",
                description = "Chaincode for secure file transfer",
                version = "0.0.1-SNAPSHOT"))
@Default
public class FileTransferContract implements ContractInterface {
    private static final Logger log = LoggerFactory.getLogger(FileTransferContract.class);
    private final MinioClient minioClient;
    private final ObjectMapper mapper;

    public FileTransferContract() {
        Security.addProvider(new BouncyCastleProvider());

        var properties = MinioProperties.load();
        this.minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint(), properties.getPort(), properties.useSsl())
                .credentials(properties.getAccessKeyId(), properties.getSecretAccessKeyId())
                .build();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .registerModule(new Jdk8Module());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String sendFile(Context ctx, String rawRequest) {
        try {
            log.info("Processing request {}", rawRequest);
            var request = mapper.readValue(rawRequest, SendFileRequest.class);

            var clientId = ctx.getClientIdentity().getMSPID();
            log.debug(String.format("Client id is %s", clientId));

            var fileHash = computeHash(clientId.toLowerCase(), request.getFileId(), request.getHashingAlgorithm());

            var fileMeta = FileMetadata.builder()
                    .withFileId(request.getFileId())
                    .withFileName(request.getFileName())
                    .withDescription(request.getDescription())
                    .withSender(clientId)
                    .withHashingProperties(HashingProperties.builder()
                            .withAlgorithm(request.getHashingAlgorithm())
                            .withHash(fileHash)
                            .build())
                    .withStorageType(request.getStorageTypeRaw())
                    .withStatus(FileStatus.PENDING.getCode())
                    .build();
            var metaAsString = mapper.writeValueAsBytes(fileMeta);
            ctx.getStub().putState(request.getFileId(), metaAsString);
            log.info("File with id={} proceeded successfully", request.getFileId());

            ctx.getStub().setEvent("SEND_FILE", metaAsString);

            return mapper.writeValueAsString(SendFileResponse.builder()
                    .withFileId(request.getFileId())
                    .build());
        } catch (JsonProcessingException e) {
            throw new ChaincodeException(e);
        }
    }

    private byte[] computeHash(@Nonnull String bucketName,
                               @Nonnull String fileId,
                               @Nonnull String algorithm) {
        try (var response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileId)
                .build())) {

            var messageDigest = MessageDigest.getInstance(algorithm);
            var buffer = new byte[1024];

            while (response.read(buffer) > 0) {
                messageDigest.update(buffer);
            }
            return messageDigest.digest();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.error("Error while computing file hash: {}", e.getMessage());
            throw new ChaincodeException(e);
        }
    }


}
