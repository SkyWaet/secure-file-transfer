#!/bin/bash
. $1

chaincodeName=fileTransfer_1
chaincodeArchive="$chaincodeName".tar.gz

setBaseEnvironment $2
peer lifecycle chaincode package "$chaincodeArchive" --path "$ROOT_PATH/chaincode" --lang java --label "$chaincodeName"

setOrgEnvironment "Org1MSP" "org1.example.com" "localhost:7051"
peer lifecycle chaincode install "$chaincodeArchive"

setOrgEnvironment "Org2MSP" "org2.example.com" "localhost:9051"
peer lifecycle chaincode install "$chaincodeArchive"