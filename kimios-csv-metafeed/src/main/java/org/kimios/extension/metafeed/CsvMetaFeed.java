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

package org.kimios.extension.metafeed;

import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.kernel.exception.MetaFeedSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by farf on 04/06/15.
 */
@Entity
@DiscriminatorValue(value = "org.kimios.extension.metafeed.CsvMetaFeed")
public class CsvMetaFeed extends MetaFeedImpl{


    private static String PREF_FILENAME = "csvFilename";

    private static String PREF_COLNAME = "csvValueCol";

    private static Logger logger = LoggerFactory.getLogger(CsvMetaFeed.class);

    public CsvMetaFeed(){}


    private File csvFile;


    private List<String> values = new ArrayList<String>();

    private void initData(){
        logger.info("starting preferences loading for {} "
            + this.preferences.get(PREF_FILENAME));

        try{

            values.clear();
            csvFile = new File(this.preferences.get(PREF_FILENAME));
            if(csvFile.exists() && csvFile.getName().toLowerCase().endsWith(".csv")){
                /*
                    load data
                 */
                int colReference = Integer.parseInt(PREF_COLNAME);
                FileInputStream fileInputStream = new FileInputStream(csvFile);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(fileInputStream)
                );
                String dataLine;
                while((dataLine = br.readLine()) != null){
                    String[] datas = dataLine.split(",");

                    if(colReference < datas.length)
                        values.add(datas[colReference]);
                }
            }

        }   catch (Exception ex){
            logger.error("error while loading csv file {}",
                    this.preferences.get(PREF_FILENAME), ex);
        }
    }

    @Override
    public List<String> getValues() {

        if(csvFile == null){
            initData();
        }
        return values;
    }

    @Override
    public String[] search(String criteria) throws MetaFeedSearchException {

        if(csvFile == null){
            initData();
        }
        try {
            ArrayList<String> v = new ArrayList<String>();
            for (String res : values) {
                if (res.toLowerCase().contains( criteria.toLowerCase() )
                        || criteria.trim().length() == 0) {
                    v.add(res);
                }
            }

            Collections.sort(v);
            return v.toArray(new String[]{});
        } catch (Exception e) {
            throw new MetaFeedSearchException("Retrieving metafeed value error : " + e.getMessage());
        }
    }
}
