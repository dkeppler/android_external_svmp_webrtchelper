package org.mitre.svmp.webrtc;

import org.mitre.svmp.protocol.WebRTCProtocol.WebRTCMessage;

import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.JsonFormat.ParseException;

public class Translator {
    
    public static String ProtobufToJSON(WebRTCMessage pb) {
        return JsonFormat.printToString(pb);
    }
    
    public static WebRTCMessage JSONToProtobuf(String json) throws ParseException {
        WebRTCMessage.Builder pb = WebRTCMessage.newBuilder();
        JsonFormat.merge(json, pb);
        return pb.build();
    }

    private Translator() {};
}