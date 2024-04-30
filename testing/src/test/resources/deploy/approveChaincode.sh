#!/bin/bash

. $1

setBaseEnvironment
setOrgEnvironment "Org1MSP" "org1.example.com" "localhost:7051"
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID $4 --name fileTransfer --version "$2" --package-id "$3" --sequence "$2" --tls --cafile "$NETWORK_PATH/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"

setOrgEnvironment "Org2MSP" "org2.example.com" "localhost:9051"
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID $4 --name fileTransfer --version "$2" --package-id "$3" --sequence "$2" --tls --cafile "$NETWORK_PATH/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"

peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID $4 --name fileTransfer --version "$2" --sequence "$2" --tls --cafile "$NETWORK_PATH/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --peerAddresses localhost:7051 --tlsRootCertFiles "$NETWORK_PATH/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "$NETWORK_PATH/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"


