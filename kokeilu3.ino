#include <Wire.h>

#define CTRL_REG1 0x20
#define CTRL_REG2 0x21
#define CTRL_REG3 0x22
#define CTRL_REG4 0x23
#define CTRL_REG5 0x24

int L3G4200D_Address = 105; //I2C address of the L3G4200D

const int analogInPin0 = A0;  // x
const int analogInPin1 = A1;  // y
const int analogInPin2 = A2;  // z


int sensorValue0 = 0;        // x
int sensorValue1 = 0;        // y
int sensorValue2 = 0;        // z


double kiihtyvyys_x = 0;  //x-akselin kiihtyvyys m/s^2
double kiihtyvyys_y = 0;  //x-akselin kiihtyvyys m/s^2
double kiihtyvyys_z = 0;  //x-akselin kiihtyvyys m/s^2
unsigned long aika = 0;
unsigned long aika2 = 0;
int x;
int y;
int z;
double Maksimi = 0;
double Minimi = 0;
int tilaylos = 0;



double tila(){
  
  if(kiihtyvyys_y > 3){
  tilaylos = 1;
  Maksimi = kiihtyvyys_y;
  return tilaylos, Maksimi;
  }
  
  if(kiihtyvyys_y < -2){
  tilaylos = 0;
  Minimi = kiihtyvyys_y;
  return tilaylos, Minimi;
  }
}



void setup(){

  Wire.begin();
  Serial.begin(115200);

  Serial.println("starting up L3G4200D");
  setupL3G4200D(2000); // Configure L3G4200  - 250, 500 or 2000 deg/sec

  delay(1500); //wait for the sensor to be ready 
}

void loop(){
   getGyroValues();
   // This will update x, y, and z with new values

  sensorValue0 = analogRead(analogInPin0);
   sensorValue1 = analogRead(analogInPin1);
    sensorValue2 = analogRead(analogInPin2);
   //jannite = sensorValue/1023.0 * 5.0;
   aika2 = aika;
   aika = millis();

kiihtyvyys_x = 0.1486 * sensorValue0 -49.79;
kiihtyvyys_x = constrain(kiihtyvyys_x, -9.81, 9.81);
  
kiihtyvyys_y = 0.1464 * sensorValue1 -48.80;
kiihtyvyys_y = constrain(kiihtyvyys_y, -9.81, 9.81);
  
kiihtyvyys_z = 0.1452 * sensorValue2 -49.32;
kiihtyvyys_z = constrain(kiihtyvyys_z, -9.81, 9.81);
    
tila();



  Serial.print(aika);
  Serial.print("\t");
  Serial.print(kiihtyvyys_x);
  Serial.print("\t");
  Serial.print(kiihtyvyys_y);
  Serial.print("\t ");
  Serial.print(kiihtyvyys_z);
  Serial.print("\t ");
  Serial.print(x);
  Serial.print("\t");
  Serial.print(y);
  Serial.print("\t");
  Serial.print(z);
  Serial.print(" \t ");
  Serial.print(Maksimi);
  Serial.print(" \t ");
  Serial.print(Minimi);
  Serial.print(" \t ");
  Serial.print(tilaylos);
  Serial.println(" \t ");


  delay(50); //Just here to slow down the serial to make it more readable
}

void getGyroValues(){

  byte xMSB = readRegister(L3G4200D_Address, 0x29);
  byte xLSB = readRegister(L3G4200D_Address, 0x28);
  x = ((xMSB << 8) | xLSB);

  byte yMSB = readRegister(L3G4200D_Address, 0x2B);
  byte yLSB = readRegister(L3G4200D_Address, 0x2A);
  y = ((yMSB << 8) | yLSB);

  byte zMSB = readRegister(L3G4200D_Address, 0x2D);
  byte zLSB = readRegister(L3G4200D_Address, 0x2C);
  z = ((zMSB << 8) | zLSB);
}

int setupL3G4200D(int scale){
  //From  Jim Lindblom of Sparkfun's code

  // Enable x, y, z and turn off power down:
  writeRegister(L3G4200D_Address, CTRL_REG1, 0b00001111);

  // If you'd like to adjust/use the HPF, you can edit the line below to configure CTRL_REG2:
  writeRegister(L3G4200D_Address, CTRL_REG2, 0b00000000);

  // Configure CTRL_REG3 to generate data ready interrupt on INT2
  // No interrupts used on INT1, if you'd like to configure INT1
  // or INT2 otherwise, consult the datasheet:
  writeRegister(L3G4200D_Address, CTRL_REG3, 0b00001000);

  // CTRL_REG4 controls the full-scale range, among other things:

  if(scale == 250){
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00000000);
  }else if(scale == 500){
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00010000);
  }else{
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00110000);
  }

  // CTRL_REG5 controls high-pass filtering of outputs, use it
  // if you'd like:
  writeRegister(L3G4200D_Address, CTRL_REG5, 0b00000000);
}

void writeRegister(int deviceAddress, byte address, byte val) {
    Wire.beginTransmission(deviceAddress); // start transmission to device 
    Wire.write(address);       // send register address
    Wire.write(val);         // send value to write
    Wire.endTransmission();     // end transmission
}

int readRegister(int deviceAddress, byte address){

    int v;
    Wire.beginTransmission(deviceAddress);
    Wire.write(address); // register to read
    Wire.endTransmission();

    Wire.requestFrom(deviceAddress, 1); // read a byte

    while(!Wire.available()) {
        // waiting
    }

    v = Wire.read();
    return v;
}
