package org.pstcl.ea.controller;

import org.pstcl.ea.model.mapping.LocationMeterMappingModel;
import org.pstcl.ea.service.impl.TempService;
import org.pstcl.ea.service.impl.masters.MapMeterLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TempController {
	@Autowired
	MapMeterLocationService mapMeterLocationService;
	
	@Autowired
	TempService tempService;
	
	@RequestMapping(value = { "/removeMaps" }, method = { RequestMethod.GET })
	public String removeMaps(final ModelMap model) {
		LocationMeterMappingModel resultModel=mapMeterLocationService.removeMaps();
		model.addAttribute("locationSurveyDataModel", resultModel);
		return "mapping/success";
	}

	@RequestMapping(value = { "/addMaps" }, method = { RequestMethod.GET })
	public String addMaps(final ModelMap model) {
		LocationMeterMappingModel resultModel=mapMeterLocationService.addMaps();
		model.addAttribute("locationSurveyDataModel", resultModel);
		return "mapping/success";
	}

	@RequestMapping(value = { "/calcDTForLocations" }, method = { RequestMethod.GET })
	public String calcDTForLocations(final ModelMap model) {
		tempService.calcDTForLocations();
		return "home";
	}
}
