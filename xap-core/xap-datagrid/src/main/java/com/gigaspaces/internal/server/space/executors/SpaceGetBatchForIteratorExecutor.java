package com.gigaspaces.internal.server.space.executors;

import com.gigaspaces.internal.client.SpaceIteratorBatchResult;
import com.gigaspaces.internal.server.space.SpaceImpl;
import com.gigaspaces.internal.server.space.iterator.ServerIteratorRequestInfo;
import com.gigaspaces.internal.space.requests.GetBatchForIteratorSpaceRequestInfo;
import com.gigaspaces.internal.space.requests.SpaceRequestInfo;
import com.gigaspaces.internal.space.responses.SpaceResponseInfo;
import com.gigaspaces.security.authorities.SpaceAuthority;
import com.j_spaces.core.GetBatchForIteratorException;
import com.j_spaces.core.ServerIteratorAnswerHolder;

import java.util.UUID;

public class SpaceGetBatchForIteratorExecutor extends SpaceActionExecutor{
    @Override
    public SpaceResponseInfo execute(SpaceImpl space, SpaceRequestInfo spaceRequestInfo) {
        GetBatchForIteratorSpaceRequestInfo requestInfo = (GetBatchForIteratorSpaceRequestInfo) spaceRequestInfo;
        ServerIteratorAnswerHolder serverIteratorAnswerHolder = null;
        GetBatchForIteratorException exception = null;
        try {
            serverIteratorAnswerHolder = space.getNextBatchFromServerIterator(requestInfo.getTemplatePacket(), requestInfo.getSpaceContext(),requestInfo.getModifiers(), new ServerIteratorRequestInfo(requestInfo.getIteratorId(), requestInfo.getBatchSize(), requestInfo.getBatchNumber(), requestInfo.getMaxInactiveDuration()));
        } catch (Exception e) {
            exception = e instanceof GetBatchForIteratorException ? (GetBatchForIteratorException) e : new GetBatchForIteratorException(e) ;
        }
        int partitionId = space.getPartitionId();
        UUID uuid = requestInfo.getIteratorId();
        if(exception != null) {
            exception.setPartitionId(partitionId).setBatchNumber(requestInfo.getBatchNumber());
            return new SpaceIteratorBatchResult(exception, uuid);
        }
        return new SpaceIteratorBatchResult(serverIteratorAnswerHolder.getEntryPackets(), partitionId, serverIteratorAnswerHolder.getBatchNumber(), uuid);
    }

    @Override
    public SpaceAuthority.SpacePrivilege getPrivilege() {
        return SpaceAuthority.SpacePrivilege.READ;
    }
}
