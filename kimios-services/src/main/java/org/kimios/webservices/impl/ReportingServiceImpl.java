/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.webservices.impl;


import org.kimios.kernel.security.model.Session;
import org.kimios.webservices.CoreService;
import org.kimios.webservices.exceptions.DMServiceException;
import org.kimios.webservices.ReportingService;

import javax.jws.WebService;


@WebService(targetNamespace = "http://kimios.org", serviceName = "ReportingService", name = "ReportingService")
public class ReportingServiceImpl extends CoreService implements ReportingService {

  /**
   * 
   * @param sessionId
   * @param className
   * @param xmlParameters
   * @return
   * @throws DMServiceException
   */
  public String getReport(String sessionId, String className, String xmlParameters) throws DMServiceException {
    try {
      Session session = getHelper().getSession(sessionId);
      return reportingController.getReport(session, className, xmlParameters);
    } catch (Exception e) {
      throw getHelper().convertException(e);
    }
  }

  /**
   * 
   * @param sessionId
   * @return
   * @throws DMServiceException
   */
  public String getReportsList(String sessionId) throws DMServiceException {
    try {
      Session session = getHelper().getSession(sessionId);
      String reportsList = reportingController.getReportsList(session);
      return reportsList;
    } catch (Exception e) {
      throw getHelper().convertException(e);
    }
  }

  /**
   * 
   * @param sessionId
   * @param className
   * @return
   * @throws DMServiceException
   */
  public String getReportAttributes(String sessionId, String className) throws DMServiceException {
    try {
      Session session = getHelper().getSession(sessionId);
      String reportAttributes = reportingController.getReportAttributes(session, className);
      return reportAttributes;
    } catch (Exception e) {
      throw getHelper().convertException(e);
    }
  }

  /**
   * 
   * @param sessionId
   * @param transactionId
   * @throws DMServiceException
   */
  public void removeGhostTransaction(String sessionId, long transactionId) throws DMServiceException {
    try {
      Session session;
      session = getHelper().getSession(sessionId);
      reportingController.removeGhostTransaction(session, transactionId);
    } catch (Exception e) {
      throw getHelper().convertException(e);
    }
  }
}

