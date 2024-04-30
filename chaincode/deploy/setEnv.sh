#!/bin/bash

function setBaseEnvironment() {
  export ROOT_PATH=/home/skywaet/studying/diploma_mag/fabric
  export NETWORK_PATH=$ROOT_PATH/test-network
  export PATH=$ROOT_PATH/bin:$PATH
  export FABRIC_CFG_PATH=$ROOT_PATH/config/
}

function setOrgEnvironment() {
  export CORE_PEER_TLS_ENABLED=true
  export CORE_PEER_LOCALMSPID=$1
  export CORE_PEER_TLS_ROOTCERT_FILE=$NETWORK_PATH/organizations/peerOrganizations/$2/peers/peer0.$2/tls/ca.crt
  export CORE_PEER_MSPCONFIGPATH=$NETWORK_PATH/organizations/peerOrganizations/$2/users/Admin@$2/msp
  export CORE_PEER_ADDRESS=$3
}