package com.leidos.xchangecore.adapter.webclient;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.uicds.incidentManagementService.ArchiveIncidentRequestDocument;
import org.uicds.incidentManagementService.ArchiveIncidentResponseDocument;
import org.uicds.incidentManagementService.CloseIncidentRequestDocument;
import org.uicds.incidentManagementService.CloseIncidentResponseDocument;
import org.uicds.incidentManagementService.CreateIncidentRequestDocument;
import org.uicds.incidentManagementService.CreateIncidentResponseDocument;
import org.uicds.incidentManagementService.UpdateIncidentRequestDocument;
import org.uicds.incidentManagementService.UpdateIncidentResponseDocument;

import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.leidos.xchangecore.adapter.util.Util;
import com.saic.precis.x2009.x06.base.ProcessingStateType;
import com.saic.precis.x2009.x06.structures.WorkProductDocument.WorkProduct;

public class WebServiceClient {

    public static WebServiceTemplate getWebServiceTemplate() {

        return webServiceTemplate;
    }

    public static void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {

        WebServiceClient.webServiceTemplate = webServiceTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(WebServiceClient.class);

    private static WebServiceTemplate webServiceTemplate;

    public WebServiceClient() {

        super();
    }

    public WebServiceClient(String uri, String username, String password) {

        super();

        webServiceTemplate.setDefaultUri(uri);
        final HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setCredentials(new UsernamePasswordCredentials(username, password));
        try {
            messageSender.afterPropertiesSet();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.debug("WebServiceClient: url: " + uri + ", username: " + username);
        webServiceTemplate.setMessageSender(messageSender);
    }

    public boolean createIncident(MappedRecord record) {

        // logger.debug("createIncident: " + record);
        final CreateIncidentRequestDocument request = CreateIncidentRequestDocument.Factory.newInstance();
        request.addNewCreateIncidentRequest().addNewIncident().set(Util.getIncidentDocument(record));

        final CreateIncidentResponseDocument response = (CreateIncidentResponseDocument) this.sendAndReceive(request);

        if ((response != null) &&
            response.getCreateIncidentResponse().getWorkProductPublicationResponse().getWorkProductProcessingStatus().getStatus().equals(ProcessingStateType.ACCEPTED)) {
            final WorkProduct workProduct = response.getCreateIncidentResponse().getWorkProductPublicationResponse().getWorkProduct();
            record.setIgID(Util.getIGID(workProduct));
            record.setWorkProductID(Util.getProductIdentification(workProduct));
            logger.debug("createIncident: IGID: " + record.getIgID() + ", WPID: " +
                record.getWorkProductID());
            return true;
        }
        return false;
    }

    public boolean deleteIncident(MappedRecord record) {

        // logger.debug("deleteIncident: " + record);
        final CloseIncidentRequestDocument closeRequest = CloseIncidentRequestDocument.Factory.newInstance();
        closeRequest.addNewCloseIncidentRequest().setIncidentID(record.getIgID());
        final CloseIncidentResponseDocument closeResponse = (CloseIncidentResponseDocument) this.sendAndReceive(closeRequest);
        if ((closeResponse != null) &&
            closeResponse.getCloseIncidentResponse().getWorkProductProcessingStatus().getStatus().equals(ProcessingStateType.ACCEPTED)) {
            final ArchiveIncidentRequestDocument archiveRequest = ArchiveIncidentRequestDocument.Factory.newInstance();
            archiveRequest.addNewArchiveIncidentRequest().setIncidentID(record.getIgID());
            final ArchiveIncidentResponseDocument archiveResponse = (ArchiveIncidentResponseDocument) this.sendAndReceive(archiveRequest);
            if ((archiveResponse != null) &&
                archiveResponse.getArchiveIncidentResponse().getWorkProductProcessingStatus().getStatus().equals(ProcessingStateType.ACCEPTED)) {
                logger.debug("deleteIncident: delete: [" + record.getIgID() +
                    "] ... successful ...");
                return true;
            }
        }
        return false;
    }

    private XmlObject sendAndReceive(XmlObject request) {

        XmlObject response = null;
        try {
            response = (XmlObject) webServiceTemplate.marshalSendAndReceive(request);
        } catch (final Throwable e) {
            logger.error("WebServiceClient.marshalSendAndReceive: " + e.getMessage());
        }
        return response;
    }

    public boolean updateIncident(MappedRecord record) {

        // logger.debug("updateIncident: " + record);
        final UpdateIncidentRequestDocument request = UpdateIncidentRequestDocument.Factory.newInstance();
        request.addNewUpdateIncidentRequest().addNewIncident().set(Util.getIncidentDocument(record));
        request.getUpdateIncidentRequest().addNewWorkProductIdentification().set(Util.getProductIdentification(record.getWorkProductID()));

        final UpdateIncidentResponseDocument response = (UpdateIncidentResponseDocument) this.sendAndReceive(request);
        if ((response != null) &&
            response.getUpdateIncidentResponse().getWorkProductPublicationResponse().getWorkProductProcessingStatus().equals(ProcessingStateType.ACCEPTED)) {
            final WorkProduct workProduct = response.getUpdateIncidentResponse().getWorkProductPublicationResponse().getWorkProduct();
            record.setWorkProductID(Util.getProductIdentification(workProduct));
            logger.debug("updateIncident: new WPID: " + record.getWorkProductID());
            return true;
        }
        return false;
    }
}
