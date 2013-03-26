#include <A5020.h>

#define NCS_BLACK_ENABLE 10
#define SCLK_WEISS_CLOCK 12
#define SDIO_ROT_DATA  11

byte pix[225];

A5020 mouse(SCLK_WEISS_CLOCK,SDIO_ROT_DATA ,NCS_BLACK_ENABLE);

void setup()
{
  Serial.begin(115200);

  pinMode(SDIO_ROT_DATA, OUTPUT);
  pinMode(SCLK_WEISS_CLOCK, OUTPUT);
  pinMode(NCS_BLACK_ENABLE, OUTPUT);

  mouse.sync();
  mouse.reset();
  delay(50); // From NRESET pull high to valid mo tion, assuming VDD and motion is present.

}

void loop()
{
//  Serial.println(ADNS_read(MINIMUM_PIXEL_REG), BIN);
  mouse.pixelGrab(pix);
  Serial.write(0xFF);
  for (int i=0; i<225;i++) {
      Serial.write(pix[i] &0x7F);
   
  }
  delay(500);

  
}

