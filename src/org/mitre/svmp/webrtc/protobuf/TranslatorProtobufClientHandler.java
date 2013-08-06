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

package org.mitre.svmp.webrtc.protobuf;

import org.mitre.svmp.protocol.SVMPProtocol;
import org.mitre.svmp.protocol.SVMPProtocol.Request.RequestType;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TranslatorProtobufClientHandler extends SimpleChannelInboundHandler<SVMPProtocol.Request> implements
        ChannelHandler {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SVMPProtocol.Request msg) throws Exception {
        // should be a Request with a WebRTCMessage inside it
        // ignore anything else
        if (msg.getType() == RequestType.WEBRTC && msg.hasWebrtcMsg()) {
            // TODO
            // translate it to JSON and send out the HTTP side to the "wait" connection
        }
    }


}
