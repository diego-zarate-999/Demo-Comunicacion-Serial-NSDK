# Demo Comunicación Serial usando NSDK.

App demo para establecer comunicación serial con PINPAD P180.

## Funciones de NSDK utilizadas.
Se utiliza la interfaz **ExternalCommunicator** de NSDK que permite hacer uso de los métodos siguientes:
- **receive(int timeout)**: Para recibir información por puerto serial.
- **send(byte[] data, int timeout)**: Permite enviar datos por puerto serial.

