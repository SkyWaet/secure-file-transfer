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
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
            log.info("Processing send file request {}", rawRequest);
            var request = mapper.readValue(rawRequest, SendFileRequest.class);

            var clientId = ctx.getClientIdentity().getMSPID();

            var fileHash = computeHash(clientId.toLowerCase(), request.getFileId(), request.getHashingAlgorithm());

            var fileMeta = FileMetadata.builder()
                    .withFileId(request.getFileId())
                    .withFileName(request.getFileName())
                    .withDescription(request.getDescription())
                    .withSender(clientId)
                    .withVerificationProperties(VerificationProperties.builder()
                            .withAlgorithm(request.getHashingAlgorithm())
                            .withHash(fileHash)
                            .build())
                    .withStorageType(request.getStorageTypeRaw())
                    .withStatus(FileStatus.PENDING)
                    .build();
            var metaAsString = mapper.writeValueAsBytes(fileMeta);
            ctx.getStub().putState(request.getFileId(), metaAsString);
            log.info("File with id={} send successfully", request.getFileId());

            ctx.getStub().setEvent(ChaincodeEventType.SEND_FILE.getCode(), metaAsString);

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

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getFileStatus(Context ctx, String rawRequest) {
        try {
            log.info("Processing get file status request {}", rawRequest);
            var request = mapper.readValue(rawRequest, GetFileStatusRequest.class);

            var rawState = ctx.getStub().getStringState(request.getFileId());
            if (StringUtils.isBlank(rawState)) {
                log.warn("File with id {} not found", request.getFileId());
                throw new ChaincodeException("File not found, id = " + request.getFileId());
            }

            var state = mapper.readValue(rawState, FileMetadata.class);
            var fileStatus = state.status().orElseThrow(() -> new ChaincodeException("unknown file status" + state.statusRaw()));
            log.info("Requested file status for file with id {} is {}", request.getFileId(), fileStatus.getCode());

            return mapper.writeValueAsString(GetFileStatusResponse.builder()
                    .withFileId(request.getFileId())
                    .withFileStatus(fileStatus)
                    .build());
        } catch (JsonProcessingException e) {
            throw new ChaincodeException(e);
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String updateFileStatus(Context ctx, String rawRequest) {
        try {
            log.info("Processing update file status request {}", rawRequest);
            var request = mapper.readValue(rawRequest, UpdateFileStatusRequest.class);

            var rawState = ctx.getStub().getStringState(request.getFileId());
            if (StringUtils.isBlank(rawState)) {
                log.warn("File with id {} not found", request.getFileId());
                return mapper.writeValueAsString(UpdateFileStatusResponse.builder()
                        .withSuccess(false)
                        .withErrorMessage("File not found, id = " + request.getFileId())
                        .build());
            }
            var state = mapper.readValue(rawState, FileMetadata.class);
            var modifiedState = FileMetadata.builder()
                    .fromPrototype(state)
                    .withStatus(request.getFileStatus()
                            .orElseThrow(() -> new ChaincodeException("unknown file status" + request.getFileStatusRaw())))
                    .build();
            ctx.getStub().putState(request.getFileId(), mapper.writeValueAsBytes(modifiedState));

            return mapper.writeValueAsString(UpdateFileStatusResponse.builder()
                    .withSuccess(true)
                    .build());
        } catch (JsonProcessingException e) {
            throw new ChaincodeException(e);
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteFile(Context ctx, String rawRequest) {
        try {
            log.info("Processing delete file request {}", rawRequest);
            var request = mapper.readValue(rawRequest, DeleteFileRequest.class);

            var rawState = ctx.getStub().getStringState(request.getFileId());
            if (StringUtils.isBlank(rawState)) {
                log.warn("File with id {} not found", request.getFileId());
                throw new ChaincodeException("File not found, id = " + request.getFileId());
            }
            var state = mapper.readValue(rawState, FileMetadata.class);
            var key = getCompositeKey(ctx.getStub(),
                    new String(state.verificationProperties().getHash(), StandardCharsets.UTF_8),
                    state.fileId());
            ctx.getStub().putState(key.toString(), state.fileId().getBytes(StandardCharsets.UTF_8));
            ctx.getStub().delState(state.fileId());
        } catch (JsonProcessingException e) {
            throw new ChaincodeException(e);
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String checkFileByContentHash(Context ctx, String rawRequest) {
        try {
            log.info("Processing checkFileByContentHash request {}", rawRequest);
            var request = mapper.readValue(rawRequest, CheckFileByContentHashRequest.class);

            var partialKey = getCompositeKey(ctx.getStub(), new String(request.getHash(), StandardCharsets.UTF_8));
            var partialKeyIterator = ctx.getStub().getStateByPartialCompositeKey(partialKey).spliterator();

            var foundFileIds = StreamSupport.stream(partialKeyIterator, false)
                    .map(KeyValue::getStringValue)
                    .collect(Collectors.toList());

            if (foundFileIds.isEmpty()) {
                log.info("No files with requested hash found, hash = {}", request.getHash());
                return mapper.writeValueAsString(CheckFileByContentHashResponse.builder()
                        .withStatus(CheckFileByContentHashResponse.Status.NOT_FOUND)
                        .build());
            }
            log.info("Found {} files with requested hash", foundFileIds.size());
            return mapper.writeValueAsString(CheckFileByContentHashResponse.builder()
                    .withStatus(CheckFileByContentHashResponse.Status.SUCCESS)
                    .withPossibleFileIds(foundFileIds)
                    .build());
        } catch (JsonProcessingException e) {
            throw new ChaincodeException(e);
        }
    }

    private CompositeKey getCompositeKey(ChaincodeStub stub, String... params) {
        return stub.createCompositeKey("deleted", params);
    }
}
