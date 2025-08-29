package com.airport_management_system.MNG.models.dto;


public class HangarDTO {

 private Long hangarId;
 private String hangarName;
 private int capacity;
 private String hangarLocation;
 private Long userId;


 public Long getHangarId() {
     return hangarId;
 }

 public void setHangarId(Long hangarId) {
     this.hangarId = hangarId;
 }

 public String getHangarName() {
     return hangarName;
 }

 public void setHangarName(String hangarName) {
     this.hangarName = hangarName;
 }

 public int getCapacity() {
     return capacity;
 }

 public void setCapacity(int capacity) {
     this.capacity = capacity;
 }

 public String getHangarLocation() {
     return hangarLocation;
 }

 public void setHangarLocation(String hangarLocation) {
     this.hangarLocation = hangarLocation;
 }

 public Long getUserId() {
     return userId;
 }

 public void setUserId(Long userId) {
     this.userId = userId;
 }
}