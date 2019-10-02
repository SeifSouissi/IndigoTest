package com.indigo.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
	 private String id = UUID.randomUUID().toString();
	 private String name;
	 private String dateTime;
	 private float duration;
	 private String brief;


	 // Getter Methods 

	 public String getId() {
	  return id;
	 }

	 public String getName() {
	  return name;
	 }

	 public String getDateTime() {
	  return dateTime;
	 }

	 public float getDuration() {
	  return duration;
	 }

	 public String getBrief() {
	  return brief;
	 }

	 // Setter Methods 

	 public void setId(String id) {
	  this.id = id;
	 }

	 public void setName(String name) {
	  this.name = name;
	 }

	 public void setDateTime(String dateTime) {
	  this.dateTime = dateTime;
	 }

	 public void setDuration(float duration) {
	  this.duration = duration;
	 }

	 public void setBrief(String brief) {
	  this.brief = brief;
	 }
	}