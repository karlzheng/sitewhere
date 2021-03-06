/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.client;

import java.util.List;
import java.util.UUID;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Provides an {@link IMultitenantApiChannel} that supplies the
 * {@link IDeviceEventManagement}. API.
 * 
 * @author Derek
 */
public interface IDeviceEventManagementApiChannel<T extends MultitenantGrpcChannel<?, ?>>
	extends IMultitenantApiChannel<T> {

    /**
     * Add a batch of events for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param batch
     */
    public void addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch,
	    StreamObserver<IDeviceEventBatchResponse> observer) throws SiteWhereException;

    /**
     * Get a device event by id.
     * 
     * @param deviceId
     * @param eventId
     * @param observer
     * @throws SiteWhereException
     */
    public void getDeviceEventById(UUID deviceId, UUID eventId, StreamObserver<IDeviceEvent> observer)
	    throws SiteWhereException;

    /**
     * Get a device event by alternate (external) id.
     * 
     * @param deviceId
     * @param alternateId
     * @param observer
     * @throws SiteWhereException
     */
    public void getDeviceEventByAlternateId(UUID deviceId, String alternateId, StreamObserver<IDeviceEvent> observer)
	    throws SiteWhereException;

    /**
     * Add measurements for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param measurements
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementsCreateRequest measurements,
	    StreamObserver<IDeviceMeasurements> observer) throws SiteWhereException;

    /**
     * List device measurement entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceMeasurements>> observer)
	    throws SiteWhereException;

    /**
     * Add location for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceLocation(UUID deviceAssignmentId, IDeviceLocationCreateRequest request,
	    StreamObserver<IDeviceLocation> observer) throws SiteWhereException;

    /**
     * List device location entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceLocation>> observer)
	    throws SiteWhereException;

    /**
     * Add alert for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceAlert(UUID deviceAssignmentId, IDeviceAlertCreateRequest request,
	    StreamObserver<IDeviceAlert> observer) throws SiteWhereException;

    /**
     * List device location entries for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceAlert>> observer)
	    throws SiteWhereException;

    /**
     * Add a chunk of stream data for a given device assignment.
     * 
     * @param deviceAssignmentId
     * @param stream
     * @param request
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceStreamData(UUID deviceAssignmentId, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request, StreamObserver<IDeviceStreamData> observer)
	    throws SiteWhereException;

    /**
     * Get a single chunk of data from a device stream.
     * 
     * @param deviceAssignmentId
     * @param streamId
     * @param sequenceNumber
     * @param observer
     * @throws SiteWhereException
     */
    public void getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber,
	    StreamObserver<IDeviceStreamData> observer) throws SiteWhereException;

    /**
     * List all chunks of data in a device assignment that belong to a given stream
     * and meet the criteria.
     * 
     * @param assignmentId
     * @param streamId
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceStreamDataForAssignment(UUID assignmentId, String streamId, IDateRangeSearchCriteria criteria,
	    StreamObserver<ISearchResults<IDeviceStreamData>> observer) throws SiteWhereException;

    /**
     * Add a device command invocation event for the given assignment.
     * 
     * @param deviceAssignmentId
     * @param request
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceCommandInvocation(UUID deviceAssignmentId, IDeviceCommandInvocationCreateRequest request,
	    StreamObserver<IDeviceCommandInvocation> observer) throws SiteWhereException;

    /**
     * List device command invocation events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceCommandInvocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceCommandInvocation>> observer)
	    throws SiteWhereException;

    /**
     * List responses associated with a command invocation.
     * 
     * @param deviceId
     * @param invocationId
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceCommandInvocationResponses(UUID deviceId, UUID invocationId,
	    StreamObserver<ISearchResults<IDeviceCommandResponse>> observer) throws SiteWhereException;

    /**
     * Adds a new device command response event.
     * 
     * @param deviceAssignmentId
     * @param request
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceCommandResponse(UUID deviceAssignmentId, IDeviceCommandResponseCreateRequest request,
	    StreamObserver<IDeviceCommandResponse> observer) throws SiteWhereException;

    /**
     * List device command response events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceCommandResponse>> observer)
	    throws SiteWhereException;

    /**
     * Adds a new device state change event.
     * 
     * @param deviceAssignmentId
     * @param request
     * @param observer
     * @throws SiteWhereException
     */
    public void addDeviceStateChange(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest request,
	    StreamObserver<IDeviceStateChange> observer) throws SiteWhereException;

    /**
     * List device state change events for an index based on criteria.
     * 
     * @param index
     * @param entityIds
     * @param criteria
     * @param observer
     * @throws SiteWhereException
     */
    public void listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceStateChange>> observer)
	    throws SiteWhereException;
}