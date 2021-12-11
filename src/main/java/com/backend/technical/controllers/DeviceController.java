package com.backend.technical.controllers;

import com.backend.technical.dtos.DeviceRequest;
import com.backend.technical.dtos.DeviceResponse;
import com.backend.technical.services.DeviceService;
import com.backend.technical.utils.CommonUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.backend.technical.utils.CommonUtils.COMMON_DATE_TIME_PATTERN;
import static com.backend.technical.utils.CommonUtils.REQUEST_DATE_TIME_PATTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> postDevice(@RequestBody DeviceRequest request,
                                                     @RequestHeader(value = "follow", defaultValue = "false") boolean follow) {
        final Optional<DeviceResponse> response = deviceService.save(request);
        return response.map(deviceResponse -> resolveDeviceResponse(deviceResponse, follow)).orElseThrow();
    }

    @GetMapping(value = "/{deviceId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable("deviceId") String deviceId,
                                                        @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = REQUEST_DATE_TIME_PATTERN) Date fromDate,
                                                        @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = REQUEST_DATE_TIME_PATTERN) Date toDate) {
        final Map<String, String> requestHeaders = buildDeviceGetRequestHeaders(fromDate, toDate);
        final Optional<DeviceResponse> response = deviceService.findByDeviceId(deviceId, requestHeaders);
        return response.map(deviceResponse -> new ResponseEntity<>(deviceResponse, HttpStatus.OK)).orElseThrow();
    }

    private Map<String, String> buildDeviceGetRequestHeaders(final Date fromDate, final Date toDate) {
        final Map<String, String> requestHeaders = new HashMap<>();
        if (fromDate != null) {
            requestHeaders.put("from", CommonUtils.dateToString(fromDate, COMMON_DATE_TIME_PATTERN));
        }
        if (fromDate != null) {
            requestHeaders.put("to", CommonUtils.dateToString(toDate, COMMON_DATE_TIME_PATTERN));
        }
        return requestHeaders;
    }

    private ResponseEntity<DeviceResponse> resolveDeviceResponse(final DeviceResponse response, final boolean follow) {
        if (follow) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
