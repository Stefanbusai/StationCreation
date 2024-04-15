package com.airedale.StationCreation.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NetworkWrapper {
    protected String networkName;
    protected Integer deviceCount;
    protected ObjectMapper mapper = new ObjectMapper();
    protected ObjectNode jsonNetworkNode = mapper.createObjectNode();
    protected ObjectNode jsonDevicesNode = mapper.createObjectNode();
    public ObjectNode getJsonNetworkNode() {
        return jsonNetworkNode;
    }
    public String getJSONString(){

        return jsonNetworkNode.toPrettyString();
    }
}
