public class SmsMessageData {
    public String sender;
    public String link;
    public String timestamp;

    // Constructor vacío requerido por Firebase
    public SmsMessageData() {
    }

    // Constructor con parámetros
    public SmsMessageData(String sender, String link, String timestamp) {
        this.sender = sender;
        this.link = link;
        this.timestamp = timestamp;
    }
}
