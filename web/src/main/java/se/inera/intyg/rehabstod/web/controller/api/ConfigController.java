package se.inera.intyg.rehabstod.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.inera.intyg.rehabstod.web.controller.api.dto.GetConfigResponse;

/**
 * Created by pebe on 2015-08-28.
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private Environment env;

    @RequestMapping(value = "")
    public GetConfigResponse getConfig()
    {
        return new GetConfigResponse(
            env.getProperty("webcert.host.url"),
            env.getProperty("webcert.start.url"));
    }
}
