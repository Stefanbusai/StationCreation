package com.airedale.StationCreation.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Map;

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

    /**
     * Check for a duplicate points list.
     * If one is found, re-use it.
     */
    protected void checkForDuplicatePointsList(Map<String, String> pointsListCsvMap, DeviceWrapper device,
                                               ObjectNode jsonSingleDeviceNode) throws IOException
    {
        String pointsListFile = device.getPointsListFile();
        String pointsListCsv = device.createPointsListCSV();

        boolean matchingFileFound = false;

        for (String pointsListFileFromMap : pointsListCsvMap.keySet()) {
            String pointsListCsvFromMap = pointsListCsvMap.get(pointsListFileFromMap);
            if (pointsListCsv.equals(pointsListCsvFromMap))
            {
                device.setPointsListFile(pointsListFileFromMap);
                jsonSingleDeviceNode.put("pointsListFile", pointsListFileFromMap);
                matchingFileFound = true;
                break;
            }
        }

        if (!matchingFileFound)
        {
            pointsListCsvMap.put(pointsListFile, pointsListCsv);
        }
    }

}
