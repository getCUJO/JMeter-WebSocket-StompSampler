package JMeter.plugins.functional.samplers.websocket;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebSocketSamplerTest {

    @Test
    public void testPayloadFormatter() throws Exception {
        String rawPayload = "SUBSCRIBE\n" +
                "id:sub-0\n" +
                "destination:/user/queue/status";
        String result = WebSocketSampler.getStompPayload(rawPayload);
        String expectedPayload = "[\"SUBSCRIBE\n" +
                "id:sub-0\n" +
                "destination:/user/queue/status\\n\\n\\u0000\"]";
        assertEquals(expectedPayload, result);
    }
}