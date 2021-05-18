// Retrieves data from world database
package cst438hw2.service;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import cst438hw2.domain.*;

// Returns information about the city and country, but also uses weatherService to get
// information about that city's weather. Returned as a cityInfo object to CityRestController
@Service
public class CityService {
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private WeatherService weatherService;
	// Needed for RabbitMQ functionality
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private FanoutExchange fanout;
	
	public CityService(CityRepository cityRepository, CountryRepository countryRepository,
			WeatherService weatherService) {
		this.cityRepository = cityRepository;
		this.countryRepository = countryRepository;
		this.weatherService = weatherService;
		
	}
	
	public CityInfo getCityInfo(String cityName) {
		List<City> cities = cityRepository.findByName(cityName);
		// List is empty, no cities found
		if(cities.size()==0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The city " + cityName + " was not found.");
		} else {
			// Otherwise, pick first city from list of results
			City city = cities.get(0);
			Country country = countryRepository.findByCode(city.getCountryCode());
			TempAndTime tempTime = weatherService.getTempAndTime(cityName);
			return new CityInfo(city, country, tempTime);			
		}
	}
	
	// Enqueue a message to RabbitMQ exchange
	public void requestReservation(
			String cityName,
			String level,
			String email) {
		String msg = "{\"cityName\": \"" + cityName + 
				"\" \"level\": \""+level+
				"\" \"email\": \""+email+"\"}" ;
		System.out.println("Sending message:" + msg);
		rabbitTemplate.convertSendAndReceive(
				fanout.getName(),
				"",  // routing key none.
				msg);
		
	}
}
