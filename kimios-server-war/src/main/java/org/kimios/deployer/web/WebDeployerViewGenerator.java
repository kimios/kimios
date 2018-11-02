/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2018  DevLib'
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
package org.kimios.deployer.web;

import org.kimios.deployer.core.ConfElement;
import org.kimios.deployer.core.ConfTemplateParser;
import org.kimios.kernel.dms.hibernate.HDMEntityFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: farf Date: 11/20/12 Time: 3:51 PM To change this template use File | Settings |
 * File Templates.
 */
public class WebDeployerViewGenerator
{

    public static String KMS_SETTINGS_TPL_FILE_PATH = "/META-INF/settings/tpl/kimios.properties";

    public void generate(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        Properties p = new Properties();
        p.load(HDMEntityFactory.class
                .getResourceAsStream(KMS_SETTINGS_TPL_FILE_PATH));
        Map<String, ConfElement> items =
                new ConfTemplateParser().parse(HDMEntityFactory.class
                        .getResourceAsStream(KMS_SETTINGS_TPL_FILE_PATH), p);

        List<String> sortedItems = new ArrayList<String>();
        sortedItems.addAll(items.keySet());
        Collections.sort(sortedItems);

        /*
           Set default properties : (User home for repositories)
        */

        String repositoryDir = System.getProperty("user.home");
        String defaultRepoName = repositoryDir + File.separator + "kimios-repository";

        ConfElement solrEl = items.get("dms.index.solr.home");
        ConfElement repoEl = items.get("dms.repository.default.path");
        ConfElement lucenEl = items.get("dms.repository.index.path");
        ConfElement tmpPath = items.get("dms.repository.tmp.path");

        if (solrEl != null) {
            solrEl.setDefaultValue(defaultRepoName + File.separator + "solr");
        }

        if (repoEl != null) {
            repoEl.setDefaultValue(defaultRepoName);
        }

        if (lucenEl != null) {
            lucenEl.setDefaultValue(defaultRepoName + File.separator + "lucene");
        }
        if(tmpPath != null)
            tmpPath.setDefaultValue( "kimios-tmp");

        request.setAttribute("elementKeys", sortedItems);
        request.setAttribute("elementsMap", items);

        request.getRequestDispatcher("/WEB-INF/jsp/conf.jsp")
                .forward(request, response);
    }
}
