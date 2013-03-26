/*
  A5020.h - Mouse sensor library
  Copyright (C) 2009 Aleksi Pihkanen.  All rights reserved.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  See file LICENSE.txt for further informations on licensing terms.
*/
#ifndef A5020_h
#define A5020_h
#include "Arduino.h"
#include "inttypes.h"

class A5020 {
  private:
    uint8_t _clock, _data, _enable, i;
    void writeByte(byte);
    byte readByte();
    void write(unsigned char addr, unsigned char data);
    byte read(unsigned char addr);
  public:
    int8_t delta_x, delta_y;
    uint8_t squal, motion;
    A5020(uint8_t,uint8_t,uint8_t);
    void reset();
    void sync();
    void readMotionBurst();
    void pixelGrab(byte* pix);
};

#endif
