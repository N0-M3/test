import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.concurrent.TimeUnit;

class CrptApi {
    private int count = 0; //инициализируем счетчик клиентов
    private TimeUnit timeUnit;
    private int request;
    private boolean keepRun = true;

    //создаем конструктор обращения
    public CrptApi(TimeUnit timeUnit, int requestLimit){
        this.timeUnit = timeUnit;
        this.request = requestLimit;
    }


    //создаем синхронизированный счетчик клиентов
    private synchronized int ClientCount(){
        return count = count + 1;

    }

    //запускаем создание запросов до поставленного лимита
    public void KeepRun() {
        while(keepRun){
            MyThread thread = new MyThread();
            thread.run();
            int client = ClientCount();
            if (client > request){
                keepRun = false;
            }
        }
    }


    //метод, создающий сам поток, который выполняет нужную нам функицю DoSom();
    private class MyThread implements Runnable {
        public void run(){
            try {
                DoSom();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //отправляем нужные данные, которые можно было вынести в отдельный json-файл, но в задании сказано уложиться в один файл
    private void DoSom() throws IOException {
        URL url = new URL("https://ismp.crpt.ru/api/v3/lk/documents/create");

        //создаем объект HttpURLConnection и настраиваем его
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        //создаем тело запроса
        String body = "{\"description\":\n" +
                "{ \"participantInn\": \"string\" }, \"doc_id\": \"string\", \"doc_status\": \"string\",\n" +
                "\"doc_type\": \"LP_INTRODUCE_GOODS\", 109 \"importRequest\": true,\n" +
                "\"owner_inn\": \"string\", \"participant_inn\": \"string\", \"producer_inn\":\n" +
                "\"string\", \"production_date\": \"2020-01-23\", \"production_type\": \"string\",\n" +
                "\"products\": [ { \"certificate_document\": \"string\",\n" +
                "\"certificate_document_date\": \"2020-01-23\",\n" +
                "\"certificate_document_number\": \"string\", \"owner_inn\": \"string\",\n" +
                "\"producer_inn\": \"string\", \"production_date\": \"2020-01-23\",\n" +
                "\"tnved_code\": \"string\", \"uit_code\": \"string\", \"uitu_code\": \"string\" } ],\n" +
                "\"reg_date\": \"2020-01-23\", \"reg_number\": \"string\"}";

        //записываем тело запроса в поток вывода
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(body);
        writer.flush();
    }
}
