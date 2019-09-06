package org.pstcl.ea.service.impl;

import org.pstcl.ea.messaging.OutputMessage;
import org.pstcl.ea.service.impl.masters.MapMonthLossReportLocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service

public class ScheduledTasksService {



	@Autowired
	private MapMonthLossReportLocationServiceImpl mapMonthLossReportLocationService;

	@Scheduled(cron = "0 14 22 27 * *")
	public  void createLossReportLocationsForMonth()
	{
		mapMonthLossReportLocationService.createLossReportLocationsForCurrentMonth();
	}
//Runs on at 22:14:00pm 28 of every month(*) or every year(*)
	@Scheduled(cron = "0 14 22 28 * *")
	public  void checkLossReportLocationsForMonth()
	{
		mapMonthLossReportLocationService.createLossReportLocationsForCurrentMonth();
	}
	
	
	
//	 @Autowired
//	    private SimpMessagingTemplate template;
//
//	    @Scheduled(fixedRate = 5000)
//	    public void greeting() throws InterruptedException {
//	        Thread.sleep(1000); // simulated delay
//	      
//	        this.template.convertAndSend("/topic/greetings", new OutputMessage("Hello, " + HtmlUtils.htmlEscape("hellos") + "!"));
//	    }
//	

}
