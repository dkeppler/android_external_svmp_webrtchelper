/*
 * Copyright 2013 The MITRE Corporation, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mitre.svmp.webrtc;

import org.mitre.svmp.webrtc.http.TranslatorHttpServer;
import org.mitre.svmp.webrtc.protobuf.TranslatorProtobufClient;

public class Main {

    // Port we'll listen on for HTTP/JSON inputs
    public static int DEFAULT_LISTEN_PORT = 8888;
    
    // For running on an SVMP VM instance and talking to the local input event server
    // This outbound connection speaks the WebRTCMessage subset of the SVMP protubuf protocol
    public static String DEFAULT_DEST_HOST = "localhost";
    public static int DEFAULT_DEST_PORT = 7675;
    
    public static void main(String[] args) throws Exception {
        // start up two threads for each side of the proxy
        TranslatorHttpServer httpSide = new TranslatorHttpServer(DEFAULT_LISTEN_PORT);
        TranslatorProtobufClient protobufSide = new TranslatorProtobufClient(DEFAULT_DEST_HOST, DEFAULT_DEST_PORT);

        // assign some message queues so the two sides can pass work to each other
        // TODO
        
        protobufSide.run();
        httpSide.run();
    }
}
