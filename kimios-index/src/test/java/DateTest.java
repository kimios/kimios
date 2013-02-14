/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 2/13/13
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateTest
{

    public static void main( String[] args )
        throws Exception
    {

        long timeStamp = 1360710000000L;
        Date d = new Date( timeStamp );
        System.out.println( " >> " + d );

        String dateToParse = "2013-02-13T00:00:00Z";

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss'Z'" );
        sdf.setTimeZone( TimeZone.getTimeZone("GMT") );
        Date dot = sdf.parse( dateToParse );

        Calendar cal = Calendar.getInstance();
        cal.setTime( dot );
        System.out.println("GMT: " + dot + " " + cal );
        sdf.setTimeZone( TimeZone.getTimeZone("UTC") );
        dot = sdf.parse( dateToParse );

        cal = Calendar.getInstance();
        cal.setTime( dot );
        System.out.println("UTC: " + dot + " " + cal );


    }

}
