#include <LiquidCrystal.h>
#include <SoftwareSerial.h>

// Pins
const byte PIN_BT_RX = 4;
const byte PIN_BT_TX = 2;
const byte PIN_LCD_D4 = 8;
const byte PIN_LCD_D5 = 7;
const byte PIN_LCD_D6 = 1;
const byte PIN_LCD_D7 = 0;
const byte PIN_LCD_ENABLE = 12;
const byte PIN_LCD_RS = 13;
const byte PIN_LED = 11;
const byte PIN_MOTOR_LEFT_BWD = 9;
const byte PIN_MOTOR_LEFT_FWD = 6;
const byte PIN_MOTOR_RIGHT_BWD = 5;
const byte PIN_MOTOR_RIGHT_FWD = 3;
const byte PIN_SPEAKER = 10;

// Transmission
// Data format: STX TRA_TYPE [DATA] ETX
const byte TRA_MAX_LENGTH = 35;

// Transmission codes
const byte TRA_END = 3;
const byte TRA_START = 2;
const byte TRA_TYPE_LCD = 24;
const byte TRA_TYPE_LED = 21;
const byte TRA_TYPE_JOYSTICK = 23;
const byte TRA_TYPE_SPEAKER = 22;

// Motor
const byte MOTOR_MIN_SPEED = 50;
const byte MOTOR_MAX_SPEED = 255;
const float MOTOR_DIFF_SPEED_DIV_100 = (MOTOR_MAX_SPEED - MOTOR_MIN_SPEED) / 100;

LiquidCrystal lcd(PIN_LCD_RS, PIN_LCD_ENABLE, PIN_LCD_D4, PIN_LCD_D5, PIN_LCD_D6, PIN_LCD_D7);
SoftwareSerial bt(PIN_BT_RX, PIN_BT_TX);

byte bytesReceived[TRA_MAX_LENGTH];
bool errorOnReceive;

void setup() {
  // Bluetooth
  bt.begin(115200);
  bt.print("$$$");
  delay(100);
  bt.println("U,9600,N");
  delay(100);
  bt.begin(9600);

  // empty BT RX buffer
  while (bt.available())
  {
    bt.read();
  }

  // LCD
  lcd.begin(16, 2);

  // LED
  pinMode(PIN_LED, OUTPUT);
  digitalWrite(PIN_LED, LOW);

  // Motor
  pinMode(PIN_MOTOR_LEFT_BWD, OUTPUT);
  pinMode(PIN_MOTOR_LEFT_FWD, OUTPUT);
  pinMode(PIN_MOTOR_RIGHT_BWD, OUTPUT);
  pinMode(PIN_MOTOR_RIGHT_FWD, OUTPUT);
  engageMotors(0, 0);

  Serial.begin(9600);
}

void loop() {
  if (bt.available())
  {
    delay(5);
    errorOnReceive = false;
    bytesReceived[0] = bt.read();

    if (bytesReceived[0] == TRA_START)
    {
      byte currentByte = 1;
      while (bt.available())
      {
        delay(2);
        bytesReceived[currentByte] = bt.read();

        if (currentByte == TRA_MAX_LENGTH)
        {
          errorOnReceive = true;
          break;
        }
        else if (bytesReceived[currentByte] == TRA_END)
        {
          break;
        }

        currentByte++;
      }

      if (!errorOnReceive)
      {
        processReceivedBytes(currentByte + 1);
      }
    }
  }
}

void processReceivedBytes(byte numberOfBytesReceived)
{
  if (numberOfBytesReceived >= 4)
  {
    switch (bytesReceived[1])
    {
      case TRA_TYPE_LCD:
        processLCDTransmission(numberOfBytesReceived);
        break;
      case TRA_TYPE_LED:
        processLEDTransmission(numberOfBytesReceived);
        break;
      case TRA_TYPE_JOYSTICK:
        processJoystickTransmission(numberOfBytesReceived);
        break;
      case TRA_TYPE_SPEAKER:
        processSpeakerTransmission(numberOfBytesReceived);
        break;
    }
  }
}

void processLCDTransmission(byte numberOfBytesReceived)
{
  byte currentByte = 2;

  for (; currentByte < numberOfBytesReceived - 1 && currentByte < 18; currentByte++)
  {
    lcd.setCursor(currentByte - 2, 0);
    lcd.write(bytesReceived[currentByte]);
  }

  for (; currentByte < numberOfBytesReceived - 1; currentByte++)
  {
    lcd.setCursor(currentByte - 2, 1);
    lcd.write(bytesReceived[currentByte]);
  }
}

void processLEDTransmission(byte numberOfBytesReceived)
{
  if (numberOfBytesReceived == 4)
  {
    digitalWrite(PIN_LED, (bytesReceived[2] == 0 ? LOW : HIGH));
  }
}

void processJoystickTransmission(byte numberOfBytesReceived)
{
  if (numberOfBytesReceived == 5)
  {
    int speedLeft = bytesReceived[2];
    int speedRight = bytesReceived[3];
    
    if (bytesReceived[2] & 0b10000000 != 0)
    {
      bytesReceived[2] = bytesReceived[2] ^ 0b10000000;
      speedLeft = bytesReceived[2];
      speedLeft = -speedLeft;
    }

    if (bytesReceived[3] & 0b10000000 != 0)
    {
      bytesReceived[3] = bytesReceived[3] ^ 0b10000000;
      speedRight = bytesReceived[3];
      speedRight = -speedRight;
    }
    
    engageMotors(speedLeft, speedRight);
  }
}

void processSpeakerTransmission(byte numberOfBytesReceived)
{
  if (numberOfBytesReceived == 4)
  {

  }
}

void engageMotors(int speedLeftPercentage, int speedRightPercentage)
{
  // Percentages between -100 and 100

  Serial.print("Left: ");
  Serial.print(speedLeftPercentage);
  Serial.print(" right: ");
  Serial.println(speedRightPercentage);
  
  if (speedLeftPercentage == 0)
  {
    analogWrite(PIN_MOTOR_LEFT_BWD, 0);
    analogWrite(PIN_MOTOR_LEFT_FWD, 0);
  }
  else
  {
    byte leftMotorValue = speedLeftPercentage * MOTOR_DIFF_SPEED_DIV_100 + MOTOR_MIN_SPEED;

    if (leftMotorValue < 0)
    {
      analogWrite(PIN_MOTOR_LEFT_BWD, -leftMotorValue);
      analogWrite(PIN_MOTOR_LEFT_FWD, 0);
    }
    else
    {
      analogWrite(PIN_MOTOR_LEFT_BWD, 0);
      analogWrite(PIN_MOTOR_LEFT_FWD, leftMotorValue);
    }
  }

  if (speedRightPercentage == 0)
  {
    analogWrite(PIN_MOTOR_RIGHT_BWD, 0);
    analogWrite(PIN_MOTOR_RIGHT_FWD, 0);
  }
  else
  {
    byte rightMotorValue = speedRightPercentage * MOTOR_DIFF_SPEED_DIV_100 + MOTOR_MIN_SPEED;

    if (rightMotorValue < 0)
    {
      analogWrite(PIN_MOTOR_RIGHT_BWD, -rightMotorValue);
      analogWrite(PIN_MOTOR_RIGHT_FWD, 0);
    }
    else
    {
      analogWrite(PIN_MOTOR_RIGHT_BWD, 0);
      analogWrite(PIN_MOTOR_RIGHT_FWD, rightMotorValue);
    }
  }
}
