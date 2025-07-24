#include <BluetoothSerial.h>

BluetoothSerial SerialBT;

// Pines
const int botonPin = 33;
const int trigPin = 26;
const int echoPin = 25;
const int vibPin   = 13;
const int buzzerPin = 27;
const int ledPin    = 12;

// Variables
long duration;
int distance;
bool esperandoConfirmacion = false;

void alertaProximidad(int onTime, int offTime) {
  digitalWrite(vibPin, HIGH);
  digitalWrite(buzzerPin, HIGH);
  digitalWrite(ledPin, HIGH);
  delay(onTime);
  digitalWrite(vibPin, LOW);
  digitalWrite(buzzerPin, LOW);
  digitalWrite(ledPin, LOW);
  delay(offTime);
}

void vibracionConfirmacion() {
  // Vibración rápida de confirmación
  for (int i = 0; i < 3; i++) {
    digitalWrite(ledPin, HIGH);
    digitalWrite(buzzerPin, HIGH);
    delay(150);
    digitalWrite(ledPin, LOW);
    digitalWrite(buzzerPin, LOW);
    delay(150);
  }
}

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_ALERT_E");

  pinMode(botonPin, INPUT_PULLUP);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(vibPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(ledPin, OUTPUT);

  // Mostrar MAC Bluetooth
  uint8_t macBT[6];
  esp_read_mac(macBT, ESP_MAC_BT);
  Serial.printf("MAC BT: %02X:%02X:%02X:%02X:%02X:%02X\n",
                macBT[0], macBT[1], macBT[2],
                macBT[3], macBT[4], macBT[5]);
}

void loop() {
  // Verificar botón
  if (digitalRead(botonPin) == LOW && !esperandoConfirmacion) {
    SerialBT.println("SEND_SMS");
    Serial.println(">> Botón presionado, comando SEND_SMS enviado");
    esperandoConfirmacion = true;

    while (digitalRead(botonPin) == LOW) {
      delay(10);
    }
  }

  // Leer respuesta del Bluetooth
  if (SerialBT.available()) {
    String response = SerialBT.readStringUntil('\n');
    response.trim();

    Serial.print("Respuesta recibida: ");
    Serial.println(response);

    if (response.equals("SMS_ENVIADO")) {
      Serial.println("SMS confirmado por Android ✅");
      vibracionConfirmacion();
      esperandoConfirmacion = false;
    }
  }

  // Medición de distancia
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH, 30000);
  distance = duration * 0.034 / 2;

  Serial.print("Distancia: ");
  Serial.print(distance);
  Serial.println(" cm");

  // Control de alertas según la distancia
  if (distance > 0 && distance <= 5) {
    alertaProximidad(30, 20);
  } else if (distance <= 10) {
    alertaProximidad(50, 50);
  } else if (distance <= 15) {
    alertaProximidad(100, 100);
  } else if (distance <= 20) {
    alertaProximidad(200, 200);
  } else {
    digitalWrite(vibPin, LOW);
    digitalWrite(buzzerPin, LOW);
    digitalWrite(ledPin, LOW);
    delay(100);
  }
}