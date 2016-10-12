package com.cspire.prov.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cspire.prov.framework.blackout.Blackout;
import com.cspire.prov.framework.blackout.BlackoutService;
import com.cspire.prov.framework.exceptions.InvalidRequest;
import com.cspire.prov.framework.heartbeat.HeartBeat;
import com.cspire.prov.framework.housekeeping.HouseKeepingService;
import com.cspire.prov.framework.loglevel.LogLevelChange;
import com.cspire.prov.framework.loglevel.LogLevelChangeService;
import com.cspire.prov.framework.model.ProvMngrResponse;
import com.cspire.prov.framework.utils.UtilFuncs;

@CrossOrigin
@RestController
@RequestMapping("/rest/mobi")
public class MiscController {

    private static final Logger log = LoggerFactory.getLogger(MiscController.class);


    @Autowired
    HouseKeepingService houseKeepingService;
    @Autowired
    UtilFuncs utils;
    @Autowired
    BlackoutService blackoutService;
    @Autowired
    Blackout blackout;
    @Autowired
    LogLevelChangeService llChangeService;


    @CrossOrigin
    @RequestMapping(value = "/houseKeepingConfig", method = RequestMethod.GET)
    @ResponseBody
    public ProvMngrResponse houseKeepingConfig() {
        MDC.put("so", "HOUSEKEEPING_CONFIG");
        try {
            houseKeepingService.dbPopulateErrorCodes();
            houseKeepingService.dbPopulateStatusCodes();
        } catch (Exception e) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.INVALID_REQUEST, e.getMessage(),
                    utils.exceptionStackTrace(e), ProvMngrResponse.FAIL_MSG, ProvMngrResponse.HOUSE_KEEPING_DB_CONFIG,
                    false);
        }
        return new ProvMngrResponse(ProvMngrResponse.SUCCESS, utils.getCurrentEpoch(),
                ProvMngrResponse.HOUSE_KEEPING_DB_CONFIG, false);
    }

    @CrossOrigin
    @RequestMapping(value = "/blackoutConfig", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse blackoutConfig(@RequestBody Blackout inputPayload) {
        MDC.put("so", "APMAX_BLACKOUT");
        try {
            log.trace("Processing Blackout Config Request:{}", inputPayload);
            log.info("Current System blackout:{}", blackout);
            if(inputPayload.getStartTime()!=null && inputPayload.getEndTime()!=null){
                if (inputPayload.getStartTime().longValue() >= inputPayload.getEndTime().longValue()) {
                    return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.INVALID_REQUEST,
                            "Blackout Start time cant be >= End Time", null, ProvMngrResponse.BLACKOUT_CONFIG_FAILED,
                            ProvMngrResponse.APMAX, true);
                }
            }
            if(inputPayload.getStartTime()==null && inputPayload.getEndTime()!=null){
                return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.INVALID_REQUEST,
                        "Blackout End Time always needs to be with Start time", null, ProvMngrResponse.BLACKOUT_CONFIG_FAILED,
                        ProvMngrResponse.APMAX, true);
            }
            
            blackout.copy(inputPayload);
            log.info("System blackout updated to:{}", blackout);
        } catch (Exception e) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.INVALID_REQUEST, e.getMessage(),
                    utils.exceptionStackTrace(e), ProvMngrResponse.BLACKOUT_CONFIG_FAILED, ProvMngrResponse.APMAX,
                    true);
        }
        return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.SUCCESS, null, null,
                ProvMngrResponse.BLACKOUT_CONFIG_SUCCESS, ProvMngrResponse.APMAX, true);
    }

    @CrossOrigin
    @RequestMapping(value = "/blackoutConfig", method = RequestMethod.GET)
    @ResponseBody
    public Blackout blackoutConfig() {
        MDC.put("so", "APMAX_BLACKOUT");
        blackout.setIsBlackoutEffective(blackoutService.checkForBalckout());
        return blackout;
    }

    @CrossOrigin
    @RequestMapping(value = "/dynamicLogLevelChange", method = RequestMethod.POST)
    @ResponseBody
    public ProvMngrResponse changeLogLevel(@RequestBody LogLevelChange llChangeReq) {
        MDC.put("so", "APMAX_DYNAMICLOGLEVEL");
        try {
            log.trace("Dynamic Log Level Change Initiated {}", llChangeReq);
            llChangeService.processLogLevelChangeRequest(llChangeReq);
            log.info("Successfully processed Dynameic log level change request {}", llChangeReq.toString());
        } catch (Exception e) {
            return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.INVALID_REQUEST, e.getMessage(),
                    utils.exceptionStackTrace(e), ProvMngrResponse.DYNAMIC_LOG_LEVEL_CHANGE_FAILED,
                    ProvMngrResponse.APMAX, true);
        }
        return new ProvMngrResponse(utils.getCurrentEpoch(), ProvMngrResponse.SUCCESS, null, null,
                ProvMngrResponse.DYNAMIC_LOG_LEVEL_CHANGE_SUCCESS, ProvMngrResponse.APMAX, true);
    }

    @CrossOrigin
    @RequestMapping(value = "/getCurrentLogLevel", method = RequestMethod.POST)
    @ResponseBody
    public LogLevelChange getCurrentLogLevel(@RequestBody LogLevelChange llChangeReq) {
        MDC.put("so", "APMAX_DYNAMICLOGLEVEL");
        try {
            log.trace("Getting Current log level {}", llChangeReq);
            llChangeReq = llChangeService.getCurrentLogLevel(llChangeReq);
            log.info("Successfully processed Dynameic log level change request {}", llChangeReq.toString());
        } catch (Exception e) {
            log.error("Failed to get the current log level",e);
            throw new InvalidRequest("Failed to get the current log level",e);
        }
        return llChangeReq;
    }
    
    @CrossOrigin
    @RequestMapping(value = "/heartBeat", method = RequestMethod.GET)
    @ResponseBody
    public HeartBeat heartBeat() {        
        return new HeartBeat(true);
    }
}
