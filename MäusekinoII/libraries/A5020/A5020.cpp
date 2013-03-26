/*
  A5020.cpp - Mouse sensor library
  Copyright (C) 2009 Aleksi Pihkanen.  All rights reserved.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  See file LICENSE.txt for further informations on licensing terms.
*/


#include "Arduino.h"
#include "A5020.h"

// ADNS internal register adresses
#define PRODUCT_ID          0x00 // should be 0x12
#define PRODUCTID2          0x3e
#define REVISION_ID         0x01

#define MOTION				0x02
#define DELTA_Y_REG         0x03
#define DELTA_X_REG         0x04
#define SQUAL_REG           0x05
#define MAXIMUM_PIXEL_REG   0x08
#define MINIMUM_PIXEL_REG   0x0a
#define PIXEL_SUM_REG       0x09
#define PIXEL_DATA_REG      0x0b
#define SHUTTER_UPPER_REG   0x06
#define SHUTTER_LOWER_REG   0x07
#define MOTION_BURST		0x63

#define RESET		    0x3a
#define CPI500v		    0x00
#define CPI1000v	    0x01

#define NUM_PIXELS          225


A5020::A5020(uint8_t c,uint8_t d,uint8_t e){
  _clock = c; _data = d; _enable = e;
  delta_x = 0;
  delta_y = 0;
  squal = 0;
  pinMode(_clock, OUTPUT);
  pinMode(_data, OUTPUT);
  pinMode(_enable, OUTPUT);
  digitalWrite(_enable, HIGH);
}

void A5020::writeByte(byte c){
  pinMode(_data, OUTPUT);
  for(i=0x80;i;i=i>>1){
    digitalWrite(_clock, LOW);
    digitalWrite(_data, c & i);
    digitalWrite(_clock, HIGH);
  }
}

byte A5020::readByte(){
  byte ret=0;
  pinMode(_data, INPUT);
  for(i=0x80;i;i=i>>1){
    digitalWrite(_clock, LOW);
    ret |= i*digitalRead(_data);
    digitalWrite(_clock, HIGH);
  }
  return(ret);
}

void A5020::reset(){
  // Initiate chip reset
  digitalWrite(_enable, LOW);
  writeByte(RESET);
  writeByte(0x5a);
  digitalWrite(_enable, HIGH);
  // Set 1000cpi resolution
  digitalWrite(_enable, LOW);
  writeByte(0x0d);
  writeByte(CPI1000v);
  digitalWrite(_enable, HIGH);
}


void A5020::sync() {
  pinMode(_enable, OUTPUT);
  digitalWrite(_enable, LOW);
  delayMicroseconds(2);
  digitalWrite(_enable, HIGH);
}

void A5020::write(unsigned char addr, unsigned char data) {
  digitalWrite(_enable, LOW);
  writeByte(addr);
  writeByte(data);
  digitalWrite(_enable, HIGH);
}

byte A5020::read(unsigned char addr) {
  digitalWrite(_enable, LOW);
  writeByte(addr);

  return readByte();
  digitalWrite(_enable, HIGH);
}

void A5020::readMotionBurst() {

  writeByte(MOTION);
  byte motion = readByte() & 0x80;

  if(motion){
  writeByte(MOTION_BURST);
	  delta_x = readByte();
	  delta_y = readByte();
	  squal = readByte();
  } else {
	  delta_x = delta_y = 0;
  }
}

void A5020::pixelGrab(byte* pix)
{

  write(PIXEL_DATA_REG, 0x0);
  int grabCount = 0;
  while( grabCount < NUM_PIXELS )
  {
    *(pix+grabCount) = read(PIXEL_DATA_REG);
    if ((*(pix+grabCount)&0x80) == 0x80 )
    grabCount++;
  }
}
