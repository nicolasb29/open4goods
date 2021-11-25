package org.open4goods.ui.controllers.ui;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.open4goods.exceptions.TechnicalException;
import org.open4goods.ui.services.OpenDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mashape.unirest.http.exceptions.UnirestException;

@Controller
/**
 * This controller maps the opendata page and dataset
 *
 * @author gof
 *
 */
public class OpenDataController extends AbstractUiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenDataController.class);

	// The siteConfig
	private @Autowired OpenDataService openDataService;

	/**
	 * The Home page.
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws UnirestException
	 */

	@GetMapping("/opendata")
	public ModelAndView opendata(final HttpServletRequest request) {
		final ModelAndView ret = defaultModelAndView("opendata", request);
		ret.addObject("count", openDataService.totalItems());
		ret.addObject("lastUpdated", openDataService.lastUpdate());
		ret.addObject("fileSize", openDataService.fileSize());
		ret.addObject("page","open data");
		
		return ret;
	}

	@GetMapping(path = "/opendata/gtin-open-data.zip")
	public void opensearch(final HttpServletResponse response) throws IOException {
		try {
			response.setHeader("Content-type", "application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"gtin-open-data.zip\"");
			IOUtils.copy(openDataService.limitedRateStream(), response.getOutputStream());
		} catch (IOException e) {			
			LOGGER.error("opendata file download error or interruption : {}",e.getMessage());
			openDataService.decrementDownloadCounter();
		} catch (TechnicalException e) {
			response.sendError(429, "Exceding the " + OpenDataService.CONCURRENT_DOWNLOADS + " concurrent downloads availlable");
			LOGGER.error("opendata file download error : {}",e.getMessage());
		}
	}

}
