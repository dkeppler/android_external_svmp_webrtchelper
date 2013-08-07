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

package org.mitre.svmp.webrtc.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.mitre.svmp.protocol.SVMPProtocol;
import org.mitre.svmp.protocol.SVMPProtocol.Request;
import org.mitre.svmp.protocol.SVMPProtocol.Response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class PeerConnectionServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    private static final int FBSTREAM_PEER_ID = 0;
    private static final int CLIENT_PEER_ID = 1;
    
    // names are from the protobuf side's perspective, so
    //    sendQueue    = from the HTTP side, out the protobuf side
    //    receiveQueue = in the protobuf side, out the HTTP side 
    private BlockingQueue<SVMPProtocol.Response> sendQueue;
    private BlockingQueue<SVMPProtocol.Request> receiveQueue;
    
    public PeerConnectionServerHandler(BlockingQueue<Response> sendQueue,
            BlockingQueue<Request> receiveQueue)
    {
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)
            throws Exception {

        // All HTTP messages used by the peerconnection_client:
        //
        // "GET /sign_in?%s HTTP/1.0\r\n\r\n", client_name_.c_str());
        // "POST /message?peer_id=%i&to=%i HTTP/1.0\r\n"
        // "GET /sign_out?peer_id=%i HTTP/1.0\r\n\r\n", my_id_);
        // "GET /wait?peer_id=%i HTTP/1.0\r\n\r\n", my_id_);
        
        DefaultFullHttpResponse response;

        HttpMethod method = req.getMethod();
        if (method.equals(HttpMethod.GET)) {
            String uri = req.getUri();
            QueryStringDecoder queryDecoder = new QueryStringDecoder(uri);
            Map<String, List<String>> params = queryDecoder.parameters();
            
            // Case: GET /sign_in?%s
            if (uri.startsWith("/sign_in")) {
                // should have a username parameter and it should be "fbstreamer"
                String username = uri.split("?")[1];
                
                // return a comma delimited list of name,peer_id,is_connected
                // where the first line is the info of the peer we're talking to
                // and all subsequent lines are the set of other connected peers
                
                // since fbstreamer on the HTTP side will always be the first peer to connect
                // lie about the other peer being there
                
                ByteBuf content = Unpooled.copiedBuffer(username + "," + FBSTREAM_PEER_ID + ",1\n" +
                                                        "svmpclient," + CLIENT_PEER_ID + ",1",
                        CharsetUtil.US_ASCII);
                response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

                response.headers().set(CONTENT_TYPE, "text/plain");
                response.headers().set(CONNECTION, "close");
                response.headers().set(PRAGMA, FBSTREAM_PEER_ID);
                
                // do we have to set the content-length header ourselves?
                // if so, how do we calculate it?
            }

            // Case: GET /sign_out?peer_id=%i
            else if (uri.startsWith("/sign_out")) {
                // TODO might be worth checking that the peer_id is actually FBSTREAM_PEER_ID
                // doesn't really matter though
                
                response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                response.headers().set(CONNECTION, "close");
                response.headers().set(CONTENT_LENGTH, 0);

                // create a BYE message, no other peers to notify
                // add it to the sendQueue
                // TODO
            }

            // Case: GET /wait?peer_id=%i
            else if (uri.startsWith("/wait")) {
                // pull something off receiveQueue
                
                // if it's a peer presence info message, set pragma to the client's ID
                // function handleServerNotification(data) {
                //   trace("Server notification: " + data);
                //   var parsed = data.split(',');
                //   if (parseInt(parsed[2]) != 0)
                //   other_peers[parseInt(parsed[1])] = parsed[0];
                // }
                
                // need to check how to set the Connection header
             
                // if it's a message from another peer, set pragma to that peer's ID
                
                // convert it to JSON
                // send in HTTP response
                response = null;
            }
        } else if (method.equals(HttpMethod.POST) && uri.startsWith("/message")) {
            // Case: POST /message?peer_id=%i&to=%i
            
            // decode the JSON payload
            // convert it to protobuf
            // add it to sendQueue
            
            // generate appropriate HTTP response
        } else {
            // some other HTTP request we don't support
            // send an error or something
        }
        
        // send response
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

}
