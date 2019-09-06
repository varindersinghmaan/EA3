package org.pstcl.ea.converters;

import org.pstcl.ea.dao.MapMeterLocationDao;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MeterLocationConverter implements Converter<String, MapMeterLocation>{

	static final Logger logger;

	@Autowired
	MapMeterLocationDao service;
	
	static {
		logger = LoggerFactory.getLogger((Class) MeterLocationConverter.class);
	}

	public MapMeterLocation convert(final String code) {
		if(code.equalsIgnoreCase(""))
		{
			return null;
		}
		DivisionConverter.logger.info("Profile : {}", (Object) code);
		return this.service.findById(Integer.parseInt(code));
	}
	

}
