// Copyright (c) Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.log4j.testhelpers.aws.sns;

import com.kdgregory.log4j.aws.SNSAppender;
import com.kdgregory.log4j.aws.internal.shared.LogMessage;
import com.kdgregory.log4j.aws.internal.shared.LogWriter;
import com.kdgregory.log4j.aws.internal.shared.MessageQueue;
import com.kdgregory.log4j.aws.internal.shared.ThreadFactory;
import com.kdgregory.log4j.aws.internal.shared.WriterFactory;
import com.kdgregory.log4j.aws.internal.sns.SNSWriterConfig;
import com.kdgregory.log4j.testhelpers.TestUtils;


/**
 *  This class provides visibility into the protected variables held by
 *  SNSAppender and AbstractAppender.
 */
public class TestableSNSAppender
extends SNSAppender
{

    public void setThreadFactory(ThreadFactory threadFactory)
    {
        this.threadFactory = threadFactory;
    }


    public void setWriterFactory(WriterFactory<SNSWriterConfig> writerFactory)
    {
        this.writerFactory = writerFactory;
    }


    public WriterFactory<SNSWriterConfig> getWriterFactory()
    {
        return writerFactory;
    }


    public LogWriter getWriter()
    {
        return writer;
    }


    public MockSNSWriter getMockWriter()
    {
        return (MockSNSWriter)writer;
    }


    public MessageQueue getMessageQueue()
    {
        return TestUtils.getFieldValue(writer, "messageQueue", MessageQueue.class);
    }


    public Throwable getLastWriterException()
    {
        return lastWriterException;
    }

    public void updateLastRotationTimestamp(long offset)
    {
        lastRotationTimestamp += offset;
    }


    public long getLastRotationTimestamp()
    {
        return lastRotationTimestamp;
    }


    @Override
    public boolean isMessageTooLarge(LogMessage message)
    {
        return super.isMessageTooLarge(message);
    }
}
