package org.open4goods.ui.controllers.ui;

import javax.servlet.http.HttpServletRequest;

import org.open4goods.ui.config.yml.UiConfig;
import org.open4goods.ui.controllers.dto.VerticalSearchResponse;
import org.open4goods.ui.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
//	TODO(i18n, P2, 0.25) : I18n the pathes
public class VerticalSearchController extends AbstractUiController {

	
	@Autowired private SearchService searchService;
	@Autowired UiConfig config;


//	@GetMapping({"/{vertical}/{query}"})
//	public ModelAndView searchGet(final HttpServletRequest request, @PathVariable String query) {
//
//		//TODO(gof) : have easy templates for specialized verticals
//		ModelAndView model = defaultModelAndView("vertical-home", request);
//
//		VerticalSearchResponse results = searchService.verticalSearch(query.toUpperCase());		
////		results.compute();
//		
//		model.addObject("results",results);
//		model.addObject("category",query.replace(">", " - "));
//		model.addObject("page",query.replace(">", " - "));
//		
//		model.addObject("query",query.toUpperCase());
//		
//		
//		return model;
//	}	
}