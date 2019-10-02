package com.indigo.event.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import com.indigo.entities.Event;

@RestController
public class EventApiController {

	@Value("${application.message}")
	private String welcomeMessage;

	@Value("${application.api}")
	private String api;

	@GetMapping("/")
	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	@GetMapping("/getAllEvents")
	public ResponseEntity<String> getAllEvents() {
		String getEventsApi = api + "peek";
		System.out.println(" API call : " + getEventsApi);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = getHttpEntity();
		ResponseEntity<String> response = restTemplate.exchange(getEventsApi, HttpMethod.GET, entity, String.class);

		return response;
	}

	@GetMapping("/getEvent/{eventId}")
	public ResponseEntity<String> getEvent(@PathVariable String eventId) {

		String getEventApi = api + "read/" + eventId;
		System.out.println(" API call : " + getEventApi);
		// Object response = restTemplate.getForObject(getListUsersApi, Object.class);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = getHttpEntity();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(getEventApi, HttpMethod.GET, entity, String.class);
		} catch (Exception e) {
			System.out.println("Record not found/Wrong request");
		}

		return response;
	}

	@PostMapping("/createEvent")
	public ResponseEntity<String> createEvent(@RequestBody String payload) throws Exception {

		UUID id = UUID.randomUUID();
		String getEventApi = api + "create/" + id;
		System.out.println(" API call : " + getEventApi);

		JSONObject personJsonObject = new JSONObject(payload);
		JSONObject data = new JSONObject(personJsonObject.get("data").toString());
		ArrayList<LocalDateTime> newEventDateTimeArray = getEventStartandEndDate(data);

		boolean isOverlap = checkOverlap(newEventDateTimeArray,id.toString());
		if (isOverlap) {
			System.out.println("Timeslot already full, please select another timeslot");
			return new ResponseEntity<String>("Timeslot already full, please select another timeslot",
					HttpStatus.NOT_ACCEPTABLE);
		}
			
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-API-KEY", "3bd4b8fc-c1e3-4d05-a74f-8f2377578a5b");
		headers.add("Content-Type", MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(payload, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(getEventApi, HttpMethod.POST, entity, String.class);

		return response;

	}

	@GetMapping("/deleteEvent/{eventId}")
	public ResponseEntity<String> deleteEvent(@PathVariable String eventId) {
		String getEventApi = api + "remove/" + eventId;
		System.out.println(" API call : " + getEventApi);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = getHttpEntity();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(getEventApi, HttpMethod.DELETE, entity, String.class);
		} catch (Exception e) {
			System.out.println("Record not found/Wrong request");
		}

		return response;
	}

	@PostMapping("/updateEvent")
	public ResponseEntity<String> updateEvent(@RequestBody String payload) {

		JSONObject personJsonObject = new JSONObject(payload);
		JSONObject data = new JSONObject(personJsonObject.get("data").toString());
		String id = data.getString("id");
		String getEventApi = api + "update/" + id;
		System.out.println(" API call : " + getEventApi);
		
		ArrayList<LocalDateTime> updatedEventDateTimeArray = getEventStartandEndDate(data);
		
		// Check if the updated event will overlap with other events
		boolean isOverlap = checkOverlap(updatedEventDateTimeArray,id);
		if (isOverlap) {
			System.out.println("Timeslot already full, please select another timeslot");
			return new ResponseEntity<String>("Timeslot already full, please select another timeslot",
					HttpStatus.NOT_ACCEPTABLE);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-API-KEY", "3bd4b8fc-c1e3-4d05-a74f-8f2377578a5b");
		headers.add("Content-Type", MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(payload, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(getEventApi, HttpMethod.PUT, entity, String.class);

		return response;
	}

	private HttpEntity<String> getHttpEntity() {

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-API-KEY", "3bd4b8fc-c1e3-4d05-a74f-8f2377578a5b");
		return new HttpEntity<>("body", headers);
	}

	private ArrayList<LocalDateTime> getEventStartandEndDate(JSONObject event) {

		String startDate = event.getString("dateTime");
		int duration = event.getInt("duration");

		LocalDateTime StartDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
		LocalDateTime EndDateTime = StartDateTime.plusMinutes(duration);

		ArrayList<LocalDateTime> dateTimeArray = new ArrayList<LocalDateTime>();
		dateTimeArray.add(StartDateTime);
		dateTimeArray.add(EndDateTime);

		return dateTimeArray;
	}
	
	// This method will check is the event is overlapping other existing events
	private boolean checkOverlap(ArrayList<LocalDateTime> newEventDateTimeArray, String id) {
		
		JSONObject allEvents = new JSONObject(getAllEvents().getBody());
		JSONArray eventsArray = allEvents.getJSONArray("message");

		for (int i = 0; i < eventsArray.length(); i++) {
			if( id.compareToIgnoreCase(eventsArray.getJSONObject(i).get("name").toString())!= 0) {
				JSONObject event = new JSONObject(eventsArray.getJSONObject(i).get("data").toString());
				ArrayList<LocalDateTime> eventDateTimeArray = getEventStartandEndDate(event);
				if (!(newEventDateTimeArray.get(0).isAfter(eventDateTimeArray.get(1))
						|| newEventDateTimeArray.get(1).isBefore(eventDateTimeArray.get(0)))) {
					return true;
				}
			}
			
		}
		
		return false;
	}

}
