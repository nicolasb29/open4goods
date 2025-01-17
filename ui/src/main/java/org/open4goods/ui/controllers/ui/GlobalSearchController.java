package org.open4goods.ui.controllers.ui;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.open4goods.model.constants.CacheConstants;
import org.open4goods.model.dto.VerticalSearchResponse;
import org.open4goods.services.SearchService;
import org.open4goods.ui.config.yml.UiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
//	TODO(i18n, P2, 0.25) : I18n the pathes
public class GlobalSearchController extends AbstractUiController {


	private final SearchService searchService;
	private final UiConfig config;

	public GlobalSearchController(SearchService searchService, UiConfig config) {
		this.searchService = searchService;
		this.config = config;
	}


	@PostMapping({"/recherche/{query}"})
	public ModelAndView searchPost(final HttpServletRequest request, @PathVariable String query, HttpServletResponse response) {

		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);

		String[] frags = request.getServletPath().substring(1).split("/");
		response.setHeader("Location", config.getBaseUrl(Locale.FRANCE) +  frags[0]+"/"+ URLEncoder.encode(frags[1],Charset.defaultCharset()) );
		return null;
	}

	@GetMapping({"/recherche"})
	@Cacheable(cacheNames = CacheConstants.ONE_HOUR_LOCAL_CACHE_NAME)
	public ModelAndView searchGet(final HttpServletRequest request, @RequestParam String q) {

			ModelAndView model = defaultModelAndView("search", request);

		//		Set<String> categroies = Sets.newHashSet("JOUR>AUTRES MEUBLES");


		VerticalSearchResponse results = searchService.globalSearch(q, null, null, null, null,0,25,0, true);

		model.addObject("results",results);
		model.addObject("query", q);

		return model;
	}


	@RequestMapping({"/recherche","/recherche/"})
	public ModelAndView search(final HttpServletRequest request) {
		return searchGet(request,"");
	}


}
