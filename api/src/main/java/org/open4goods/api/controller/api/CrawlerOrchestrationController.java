package org.open4goods.api.controller.api;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.NotBlank;

import org.open4goods.api.services.FetcherOrchestrationService;
import org.open4goods.config.yml.datasource.DataSourceProperties;
import org.open4goods.exceptions.InvalidParameterException;
import org.open4goods.model.constants.RolesConstants;
import org.open4goods.model.constants.UrlConstants;
import org.open4goods.model.crawlers.FetcherGlobalStats;
import org.open4goods.model.data.DataFragment;
import org.open4goods.model.dto.FetchRequestResponse;
import org.open4goods.services.DataSourceConfigService;
import org.open4goods.services.SerialisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * This controller allows informations and communications from fetchers
 * @author goulven
 *
 */
@RestController

public class CrawlerOrchestrationController {


	private @Autowired SerialisationService serialisationService;

	private @Autowired FetcherOrchestrationService fetcherOrchestrationService;

	private @Autowired DataSourceConfigService datasourceConfigService;

	@PutMapping(path=UrlConstants.MASTER_API_CRAWLER_UPDATE_PREFIX+"{crawlerNodeName}",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation("Update the presence and status of a Fetcher")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_CRAWLER+"')")
	@ApiIgnore
	public void updateFetcherStatus( @PathVariable @NotBlank final String crawlerNodeName, @RequestBody @NotBlank final FetcherGlobalStats globalStats) {
		fetcherOrchestrationService.updateClientStatus(globalStats);
	}

	@PostMapping(path=UrlConstants.MASTER_API_CRAWLER_UPDATE_PREFIX+"{crawlerNodeName}"+"/"+"{datasourceName}"  + UrlConstants.MASTER_API_CRAWLER_TRIGGER_SUFFIX)
	@ApiOperation("Run a datasource retrieving against a specific node")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	@ApiIgnore
	public FetchRequestResponse triggerFetcher( @PathVariable @NotBlank final String crawlerNodeName, @PathVariable @NotBlank final String datasourceName) {
		return fetcherOrchestrationService.triggerRemoteCrawling(crawlerNodeName, datasourceName);
	}

	@GetMapping(path=UrlConstants.MASTER_API_CRAWLERS)
	@ApiOperation("List all availlable fetchers and their stats")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public Map<String,FetcherGlobalStats> fetcherStats () {
		return fetcherOrchestrationService.getCrawlerStatuses().asMap();
	}

	@PostMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.MASTER_API_CRAWLER_TRIGGER_SUFFIX+"/all")
	@ApiOperation("Run a all datasources retrieving against best availlables node")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public FetchRequestResponse triggerAllFetcher() {
		for (final Entry<String, DataSourceProperties> ds : datasourceConfigService.getDatasourceConfigs().entrySet()) {
			fetcherOrchestrationService.triggerRemoteCrawling(ds.getKey());
		}
		return new FetchRequestResponse(true);
	}

	@PostMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.MASTER_API_CRAWLER_TRIGGER_SUFFIX+"/csv/all")
	@ApiOperation("Run a all CSV datasources retrieving against best availlables node")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public FetchRequestResponse triggerAllCsvFetcher() {
		for (final Entry<String, DataSourceProperties> ds : datasourceConfigService.getDatasourceConfigs().entrySet()) {
			if (null != ds.getValue().getCsvDatasource()) {
				fetcherOrchestrationService.triggerRemoteCrawling(ds.getKey());
			}
		}
		return new FetchRequestResponse(true);
	}


	@PostMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.MASTER_API_CRAWLER_TRIGGER_SUFFIX+"/{datasourceName}")
	@ApiOperation("Run a datasource retrieving against the best availlable node")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public FetchRequestResponse triggerFetcher( @PathVariable @NotBlank final String datasourceName) {
		return fetcherOrchestrationService.triggerRemoteCrawling(datasourceName);
	}


	@PostMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.MASTER_API_CRAWLER_SYNCH_HTTP_FETCH)
	@ApiOperation("Run an url direct fetching against the best availlable node")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public DataFragment testFetchUrl(  @RequestBody @NotBlank final String url) throws InvalidParameterException{
		// Get the providerName corresponding to the url
		final DataSourceProperties dsp = datasourceConfigService.getDatasourcePropertiesForUrl(url);
		if (null == dsp) {
			throw new InvalidParameterException("Cannot find a matching DatasourceProperties for " + url);
		}
		return fetcherOrchestrationService.triggerHttpSynchFetching(dsp, url);
	}

//
//	@GetMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.MASTER_API_CRAWLER_SYNCH_CSV_FETCH)
//	@ApiOperation("Run a csv line direct fetching against the best availlable node")
//	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
//	public DataFragment testFetchCsv(  @RequestParam @NotBlank final String csvLine, @RequestParam @NotBlank final String csvHeaders, @RequestParam @NotBlank final String datasourceName) throws InvalidParameterException{
//		// Get the providerName corresponding to the url
//		final DataSourceProperties dsp = this.datasourceConfigService.getDatasourceConfig(datasourceName);
//		if (null == dsp) {
//			throw new InvalidParameterException("Cannot find a matching DatasourceProperties for " + datasourceName);
//		}
//		return this.fetcherOrchestrationService.triggerCsvSynchFetching(dsp, csvLine, csvHeaders);
//	}


	@PostMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.MASTER_API_CRAWLER_SYNCH_FETCH_WITH_CONFIG)
	@ApiOperation("Run an url direct fetching against the best availlable node, with a given DataSourceProperties")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public DataFragment fetchUrlWithConfig(  @RequestParam @NotBlank final String url, @RequestBody @NotBlank final String datasourceProperty ) throws InvalidParameterException, JsonParseException, JsonMappingException, IOException{
		// Get the providerName corresponding to the url
		return fetcherOrchestrationService.triggerHttpSynchFetching( serialisationService.fromYaml(datasourceProperty, DataSourceProperties.class), url);
	}

	@GetMapping(path=UrlConstants.MASTER_API_CRAWLERS+"/{crawlerNodeName}" + UrlConstants.MASTER_API_CRAWLER_TRIGGER_SUFFIX)
	@ApiOperation("Get stats for a specific fetcher")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public FetcherGlobalStats fetcherStats ( @PathVariable @NotBlank final String crawlerNodeName) {
		return fetcherOrchestrationService.getCrawlerStatuses().asMap().get(crawlerNodeName);
	}


	@GetMapping(path=UrlConstants.MASTER_API_CRAWLERS  + UrlConstants.CRAWLER_API_STOP_FETCHING)
	@ApiOperation("Stop a fetching job, will request any fetchers")
	@PreAuthorize("hasAuthority('"+RolesConstants.ROLE_ADMIN+"')")
	public void stopFetching(  @RequestParam @NotBlank final String provider) throws InvalidParameterException{
		// Get the providerName corresponding to the url
		final DataSourceProperties dsp = datasourceConfigService.getDatasourceConfig(provider);
		if (null == dsp) {
			throw new InvalidParameterException("Cannot find a matching DatasourceProperties for " + provider);
		}

		fetcherOrchestrationService.stop(dsp, dsp);
	}


}
