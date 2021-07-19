function fn() {

    karate.configure('connectTimeout', 1000);
    karate.configure('readTimeout', 1000);

    return {
        serverUrl: Java.type('nl.rabobank.integrationtest.IntegrationTest').getServerUrl()
    };
}